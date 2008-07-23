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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jst.common.project.facet.core.internal.FacetCorePlugin;
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

@SuppressWarnings( "restriction" )

public final class ClasspathHelper
{
    /**
     * @since 3.1
     */
    
    public static final String LEGACY_METADATA_FILE_NAME 
        = ".settings/org.eclipse.jst.common.project.facet.core.prefs"; //$NON-NLS-1$
    
    private static final Object SYSTEM_OWNER = new Object();
    private static final String OWNER_PROJECT_FACETS_ATTR = "owner.project.facets"; //$NON-NLS-1$
    
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
        final IFacetedProject fproj = ProjectFacetsManager.create( project );
        final IRuntime runtime = fproj.getPrimaryRuntime();
        
        if( runtime != null )
        {
            final IClasspathProvider cpprov 
                = (IClasspathProvider) runtime.getAdapter( IClasspathProvider.class );
            
            final List<IClasspathEntry> cpentries = cpprov.getClasspathEntries( fv );
            
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
                                            final List<IClasspathEntry> cpentries )
    
        throws CoreException
        
    {
        final IJavaProject jproj = JavaCore.create( project );
        
        convertLegacyMetadata( jproj );
        
        final List<IClasspathEntry> cp = getProjectClasspath( jproj );
        
        for( IClasspathEntry cpe : cpentries )
        {
            IClasspathEntry existingClasspathEntry = null;
            
            for( IClasspathEntry x : cp )
            {
                if( x.getPath().equals( cpe.getPath() ) )
                {
                    existingClasspathEntry = x;
                    break;
                }
            }

            final Set<Object> owners = getOwners( existingClasspathEntry );
            
            owners.add( fv );
            
            if( existingClasspathEntry != null )
            {
                final IClasspathEntry annotatedEntry = setOwners( existingClasspathEntry, owners );
                final int existingIndex = cp.indexOf( existingClasspathEntry );
                cp.set( existingIndex, annotatedEntry );
            }
            else
            {
                final IClasspathEntry annotatedEntry = setOwners( cpe, owners );
                cp.add( annotatedEntry );
            }
        }

        setProjectClasspath( jproj, cp );
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
        final IJavaProject jproj = JavaCore.create( project );
        
        convertLegacyMetadata( jproj );
        
        final List<IClasspathEntry> cp = getProjectClasspath( jproj );
        boolean cpchanged = false;
        
        for( ListIterator<IClasspathEntry> itr = cp.listIterator(); itr.hasNext(); )
        {
            final IClasspathEntry cpe = itr.next();
            final Set<Object> owners = getOwners( cpe );
            
            if( owners.remove( fv ) )
            {
                if( owners.size() == 0 )
                {
                    itr.remove();
                }
                else
                {
                    itr.set( setOwners( cpe, owners ) );
                }
                
                cpchanged = true;
            }
        }

        if( cpchanged )
        {
            setProjectClasspath( jproj, cp );
        }
    }
    
    private static List<IClasspathEntry> getProjectClasspath( final IJavaProject jproj )
    
        throws CoreException
        
    {
        final List<IClasspathEntry> result = new ArrayList<IClasspathEntry>();
        
        for( IClasspathEntry cpe : jproj.getRawClasspath() )
        {
            result.add( cpe );
        }
        
        return result;
    }
    
    private static void setProjectClasspath( final IJavaProject jproj,
                                             final List<IClasspathEntry> cp )
    
        throws CoreException
        
    {
        jproj.setRawClasspath( cp.toArray( new IClasspathEntry[ cp.size() ] ), null );
    }
    
