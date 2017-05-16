/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.tests.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class TestUtils
{
    public static void writeToFile( final IFile file,
                                    final String contents )
    
        throws CoreException
        
    {
        try
        {
            final byte[] bytes = contents.getBytes( "UTF-8" );
            
            if( file.exists() )
            {
                file.setContents( new ByteArrayInputStream( bytes ),
                                  false, false, null );
            }
            else
            {
                file.create( new ByteArrayInputStream( bytes ), false, null );
            }
        }
        catch( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }
    
    public static String readFromFile( final IFile file )
    
        throws CoreException, IOException
        
    {
        TestCase.assertTrue( file.exists() );
        
        final StringBuffer buf = new StringBuffer();
        final Reader r = new InputStreamReader( file.getContents() );
        
        try
        {
            char[] chars = new char[ 1024 ];
            
            for( int count; ( count = r.read( chars ) ) != -1; )
            {
                buf.append( chars, 0, count );
            }
        }
        finally
        {
            try
            {
                r.close();
            }
            catch( IOException e ) {}
        }
        
        return buf.toString();
    }
    
    
    public static void assertEquals( final IFile file,
                                     final String expectedContents )
    
        throws CoreException, IOException
        
    {
        TestCase.assertEquals( readFromFile( file ), expectedContents );
    }
    
    public static void assertFileContains( final IFile file,
                                           final String str )
    
        throws CoreException, IOException
        
    {
        TestCase.assertTrue( readFromFile( file ).indexOf( str ) != -1 );
    }
    
    public static void waitForCondition( final ICondition condition )
    {
        waitForCondition( condition, 10 );
    }
    
    public static void waitForCondition( final ICondition condition,
                                         final int seconds )
    {
        for( int i = 0; i < seconds && ! condition.check(); i++ )
        {
            try
            {
                Thread.sleep( 1000 );
            }
            catch( InterruptedException e ) {}
        }
        
        TestCase.assertTrue( condition.check() );
    }
    
    public static interface ICondition
    {
        boolean check();
    }
    
    public static <T> Set<T> asSet( final T... objects )
    {
        final Set<T> set = new LinkedHashSet<T>();
        set.addAll( Arrays.asList( objects ) );
        return set;
    }
    
}
