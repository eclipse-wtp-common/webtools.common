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

package org.eclipse.wst.common.project.facet.core.internal;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IActionConfig;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectValidator;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

/* 
 * Synchronization Notes
 * 
 * 1. There is an internal lock object that's used to synchronize access to
 *    the data structure. By synchronizing on an internal object, outside code
 *    cannot cause a deadlock by synchronizing on the FacetedProject object.
 *    
 * 2. Readers synchronize on the lock object for the duration of the method
 *    call. This protects the readers from writers and makes sure that reader
 *    is not reading stale data from thread's local memory.
 *    
 * 3. All collections that are returned by the reader methods are guaranteed
 *    to not change after the fact. This is implemented through a copy-on-write 
 *    policy.
 * 
 * 4. Writers synchronize on the lock object briefly at the start of the method 
 *    and mark the FacetedProject as being modified. If the project is already
 *    being modified, the new writer will wait. Inside the bodies of the
 *    modifier methods, the writer thread is only synchronized on the lock
 *    object while modifying the internal datastructures. These synchronization
 *    sections are kept short and they never span over code that might modify
 *    file system resources. This is done to prevent deadlocks. Once the write
 *    is complete, the writer thread synchronizes on the lock object, resets the 
 *    "being modified" flag, and notifies any writers that may be waiting. 
 */

public final class FacetedProject

    implements IFacetedProject
    
