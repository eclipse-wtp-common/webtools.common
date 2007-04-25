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
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhalign;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhspan;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdwhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.SwtUtil.getPreferredWidth;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.common.project.facet.core.IActionConfig;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.ui.IDecorationsProvider;
import org.eclipse.wst.common.project.facet.ui.IWizardContext;
import org.eclipse.wst.common.project.facet.ui.internal.AbstractDataModel.IDataModelListener;
import org.eclipse.wst.common.project.facet.ui.internal.ChangeTargetedRuntimesDataModel.IRuntimeFilter;
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
    
    private final IDialogSettings settings;
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
    private final Menu popupMenu;
    private final MenuItem popupMenuConstraints;
    private final ComboBoxCellEditor ceditor;
    private final FixedFacetToolTip fixedFacetToolTip;
    private final TableViewer problemsView;
    private final RuntimesPanel runtimesPanel;
    private final Button showHideRuntimesButton;
    
    private final IWizardContext context;

    /**
     * Contains the <code>TableRowData</code> objects representing all of the
     * facets, regardless whether they are displayed or not.
     */

    private final List<TableRowData> data;
    private final Set<IProjectFacetVersion> base;
    private final Set<Action> actions;
    private Object oldSelection;

    private IStatus problems;
    private final List<Listener> listeners;
    private final List<ISelectionChangedListener> selectionListeners;
    
    private final ModifyFacetedProjectDataModel model;
    
    /**
     * Holds images used throughout the panel.
     */
    
    private final ImageRegistry imageRegistry;
    
    public interface IFilter 
    {
        boolean check( IProjectFacetVersion fv );
    }

    public FacetsSelectionPanel( final Composite parent,
                                 final int style,
                                 final IWizardContext context,
                                 final Set<IProjectFacetVersion> base,
                                 final ModifyFacetedProjectDataModel model )
    {
        super( parent, style );

        this.context = context;
        this.data = new ArrayList<TableRowData>();
        this.model = model;
        this.base = ( base == null ? new HashSet<IProjectFacetVersion>() : base );
        this.actions = new HashSet<Action>();
        this.oldSelection = null;
        this.problems = Status.OK_STATUS;
        this.listeners = new ArrayList<Listener>();
        this.selectionListeners = new ArrayList<ISelectionChangedListener>();
        
        for( IProjectFacet f : ProjectFacetsManager.getProjectFacets() )
        {
            try
            {
                this.data.add( new TableRowData( f ) );
            }
            catch( CoreException e )
            {
                FacetUiPlugin.log( e );
            }
        }
        
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
        
        syncWithPresetsModel( this.presetsCombo );
        
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

        this.popupMenu = new Menu( getShell(), SWT.POP_UP );
        this.popupMenuConstraints = new MenuItem( this.popupMenu, SWT.PUSH );
        this.popupMenuConstraints.setText( Resources.showConstraints );
        
        this.popupMenuConstraints.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    handleShowConstraints();
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
                    FacetsSelectionPanel.this.selectionChanged( e );
                }
            }
        );

        this.treeViewer.addCheckStateListener
        (
            new ICheckStateListener()
            {
                public void checkStateChanged( final CheckStateChangedEvent e )
                {
                    FacetsSelectionPanel.this.checkStateChanged( e );
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
        
        this.runtimesPanel 
            = new RuntimesPanel( this.sform1, SWT.NONE, 
                                 this.model.getTargetedRuntimesDataModel() );
        
        this.runtimesPanel.setLayoutData( gdhhint( gdhfill(), 80 ) );
        
        this.model.getTargetedRuntimesDataModel().addRuntimeFilter
        ( 
            new IRuntimeFilter()
            {
                public boolean check( final IRuntime runtime )
                {
                    for( IProjectFacetVersion fv : getSelectedProjectFacets() )
                    {
                        if( ! runtime.supports( fv ) )
                        {
                            return false;
                        }
                    }
                    
                    return true;
                }
            }
        );
        
        addProjectFacetsListener
        (
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    final ChangeTargetedRuntimesDataModel rdm
                        = getDataModel().getTargetedRuntimesDataModel();
                    
                    rdm.refreshTargetableRuntimes();
                }
            }
        );
        
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
        this.sform1.setMaximizedControl( this.sform2 );
        
        this.showHideRuntimesButton = new Button( this.topComposite, SWT.PUSH );
        this.showHideRuntimesButton.setText( Resources.showRuntimes );
        GridData gd = gdhalign( gdhspan( gd(), 4 ), GridData.END );
        gd = gdwhint( gd, getPreferredWidth( this.showHideRuntimesButton ) + 15 );
        this.showHideRuntimesButton.setLayoutData( gd );
        
        this.showHideRuntimesButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    handleShowHideRuntimes();
                }
            }
        );
        
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
        
        updateValidationDisplay();
        Dialog.applyDialogFont(parent);
        
        // Bind to the model.
        
        this.model.addListener
        ( 
            ModifyFacetedProjectDataModel.EVENT_FIXED_FACETS_CHANGED, 
            new IDataModelListener()
            {
                public void handleEvent()
                {
                    handleModelChangedEvent( ModifyFacetedProjectDataModel.EVENT_FIXED_FACETS_CHANGED );
                }
            }
        );

        this.model.addListener
        ( 
            ModifyFacetedProjectDataModel.EVENT_SELECTED_PRESET_CHANGED, 
            new IDataModelListener()
            {
                public void handleEvent()
                {
                    handleModelChangedEvent( ModifyFacetedProjectDataModel.EVENT_SELECTED_PRESET_CHANGED );
                }
            }
        );
        
        syncWithPresetsModel( this.presetsCombo );
        
        this.model.getTargetedRuntimesDataModel().addListener
        ( 
            ChangeTargetedRuntimesDataModel.EVENT_TARGETED_RUNTIMES_CHANGED,
            new IDataModelListener()
            {
                public void handleEvent()
                {
                    handleModelChangedEvent( ChangeTargetedRuntimesDataModel.EVENT_TARGETED_RUNTIMES_CHANGED );
                }
            }
        );
    }
    
    public ModifyFacetedProjectDataModel getDataModel()
    {
        return this.model;
    }
    
    public boolean isSelectionValid()
    {
        return ( this.problems.getSeverity() != IStatus.ERROR );
    }
    
    public Set<Action> getActions()
    {
        return this.actions;
    }
    
    public Action getAction( final Action.Type type,
                             final IProjectFacetVersion f )
    {
        return getAction( this.actions, type, f );
    }
    
    private static Action getAction( final Set<Action> actions,
                                     final Action.Type type,
                                     final IProjectFacetVersion fv )
    {
        for( Action action : actions )
        {
            if( action.getType() == type && action.getProjectFacetVersion() == fv )
            {
                return action;
            }
        }
        
        return null;
    }
    
    private static Action getAction( final Set<Action> actions,
                                     final Action.Type type,
                                     final IProjectFacet f )
    {
        for( Action action : actions )
        {
            if( action.getType() == type && 
                action.getProjectFacetVersion().getProjectFacet() == f )
            {
                return action;
            }
        }
        
        return null;
    }
    
    private Action createAction( final Set<Action> actions,
                                 final Action.Type type,
                                 final IProjectFacetVersion fv )
    {
        Action action = getAction( actions, type, fv );
        
        if( action == null )
        {
            Object config = null;
            
            if( fv.supports( this.base, type ) )
            {
                try
                {
                    final IProjectFacet f = fv.getProjectFacet();
                    
                    action = getAction( actions, type, f );
                    
                    if( action != null )
                    {
                        final IProjectFacetVersion current
                            = action.getProjectFacetVersion();
                        
                        if( fv.supports( this.base, type ) &&
                            current.supports( this.base, type ) &&
                            fv.getActionDefinition( this.base, type )
                              == current.getActionDefinition( this.base, type ) )
                        {
                            config = action.getConfig();
                            
                            IActionConfig c = null;
                            
                            if( config instanceof IActionConfig )
                            {
                                c = (IActionConfig) config;
                            }
                            else if( config != null )
                            {
                                final IAdapterManager m 
                                    = Platform.getAdapterManager();
                                
                                final String t
                                    = IActionConfig.class.getName();
                                
                                c = (IActionConfig) m.loadAdapter( config, t );
                            }
                            
                            if( c != null )
                            {
                                c.setVersion( fv );
                            }
                        }
                    }
                    
                    if( config == null )
                    {
                    	final String pjname = this.context.getProjectName();
                        config = this.context.getConfig(fv, type, pjname);
                    }
                }
                catch( CoreException e )
                {
                    FacetUiPlugin.log( e );
                }
            }

            action = new Action( type, fv, config );
        }
        
        return action;
    }
    
    public void setDefaultFacetsForRuntime( final IRuntime runtime )
    {
        final Set<IProjectFacetVersion> defaultFacets;
        
        if( runtime != null )
        {
            try
            {
                defaultFacets = runtime.getDefaultFacets( this.model.getFixedFacets() );
            }
            catch( CoreException e )
            {
                FacetUiPlugin.log( e );
                return;
            }
        }
        else
        {
            defaultFacets = new HashSet<IProjectFacetVersion>();
            
            for( IProjectFacet f : this.model.getFixedFacets() )
            {
                defaultFacets.add( f.getDefaultVersion() );
            }
        }
            
        setSelectedProjectFacets( defaultFacets );
        this.model.setSelectedPreset( null );
    }
    
    public Set<IProjectFacetVersion> getSelectedProjectFacets()
    {
        final Set<IProjectFacetVersion> set = new HashSet<IProjectFacetVersion>();

        for( TableRowData trd : this.data )
        {
            if( trd.isSelected() )
            {
                set.add( trd.getCurrentVersion() );
            }
        }

        return set;
    }

    public void setSelectedProjectFacets( final Set<IProjectFacetVersion> sel )
    {
        final List<TableRowData> toCheck = new ArrayList<TableRowData>();
        final List<TableRowData> needsCategoryRefresh = new ArrayList<TableRowData>();
        
        for( IProjectFacetVersion fv : sel )
        {
            final IProjectFacet f = fv.getProjectFacet();
            final TableRowData trd = findTableRowData( f, true );

            if( fv.getPluginId() == null )
            {
                trd.addUnknownVersion( fv );
            }
            
            trd.setSelected( true );
            trd.setCurrentVersion( fv );
            
            toCheck.add( trd );
            needsCategoryRefresh.add( trd );
        }

        for( TableRowData trd : this.data )
        {
            if( trd.isSelected() && ! sel.contains( trd.getCurrentVersion() ) )
            {
                trd.setSelected( false );
                needsCategoryRefresh.add( trd );
            }
        }

        refresh();
        
        this.treeViewer.setCheckedElements( toCheck.toArray() );
        
        for( TableRowData trd : needsCategoryRefresh )
        {
            refreshCategoryState( trd );
        }
        
        updateValidationDisplay();
    }
    
    public void setFixedProjectFacets( final Set<IProjectFacet> fixed )
    {
        this.model.setFixedFacets( fixed );
    }
    
    /**
     * @deprecated
     */
    
    public void selectPreset( final IPreset preset )
    {
        this.model.setSelectedPreset( preset.getId() );
    }
    
    public boolean setFocus()
    {
        return this.tree.setFocus();
    }
    
    public void addProjectFacetsListener( final Listener listener )
    {
        this.listeners.add( listener );
    }

    public void removeProjectFacetsListener( final Listener listener )
    {
        this.listeners.remove( listener );
    }

    private void notifyProjectFacetsListeners()
    {
        for( Listener listener : this.listeners )
        {
            listener.handleEvent( null );
        }
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
        final IStructuredSelection ss
            = (IStructuredSelection) this.treeViewer.getSelection();

        Object sel = ss.getFirstElement();

        if( sel instanceof TableRowData )
        {
            sel = ( (TableRowData) sel ).getProjectFacet();
        }

        if( sel == null )
        {
            return new StructuredSelection( new Object[ 0 ] );
        }
        else
        {
            return new StructuredSelection( sel );
        }
    }

    public void setSelection( final ISelection selection )
    {
        final IStructuredSelection ss = (IStructuredSelection) selection;
        final Object sel = ss.getFirstElement();
        final ISelection ts;

        if( sel == null )
        {
            ts = new StructuredSelection( new Object[ 0 ] );
        }
        else
        {
            if( sel instanceof IProjectFacet )
            {
                final TableRowData trd
                    = findTableRowData( (IProjectFacet) sel );

                ts = new StructuredSelection( trd );
            }
            else
            {
                ts = selection;
            }
        }

        this.treeViewer.setSelection( ts );
    }

    public void notifySelectionChangedListeners()
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

    private void selectionChanged( final SelectionChangedEvent event )
    {
        final Object selection
            = ( (IStructuredSelection) event.getSelection() ).getFirstElement();

        if( selection != this.oldSelection )
        {
            this.oldSelection = selection;
            refreshVersionsDropDown();
            notifySelectionChangedListeners();
        }
    }
    
    private void checkStateChanged( final CheckStateChangedEvent event )
    {
        final Object el = event.getElement();
        final boolean checked = event.getChecked();

        if( el instanceof TableRowData )
        {
            final TableRowData trd = (TableRowData) el;
            
            if( trd.isFixed() )
            {
                if( ! checked )
                {
                    this.treeViewer.setChecked( el, true );
                    
                    final String msg 
                        = NLS.bind( Resources.couldNotDeselectFixedFacetMessage,
                                    trd.getProjectFacet().getLabel() );                    

                    this.fixedFacetToolTip.setMessage( msg );
                    
                    final Point cursorLocation = getDisplay().getCursorLocation();
                    this.fixedFacetToolTip.show( this.tree.toControl( cursorLocation ) );
                }
                
                return;
            }
            
            trd.setSelected( checked );
            refreshCategoryState( trd );
        }
        else
        {
            final ContentProvider cp
                = (ContentProvider) this.treeViewer.getContentProvider();

            final Object[] children = cp.getChildren( el );
            int selected = 0;
            
            for( Object child : children )
            {
                final TableRowData trd = (TableRowData) child;
                
                if( ! trd.isFixed() )
                {
                    trd.setSelected( checked );
                    this.treeViewer.setChecked( trd, checked );
                }
                
                if( trd.isSelected() )
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
        }

        updateValidationDisplay();
        
        this.model.setSelectedPreset( null );
    }

    private void updateValidationDisplay()
    {
        final Set<IProjectFacetVersion> sel = getSelectedProjectFacets();
        final Set<Action> old = new HashSet<Action>( this.actions );
        this.actions.clear();
        
        // What has been removed?
        
        for( IProjectFacetVersion fv : this.base )
        {
            if( ! sel.contains( fv ) )
            {
                this.actions.add( createAction( old, Action.Type.UNINSTALL, fv ) );
            }
        }

        // What has been added?

        for( IProjectFacetVersion fv : sel )
        {
            if( ! this.base.contains( fv ) )
            {
                this.actions.add( createAction( old, Action.Type.INSTALL, fv ) );
            }
        }
        
        // Coalesce uninstall/install pairs into version change actions, if
        // possible.
        
        final Set<Action> toadd = new HashSet<Action>();
        final Set<Action> toremove = new HashSet<Action>();
        
        for( Action action1 : this.actions )
        {
            for( Action action2 : this.actions )
            {
                if( action1.getType() == Action.Type.UNINSTALL &&
                    action2.getType() == Action.Type.INSTALL )
                {
                    final IProjectFacetVersion f1 = action1.getProjectFacetVersion();
                    final IProjectFacetVersion f2 = action2.getProjectFacetVersion();
                    
                    if( f1.getProjectFacet() == f2.getProjectFacet() )
                    {
                        toremove.add( action1 );
                        toremove.add( action2 );
                        toadd.add( createAction( old, Action.Type.VERSION_CHANGE, f2 ) );
                    }
                }
            }
        }
        
        this.actions.removeAll( toremove );
        this.actions.addAll( toadd );
        
        this.problems = calculateProblems();
        this.problemsView.refresh();

        if( this.problems.isOK() )
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

        notifyProjectFacetsListeners();
    }
    
    private IStatus calculateProblems()
    {
        IStatus st = ProjectFacetsManager.check( this.base, this.actions );
        
        for( IProjectFacetVersion fv : this.base )
        {
            final IProjectFacet f = fv.getProjectFacet();
            
            String msg = null; 
            
            if( f.getPluginId() == null )
            {
                msg = NLS.bind( Resources.facetNotFound, f.getId() );
            }
            else if( fv.getPluginId() == null )
            {
                msg = NLS.bind( Resources.facetVersionNotFound, f.getId(), 
                                fv.getVersionString() );
            }
            
            if( msg != null )
            {
                final IStatus sub
                    = new Status( IStatus.WARNING, FacetUiPlugin.PLUGIN_ID, 0,
                                  msg, null );
                
                final IStatus[] existing = st.getChildren();
                final IStatus[] modified = new IStatus[ existing.length + 1 ];
                System.arraycopy( existing, 0, modified, 0, existing.length );
                modified[ existing.length ] = sub;
                
                st = new MultiStatus( FacetUiPlugin.PLUGIN_ID, 0, modified, 
                                      "", null ); //$NON-NLS-1$
            }
        }
        
        return st;
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
    
    public void syncWithPresetsModel( final Combo combo )
    {
        final List<IPreset> sortedPresets = new ArrayList<IPreset>();
        
        // Contents : model -> view

        final IDataModelListener modelToViewContentsListener = new IDataModelListener()
        {
            public void handleEvent()
            {
                synchronized( sortedPresets )
                {
                    sortedPresets.clear();
                    sortedPresets.addAll( FacetsSelectionPanel.this.model.getAvailablePresets() );
                    
                    Collections.sort
                    (
                        sortedPresets,
                        new Comparator<IPreset>()
                        {
                            public int compare( final IPreset p1, 
                                                final IPreset p2 ) 
                            {
                                if( p1 == p2 )
                                {
                                    return 0;
                                }
                                else
                                {
                                    return p1.getLabel().compareTo( p2.getLabel() );
                                }
                            }
                        }
                    );
                    
                    final IPreset selectedPreset 
                        = FacetsSelectionPanel.this.model.getSelectedPreset();
                    
                    combo.removeAll();
                    combo.add( Resources.customPreset );
                    
                    if( selectedPreset == null )
                    {
                        combo.select( 0 );
                    }
                    
                    for( IPreset preset : sortedPresets )
                    {
                        combo.add( preset.getLabel() );
                        
                        if( preset == selectedPreset )
                        {
                            combo.select( combo.getItemCount() - 1 );
                        }
                    }
                }
            }
        };

        this.model.addListener( ModifyFacetedProjectDataModel.EVENT_AVAILABLE_PRESETS_CHANGED,
                                modelToViewContentsListener );
        
        // Selection : model -> view
        
        this.model.addListener
        ( 
            ModifyFacetedProjectDataModel.EVENT_SELECTED_PRESET_CHANGED, 
            new IDataModelListener()
            {
                public void handleEvent()
                {
                    synchronized( sortedPresets )
                    {
                        final IPreset preset
                            = FacetsSelectionPanel.this.model.getSelectedPreset();
                        
                        final int index;
                        
                        if( preset == null )
                        {
                            index = -1;
                        }
                        else
                        {
                            index = sortedPresets.indexOf( preset );
                        }
                        
                        combo.select( index + 1 );
                        
                        handlePresetSelected();
                    }
                }
            }
        );
        
        // Selection : view -> model
        
        combo.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    synchronized( sortedPresets )
                    {
                        final int selection = combo.getSelectionIndex();
                        final String presetId;
                        
                        if( selection == 0 )
                        {
                            presetId = null;
                        }
                        else
                        {
                            presetId = sortedPresets.get( selection - 1 ).getId();
                        }
                        
                        FacetsSelectionPanel.this.model.setSelectedPreset( presetId );
                    }
                }
            }
        );
        
        // Trigger initial UI population.
        
        modelToViewContentsListener.handleEvent();
    }
    
    private void refreshCategoryState( final TableRowData trd )
    {
        final ICategory category = trd.getProjectFacet().getCategory();
        
        if( category != null )
        {
            int selected = 0;
    
            for( IProjectFacet f : category.getProjectFacets() )
            {
                final TableRowData ctrd = findTableRowData( f );
    
                if( ctrd.isSelected() )
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
    }
    
    private void refreshVersionsDropDown()
    {
        final TableRowData trd = getSelectedTableRowData();
        
        if( trd == null )
        {
            return;
        }
        
        final List<IProjectFacetVersion> versions = trd.getVersions();
        final String[] verstrs = new String[ versions.size() ];

        for( int i = 0, n = versions.size(); i < n; i++ )
        {
            final IProjectFacetVersion fv = versions.get( i );
            verstrs[ i ] = fv.getVersionString(); 
        }

        this.ceditor.setItems( verstrs );

        for( int i = 0, n = versions.size(); i < n; i++ )
        {
            if( versions.get( i ) == trd.getCurrentVersion() )
            {
                this.ceditor.setValue( new Integer( i ) );
                break;
            }
        }
    }

    private TableRowData getSelectedTableRowData()
    {
        final IStructuredSelection ssel 
            = (IStructuredSelection) this.treeViewer.getSelection();
        
        if( ssel != null && ! ssel.isEmpty() )
        {
            final Object obj = ssel.getFirstElement();
            
            if( obj instanceof TableRowData )
            {
                return (TableRowData) obj;
            }
        }

        return null;
    }
    
    private TableRowData findTableRowData( final IProjectFacet f )
    {
        return findTableRowData( f, false );
    }

    private TableRowData findTableRowData( final IProjectFacet f,
                                           final boolean createIfNecessary )
    {
        for( TableRowData trd : this.data )
        {
            if( trd.getProjectFacet() == f )
            {
                return trd;
            }
        }
        
        if( createIfNecessary )
        {
            try
            {
                final TableRowData trd = new TableRowData( f );
                this.data.add( trd );
                return trd;
            }
            catch( CoreException e )
            {
                FacetUiPlugin.log( e );
            }
        }

        throw new IllegalStateException();
    }

    private void handleMouseDownEvent( final Event event )
    {
        final List<TreeItem> items = getAllTreeItems();
        
        TreeItem onItem = null;

        for( TreeItem item : items )
        {
            if( item.getBounds( 0 ).contains( event.x, event.y ) )
            {
                onItem = item;
                break;
            }

            if( item.getBounds( 1 ).contains( event.x, event.y ) )
            {
                this.tree.setSelection( new TreeItem[] { item } );
                this.treeViewer.editElement( item.getData(), 1 );
                break;
            }
        }
        
        if( onItem != null && onItem.getData() instanceof TableRowData )
        {
            final TableRowData trd = (TableRowData) onItem.getData();
            final IProjectFacetVersion fv = trd.getCurrentVersion();
            final IConstraint c = fv.getConstraint();
            
            if( c.getType() == IConstraint.Type.AND && 
                c.getOperands().size() == 0 )
            {
                this.popupMenuConstraints.setEnabled( false );
            }
            else
            {
                this.popupMenuConstraints.setEnabled( true );
            }
            
            this.tree.setMenu( this.popupMenu );
        }
        else
        {
            this.tree.setMenu( null );
        }
    }
    
    private void handlePaintItemEvent( final Event event )
    {
        final TreeItem item = (TreeItem) event.item;
        final Object itemData = item.getData();

        if( itemData instanceof TableRowData && event.index == 1 )
        {
            final TableRowData trd = (TableRowData) itemData;
            
            if( trd.getVersions().size() > 1 )
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
    }
    
    private void handleShowConstraints()
    {
        final TreeItem[] items = this.tree.getSelection();
        if( items.length != 1 ) throw new IllegalStateException();
        final TreeItem item = items[ 0 ];
        final TableRowData trd = (TableRowData) item.getData();
        final IProjectFacetVersion fv = trd.getCurrentVersion();
        
        final Rectangle bounds = item.getBounds();
        
        Point location = new Point( bounds.x, bounds.y + bounds.height );
        location = this.tree.toDisplay( location );
        
        final ConstraintDisplayDialog dialog 
            = new ConstraintDisplayDialog( getShell(), location,
                                           fv.getConstraint() );

        dialog.open();
    }
    
    private void handlePresetSelected()
    {
        final IPreset preset = this.model.getSelectedPreset();
        
        if( preset != null )
        {
            final Set<TableRowData> selected = new HashSet<TableRowData>();
            
            for( IProjectFacetVersion fv : preset.getProjectFacets() )
            {
                final TableRowData trd = findTableRowData( fv.getProjectFacet() );
                
                if( ! trd.isSelected() )
                {
                    this.treeViewer.setChecked( trd, true );
                    trd.setSelected( true );
                    refreshCategoryState( trd );
                }
                
                if( trd.getCurrentVersion() != fv )
                {
                    trd.setCurrentVersion( fv );
                    this.treeViewer.update( trd, null );
                }
                
                selected.add( trd );
            }

            for( TableRowData trd : this.data )
            {
                if( ! selected.contains( trd ) )
                {
                    this.treeViewer.setChecked( trd, false );
                    trd.setSelected( false );
                    refreshCategoryState( trd );
                }
            }

            updateValidationDisplay();
        }
    }
    
    private void handleSavePreset()
    {
        final Set<IProjectFacetVersion> facets = getSelectedProjectFacets();
        final IPreset preset = SavePresetDialog.showDialog( getShell(), facets );
        
        if( preset != null )
        {
            this.model.refreshAvailablePresets();
            this.model.setSelectedPreset( preset.getId() );
        }
    }
    
    private void handleDeletePreset()
    {
        final IPreset preset = this.model.getSelectedPreset();
        this.model.setSelectedPreset( null );
        ProjectFacetsManager.deletePreset( preset );
        this.model.refreshAvailablePresets();
    }
    
    private void handleShowHideRuntimes()
    {
        if( this.sform1.getMaximizedControl() == null )
        {
            this.sform1.setMaximizedControl( this.sform2 );
            this.showHideRuntimesButton.setText( Resources.showRuntimes );
        }
        else
        {
            this.sform1.setMaximizedControl( null );
            this.showHideRuntimesButton.setText( Resources.hideRuntimes );
        }
    }
    
    private void handleModelChangedEvent( final String event )
    {
        if( event.equals( ModifyFacetedProjectDataModel.EVENT_FIXED_FACETS_CHANGED ) )
        {
            for( TableRowData trd : this.data )
            {
                trd.setFixed( false );
            }

            for( IProjectFacet f : this.model.getFixedFacets() )
            {
                final TableRowData trd = findTableRowData( f, true );
                
                trd.setFixed( true );
                trd.setSelected( true );
                this.treeViewer.setChecked( trd, true );
            }

            refresh();
            updateValidationDisplay();
        }
        else if( event.equals( ModifyFacetedProjectDataModel.EVENT_SELECTED_PRESET_CHANGED ) )
        {
            final IPreset preset = this.model.getSelectedPreset();
            
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
        else if( event.equals( ChangeTargetedRuntimesDataModel.EVENT_TARGETED_RUNTIMES_CHANGED ) )
        {
            refresh();
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
    
    private final class TableRowData
    {
        private IProjectFacet f;
        private List<IProjectFacetVersion> allVersionsSorted;
        private IProjectFacetVersion current;
        private boolean isSelected;
        private boolean isFixed;

        public TableRowData( final IProjectFacet f )
        
            throws CoreException
            
        {
            this.f = f;
            // TODO: Remove this unnecessary copy.
            this.allVersionsSorted = new ArrayList<IProjectFacetVersion>( f.getSortedVersions( false ) );
            this.current = f.getDefaultVersion();
            this.isSelected = false;
            this.isFixed = false;
        }

        public IProjectFacet getProjectFacet()
        {
            return this.f;
        }

        public List<IProjectFacetVersion> getVersions()
        {
            final Set<IProjectFacetVersion> versions
                = FacetsSelectionPanel.this.model.getAvailableFacets().get( this.f );
            
            if( versions == null )
            {
                return Collections.emptyList();
            }
            else
            {
                final List<IProjectFacetVersion> sortedVersions 
                    = new ArrayList<IProjectFacetVersion>( this.allVersionsSorted );
                
                sortedVersions.retainAll( versions );
                
                return sortedVersions;
            }
        }
        
        public void addUnknownVersion( final IProjectFacetVersion fv )
        {
            try
            {
                final Comparator<String> c = this.f.getVersionComparator();
                boolean added = false;
                
                for( int i = 0, n = this.allVersionsSorted.size(); i < n; i++ )
                {
                    final IProjectFacetVersion x = this.allVersionsSorted.get( i );
                    
                    if( c.compare( x.getVersionString(), fv.getVersionString() ) < 0 )
                    {
                        this.allVersionsSorted.add( i, fv );
                        added = true;
                        break;
                    }
                }
                
                if( ! added )
                {
                    this.allVersionsSorted.add( fv );
                }
            }
            catch( CoreException e )
            {
                FacetUiPlugin.log( e );
            }
        }

        public IProjectFacetVersion getCurrentVersion()
        {
            final Set<IProjectFacetVersion> versions
                = FacetsSelectionPanel.this.model.getAvailableFacets().get( this.f );
            
            if( versions == null )
            {
                if( this.current != null )
                {
                    this.current = null;
                }
            }
            else
            {
                if( ! versions.contains( this.current ) )
                {
                    this.current = getVersions().get( 0 );
                }
            }

            return this.current;
        }

        public void setCurrentVersion( final IProjectFacetVersion fv )
        {
            this.current = fv;
        }

        public boolean isSelected()
        {
            if( getVersions().isEmpty() )
            {
                this.isSelected = false;
            }

            return this.isSelected;
        }

        public void setSelected( final boolean isSelected )
        {
            this.isSelected = isSelected;
        }

        public boolean isFixed()
        {
            return this.isFixed;
        }

        public void setFixed( final boolean isFixed )
        {
            this.isFixed = isFixed;
        }

        public boolean isVisible()
        {
            return ! getVersions().isEmpty();
        }
        
        public String toString()
        {
            return this.current.toString();
        }
    }

    private final class ContentProvider

        implements ITreeContentProvider

    {
        public Object[] getElements( final Object element )
        {
            final List<Object> list = new ArrayList<Object>();
            final Set<ICategory> categories = ProjectFacetsManager.getCategories();

            for( ICategory cat : categories )
            {
                boolean visible = false;

                for( IProjectFacet f : cat.getProjectFacets() )
                {
                    if( findTableRowData( f ).isVisible() )
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

            for( TableRowData trd : FacetsSelectionPanel.this.data )
            {
                if( trd.getProjectFacet().getCategory() == null && trd.isVisible() )
                {
                    list.add( trd );
                }
            }

            return list.toArray();
        }

        public Object[] getChildren( final Object parent )
        {
            if( parent instanceof ICategory )
            {
                final ICategory category = (ICategory) parent;
                final List<TableRowData> trds = new ArrayList<TableRowData>();

                for( IProjectFacet f : category.getProjectFacets() )
                {
                    final TableRowData trd = findTableRowData( f );

                    if( trd.isVisible() )
                    {
                        trds.add( trd );
                    }
                }

                return trds.toArray();
            }
            else
            {
                return new Object[ 0 ];
            }
        }

        public Object getParent( final Object element )
        {
            if( element instanceof TableRowData )
            {
                final IProjectFacet f 
                    = ( (TableRowData) element ).getProjectFacet();

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
                final TableRowData trd = (TableRowData) element;

                switch( column )
                {
                    case 0:
                    {
                        return trd.getProjectFacet().getLabel();
                    }
                    case 1:
                    {
                        return trd.getCurrentVersion().getVersionString();
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
            
            String id;
            IAdaptable obj;
            boolean isFixed = false;

            if( element instanceof TableRowData )
            {
                final TableRowData trd = (TableRowData) element;
                final IProjectFacet f = trd.getProjectFacet();

                isFixed = trd.isFixed;
                id = ( isFixed ? "F:" : "f:" ) + f.getId(); //$NON-NLS-1$ //$NON-NLS-2$
                obj = f;
            }
            else
            {
                id = "c:" + ( (ICategory) element ).getId(); //$NON-NLS-1$
                obj = (IAdaptable) element;
            }
            
            Image image = getImageRegistry().get( id );
            
            if( image == null )
            {
                final IDecorationsProvider decprov
                    = (IDecorationsProvider) obj.getAdapter( IDecorationsProvider.class );
                
                ImageDescriptor imgdesc = decprov.getIcon();
                
                if( isFixed )
                {
                    imgdesc = new FixedFacetImageDescriptor( imgdesc );
                }
                
                getImageRegistry().put( id, imgdesc );
                image = getImageRegistry().get( id );
            }

            return image;
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
        public Object getValue( final Object element,
                                final String property )
        {
            final TableRowData trd = (TableRowData) element;

            if( property.equals( VERSION_COLUMN ) )
            {
                final List<IProjectFacetVersion> versions = trd.getVersions();

                for( int i = 0, n = versions.size(); i < n; i++ )
                {
                    if( versions.get( i ) == trd.getCurrentVersion() )
                    {
                        return new Integer( i );
                    }
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
                   element instanceof TableRowData &&
                   ( (TableRowData) element ).getVersions().size() > 1;
        }

        public void modify( final Object element,
                            final String property,
                            final Object value )
        {
            final TreeItem item = (TreeItem) element;
            final TableRowData trd = (TableRowData) item.getData();

            if( property.equals( VERSION_COLUMN ) )
            {
                final int index = ( (Integer) value ).intValue();

                if( index != -1 )
                {
                    final IProjectFacetVersion fv = trd.getVersions().get( index );
                    
                    if( trd.getCurrentVersion() != fv )
                    {
                        trd.setCurrentVersion( fv );
                        FacetsSelectionPanel.this.treeViewer.update( trd, null );
                        
                        if( trd.isSelected() )
                        {
                            FacetsSelectionPanel.this.model.setSelectedPreset( null );
                        }
    
                        updateValidationDisplay();
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
            if( obj instanceof TableRowData )
            {
                return ( (TableRowData) obj ).getProjectFacet().getLabel();
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
            final TreeItem treeItem = getTreeItem( event.x, event.y );
            String description = null;
            
            if( treeItem != null && treeItem.getBounds( 0 ).contains( event.x, event.y ) )
            {
                final Object treeItemData = treeItem.getData();
                
                if( treeItemData instanceof TableRowData )
                {
                    final IProjectFacetVersion fv 
                        = ( (TableRowData) treeItemData ).getCurrentVersion();
                    
                    description = fv.getProjectFacet().getDescription();
                }
            }
            
            return ( description != null && description.trim().length() > 0 );
        }

        @Override
        protected String getToolTipTitle( final Event event )
        {
            final TableRowData trd = (TableRowData) getTreeItem( event.x, event.y ).getData();
            return trd.getCurrentVersion().toString();
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

            final TableRowData trd = (TableRowData) getTreeItem( event.x, event.y ).getData();
            label.setText( trd.getCurrentVersion().getProjectFacet().getDescription() );
            
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
            return FacetsSelectionPanel.this.problems.getChildren();
        }

        public void dispose() { }

        public void inputChanged( final Viewer viewer,
                                  final Object oldObject,
                                  final Object newObject ) {}
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
        public static String customPreset;
        public static String saveButtonLabel;
        public static String deleteButtonLabel;
        public static String savePresetDialogTitle;
        public static String savePresetDialogMessage;
        public static String facetColumnLabel;
        public static String versionColumnLabel;
        public static String showConstraints;
        public static String showRuntimes;
        public static String hideRuntimes;
        public static String couldNotDeselectFixedFacetMessage;
        public static String facetNotFound;
        public static String facetVersionNotFound;
        
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
