/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gd;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhalign;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glmargins;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleEvent;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleListener;
import org.eclipse.wst.common.project.facet.core.runtime.events.IValidationStatusChangedEvent;
import org.eclipse.wst.common.project.facet.core.runtime.internal.UnknownRuntime;
import org.eclipse.wst.common.project.facet.ui.IDecorationsProvider;
import org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider;
import org.eclipse.wst.common.project.facet.ui.internal.util.BasicToolTip;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class RuntimesPanel

    extends Composite

{
    private static final Object NO_RUNTIME_SELECTED_PLACEHOLDER = new Object();
    
    private final IFacetedProjectWorkingCopy fpjwc;
    private boolean showAllRuntimesSetting;
    private final CheckboxTableViewer runtimes;
    private final Button showAllRuntimesCheckbox;
    private final Button makePrimaryButton;
    private final Button newRuntimeButton;
    private final Label runtimeComponentsLabel;
    private final TableViewer runtimeComponents;
    private IRuntime currentPrimaryRuntime;
    private final List<IFacetedProjectListener> listeners;
    private Color colorGray;
    private final RuntimeValidationAssistant runtimeValidationAssistant;
    
    public RuntimesPanel( final Composite parent,
                          final IFacetedProjectWorkingCopy fpjwc )
    {
        super( parent, SWT.NONE );
        
        this.listeners = new ArrayList<IFacetedProjectListener>();
        
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
        
        // Setup runtime validation assistant.
        
        this.runtimeValidationAssistant = new RuntimeValidationAssistant();
       
        // Bind to the data model.
        
        this.fpjwc = fpjwc;
        
        addDataModelListener
        ( 
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    handleAvailableRuntimesChanged();
                }
            },
            IFacetedProjectEvent.Type.AVAILABLE_RUNTIMES_CHANGED
        );
        
        addDataModelListener
        ( 
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    handleTargetableRuntimesChanged();
                }
            },
            IFacetedProjectEvent.Type.TARGETABLE_RUNTIMES_CHANGED
        );
        
        addDataModelListener
        ( 
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    handleTargetedRuntimesChanged();
                }
            },
            IFacetedProjectEvent.Type.TARGETED_RUNTIMES_CHANGED
        );
        
        addDataModelListener
        ( 
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    handlePrimaryRuntimeChanged();
                }
            },
            IFacetedProjectEvent.Type.PRIMARY_RUNTIME_CHANGED
        );
        
        this.showAllRuntimesSetting = false;

        // Initialize the colors.
        
        this.colorGray = new Color( null, 160, 160, 164 );

        // Layout the panel.
        
        final GridLayout layout = new GridLayout( 1, false );
        layout.marginHeight = 5;
        layout.marginWidth = 5;

        setLayout( layout );

        this.runtimes = CheckboxTableViewer.newCheckList( this, SWT.BORDER );
        this.runtimes.getTable().setLayoutData( gdfill() );
        this.runtimes.setContentProvider( new ContentProvider() );
        this.runtimes.setLabelProvider( new LabelProvider() );
        this.runtimes.setSorter( new Sorter() );
        this.runtimes.setInput( new Object() );
        
        new ValidationProblemToolTip( this.runtimes.getTable() );
        
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
        
        final Composite buttons = new Composite( this, SWT.NONE );
        buttons.setLayoutData( gdhalign( gd(), GridData.END ) );
        buttons.setLayout( glmargins( gl( 2 ), 0, 0 ) );
        
        this.makePrimaryButton = new Button( buttons, SWT.PUSH );
        this.makePrimaryButton.setText( Resources.makePrimaryLabel );
        this.makePrimaryButton.setEnabled( false );
        GridDataFactory.defaultsFor( this.makePrimaryButton ).applyTo( this.makePrimaryButton );
        
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
        
        this.newRuntimeButton = new Button( buttons, SWT.PUSH );
        this.newRuntimeButton.setText( Resources.newRuntimeButtonLabel );
        GridDataFactory.defaultsFor( this.newRuntimeButton ).applyTo( this.newRuntimeButton );

        this.newRuntimeButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    handleNewRuntimeButtonSelected();
                }
            }
        );
        
        this.runtimeComponentsLabel = new Label( this, SWT.NONE );
        this.runtimeComponentsLabel.setText( Resources.runtimeCompositionLabel );
        this.runtimeComponentsLabel.setLayoutData( gdhfill() );
        
        final Color infoBackgroundColor
            = parent.getDisplay().getSystemColor( SWT.COLOR_INFO_BACKGROUND );
        
        final Color infoForegroundColor
            = parent.getDisplay().getSystemColor( SWT.COLOR_INFO_FOREGROUND );
        
        this.runtimeComponents = new TableViewer( this, SWT.BORDER );
        this.runtimeComponents.getTable().setLayoutData( gdhhint( gdhfill(), 50 ) );
        this.runtimeComponents.getTable().setBackground( infoBackgroundColor );
        this.runtimeComponents.getTable().setForeground( infoForegroundColor );
        this.runtimeComponents.setContentProvider( new RuntimeComponentsContentProvider() );
        this.runtimeComponents.setLabelProvider( new RuntimeComponentsLabelProvider() );
        
        this.runtimeComponents.setInput( NO_RUNTIME_SELECTED_PLACEHOLDER );
        this.runtimeComponents.getTable().setEnabled( false );
        this.runtimeComponentsLabel.setEnabled( false );
        
        refresh();
        this.currentPrimaryRuntime = this.fpjwc.getPrimaryRuntime();
        
	    Dialog.applyDialogFont( parent );
    }
    
    public IFacetedProjectWorkingCopy getFacetedProjectWorkingCopy()
    {
        return this.fpjwc;
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
        
        final Set<IRuntime> targeted = this.fpjwc.getTargetedRuntimes();
        
        for( IRuntime r : this.fpjwc.getTargetableRuntimes() )
        {
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
        
        this.currentPrimaryRuntime = this.fpjwc.getPrimaryRuntime();
        
        if( this.currentPrimaryRuntime != null )
        {
            this.runtimes.update( this.currentPrimaryRuntime, null );
        }
    }
    
    private void handleCheckStateChanged( final CheckStateChangedEvent e )
    {
        final IRuntime runtime = (IRuntime) e.getElement();
        
        if( ! getFacetedProjectWorkingCopy().getTargetableRuntimes().contains( runtime ) &&
            ! ( runtime instanceof UnknownRuntime ) && e.getChecked() )
        {
            this.runtimes.setChecked( runtime, false );
            return;
        }
        
        if( e.getChecked() )
        {
            this.fpjwc.addTargetedRuntime( runtime );
        }
        else
        {
            this.fpjwc.removeTargetedRuntime( runtime );
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
                this.fpjwc.getPrimaryRuntime() != null && 
                ! this.fpjwc.getPrimaryRuntime().equals( r ) &&
                ( this.fpjwc.getTargetableRuntimes().contains( r ) || r instanceof UnknownRuntime ) )
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
        this.fpjwc.setPrimaryRuntime( getSelection() );
    }
    
    @SuppressWarnings( "unchecked" )
    private void handleNewRuntimeButtonSelected()
    {
        final String SERVER_UI_PLUGIN_ID = "org.eclipse.wst.server.ui"; //$NON-NLS-1$
        final String CLASS_NAME = "org.eclipse.wst.server.ui.internal.ServerUIPlugin"; //$NON-NLS-1$
        final String METHOD_NAME = "showNewRuntimeWizard"; //$NON-NLS-1$
        
        final Bundle serverUiBundle = Platform.getBundle( SERVER_UI_PLUGIN_ID );
        
        if( serverUiBundle == null )
        {
            this.newRuntimeButton.setEnabled( false );
            return;
        }

        try
        {
            final Class serverUiPluginClass = serverUiBundle.loadClass( CLASS_NAME );
            
            final Method method
                = serverUiPluginClass.getMethod( METHOD_NAME, Shell.class, String.class );
            
            final Object result = method.invoke( null, getShell(), null );
            
            if( result.equals( true ) )
            {
                final Thread refreshThread = new Thread()
                {
                    public void run()
                    {
                        getFacetedProjectWorkingCopy().refreshTargetableRuntimes();
                    }
                };
                
                refreshThread.start();
            }
        }
        catch( Exception e )
        {
            FacetUiPlugin.log( e );
        }
    }

    private void handleWidgetDisposed()
    {
        removeDataModelListeners();
        this.colorGray.dispose();
        this.runtimeValidationAssistant.dispose();
    }
    
    private void handleRuntimeValidationResultChanged()
    {
        final Runnable uiRunnable = new Runnable()
        {
            public void run()
            {
                for( TableItem item : RuntimesPanel.this.runtimes.getTable().getItems() )
                {
                    RuntimesPanel.this.runtimes.update( item.getData(), null );
                }
            }
        };
        
        getDisplay().syncExec( uiRunnable );
    }
    
    private void refresh()
    {
        this.runtimes.refresh();

        final Set<IRuntime> untargetable = new HashSet<IRuntime>( RuntimeManager.getRuntimes() );
        untargetable.removeAll( this.fpjwc.getTargetableRuntimes() );
        
        this.runtimes.setCheckedElements( this.fpjwc.getTargetedRuntimes().toArray() );
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
    
    private void addDataModelListener( final IFacetedProjectListener listener,
                                       final IFacetedProjectEvent.Type... types )
    {
        this.fpjwc.addListener( listener, types );
        this.listeners.add( listener );
    }
    
    private void removeDataModelListeners()
    {
        for( IFacetedProjectListener listener : this.listeners )
        {
            this.fpjwc.removeListener( listener );
        }
    }
    
    private TableItem getTableItem( final int x,
                                    final int y )
    {
        for( TableItem item : this.runtimes.getTable().getItems() )
        {
            if( item.getBounds().contains( x, y ) )
            {
                return item;
            }
        }
        
        return null;
    }
    
    private RuntimeValidationAssistant getRuntimeValidationAssistant()
    {
        return this.runtimeValidationAssistant;
    }
    
    private final class ContentProvider

        implements IStructuredContentProvider
    
    {
        public Object[] getElements( final Object element )
        {
            final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
            final ArrayList<IRuntime> runtimes = new ArrayList<IRuntime>();
            
            if( RuntimesPanel.this.showAllRuntimesSetting )
            {
                runtimes.addAll( RuntimeManager.getRuntimes() );
            }
            else
            {
                runtimes.addAll( fpjwc.getTargetableRuntimes() );
            }
            
            final IFacetedProject fpj = fpjwc.getFacetedProject();
            
            if( fpj != null )
            {
                for( IRuntime runtime : fpj.getTargetedRuntimes() )
                {
                    if( runtime instanceof UnknownRuntime )
                    {
                        runtimes.add( runtime );
                    }
                }
            }
            
            return runtimes.toArray();
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
            return ( (IRuntime) element ).getLocalizedName();
        }
        
        private String getImageRegistryKey( final IRuntime runtime,
                                            final boolean isPrimary,
                                            final IStatus validationResult )
        {
            final StringBuilder buf = new StringBuilder();
            
            buf.append( runtime.getName() );
            
            if( isPrimary )
            {
                buf.append( ",##primary##" ); //$NON-NLS-1$
            }
            
            if( validationResult != null && validationResult.getSeverity() == IStatus.ERROR )
            {
                buf.append( ",##error##" ); //$NON-NLS-1$
            }
            
            return buf.toString();
        }

        public Image getImage( final Object element )
        {
            final IRuntime r = (IRuntime) element;
            final IRuntime primary = getFacetedProjectWorkingCopy().getPrimaryRuntime();
            final boolean isPrimary = primary != null && primary.equals( r );
            final IStatus valResult = getRuntimeValidationAssistant().getValidationResult( r );
            final String imgid = getImageRegistryKey( r, isPrimary, valResult );
            
            Image image = this.imageRegistry.get( imgid );
            
            if( image == null )
            {
                final IDecorationsProvider decprov
                    =  r.getAdapter( IDecorationsProvider.class );

                final ImageDescriptor imgdesc
                    = new DecoratedRuntimeImageDescriptor( decprov.getIcon(), isPrimary, valResult );
                
                this.imageRegistry.put( imgid, imgdesc );
                image = this.imageRegistry.get( imgid );
            }

            if( RuntimesPanel.this.fpjwc.getTargetedRuntimes().contains( r ) )
            {
                RuntimesPanel.this.runtimes.setChecked( r, true );
            }
            else
            {
                RuntimesPanel.this.runtimes.setChecked( r, false );
            }
            
            if( ! getFacetedProjectWorkingCopy().getTargetableRuntimes().contains( r ) &&
                ! ( r instanceof UnknownRuntime ) )
            {
                final String greyedId = r.getName() + "##greyed##"; //$NON-NLS-1$
                Image greyed = this.imageRegistry.get( greyedId );
                
                if( greyed == null )
                {
                    greyed = new Image( null, image, SWT.IMAGE_GRAY );
                    this.imageRegistry.put( greyedId, greyed );
                }
                
                image = greyed;
            }
            
            return image;
        }
        
        public Color getForeground( final Object element )
        {
            if( ! getFacetedProjectWorkingCopy().getTargetableRuntimes().contains( element ) &&
                ! ( element instanceof UnknownRuntime ) )
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
            
            return r1.getLocalizedName().compareToIgnoreCase( r2.getLocalizedName() );
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
                =  comp.getAdapter( IRuntimeComponentLabelProvider.class );
            
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
            final IRuntimeComponentVersion rcv = rc.getRuntimeComponentVersion();
            
            final String key = rct.getId() + ":" + rcv.getVersionString(); //$NON-NLS-1$
            Image image = this.imageRegistry.get( key );
            
            if( image == null )
            {
                final IDecorationsProvider decprov
                    = rcv.getAdapter( IDecorationsProvider.class );
                
                this.imageRegistry.put( key, decprov.getIcon() );
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
    
    private static final class DecoratedRuntimeImageDescriptor 
    
        extends CompositeImageDescriptor 
        
    {
        private static final String PRIMARY_RUNTIME_OVERLAY_IMG_LOCATION
            = "images/primary-runtime-overlay.gif"; //$NON-NLS-1$
        
        private static final ImageData PRIMARY_RUNTIME_OVERLAY
            = FacetUiPlugin.getImageDescriptor( PRIMARY_RUNTIME_OVERLAY_IMG_LOCATION ).getImageData();
        
        private static final String ERROR_OVERLAY_IMG_LOCATION
            = "images/error-overlay.gif"; //$NON-NLS-1$
        
        private static final ImageData ERROR_OVERLAY
            = FacetUiPlugin.getImageDescriptor( ERROR_OVERLAY_IMG_LOCATION ).getImageData();

        private static final String WARNING_OVERLAY_IMG_LOCATION
            = "images/warning-overlay.gif"; //$NON-NLS-1$
        
        private static final ImageData WARNING_OVERLAY
            = FacetUiPlugin.getImageDescriptor( WARNING_OVERLAY_IMG_LOCATION ).getImageData();
    
        private final ImageData base;
        private final Point size;
        private boolean isPrimary;
        private IStatus valResult;
        
        public DecoratedRuntimeImageDescriptor( final ImageDescriptor base,
                                                final boolean isPrimary,
                                                final IStatus valResult ) 
        {
            this.base = base.getImageData();
            this.size = new Point( this.base.width, this.base.height );
            this.isPrimary = isPrimary;
            this.valResult = valResult;
        }
    
        protected void drawCompositeImage( final int width, 
                                           final int height ) 
        {
            drawImage( this.base, 0, 0 );
            
            if( this.isPrimary )
            {
                drawImage( PRIMARY_RUNTIME_OVERLAY, width - PRIMARY_RUNTIME_OVERLAY.width, 
                           height - PRIMARY_RUNTIME_OVERLAY.height );
            }
            
            if( this.valResult != null && ! this.valResult.isOK() )
            {
                final ImageData valOverlay
                    = this.valResult.getSeverity() == IStatus.ERROR 
                      ? ERROR_OVERLAY : WARNING_OVERLAY;
                
                drawImage( valOverlay, 0, height - valOverlay.height );
            }
        }
    
        protected Point getSize()
        {
            return this.size;
        }
    }
    
    private final class ValidationProblemToolTip
    
        extends BasicToolTip
        
    {
        public ValidationProblemToolTip( final Control control )
        {
            super( control );
        }
        
        @Override
        protected Composite createToolTipContentArea( final Event event,
                                                      final Composite parent )
        {
            final IStatus validationResult = getValidationResult( event );
            setMessage( validationResult.getMessage() );
            return super.createToolTipContentArea( event, parent );
        }
        
        @Override
        protected boolean shouldCreateToolTip( final Event event ) 
        {
            return ! getValidationResult( event ).isOK();
        }
        
        private IRuntime getRuntime( final Event event )
        {
            final TableItem item = getTableItem( event.x, event.y );
            return item != null ? (IRuntime) item.getData() : null;
        }
        
        private IStatus getValidationResult( final Event event )
        {
            final IRuntime runtime = getRuntime( event );
            IStatus result = null;
            
            if( runtime != null )
            {
                result = RuntimesPanel.this.runtimeValidationAssistant.getValidationResult( runtime );
            }
            
            if( result == null )
            {
                result = Status.OK_STATUS;
            }
            
            return result;
        }
    }
    
    private final class RuntimeValidationAssistant
    {
        private final Map<String,IStatus> validationResults;
        private final IRuntimeLifecycleListener runtimeLifecycleListener;
        
        public RuntimeValidationAssistant()
        {
            this.validationResults = new HashMap<String,IStatus>();
            
            this.runtimeLifecycleListener = new IRuntimeLifecycleListener()
            {
                public void handleEvent( final IRuntimeLifecycleEvent event )
                {
                    final IValidationStatusChangedEvent evt = (IValidationStatusChangedEvent) event;
                    setValidationResult( evt.getRuntime(), evt.getNewValidationStatus() );
                    handleRuntimeValidationResultChanged();
                }
            };
            
            final Thread initialValidationThread = new Thread()
            {
                public void run()
                {
                    for( IRuntime runtime : RuntimeManager.getRuntimes() )
                    {
                        final IStatus result = runtime.validate( new NullProgressMonitor() );
                        
                        synchronized( RuntimeValidationAssistant.this.validationResults )
                        {
                            if( getValidationResult( runtime ) == null )
                            {
                                setValidationResult( runtime, result );
                            }
                        }
                    }
                    
                    RuntimeManager.addListener( RuntimeValidationAssistant.this.runtimeLifecycleListener, 
                                                IRuntimeLifecycleEvent.Type.VALIDATION_STATUS_CHANGED );
                    
                    handleRuntimeValidationResultChanged();
                }
            };
            
            initialValidationThread.start();
        }
        
        public IStatus getValidationResult( final IRuntime runtime )
        {
            synchronized( this.validationResults )
            {
                return this.validationResults.get( runtime.getName() );
            }
        }
        
        private void setValidationResult( final IRuntime runtime,
                                          final IStatus validationResult )
        {
            synchronized( this.validationResults )
            {
                this.validationResults.put( runtime.getName(), validationResult );
            }
        }
        
        public void dispose()
        {
            RuntimeManager.removeListener( this.runtimeLifecycleListener );
        }
    }

    private static final class Resources
    
        extends NLS
        
    {
        public static String runtimeCompositionLabel;
        public static String makePrimaryLabel;
        public static String newRuntimeButtonLabel;
        public static String showAllRuntimes;
        public static String noRuntimeSelectedLabel;
        
        static
        {
            initializeMessages( RuntimesPanel.class.getName(), 
                                Resources.class );
        }
    }
    
}
