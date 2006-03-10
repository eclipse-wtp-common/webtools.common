/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectValidator;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectValidationBuilder

    extends IncrementalProjectBuilder
    
{
    public static final String BUILDER_ID
        = FacetCorePlugin.PLUGIN_ID + ".builder"; //$NON-NLS-1$

    public static final String VALIDATORS_EXTENSION_ID = "validators"; //$NON-NLS-1$
    
    private static final String EL_VALIDATOR = "validator"; //$NON-NLS-1$
    private static final String EL_FACET = "facet"; //$NON-NLS-1$
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

    private static final List validators = new ArrayList();
    
    static
    {
        readMetadata();
    }
    
    protected IProject[] build( final int kind, 
                                final Map args, 
                                final IProgressMonitor monitor ) 
    
        throws CoreException
        
    {
        final IProject proj = getProject();
        final IFacetedProject fproj = ProjectFacetsManager.create( proj );
        
        proj.deleteMarkers( IFacetedProjectValidator.BASE_MARKER_ID, true, 
                            IResource.DEPTH_INFINITE );
        
        for( Iterator itr = validators.iterator(); itr.hasNext(); )
        {
            final ValidatorDefinition def = (ValidatorDefinition) itr.next();
            
            if( def.isApplicable( fproj.getProjectFacets() ) )
            {
                final IFacetedProjectValidator validator = def.getValidator();
                
                if( validator != null )
                {
                    validator.validate( fproj );
                }
            }
        }
        
        return new IProject[0];
    }
    
    private static void readMetadata()
    {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        
        final IExtensionPoint point 
            = registry.getExtensionPoint( FacetCorePlugin.PLUGIN_ID, 
                                          VALIDATORS_EXTENSION_ID );
        
        if( point == null )
        {
            throw new RuntimeException( "Extension point not found!" ); //$NON-NLS-1$
        }
        
        final ArrayList cfgels = new ArrayList();
        final IExtension[] extensions = point.getExtensions();
        
        for( int i = 0; i < extensions.length; i++ )
        {
            final IConfigurationElement[] elements 
                = extensions[ i ].getConfigurationElements();
            
            for( int j = 0; j < elements.length; j++ )
            {
                cfgels.add( elements[ j ] );
            }
        }
        
        for( int i = 0, n = cfgels.size(); i < n; i++ )
        {
            final IConfigurationElement config
                = (IConfigurationElement) cfgels.get( i );
            
            if( config.getName().equals( EL_VALIDATOR ) )
            {
                ValidatorDefinition def = new ValidatorDefinition();
                def.plugin = config.getDeclaringExtension().getNamespace();
                
                def.className = config.getAttribute( ATTR_CLASS );

                if( def.className == null )
                {
                    ProjectFacetsManagerImpl.reportMissingAttribute( config, ATTR_CLASS );
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
    
    private static final class ValidatorDefinition
    {
        public String plugin;
        public String className;
        public IFacetedProjectValidator validator;
        public final List constraints = new ArrayList();
        
        public boolean isApplicable( final Set facets )
        
            throws CoreException
            
        {
            for( Iterator itr = this.constraints.iterator(); itr.hasNext(); )
            {
                final ProjectFacetRef ref = (ProjectFacetRef) itr.next();
                
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
                final Object instance
                    = FacetCorePlugin.instantiate( this.plugin, 
                                                   this.className, 
                                                   IFacetedProjectValidator.class );
            
                this.validator = (IFacetedProjectValidator) instance;
            }
            
            return this.validator;
        }
    }
            
}
