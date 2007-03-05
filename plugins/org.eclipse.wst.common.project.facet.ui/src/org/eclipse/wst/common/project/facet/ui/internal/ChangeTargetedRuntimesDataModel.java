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

package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.wst.common.project.facet.core.IListener;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ChangeTargetedRuntimesDataModel

    extends AbstractDataModel
    
{
    public static final String EVENT_AVAILABLE_RUNTIMES_CHANGED 
        = "availableRuntimesChanged"; //$NON-NLS-1$
    
    public static final String EVENT_TARGETABLE_RUNTIMES_CHANGED
        = "targetableRuntimesChanged"; //$NON-NLS-1$
    
    public static final String EVENT_TARGETED_RUNTIMES_CHANGED
        = "targetedRuntimesChanged"; //$NON-NLS-1$
    
    public static final String EVENT_PRIMARY_RUNTIME_CHANGED
        = "primaryRuntimeChanged"; //$NON-NLS-1$
    
    public interface IRuntimeFilter
    {
        boolean check( IRuntime runtime );
    }

    private final List<IRuntimeFilter> filters;
    private final Set<IRuntime> targetableRuntimes;
    private final Set<IRuntime> targetedRuntimes;
    private IRuntime primaryRuntime;
    private IListener runtimeManagerListener;

    public ChangeTargetedRuntimesDataModel()
    {
        this.filters = new ArrayList<IRuntimeFilter>();
        
        this.targetableRuntimes = new CopyOnWriteArraySet<IRuntime>();
        this.targetableRuntimes.addAll( getAllRuntimes() );
        
        this.targetedRuntimes = new CopyOnWriteArraySet<IRuntime>();
        
        this.primaryRuntime = null;
        
        this.runtimeManagerListener = new IListener()
        {
            public void handle()
            {
                notifyListeners( EVENT_AVAILABLE_RUNTIMES_CHANGED );
                refreshTargetableRuntimes();
            }
        };
        
        RuntimeManager.addRuntimeListener( this.runtimeManagerListener );
    }
    
    public synchronized void addRuntimeFilter( final IRuntimeFilter filter )
    {
        this.filters.add( filter );
        refreshTargetableRuntimes();
    }
    
    public synchronized void removeRuntimeFilter( final IRuntimeFilter filter )
    {
        this.filters.remove( filter );
        refreshTargetableRuntimes();
    }
    
    public synchronized Set<IRuntime> getAllRuntimes()
    {
        return RuntimeManager.getRuntimes();
    }
    
    public synchronized Set<IRuntime> getTargetableRuntimes()
    {
        return this.targetableRuntimes;
    }
    
    public synchronized void refreshTargetableRuntimes()
    {
        final Set<IRuntime> result = new HashSet<IRuntime>();
        
        for( IRuntime r : getAllRuntimes() )
        {
            boolean ok = true;

            for( IRuntimeFilter filter : this.filters )
            {
                if( ! filter.check( r ) )
                {
                    ok = false;
                    break;
                }
            }
            
            if( ok )
            {
                result.add( r );
            }
        }
        
        if( ! this.targetableRuntimes.equals( result ) )
        {
            this.targetableRuntimes.clear();
            this.targetableRuntimes.addAll( result );
            notifyListeners( EVENT_TARGETABLE_RUNTIMES_CHANGED );
            
            final List<IRuntime> toRemove = new ArrayList<IRuntime>();
            
            for( IRuntime r : this.targetedRuntimes )
            {
                if( ! this.targetableRuntimes.contains( r ) )
                {
                    toRemove.add( r );
                }
            }
            
            this.targetedRuntimes.removeAll( toRemove );
            
            if( ! toRemove.isEmpty() )
            {
                notifyListeners( EVENT_TARGETED_RUNTIMES_CHANGED );
                
                if( this.primaryRuntime != null && 
                    ! this.targetableRuntimes.contains( this.primaryRuntime ) )
                {
                    autoAssignPrimaryRuntime();
                }
            }
        }
    }
    
    public synchronized Set<IRuntime> getTargetedRuntimes()
    {
        return this.targetedRuntimes;
    }
    
    public synchronized void setTargetedRuntimes( final Set<IRuntime> runtimes )
    {
        if( ! this.targetedRuntimes.equals( runtimes ) )
        {
            this.targetedRuntimes.clear();
            
            for( IRuntime r : runtimes )
            {
                if( this.targetableRuntimes.contains( r ) )
                {
                    this.targetedRuntimes.add( r );
                }
            }
            
            notifyListeners( EVENT_TARGETED_RUNTIMES_CHANGED );
            
            if( this.primaryRuntime == null ||
                ! this.targetedRuntimes.contains( this.primaryRuntime ) )
            {
                autoAssignPrimaryRuntime();
            }
        }
    }
    
    public synchronized void addTargetedRuntime( final IRuntime runtime )
    {
        if( runtime == null )
        {
            throw new NullPointerException();
        }
        else
        {
            this.targetedRuntimes.add( runtime );
            notifyListeners( EVENT_TARGETED_RUNTIMES_CHANGED );
            
            if( this.primaryRuntime == null )
            {
                this.primaryRuntime = runtime;
                notifyListeners( EVENT_PRIMARY_RUNTIME_CHANGED );
            }
        }
    }
    
    public synchronized void removeTargetedRuntime( final IRuntime runtime )
    {
        if( runtime == null )
        {
            throw new NullPointerException();
        }
        else
        {
            if( this.targetedRuntimes.remove( runtime ) )
            {
                notifyListeners( EVENT_TARGETED_RUNTIMES_CHANGED );
                
                if( runtime.equals( this.primaryRuntime ) )
                {
                    autoAssignPrimaryRuntime();
                }
            }
        }
    }
    
    public synchronized IRuntime getPrimaryRuntime()
    {
        return this.primaryRuntime;
    }
    
    public synchronized void setPrimaryRuntime( final IRuntime runtime )
    {
        if( ! equals( this.primaryRuntime, runtime ) )
        {
            if( runtime == null && this.targetedRuntimes.size() > 0 )
            {
                throw new IllegalArgumentException();
            }
            
            if( this.targetedRuntimes.contains( runtime ) )
            {
                this.primaryRuntime = runtime;
            }
            
            notifyListeners( EVENT_PRIMARY_RUNTIME_CHANGED );
        }
    }
    
    private void autoAssignPrimaryRuntime()
    {
        if( this.targetedRuntimes.isEmpty() )
        {
            this.primaryRuntime = null;
        }
        else
        {
            // Pick one to be the primary. No special semantics as to which 
            // one.
            
            this.primaryRuntime = this.targetedRuntimes.iterator().next();
        }
        
        notifyListeners( EVENT_PRIMARY_RUNTIME_CHANGED );
    }
    
    public void dispose()
    {
        RuntimeManager.removeRuntimeListener( this.runtimeManagerListener );
    }
    
    private static boolean equals( final IRuntime r1,
                                   final IRuntime r2 )
    {
        if( r1 == null && r2 == null )
        {
            return true;
        }
        else if( r1 == null || r2 == null )
        {
            return false;
        }
        else
        {
            return r1.equals( r2 );
        }
    }
    

}
