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

package org.eclipse.jst.common.project.facet.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.common.project.facet.core.internal.FacetCorePlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * <p>A utility used in conjunction with IClasspathProvider in order to manage
 * the bookkeeping when project facets are installed and uninstalled, and when
 * the bound runtime changes. This utility tracks which classpath entries were
 * added to the project by which facet and stores this information in a project
 * metadata file. This enables the classpath entries to be removed without
 * knowing what they are. It is only necessary to know which facet added them.
 * This utility supports the case where the same classpath entry is added by
 * multiple project facets. In this situation, a classpath entry is only
 * removed when the removal has been requested for all of the facets that added
 * it.</p>
 * 
 * <p>Typically the project facet author will write something like this in the
 * install delegate:</p>
 * 
 * <pre>
 * if( ! ClasspathHelper.addClasspathEntries( project, fv )
 * {
 *     // Handle the case when there is no bound runtime or when the bound
 *     // runtime cannot provide classpath entries for this facet.
 *     
 *     final List alternate = ...;
 *     ClasspathHelper.addClasspathEntries( project, fv, alternate );
 * }
 * </pre>
 * 
 * <p>And something like this in the uninstall delegate:</p>
 * 
 * <pre>
 * ClasspathHelper.removeClasspathEntries( project, fv );
 * </pre>
 * 
 * <p>And something like this in the runtime changed delegate:</p>
 * 
 * <pre>
 * ClasspathHelper.removeClasspathEntries( project, fv );
 * 
 * if( ! ClasspathHelper.addClasspathEntries( project, fv )
 * {
 *     // Handle the case when there is no bound runtime or when the bound
 *     // runtime cannot provide classpath entries for this facet.
 *     
 *     final List alternate = ...;
 *     ClasspathHelper.addClasspathEntries( project, fv, alternate );
 * }
 * </pre>
 * 
 * <p>And something like this in the version change delegate:</p>
 * 
 * <pre>
 * final IProjectFacetVersion oldver
 *   = fproj.getInstalledVersion( fv.getProjectFacet() );
 * 
 * ClasspathHelper.removeClasspathEntries( project, oldver );
 * 
 * if( ! ClasspathHelper.addClasspathEntries( project, fv )
 * {
 *     // Handle the case when there is no bound runtime or when the bound
 *     // runtime cannot provide classpath entries for this facet.
 *     
 *     final List alternate = ...;
 *     ClasspathHelper.addClasspathEntries( project, fv, alternate );
 * }
 * </pre>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ClasspathHelper
{
    private static final Object SYSTEM_OWNER = new Object();
    
    private ClasspathHelper() {}
    
    /**
     * Convenience method for adding to the project the classpath entries
     * provided for the specified project facet by the runtime bound to the
     * project. The entries are marked as belonging to the specified project
     * facet.
     *   
     * @param project the project
     * @param fv the project facet version that will own these entries
     * @return <code>true</code> if classpath entries were added, or
     *   <code>false</code> if there is no runtime bound to the project or if
     *   it cannot provide classpath entries for the specified facet
     * @throws CoreException if failed while adding the classpath entries
     */
    
    public static boolean addClasspathEntries( final IProject project,
                                               final IProjectFacetVersion fv )
    
        throws CoreException
        
    {
        final IFacetedProject fproj 
            = ProjectFacetsManager.create( project );
    
        final IRuntime runtime = fproj.getPrimaryRuntime();
        
        if( runtime != null )
        {
            final IClasspathProvider cpprov 
                = (IClasspathProvider) runtime.getAdapter( IClasspathProvider.class );
            
            final List cpentries = cpprov.getClasspathEntries( fv );
            
            if( cpentries != null )
            {
                addClasspathEntries( project, fv, cpentries );
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Add the provided classpath entries to project and marks them as belonging
     * to the specified project facet.
     * 
     * @param project the project
     * @param fv the project facet version that will own these entries
     * @param cpentries the classpath entries (element type: 
     *   {@see IClasspathEntry})
     * @throws CoreException if failed while adding the classpath entries
     */
    
    public static void addClasspathEntries( final IProject project,
                                            final IProjectFacetVersion fv,
                                            final List cpentries )
    
        throws CoreException
        
    {
        try
        {
            final IJavaProject jproj = JavaCore.create( project );
            final List cp = getClasspath( jproj );
            boolean cpchanged = false;

            final Map prefs = readPreferences( project );
            
            for( Iterator itr = cpentries.iterator(); itr.hasNext(); )
            {
                final IClasspathEntry cpentry = (IClasspathEntry) itr.next();
                final IPath path = cpentry.getPath();
                
                final boolean contains = cp.contains( cpentry );
                
                Set owners = (Set) prefs.get( path );
                
                if( owners == null )
                {
                    owners = new HashSet();

                    if( contains )
                    {
                        owners.add( SYSTEM_OWNER );
                    }
                    
                    prefs.put( path, owners );
                }
                
                owners.add( fv );
                
                if( ! contains )
                {
                    cp.add( cpentry );
                    cpchanged = true;
                }
            }
            
            if( cpchanged )
            {
                setClasspath( jproj, cp );
            }
            
            writePreferences( project, prefs );
        }
        catch( BackingStoreException e )
        {
            final IStatus st
                = new Status( IStatus.ERROR, FacetCorePlugin.PLUGIN_ID, 0, 
                              Resources.failedWritingPreferences, e );
            
            throw new CoreException( st );
        }
    }
    
    /**
     * Removes the classpath entries belonging to the specified project facet.
     * Any entries that also belong to another facet are left in place.
     * 
     * @param project the project
     * @param fv the project facet that owns the entries that should be removed
     * @throws CoreException if failed while removing classpath entries
     */
    
    public static void removeClasspathEntries( final IProject project,
                                               final IProjectFacetVersion fv )
    
        throws CoreException
        
    {
        try
        {
            final IJavaProject jproj = JavaCore.create( project );
            final List cp = getClasspath( jproj );
            boolean cpchanged = false;

            final Map prefs = readPreferences( project );
            
            for( Iterator itr1 = prefs.entrySet().iterator(); itr1.hasNext(); )
            {
                final Map.Entry entry = (Map.Entry) itr1.next();
                final IPath path = (IPath) entry.getKey();
                final Set owners = (Set) entry.getValue();
                
                if( owners.contains( fv ) )
                {
                    owners.remove( fv );
                    
                    if( owners.size() == 0 )
                    {
                        itr1.remove();
                        
                        for( Iterator itr2 = cp.iterator(); itr2.hasNext(); )
                        {
                            final IClasspathEntry cpentry
                                = (IClasspathEntry) itr2.next();
                            
                            if( cpentry.getPath().equals( path ) )
                            {
                                itr2.remove();
                                cpchanged = true;
                                break;
                            }
                        }
                    }
                }
            }

            if( cpchanged )
            {
                setClasspath( jproj, cp );
            }
            
            writePreferences( project, prefs );
        }
        catch( BackingStoreException e )
        {
            final IStatus st
                = new Status( IStatus.ERROR, FacetCorePlugin.PLUGIN_ID, 0, 
                              Resources.failedWritingPreferences, e );
            
            throw new CoreException( st );
        }
    }
    
    private static List getClasspath( final IJavaProject jproj )
    
        throws CoreException
        
    {
        final ArrayList list = new ArrayList();
        final IClasspathEntry[] cp = jproj.getRawClasspath();
        
        for( int i = 0; i < cp.length; i++ )
        {
            list.add( cp[ i ] );
        }
        
        return list;
    }
    
    private static void setClasspath( final IJavaProject jproj,
                                      final List cp )
    
        throws CoreException
        
    {
        final IClasspathEntry[] newcp
            = (IClasspathEntry[]) cp.toArray( new IClasspathEntry[ cp.size() ] );
        
        jproj.setRawClasspath( newcp, null );
    }
    
    private static Map readPreferences( final IProject project )
    
        throws BackingStoreException
        
    {
        final Preferences root = getPreferencesNode( project );
        final Map result = new HashMap();
        
        final String[] keys = root.childrenNames();
        
        for( int i = 0; i < keys.length; i++ )
        {
            final String key = keys[ i ];
            final Preferences node = root.node( key );
            
            final Set set = new HashSet();
            final String owners = node.get( "owners", null ); //$NON-NLS-1$
            
            if( owners != null )
            {
                final String[] split = owners.split( ";" ); //$NON-NLS-1$
                
                for( int j = 0; j < split.length; j++ )
                {
                    final String segment = split[ j ];
                    
                    if( segment.equals( "#system#" ) ) //$NON-NLS-1$
                    {
                        set.add( SYSTEM_OWNER );
                    }
                    else
                    {
                        final IProjectFacetVersion fv 
                            = parseFeatureVersion( segment );
                        
                        set.add( fv );
                    }
                }
            }
            
            result.put( decode( key ), set );
        }
        
        return result;
    }
    
    private static void writePreferences( final IProject project,
                                          final Map prefs )
    
        throws BackingStoreException
        
    {
        final Preferences root = getPreferencesNode( project );
        final String[] children = root.childrenNames();
        
        for( int i = 0; i < children.length; i++ )
        {
            root.node( children[ i ] ).removeNode();
        }
        
        for( Iterator itr1 = prefs.entrySet().iterator(); itr1.hasNext(); )
        {
            final Map.Entry entry = (Map.Entry) itr1.next();
            final IPath path = (IPath) entry.getKey();
            final Set owners = (Set) entry.getValue();
            
            final StringBuffer buf = new StringBuffer();
            
            for( Iterator itr2 = owners.iterator(); itr2.hasNext(); )
            {
                final Object owner = itr2.next();

                if( buf.length() > 0 ) 
                {
                    buf.append( ';' );
                }
                
                if( owner == SYSTEM_OWNER )
                {
                    buf.append( "#system#" ); //$NON-NLS-1$
                }
                else
                {
                    final IProjectFacetVersion fv 
                        = (IProjectFacetVersion) owner;
                    
                    buf.append( fv.getProjectFacet().getId() );
                    buf.append( ':' );
                    buf.append( fv.getVersionString() );
                }
            }

            final Preferences node = root.node( encode( path ) );
            node.put( "owners", buf.toString() ); //$NON-NLS-1$
        }
        
        root.flush();
    }
    
    
    private static Preferences getPreferencesNode( final IProject project )
    {
        final ProjectScope scope = new ProjectScope( project );
        final IEclipsePreferences pluginRoot = scope.getNode( FacetCorePlugin.PLUGIN_ID );
        return pluginRoot.node( "classpath.helper" ); //$NON-NLS-1$
    }
    
    private static IProjectFacetVersion parseFeatureVersion( final String str )
    {
        final int colon = str.indexOf( ':' );
        final String id = str.substring( 0, colon );
        final String ver = str.substring( colon + 1 );
        
        return ProjectFacetsManager.getProjectFacet( id ).getVersion( ver );
    }
    
    private static String encode( final IPath path )
    {
        return path.toString().replaceAll( "/", "::" ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private static IPath decode( final String path )
    {
        return new Path( path.replaceAll( "::", "/" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String failedWritingPreferences;
        
        static
        {
            initializeMessages( ClasspathHelper.class.getName(), 
                                Resources.class );
        }
    }

}
