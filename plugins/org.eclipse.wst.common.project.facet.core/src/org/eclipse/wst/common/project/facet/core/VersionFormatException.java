/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core;

import org.eclipse.osgi.util.NLS;

/**
 * The exception that's thrown when version string cannot be parsed.
 * 
 * <p><i>This class is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being 
 * made available at this early stage to solicit feedback from pioneering 
 * adopters on the understanding that any code that uses this API will almost 
 * certainly be broken (repeatedly) as the API evolves.</i></p>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class VersionFormatException 
    
    extends RuntimeException 
    
{
    private static final long serialVersionUID = 1L;
    
    private final Object comparator;
    private final String version;
    
    public VersionFormatException( final Object comparator,
                                   final String version )
    {
        this.comparator = comparator;
        this.version = version;
    }
    
    public Object getComparator()
    {
        return this.comparator;
    }
    
    public String getVersion()
    {
        return this.version;
    }
    
    public String getMessage()
    {
        return NLS.bind( Resources.couldNotParse, this.version,
                         this.comparator.getClass().getName() );
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String couldNotParse;
        
        static
        {
            initializeMessages( VersionFormatException.class.getName(), 
                                Resources.class );
        }
    }

}
