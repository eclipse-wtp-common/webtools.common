package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IListener;
import org.eclipse.wst.common.project.facet.core.internal.CopyOnWriteSet;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;

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

    private final List filters;
    private final CopyOnWriteSet targetableRuntimes;
    private final CopyOnWriteSet targetedRuntimes;
    private IRuntime primaryRuntime;
    private IListener runtimeManagerListener;

    public ChangeTargetedRuntimesDataModel()
    {
        this.filters = new ArrayList();
        
        this.targetableRuntimes = new CopyOnWriteSet();
        this.targetableRuntimes.addAll( getAllRuntimes() );
        
        this.targetedRuntimes = new CopyOnWriteSet();
        
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
    
    public synchronized Set getAllRuntimes()
    {
        return RuntimeManager.getRuntimes();
    }
    
    public synchronized Set getTargetableRuntimes()
    {
        return this.targetableRuntimes;
    }
    
    public synchronized void refreshTargetableRuntimes()
    {
        final Set result = new HashSet();
        
        for( Iterator itr1 = getAllRuntimes().iterator(); itr1.hasNext(); )
        {
            final IRuntime r = (IRuntime) itr1.next();
            
            boolean ok = true;
            
            for( Iterator itr2 = this.filters.iterator(); itr2.hasNext(); )
            {
                final IRuntimeFilter filter = (IRuntimeFilter) itr2.next();
                
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
            
            boolean modified = false;
            
            for( Iterator itr = this.targetedRuntimes.iterator(); itr.hasNext(); )
            {
                final IRuntime r = (IRuntime) itr.next();
                
                if( ! this.targetableRuntimes.contains( r ) )
                {
                    itr.remove();
                    modified = true;
                }
            }
            
            if( modified )
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
    
    public synchronized Set getTargetedRuntimes()
    {
        return this.targetedRuntimes;
    }
    
    public synchronized void setTargetedRuntimes( final Set runtimes )
    {
        if( ! this.targetedRuntimes.equals( runtimes ) )
        {
            this.targetedRuntimes.clear();
            
            for( Iterator itr = runtimes.iterator(); itr.hasNext(); )
            {
                final IRuntime r = (IRuntime) itr.next();
                
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
            
            this.primaryRuntime 
                = (IRuntime) this.targetedRuntimes.iterator().next();
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
