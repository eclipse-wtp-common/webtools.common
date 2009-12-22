/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.internal;

import java.util.ArrayList;
import java.util.Collections;
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
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@SuppressWarnings( "restriction" )
public final class ClasspathUtil
{
    public static final String LEGACY_METADATA_FILE_NAME 
        = ".settings/org.eclipse.jst.common.project.facet.core.prefs"; //$NON-NLS-1$

    public static final Object SYSTEM_OWNER = new Object();
    private static final String OWNER_PROJECT_FACETS_ATTR = "owner.project.facets"; //$NON-NLS-1$
    
    private ClasspathUtil() {}

    public static List<IClasspathEntry> getProjectClasspath( final IJavaProject jproj )
    
        throws CoreException
        
    {
        final List<IClasspathEntry> result = new ArrayList<IClasspathEntry>();
        
        for( IClasspathEntry cpe : jproj.getRawClasspath() )
        {
            result.add( cpe );
        }
        
        return result;
    }
    
    public static void setProjectClasspath( final IJavaProject jproj,
                                            final List<IClasspathEntry> cp )
    
        throws CoreException
        
    {
        jproj.setRawClasspath( cp.toArray( new IClasspathEntry[ cp.size() ] ), null );
    }
    
    public static void addClasspathEntry( final IProject project,
                                          final IClasspathEntry cpe )
    
        throws CoreException
        
    {
        addClasspathEntry( JavaCore.create( project ), cpe );
    }

    public static void addClasspathEntry( final IJavaProject project,
                                          final IClasspathEntry cpe )
    
        throws CoreException
        
    {
        final IClasspathEntry[] cpOld = project.getRawClasspath();
        final IClasspathEntry[] cpNew = new IClasspathEntry[ cpOld.length + 1 ];
        System.arraycopy( cpOld, 0, cpNew, 0, cpOld.length );
        cpNew[ cpOld.length ] = cpe;
        project.setRawClasspath( cpNew, null );
    }

    public static List<IClasspathEntry> getClasspathEntries( final IProject project,
                                                             final IProjectFacet facet )
                                                             
        throws CoreException
        
    {
        final IJavaProject jproj = JavaCore.create( project );
        return getClasspathEntries( jproj, facet );
    }
    
    public static List<IClasspathEntry> getClasspathEntries( final IJavaProject project,
                                                             final IProjectFacet facet )
    
        throws CoreException
        
    {
        final List<IClasspathEntry> result = new ArrayList<IClasspathEntry>();
        
        for( IClasspathEntry cpe : project.getRawClasspath() )
        {
            final Set<Object> owners = getOwners( project, cpe );
            
            if( owners.contains( facet ) )
            {
                result.add( cpe );
            }
        }
        
        return result;
    }
    
    
    public static void addClasspathEntries( final IProject project,
                                            final IProjectFacet facet,
                                            final List<IClasspathEntry> cpentries )
    
        throws CoreException
        
    {
        final IJavaProject jproj = JavaCore.create( project );
        addClasspathEntries( jproj, facet, cpentries );
    }
    
    public static void addClasspathEntries( final IJavaProject project,
                                            final IProjectFacet facet,
                                            final List<IClasspathEntry> cpentries )
    
        throws CoreException
        
    {
        convertLegacyMetadata( project );
        
        final List<IClasspathEntry> cp = getProjectClasspath( project );
        
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

            final Set<Object> owners = getOwners( project, existingClasspathEntry );
            
            owners.add( facet );
            
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

        setProjectClasspath( project, cp );
    }
    
    public static void removeClasspathEntries( final IProject project,
                                               final IProjectFacet facet )
    
        throws CoreException
        
    {
        final IJavaProject jproj = JavaCore.create( project );
        removeClasspathEntries( jproj, facet );
    }
        
    public static void removeClasspathEntries( final IJavaProject project,
                                               final IProjectFacet facet )
    
        throws CoreException
        
