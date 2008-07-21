/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import static java.lang.Math.max;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gd;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhspan;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdwhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glmargins;
import static org.eclipse.wst.common.project.facet.ui.internal.util.SwtUtil.*;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetsChangedEvent;
import org.eclipse.wst.common.project.facet.core.internal.ProjectFacet;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleEvent;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleListener;
import org.eclipse.wst.common.project.facet.ui.IDecorationsProvider;
import org.eclipse.wst.common.project.facet.ui.ModifyFacetedProjectWizard;
import org.eclipse.wst.common.project.facet.ui.internal.util.BasicToolTip;
import org.eclipse.wst.common.project.facet.ui.internal.util.HeaderToolTip;
import org.eclipse.wst.common.project.facet.ui.internal.util.ReadOnlyComboBoxCellEditor;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetsSelectionPanel

    extends Composite
    implements ISelectionProvider

{
    private static final String CW_FACET = "cw.facet"; //$NON-NLS-1$
    private static final String CW_VERSION = "cw.version"; //$NON-NLS-1$
    private static final String SASH1W1 = "sash.1.weight.1"; //$NON-NLS-1$
    private static final String SASH1W2 = "sash.1.weight.2"; //$NON-NLS-1$
    private static final String SASH2W1 = "sash.2.weight.1"; //$NON-NLS-1$
    private static final String SASH2W2 = "sash.2.weight.2"; //$NON-NLS-1$
    
    private static final String IMG_DOWN_ARROW = "##down-arrow##"; //$NON-NLS-1$
    
    private final Composite topComposite;
    private final SashForm sform1;
    private final SashForm sform2;
    private final Label presetsLabel;
    private final Combo presetsCombo;
    private final Button savePresetButton;
    private final Button deletePresetButton;
    private final CheckboxTreeViewer treeViewer;
    private final Tree tree;
    private final TreeViewerColumn colFacet;
    private final TreeViewerColumn colVersion;
    private final FixedFacetToolTip fixedFacetToolTip;
    private final Menu popupMenu;
    private final MenuItem popupMenuLockUnlock;
    private final MenuItem popupMenuChangeVersion;
    private final TableViewer problemsView;
    private final TabItem detailsTabItem;
    private final DetailsPanel detailsPanel;
    private final TabItem runtimesTabItem;
    private final RuntimesPanel runtimesPanel;
    
    private final IDialogSettings settings;
    private boolean showToolTips;
    
    
    private final IFacetedProjectWorkingCopy fpjwc;
    private final List<IFacetedProjectListener> registeredWorkingCopyListeners;
    private final Map<IProjectFacet,IProjectFacetVersion> selectedVersions;
    private final List<ISelectionChangedListener> selectionListeners;
    private Object selection;
    
    /**
     * Holds images used throughout the panel.
     */
    
    private final ImageRegistry imageRegistry;
    
    private final IRuntimeLifecycleListener runtimeLifecycleListener;
    
    public interface IFilter 
    {
        boolean check( IProjectFacetVersion fv );
    }

    public FacetsSelectionPanel( final Composite parent,
                                 final IFacetedProjectWorkingCopy fpjwc )
    {
        super( parent, SWT.NONE );

        this.fpjwc = fpjwc;
        this.registeredWorkingCopyListeners = new ArrayList<IFacetedProjectListener>(); 
        this.selectedVersions = new HashMap<IProjectFacet,IProjectFacetVersion>();
        this.selection = null;
        this.selectionListeners = new ArrayList<ISelectionChangedListener>();
        this.showToolTips = false;
        
        // Initialize the image registry.
        
        this.imageRegistry = new ImageRegistry();
        final Bundle bundle = Platform.getBundle( FacetUiPlugin.PLUGIN_ID );
        
        URL url = bundle.getEntry( "images/down-arrow.gif" ); //$NON-NLS-1$
        this.imageRegistry.put( IMG_DOWN_ARROW, ImageDescriptor.createFromURL( url ) );

        // Read the dialog settings.

        final IDialogSettings root
            = FacetUiPlugin.getInstance().getDialogSettings();

        IDialogSettings temp = root.getSection( getClass().getName() );

        if( temp == null )
        {
            temp = root.addNewSection( getClass().getName() );
        }
        
        this.settings = temp;

        // Layout the panel.

        setLayout( glmargins( gl( 1 ), 0, 0 ) );
        
        this.topComposite = new Composite( this, SWT.NONE );
        this.topComposite.setLayout( glmargins( gl( 4 ), 0, 0 ) );
        this.topComposite.setLayoutData( gdfill() );
        
        this.presetsLabel = new Label( this.topComposite, SWT.NONE );
        this.presetsLabel.setText( Resources.presetsLabel );
        
        this.presetsCombo = new Combo( this.topComposite, SWT.READ_ONLY );
        this.presetsCombo.setLayoutData( gdhfill() );
        
        this.savePresetButton = new Button( this.topComposite, SWT.PUSH );
        this.savePresetButton.setText( Resources.saveButtonLabel );
        
        this.savePresetButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    handleSavePreset();
                }
            }
        );

        this.deletePresetButton = new Button( this.topComposite, SWT.PUSH );
        this.deletePresetButton.setText( Resources.deleteButtonLabel );
        
        this.deletePresetButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    handleDeletePreset();
                }
            }
        );
        
        final int width 
            = Math.max( getPreferredWidth( this.savePresetButton ), 
                        getPreferredWidth( this.deletePresetButton ) ) + 15;
                        
        this.savePresetButton.setLayoutData( gdwhint( gd(), width ) );
        this.deletePresetButton.setLayoutData( gdwhint( gd(), width ) );
        
        this.sform1 = new SashForm( this.topComposite, SWT.VERTICAL | SWT.SMOOTH );
        this.sform1.setLayoutData( gdhspan( gdfill(), 4 ) );
        
        this.sform2 = new SashForm( this.sform1, SWT.HORIZONTAL | SWT.SMOOTH );
        this.sform2.setLayoutData( gdhspan( gdfill(), 4 ) );
        
        this.treeViewer = new CheckboxTreeViewer( this.sform2, SWT.BORDER );
        this.tree = this.treeViewer.getTree();
        
        this.tree.setHeaderVisible( true );
        
        this.treeViewer.setContentProvider( new ContentProvider() );
        this.treeViewer.setSorter( new Sorter() );
        
        this.colFacet = new TreeViewerColumn( this.treeViewer, SWT.NONE );
        this.colFacet.getColumn().setText( Resources.facetColumnLabel );
        this.colFacet.getColumn().setResizable( true );
        this.colFacet.setLabelProvider( new FacetColumnLabelProvider() );
        
        if( this.settings.get( CW_FACET ) == null )
        {
            this.settings.put( CW_FACET, computeDefaultFacetColumnWidth() );
        }
        
        this.colFacet.getColumn().setWidth( this.settings.getInt( CW_FACET ) );
        
        this.colFacet.getColumn().addListener
        (
            SWT.Resize,
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    FacetsSelectionPanel.this.settings.put( CW_FACET, FacetsSelectionPanel.this.colFacet.getColumn().getWidth() );
                }
            }
        );

        this.colVersion = new TreeViewerColumn( this.treeViewer, SWT.NONE );
        this.colVersion.getColumn().setText( Resources.versionColumnLabel );
        this.colVersion.getColumn().setResizable( true );
        this.colVersion.setLabelProvider( new FacetVersionColumnLabelProvider() );
        this.colVersion.setEditingSupport( new FacetVersionColumnEditingSupport( this.treeViewer ) );
        
        if( this.settings.get( CW_VERSION ) == null )
        {
            this.settings.put( CW_VERSION, computeDefaultVersionColumnWidth() );
        }

        this.colVersion.getColumn().setWidth( this.settings.getInt( CW_VERSION ) );
        
        this.colVersion.getColumn().addListener
        (
            SWT.Resize,
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    FacetsSelectionPanel.this.settings.put( CW_VERSION, FacetsSelectionPanel.this.colVersion.getColumn().getWidth() );
                }
            }
        );
        
        this.popupMenu = new Menu( getShell(), SWT.POP_UP );
        
        this.popupMenuChangeVersion = new MenuItem( this.popupMenu, SWT.PUSH );
        this.popupMenuChangeVersion.setText( Resources.changeVersionMenuItem );

        this.popupMenuChangeVersion.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    handleChangeVersionMenuSelected();
                }
            }
        );
        
        this.popupMenuLockUnlock = new MenuItem( this.popupMenu, SWT.PUSH );
        
        this.popupMenuLockUnlock.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    handleFacetLockUnlock();
                }
            }
        );

        new FacetToolTip( this.tree );
        new CategoryToolTip( this.tree );
        this.fixedFacetToolTip = new FixedFacetToolTip( this.tree );
        
        this.treeViewer.setInput( new Object() );

        this.treeViewer.addSelectionChangedListener
        (
            new ISelectionChangedListener()
            {
                public void selectionChanged( final SelectionChangedEvent e )
                {
                    FacetsSelectionPanel.this.handleSelectionChangedEvent();
                }
            }
        );
        
        this.treeViewer.addCheckStateListener
        (
            new ICheckStateListener()
            {
                public void checkStateChanged( final CheckStateChangedEvent e )
                {
                    FacetsSelectionPanel.this.handleCheckStateChanged( e );
                }
            }
        );

        this.tree.addListener
        (
            SWT.PaintItem,
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    handlePaintItemEvent( event );
                }
            }
        );
        
        this.tree.addListener
        (
            SWT.MouseDown,
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    handleMouseDownEvent( event );
                }
            }
        );
        
        final TabFolder tabFolder = new TabFolder( this.sform2, SWT.NONE );
        tabFolder.setLayoutData( gdhhint( gdhfill(), 80 ) );

        this.detailsPanel = new DetailsPanel( tabFolder, this );
        this.detailsTabItem = new TabItem( tabFolder, SWT.NULL );
        this.detailsTabItem.setControl( this.detailsPanel );
        this.detailsTabItem.setText( Resources.detailsTabLabel );

        this.runtimesPanel = new RuntimesPanel( tabFolder, this.fpjwc );
        this.runtimesTabItem = new TabItem( tabFolder, SWT.NULL );
        this.runtimesTabItem.setControl( this.runtimesPanel );
        this.runtimesTabItem.setText( Resources.runtimesTabLabel );
        
        this.runtimesPanel.addListener
        (
            SWT.Resize,
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    final int[] weights = FacetsSelectionPanel.this.sform1.getWeights();
                    FacetsSelectionPanel.this.settings.put( SASH1W1, weights[ 0 ] );
                    FacetsSelectionPanel.this.settings.put( SASH1W2, weights[ 1 ] );
                }
            }
        );

        this.problemsView = new TableViewer( this.sform1, SWT.BORDER );
        this.problemsView.setContentProvider( new ProblemsContentProvider() );
        this.problemsView.setLabelProvider( new ProblemsLabelProvider() );
        this.problemsView.setInput( new Object() );

        this.problemsView.getTable().addListener
        (
            SWT.Resize,
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    final int[] weights = FacetsSelectionPanel.this.sform2.getWeights();
                    FacetsSelectionPanel.this.settings.put( SASH2W1, weights[ 0 ] );
                    FacetsSelectionPanel.this.settings.put( SASH2W2, weights[ 1 ] );
                }
            }
        );
        
        if( this.settings.get( SASH1W1 ) == null ) this.settings.put( SASH1W1, 70 );
        if( this.settings.get( SASH1W2 ) == null ) this.settings.put( SASH1W2, 30 );
        if( this.settings.get( SASH2W1 ) == null ) this.settings.put( SASH2W1, 60 );
        if( this.settings.get( SASH2W2 ) == null ) this.settings.put( SASH2W2, 40 );
        
        final int[] weights1
        	= new int[] { this.settings.getInt( SASH1W1 ),
                          this.settings.getInt( SASH1W2 ) };

    	this.sform1.setWeights( weights1 );

        final int[] weights2
            = new int[] { this.settings.getInt( SASH2W1 ),
                          this.settings.getInt( SASH2W2 ) };

        this.sform2.setWeights( weights2 );
        
        this.addDisposeListener
        (
            new DisposeListener()
            {
                public void widgetDisposed( final DisposeEvent e )
                {
                    handleDisposeEvent();
                }
            }
        );
        
        Dialog.applyDialogFont( parent );
        
        // Setup runtime lifecycle listener.
        
        this.runtimeLifecycleListener = new IRuntimeLifecycleListener()
        {
            public void handleEvent( final IRuntimeLifecycleEvent event )
            {
                handleValidationProblemsChangedEvent();
            }
        };
        
        RuntimeManager.addListener( this.runtimeLifecycleListener, 
                                    IRuntimeLifecycleEvent.Type.VALIDATION_STATUS_CHANGED );
        
        // Bind to the model.
        
        addWorkingCopyListener
        (
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    handleProjectFacetsChangedEvent( event );
                }
            },
            IFacetedProjectEvent.Type.PROJECT_FACETS_CHANGED
        );
        
        handleProjectFacetsChangedEvent( null );
        
        addWorkingCopyListener
        (
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    handleValidationProblemsChangedEvent();
                }
            },
            IFacetedProjectEvent.Type.VALIDATION_PROBLEMS_CHANGED,
            IFacetedProjectEvent.Type.PROJECT_MODIFIED
        );
        
        handleValidationProblemsChangedEvent();
        
        addWorkingCopyListener
        ( 
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    handleSelectedPresetChangedEvent();
                }
            },
            IFacetedProjectEvent.Type.SELECTED_PRESET_CHANGED
        );

        ModifyFacetedProjectWizard.syncWithPresetsModel( this.fpjwc, this.presetsCombo );
        
        addWorkingCopyListener
        ( 
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    handleModelChangedEvent( event );
                }
            },
            IFacetedProjectEvent.Type.FIXED_FACETS_CHANGED, 
            IFacetedProjectEvent.Type.SELECTED_PRESET_CHANGED,
            IFacetedProjectEvent.Type.TARGETED_RUNTIMES_CHANGED
        );
        
        // Select the first item in the table.
        
        if( this.tree.getItemCount() > 0 )
        {
            final TreeItem firstItem = this.tree.getItem( 0 );
            this.treeViewer.setSelection( new StructuredSelection( firstItem.getData() ) );
        }
        
        handleSelectionChangedEvent();
    }
    
    public IFacetedProjectWorkingCopy getFacetedProjectWorkingCopy()
    {
        return this.fpjwc;
    }
    
    public boolean isSelectionValid()
    {
        return ( this.fpjwc.validate().getSeverity() != IStatus.ERROR );
    }
    
    public boolean setFocus()
    {
        return this.tree.setFocus();
    }
    
    public void addSelectionChangedListener( final ISelectionChangedListener listener )
    {
        this.selectionListeners.add( listener );
    }

    public void removeSelectionChangedListener( final ISelectionChangedListener listener )
    {
        this.selectionListeners.remove( listener );
    }

    public ISelection getSelection()
    {
    	if( this.selection != null )
    	{
    		return new StructuredSelection( this.selection );
    	}
    	else
    	{
    		return new StructuredSelection( new Object[ 0 ] );
    	}
    }

    public void setSelection( final ISelection selection )
    {
        throw new UnsupportedOperationException();
    }

    private void notifySelectionChangedListeners()
    {
        final SelectionChangedEvent event = new SelectionChangedEvent( this, getSelection() );

        for( ISelectionChangedListener listener : this.selectionListeners )
        {
            listener.selectionChanged( event );
        }
    }
    
    private ImageRegistry getImageRegistry()
    {
        return this.imageRegistry;
    }
    
    public Image getImage( final IProjectFacet facet,
                           final boolean showDecorations )
    {
        final boolean isFixed = getFacetedProjectWorkingCopy().isFixedProjectFacet( facet );
        final String id = ( isFixed && showDecorations ? "F:" : "f:" ) + facet.getId(); //$NON-NLS-1$ //$NON-NLS-2$
        
        Image image = getImageRegistry().get( id );
        
        if( image == null )
        {
            final IDecorationsProvider decprov
                = (IDecorationsProvider) facet.getAdapter( IDecorationsProvider.class );
            
            ImageDescriptor imgdesc = decprov.getIcon();
            
            if( isFixed && showDecorations )
            {
                imgdesc = new FixedFacetImageDescriptor( imgdesc );
            }
            
            getImageRegistry().put( id, imgdesc );
            image = getImageRegistry().get( id );
        }

        return image;
    }
    
    public Image getImage( final ICategory category )
    {
        final String id = "c:" + category.getId(); //$NON-NLS-1$
        
        Image image = getImageRegistry().get( id );
        
        if( image == null )
        {
            final IDecorationsProvider decprov
                = (IDecorationsProvider) category.getAdapter( IDecorationsProvider.class );
            
            final ImageDescriptor imgdesc = decprov.getIcon();
            
            getImageRegistry().put( id, imgdesc );
            image = getImageRegistry().get( id );
        }

        return image;
        
    }

    private void refresh()
    {
        // Somehow the checked state of nested items gets lost when a refresh
        // is performed, so we have to do this workaround.
        
        final Object[] checked = this.treeViewer.getCheckedElements();
        this.treeViewer.refresh();
        this.treeViewer.setCheckedElements( checked );
    }
    
    public void setCategoryExpandedState( final ICategory category,
                                          final boolean expanded )
    {
        this.treeViewer.setExpandedState( category, expanded );
    }
    
    public boolean getShowToolTips()
    {
        return this.showToolTips;
    }
    
    public void setShowToolTips( final boolean showToolTips )
    {
        this.showToolTips = showToolTips;
    }
    
    private void refreshCategoryState( final ICategory category )
    {
        if( category == null )
        {
            return;
        }
        
        int available = 0;
        int selected = 0;

        for( IProjectFacet f : category.getProjectFacets() )
        {
            if( this.fpjwc.isFacetAvailable( f ) )
            {
                available++;
            }
            
            if( this.fpjwc.hasProjectFacet( f ) )
            {
                selected++;
            }
        }

        if( selected == 0 )
        {
            this.treeViewer.setChecked( category, false );
            this.treeViewer.setGrayed( category, false );
        }
        else if( selected == available )
        {
            this.treeViewer.setChecked( category, true );
            this.treeViewer.setGrayed( category, false );
        }
        else
        {
            this.treeViewer.setGrayChecked( category, true );
        }
    }
    
    private void addWorkingCopyListener( final IFacetedProjectListener listener,
                                         final IFacetedProjectEvent.Type... types )
    {
        this.fpjwc.addListener( listener, types );
        this.registeredWorkingCopyListeners.add( listener );
    }

    public IProjectFacet getSelectedProjectFacet()
    {
        final IProjectFacetVersion fv = getSelectedProjectFacetVersion();
        
        if( fv != null )
        {
            return fv.getProjectFacet();
        }
        
        return null;
    }
    
    public IProjectFacetVersion getSelectedProjectFacetVersion()
    {
        if( this.selection != null && this.selection instanceof IProjectFacetVersion )
        {
            return (IProjectFacetVersion) this.selection;
        }
        
        return null;
    }
    
    private IProjectFacetVersion getSelectedVersion( final IProjectFacet f )
    {
        final Set<IProjectFacetVersion> availableVersions = this.fpjwc.getAvailableVersions( f );
        
        if( availableVersions.isEmpty() )
        {
            throw new IllegalStateException();
        }
        
        IProjectFacetVersion selectedVersion = this.fpjwc.getProjectFacetVersion( f );
        
        if( selectedVersion == null )
        {
            selectedVersion = this.selectedVersions.get( f );

            if( selectedVersion == null )
            {
                selectedVersion = f.getDefaultVersion();
            }
            
            if( ! availableVersions.contains( selectedVersion ) )
            {
                selectedVersion = this.fpjwc.getHighestAvailableVersion( f );
            }
        }
        
        this.selectedVersions.put( f, selectedVersion );
        
        return selectedVersion;
    }
    
    private void setSelectedVersion( final IProjectFacet f,
                                     final IProjectFacetVersion fv )
    {
        if( this.fpjwc.getProjectFacetVersion( f ) != null )
        {
            this.fpjwc.changeProjectFacetVersion( fv );
        }
        
        this.selectedVersions.put( f, fv );
        
        if( f == this.getSelectedProjectFacet() )
        {
            this.selection = fv;
            notifySelectionChangedListeners();
        }
    }
    
    private void handleSelectionChangedEvent()
    {
        Object selection = ( (IStructuredSelection) this.treeViewer.getSelection() ).getFirstElement();

        if( selection != null && selection instanceof IProjectFacet )
        {
            selection = getSelectedVersion( (IProjectFacet ) selection );
        }
        
        if( selection != this.selection )
        {
            this.selection = selection;

            notifySelectionChangedListeners();
            updatePopupMenu();
        }
    }
    
    private void handleCheckStateChanged( final CheckStateChangedEvent event )
    {
        final Object el = event.getElement();
        final boolean checked = event.getChecked();
        
        if( el instanceof IProjectFacet )
        {
            final IProjectFacet f = (IProjectFacet) el;
            
            if( this.fpjwc.getFixedProjectFacets().contains( f ) )
            {
                if( ! checked )
                {
                    this.treeViewer.setChecked( el, true );
                    
                    final String msg 
                        = NLS.bind( Resources.couldNotDeselectFixedFacetMessage, f.getLabel() );                    

                    this.fixedFacetToolTip.setMessage( msg );
                    
                    final Point cursorLocation = getDisplay().getCursorLocation();
                    this.fixedFacetToolTip.show( this.tree.toControl( cursorLocation ) );
                }
                
                return;
            }
            
            if( checked )
            {
                this.fpjwc.addProjectFacet( getSelectedVersion( f ) );
            }
            else
            {
                this.fpjwc.removeProjectFacet( f );
            }
            
            refreshCategoryState( f.getCategory() );
        }
        else
        {
            final ContentProvider cp
                = (ContentProvider) this.treeViewer.getContentProvider();

            final Set<IProjectFacetVersion> facets
                = new HashSet<IProjectFacetVersion>( this.fpjwc.getProjectFacets() );
                
            final Object[] children = cp.getChildren( el );
            int selected = 0;
            
            for( Object child : children )
            {
                final IProjectFacet f = (IProjectFacet) child;
                
                if( ! this.fpjwc.getFixedProjectFacets().contains( f ) )
                {
                    final IProjectFacetVersion fv = getSelectedVersion( f );
                    
                    if( checked )
                    {
                        facets.add( fv );
                    }
                    else
                    {
                        facets.remove( fv );
                    }
                    
                    this.treeViewer.setChecked( f, checked );
                }
                
                if( this.fpjwc.hasProjectFacet( f ) )
                {
                    selected++;
                }
            }
            
            if( selected == 0 || selected == children.length )
            {
                this.treeViewer.setGrayed( el, false );
            }
            else
            {
                this.treeViewer.setGrayChecked( el, true );
            }
            
            this.fpjwc.setProjectFacets( facets );
        }

        this.fpjwc.setSelectedPreset( null );
        
        updatePopupMenu();
    }

    private void handleMouseDownEvent( final Event event )
    {
    	handleMouseDownEventHelper( event, this.tree.getItems() );
    }
    
    private boolean handleMouseDownEventHelper( final Event event,
    		                                    final TreeItem[] items )
    {
        for( TreeItem item : items )
        {
        	if( item.getBounds( 1 ).contains( event.x, event.y ) )
            {
            	final TreeItem[] newSelection = new TreeItem[] { item };
            	
            	if( ! Arrays.equals( this.tree.getSelection(), newSelection ) )
            	{
	                this.tree.setSelection( new TreeItem[] { item } );
	                this.treeViewer.editElement( item.getData(), 1 );
            	}
                
                return true;
            }
        	else if( handleMouseDownEventHelper( event, item.getItems() ) )
            {
            	return true;
            }
        }
        
        return false;
    }
    
    private void handlePaintItemEvent( final Event event )
    {
        final TreeItem item = (TreeItem) event.item;
        final Object itemData = item.getData();

        if( itemData instanceof IProjectFacet && event.index == 1 )
        {
            final IProjectFacet f = (IProjectFacet) itemData;
            
            if( this.fpjwc.getAvailableVersions( f ).size() > 1 )
            {
                final Image arrowImage = getImageRegistry().get( IMG_DOWN_ARROW );
                final Rectangle arrowImageBounds = arrowImage.getBounds();
                
                final int columnWidth = this.colVersion.getColumn().getWidth();
                final int itemHeight = this.tree.getItemHeight();
                
                final int bgcolor;
                
                bgcolor = SWT.COLOR_LIST_BACKGROUND;
                
                int x, y;
                
                x = event.x + columnWidth - arrowImageBounds.width - 10;
                y = event.y;
                event.gc.setBackground( item.getDisplay().getSystemColor( bgcolor ) );
                event.gc.fillRectangle( x, y, arrowImageBounds.width + 10, itemHeight );
                
                y = event.y + ( itemHeight - arrowImageBounds.height ) / 2;
                event.gc.drawImage( arrowImage, x, y );
            }
        }
    }
    
    private void handleDisposeEvent()
    {
        this.imageRegistry.dispose();
        
        for( IFacetedProjectListener listener : this.registeredWorkingCopyListeners )
        {
            this.fpjwc.removeListener( listener );
        }
        
        RuntimeManager.removeListener( this.runtimeLifecycleListener );
    }
    
    private void handleSavePreset()
    {
        final Set<IProjectFacetVersion> facets = this.fpjwc.getProjectFacets();
        final IPreset preset = SavePresetDialog.showDialog( getShell(), facets );
        
        if( preset != null )
        {
            this.fpjwc.setSelectedPreset( preset.getId() );
        }
    }
    
    private void handleDeletePreset()
    {
        final IPreset preset = this.fpjwc.getSelectedPreset();
        this.fpjwc.setSelectedPreset( null );
        ProjectFacetsManager.deletePreset( preset );
    }
    
    private void handleProjectFacetsChangedEvent( final IFacetedProjectEvent event )
    {
        final Set<ICategory> affectedCategories = new HashSet<ICategory>();
        
        if( event != null )
        {
            final IFacetedProjectWorkingCopy fpjwc = event.getWorkingCopy();
            
            final IProjectFacetsChangedEvent evt
                = (IProjectFacetsChangedEvent) event;
            
            for( IProjectFacetVersion fv : evt.getAllAffectedFacets())
            {
                final IProjectFacet f = fv.getProjectFacet();
                final boolean checked = fpjwc.hasProjectFacet( fv );
                this.treeViewer.setChecked( f, checked );
                this.treeViewer.update( f, null );
                
                final ICategory category = f.getCategory();
                
                if( category != null )
                {
                    affectedCategories.add( category );
                }
            }
        }
        else
        {
            final List<IProjectFacet> facets = new ArrayList<IProjectFacet>();
            
            for( IProjectFacetVersion fv : this.fpjwc.getProjectFacets() )
            {
                facets.add( fv.getProjectFacet() );
            }
            
            this.treeViewer.setCheckedElements( facets.toArray() );
            
            for( IProjectFacet facet : this.fpjwc.getAvailableFacets().keySet() )
            {
                final ICategory category = facet.getCategory();
                
                if( category != null )
                {
                    affectedCategories.add( category );
                }
            }
            
            this.treeViewer.update( this.fpjwc.getAvailableFacets().keySet().toArray(), null );
        }

        for( ICategory category : affectedCategories )
        {
            refreshCategoryState( category );
        }
    }
    
    private void handleValidationProblemsChangedEvent()
    {
        if( ! Thread.currentThread().equals( getDisplay().getThread() ) )
        {
            final Runnable uiRunnable = new Runnable()
            {
                public void run()
                {
                    handleValidationProblemsChangedEvent();
                }
            };
            
            getDisplay().asyncExec( uiRunnable );
            
            return;
        }
        
        this.problemsView.refresh();

        if( getFilteredProblems().length == 0 )
        {
            if( this.sform1.getMaximizedControl() == null )
            {
                this.sform1.setMaximizedControl( this.sform2 );
            }
        }
        else
        {
            if( this.sform1.getMaximizedControl() != null )
            {
                this.sform1.setMaximizedControl( null );
            }
        }
    }
    
    private void handleSelectedPresetChangedEvent()
    {
        final IPreset preset = this.fpjwc.getSelectedPreset();
        
        if( preset == null )
        {
            this.savePresetButton.setEnabled( true );
            this.deletePresetButton.setEnabled( false );
        }
        else
        {
            this.savePresetButton.setEnabled( false );
            this.deletePresetButton.setEnabled( preset.getType() == IPreset.Type.USER_DEFINED );
        }
    }
    
    private void handleModelChangedEvent( final IFacetedProjectEvent event )
    {
        switch( event.getType() )
        {
            case FIXED_FACETS_CHANGED:
            case TARGETED_RUNTIMES_CHANGED:
            {
                final Runnable runnable = new Runnable()
                {
                    public void run()
                    {
                        refresh();
                    }
                };
                
                runOnDisplayThread( getDisplay(), runnable );
                
                break;
            }
        }
    }
    
    private void handleChangeVersionMenuSelected()
    {
    	final IProjectFacet f = getSelectedProjectFacet();
    	final IProjectFacetVersion fv = getSelectedVersion( f );
    	final SortedSet<IProjectFacetVersion> versions = this.fpjwc.getAvailableVersions( f );
    	
    	final IProjectFacetVersion newVersion
    		= ChangeFacetVersionDialog.showDialog( getShell(), f, fv, versions );
    		
    	if( newVersion != null )
    	{
    		this.fpjwc.changeProjectFacetVersion( newVersion );
    	}
    }
    
    private void handleFacetLockUnlock()
    {
    	final IProjectFacet f = getSelectedProjectFacet();
    	
    	final Set<IProjectFacet> fixedFacets 
    		= new HashSet<IProjectFacet>( this.fpjwc.getFixedProjectFacets() );
    	
    	if( fixedFacets.contains( f ) )
    	{
    		fixedFacets.remove( f );
    	}
    	else
    	{
    		fixedFacets.add( f );
    	}
    	
    	this.fpjwc.setFixedProjectFacets( fixedFacets );
    	
    	updatePopupMenu();
    }
    
    private void updatePopupMenu()
    {
        if( this.selection instanceof IProjectFacetVersion )
        {
			this.tree.setMenu( this.popupMenu );

			final IProjectFacet f = ( (IProjectFacetVersion) this.selection ).getProjectFacet();
			
			if( this.fpjwc.isFixedProjectFacet( f ) )
			{
				this.popupMenuLockUnlock.setText( Resources.unlockMenuItem );
			}
			else
			{
				this.popupMenuLockUnlock.setText( Resources.lockMenuItem );
			}
			
			this.popupMenuLockUnlock.setEnabled( this.fpjwc.hasProjectFacet( f ) );
			
			final int numAvailableVersions = this.fpjwc.getAvailableVersions( f ).size();
			this.popupMenuChangeVersion.setEnabled( numAvailableVersions > 1 );
    	}
        else
        {
        	this.tree.setMenu( null );
        }
    }
    
    private TreeItem getTreeItem( final int x,
                                  final int y )
    {
        return getTreeItemHelper( x, y, this.tree.getItems() );
    }
    
    private static TreeItem getTreeItemHelper( final int x,
                                               final int y,
                                               final TreeItem[] items )
    {
        for( TreeItem item : items )
        {
            if( item.getBounds().contains( x, y ) )
            {
                return item;
            }
            
            final TreeItem res = getTreeItemHelper( x, y, item.getItems() );
            
            if( res != null )
            {
                return res;
            }
        }
        
        return null;
    }
    
    private int computeDefaultFacetColumnWidth()
    {
        final GC gc = new GC( this.getDisplay() );
        int maxFacetLabelWidth = 0;
        
        try
        {
            gc.setFont( this.tree.getFont() );
            
            for( IProjectFacet f : ProjectFacetsManager.getProjectFacets() )
            {
                maxFacetLabelWidth = max( maxFacetLabelWidth, gc.textExtent( f.getLabel() ).x );
            }
        }
        finally
        {
            gc.dispose();
        }
        
        return max( maxFacetLabelWidth + 100, 200 );
    }

    private int computeDefaultVersionColumnWidth()
    {
        final GC gc = new GC( this.getDisplay() );
        int maxVersionStringWidth = 0;
        final int columnLabelWidth;
        
        try
        {
            gc.setFont( this.tree.getFont() );

            for( IProjectFacet f : ProjectFacetsManager.getProjectFacets() )
            {
                for( IProjectFacetVersion fv : f.getVersions() )
                {
                    final int textExtent = gc.textExtent( fv.getVersionString() ).x;
                    maxVersionStringWidth = max( maxVersionStringWidth, textExtent + 30 );
                }
            }

            columnLabelWidth = gc.textExtent( Resources.versionColumnLabel ).x + 30;
        }
        finally
        {
            gc.dispose();
        }
        
        return max( maxVersionStringWidth, columnLabelWidth );
    }
    
    private IStatus[] getFilteredProblems()
    {
        final IStatus[] unfiltered = this.fpjwc.validate().getChildren();
        boolean somethingToRemove = false;
        
        for( IStatus st : unfiltered )
        {
            if( st.getCode() == IFacetedProjectWorkingCopy.PROBLEM_PROJECT_NAME )
            {
                somethingToRemove = true;
                break;
            }
        }
        
        if( ! somethingToRemove )
        {
            return unfiltered;
        }
        
        final List<IStatus> filtered = new ArrayList<IStatus>();
        
        for( IStatus st : unfiltered )
        {
            if( st.getCode() != IFacetedProjectWorkingCopy.PROBLEM_PROJECT_NAME )
            {
                filtered.add( st );
            }
        }
        
        return filtered.toArray( new IStatus[ filtered.size() ] );
    }

    private final class ContentProvider

        implements ITreeContentProvider

    {
        public Object[] getElements( final Object element )
        {
            final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
            final List<Object> list = new ArrayList<Object>();
            final Set<ICategory> categories = ProjectFacetsManager.getCategories();

            for( ICategory cat : categories )
            {
                boolean visible = false;

                for( IProjectFacet f : cat.getProjectFacets() )
                {
                    if( ! fpjwc.getAvailableVersions( f ).isEmpty() )
                    {
                        visible = true;
                        break;
                    }
                }

                if( visible )
                {
                    list.add( cat );
                }
            }
            
            for( Map.Entry<IProjectFacet,SortedSet<IProjectFacetVersion>> entry 
                 : fpjwc.getAvailableFacets().entrySet() )
            {
                final IProjectFacet f = entry.getKey();
                final SortedSet<IProjectFacetVersion> availableVersions = entry.getValue();
                
                if( f.getCategory() == null && ! availableVersions.isEmpty() )
                {
                    list.add( f );
                }
            }

            return list.toArray();
        }

        public Object[] getChildren( final Object parent )
        {
            if( parent instanceof ICategory )
            {
                final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
                final ICategory category = (ICategory) parent;
                final List<IProjectFacet> facets = new ArrayList<IProjectFacet>();

                for( IProjectFacet f : category.getProjectFacets() )
                {
                    if( ! fpjwc.getAvailableVersions( f ).isEmpty() )
                    {
                        facets.add( f );
                    }
                }

                return facets.toArray();
            }
            else
            {
                return new Object[ 0 ];
            }
        }

        public Object getParent( final Object element )
        {
            if( element instanceof IProjectFacet )
            {
                final IProjectFacet f = (IProjectFacet) element;
                return f.getCategory();
            }
            else
            {
                return null;
            }
        }

        public boolean hasChildren( final Object element )
        {
            return ( element instanceof ICategory ) &&
                   ! ( (ICategory) element ).getProjectFacets().isEmpty();
        }

        public void dispose() { }

        public void inputChanged( final Viewer viewer,
                                  final Object oldObject,
                                  final Object newObject ) {}
    }

    private final class FacetColumnLabelProvider

        extends ColumnLabelProvider
    
    {
        @Override
        public String getText( final Object element )
        {
            if( element instanceof ICategory )
            {
                return ( (ICategory) element ).getLabel();
            }
            else
            {
                return ( (IProjectFacet) element ).getLabel();
            }
        }
    
        @Override
        public Image getImage( final Object element )
        {
            if( element instanceof IProjectFacet )
            {
                return FacetsSelectionPanel.this.getImage( (IProjectFacet) element, true );
            }
            else
            {
                return FacetsSelectionPanel.this.getImage( (ICategory) element );
            }
        }
    }

    private final class FacetVersionColumnLabelProvider

        extends ColumnLabelProvider

    {
        @Override
        public String getText( final Object element )
        {
            if( element instanceof IProjectFacet )
            {
                final ProjectFacet f = (ProjectFacet) element;
                
                if( ! f.isVersionHidden() )
                {
                    return getSelectedVersion( f ).getVersionString();
                }
            }
            
            return null;
        }
    }

    private final class FacetVersionColumnEditingSupport

        extends EditingSupport
    
    {
        private final ReadOnlyComboBoxCellEditor ceditor;
        private final IFacetedProjectWorkingCopy fpjwc;
        
        public FacetVersionColumnEditingSupport( final TreeViewer treeViewer )
        {
            super( treeViewer );
            this.ceditor = new ReadOnlyComboBoxCellEditor( treeViewer.getTree(), new String[ 0 ], SWT.DROP_DOWN | SWT.READ_ONLY );
            this.fpjwc = getFacetedProjectWorkingCopy();
        }
        
        @Override
        public boolean canEdit( final Object element )
        {
            return element instanceof IProjectFacet &&
                   this.fpjwc.getAvailableVersions( (IProjectFacet ) element ).size() > 1;
        }
        
        @Override
        protected CellEditor getCellEditor( final Object element )
        {
            final IProjectFacet f = getSelectedProjectFacet();
            
            if( f == null )
            {
                throw new IllegalStateException();
            }
            
            final SortedSet<IProjectFacetVersion> versions = this.fpjwc.getAvailableVersions( f );
            final String[] verstrs = new String[ versions.size() ];
            Integer value = null;
            
            int i = 0;
            
            for( IProjectFacetVersion fv : versions )
            {
                verstrs[ i ] = fv.getVersionString();
                
                if( fv == getSelectedVersion( f ) )
                {
                    value = new Integer( i );
                }
                
                i++;
            }
            
            this.ceditor.setItems( verstrs );
            this.ceditor.setValue( value );
            
            return this.ceditor;
        }
    
        @Override
        public Object getValue( final Object element )
        {
            final IProjectFacet f = (IProjectFacet) element;
            int i = 0;
            
            for( IProjectFacetVersion fv : this.fpjwc.getAvailableVersions( f ) )
            {
                if( fv == getSelectedVersion( f ) )
                {
                    return new Integer( i );
                }
                
                i++;
            }
    
            return new IllegalStateException();
        }
    
        @Override
        public void setValue( final Object element,
                              final Object value )
        {
            final IProjectFacet f = (IProjectFacet) element;
            final int index = ( (Integer) value ).intValue();
    
            if( index != -1 )
            {
                int i = 0;
                
                for( IProjectFacetVersion fv : this.fpjwc.getAvailableVersions( f ) )
                {
                    if( i == index )
                    {
                        setSelectedVersion( f, fv );
                        FacetsSelectionPanel.this.treeViewer.update( f, null );
                        
                        break;
                    }
                    
                    i++;
                }
            }
        }
    }
    
    private static final class FixedFacetImageDescriptor 
    
        extends CompositeImageDescriptor 
        
    {
        private static final String OVERLAY_IMG_LOCATION
            = "images/lock.gif"; //$NON-NLS-1$
        
        private static final ImageData OVERLAY
            = FacetUiPlugin.getImageDescriptor( OVERLAY_IMG_LOCATION ).getImageData();
        
        private final ImageData base;
        private final Point size;
        
        public FixedFacetImageDescriptor( final ImageDescriptor base ) 
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
    
    private static final class Sorter

        extends ViewerSorter

    {
        public int compare( final Viewer viewer,
                            final Object a,
                            final Object b )
        {
            return getLabel( a ).compareToIgnoreCase( getLabel( b ) );
        }

        private static String getLabel( final Object obj )
        {
            if( obj instanceof IProjectFacet )
            {
                return ( (IProjectFacet) obj ).getLabel();
            }
            else
            {
                return ( (ICategory) obj ).getLabel();
            }
        }
    }
    
    private final class FacetToolTip
    
        extends HeaderToolTip
        
    {
        public FacetToolTip( final Control control )
        {
            super( control );
        }
        
        @Override
        protected final boolean shouldCreateToolTip( final Event event ) 
        {
            if( getShowToolTips() == false )
            {
                return false;
            }
            
            final TreeItem treeItem = getTreeItem( event.x, event.y );
            String description = null;
            
            if( treeItem != null && treeItem.getBounds( 0 ).contains( event.x, event.y ) )
            {
                final Object treeItemData = treeItem.getData();
                
                if( treeItemData instanceof IProjectFacet )
                {
                    description = ( (IProjectFacet) treeItemData ).getDescription();
                }
            }
            
            return ( description != null && description.trim().length() > 0 );
        }

        @Override
        protected String getToolTipTitle( final Event event )
        {
            final IProjectFacet f = (IProjectFacet) getTreeItem( event.x, event.y ).getData();
            return getSelectedVersion( f ).toString();
        }

        @Override
        protected Composite createContentArea( final Event event,
                                               final Composite parent )
        {
            final Display display = parent.getDisplay();
            
            final Composite composite = new Composite( parent, SWT.NONE );
            composite.setLayout( gl( 1 ) );
            composite.setBackground( display.getSystemColor( SWT.COLOR_INFO_BACKGROUND ) );
            
            final Label label = new Label( composite, SWT.WRAP );
            label.setLayoutData( gdfill() );
            label.setBackground( display.getSystemColor( SWT.COLOR_INFO_BACKGROUND ) );

            final IProjectFacet f = (IProjectFacet) getTreeItem( event.x, event.y ).getData();
            label.setText( f.getDescription() );
            
            return composite;
        }
    }

    private final class CategoryToolTip
    
        extends HeaderToolTip
        
    {
        public CategoryToolTip( final Control control )
        {
            super( control );
        }
        
        @Override
        protected final boolean shouldCreateToolTip( final Event event ) 
        {
            if( getShowToolTips() == false )
            {
                return false;
            }
            
            final TreeItem treeItem = getTreeItem( event.x, event.y );
            String description = null;
            
            if( treeItem != null && treeItem.getBounds( 0 ).contains( event.x, event.y ) )
            {
                final Object treeItemData = treeItem.getData();
                
                if( treeItemData instanceof ICategory )
                {
                    description = ( (ICategory) treeItemData ).getDescription();
                }
            }
            
            return ( description != null && description.trim().length() > 0 );
        }
    
        @Override
        protected String getToolTipTitle( final Event event )
        {
            return ( (ICategory) getTreeItem( event.x, event.y ).getData() ).getLabel();
        }
    
        @Override
        protected Composite createContentArea( final Event event,
                                               final Composite parent )
        {
            final Display display = parent.getDisplay();
            
            final Composite composite = new Composite( parent, SWT.NONE );
            composite.setLayout( gl( 1 ) );
            composite.setBackground( display.getSystemColor( SWT.COLOR_INFO_BACKGROUND ) );
            
            final Label label = new Label( composite, SWT.WRAP );
            label.setLayoutData( gdfill() );
            label.setBackground( display.getSystemColor( SWT.COLOR_INFO_BACKGROUND ) );
    
            final ICategory category = (ICategory) getTreeItem( event.x, event.y ).getData();
            label.setText( category.getDescription() );
            
            return composite;
        }
    }

    private final class FixedFacetToolTip
    
        extends BasicToolTip
        
    {
        private static final int FAKE_EVENT_TYPE = -9999;
        
        public FixedFacetToolTip( final Control control )
        {
            super( control );
            setPopupDelay( 0 );
        }
        
        public void show( final Point location )
        {
            // The JFace ToolTip class does not support alternative methods of tool tip activation.
            // An enhancement request https://bugs.eclipse.org/bugs/show_bug.cgi?id=174844 tracks
            // this issue. When that enhancement request has been resolved, this hacky 
            // implementation should be replaced with something more sensible.
            
            final Event fakeEvent = new Event();
            fakeEvent.type = FAKE_EVENT_TYPE;
            fakeEvent.x = location.x;
            fakeEvent.y = location.y;
            
            try
            {
                final Method method
                    = ToolTip.class.getDeclaredMethod( "toolTipCreate", Event.class ); //$NON-NLS-1$
                
                method.setAccessible( true );
                method.invoke( this, fakeEvent );
            }
            catch( Exception e )
            {
                FacetUiPlugin.log( e );
            }
        }
        
        @Override
        protected final boolean shouldCreateToolTip( final Event event ) 
        {
            return ( event.type == FAKE_EVENT_TYPE );
        }
    }
    
    private final class ProblemsContentProvider

        implements IStructuredContentProvider

    {
        public Object[] getElements( final Object element )
        {
            return getFilteredProblems();
        }

        public void inputChanged( final Viewer viewer,
                                  final Object oldObject,
                                  final Object newObject ) {}
        
        public void dispose() {}
    }

    private final class ProblemsLabelProvider

        implements ITableLabelProvider

    {
        public String getColumnText( final Object element,
                                     final int column )
        {
            return ( (IStatus) element ).getMessage();
        }

        public Image getColumnImage( final Object element,
                                     final int column )
        {
        	final ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
	        final String imageType;
	        
	        if( ( (IStatus) element ).getSeverity() == IStatus.ERROR )
	        {
	        	imageType = ISharedImages.IMG_OBJS_ERROR_TSK;
	        }
	        else
	        {
	        	imageType = ISharedImages.IMG_OBJS_WARN_TSK;
	        }
	        
	        return sharedImages.getImage( imageType );
        }

        public boolean isLabelProperty( final Object obj,
                                        final String s )
        {
            return false;
        }

        public void dispose() {}
        public void addListener( final ILabelProviderListener listener ) {}
        public void removeListener( ILabelProviderListener listener ) {}
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String presetsLabel;
        public static String saveButtonLabel;
        public static String deleteButtonLabel;
        public static String savePresetDialogTitle;
        public static String savePresetDialogMessage;
        public static String facetColumnLabel;
        public static String versionColumnLabel;
        public static String couldNotDeselectFixedFacetMessage;
        public static String detailsTabLabel;
        public static String runtimesTabLabel;
        public static String lockMenuItem;
        public static String unlockMenuItem;
        public static String changeVersionMenuItem;
        
        static
        {
            initializeMessages( FacetsSelectionPanel.class.getName(), 
                                Resources.class );
        }
        
        public static String bind( final String msg,
                                   final Object arg1,
                                   final Object arg2,
                                   final Object arg3 )
        {
            return NLS.bind( msg, new Object[] { arg1, arg2, arg3 } );
        }
    }
    
}
