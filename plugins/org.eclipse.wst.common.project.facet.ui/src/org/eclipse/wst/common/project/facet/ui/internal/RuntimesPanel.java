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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wst.common.project.facet.core.IListener;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.ui.IDecorationsProvider;
import org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class RuntimesPanel

    extends Composite

{
    private final Label runtimesLabel;
    private final CheckboxTableViewer runtimes;
    private final Button makePreferredButton;
    private final Label runtimeComponentsLabel;
    private final TableViewer runtimeComponents;
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
        this.runtimesLabel.setText( Resources.runtimesLabel );
        this.runtimesLabel.setLayoutData( gdhfill() );
        
        this.runtimes = CheckboxTableViewer.newCheckList( this, SWT.BORDER );
        this.runtimes.getTable().setLayoutData( gdfill() );
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
                    handleRuntimeSelectionChanged();
                }
            }
        );
        
        this.runtimes.addCheckStateListener
        (
            new ICheckStateListener()
            {
                public void checkStateChanged( final CheckStateChangedEvent e )
                {
                    handleCheckStateChanged( e );
                }
            }
        );
        
        this.makePreferredButton = new Button( this, SWT.PUSH );
        this.makePreferredButton.setText( Resources.makePreferredLabel );
        GridData gd = halign( new GridData(), GridData.END );
        gd = whint( gd, getPreferredWidth( this.makePreferredButton ) + 15 );
        this.makePreferredButton.setLayoutData( gd );
        
        this.makePreferredButton.setEnabled( false );
        
        this.makePreferredButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    handleMakePreferred();
                }
            }
        );
        
        this.runtimeComponentsLabel = new Label( this, SWT.NONE );
        this.runtimeComponentsLabel.setText( Resources.runtimeCompositionLabel );
        this.runtimeComponentsLabel.setLayoutData( gdhfill() );
        
        this.runtimeComponents = new TableViewer( this, SWT.BORDER );
        this.runtimeComponents.getTable().setLayoutData( hhint( gdhfill(), 50 ) );
        this.runtimeComponents.setContentProvider( new RuntimeComponentsContentProvider() );
        this.runtimeComponents.setLabelProvider( new RuntimeComponentsLabelProvider() );
        this.runtimeComponents.getTable().setBackground( new Color( null, 255, 255, 206 ) );
        
        final IListener listener = new IListener()
        {
            public void handle()
            {
                refresh();
            }
        };
        
        RuntimeManager.addRuntimeListener( listener );
        
        addDisposeListener
        ( 
            new DisposeListener()
            {
                public void widgetDisposed( final DisposeEvent e )
                {
                    RuntimeManager.removeRuntimeListener( listener );
                }
            }
        );
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
        refresh();
    }
    
    public void removeFilter( final IFilter filter )
    {
        this.filters.remove( filter );
        refresh();
    }
    
    private boolean isFilteredOut( final IRuntime r )
    {
        for( Iterator itr = RuntimesPanel.this.filters.iterator(); 
             itr.hasNext(); )
        {
            final IFilter filter = (IFilter) itr.next();
            
            if( ! filter.check( r ) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    public void refresh()
    {
        this.runtimes.refresh();
        
        for( Iterator itr = RuntimeManager.getRuntimes().iterator(); 
             itr.hasNext(); )
        {
            final IRuntime r = (IRuntime) itr.next();
            
            if( isFilteredOut( r ) )
            {
                this.runtimes.setGrayed( r, true );
            }
            else
            {
                this.runtimes.setGrayed( r, false );
            }
        }
    }
    
    private void addProjectFacetsFilter( final IRuntime r )
    {
        if( ! this.facetFilters.containsKey( r ) )
        {
            final FacetsSelectionPanel.IFilter filter
                = new SupportedFacetsFilter( r );
            
            this.facetFilters.put( r, filter );
            this.facetsSelectionPanel.addFilter( filter );
            
            if( ! this.runtimes.getChecked( r ) )
            {
                this.runtimes.setChecked( r, true );
            }
        }
    }
    
    private void removeProjectFacetsFilter( final IRuntime r )
    {
        final FacetsSelectionPanel.IFilter filter
            = (FacetsSelectionPanel.IFilter) this.facetFilters.remove( r );
        
        this.facetsSelectionPanel.removeFilter( filter );
        
        if( this.runtimes.getChecked( r ) )
        {
            this.runtimes.setChecked( r, false );
        }
    }
    
    private void handleCheckStateChanged( final CheckStateChangedEvent e )
    {
        final IRuntime runtime = (IRuntime) e.getElement();
        
        if( isFilteredOut( runtime ) && e.getChecked() )
        {
            this.runtimes.setChecked( runtime, false );
            return;
        }
        
        if( this.facetFilters.containsKey( runtime ) )
        {
            removeProjectFacetsFilter( runtime );

            if( runtime == this.boundRuntime )
            {
                if( this.facetFilters.isEmpty() )
                {
                    this.boundRuntime = null;
                }
                else
                {
                    final IRuntime r 
                        = (IRuntime) this.facetFilters.keySet().iterator().next();
                    
                    this.boundRuntime = r;
                    this.runtimes.update( r, null );
                    
                    final IRuntime selection = getSelection();
                    
                    if( selection != null && selection.equals( r ) )
                    {
                        this.makePreferredButton.setEnabled( false );
                    }
                }

                this.runtimes.update( runtime, null );
                notifyRuntimeListeners();
            }
        }
        else
        {
            addProjectFacetsFilter( runtime );
            
            if( this.boundRuntime == null )
            {
                this.boundRuntime = runtime;
                this.runtimes.update( runtime, null );
                notifyRuntimeListeners();
            }
        }
    }
    
    private void handleRuntimeSelectionChanged()
    {
        final IRuntime r = getSelection();
        
        if( r != null )
        {
            if( this.runtimeComponents.getInput() == null ||
                ! this.runtimeComponents.getInput().equals( r ) )
            {
                this.runtimeComponents.setInput( r );
            }
            
            if( this.runtimes.getChecked( r ) && this.boundRuntime != null && 
                ! this.boundRuntime.equals( r ) && ! isFilteredOut( r ) )
            {
                this.makePreferredButton.setEnabled( true );
            }
            else
            {
                this.makePreferredButton.setEnabled( false );
            }
        }
    }
    
    private void handleMakePreferred()
    {
        final IRuntime old = this.boundRuntime;
        this.boundRuntime = getSelection();
        
        this.runtimes.refresh( old );
        this.runtimes.refresh( this.boundRuntime );
        
        notifyRuntimeListeners();
    }
    
    private IRuntime getSelection()
    {
        final IStructuredSelection ssel 
            = (IStructuredSelection) this.runtimes.getSelection();
        
        if( ssel.isEmpty() )
        {
            return null;
        }
        else
        {
            return (IRuntime) ssel.getFirstElement();
        }
    }
    
    private final class ContentProvider

        implements IStructuredContentProvider
    
    {
        public Object[] getElements( final Object element )
        {
            return RuntimeManager.getRuntimes().toArray();
        }
    
        public void dispose() { }
    
        public void inputChanged( final Viewer viewer,
                                  final Object oldObject,
                                  final Object newObject ) {}
    }
    
    private final class LabelProvider

        implements ILabelProvider, IFontProvider, IColorProvider
    
    {
        private final Color COLOR_GREY 
            = new Color( null, 160, 160, 164 );
        
        private final ImageRegistry imageRegistry;
        private final Font boldFont;
        
        public LabelProvider()
        {
            this.imageRegistry = new ImageRegistry();
            
            final FontData system 
                = Display.getCurrent().getSystemFont().getFontData()[ 0 ];
        
            final FontData bold 
                = new FontData( system.getName(), system.getHeight(), SWT.BOLD );
            
            this.boldFont = new Font( Display.getCurrent(), bold );
        }
        
        public String getText( final Object element )
        {
            return ( (IRuntime) element ).getName();
        }

        public Image getImage( final Object element )
        {
            final IRuntime r = (IRuntime) element;
            
            final IRuntimeComponent rc 
                = (IRuntimeComponent) r.getRuntimeComponents().get( 0 );
            
            final IRuntimeComponentType rct = rc.getRuntimeComponentType();
            
            Image image = this.imageRegistry.get( rct.getId() );
            
            if( image == null )
            {
                final IDecorationsProvider decprov
                    = (IDecorationsProvider) rct.getAdapter( IDecorationsProvider.class );
                
                this.imageRegistry.put( rct.getId(), decprov.getIcon() );
                image = this.imageRegistry.get( rct.getId() );
            }

            if( isFilteredOut( r ) )
            {
                final String greyedId = rct.getId() + "##greyed##"; //$NON-NLS-1$
                Image greyed = this.imageRegistry.get( greyedId );
                
                if( greyed == null )
                {
                    greyed = new Image( null, image, SWT.IMAGE_GRAY );
                    this.imageRegistry.put( greyedId, greyed );
                }
                
                return greyed;
            }
            else
            {
                return image;
            }
        }
        
        public Font getFont( final Object element )
        {
            if( RuntimesPanel.this.boundRuntime != null &&
                RuntimesPanel.this.boundRuntime.equals( element ) )
            {
                return this.boldFont;
            }
            
            return null;
        }
        
        public Color getForeground( final Object element )
        {
            if( isFilteredOut( (IRuntime) element ) )
            {
                return this.COLOR_GREY;
            }
            else
            {
                return null;
            }
        }

        public Color getBackground( final Object element )
        {
            return null;
        }

        public void dispose()
        {
            this.imageRegistry.dispose();
            this.boldFont.dispose();
        }

        public boolean isLabelProperty( final Object element, 
                                        final String property )
        {
            return false;
        }

        public void addListener( final ILabelProviderListener listener ) {}
        public void removeListener( final ILabelProviderListener listener ) {}
    }

    private final class Sorter

        extends ViewerSorter
    
    {
        public int compare( final Viewer viewer,
                            final Object a,
                            final Object b )
        {
            final IRuntime r1 = (IRuntime) a;
            final boolean r1fo = isFilteredOut( r1 );
            
            final IRuntime r2 = (IRuntime) b;
            final boolean r2fo = isFilteredOut( r2 );
            
            if( r1fo && ! r2fo )
            {
                return 1;
            }
            else if( ! r1fo && r2fo )
            {
                return -1;
            }
            else
            {
                return r1.getName().compareToIgnoreCase( r2.getName() );
            }
        }
    }
    
    private final class RuntimeComponentsContentProvider

        implements IStructuredContentProvider
    
    {
        public Object[] getElements( final Object element )
        {
            final IRuntime r = (IRuntime) element;
            return r.getRuntimeComponents().toArray();
        }
    
        public void dispose() { }
    
        public void inputChanged( final Viewer viewer,
                                  final Object oldObject,
                                  final Object newObject ) {}
    }
    
    private final class RuntimeComponentsLabelProvider

        implements ILabelProvider
    
    {
        private final ImageRegistry imageRegistry = new ImageRegistry();
        
        public String getText( final Object element )
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

        public Image getImage( final Object element )
        {
            final IRuntimeComponent rc = (IRuntimeComponent) element;
            final IRuntimeComponentType rct = rc.getRuntimeComponentType();
            
            Image image = this.imageRegistry.get( rct.getId() );
            
            if( image == null )
            {
                final IDecorationsProvider decprov
                    = (IDecorationsProvider) rct.getAdapter( IDecorationsProvider.class );
                
                this.imageRegistry.put( rct.getId(), decprov.getIcon() );
                image = this.imageRegistry.get( rct.getId() );
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

    private static final GridData hhint( final GridData gd,
                                         final int height )
    {
        gd.heightHint = height;
        return gd;
    }
    
    private static final GridData halign( final GridData gd,
                                          final int alignment )
    {
        gd.horizontalAlignment = alignment;
        return gd;
    }

    private static final int getPreferredWidth( final Control control )
    {
        return control.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x;
    }

    private static final class Resources
    
        extends NLS
        
    {
        public static String runtimesLabel;
        public static String runtimeCompositionLabel;
        public static String makePreferredLabel;
        
        static
        {
            initializeMessages( RuntimesPanel.class.getName(), 
                                Resources.class );
        }
    }
    
}
