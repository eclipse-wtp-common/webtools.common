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

package org.eclipse.wst.common.project.facet.core.util.internal;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class XmlUtil
{
    private XmlUtil() {}
    
    public static String escape( final String string )
    {
        final StringBuilder buf = new StringBuilder();
        
        for( int i = 0, n = string.length(); i < n; i++ )
        {
            final char ch = string.charAt( i );
            
            if( ch == '<' )
            {
                buf.append( "&lt;" ); //$NON-NLS-1$
            }
            else if( ch == '>' )
            {
                buf.append( "&gt;" ); //$NON-NLS-1$
            }
            else if( ch == '&' )
            {
                buf.append( "&amp;" ); //$NON-NLS-1$
            }
            else
            {
                buf.append( ch );
            }
        }
        
        return buf.toString();
    }

}
