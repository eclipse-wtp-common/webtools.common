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

package org.eclipse.wst.common.project.facet.ui.internal;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class RuntimesPanel

    extends Composite

{
    private final Label runtimesLabel;
    private final TreeViewer runtimes;
    private final Button filterButton;
    private final Button bindButton;
    //private final Button newButton;
    private final FacetsSelectionPanel facetsSelectionPanel;
    private final Map facetFilters;
    private final Set filters;
    private IRuntime boundRuntime;
    private final Set listeners;
    
    public interface IFilter
    {
        boolean check( IRuntime runtime );
    }

    public RuntimesPanel( final Composite parent,
                          final int style,
                          final FacetsSelectionPanel facetsSelectionPanel )
    {
        super( parent, style );

        this.facetsSelectionPanel = facetsSelectionPanel;
        this.facetFilters = new HashMap();
        this.filters = new HashSet();
        this.listeners = new HashSet();

        // Layout the panel.
        
        final GridLayout layout = new GridLayout( 1, false );
        layout.marginHeight = 0;
        layout.marginWidth = 0;

        setLayout( layout );

        this.runtimesLabel = new Label( this, SWT.NONE );
        this.runtimesLabel.setText( "Runtimes:" );
        this.runtimesLabel.setLayoutData( gdhfill() );
        
        this.runtimes = new TreeViewer( this, SWT.BORDER );
        this.runtimes.getTree().setLayoutData( gdfill() );
        this.runtimes.setContentProvider( new ContentProvider() );
        this.runtimes.setLabelProvider( new LabelProvider() );
        this.runtimes.setSorter( new Sorter() );
        this.runtimes.setInput( new Object() );
        
        this.runtimes.addSelectionChangedListener
        (
            new ISelectionChangedListener()
            {
                public void selectionChanged( final SelectionChangedEvent e )
                {
                    updateButtons();
                }
            }
        );
        
        final Composite buttons = new Composite( this, SWT.NONE );
        buttons.setLayoutData( halign( new GridData(), SWT.RIGHT ) );
        
        final GridLayout buttonsLayout = new GridLayout( 3, false );
        buttonsLayout.marginHeight = 0;
        buttonsLayout.marginWidth = 0;
        
        buttons.setLayout( buttonsLayout );
        
        this.filterButton = new Button( buttons, SWT.PUSH );
        this.filterButton.setText( "Add Filter" );
        this.filterButton.setLayoutData( whint( new GridData(), 80 ) );
        
        this.filterButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    handleAddRemoveFilter();
                }
            }
        );

        this.bindButton = new Button( buttons, SWT.PUSH );
        this.bindButton.setText( "Bind" );
        this.bindButton.setLayoutData( whint( new GridData(), 60 ) );

        this.bindButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    handleBindUnbindRuntime();
                }
            }
        );
        
        //this.newButton = new Button( buttons, SWT.PUSH );
        //this.newButton.setText( "New" );
        //this.newButton.setLayoutData( whint( new GridData(), 60 ) );
        
        updateButtons();
    }
    
    public IRuntime getRuntime()
    {
        return this.boundRuntime;
    }
    
    public void setRuntime( final IRuntime runtime )
    {
        final IRuntime old = this.boundRuntime;
        
        if( old != null )
        {
            removeProjectFacetsFilter( old );
            this.boundRuntime = null;
            this.runtimes.update( old, null );
        }
        
        this.boundRuntime = runtime;

        if( runtime != null )
        {
            addProjectFacetsFilter( runtime );
            this.runtimes.update( runtime, null );
        }
        
        notifyRuntimeListeners();
        updateButtons();
    }
    
    public void addRuntimeListener( final Listener listener )
    {
        this.listeners.add( listener );
    }
    
    public void removeRuntimeListener( final Listener listener )
    {
        this.listeners.remove( listener );
    }
    
    private void notifyRuntimeListeners()
    {
        for( Iterator itr = this.listeners.iterator(); itr.hasNext(); )
        {
            ( (Listener) itr.next() ).handleEvent( null );
        }
    }
    
    public void addFilter( final IFilter filter )
    {
        this.filters.add( filter );
        this.runtimes.refresh();
    }
    
    public void removeFilter( final IFilter filter )
    {
        this.filters.remove( filter );
        this.runtimes.refresh();
    }
    
    public void refresh()
    {
        this.runtimes.refresh();
    }
    
    private void addProjectFacetsFilter( final IRuntime r )
    {
        if( ! this.facetFilters.containsKey( r ) )
        {
            final FacetsSelectionPanel.IFilter filter
                = new SupportedFacetsFilter( r );
            
            this.facetFilters.put( r, filter );
            this.facetsSelectionPanel.addFilter( filter );
        }
    }
    
    private void removeProjectFacetsFilter( final IRuntime r )
    {
        final FacetsSelectionPanel.IFilter filter
            = (FacetsSelectionPanel.IFilter) this.facetFilters.remove( r );
        
        this.facetsSelectionPanel.removeFilter( filter );
    }
    
    private void handleAddRemoveFilter()
    {
        final IStructuredSelection ssel 
            = (IStructuredSelection) this.runtimes.getSelection();
        
        final IRuntime runtime = (IRuntime) ssel.getFirstElement();
        
        if( this.facetFilters.containsKey( runtime ) )
        {
            removeProjectFacetsFilter( runtime );
        }
        else
        {
            addProjectFacetsFilter( runtime );
        }
        
        this.runtimes.update( runtime, null );
        updateButtons();
    }
    
    private void handleBindUnbindRuntime()
    {
        final IStructuredSelection ssel 
            = (IStructuredSelection) this.runtimes.getSelection();
        
        final IRuntime runtime = (IRuntime) ssel.getFirstElement();
        
        if( this.boundRuntime.equals( runtime ) )
        {
            setRuntime( null );
        }
        else
        {
            setRuntime( runtime );
        }
    }
    
    private void updateButtons()
    {
        final IStructuredSelection ssel
            = (IStructuredSelection) this.runtimes.getSelection();
        
        final Object sel = ssel.getFirstElement();
        
        if( sel == null || sel instanceof IRuntimeComponent )
        {
            this.filterButton.setText( "Add Filter" );
            this.filterButton.setEnabled( false );
            this.bindButton.setText( "Bind" );
            this.bindButton.setEnabled( false );
        }
        else
        {
            final IRuntime runtime = (IRuntime) sel;
            
            if( this.facetFilters.containsKey( runtime ) )
            {
                this.filterButton.setText( "Remove Filter" );
            }
            else
            {
                this.filterButton.setText( "Add Filter" );
            }
            
            if( this.boundRuntime.equals( runtime ) )
            {
                this.bindButton.setText( "Unbind" );
                this.filterButton.setEnabled( false );
            }
            else
            {
                this.bindButton.setText( "Bind" );
                this.filterButton.setEnabled( true );
            }
            
            this.bindButton.setEnabled( true );
        }
    }
    
    private final class ContentProvider

        implements ITreeContentProvider
    
    {
        public Object[] getElements( final Object element )
        {
            final Set res = new HashSet();
            
            for( Iterator itr1 = RuntimeManager.getRuntimes().iterator();
                 itr1.hasNext(); )
            {
                final IRuntime r = (IRuntime) itr1.next();
                boolean ok = true;
                
                for( Iterator itr2 = RuntimesPanel.this.filters.iterator();
                     itr2.hasNext(); )
                {
                    if( ! ( (IFilter) itr2.next() ).check( r ) )
                    {
                        ok = false;
                        break;
                    }
                }
                
                if( ok )
                {
                    res.add( r );
                }
            }
            
            return res.toArray();
        }
    
        public Object[] getChildren( final Object parent )
        {
            if( parent instanceof IRuntime )
            {
                return ( (IRuntime) parent ).getRuntimeComponents().toArray();
            }
            else
            {
                return new Object[ 0 ];
            }
        }
    
        public Object getParent( final Object element )
        {
            return null;
        }
    
        public boolean hasChildren( final Object element )
        {
            return ( element instanceof IRuntime );
        }
    
        public void dispose() { }
    
        public void inputChanged( final Viewer viewer,
                                  final Object oldObject,
                                  final Object newObject ) {}
    }
    
    private final class LabelProvider

        implements ILabelProvider
    
    {
        private ImageRegistry imageRegistry = new ImageRegistry();
        
        public String getText( final Object element )
        {
            if( element instanceof IRuntime )
            {
                final StringBuffer label = new StringBuffer();
                label.append( ( (IRuntime) element ).getName() );
                
                if( RuntimesPanel.this.facetFilters.containsKey( element ) )
                {
                    label.append( " <f>" );
                }
                
                if( RuntimesPanel.this.boundRuntime != null &&
                    RuntimesPanel.this.boundRuntime.equals( element ) )
                {
                    label.append( " <b>" );
                }
                
                return label.toString();
            }
            else
            {
                final IRuntimeComponent comp = (IRuntimeComponent) element;
                
                final IRuntimeComponentLabelProvider provider
                    = (IRuntimeComponentLabelProvider) comp.getAdapter( IRuntimeComponentLabelProvider.class );
                
                if( provider == null )
                {
                    final StringBuffer label = new StringBuffer();
                    label.append( comp.getRuntimeComponentType().getId() );
                    label.append( ' ' );
                    label.append( comp.getRuntimeComponentVersion().getVersionString() );
                    
                    return label.toString();
                }
                else
                {
                    return provider.getLabel();
                }
            }
        }

        public Image getImage( final Object element )
        {
            String plugin = null;
            String iconPath = null;

            if( element instanceof IRuntime )
            {
                final IRuntime r = (IRuntime) element;
                
                final IRuntimeComponent rc 
                    = (IRuntimeComponent) r.getRuntimeComponents().get( 0 );
                
                final IRuntimeComponentType rct = rc.getRuntimeComponentType();
                
                plugin = rct.getPluginId();
                iconPath = rct.getIconPath();
            }
            else
            {
                final IRuntimeComponent rc = (IRuntimeComponent) element;
                final IRuntimeComponentType rct = rc.getRuntimeComponentType();
                
                plugin = rct.getPluginId();
                iconPath = rct.getIconPath();
            }

            if( iconPath == null )
            {
                plugin = FacetUiPlugin.PLUGIN_ID;
                iconPath = "images/unknown.gif";
            }

            final String key = plugin + ":" + iconPath;
            Image image = this.imageRegistry.get( key );

            if( image == null )
            {
                final Bundle bundle = Platform.getBundle( plugin );
                final URL url = bundle.getEntry( iconPath );

                this.imageRegistry.put( key, ImageDescriptor.createFromURL( url ) );
                image = this.imageRegistry.get( key );
            }

            return image;
        }
        
        public void dispose()
        {
            this.imageRegistry.dispose();
        }

        public boolean isLabelProperty( final Object element, 
                                        final String property )
        {
            return false;
        }

        public void addListener( final ILabelProviderListener listener ) {}
        public void removeListener( final ILabelProviderListener listener ) {}
    }

    private static final class Sorter

        extends ViewerSorter
    
    {
        public int compare( final Viewer viewer,
                            final Object a,
                            final Object b )
        {
            if( a instanceof IRuntime )
            {
                final IRuntime r1 = (IRuntime) a;
                final IRuntime r2 = (IRuntime) b;
                
                return r1.getName().compareToIgnoreCase( r2.getName() );
            }
            else
            {
                // Don't sort the runtime components. Their order is 
                // significant.
                
                return 0;
            }
        }
    }
    
    private static final class SupportedFacetsFilter
    
        implements FacetsSelectionPanel.IFilter
        
    {
        private final IRuntime runtime;
        
        public SupportedFacetsFilter( final IRuntime runtime )
        {
            this.runtime = runtime;
        }
        
        public boolean check( final IProjectFacetVersion fv )
        {
            return this.runtime.supports( fv );
        }
    }
    
    private static final GridData gdfill()
    {
        return new GridData( SWT.FILL, SWT.FILL, true, true );
    }
    
    private static final GridData gdhfill()
    {
        return new GridData( GridData.FILL_HORIZONTAL );
    }
    
    private static final GridData whint( final GridData gd,
                                         final int width )
    {
        gd.widthHint = width;
        return gd;
    }

    private static final GridData halign( final GridData gd,
                                          final int alignment )
    {
        gd.horizontalAlignment = alignment;
        return gd;
    }
    
}
