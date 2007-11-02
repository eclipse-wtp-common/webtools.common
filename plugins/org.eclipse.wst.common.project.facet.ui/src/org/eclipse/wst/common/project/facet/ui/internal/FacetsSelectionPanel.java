/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    David Schneider, david.schneider@unisys.com - [142500] WTP properties pages fonts don't follow Eclipse preferences
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
import static org.eclipse.wst.common.project.facet.ui.internal.util.SwtUtil.getPreferredWidth;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
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
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetsChangedEvent;
import org.eclipse.wst.common.project.facet.ui.IDecorationsProvider;
import org.eclipse.wst.common.project.facet.ui.ModifyFacetedProjectWizard;
import org.eclipse.wst.common.project.facet.ui.internal.util.BasicToolTip;
import org.eclipse.wst.common.project.facet.ui.internal.util.HeaderToolTip;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetsSelectionPanel

    extends Composite
    implements ISelectionProvider

{
    private static final String FACET_COLUMN = "facet"; //$NON-NLS-1$
    private static final String VERSION_COLUMN = "version"; //$NON-NLS-1$
    private static final String WIDTH = "width"; //$NON-NLS-1$
    private static final String HEIGHT = "height"; //$NON-NLS-1$
    private static final String CW_FACET = "cw.facet"; //$NON-NLS-1$
    private static final String CW_VERSION = "cw.version"; //$NON-NLS-1$
    private static final String SASH1W1 = "sash.1.weight.1"; //$NON-NLS-1$
    private static final String SASH1W2 = "sash.1.weight.2"; //$NON-NLS-1$
    private static final String SASH2W1 = "sash.2.weight.1"; //$NON-NLS-1$
    private static final String SASH2W2 = "sash.2.weight.2"; //$NON-NLS-1$
    
    private static final String IMG_ERROR = "##error##"; //$NON-NLS-1$
    private static final String IMG_WARNING = "##warning##"; //$NON-NLS-1$
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
    private final TreeColumn colFacet;
    private final TreeColumn colVersion;
    private final ComboBoxCellEditor ceditor;
    private final FixedFacetToolTip fixedFacetToolTip;
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
        
        URL url = bundle.getEntry( "images/error.gif" ); //$NON-NLS-1$
        this.imageRegistry.put( IMG_ERROR, ImageDescriptor.createFromURL( url ) );

        url = bundle.getEntry( "images/warning.gif" ); //$NON-NLS-1$
        this.imageRegistry.put( IMG_WARNING, ImageDescriptor.createFromURL( url ) );

        url = bundle.getEntry( "images/down-arrow.gif" ); //$NON-NLS-1$
        this.imageRegistry.put( IMG_DOWN_ARROW, ImageDescriptor.createFromURL( url ) );

        // Read the dialog settings.

        final IDialogSettings root
            = FacetUiPlugin.getInstance().getDialogSettings();

        IDialogSettings temp = root.getSection( getClass().getName() );

        if( temp == null )
        {
            temp = root.addNewSection( getClass().getName() );
        }
        
        if( temp.get( WIDTH ) == null ) temp.put( WIDTH, 600 );
        if( temp.get( HEIGHT ) == null ) temp.put( HEIGHT, 300 );
        if( temp.get( SASH1W1 ) == null ) temp.put( SASH1W1, 60 );
        if( temp.get( SASH1W2 ) == null ) temp.put( SASH1W2, 40 );
        if( temp.get( SASH2W1 ) == null ) temp.put( SASH2W1, 70 );
        if( temp.get( SASH2W2 ) == null ) temp.put( SASH2W2, 30 );
        
        this.settings = temp;

        // Layout the panel.

        final GridLayout layout = new GridLayout( 1, false );
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        
        setLayout( layout );
        
        final GridData topgd = gdfill();
        topgd.heightHint = this.settings.getInt( HEIGHT );
        topgd.widthHint = this.settings.getInt( WIDTH );    
        
        this.topComposite = new Composite( this, SWT.NONE );
        this.topComposite.setLayout( new GridLayout( 4, false ) );
        this.topComposite.setLayoutData( topgd );
        
        this.topComposite.addListener
        (
            SWT.Resize,
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    final Point size 
                        = FacetsSelectionPanel.this.topComposite.getSize();
                    
                    FacetsSelectionPanel.this.settings.put( WIDTH, size.x );
                    FacetsSelectionPanel.this.settings.put( HEIGHT, size.y );
                }
            }
        );

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
        
        this.sform1 = new SashForm( this.topComposite, SWT.HORIZONTAL | SWT.SMOOTH );
        this.sform1.setLayoutData( gdhspan( gdfill(), 4 ) );
        
        this.sform2 = new SashForm( this.sform1, SWT.VERTICAL | SWT.SMOOTH );
        this.sform2.setLayoutData( gdhspan( gdfill(), 4 ) );
        
        this.treeViewer = new CheckboxTreeViewer( this.sform2, SWT.BORDER );
        this.tree = this.treeViewer.getTree();
        
        this.tree.setHeaderVisible( true );

        this.ceditor = new ComboBoxCellEditor( this.tree, new String[ 0 ], SWT.READ_ONLY );

        this.treeViewer.setColumnProperties( new String[] { FACET_COLUMN, VERSION_COLUMN } );
        this.treeViewer.setCellModifier( new CellModifier() );
        this.treeViewer.setCellEditors( new CellEditor[] { null, this.ceditor } );

        this.treeViewer.setContentProvider( new ContentProvider() );
        this.treeViewer.setLabelProvider( new LabelProvider() );
        this.treeViewer.setSorter( new Sorter() );
        
        this.colFacet = new TreeColumn( this.tree, SWT.NONE );
        this.colFacet.setText( Resources.facetColumnLabel );
        this.colFacet.setResizable( true );
        
        if( this.settings.get( CW_FACET ) == null )
        {
            this.settings.put( CW_FACET, computeDefaultFacetColumnWidth() );
        }
        
        this.colFacet.setWidth( this.settings.getInt( CW_FACET ) );
        
        this.colFacet.addListener
        (
            SWT.Resize,
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    FacetsSelectionPanel.this.settings.put( CW_FACET, FacetsSelectionPanel.this.colFacet.getWidth() );
                }
            }
        );

        this.colVersion = new TreeColumn( this.tree, SWT.NONE );
        this.colVersion.setText( Resources.versionColumnLabel );
        this.colVersion.setResizable( true );
        
        if( this.settings.get( CW_VERSION ) == null )
        {
            this.settings.put( CW_VERSION, computeDefaultVersionColumnWidth() );
        }

        this.colVersion.setWidth( this.settings.getInt( CW_VERSION ) );
        
        this.colVersion.addListener
        (
            SWT.Resize,
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    FacetsSelectionPanel.this.settings.put( CW_VERSION, FacetsSelectionPanel.this.colVersion.getWidth() );
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
                    FacetsSelectionPanel.this.handleSelectionChangedEvent( e );
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
            SWT.MouseDown,
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    handleMouseDownEvent( event );
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
        
        this.problemsView = new TableViewer( this.sform2, SWT.BORDER );
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

        final int[] weights2
            = new int[] { this.settings.getInt( SASH2W1 ),
                          this.settings.getInt( SASH2W2 ) };

        this.sform2.setWeights( weights2 );
        
        final TabFolder tabFolder = new TabFolder( this.sform1, SWT.NONE );
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

        final int[] weights1
            = new int[] { this.settings.getInt( SASH1W1 ),
                          this.settings.getInt( SASH1W2 ) };
    
        this.sform1.setWeights( weights1 );
        
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
        
        Dialog.applyDialogFont(parent);
        
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
            IFacetedProjectEvent.Type.VALIDATION_PROBLEMS_CHANGED
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
        return new StructuredSelection( this.selection );
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
        refreshVersionsDropDown();
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
        
        int selected = 0;

        for( IProjectFacet f : category.getProjectFacets() )
        {
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
        else if( selected == category.getProjectFacets().size() )
        {
            this.treeViewer.setChecked( category, true );
            this.treeViewer.setGrayed( category, false );
        }
        else
        {
            this.treeViewer.setGrayChecked( category, true );
        }
    }
    
    private void refreshVersionsDropDown()
    {
        final IProjectFacet f = getSelectedProjectFacet();
        
        if( f == null )
        {
            return;
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
    
    private void handleSelectionChangedEvent( final SelectionChangedEvent event )
    {
        Object selection = ( (IStructuredSelection) event.getSelection() ).getFirstElement();

        if( selection != null && selection instanceof IProjectFacet )
        {
            selection = getSelectedVersion( (IProjectFacet ) selection );
        }
        
        if( selection != this.selection )
        {
            this.selection = selection;
            
            refreshVersionsDropDown();
            notifySelectionChangedListeners();
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
    }

    private void handleMouseDownEvent( final Event event )
    {
        final List<TreeItem> items = getAllTreeItems();
        
        for( TreeItem item : items )
        {
            if( item.getBounds( 1 ).contains( event.x, event.y ) )
            {
                this.tree.setSelection( new TreeItem[] { item } );
                this.treeViewer.editElement( item.getData(), 1 );
            }
        }
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
                
                final int columnWidth = this.colVersion.getWidth();
                final int itemHeight = this.tree.getItemHeight();
                
                int x, y;
                
                x = event.x + columnWidth - arrowImageBounds.width - 10;
                y = event.y;
                event.gc.setBackground( item.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
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
        this.problemsView.refresh();

        if( this.fpjwc.validate().isOK() )
        {
            if( this.sform2.getMaximizedControl() == null )
            {
                this.sform2.setMaximizedControl( this.tree );
            }
        }
        else
        {
            if( this.sform2.getMaximizedControl() != null )
            {
                this.sform2.setMaximizedControl( null );
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
            {
                refresh();
                
                break;
            }
            case TARGETED_RUNTIMES_CHANGED:
            {
                refresh();
                break;
            }
        }
    }
    
    private List<TreeItem> getAllTreeItems()
    {
        final List<TreeItem> result = new ArrayList<TreeItem>();
        getAllTreeItems( this.tree.getItems(), result );
        return result;
    }

    private static void getAllTreeItems( final TreeItem[] items,
                                         final List<TreeItem> result )
    {
        for( TreeItem item : items )
        {
            result.add( item );
            getAllTreeItems( item.getItems(), result );
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
    
    /*private TreeItem getTreeItem( final Object modelObject )
    {
        for( TreeItem item : getAllTreeItems() )
        {
            if( item.getData().equals( modelObject ) )
            {
                return item;
            }
        }
        
        return null;
    }*/
    
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

    private final class LabelProvider

        implements ITableLabelProvider

    {
        public String getColumnText( final Object element,
                                     final int column )
        {
            if( element instanceof ICategory )
            {
                if( column == 0 )
                {
                    return ( (ICategory) element ).getLabel();
                }
                else
                {
                    return ""; //$NON-NLS-1$
                }
            }
            else
            {
                final IProjectFacet f = (IProjectFacet) element;

                switch( column )
                {
                    case 0:
                    {
                        return f.getLabel();
                    }
                    case 1:
                    {
                        return getSelectedVersion( f ).getVersionString();
                    }
                    default:
                    {
                        throw new IllegalStateException();
                    }
                }
            }
        }

        public Image getColumnImage( final Object element,
                                     final int column )
        {
            if( column != 0 )
            {
                return null;
            }
            
            if( element instanceof IProjectFacet )
            {
                return getImage( (IProjectFacet) element, true );
            }
            else
            {
                return getImage( (ICategory) element );
            }
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
    
    private final class CellModifier

        implements ICellModifier

    {
        private final IFacetedProjectWorkingCopy fpjwc;
        
        public CellModifier()
        {
            this.fpjwc = getFacetedProjectWorkingCopy();
        }
        
        public Object getValue( final Object element,
                                final String property )
        {
            final IProjectFacet f = (IProjectFacet) element;

            if( property.equals( VERSION_COLUMN ) )
            {
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
            else
            {
                throw new IllegalStateException();
            }
        }

        public boolean canModify( final Object element,
                                  final String property )
        {
            return property.equals( VERSION_COLUMN ) &&
                   element instanceof IProjectFacet &&
                   this.fpjwc.getAvailableVersions( (IProjectFacet ) element ).size() > 1;
        }

        public void modify( final Object element,
                            final String property,
                            final Object value )
        {
            final TreeItem item = (TreeItem) element;
            final IProjectFacet f = (IProjectFacet) item.getData();

            if( property.equals( VERSION_COLUMN ) )
            {
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
            else
            {
                throw new IllegalStateException();
            }
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
            return FacetsSelectionPanel.this.fpjwc.validate().getChildren();
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
            final IStatus st = (IStatus) element;
            
            if( st.getSeverity() == IStatus.ERROR )
            {
                return getImageRegistry().get( IMG_ERROR );
            }
            else
            {
                return getImageRegistry().get( IMG_WARNING );
            }
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
