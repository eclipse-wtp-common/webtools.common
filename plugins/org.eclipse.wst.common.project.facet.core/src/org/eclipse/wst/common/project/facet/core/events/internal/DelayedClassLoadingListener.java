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

package org.eclipse.wst.common.project.facet.core.events.internal;

import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.instantiate;

import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;

/**
 * This listener implementation is used to delay class loading of listeners registered via the
 * <code>listener</code> extension point until the listener is actually invoked. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class DelayedClassLoadingListener

    implements IFacetedProjectListener
    
{
    private final String pluginId;
    private final String listenerClassName;
    private IFacetedProjectListener listener;
    
    public DelayedClassLoadingListener( final String pluginId,
                                        final String listenerClassName )
    {
        this.pluginId = pluginId;
        this.listenerClassName = listenerClassName;
        this.listener = null;
    }
    
    public void handleEvent( IFacetedProjectEvent event )
    {
        synchronized( this )
        {
            if( this.listener == null )
            {
                this.listener 
                    = instantiate( this.pluginId, this.listenerClassName, 
                                   IFacetedProjectListener.class );
                
                if( this.listener == null )
                {
                    FacetedProjectFramework.removeListener( this );
                    return;
                }
            }
        }
        
        this.listener.handleEvent( event );
    }

}
