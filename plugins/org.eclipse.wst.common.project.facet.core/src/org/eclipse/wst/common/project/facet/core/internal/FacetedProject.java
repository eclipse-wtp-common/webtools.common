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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
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

public final class FacetedProject

    implements IFacetedProject
    
{
    private final IProject project;
    private final HashSet facets;
    private final Set facetsReadOnly;
    private final HashSet fixed;
    private final Set fixedReadOnly;
    private IRuntime runtime;
    private final IFile f;
    
    FacetedProject( final IProject project )
    
        throws CoreException
        
    {
        this.project = project;
        this.facets = new HashSet();
        this.facetsReadOnly = Collections.unmodifiableSet( this.facets );
        this.fixed = new HashSet();
        this.fixedReadOnly = Collections.unmodifiableSet( this.fixed );
        this.runtime = null;
        
        this.f = project.getFile( ".settings/" + FacetCorePlugin.PLUGIN_ID 
                                  + ".xml" );
        
        open();
    }
    
    public IProject getProject()
    {
        return this.project;
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
            
            final IWorkspaceRunnable wr = new IWorkspaceRunnable()
            {
                public void run( final IProgressMonitor monitor ) 
                
                    throws CoreException
                    
                {
                    for( Iterator itr = copy.iterator(); itr.hasNext(); )
                    {
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
                            
                            callDelegate( project, fv, action.getConfig(),
                                          IDelegate.Type.get( type ), delegate,
                                          submonitor );
                        }
        
                        apply( facets, action );
                        save();
                    }
                }
            };
            
            final IWorkspace ws = ResourcesPlugin.getWorkspace();
            ws.run( wr, ws.getRoot(), IWorkspace.AVOID_UPDATE, monitor ); 
            
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
                = new Status( IStatus.ERROR, FacetCorePlugin.PLUGIN_ID, 0, 
                              msg, e );

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
    
    private void save()
    
        throws CoreException
        
    {
        final StringWriter w = new StringWriter();
        final PrintWriter out = new PrintWriter( w );
        
        final String nl = System.getProperty( "line.separator" );
        
        out.print( "<faceted-project>" );
        out.print( nl );
        
        if( this.runtime != null )
        {
            out.print( "  <runtime name=\"" );
            out.print( this.runtime.getName() );
            out.print( "\"/>" );
            out.print( nl );
        }
        
        for( Iterator itr = this.fixed.iterator(); itr.hasNext(); )
        {
            final IProjectFacet f = (IProjectFacet) itr.next();
            
            out.print( "  <fixed facet=\"" );
            out.print( f.getId() );
            out.print( "\"/>" );
            out.print( nl );
        }
        
        for( Iterator itr = this.facets.iterator(); itr.hasNext(); )
        {
            final IProjectFacetVersion fv
                = (IProjectFacetVersion) itr.next();
            
            out.print( "  <installed facet=\"" );
            out.print( fv.getProjectFacet().getId() );
            out.print( "\" version=\"" );
            out.print( fv.getVersionString() );
            out.print( "\"/>" );
            out.print( nl );
        }
        
        out.print( "</faceted-project>" );
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
    }

    private void open()
    
        throws CoreException
        
    {
        if( ! this.f.exists() )
        {
            return;
        }
        
        final Element root = parse( this.f.getLocation().toFile() );
        final Element[] elements = children( root );
        
        for( int i = 0; i < elements.length; i++ )
        {
            final Element e = elements[ i ];
            final String name = e.getNodeName();
            
            if( name.equals( "runtime" ) )
            {
                final String rn = e.getAttribute( "name" );
                this.runtime = RuntimeManager.getRuntime( rn );
            }
            else if( name.equals( "fixed" ) )
            {
                final String id = e.getAttribute( "facet" );
                
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
            else if( name.equals( "installed" ) )
            {
                final String id = e.getAttribute( "facet" );
                final String version = e.getAttribute( "version" );
                
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
                        return new InputSource( new StringReader( "" ) );
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
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String failedOnInstall;
        public static String failedOnUninstall;
        public static String failedOnVersionChange;
        public static String failedOnRuntimeChanged;
        public static String facetNotDefined;
        public static String facetVersionNotDefined;
        
        static
        {
            initializeMessages( FacetedProject.class.getName(), 
                                Resources.class );
        }
    }

}
