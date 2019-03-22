/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * The default version comparator that will be used when one is not explicitly 
 * specified. The default version comparator can handle version strings using 
 * the standard decimal notation. It can also be subclassed to modify the 
 * separators that are used or to provide custom parsing for a version segment.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class DefaultVersionComparator

    implements Comparator<String>
    
{
    public final int compare( final String obj1,
                              final String obj2 )
    
        throws VersionFormatException
        
    {
        final Comparable<Object>[] parsed1 = parse( obj1 );
        final Comparable<Object>[] parsed2 = parse( obj2 );
        
        for( int i = 0; i < parsed1.length && i < parsed2.length; i++ )
        {
            final int res = parsed1[ i ].compareTo( parsed2[ i ] );
            if( res != 0 ) return res;
        }
        
        if( parsed1.length > parsed2.length )
        {
            return 1;
        }
        else if( parsed1.length < parsed2.length )
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
    
    /**
     * Returns the string containing the separator characters that should be
     * used when breaking the version string into segments. The default
     * implementation returns ".". Subclasses can override this method.
     * 
     * @return the separator characters
     */
    
    protected String getSeparators()
    {
        return "."; //$NON-NLS-1$
    }
    
    /**
     * Parses a segment of the version string. The default implementation parses
     * the first segment as an integer (leading zeroes are ignored) and the
     * rest of the segments as decimals (leading zeroes are kept). Subclasses 
     * can override this method to provide custom parsing for any number of 
     * segments.
     * 
     * @param version the full version string
     * @param segment the version segment
     * @param position the position of the segment in the version string
     * @return the parsed representation of the segment as a {@link Comparable}
     * @throws VersionFormatException if encountered an error while parsing
     */
    
    protected Comparable<? extends Object> parse( final String version,
                                                  final String segment,
                                                  final int position )
    
        throws VersionFormatException
        
    {
        try
        {
            if( position == 0 )
            {
                return new Integer( segment );
            }
            else
            {
                return new BigDecimal( "." + segment ); //$NON-NLS-1$
            }
        }
        catch( NumberFormatException e )
        {
            throw new VersionFormatException( this, version );
        }
    }
    
    /**
     * Parses the version string.
     * 
     * @param ver the version string
     * @return an array containing the parsed representation of the version
     */
    
    @SuppressWarnings( "unchecked" )
    private Comparable<Object>[] parse( final String ver )
    {
        final List<String> segments = new ArrayList<String>();
        
        for( StringTokenizer t = new StringTokenizer( ver, getSeparators() );
             t.hasMoreTokens(); )
        {
            segments.add( t.nextToken() );
        }
        
        final Comparable[] parsed = new Comparable[ segments.size() ];
        
        for( int i = 0, n = segments.size(); i < n; i++ )
        {
            parsed[ i ] = parse( ver, segments.get( i ), i );
        }
        
        return parsed;
    }
    
}