    private static String encodeOwnersString( final Set<Object> owners )
    {
        final StringBuilder buf = new StringBuilder();
        
        for( Object owner : owners )
        {
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
        
        return buf.toString();
    }
    
    private static Set<Object> decodeOwnersString( final String str )
    {
        final Set<Object> owners = new HashSet<Object>();
        final String[] split = str.split( ";" ); //$NON-NLS-1$
        
        for( int j = 0; j < split.length; j++ )
        {
            final String segment = split[ j ];
            
            if( segment.equals( "#system#" ) ) //$NON-NLS-1$
            {
                owners.add( SYSTEM_OWNER );
            }
            else
            {
                final IProjectFacetVersion fv = decodeFacetVersion( segment );
                owners.add( fv );
            }
        }
        
        return owners;
    }
    
    private static IProjectFacetVersion decodeFacetVersion( final String str )
    {
        final int colon = str.indexOf( ':' );
        final String id = str.substring( 0, colon );
        final String ver = str.substring( colon + 1 );
        
        return ProjectFacetsManager.getProjectFacet( id ).getVersion( ver );
    }

    private static Set<Object> getOwners( final IClasspathEntry cpe )
    {
        final Set<Object> owners = new HashSet<Object>();
        
        if( cpe != null )
        {
            for( IClasspathAttribute attr : cpe.getExtraAttributes() )
            {
                if( attr.getName().equals( OWNER_PROJECT_FACETS_ATTR ) )
                {
                    owners.addAll( decodeOwnersString( attr.getValue() ) );
                    break;
                }
            }
            
            if( owners.isEmpty() )
            {
                owners.add( SYSTEM_OWNER );
            }
        }
        
        return owners;
    }
    
    private static IClasspathEntry setOwners( final IClasspathEntry cpe,
                                              final Set<Object> owners )
    {
        if( owners.size() == 1 && owners.iterator().next() == SYSTEM_OWNER )
        {
            owners.clear();
        }
        
        final String ownersString = ( owners.size() == 0 ? null : encodeOwnersString( owners ) );
        return setOwners( cpe, ownersString );
    }

    private static IClasspathEntry setOwners( final IClasspathEntry cpe,
                                              final String owners )
    {
        final List<IClasspathAttribute> attrs = new ArrayList<IClasspathAttribute>();
        
        for( IClasspathAttribute attr : cpe.getExtraAttributes() )
        {
            if( ! attr.getName().equals( OWNER_PROJECT_FACETS_ATTR ) )
            {
                attrs.add( attr );
            }
        }
        
        if( owners != null )
        {
            attrs.add( JavaCore.newClasspathAttribute( OWNER_PROJECT_FACETS_ATTR, owners ) );
        }
        
        return new ClasspathEntry( cpe.getContentKind(), cpe.getEntryKind(), cpe.getPath(),
                                   cpe.getInclusionPatterns(), cpe.getExclusionPatterns(),
                                   cpe.getSourceAttachmentPath(), cpe.getSourceAttachmentRootPath(),
                                   cpe.getOutputLocation(), cpe.isExported(), cpe.getAccessRules(),
                                   cpe.combineAccessRules(), 
                                   attrs.toArray( new IClasspathAttribute[ attrs.size() ] ) );
    }
    
    private static void convertLegacyMetadata( final IJavaProject jproj )
    
        throws CoreException
        
    {
        final IProject project = jproj.getProject();
        final IFile legacyMetadataFile = project.getFile( LEGACY_METADATA_FILE_NAME );
        
        if( legacyMetadataFile.exists() )
        {
            final ProjectScope scope = new ProjectScope( project );
            final IEclipsePreferences pluginRoot = scope.getNode( FacetCorePlugin.PLUGIN_ID );
            final Preferences root = pluginRoot.node( "classpath.helper" ); //$NON-NLS-1$
            final Map<IPath,String> metadata = new HashMap<IPath,String>();
            
            final String[] keys;
            
            try
            {
                keys = root.childrenNames();
            }
            catch( BackingStoreException e )
            {
                throw new CoreException( FacetCorePlugin.createErrorStatus( e.getMessage(), e ) );
            }
            
            for( String key : keys )
            {
                final Preferences node = root.node( key );
                final String owners = node.get( "owners", null ); //$NON-NLS-1$
                
                if( owners != null )
                {
                    metadata.put( new Path( key.replaceAll( "::", "/" ) ), owners ); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            
            if( ! metadata.isEmpty() )
            {
                final List<IClasspathEntry> cp = getProjectClasspath( jproj );
                boolean cpchanged = false;
                
                for( ListIterator<IClasspathEntry> itr = cp.listIterator(); itr.hasNext(); )
                {
                    final IClasspathEntry cpe = itr.next();
                    final String owners = metadata.get( cpe.getPath() );
                    
                    if( owners != null )
                    {
                        itr.set( setOwners( cpe, owners ) );
                        cpchanged = true;
                    }
                }
                
                if( cpchanged )
                {
                    setProjectClasspath( jproj, cp );
                }
            }
            
            legacyMetadataFile.delete( true, null );
        }
    }

}
