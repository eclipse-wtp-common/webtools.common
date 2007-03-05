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

package org.eclipse.wst.common.project.facet.core.events.internal;

import static org.eclipse.wst.common.project.facet.core.internal.util.PluginUtil.instantiate;

import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IRuntimeChangedEvent;
import org.eclipse.wst.common.project.facet.core.IVersionExpr;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IPrimaryRuntimeChangedEvent;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetActionEvent;
import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class LegacyEventHandlerAdapter

    implements IFacetedProjectListener
    
{
    private final IProjectFacet facet;
    private final IVersionExpr vexpr;
    private final String pluginId;
    private Object delegate;
    
    public LegacyEventHandlerAdapter( final IProjectFacet facet,
                                      final IVersionExpr vexpr,
                                      final String delegatePluginId,
                                      final String delegateClassName )
    {
        this.facet = facet;
        this.vexpr = vexpr;
        this.pluginId = delegatePluginId;
        this.delegate = delegateClassName;
    }
    
    public void handleEvent( final IFacetedProjectEvent event )
    {
        if( event instanceof IProjectFacetActionEvent )
        {
            final IProjectFacetActionEvent ev = (IProjectFacetActionEvent) event;
            
            if( this.facet == ev.getProjectFacet() && 
                this.vexpr.check( ev.getProjectFacetVersion() ) )
            {
                final IDelegate delegate = getDelegate();
                
                if( delegate != null )
                {
                    try
                    {
                        delegate.execute( ev.getProject().getProject(), ev.getProjectFacetVersion(), 
                                          ev.getActionConfig(), null );
                    }
                    catch( Exception e )
                    {
                        FacetCorePlugin.log( e );
                    }
                }
            }
        }
        else if( event instanceof IPrimaryRuntimeChangedEvent )
        {
            final IPrimaryRuntimeChangedEvent ev = (IPrimaryRuntimeChangedEvent) event;
            final IFacetedProject project = ev.getProject();
            
            if( project.hasProjectFacet( this.facet ) )
            {
                final IProjectFacetVersion fv = project.getInstalledVersion( this.facet );
                
                if( this.vexpr.check( fv ) )
                {
                    final RuntimeChangedEvent legacyEvent
                        = new RuntimeChangedEvent( ev.getOldPrimaryRuntime(), 
                                                   ev.getNewPrimaryRuntime() );
                    
                    final IDelegate delegate = getDelegate();
                    
                    if( delegate != null )
                    {
                        try
                        {
                            delegate.execute( ev.getProject().getProject(), fv, legacyEvent, null );
                        }
                        catch( Exception e )
                        {
                            FacetCorePlugin.log( e );
                        }
                    }
                }
            }
        }
        else
        {
            throw new IllegalStateException();
        }
    }
    
    private synchronized IDelegate getDelegate()
    {
        try
        {
            if( this.delegate instanceof String )
            {
                this.delegate 
                    = instantiate( this.pluginId, (String) this.delegate, IDelegate.class );
            }
            
            return (IDelegate) this.delegate;
        }
        catch( Exception e )
        {
            FacetCorePlugin.log( e );
        }
        
        return null;
    }
    
    private static final class RuntimeChangedEvent

        implements IRuntimeChangedEvent
        
    {
        private final IRuntime oldRuntime;
        private final IRuntime newRuntime;
        
        public RuntimeChangedEvent( final IRuntime oldRuntime,
                                    final IRuntime newRuntime )
        {
            this.oldRuntime = oldRuntime;
            this.newRuntime = newRuntime;
        }
        
        public IRuntime getOldRuntime()
        {
            return this.oldRuntime;
        }
    
        public IRuntime getNewRuntime()
        {
            return this.newRuntime;
        }
    
    }

}
