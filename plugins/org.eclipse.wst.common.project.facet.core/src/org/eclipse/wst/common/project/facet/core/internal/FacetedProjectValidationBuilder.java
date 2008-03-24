/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import static org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin.PLUGIN_ID;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findExtensions;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getTopLevelElements;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.instantiate;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.reportMissingAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectValidator;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectValidationBuilder

    extends IncrementalProjectBuilder
    
{
    public static final String BUILDER_ID
        = FacetCorePlugin.PLUGIN_ID + ".builder"; //$NON-NLS-1$

    private static final String EXTENSION_POINT_ID = "validators"; //$NON-NLS-1$
    
    private static final String EL_VALIDATOR = "validator"; //$NON-NLS-1$
    private static final String EL_FACET = "facet"; //$NON-NLS-1$
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

    private static final List<ValidatorDefinition> validators 
        = new ArrayList<ValidatorDefinition>();
    
    static
    {
        readExtensions();
    }
    
    protected IProject[] build( final int kind, 
                                final Map args, 
                                final IProgressMonitor monitor ) 
    
        throws CoreException
        
    {
        if( monitor != null )
        {
            monitor.beginTask( Resources.taskDescription, 3 );
        }
        
        try
        {
            final IProject proj = getProject();
            final IFacetedProject fproj = ProjectFacetsManager.create( proj );
            
            // Delete existing problem markers.
            
            proj.deleteMarkers( IFacetedProjectValidator.BASE_MARKER_ID, true, 
                                IResource.DEPTH_INFINITE );
            
            worked( monitor, 1 );
            
            // Perform basic validation.
            
            final IStatus st = fproj.validate( submon( monitor, 1 ) );
            
            if( ! st.isOK() )
            {
                final IStatus[] problems = st.getChildren();
                
                for( int i = 0; i < problems.length; i++ )
                {
                    final IStatus problem = problems[ i ];
                    final int severity = problem.getSeverity();
                    final String message = problem.getMessage();
                    
                    if( severity == IStatus.ERROR )
                    {
                        fproj.createErrorMarker( message );
                    }
                    else if( severity == IStatus.WARNING )
                    {
                        fproj.createWarningMarker( message );
                    }
                }
            }
            
            // Call the registered validators.
            
            for( ValidatorDefinition def : validators )
            {
                if( monitor != null && monitor.isCanceled() )
                {
                    throw new OperationCanceledException();
                }
                
                if( def.isApplicable( fproj.getProjectFacets() ) )
                {
                    final IFacetedProjectValidator validator = def.getValidator();
                    
                    if( validator != null )
                    {
                        validator.validate( fproj );
                    }
                }
            }
            
            worked( monitor, 1 );
            
            return new IProject[0];
        }
        finally
        {
            if( monitor != null )
            {
                monitor.done();
            }
        }
    }
    
    private static void readExtensions()
    {
        for( IConfigurationElement config 
            : getTopLevelElements( findExtensions( PLUGIN_ID, EXTENSION_POINT_ID ) ) )
        {
            if( config.getName().equals( EL_VALIDATOR ) )
            {
                ValidatorDefinition def = new ValidatorDefinition();
                def.plugin = config.getContributor().getName();
                
                def.className = config.getAttribute( ATTR_CLASS );

                if( def.className == null )
                {
                    reportMissingAttribute( config, ATTR_CLASS );
                    continue;
                }
                
                final IConfigurationElement[] children = config.getChildren();
                
                for( int j = 0; j < children.length; j++ )
                {
                    final IConfigurationElement child = children[ j ];
                    final String childName = child.getName();
                    
                    if( childName.equals( EL_FACET ) )
                    {
                        final ProjectFacetRef ref 
                            = ProjectFacetRef.read( child );
                        
                        if( ref != null )
                        {
                            def.constraints.add( ref );
                        }
                        else
                        {
                            def = null;
                            break;
                        }
                    }
                }
                
                if( def != null )
                {
                    validators.add( def );
                }
            }
        }
    }
    
    private static SubProgressMonitor submon( final IProgressMonitor parent,
                                              final int ticks )
    {
        if( parent == null )
        {
            return null;
        }
        else
        {
            return new SubProgressMonitor( parent, ticks );
        }
    }
    
    private static void worked( final IProgressMonitor monitor,
                                final int ticks )
    {
        if( monitor != null )
        {
            monitor.worked( ticks );
            
            if( monitor.isCanceled() )
            {
                throw new OperationCanceledException();
            }
        }
    }
    
    private static final class ValidatorDefinition
    {
        public String plugin;
        public String className;
        public IFacetedProjectValidator validator;
        public final List<ProjectFacetRef> constraints = new ArrayList<ProjectFacetRef>();
        
        public boolean isApplicable( final Set<IProjectFacetVersion> facets )
        
            throws CoreException
            
        {
            for( ProjectFacetRef ref : this.constraints )
            {
                if( ! ref.check( facets ) )
                {
                    return false;
                }
            }
            
            return true;
        }
        
        public synchronized IFacetedProjectValidator getValidator()
        
            throws CoreException
            
        {
            if( this.validator == null )
            {
                this.validator
                    = instantiate( this.plugin, this.className, IFacetedProjectValidator.class );
            }
            
            return this.validator;
        }
    }
            
    private static final class Resources
    
        extends NLS
        
    {
        public static String taskDescription;
        
        static
        {
            initializeMessages( FacetedProjectValidationBuilder.class.getName(), 
                                Resources.class );
        }
    }
}
