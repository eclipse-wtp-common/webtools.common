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

package org.eclipse.jst.common.project.facet.ui.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StandardJreComponentLabelProvider 

    implements IRuntimeComponentLabelProvider 
    
{
    private final IRuntimeComponent rc;

    public StandardJreComponentLabelProvider( final IRuntimeComponent rc ) 
    {
        this.rc = rc;
    }

    public String getLabel() 
    {
        final IRuntimeComponentVersion rcv = this.rc.getRuntimeComponentVersion();
        return Resources.bind( Resources.label, rcv.getVersionString() );
    }
    
    public static final class Factory
    
        implements IAdapterFactory
        
    {
        private static final Class[] ADAPTER_TYPES = { IRuntimeComponentLabelProvider.class };

        public Class[] getAdapterList()
        {
            return ADAPTER_TYPES;
        }

        public Object getAdapter( final Object adaptableObject, 
                                  final Class adapterType )
        {
            return new StandardJreComponentLabelProvider( (IRuntimeComponent) adaptableObject );
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String label;
        
        static
        {
            initializeMessages( StandardJreComponentLabelProvider.class.getName(), 
                                Resources.class );
        }
    }
    
}
