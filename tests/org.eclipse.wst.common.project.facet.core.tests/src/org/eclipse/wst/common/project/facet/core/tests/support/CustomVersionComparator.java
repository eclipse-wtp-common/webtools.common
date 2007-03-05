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

import org.eclipse.wst.common.project.facet.core.DefaultVersionComparator;
import org.eclipse.wst.common.project.facet.core.VersionFormatException;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class CustomVersionComparator

    extends DefaultVersionComparator
    
{
    protected String getSeparators()
    {
        return ".#";
    }
    
    protected Comparable parse( final String version,
                                final String segment,
                                final int position )
    
        throws VersionFormatException
        
    {
        if( position == 2 )
        {
            return new Inverter( segment );
        }
        else
        {
            return super.parse( version, segment, position );
        }
    }
    
    public static class Inverter
    
        implements Comparable
        
    {
        private final Comparable base;
        
        public Inverter( final Comparable base )
        {
            this.base = base;
        }
        
        public boolean equals( final Object obj )
        {
            if( ! ( obj instanceof Inverter ) )
            {
                return false;
            }
            else
            {
                return this.base.equals( ( (Inverter) obj ).base );
            }
        }
        
        public int compareTo( final Object obj )
        {
            return -1 * this.base.compareTo( ( (Inverter) obj ).base );
        }
    }

}
