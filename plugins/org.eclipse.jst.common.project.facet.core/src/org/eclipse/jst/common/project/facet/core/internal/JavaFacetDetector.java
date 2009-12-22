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

package org.eclipse.jst.common.project.facet.core.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.jst.common.project.facet.core.JavaFacetInstallConfig;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.ProjectFacetDetector;

public final class JavaFacetDetector

    extends ProjectFacetDetector
    
{
    private static final Pattern IMPORT_STATEMENT_PATTERN = Pattern.compile( ".*package[ ]*(.*);.*" ); //$NON-NLS-1$
    
    public static void main( final String[] args )
    {
        final Matcher matcher = IMPORT_STATEMENT_PATTERN.matcher( "package a.b.c;" ); //$NON-NLS-1$
        
        if( matcher.matches() )
        {
            System.err.println( matcher.group( 1 ) );
        }
        else
        {
            System.err.println( "no match" ); //$NON-NLS-1$
        }
    }
    
    @Override
    public void detect( final IFacetedProjectWorkingCopy fpjwc,
                        final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        monitor.beginTask( "", 1 ); //$NON-NLS-1$
        
        try
        {
            if( fpjwc.hasProjectFacet( JavaFacet.FACET ) )
            {
                return;
            }
            
            if( fpjwc.getProject().hasNature( JavaCore.NATURE_ID ) )
            {
                fpjwc.addProjectFacet( JavaFacet.FACET.getDefaultVersion() );
            }
            else
            {
                final IProject pj = fpjwc.getProject();
                final List<IPath> sourceFolders = detectSourceFolders( pj );
                
                if( ! sourceFolders.isEmpty() )
                {
                    fpjwc.addProjectFacet( JavaFacet.FACET.getDefaultVersion() );
                    
                    final JavaFacetInstallConfig javaFacetInstallConfig
                        = (JavaFacetInstallConfig) fpjwc.getProjectFacetAction( JavaFacet.FACET ).getConfig();
                    
                    javaFacetInstallConfig.setSourceFolders( sourceFolders );
                    javaFacetInstallConfig.setDefaultOutputFolder( null );
                }
            }
        }
        finally
        {
            monitor.done();
        }
    }
    
    private List<IPath> detectSourceFolders( final IResource resource )
    
        throws CoreException
        
    {
        if( resource instanceof IFile )
        {
            final String extension = resource.getFileExtension();
            
            if( extension != null && extension.equals( "java" ) ) //$NON-NLS-1$
            {
                final InputStream in = ( (IFile) resource ).getContents( true );
                
                try
                {
                    final BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
                    
                    for( String line = reader.readLine(); line != null; line = reader.readLine() )
                    {
                        final Matcher matcher = IMPORT_STATEMENT_PATTERN.matcher( line );
                        
                        if( matcher.matches() )
                        {
                            final String packageName = matcher.group( 1 );
                            final IPath packagePath = new Path( packageName.replace( '.', '/' ) );
                            final int packagePathLength = packagePath.segmentCount();
                            
                            IPath path = resource.getProjectRelativePath().removeLastSegments( 1 );
                            
                            if( path.segmentCount() > packagePathLength )
                            {
                                for( int i = packagePathLength - 1; i >= 0; i-- )
                                {
                                    if( path.lastSegment().equals( packagePath.segment( i ) ) )
                                    {
                                        path = path.removeLastSegments( 1 );
                                    }
                                    else
                                    {
                                        return Collections.emptyList();
                                    }
                                }
                                
                                return Collections.singletonList( path );
                            }
                            else
                            {
                                return Collections.emptyList();
                            }
                        }
                    }
                }
                catch( IOException e )
                {
                    FacetCorePlugin.log( e );
                }
                finally
                {
                    try
                    {
                        in.close();
                    }
                    catch( IOException e ) {}
                }
            }
            
            return Collections.emptyList();
        }
        else
        {
            List<IPath> result = null;
            
            for( IResource child : ( (IContainer) resource ).members() )
            {
                final List<IPath> subResult = detectSourceFolders( child );
                final IPath childPath = child.getProjectRelativePath();

                if( ! subResult.isEmpty() )
                {
                    final IPath subResultPath = subResult.get( 0 );
                    
                    if( subResult.size() == 1 && subResultPath.isPrefixOf( childPath ) && 
                        childPath.segmentCount() > subResultPath.segmentCount() )
                    {
                        return subResult;
                    }
                    
                    if( result == null )
                    {
                        result = new ArrayList<IPath>();
                    }
                    
                    result.addAll( subResult );
                }
            }
            
            return ( result == null ? Collections.<IPath>emptyList() : result );
        }
    }
    
}