    {
        convertLegacyMetadata( project );
        
        final List<IClasspathEntry> cp = getProjectClasspath( project );
        boolean cpchanged = false;
        
        for( ListIterator<IClasspathEntry> itr = cp.listIterator(); itr.hasNext(); )
        {
            final IClasspathEntry cpe = itr.next();
            final Set<Object> owners = getOwners( project, cpe );
            
            if( owners.remove( facet ) )
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
            setProjectClasspath( project, cp );
        }
    }

    public static void removeClasspathEntries( final IProject project,
                                               final IProjectFacet facet,
                                               final List<IClasspathEntry> cpentries )
    
        throws CoreException
        
    {
        final IJavaProject jproj = JavaCore.create( project );
        removeClasspathEntries( jproj, facet, cpentries );
    }
        
    public static void removeClasspathEntries( final IJavaProject project,
                                               final IProjectFacet facet,
                                               final List<IClasspathEntry> cpentries )
    
        throws CoreException
        
    {
        convertLegacyMetadata( project );
        
        final List<IClasspathEntry> cp = getProjectClasspath( project );
        boolean cpchanged = false;
        
        for( ListIterator<IClasspathEntry> itr = cp.listIterator(); itr.hasNext(); )
        {
            final IClasspathEntry cpe = itr.next();
            
            if( cpentries.contains( cpe ) )
            {
                final Set<Object> owners = getOwners( project, cpe );
                
                if( owners.remove( facet ) )
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
        }

        if( cpchanged )
        {
            setProjectClasspath( project, cp );
        }
    }
    
    private static Set<Object> getOwners( final IJavaProject project,
                                          final IClasspathEntry cpe )
                                          
        throws CoreException
        
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
            
            owners.addAll( getOwnersFromLegacyMetadata( project, cpe ) );
            
            if( owners.isEmpty() )
            {
                owners.add( SYSTEM_OWNER );
            }
        }
        
        return owners;
    }

    private static Set<Object> getOwnersFromLegacyMetadata( final IJavaProject project,
                                                            final IClasspathEntry cpe )
    
        throws CoreException
        
    {
        final IProject pj = project.getProject();
        final IFile legacyMetadataFile = pj.getFile( LEGACY_METADATA_FILE_NAME );
        
        if( legacyMetadataFile.exists() )
        {
            final ProjectScope scope = new ProjectScope( pj );
            final IEclipsePreferences pluginRoot = scope.getNode( FacetCorePlugin.PLUGIN_ID );
            final Preferences root = pluginRoot.node( "classpath.helper" ); //$NON-NLS-1$
            
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
                    final IPath path = new Path( key.replaceAll( "::", "/" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                    
                    if( cpe.getPath().equals( path ) )
                    {
                        return decodeOwnersString( owners );
                    }
                }
            }
        }
        
        return Collections.emptySet();
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
                final IProjectFacet facet = (IProjectFacet) owner;
                buf.append( facet.getId() );
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
                String facetId = segment;
                final int colon = facetId.indexOf( ':' );
                
                if( colon != -1 )
                {
                    facetId = facetId.substring( 0, colon );
                }
                
                owners.add( ProjectFacetsManager.getProjectFacet( facetId ) );
            }
        }
        
        return owners;
    }
    
    private static void convertLegacyMetadata( final IJavaProject project )
    
        throws CoreException
        
    {
        final IProject pj = project.getProject();
        final IFile legacyMetadataFile = pj.getFile( LEGACY_METADATA_FILE_NAME );
        
        if( legacyMetadataFile.exists() )
        {
            final ProjectScope scope = new ProjectScope( pj );
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
                final List<IClasspathEntry> cp = getProjectClasspath( project );
                boolean cpchanged = false;
                
                for( ListIterator<IClasspathEntry> itr = cp.listIterator(); itr.hasNext(); )
                {
                    final IClasspathEntry cpe = itr.next();
                    final String ownersString = metadata.get( cpe.getPath() );
                    
                    if( ownersString != null )
                    {
                        final Set<Object> owners = decodeOwnersString( ownersString );
                        itr.set( setOwners( cpe, encodeOwnersString( owners ) ) );
                        cpchanged = true;
                    }
                }
                
                if( cpchanged )
                {
                    setProjectClasspath( project, cp );
                }
            }
            
            legacyMetadataFile.delete( true, null );
        }
    }
    
}