{
    private static final String TRACING_DELEGATE_CALLS
        = FacetCorePlugin.PLUGIN_ID + "/delegate/calls"; //$NON-NLS-1$
    
    private static final String FACETS_METADATA_FILE
        = ".settings/" + FacetCorePlugin.PLUGIN_ID + ".xml"; //$NON-NLS-1$ //$NON-NLS-2$
    
    private static final String EL_RUNTIME = "runtime"; //$NON-NLS-1$
    private static final String EL_FIXED = "fixed"; //$NON-NLS-1$
    private static final String EL_INSTALLED = "installed"; //$NON-NLS-1$
    private static final String ATTR_NAME = "name"; //$NON-NLS-1$
    private static final String ATTR_FACET = "facet"; //$NON-NLS-1$
    private static final String ATTR_VERSION = "version"; //$NON-NLS-1$

    private final IProject project;
    private final CopyOnWriteSet facets;
    private final CopyOnWriteSet fixed;
    private String runtimeName;
    IFile f;
    private long fLastModified = -1;
    private final List listeners;
    private final Object lock = new Object();
    private boolean isBeingModified = false;
    private Thread modifierThread = null;
    
    FacetedProject( final IProject project )
    
        throws CoreException
        
    {
        this.project = project;
        this.facets = new CopyOnWriteSet();
        this.fixed = new CopyOnWriteSet();
        this.listeners = new ArrayList();
        
        this.f = project.getFile( FACETS_METADATA_FILE );
        
        refresh();
    }
    
    public IProject getProject()
    {
        return this.project;
    }
    
    public Set getProjectFacets()
    {
        synchronized( this.lock )
        {
            return this.facets.getReadOnlySet();
        }
    }
    
    public boolean hasProjectFacet( final IProjectFacet f )
    {
        synchronized( this.lock )
        {
            for( Iterator itr = this.facets.iterator(); itr.hasNext(); )
            {
                final IProjectFacetVersion fv 
                    = (IProjectFacetVersion) itr.next();
                
                if( fv.getProjectFacet() == f )
                {
                    return true;
                }
            }
            
            return false;
        }
    }
    
    public boolean hasProjectFacet( final IProjectFacetVersion fv )
    {
        synchronized( this.lock )
        {
            return this.facets.contains( fv );
        }
    }
    
    public IProjectFacetVersion getInstalledVersion( final IProjectFacet f )
    {
        synchronized( this.lock )
        {
            for( Iterator itr = this.facets.iterator(); itr.hasNext(); )
            {
                final IProjectFacetVersion fv 
                    = (IProjectFacetVersion) itr.next();
                
                if( fv.getProjectFacet() == f )
                {
                    return fv;
                }
            }
            
            return null;
        }
    }

    public void installProjectFacet( final IProjectFacetVersion fv,
                                     final Object config,
                                     final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        final Action action 
            = new Action( Action.Type.INSTALL, fv, config );
            
        modify( Collections.singleton( action ), monitor );
    }

    public void uninstallProjectFacet( final IProjectFacetVersion fv,
                                       final Object config,
                                       final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        final Action action 
            = new Action( Action.Type.UNINSTALL, fv, config );
            
        modify( Collections.singleton( action ), monitor );
    }
    
    public void modify( final Set actions,
                        final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        beginModification();
        
        try
        {
            final IWorkspaceRunnable wr = new IWorkspaceRunnable()
            {
                public void run( final IProgressMonitor monitor ) 
                
                    throws CoreException
                    
                {
                    modifyInternal( actions, monitor );
                }
            };
            
            final IWorkspace ws = ResourcesPlugin.getWorkspace();
            ws.run( wr, ws.getRoot(), IWorkspace.AVOID_UPDATE, monitor );
        }
        finally
        {
            endModification();
        }
        
        notifyListeners();
    }
        
    private void modifyInternal( final Set actions,
                                 final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        if( monitor != null )
        {
            monitor.beginTask( "", actions.size() * 100 ); //$NON-NLS-1$
        }
        
        try
        {
            final IStatus st 
                = ProjectFacetsManager.check( this.facets, actions );
            
            if( ! st.isOK() )
            {
                throw new CoreException( st );
            }
            
            // Sort the actions into the order of execution.
            
            final List copy = new ArrayList( actions );
            ProjectFacetsManager.sort( this.facets, copy );
            
            // Update and check the action configs.
            
            for( int i = 0, n = copy.size(); i < n; i++ )
            {
                Action action = (Action) copy.get( i );
                final IProjectFacetVersion fv = action.getProjectFacetVersion();
                Object config = action.getConfig();
                
                if( config == null )
                {
                    config = fv.createActionConfig( action.getType(), 
                                                    this.project.getName() );
                    
                    if( config != null )
                    {
                        action = new Action( action.getType(), fv, config );
                        copy.set( i, action );
                    }
                }
                
                if( config != null )
                {
                    IActionConfig cfg = null;
                    
                    if( config instanceof IActionConfig )
                    {
                        cfg = (IActionConfig) config;
                    }
                    else
                    {
                        final IAdapterManager m = Platform.getAdapterManager();
                        cfg = (IActionConfig) m.loadAdapter( config, IActionConfig.class.getName() );
                    }
                    
                    if( cfg != null )
                    {
                        cfg.setProjectName( this.project.getName() );
                        cfg.setVersion( fv );
                        
                        final IStatus status = cfg.validate();
                        
                        if( status.getSeverity() != IStatus.OK )
                        {
                            throw new CoreException( status );
                        }
                    }
                }
            }
            
            // Execute the actions.
            
            for( Iterator itr = copy.iterator(); itr.hasNext(); )
            {
                final Action action = (Action) itr.next();
                final Action.Type type = action.getType();
                
                final ProjectFacetVersion fv 
                    = (ProjectFacetVersion) action.getProjectFacetVersion();
                
                callEventHandlers( fv, getPreEventHandlerType( type ), 
                                   action.getConfig(), submon( monitor, 10 ) );
                
                final IDelegate delegate = fv.getDelegate( type );
                
                if( delegate == null )
                {
                    if( monitor != null )
                    {
                        monitor.worked( 80 );
                    }
                }
                else
                {
                    callDelegate( fv, delegate, action.getConfig(), type,
                                  submon( monitor, 80 ) );
                }
        
                synchronized( this.lock )
                {
                    apply( action );
                }
                
                save();

                callEventHandlers( fv, getPostEventHandlerType( type ), 
                                   action.getConfig(), submon( monitor, 10 ) );
            }
        }
        finally
        {
            if( monitor != null )
            {
                monitor.done();
            }
        }
    }
    
    public Set getFixedProjectFacets()
    {
        synchronized( this.lock )
        {
            return this.fixed.getReadOnlySet();
        }
    }
    
    public void setFixedProjectFacets( final Set facets )
    
        throws CoreException
        
    {
        beginModification();
        
        try
        {
            final IWorkspaceRunnable wr = new IWorkspaceRunnable()
            {
                public void run( final IProgressMonitor monitor ) 
                
                    throws CoreException
                    
                {
                    synchronized( FacetedProject.this.lock )
                    {
                        FacetedProject.this.fixed.clear();
                        FacetedProject.this.fixed.addAll( facets );
                    }
                        
                    save();
                }
            };
            
            final IWorkspace ws = ResourcesPlugin.getWorkspace();
            ws.run( wr, ws.getRoot(), IWorkspace.AVOID_UPDATE, null );
        }
        finally
        {
            endModification();
        }
        
        notifyListeners();
    }
    
    public String getRuntimeName()
    {
        synchronized( this.lock )
        {
            return this.runtimeName;
        }
    }
    
    public IRuntime getRuntime()
    {
        synchronized( this.lock )
        {
            if( RuntimeManager.isRuntimeDefined( this.runtimeName ) )
            {
                return RuntimeManager.getRuntime( this.runtimeName );
            }
            else
            {
                return null;
            }
        }
    }
    
    public void setRuntime( final IRuntime runtime,
                            final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        beginModification();
        
        try
        {
            final IWorkspaceRunnable wr = new IWorkspaceRunnable()
            {
                public void run( final IProgressMonitor monitor ) 
                
                    throws CoreException
                    
                {
                    setRuntimeInternal( runtime, monitor );
                }
            };
            
            final IWorkspace ws = ResourcesPlugin.getWorkspace();
            ws.run( wr, ws.getRoot(), IWorkspace.AVOID_UPDATE, null );
        }
        finally
        {
            endModification();
        }
        
        notifyListeners();
    }
    
    private void setRuntimeInternal( final IRuntime runtime,
                                     final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        if( monitor != null )
        {
            monitor.beginTask( "", this.facets.size() ); //$NON-NLS-1$
        }
        
        try
        {
            if( runtime != null )
            {
                for( Iterator itr = this.facets.iterator(); itr.hasNext(); )
                {
                    final IProjectFacetVersion fv 
                        = (IProjectFacetVersion) itr.next();
                    
                    if( ! runtime.supports( fv ) )
                    {
                        final String msg 
                            = NLS.bind( Resources.facetNotSupported, 
                                        runtime.getName(), fv.toString() );
                        
                        final IStatus st 
                            = FacetCorePlugin.createErrorStatus( msg );
                        
                        throw new CoreException( st );
                    }
                }
            }
            
            this.runtimeName = ( runtime == null ? null : runtime.getName() );
            save();

            for( Iterator itr = this.facets.iterator(); itr.hasNext(); )
            {
                final ProjectFacetVersion fv
                    = (ProjectFacetVersion) itr.next();
                
                callEventHandlers( fv, EventHandler.Type.RUNTIME_CHANGED, null,
                                   submon( monitor, 1 ) );
            }
        }
        finally
        {
            if( monitor != null )
            {
                monitor.done();
            }
        }
    }
    
    public IMarker createErrorMarker( final String message )
    
        throws CoreException
        
    {
        return createErrorMarker( IFacetedProjectValidator.BASE_MARKER_ID, message );
    }

    public IMarker createErrorMarker( final String type,
                                      final String message )
    
        throws CoreException
        
    {
        return createMarker( IMarker.SEVERITY_ERROR, type, message );
    }
    
    public IMarker createWarningMarker( final String message )
    
        throws CoreException
        
    {
        return createWarningMarker( IFacetedProjectValidator.BASE_MARKER_ID, message );
    }
    
    public IMarker createWarningMarker( final String type,
                                      final String message )
    
        throws CoreException
        
    {
        return createMarker( IMarker.SEVERITY_WARNING, type, message );
    }
    
    private IMarker createMarker( final int severity,
                                  final String type,
                                  final String message )
      
        throws CoreException
      
    {
        final IMarker[] existing
            = this.project.findMarkers( type, false, IResource.DEPTH_ZERO );
        
        for( int i = 0; i < existing.length; i++ )
        {
            final IMarker m = existing[ i ];
            
            if( m.getAttribute( IMarker.SEVERITY, -1 ) == severity &&
                m.getAttribute( IMarker.MESSAGE, "" ).equals( message ) ) //$NON-NLS-1$
            {
                return m;
            }
        }
        
        final IMarker m = this.project.createMarker( type );
      
        m.setAttribute( IMarker.MESSAGE, message ); 
        m.setAttribute( IMarker.SEVERITY, severity );
      
        return m;
    }
    
    public void addListener( final IFacetedProjectListener listener )
    {
        synchronized( this.listeners )
        {
            this.listeners.add( listener );
        }
    }
    
    public void removeListener( final IFacetedProjectListener listener )
    {
        synchronized( this.listeners )
        {
            this.listeners.remove( listener );
        }
    }
    
    private void notifyListeners()
    {
        // Copy the list of listeners in order to avoid holding the monitor
        // while calling the listeners. This is done to avoid potential 
        // deadlocks.
        
        final Object[] copy;
        
        synchronized( this.listeners )
        {
            copy = this.listeners.toArray();
        }
        
        for( int i = 0; i < copy.length; i++ )
        {
            try
            {
                ( (IFacetedProjectListener) copy[ i ] ).projectChanged();
            }
            catch( Exception e )
            {
                FacetCorePlugin.log( e );
            }
        }
    }
    
    private void beginModification()
    
        throws CoreException
        
    {
        synchronized( this.lock )
        {
            while( this.isBeingModified )
            {
                if( this.modifierThread == Thread.currentThread() )
                {
                    final String msg = Resources.illegalModificationMsg;
                    final IStatus st = FacetCorePlugin.createErrorStatus( msg );
                    
                    throw new CoreException( st );
                }
                
                try
                {
                    this.lock.wait();
                }
                catch( InterruptedException e ) {}
            }
            
            this.isBeingModified = true;
            this.modifierThread = Thread.currentThread();
        }
    }
    
    private void endModification()
    {
        synchronized( this.lock )
        {
            this.isBeingModified = false;
            this.modifierThread = null;
            this.lock.notifyAll();
        }
    }
    
    private EventHandler.Type getPreEventHandlerType( final Action.Type t )
    {
        if( t == Action.Type.INSTALL )
        {
            return EventHandler.Type.PRE_INSTALL;
        }
        else if( t == Action.Type.UNINSTALL )
        {
            return EventHandler.Type.PRE_UNINSTALL;
        }
        else if( t == Action.Type.VERSION_CHANGE )
        {
            return EventHandler.Type.PRE_VERSION_CHANGE;
        }
        else
        {
            throw new IllegalStateException();
        }
    }
    
    private EventHandler.Type getPostEventHandlerType( final Action.Type t )
    {
        if( t == Action.Type.INSTALL )
        {
            return EventHandler.Type.POST_INSTALL;
        }
        else if( t == Action.Type.UNINSTALL )
        {
            return EventHandler.Type.POST_UNINSTALL;
        }
        else if( t == Action.Type.VERSION_CHANGE )
        {
            return EventHandler.Type.POST_VERSION_CHANGE;
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    private void callEventHandlers( final IProjectFacetVersion fv,
                                    final EventHandler.Type type,
                                    final Object config,
                                    final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        final ProjectFacet f = (ProjectFacet) fv.getProjectFacet();
        final List handlers = f.getEventHandlers( fv, type );
        
        if( monitor != null )
        {
            monitor.beginTask( "", handlers.size() ); //$NON-NLS-1$
        }
        
        try
        {
            for( Iterator itr = handlers.iterator(); itr.hasNext(); )
            {
                final EventHandler h = (EventHandler) itr.next();
                IDelegate delegate = null;
                
                try
                {
                    delegate = h.getDelegate();
                }
                catch( CoreException e )
                {
                    FacetCorePlugin.log( e.getStatus() );
                }
                
                if( delegate != null )
                {
                    callDelegate( fv, delegate, config, type,
                                  submon( monitor, 1 ) );
                }
                
                if( monitor != null )
                {
                    monitor.worked( 1 );
                }
            }
        }
        finally
        {
            if( monitor != null )
            {
                monitor.done();
            }
        }
    }
    
    private void callDelegate( final IProjectFacetVersion fv,
                               final IDelegate delegate,
                               final Object config,
                               final Object context,
                               final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        final String tracingDelegateCallsStr
            = Platform.getDebugOption( TRACING_DELEGATE_CALLS );
        
        final boolean tracingDelegateCalls 
            = tracingDelegateCallsStr == null ? false 
              : tracingDelegateCallsStr.equals( "true" );  //$NON-NLS-1$
        
        long timeStarted = -1;
        
        if( tracingDelegateCalls )
        {
            final String msg
                = Resources.bind( Resources.tracingDelegateStarting,
                                  fv.getProjectFacet().getId(),
                                  fv.getVersionString(), context.toString(),
                                  delegate.getClass().getName() );
            
            System.out.println( msg );
            
            timeStarted = System.currentTimeMillis();
        }
        
        try
        {
            delegate.execute( this.project, fv, config, monitor ); 
        }
        catch( Exception e )
        {
            final String msg;
            
            if( context == Action.Type.INSTALL ||
                context == EventHandler.Type.PRE_INSTALL ||
                context == EventHandler.Type.POST_INSTALL )
            {
                msg = NLS.bind( Resources.failedOnInstall, fv );
            }
            else if( context == Action.Type.UNINSTALL ||
                     context == EventHandler.Type.PRE_UNINSTALL ||
                     context == EventHandler.Type.POST_UNINSTALL )
            {
                msg = NLS.bind( Resources.failedOnUninstall, fv );
            }
            else if( context == Action.Type.VERSION_CHANGE ||
                     context == EventHandler.Type.PRE_VERSION_CHANGE ||
                     context == EventHandler.Type.POST_VERSION_CHANGE )
            {
                msg = NLS.bind( Resources.failedOnVersionChange, 
                                fv.getProjectFacet().getLabel(), 
                                fv.getVersionString() );
            }
            else if( context == EventHandler.Type.RUNTIME_CHANGED )
            {
                msg = NLS.bind( Resources.failedOnRuntimeChanged, fv );
            }
            else
            {
                throw new IllegalStateException( context.toString() );
            }
            
            final IStatus status
                = new Status( IStatus.ERROR, FacetCorePlugin.PLUGIN_ID, 0, 
                              msg, e );

            throw new CoreException( status ); 
        }
        
        if( tracingDelegateCalls )
        {
            final long duration = System.currentTimeMillis() - timeStarted;
            
            final String msg 
                = NLS.bind( Resources.tracingDelegateFinished, 
                            String.valueOf( duration ) );
            
            System.out.println( msg );
        }
    }
    
    private void apply( final Action action )
    {
        final Action.Type type = action.getType();
        final IProjectFacetVersion fv = action.getProjectFacetVersion();
        
        if( type == Action.Type.INSTALL )
        {
            this.facets.add( fv );
        }
        else if( type == Action.Type.UNINSTALL )
        {
            this.facets.remove( fv );
        }
        else if( type == Action.Type.VERSION_CHANGE )
        {
            for( Iterator itr = this.facets.iterator(); itr.hasNext(); )
            {
                final IProjectFacetVersion x 
                    = (IProjectFacetVersion) itr.next();
                
                if( x.getProjectFacet() == fv.getProjectFacet() )
                {
                    itr.remove();
                    break;
                }
            }
            
            this.facets.add( fv );
        }
    }
    
    private void save()
    
        throws CoreException
        
    {
        final StringWriter w = new StringWriter();
        final PrintWriter out = new PrintWriter( w );
        
        final String nl = System.getProperty( "line.separator" ); //$NON-NLS-1$
        
        out.print( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" ); //$NON-NLS-1$
        out.print( nl );
        out.print( "<faceted-project>" ); //$NON-NLS-1$
        out.print( nl );
        
        if( this.runtimeName != null )
        {
            out.print( "  <runtime name=\"" ); //$NON-NLS-1$
            out.print( this.runtimeName );
            out.print( "\"/>" ); //$NON-NLS-1$
            out.print( nl );
        }
        
        for( Iterator itr = this.fixed.iterator(); itr.hasNext(); )
        {
            final IProjectFacet f = (IProjectFacet) itr.next();
            
            out.print( "  <fixed facet=\"" ); //$NON-NLS-1$
            out.print( f.getId() );
            out.print( "\"/>" ); //$NON-NLS-1$
            out.print( nl );
        }
        
        for( Iterator itr = this.facets.iterator(); itr.hasNext(); )
        {
            final IProjectFacetVersion fv
                = (IProjectFacetVersion) itr.next();
            
            out.print( "  <installed facet=\"" ); //$NON-NLS-1$
            out.print( fv.getProjectFacet().getId() );
            out.print( "\" version=\"" ); //$NON-NLS-1$
            out.print( fv.getVersionString() );
            out.print( "\"/>" ); //$NON-NLS-1$
            out.print( nl );
        }
        
        out.print( "</faceted-project>" ); //$NON-NLS-1$
        out.print( nl );
        
        final InputStream in 
            = new ByteArrayInputStream( w.getBuffer().toString().getBytes() );
        
        if( this.f.exists() )
        {
            this.f.setContents( in, true, false, null );
        }
        else
        {
            final IFolder parent = (IFolder) this.f.getParent();
            
            if( ! parent.exists() )
            {
                parent.create( true, true, null );
            }
            
            this.f.create( in, true, null );
        }
        
        this.fLastModified = this.f.getLocation().toFile().lastModified();
    }

    public void refresh()
    
        throws CoreException
        
    {
        synchronized( this.lock )
        {
            if( this.f.exists() && 
                this.f.getLocation().toFile().lastModified() == this.fLastModified )
            {
                return;
            }
            
            this.facets.clear();
            this.fixed.clear();
            this.runtimeName = null;
            
            if( ! this.f.exists() )
            {
                this.fLastModified = -1;
                return;
            }
            
            this.fLastModified = this.f.getLocation().toFile().lastModified();
            
            final Element root = parse( this.f.getLocation().toFile() );
            final Element[] elements = children( root );
            
            for( int i = 0; i < elements.length; i++ )
            {
                final Element e = elements[ i ];
                final String name = e.getNodeName();
                
                if( name.equals( EL_RUNTIME ) )
                {
                    this.runtimeName = e.getAttribute( ATTR_NAME );
                }
                else if( name.equals( EL_FIXED ) )
                {
                    final String id = e.getAttribute( ATTR_FACET );
                    
                    if( ! ProjectFacetsManager.isProjectFacetDefined( id ) )
                    {
                        final String msg
                            = NLS.bind( Resources.facetNotDefined, id );
                        
                        final IStatus st = FacetCorePlugin.createErrorStatus( msg );
                        
                        throw new CoreException( st );
                    }
                    
                    final IProjectFacet f
                        = ProjectFacetsManager.getProjectFacet( id );
                    
                    this.fixed.add( f );
                }
                else if( name.equals( EL_INSTALLED ) )
                {
                    final String id = e.getAttribute( ATTR_FACET );
                    final String version = e.getAttribute( ATTR_VERSION );
                    
                    if( ! ProjectFacetsManager.isProjectFacetDefined( id ) )
                    {
                        final String msg
                            = NLS.bind( Resources.facetNotDefined, id );
                        
                        final IStatus st = FacetCorePlugin.createErrorStatus( msg );
                        
                        throw new CoreException( st );
                    }
                    
                    final IProjectFacet f
                        = ProjectFacetsManager.getProjectFacet( id );
                    
                    if( ! f.hasVersion( version ) )
                    {
                        final String msg
                            = NLS.bind( Resources.facetVersionNotDefined, id,
                                        version );
                        
                        final IStatus st = FacetCorePlugin.createErrorStatus( msg );
                        
                        throw new CoreException( st );
                    }
                    
                    final IProjectFacetVersion fv = f.getVersion( version );
                    
                    this.facets.add( fv );
                }
            }
        }
    }
    
    private static Element parse( final File f )
    {
        final DocumentBuilder docbuilder;
        
        try
        {
            final DocumentBuilderFactory factory 
                = DocumentBuilderFactory.newInstance();
            
            factory.setValidating( false );
            
            docbuilder = factory.newDocumentBuilder();
            
            docbuilder.setEntityResolver
            (
                new EntityResolver()
                {
                    public InputSource resolveEntity( final String publicID, 
                                                      final String systemID )
                    {
                        return new InputSource( new StringReader( "" ) ); //$NON-NLS-1$
                    }
                }
            );
        }
        catch( ParserConfigurationException e )
        {
            throw new RuntimeException( e );
        }

        InputStream in = null;

        try
        {
            in = new BufferedInputStream( new FileInputStream( f ) );
            return docbuilder.parse( in ).getDocumentElement();
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            if( in != null )
            {
                try
                {
                    in.close();
                }
                catch( IOException e ) {}
            }
        }
    }
    
    private Element[] children( final Element element )
    {
        final List list = new ArrayList();
        final NodeList nl = element.getChildNodes();
        
        for( int i = 0, n = nl.getLength(); i < n; i++ )
        {
            final Node node = nl.item( i );
            
            if( node.getNodeType() == Node.ELEMENT_NODE )
            {
                list.add( node );
            }
        }
        
        return (Element[]) list.toArray( new Element[ list.size() ] );
    }
    
    private static IProgressMonitor submon( final IProgressMonitor parent,
                                            final int ticks )
    {
        return ( parent == null ? null : new SubProgressMonitor( parent, ticks ) );
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String failedOnInstall;
        public static String failedOnUninstall;
        public static String failedOnVersionChange;
        public static String failedOnRuntimeChanged;
        public static String facetNotDefined;
        public static String facetVersionNotDefined;
        public static String facetNotSupported;
        public static String illegalModificationMsg;
        public static String tracingDelegateStarting;
        public static String tracingDelegateFinished;
        
        static
        {
            initializeMessages( FacetedProject.class.getName(), 
                                Resources.class );
        }
        
        public static final String bind( final String msg,
                                         final String arg1,
                                         final String arg2,
                                         final String arg3,
                                         final String arg4 )
        {
            return NLS.bind( msg, new Object[] { arg1, arg2, arg3, arg4 } );
        }
    }

}
