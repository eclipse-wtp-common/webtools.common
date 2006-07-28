/******************************************************************************
 * Copyright (c) 2005, 2006 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.ui.IDecorationsProvider;
import org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider;
import org.eclipse.wst.common.project.facet.ui.internal.AbstractDataModel.IDataModelListener;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class RuntimesPanel

    extends Composite

{
    private static final Object NO_RUNTIME_SELECTED_PLACEHOLDER = new Object();
    
    private final ChangeTargetedRuntimesDataModel model;
    private boolean showAllRuntimesSetting;
    private final Label runtimesLabel;
    private final CheckboxTableViewer runtimes;
    private final Button showAllRuntimesCheckbox;
    private final Button makePrimaryButton;
    private final Label runtimeComponentsLabel;
    private final TableViewer runtimeComponents;
    private IRuntime currentPrimaryRuntime;
    private final List listeners;
    private Color colorGray;
    private Color colorManila;
    
    public RuntimesPanel( final Composite parent,
                          final int style,
                          final ChangeTargetedRuntimesDataModel model )
    {
        super( parent, style );
        
        this.listeners = new ArrayList();
        
        addDisposeListener
        ( 
            new DisposeListener()
            {
                public void widgetDisposed( final DisposeEvent e )
                {
                    handleWidgetDisposed();
                }
            }
        );
        
        // Bind to the data model.
        
        this.model = model;
        
        addDataModelListener
        ( 
            ChangeTargetedRuntimesDataModel.EVENT_AVAILABLE_RUNTIMES_CHANGED, 
            new IDataModelListener()
            {
                public void handleEvent()
                {
                    handleAvailableRuntimesChanged();
                }
            }
        );
        
        addDataModelListener
        ( 
            ChangeTargetedRuntimesDataModel.EVENT_TARGETABLE_RUNTIMES_CHANGED, 
            new IDataModelListener()
            {
                public void handleEvent()
                {
                    handleTargetableRuntimesChanged();
                }
            }
        );
        
        addDataModelListener
        ( 
            ChangeTargetedRuntimesDataModel.EVENT_TARGETED_RUNTIMES_CHANGED, 
            new IDataModelListener()
            {
                public void handleEvent()
                {
                    handleTargetedRuntimesChanged();
                }
            }
        );
        
        addDataModelListener
        ( 
            ChangeTargetedRuntimesDataModel.EVENT_PRIMARY_RUNTIME_CHANGED, 
            new IDataModelListener()
            {
                public void handleEvent()
                {
                    handlePrimaryRuntimeChanged();
                }
            }
        );
        
        this.showAllRuntimesSetting = false;

        // Initialize the colors.
        
        this.colorGray = new Color( null, 160, 160, 164 );
        this.colorManila = new Color( null, 255, 255, 206 );

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
        
        this.showAllRuntimesCheckbox = new Button( this, SWT.CHECK );
        this.showAllRuntimesCheckbox.setText( Resources.showAllRuntimes );
        this.showAllRuntimesCheckbox.setSelection( this.showAllRuntimesSetting );
        
        this.showAllRuntimesCheckbox.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    handleShowAllRuntimesSelected();
                }
            }
        );
        
        this.makePrimaryButton = new Button( this, SWT.PUSH );
        this.makePrimaryButton.setText( Resources.makePrimaryLabel );
        GridData gd = halign( new GridData(), GridData.END );
        gd = whint( gd, getPreferredWidth( this.makePrimaryButton ) + 15 );
        this.makePrimaryButton.setLayoutData( gd );
        
        this.makePrimaryButton.setEnabled( false );
        
        this.makePrimaryButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    handleMakePrimarySelected();
                }
            }
        );
        
        this.runtimeComponentsLabel = new Label( this, SWT.NONE );
        this.runtimeComponentsLabel.setText( Resources.runtimeCompositionLabel );
        this.runtimeComponentsLabel.setLayoutData( gdhfill() );
        
        this.runtimeComponents = new TableViewer( this, SWT.BORDER );
        this.runtimeComponents.getTable().setLayoutData( hhint( gdhfill(), 50 ) );
        this.runtimeComponents.getTable().setBackground( this.colorManila );
        this.runtimeComponents.setContentProvider( new RuntimeComponentsContentProvider() );
        this.runtimeComponents.setLabelProvider( new RuntimeComponentsLabelProvider() );
        
        this.runtimeComponents.setInput( NO_RUNTIME_SELECTED_PLACEHOLDER );
        this.runtimeComponents.getTable().setEnabled( false );
        this.runtimeComponentsLabel.setEnabled( false );
        
        refresh();
        this.currentPrimaryRuntime = this.model.getPrimaryRuntime();
    }
    
    public ChangeTargetedRuntimesDataModel getDataModel()
    {
        return this.model;
    }
    
    private void handleAvailableRuntimesChanged()
    {
        if( ! Thread.currentThread().equals( getDisplay().getThread() ) )
        {
            getDisplay().asyncExec
            ( 
                new Runnable()
                {
                    public void run()
                    {
                        handleAvailableRuntimesChanged();
                    }
                }
            );
            
            return;
        }
        
        if( this.showAllRuntimesSetting )
        {
            refresh();
        }
    }
    
    private void handleTargetableRuntimesChanged()
    {
        if( ! Thread.currentThread().equals( getDisplay().getThread() ) )
        {
            getDisplay().asyncExec
            ( 
                new Runnable()
                {
                    public void run()
                    {
                        handleTargetableRuntimesChanged();
                    }
                }
            );
            
            return;
        }
        
        refresh();
    }
    
    private void handleTargetedRuntimesChanged()
    {
        if( ! Thread.currentThread().equals( getDisplay().getThread() ) )
        {
            getDisplay().asyncExec
            ( 
                new Runnable()
                {
                    public void run()
                    {
                        handleTargetedRuntimesChanged();
                    }
                }
            );
            
            return;
        }
        
        final Set targeted = this.model.getTargetedRuntimes();
        
        for( Iterator itr = this.model.getTargetableRuntimes().iterator(); 
             itr.hasNext(); )
        {
            final IRuntime r = (IRuntime) itr.next();
            
            if( targeted.contains( r ) )
            {
                if( ! this.runtimes.getChecked( r ) )
                {
                    this.runtimes.setChecked( r, true );
                }
            }
            else
            {
                if( this.runtimes.getChecked( r ) )
                {
                    this.runtimes.setChecked( r, false );
                }
            }
        }
    }
    
    private void handlePrimaryRuntimeChanged()
    {
        if( ! Thread.currentThread().equals( getDisplay().getThread() ) )
        {
            getDisplay().asyncExec
            ( 
                new Runnable()
                {
                    public void run()
                    {
                        handlePrimaryRuntimeChanged();
                    }
                }
            );
            
            return;
        }
        
        if( this.currentPrimaryRuntime != null )
        {
            this.runtimes.update( this.currentPrimaryRuntime, null );
        }
        
        this.currentPrimaryRuntime = this.model.getPrimaryRuntime();
        
        if( this.currentPrimaryRuntime != null )
        {
            this.runtimes.update( this.currentPrimaryRuntime, null );
        }
    }
    
    private void handleCheckStateChanged( final CheckStateChangedEvent e )
    {
        final IRuntime runtime = (IRuntime) e.getElement();
        
        if( ! this.model.getTargetableRuntimes().contains( runtime ) &&
            e.getChecked() )
        {
            this.runtimes.setChecked( runtime, false );
            return;
        }
        
        if( e.getChecked() )
        {
            this.model.addTargetedRuntime( runtime );
        }
        else
        {
            this.model.removeTargetedRuntime( runtime );
        }
    }
    
    private void handleRuntimeSelectionChanged()
    {
        final IRuntime r = getSelection();
        
        if( r == null )
        {
            if( this.runtimeComponents.getInput() != null )
            {
                this.runtimeComponentsLabel.setEnabled( false );
                this.runtimeComponents.getTable().setEnabled( false );
                this.runtimeComponents.setInput( NO_RUNTIME_SELECTED_PLACEHOLDER );
            }
        }
        else
        {
            if( this.runtimeComponents.getInput() == null ||
                ! this.runtimeComponents.getInput().equals( r ) )
            {
                this.runtimeComponentsLabel.setEnabled( true );
                this.runtimeComponents.getTable().setEnabled( true );
                this.runtimeComponents.setInput( r );
            }
            
            if( this.runtimes.getChecked( r ) && 
                this.model.getPrimaryRuntime() != null && 
                ! this.model.getPrimaryRuntime().equals( r ) &&
                this.model.getTargetableRuntimes().contains( r ) )
            {
                this.makePrimaryButton.setEnabled( true );
            }
            else
            {
                this.makePrimaryButton.setEnabled( false );
            }
        }
    }
    
    private void handleShowAllRuntimesSelected()
    {
        this.showAllRuntimesSetting 
            = this.showAllRuntimesCheckbox.getSelection();
        
        refresh();
    }
    
    private void handleMakePrimarySelected()
    {
        this.model.setPrimaryRuntime( getSelection() );
    }

    private void handleWidgetDisposed()
    {
        removeDataModelListeners();
        
        this.colorGray.dispose();
        this.colorManila.dispose();
    }
    
    private void refresh()
    {
        this.runtimes.refresh();

        final Set untargetable = new HashSet( this.model.getAllRuntimes() );
        untargetable.removeAll( this.model.getTargetableRuntimes() );
        
        this.runtimes.setCheckedElements( this.model.getTargetedRuntimes().toArray() );
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
    
    private void addDataModelListener( final String event,
                                       final IDataModelListener listener )
    {
        this.model.addListener( event, listener );
        this.listeners.add( listener );
    }
    
    private void removeDataModelListeners()
    {
        for( Iterator itr = this.listeners.iterator(); itr.hasNext(); )
        {
            this.model.removeListener( (IDataModelListener) itr.next() );
        }
    }
    
    private final class ContentProvider

        implements IStructuredContentProvider
    
    {
        public Object[] getElements( final Object element )
        {
            if( RuntimesPanel.this.showAllRuntimesSetting )
            {
                return getDataModel().getAllRuntimes().toArray();
            }
            else
            {
                return getDataModel().getTargetableRuntimes().toArray();
            }
        }
    
        public void dispose() { }
    
        public void inputChanged( final Viewer viewer,
                                  final Object oldObject,
                                  final Object newObject ) {}
    }
    
    private final class LabelProvider

        implements ILabelProvider, IColorProvider
    
    {
        private final ImageRegistry imageRegistry;
        
        public LabelProvider()
        {
            this.imageRegistry = new ImageRegistry();
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
            
            final IRuntime primary = getDataModel().getPrimaryRuntime();
            final boolean isPrimary = primary != null && primary.equals( r );
            
            final String imgid
                = ( isPrimary ? "p:" : "s" ) //$NON-NLS-1$ //$NON-NLS-2$
                  + rct.getId();
            
            Image image = this.imageRegistry.get( imgid );
            
            if( image == null )
            {
                final IDecorationsProvider decprov
                    = (IDecorationsProvider) rct.getAdapter( IDecorationsProvider.class );
                
                ImageDescriptor imgdesc = decprov.getIcon();
                
                if( isPrimary )
                {
                    imgdesc = new PrimaryRuntimeImageDescriptor( imgdesc );
                }
                
                this.imageRegistry.put( imgid, imgdesc );
                image = this.imageRegistry.get( imgid );
            }

            if( getDataModel().getTargetableRuntimes().contains( r ) )
            {
                if( RuntimesPanel.this.model.getTargetedRuntimes().contains( r ) )
                {
                    RuntimesPanel.this.runtimes.setChecked( r, true );
                }
                else
                {
                    RuntimesPanel.this.runtimes.setChecked( r, false );
                }
                
                return image;
            }
            else
            {
                RuntimesPanel.this.runtimes.setChecked( r, false );
                
                final String greyedId = rct.getId() + "##greyed##"; //$NON-NLS-1$
                Image greyed = this.imageRegistry.get( greyedId );
                
                if( greyed == null )
                {
                    greyed = new Image( null, image, SWT.IMAGE_GRAY );
                    this.imageRegistry.put( greyedId, greyed );
                }
                
                return greyed;
            }
        }
        
        public Color getForeground( final Object element )
        {
            if( ! getDataModel().getTargetableRuntimes().contains( element ) )
            {
                return RuntimesPanel.this.colorGray;
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
            final IRuntime r2 = (IRuntime) b;
            
            return r1.getName().compareToIgnoreCase( r2.getName() );
        }
    }
    
    private final class RuntimeComponentsContentProvider

        implements IStructuredContentProvider
    
    {
        public Object[] getElements( final Object element )
        {
            if( element == NO_RUNTIME_SELECTED_PLACEHOLDER )
            {
                return new Object[] { NO_RUNTIME_SELECTED_PLACEHOLDER };
            }
            else
            {
                final IRuntime r = (IRuntime) element;
                return r.getRuntimeComponents().toArray();
            }
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
            if( element == NO_RUNTIME_SELECTED_PLACEHOLDER )
            {
                return Resources.noRuntimeSelectedLabel;
            }
            
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
            if( element == NO_RUNTIME_SELECTED_PLACEHOLDER )
            {
                return null;
            }

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
    
    private static final class PrimaryRuntimeImageDescriptor 
    
        extends CompositeImageDescriptor 
        
    {
        private static final String OVERLAY_IMG_LOCATION
            = "images/primary-runtime-overlay.gif"; //$NON-NLS-1$
        
        private static final ImageData OVERLAY
            = FacetUiPlugin.getImageDescriptor( OVERLAY_IMG_LOCATION ).getImageData();
        
        private final ImageData base;
        private final Point size;
        
        public PrimaryRuntimeImageDescriptor( final ImageDescriptor base ) 
        {
            this.base = base.getImageData();
            this.size = new Point( this.base.width, this.base.height ); 
        }
    
        protected void drawCompositeImage( final int width, 
                                           final int height ) 
        {
            drawImage( this.base, 0, 0 );
            drawImage( OVERLAY, 0, height - OVERLAY.height );
        }
    
        protected Point getSize()
        {
            return this.size;
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
        public static String makePrimaryLabel;
        public static String showAllRuntimes;
        public static String noRuntimeSelectedLabel;
        
        static
        {
            initializeMessages( RuntimesPanel.class.getName(), 
                                Resources.class );
        }
    }
    
}
