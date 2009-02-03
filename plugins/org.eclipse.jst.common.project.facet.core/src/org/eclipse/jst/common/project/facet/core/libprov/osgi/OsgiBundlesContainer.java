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

package org.eclipse.jst.common.project.facet.core.libprov.osgi;

import static org.eclipse.jst.common.project.facet.core.JavaFacet.isJavaProject;
import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @since 1.4
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class OsgiBundlesContainer
{
    public static final String CONTAINER_ID = "eclipse.fproj.jdt.libprov.osgi"; //$NON-NLS-1$
    public static final IPath CONTAINER_PATH = new Path( CONTAINER_ID );
    
    private static final String PREFS_OSGI_BUNDLES_CONTAINER = "osgi-bundles-container"; //$NON-NLS-1$
    private static final String PREFS_BUNDLES = "bundles"; //$NON-NLS-1$
    
    public static boolean isOnClasspath( final IProject project,
                                         final IProjectFacet facet )
    
        throws CoreException
        
    {
        if( isJavaProject( project ) )
        {
            return isOnClasspath( JavaCore.create( project ), facet );
        }
        
        return false;
    }
    
    public static boolean isOnClasspath( final IJavaProject project,
                                         final IProjectFacet facet )
    
        throws CoreException
        
    {
        final IClasspathEntry[] cp = project.getRawClasspath();
        
        for( IClasspathEntry cpe : cp )
        {
            if( isOsgiBundlesContainer( cpe ) )
            {
                final String fid = cpe.getPath().segment( 1 );
                return facet.getId().equals( fid );
            }
        }
        
        return false;
    }
    
    public static void addToClasspath( final IProject project,
                                       final IProjectFacet facet )
    
        throws CoreException
        
    {
        addToClasspath( project, facet, null );
    }

    public static void addToClasspath( final IProject project,
                                       final IProjectFacet facet,
                                       final IClasspathAttribute[] attributes )
    
        throws CoreException
        
    {
        if( isJavaProject( project ) )
        {
            addToClasspath( JavaCore.create( project ), facet, attributes );
        }
    }

    public static void addToClasspath( final IJavaProject project,
                                       final IProjectFacet facet )
    
        throws CoreException
        
    {
        addToClasspath( project, facet, null );
    }
    
    public static void addToClasspath( final IJavaProject project,
                                       final IProjectFacet facet,
                                       final IClasspathAttribute[] attributes )
    
        throws CoreException
        
    {
        if( ! isOnClasspath( project, facet ) )
        {
            final IClasspathEntry[] oldcp = project.getRawClasspath();
            final IClasspathEntry[] newcp = new IClasspathEntry[ oldcp.length + 1 ];
            System.arraycopy( oldcp, 0, newcp, 0, oldcp.length );
            final IPath path = CONTAINER_PATH.append( facet.getId() );
            final IClasspathAttribute[] attrs = ( attributes != null ? attributes : new IClasspathAttribute[ 0 ] );
            newcp[ newcp.length - 1 ] = JavaCore.newContainerEntry( path, null, attrs, false );
            
            project.setRawClasspath( newcp, null );
        }
    }
    
    public static void removeFromClasspath( final IProject project,
                                            final IProjectFacet facet )
    
        throws CoreException
        
    {
        if( isJavaProject( project ) )
        {
            removeFromClasspath( JavaCore.create( project ), facet );
        }
    }
    
    public static void removeFromClasspath( final IJavaProject project,
                                            final IProjectFacet facet )
    
        throws CoreException
        
    {
        final IClasspathEntry[] oldcp = project.getRawClasspath();
        
        for( int i = 0; i < oldcp.length; i++ )
        {
            final IClasspathEntry cpe = oldcp[ i ];
            
            if( isOsgiBundlesContainer( cpe ) )
            {
                final String fid = cpe.getPath().segment( 1 );
                
                if( facet.getId().equals( fid ) )
                {
                    final IClasspathEntry[] newcp = new IClasspathEntry[ oldcp.length - 1 ];
                    System.arraycopy( oldcp, 0, newcp, 0, i );
                    System.arraycopy( oldcp, i + 1, newcp, i, oldcp.length - i - 1 );
                    
                    project.setRawClasspath( newcp, null );
                    
                    return;
                }
            }
        }
    }
    
    public static boolean isOsgiBundlesContainer( final IClasspathEntry cpe )
    {
        final IPath path = cpe.getPath();
        return path.segmentCount() >= 2 && path.segment( 0 ).equals( CONTAINER_ID );
    }
    
    public static List<BundleReference> getBundleReferences( final IProject project,
                                                             final IProjectFacet facet )
    {
        IFacetedProject fproj = null;
        
        try
        {
            fproj = ProjectFacetsManager.create( project );
        }
        catch( CoreException e )
        {
            log( e );
        }
        
        if( fproj == null )
        {
            return Collections.emptyList();
        }
        else
        {
            return getBundleReferences( fproj, facet );
        }
    }
    
    public static List<BundleReference> getBundleReferences( final IFacetedProject project,
                                                             final IProjectFacet facet )
    {
        final List<BundleReference> bundleReferences = new ArrayList<BundleReference>();
        
        try
        {
            Preferences prefs = project.getPreferences( facet );
            
            if( prefs.nodeExists( PREFS_OSGI_BUNDLES_CONTAINER ) )
            {
                prefs = prefs.node( PREFS_OSGI_BUNDLES_CONTAINER );
                
                final String unparsedMetadata = prefs.get( PREFS_BUNDLES, null );
                
                if( unparsedMetadata != null )
                {
                    for( String unparsedBundleReference : unparsedMetadata.split( ";" ) ) //$NON-NLS-1$
                    {
                        bundleReferences.add( parseBundleReference( unparsedBundleReference ) );
                    }
                }
            }
        }
        catch( BackingStoreException e )
        {
            log( e );
        }
        
        return bundleReferences;
    }
    
    public static void setBundleReferences( final IProject project,
                                            final IProjectFacet facet,
                                            final List<BundleReference> bundleReferences )
    {
        IFacetedProject fproj = null;
        
        try
        {
            fproj = ProjectFacetsManager.create( project );
        }
        catch( CoreException e )
        {
            log( e );
        }
        
        if( fproj != null )
        {
            setBundleReferences( fproj, facet, bundleReferences );
        }
    }
    
    public static void setBundleReferences( final IFacetedProject project,
                                            final IProjectFacet facet,
                                            final List<BundleReference> bundleReferences )
    {
        try
        {
            Preferences prefs = project.getPreferences( facet );
            
            if( bundleReferences == null || bundleReferences.isEmpty() )
            {
                prefs.removeNode();
            }
            else
            {
                final StringBuilder buf = new StringBuilder();
                
                for( BundleReference bundleReference : bundleReferences )
                {
                    if( buf.length() > 0 )
                    {
                        buf.append( ';' );
                    }
                    
                    buf.append( convert( bundleReference ) );
                }
                
                prefs = prefs.node( PREFS_OSGI_BUNDLES_CONTAINER );
                prefs.put( PREFS_BUNDLES, buf.toString() );
            }
            
            prefs.flush();
        }
        catch( BackingStoreException e )
        {
            log( e );
        }
    }
    
    public static BundleReference parseBundleReference( final String bundleReferenceString )
    {
        String bundleId;
        VersionRange versionRange;
        
        final int colon = bundleReferenceString.indexOf( ':' );
        
        if( colon == -1 )
        {
            bundleId = bundleReferenceString;
            versionRange = null;
        }
        else
        {
            bundleId = bundleReferenceString.substring( 0, colon );
            
            try
            {
                versionRange = new VersionRange( bundleReferenceString.substring( colon + 1 ) );
            }
            catch( IllegalArgumentException e )
            {
                log( e );
                versionRange = null;
            }
        }
        
        return new BundleReference( bundleId, versionRange );        
    }
    
    private static String convert( final BundleReference bundleReference )
    {
        final StringBuilder buf = new StringBuilder();
        
        buf.append( bundleReference.getBundleId() );
        
        if( bundleReference.getVersionRange() != null )
        {
            buf.append( ':' );
            buf.append( bundleReference.getVersionRange().toString() );
        }
        
        return buf.toString();
    }
    
    /**
     * This class should not be instantiated.
     */

    private OsgiBundlesContainer() {}
    
}
