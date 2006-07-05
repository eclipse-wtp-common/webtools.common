package org.eclipse.wst.common.project.facet.core.tests.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

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
    
    public static Set asSet( final Object obj )
    {
        return asSet( new Object[] { obj } );
    }

    public static Set asSet( final Object obj1,
                             final Object obj2 )
    {
        return asSet( new Object[] { obj1, obj2 } );
    }

    public static Set asSet( final Object obj1,
                             final Object obj2,
                             final Object obj3 )
    {
        return asSet( new Object[] { obj1, obj2, obj3 } );
    }

    public static Set asSet( final Object obj1,
                             final Object obj2,
                             final Object obj3,
                             final Object obj4 )
    {
        return asSet( new Object[] { obj1, obj2, obj3, obj4 } );
    }

    public static Set asSet( final Object obj1,
                             final Object obj2,
                             final Object obj3,
                             final Object obj4,
                             final Object obj5 )
    {
        return asSet( new Object[] { obj1, obj2, obj3, obj4, obj5 } );
    }
    
    public static Set asSet( final Object[] array )
    {
        final HashSet set = new HashSet();
        set.addAll( Arrays.asList( array ) );
        return set;
    }

    public static List asList( final Object obj )
    {
        return asList( new Object[] { obj } );
    }

    public static List asList( final Object obj1,
                               final Object obj2 )
    {
        return asList( new Object[] { obj1, obj2 } );
    }

    public static List asList( final Object obj1,
                               final Object obj2,
                               final Object obj3 )
    {
        return asList( new Object[] { obj1, obj2, obj3 } );
    }

    public static List asList( final Object obj1,
                               final Object obj2,
                               final Object obj3,
                               final Object obj4 )
    {
        return asList( new Object[] { obj1, obj2, obj3, obj4 } );
    }

    public static List asList( final Object obj1,
                               final Object obj2,
                               final Object obj3,
                               final Object obj4,
                               final Object obj5 )
    {
        return asList( new Object[] { obj1, obj2, obj3, obj4, obj5 } );
    }
    
    public static List asList( final Object[] array )
    {
        return Arrays.asList( array );
    }

}
