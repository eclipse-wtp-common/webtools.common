/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.libprov.osgi.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.common.project.facet.core.libprov.osgi.BundleReference;
import org.eclipse.jst.common.project.facet.core.libprov.osgi.OsgiBundlesContainer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class OsgiBundlesContainerImpl

    implements IClasspathContainer
    
{
    private final IPath containerPath;
    private final IProjectFacet facet;
    private final IClasspathEntry[] entries;
    private final String description;
    
    private OsgiBundlesContainerImpl( final IJavaProject project,
                                      final IPath containerPath )
    {
        this.containerPath = containerPath;
        
        final String fid = containerPath.segment( 1 );
        this.facet = ProjectFacetsManager.getProjectFacet( fid );
        
        final IProject pj = project.getProject();
        
        String desc = OsgiBundlesContainer.getContainerLabel( pj, this.facet );
        
        if( desc == null )
        {
            desc = NLS.bind( Resources.containerLabel, this.facet.getLabel() );
        }
        
        this.description = desc;

        final List<BundleReference> bundleReferences 
            = OsgiBundlesContainer.getBundleReferences( pj, this.facet );
        
        final List<IClasspathEntry> entriesList = new ArrayList<IClasspathEntry>();
        
        for( BundleReference bundleReference : bundleReferences )
        {
            final Bundle bundle = bundleReference.getBundle();
            
            if( bundle != null )
            {
                File file = null;
                
                try
                {
                    file = FileLocator.getBundleFile( bundle );
                }
                catch( IOException e ) {}
                
                if( file != null )
                {
                    entriesList.add( JavaCore.newLibraryEntry( new Path( file.getPath() ), null, null ) );
                }
            }
        }
        
        this.entries = entriesList.toArray( new IClasspathEntry[ entriesList.size() ] );
    }
    
    public IClasspathEntry[] getClasspathEntries()
    {
        return this.entries;
    }

    public String getDescription()
    {
        return this.description;
    }

    public int getKind()
    {
    	return K_APPLICATION;
    }

    public IPath getPath()
    {
        return this.containerPath;
    }
    
    public boolean equals( final Object obj )
    {
        if( ! ( obj instanceof OsgiBundlesContainerImpl ) )
        {
            return false;
        }
        else
        {
            return this.entries.equals( ( (OsgiBundlesContainerImpl) obj ).entries );
        }
    }
    
    public static final class Initializer
    
        extends ClasspathContainerInitializer
        
    {
        @Override
        public void initialize( final IPath containerPath,
                                final IJavaProject project )
        
            throws CoreException
            
        {
            final OsgiBundlesContainerImpl container 
                = new OsgiBundlesContainerImpl( project, containerPath );
            
            JavaCore.setClasspathContainer( containerPath, new IJavaProject[] { project },
                                            new IClasspathContainer[] { container }, null );
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String containerLabel;
        
        static
        {
            initializeMessages( OsgiBundlesContainerImpl.class.getName(), 
                                Resources.class );
        }
    }
    
}
