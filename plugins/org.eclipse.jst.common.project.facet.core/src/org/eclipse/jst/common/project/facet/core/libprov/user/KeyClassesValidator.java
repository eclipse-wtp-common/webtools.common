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

package org.eclipse.jst.common.project.facet.core.libprov.user;

import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.PLUGIN_ID;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.osgi.util.NLS;

/**
 * An implementation of {@link UserLibraryValidator} that checks for presence of key classes in
 * the selected libraries. Validation fails if one or more of the key classes are not found. This
 * validator can be used as is or subclassed.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public class KeyClassesValidator

    extends UserLibraryValidator
    
{
    private final Set<String> classFileNames = new HashSet<String>();
    private final Map<String,String> classFileNameToClassName = new HashMap<String,String>();

    @Override
    public void init( final List<String> params )
    {
        for( String className : params )
        {
            final StringBuilder buf = new StringBuilder();
            
            for( int i = 0, n = className.length(); i < n; i++ )
            {
                final char ch = className.charAt( i );
                
                if( ch == '.' )
                {
                    buf.append( '/' );
                }
                else
                {
                    buf.append( ch );
                }
            }
            
            buf.append( ".class" ); //$NON-NLS-1$
            
            final String classFileName = buf.toString();
            this.classFileNames.add( classFileName );
            this.classFileNameToClassName.put( classFileName, className );
        }
    }

    @Override
    public IStatus validate( final UserLibraryProviderInstallOperationConfig config )
    {
        final Map<String,Integer> classAppearanceCounts = new HashMap<String,Integer>();
        
        for( String classFileName : this.classFileNames )
        {
            classAppearanceCounts.put( classFileName, 0 );
        }
        
        for( IClasspathEntry cpe : config.resolve() )
        {
            if( cpe.getEntryKind() == IClasspathEntry.CPE_LIBRARY )
            {
                final File file = cpe.getPath().toFile();
                
                if( file.exists() )
                {
                    ZipFile zip = null;
                    
                    try
                    {
                        zip = new ZipFile( file );
                        
                        for( Enumeration<? extends ZipEntry> itr = zip.entries(); itr.hasMoreElements(); )
                        {
                            final ZipEntry zipEntry = itr.nextElement();
                            final String name = zipEntry.getName();
                            
                            Integer count = classAppearanceCounts.get( name );
                            
                            if( count != null )
                            {
                                classAppearanceCounts.put( name, count + 1 );
                            }
                        }
                    }
                    catch( IOException e )
                    {
                        
                    }
                    finally
                    {
                        if( zip != null )
                        {
                            try
                            {
                                zip.close();
                            }
                            catch( IOException e ) {}
                        }
                    }
                }
            }
        }
        
        for( Map.Entry<String,Integer> entry : classAppearanceCounts.entrySet() )
        {
            final int count = entry.getValue();
            
            if( count != 1 )
            {
                final String classFileName = entry.getKey();
                final String className = this.classFileNameToClassName.get( classFileName );
                final String message;
                
                if( count == 0 )
                {
                    message = Resources.bind( Resources.classNotFound, className );
                }
                else
                {
                    message = Resources.bind( Resources.classPresentMultipleTimes, className );
                }
                
                return new Status( IStatus.ERROR, PLUGIN_ID, message );
            }
        }
        
        return Status.OK_STATUS;
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String classNotFound;
        public static String classPresentMultipleTimes;
        
        static
        {
            initializeMessages( KeyClassesValidator.class.getName(), 
                                Resources.class );
        }
    }

}
