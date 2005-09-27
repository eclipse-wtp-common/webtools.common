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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetedProject

    implements IFacetedProject
    
{
    private final IProject project;
    private final HashSet facets;
    private final Set facetsReadOnly;
    private final HashSet fixed;
    private final Set fixedReadOnly;
    private IRuntime runtime;
    private final File f;
    
    public FacetedProject( final IProject project )
    {
        this.project = project;
        this.facets = new HashSet();
        this.facetsReadOnly = Collections.unmodifiableSet( this.facets );
        this.fixed = new HashSet();
        this.fixedReadOnly = Collections.unmodifiableSet( this.fixed );
        this.runtime = null;
        this.f = new File( project.getLocation().toFile(), ".facets" );
        
        open();
    }
    
    public Set getProjectFacets()
    {
        return this.facetsReadOnly;
    }
    
    public boolean hasProjectFacet( final IProjectFacet f )
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
    
    public boolean hasProjectFacet( final IProjectFacetVersion fv )
    {
        return this.facets.contains( fv );
    }
    
    public IProjectFacetVersion getInstalledVersion( final IProjectFacet f )
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
        if( monitor != null )
        {
            monitor.beginTask( "", actions.size() + 1 );
        }
        
        try
        {
            final IStatus st 
                = ProjectFacetsManager.get().check( this.facets, actions );
            
            if( ! st.isOK() )
            {
                throw new CoreException( st );
            }
            
            final List copy = new ArrayList( actions );
            ProjectFacetsManager.get().sort( this.facets, copy );
            
            for( Iterator itr = copy.iterator(); itr.hasNext(); )
            {
                //if( monitor != null && monitor.isCanceled() &&
                //    ProjectFacetsManager.get().check( this.facets ).isEmpty() )
                //{
                //    throw new OperationCanceledException();
                //}
                
                final Action action = (Action) itr.next();
                final Action.Type type = action.getType();
                
                final ProjectFacetVersion fv 
                    = (ProjectFacetVersion) action.getProjectFacetVersion();
                
                final IDelegate delegate 
                    = fv.getDelegate( IDelegate.Type.get( type ) );
                
                if( delegate == null )
                {
                    if( monitor != null )
                    {
                        monitor.worked( 1 );
                    }
                }
                else
                {
                    final SubProgressMonitor submonitor
                        = monitor == null 
                          ? null : new SubProgressMonitor( monitor, 1 );
                    
                    callDelegate( this.project, fv, action.getConfig(),
                                  IDelegate.Type.get( action.getType() ),
                                  delegate, submonitor );
                }

                apply( this.facets, action );
                save();
            }
            
            if( monitor != null )
            {
                monitor.worked( 1 );
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
        return this.fixedReadOnly;
    }
    
    public void setFixedProjectFacets( final Set facets )
    
        throws CoreException
        
    {
        this.fixed.clear();
        this.fixed.addAll( facets );
        
        save();
    }
    
    public IRuntime getRuntime()
    {
        return this.runtime;
    }
    
    public void setRuntime( final IRuntime runtime,
                            final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        if( monitor != null )
        {
            monitor.beginTask( "", this.facets.size() );
        }
        
        try
        {
            if( this.runtime != runtime )
            {
                this.runtime = runtime;
                save();
    
                for( Iterator itr = this.facets.iterator(); itr.hasNext(); )
                {
                    final ProjectFacetVersion fv
                        = (ProjectFacetVersion) itr.next();
                    
                    final IDelegate delegate
                        = fv.getDelegate( IDelegate.Type.RUNTIME_CHANGED );
                    
                    if( delegate == null )
                    {
                        if( monitor != null )
                        {
                            monitor.worked( 1 );
                        }
                    }
                    else
                    {
                        final SubProgressMonitor submonitor
                            = monitor == null 
                              ? null : new SubProgressMonitor( monitor, 1 );
                        
                        callDelegate( this.project, fv, null,
                                      IDelegate.Type.RUNTIME_CHANGED, delegate,
                                      submonitor );
                    }
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
    
    private static void callDelegate( final IProject project,
                                      final IProjectFacetVersion fv,
                                      final Object config,
                                      final IDelegate.Type type,
                                      final IDelegate delegate,
                                      final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        try
        {
            delegate.execute( project, fv, config, monitor ); 
        }
        catch( Exception e )
        {
            final IStatus cause;
            
            if( e instanceof CoreException )
            {
                cause = ( (CoreException) e ).getStatus();
            }
            else
            {
                cause = new Status( IStatus.ERROR, FacetCorePlugin.PLUGIN_ID,
                                    0, e.getMessage(), e );
            }
            
            final String msg;
            
            if( type == IDelegate.Type.INSTALL )
            {
                msg = NLS.bind( Resources.failedOnInstall, fv );
            }
            else if( type == IDelegate.Type.UNINSTALL )
            {
                msg = NLS.bind( Resources.failedOnUninstall, fv );
            }
            else if( type == IDelegate.Type.VERSION_CHANGE )
            {
                msg = NLS.bind( Resources.failedOnVersionChange, 
                                fv.getProjectFacet().getLabel(), 
                                fv.getVersionString() );
            }
            else if( type == IDelegate.Type.RUNTIME_CHANGED )
            {
                msg = NLS.bind( Resources.failedOnRuntimeChanged, fv );
            }
            else
            {
                msg = "Unknown delegate type!";
            }
            
            final IStatus status 
                = new MultiStatus( FacetCorePlugin.PLUGIN_ID, 0, 
                                   new IStatus[] { cause }, msg,  null );

            throw new CoreException( status ); 
        }
        
    }
    
    private static void apply( final Set facets,
                               final Action action )
    {
        final Action.Type type = action.getType();
        final IProjectFacetVersion fv = action.getProjectFacetVersion();
        
        if( type == Action.Type.INSTALL )
        {
            facets.add( fv );
        }
        else if( type == Action.Type.UNINSTALL )
        {
            facets.remove( fv );
        }
        else if( type == Action.Type.VERSION_CHANGE )
        {
            for( Iterator itr = facets.iterator(); itr.hasNext(); )
            {
                final IProjectFacetVersion x 
                    = (IProjectFacetVersion) itr.next();
                
                if( x.getProjectFacet() == fv.getProjectFacet() )
                {
                    itr.remove();
                    break;
                }
            }
            
            facets.add( fv );
        }
    }
    
    private void open()
    {
        if( ! this.f.exists() )
        {
            return;
        }
        
        BufferedReader in = null;
        
        try
        {
            in = new BufferedReader( new FileReader( this.f ) );
            
            for( String line = in.readLine(); line != null; 
                 line = in.readLine() )
            {
                if( line.startsWith( "r:" ) )
                {
                    final String name = line.substring( 2 );
                    this.runtime = RuntimeManager.get().getRuntime( name );
                }
                else if( line.startsWith( "f:" ) )
                {
                    final IProjectFacet f
                        = ProjectFacetsManager.get().getProjectFacet( line.substring( 2 ) );
                    
                    this.fixed.add( f );
                }
                else
                {
                    final int colon = line.indexOf( ':' );
                    
                    if( colon == -1 )
                    {
                        // TODO: Handle this.
                        continue;
                    }
                    
                    final String name = line.substring( 0, colon );
                    final String version = line.substring( colon + 1 );
                    
                    // TODO: Handle the case where the facet is not available.
                    
                    final IProjectFacetVersion fv
                        = ProjectFacetsManager.get().getProjectFacet( name ).getVersion( version );
    
                    this.facets.add( fv );
                }
            }
        }
        catch( IOException e )
        {
            // TODO: Handle this better.
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
    
    private void save()
    {
        PrintWriter out = null;
        
        try
        {
            final Writer w = new BufferedWriter( new FileWriter( this.f ) );
            out = new PrintWriter( w );
            
            if( this.runtime != null )
            {
                out.print( "r:" );
                out.println( this.runtime.getName() );
            }
            
            for( Iterator itr = this.fixed.iterator(); itr.hasNext(); )
            {
                final IProjectFacet f = (IProjectFacet) itr.next();
                
                out.print( "f:" );
                out.println( f.getId() );
            }
            
            for( Iterator itr = this.facets.iterator(); itr.hasNext(); )
            {
                final IProjectFacetVersion fv
                    = (IProjectFacetVersion) itr.next();
                
                out.print( fv.getProjectFacet().getId() );
                out.print( ':' );
                out.println( fv.getVersionString() );
            }
        }
        catch( IOException e )
        {
            // TODO: Handle this better.
            throw new RuntimeException( e );
        }
        finally
        {
            if( out != null )
            {
                out.close();
            }
        }
        
        try
        {
            this.project.refreshLocal( 1, null );
        }
        catch( CoreException e )
        {
            // TODO: Report this.
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String failedOnInstall;
        public static String failedOnUninstall;
        public static String failedOnVersionChange;
        public static String failedOnRuntimeChanged;
        
        static
        {
            initializeMessages( Constraint.class.getName(), 
                                Resources.class );
        }
    }

}
