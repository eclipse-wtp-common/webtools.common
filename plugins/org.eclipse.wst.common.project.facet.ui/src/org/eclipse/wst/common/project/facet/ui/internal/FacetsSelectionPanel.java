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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IFontProvider;
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
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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
import org.eclipse.wst.common.project.facet.ui.IWizardContext;
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
    private static final Font FIXED_FONT;
    
    static
    {
        final FontData system 
            = Display.getCurrent().getSystemFont().getFontData()[ 0 ];
        
        final FontData italic 
            = new FontData( system.getName(), system.getHeight(), SWT.BOLD );
        
        FIXED_FONT = new Font( Display.getCurrent(), italic );
    }

    private final IDialogSettings settings;
    private final SashForm sform1;
    private final SashForm sform2;
    private final Label presetsLabel;
    private final Combo presetsCombo;
    private final Button savePresetButton;
    private final Button deletePresetButton;
    private final CheckboxTreeViewer tree;
    private final TreeColumn colFacet;
    private final TreeColumn colVersion;
    private final Menu popupMenu;
    private final MenuItem popupMenuConstraints;
    private final ComboBoxCellEditor ceditor;
    private final TableViewer problemsView;
    private final RuntimesPanel runtimesPanel;
    
    private final IWizardContext context;

    /**
     * Contains the <code>TableRowData</code> objects representing all of the
     * facets, regardless whether they are displayed or not.
     */

    private final ArrayList data;
    private final HashSet fixed;
    private final Set base;
    private final HashSet actions;
    private final ArrayList presets;

    private IStatus problems;
    private final HashSet filters;
    private final ArrayList listeners;
    private final ArrayList selectionListeners;
    
    public interface IFilter 
    {
        boolean check( IProjectFacetVersion fv );
    }

    public FacetsSelectionPanel( final Composite parent,
                                 final int style,
                                 final IWizardContext context,
                                 final Set base )
    {
        super( parent, style );

        this.context = context;
        this.data = new ArrayList();
        this.fixed = new HashSet();
        this.base = ( base == null ? new HashSet() : base );
        this.actions = new HashSet();
        this.presets = new ArrayList();
        this.problems = Status.OK_STATUS;
        this.filters = new HashSet();
        this.listeners = new ArrayList();
        this.selectionListeners = new ArrayList();
        
        for( Iterator itr = ProjectFacetsManager.getProjectFacets().iterator();
             itr.hasNext(); )
        {
            try
            {
                this.data.add( new TableRowData( (IProjectFacet) itr.next() ) );
            }
            catch( CoreException e )
            {
                FacetUiPlugin.log( e );
            }
        }

        // Read the dialog settings.

        final IDialogSettings root
            = FacetUiPlugin.getInstance().getDialogSettings();

        IDialogSettings temp = root.getSection( getClass().getName() );

        if( temp == null )
        {
            temp = root.addNewSection( getClass().getName() );
        }
        
        if( temp.get( CW_FACET ) == null ) temp.put( CW_FACET, 200 );
        if( temp.get( CW_VERSION ) == null ) temp.put( CW_VERSION, 100 );
        if( temp.get( SASH1W1 ) == null ) temp.put( SASH1W1, 60 );
        if( temp.get( SASH1W2 ) == null ) temp.put( SASH1W2, 40 );
        if( temp.get( SASH2W1 ) == null ) temp.put( SASH2W1, 70 );
        if( temp.get( SASH2W2 ) == null ) temp.put( SASH2W2, 30 );

        this.settings = temp;

        // Layout the panel.

        setLayout( new GridLayout( 1, false ) );
        
        this.sform1 = new SashForm( this, SWT.HORIZONTAL | SWT.SMOOTH );
        this.sform1.setLayoutData( gdfill() );

        final Composite composite = new Composite( this.sform1, SWT.NONE );
        composite.setLayoutData( gdhfill() );
        
        final GridLayout layout = new GridLayout( 4, false );
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout( layout );
        
        this.presetsLabel = new Label( composite, SWT.NONE );
        this.presetsLabel.setText( "Presets: " );
        
        this.presetsCombo = new Combo( composite, SWT.READ_ONLY );
        this.presetsCombo.setLayoutData( gdhfill() );

        this.presetsCombo.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e ) 
                {
                    handlePresetSelected();
                }
            }
        );
        
        this.savePresetButton = new Button( composite, SWT.PUSH );
        this.savePresetButton.setText( "Save" );
        this.savePresetButton.setLayoutData( whint( new GridData(), 60 ) );
        
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

        this.deletePresetButton = new Button( composite, SWT.PUSH );
        this.deletePresetButton.setText( "Delete" );
        this.deletePresetButton.setLayoutData( whint( new GridData(), 60 ) );
        
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
        
        refreshPresetsCombo();
        
        this.sform2 = new SashForm( composite, SWT.VERTICAL | SWT.SMOOTH );
        this.sform2.setLayoutData( hspan( gdfill(), 4 ) );
        
        this.tree = new CheckboxTreeViewer( this.sform2, SWT.BORDER );
        this.tree.getTree().setHeaderVisible( true );

        this.ceditor
            = new ComboBoxCellEditor( this.tree.getTree(), new String[ 0 ],
                                      SWT.READ_ONLY );

        this.tree.setColumnProperties( new String[] { "facet", "version" } );
        this.tree.setCellModifier( new CellModifier() );
        this.tree.setCellEditors( new CellEditor[] { null, this.ceditor } );

        this.tree.setContentProvider( new ContentProvider() );
        this.tree.setLabelProvider( new LabelProvider() );
        this.tree.setSorter( new Sorter() );
        
        this.colFacet = new TreeColumn( this.tree.getTree(), SWT.NONE );
        this.colFacet.setText( "Project Facet" );
        this.colFacet.setWidth( this.settings.getInt( CW_FACET ) );
        this.colFacet.setResizable( true );
        
        this.colFacet.addListener
        (
            SWT.Resize,
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    settings.put( CW_FACET, colFacet.getWidth() );
                }
            }
        );

        this.colVersion = new TreeColumn( this.tree.getTree(), SWT.NONE );
        this.colVersion.setText( "Version" );
        this.colVersion.setWidth( this.settings.getInt( CW_VERSION ) );
        this.colVersion.setResizable( true );

        this.colVersion.addListener
        (
            SWT.Resize,
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    settings.put( CW_VERSION, colVersion.getWidth() );
                }
            }
        );

        this.popupMenu = new Menu( getShell(), SWT.POP_UP );
        this.popupMenuConstraints = new MenuItem( this.popupMenu, SWT.PUSH );
        this.popupMenuConstraints.setText( "Show Constraints..." );
        
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

        this.tree.setInput( new Object() );

        this.tree.addSelectionChangedListener
        (
            new ISelectionChangedListener()
            {
                public void selectionChanged( final SelectionChangedEvent e )
                {
                    FacetsSelectionPanel.this.selectionChanged( e );
                }
            }
        );

        this.tree.addCheckStateListener
        (
            new ICheckStateListener()
            {
                public void checkStateChanged( final CheckStateChangedEvent e )
                {
                    FacetsSelectionPanel.this.checkStateChanged( e );
                }
            }
        );

        this.tree.getTree().addListener
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
                    final int[] weights = sform2.getWeights();
                    settings.put( SASH2W1, weights[ 0 ] );
                    settings.put( SASH2W2, weights[ 1 ] );
                }
            }
        );

        final int[] weights2
            = new int[] { this.settings.getInt( SASH2W1 ),
                          this.settings.getInt( SASH2W2 ) };

        this.sform2.setWeights( weights2 );
        
        this.runtimesPanel = new RuntimesPanel( this.sform1, SWT.NONE, this );
        this.runtimesPanel.setLayoutData( hhint( gdhfill(), 80 ) );
        
        this.runtimesPanel.addFilter
        (
            new RuntimesPanel.IFilter()
            {
                public boolean check( final IRuntime runtime )
                {
                    for( Iterator itr = getSelectedProjectFacets().iterator();
                         itr.hasNext(); )
                    {
                        final IProjectFacetVersion fv
                            = (IProjectFacetVersion) itr.next();
                        
                        if( ! runtime.supports( fv ) )
                        {
                            return false;
                        }
                    }
                    
                    return true;
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
                    final int[] weights = sform1.getWeights();
                    settings.put( SASH1W1, weights[ 0 ] );
                    settings.put( SASH1W2, weights[ 1 ] );
                }
            }
        );

        final int[] weights1
            = new int[] { this.settings.getInt( SASH1W1 ),
                          this.settings.getInt( SASH1W2 ) };
    
        this.sform1.setWeights( weights1 );
        
        updateValidationDisplay();
    }
    
    public boolean isSelectionValid()
    {
        return this.problems.isOK();
    }
    
    public Set getActions()
    {
        return this.actions;
    }
    
    public Action getAction( final Action.Type type,
                             final IProjectFacetVersion f )
    {
        return getAction( this.actions, type, f );
    }
    
    private static Action getAction( final Set actions,
                                     final Action.Type type,
                                     final IProjectFacetVersion fv )
    {
        for( Iterator itr = actions.iterator(); itr.hasNext(); )
        {
            final Action action = (Action) itr.next();
            
            if( action.getType() == type && action.getProjectFacetVersion() == fv )
            {
                return action;
            }
        }
        
        return null;
    }
    
    private static Action getAction( final Set actions,
                                     final Action.Type type,
                                     final IProjectFacet f )
    {
        for( Iterator itr = actions.iterator(); itr.hasNext(); )
        {
            final Action action = (Action) itr.next();
            
            if( action.getType() == type && 
                action.getProjectFacetVersion().getProjectFacet() == f )
            {
                return action;
            }
        }
        
        return null;
    }
    
    private Action createAction( final Set actions,
                                 final Action.Type type,
                                 final IProjectFacetVersion fv )
    {
        Action action = getAction( actions, type, fv );
        
        if( action == null )
        {
            Object config = null;
            
            if( fv.supports( type ) )
            {
                try
                {
                    final IProjectFacet f = fv.getProjectFacet();
                    
                    action = getAction( actions, type, f );
                    
                    if( action != null )
                    {
                        final IProjectFacetVersion current
                            = action.getProjectFacetVersion();
                        
                        if( fv.isSameActionConfig( type, current ) )
                        {
                            config = action.getConfig();
                            
                            IActionConfig c = null;
                            
                            if( config instanceof IActionConfig )
                            {
                                c = (IActionConfig) config;
                            }
                            else
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
    
    public IRuntime getRuntime()
    {
        return this.runtimesPanel.getRuntime();
    }
    
    public void setRuntime( final IRuntime runtime )
    {
        this.runtimesPanel.setRuntime( runtime );
    }
    
    public Set getSelectedProjectFacets()
    {
        final HashSet set = new HashSet();

        for( int i = 0, n = this.data.size(); i < n; i++ )
        {
            final TableRowData trd = (TableRowData) this.data.get( i );

            if( trd.isSelected() )
            {
                set.add( trd.getCurrentVersion() );
            }
        }

        return set;
    }

    public void setSelectedProjectFacets( final Set sel )
    {
        for( Iterator itr = sel.iterator(); itr.hasNext(); )
        {
            final IProjectFacetVersion fv 
                = (IProjectFacetVersion) itr.next();
            
            final IProjectFacet f = fv.getProjectFacet();
            final TableRowData trd = findTableRowData( f );

            trd.setSelected( true );
            trd.setCurrentVersion( fv );

            this.tree.setChecked( trd, true );
        }

        this.tree.refresh();
        this.runtimesPanel.refresh();
        updateValidationDisplay();
    }
    
    public void selectPreset( final IPreset preset )
    {
        if( preset != null )
        {
            final int index = this.presets.indexOf( preset );
            
            if( index == -1 )
            {
                throw new IllegalArgumentException();
            }
            
            this.presetsCombo.select( index + 1 );
            handlePresetSelected();
        }
    }

    public void setFixedProjectFacets( final Set fixed )
    {
        for( int i = 0, n = this.data.size(); i < n; i++ )
        {
            ( (TableRowData) this.data.get( i ) ).setFixed( false );
        }

        for( Iterator itr = fixed.iterator(); itr.hasNext(); )
        {
            final IProjectFacet f = (IProjectFacet) itr.next();
            final TableRowData trd = findTableRowData( f );
            
            this.fixed.add( f );
            trd.setFixed( true );
            trd.setSelected( true );
            this.tree.setChecked( trd, true );
        }

        this.tree.refresh();
        this.runtimesPanel.refresh();
        refreshPresetsCombo();
        updateValidationDisplay();
    }
    
    public void addFilter( final IFilter filter )
    {
        this.filters.add( filter );
        this.tree.refresh();
    }

    public void removeFilter( final IFilter filter )
    {
        this.filters.remove( filter );
        this.tree.refresh();
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
        for( int i = 0, n = this.listeners.size(); i < n; i++ )
        {
            ( (Listener) this.listeners.get( i ) ).handleEvent( null );
        }
    }
    
    public void addRuntimeListener( final Listener listener )
    {
        this.runtimesPanel.addRuntimeListener( listener );
    }
    
    public void removeRuntimeListener( final Listener listener )
    {
        this.runtimesPanel.removeRuntimeListener( listener );
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
            = (IStructuredSelection) this.tree.getSelection();

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

        this.tree.setSelection( ts );
    }

    public void notifySelectionChangedListeners()
    {
        final SelectionChangedEvent event
            = new SelectionChangedEvent( this, getSelection() );

        for( int i = 0, n = this.selectionListeners.size(); i < n; i++ )
        {
            final ISelectionChangedListener listener
                = (ISelectionChangedListener) this.selectionListeners.get( i );

            listener.selectionChanged( event );
        }
    }

    private void selectionChanged( final SelectionChangedEvent event )
    {
        final Object selection
            = ( (IStructuredSelection) event.getSelection() ).getFirstElement();

        if( selection instanceof TableRowData )
        {
            final TableRowData trd = (TableRowData) selection;

            if( trd == null )
            {
                return;
            }

            // Reset the contents of the combo box cell editor to contain the
            // versions of the selected project facet.

            final IProjectFacetVersion[] versions = trd.getVersions();
            final String[] verstrs = new String[ versions.length ];

            for( int i = 0; i < versions.length; i++ )
            {
                verstrs[ i ] = versions[ i ].getVersionString();
            }

            this.ceditor.setItems( verstrs );

            for( int i = 0; i < versions.length; i++ )
            {
                if( versions[ i ] == trd.getCurrentVersion() )
                {
                    this.ceditor.setValue( new Integer( i ) );
                    break;
                }
            }
        }

        notifySelectionChangedListeners();
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
                    this.tree.setChecked( el, true );
                }
                
                return;
            }
            
            trd.setSelected( checked );
            refreshCategoryState( trd );
        }
        else
        {
            final ContentProvider cp
                = (ContentProvider) this.tree.getContentProvider();

            final Object[] children = cp.getChildren( el );

            for( int i = 0; i < children.length; i++ )
            {
                final TableRowData trd = (TableRowData) children[ i ];
                trd.setSelected( checked );
                this.tree.setChecked( trd, checked );
            }

            this.tree.setGrayed( el, false );
        }

        updateValidationDisplay();
        this.runtimesPanel.refresh();
        
        this.presetsCombo.select( 0 );
        refreshPresetsButtons();
    }

    private void updateValidationDisplay()
    {
        final Set sel = getSelectedProjectFacets();
        final Set old = new HashSet( this.actions );
        this.actions.clear();
        
        // What has been removed?
        
        for( Iterator itr = base.iterator(); itr.hasNext(); )
        {
            final IProjectFacetVersion f
                = (IProjectFacetVersion) itr.next();
            
            if( ! sel.contains( f ) )
            {
                this.actions.add( createAction( old, Action.Type.UNINSTALL, f ) );
            }
        }

        // What has been added?
        
        for( Iterator itr = sel.iterator(); itr.hasNext(); )
        {
            final IProjectFacetVersion f 
                = (IProjectFacetVersion) itr.next();
            
            if( ! base.contains( f ) )
            {
                this.actions.add( createAction( old, Action.Type.INSTALL, f ) );
            }
        }
        
        // Coalesce uninstall/install pairs into version change actions, if
        // possible.
        
        final HashSet toadd = new HashSet();
        final HashSet toremove = new HashSet();
        
        for( Iterator itr1 = this.actions.iterator(); itr1.hasNext(); )
        {
            final Action action1 = (Action) itr1.next();
            
            for( Iterator itr2 = this.actions.iterator(); itr2.hasNext(); )
            {
                final Action action2 = (Action) itr2.next();
                
                if( action1.getType() == Action.Type.UNINSTALL &&
                    action2.getType() == Action.Type.INSTALL )
                {
                    final IProjectFacetVersion f1 = action1.getProjectFacetVersion();
                    final IProjectFacetVersion f2 = action2.getProjectFacetVersion();
                    
                    if( f1.getProjectFacet() == f2.getProjectFacet() )
                    {
                        if( f2.supports( Action.Type.VERSION_CHANGE ) )
                        {
                            toremove.add( action1 );
                            toremove.add( action2 );
                            toadd.add( createAction( old, Action.Type.VERSION_CHANGE, f2 ) );
                        }
                    }
                }
            }
        }
        
        this.actions.removeAll( toremove );
        this.actions.addAll( toadd );
        
        this.problems = ProjectFacetsManager.check( this.base, this.actions );
        this.problemsView.refresh();

        if( this.problems.isOK() )
        {
            this.sform2.setMaximizedControl( this.tree.getTree() );
        }
        else
        {
            this.sform2.setMaximizedControl( null );
        }

        notifyProjectFacetsListeners();
    }
    
    private void refreshPresetsCombo()
    {
        this.presetsCombo.removeAll();
        this.presets.clear();
        
        for( Iterator itr1 = ProjectFacetsManager.getPresets().iterator(); 
             itr1.hasNext(); )
        {
            final IPreset preset = (IPreset) itr1.next();
            final HashSet temp = new HashSet();
            
            for( Iterator itr2 = preset.getProjectFacets().iterator(); 
                 itr2.hasNext(); )
            {
                final IProjectFacetVersion fv
                    = (IProjectFacetVersion) itr2.next();
                
                temp.add( fv.getProjectFacet() );
            }
            
            if( temp.containsAll( this.fixed ) )
            {
                this.presets.add( preset );
            }
        }
        
        Collections.sort
        (
            this.presets,
            new Comparator()
            {
                public int compare( final Object p1, 
                                    final Object p2 ) 
                {
                    if( p1 == p2 )
                    {
                        return 0;
                    }
                    else
                    {
                        final String label1 = ( (IPreset) p1 ).getLabel();
                        final String label2 = ( (IPreset) p2 ).getLabel();
                        
                        return label1.compareTo( label2 );
                    }
                }
            }
        );
        
        this.presetsCombo.add( "<custom>" );
        
        for( Iterator itr = this.presets.iterator(); itr.hasNext(); )
        {
            final IPreset preset = (IPreset) itr.next();
            this.presetsCombo.add( preset.getLabel() );
        }
        
        this.presetsCombo.select( 0 );
        refreshPresetsButtons();
    }
    
    private void refreshPresetsButtons()
    {
        final int selection = this.presetsCombo.getSelectionIndex();
        
        if( selection == 0 )
        {
            this.savePresetButton.setEnabled( true );
            this.deletePresetButton.setEnabled( false );
        }
        else
        {
            final IPreset preset = (IPreset) this.presets.get( selection - 1 );
            
            this.savePresetButton.setEnabled( false );
            this.deletePresetButton.setEnabled( preset.isUserDefined() );
        }
    }
    
    private void refreshCategoryState( final TableRowData trd )
    {
        final ICategory category = trd.getProjectFacet().getCategory();
        
        if( category != null )
        {
            int selected = 0;
    
            for( Iterator itr = category.getProjectFacets().iterator(); 
                 itr.hasNext(); )
            {
                final TableRowData ctrd 
                    = findTableRowData( (IProjectFacet) itr.next() );
    
                if( ctrd.isSelected() )
                {
                    selected++;
                }
            }
    
            if( selected == 0 )
            {
                this.tree.setChecked( category, false );
                this.tree.setGrayed( category, false );
            }
            else if( selected == category.getProjectFacets().size() )
            {
                this.tree.setChecked( category, true );
                this.tree.setGrayed( category, false );
            }
            else
            {
                this.tree.setGrayChecked( category, true );
            }
        }
    }

    private boolean isFilteredOut( final IProjectFacetVersion fv )
    {
        for( Iterator itr = FacetsSelectionPanel.this.filters.iterator();
             itr.hasNext(); )
        {
            if( ! ( (IFilter) itr.next() ).check( fv ) )
            {
                return true;
            }
        }

        return false;
    }

    private TableRowData findTableRowData( final IProjectFacet f )
    {
        for( int i = 0, n = this.data.size(); i < n; i++ )
        {
            final TableRowData trd = (TableRowData) this.data.get( i );

            if( trd.getProjectFacet() == f )
            {
                return trd;
            }
        }

        throw new IllegalStateException();
    }

    private void handleMouseDownEvent( final Event event )
    {
        final ArrayList items = getAllTreeItems();
        
        TreeItem onItem = null;

        for( int i = 0, n = items.size(); i < n; i++ )
        {
            final TreeItem item = (TreeItem) items.get( i );
            
            if( item.getBounds( 0 ).contains( event.x, event.y ) )
            {
                onItem = item;
                break;
            }

            if( item.getBounds( 1 ).contains( event.x, event.y ) )
            {
                this.tree.getTree().setSelection( new TreeItem[] { item } );
                this.tree.editElement( item.getData(), 1 );
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
            
            this.tree.getTree().setMenu( this.popupMenu );
        }
        else
        {
            this.tree.getTree().setMenu( null );
        }
    }
    
    private void handleShowConstraints()
    {
        final TreeItem[] items = this.tree.getTree().getSelection();
        if( items.length != 1 ) throw new IllegalStateException();
        final TreeItem item = items[ 0 ];
        final TableRowData trd = (TableRowData) item.getData();
        final IProjectFacetVersion fv = trd.getCurrentVersion();
        
        final Rectangle bounds = item.getBounds();
        
        Point location = new Point( bounds.x, bounds.y + bounds.height );
        location = this.tree.getTree().toDisplay( location );
        
        final ConstraintDisplayDialog dialog 
            = new ConstraintDisplayDialog( getShell(), location,
                                           fv.getConstraint() );

        dialog.open();
    }
    
    private void handlePresetSelected()
    {
        final int selection = this.presetsCombo.getSelectionIndex();
        
        if( selection > 0 )
        {
            final IPreset preset = (IPreset) this.presets.get( selection - 1 );
            final Set selected = new HashSet();
            
            for( Iterator itr = preset.getProjectFacets().iterator(); 
                 itr.hasNext(); )
            {
                final IProjectFacetVersion fv 
                    = (IProjectFacetVersion) itr.next();
                
                final TableRowData trd 
                    = findTableRowData( fv.getProjectFacet() );
                
                if( ! trd.isSelected() )
                {
                    this.tree.setChecked( trd, true );
                    trd.setSelected( true );
                    refreshCategoryState( trd );
                }
                
                if( trd.getCurrentVersion() != fv )
                {
                    trd.setCurrentVersion( fv );
                    this.tree.update( trd, null );
                }
                
                selected.add( trd );
            }

            for( Iterator itr = this.data.iterator(); itr.hasNext(); )
            {
                final TableRowData trd = (TableRowData) itr.next();
                
                if( ! selected.contains( trd ) )
                {
                    this.tree.setChecked( trd, false );
                    trd.setSelected( false );
                    refreshCategoryState( trd );
                }
            }
        }
        
        refreshPresetsButtons();
        updateValidationDisplay();
        this.runtimesPanel.refresh();
    }
    
    private void handleSavePreset()
    {
        final InputDialog dialog 
            = new InputDialog( getShell(), "Save Preset", 
                               "Enter the name for the preset.",
                               null, null );
        
        if( dialog.open() == IDialogConstants.OK_ID )
        {
            final String name = dialog.getValue();
            final Set facets = getSelectedProjectFacets();
            
            final IPreset preset
                = ProjectFacetsManager.definePreset( name, facets );
            
            refreshPresetsCombo();
            
            final int pos = this.presets.indexOf( preset );
            this.presetsCombo.select( pos + 1 );
            refreshPresetsButtons();
        }
    }
    
    private void handleDeletePreset()
    {
        final int selection = this.presetsCombo.getSelectionIndex();
        final IPreset preset = (IPreset) this.presets.get( selection - 1 );
        
        ProjectFacetsManager.deletePreset( preset );
        
        refreshPresetsCombo();
    }
    
    private ArrayList getAllTreeItems()
    {
        final ArrayList result = new ArrayList();
        getAllTreeItems( this.tree.getTree().getItems(), result );
        return result;
    }

    private static void getAllTreeItems( final TreeItem[] items,
                                         final ArrayList result)
    {
        for( int i = 0; i < items.length; i++ )
        {
            final TreeItem item = items[ i ];
            result.add( item );

            getAllTreeItems( item.getItems(), result );
        }
    }
    
    private final class TableRowData
    {
        private IProjectFacet f;
        private List versions;
        private IProjectFacetVersion current;
        private boolean isSelected;
        private boolean isFixed;

        public TableRowData( final IProjectFacet f )
        
            throws CoreException
            
        {
            this.f = f;
            this.versions = f.getSortedVersions( false );
            this.current = f.getLatestVersion();
            this.isSelected = false;
            this.isFixed = false;
        }

        public IProjectFacet getProjectFacet()
        {
            return this.f;
        }

        public IProjectFacetVersion[] getVersions()
        {
            final ArrayList list = new ArrayList();

            for( Iterator itr = this.versions.iterator(); itr.hasNext(); )
            {
                final IProjectFacetVersion fv 
                    = (IProjectFacetVersion) itr.next();

                if( ! isFilteredOut( fv ) )
                {
                    list.add( fv );
                }
            }

            final IProjectFacetVersion[] array
                = new IProjectFacetVersion[ list.size() ];

            return (IProjectFacetVersion[]) list.toArray( array );
        }

        public IProjectFacetVersion getCurrentVersion()
        {
            if( isFilteredOut( this.current ) )
            {
                this.current = getVersions()[ 0 ];
            }

            return this.current;
        }

        public void setCurrentVersion( final IProjectFacetVersion fv )
        {
            this.current = fv;
        }

        public boolean isSelected()
        {
            if( getVersions().length == 0 )
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
            return getVersions().length > 0;
        }
    }

    private final class ContentProvider

        implements ITreeContentProvider

    {
        public Object[] getElements( final Object element )
        {
            final ArrayList list = new ArrayList();
            final Set categories = ProjectFacetsManager.getCategories();

            for( Iterator itr1 = categories.iterator(); itr1.hasNext(); )
            {
                boolean visible = false;
                
                final ICategory cat = (ICategory) itr1.next();

                for( Iterator itr2 = cat.getProjectFacets().iterator(); 
                     itr2.hasNext(); )
                {
                    final IProjectFacet f 
                        = (IProjectFacet) itr2.next();
                    
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

            for( int i = 0; i < FacetsSelectionPanel.this.data.size(); i++ )
            {
                final TableRowData trd
                    = (TableRowData) FacetsSelectionPanel.this.data.get( i );

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
                
                final ArrayList trds = new ArrayList();

                for( Iterator itr = category.getProjectFacets().iterator();
                     itr.hasNext(); )
                {
                    final TableRowData trd 
                        = findTableRowData( (IProjectFacet) itr.next() );

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

        implements ITableLabelProvider, IFontProvider

    {
        private ImageRegistry imageRegistry = new ImageRegistry();
        
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
                    return "";
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
                        final String vstr
                            = trd.getCurrentVersion().getVersionString();
                        
                        return trd.getVersions().length == 1 
                               ? vstr : vstr + " ...";
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

            String plugin = null;
            String iconPath = null;

            if( element instanceof TableRowData )
            {
                final IProjectFacet f
                    = ( (TableRowData) element ).getProjectFacet();

                iconPath = f.getIconPath();

                if( iconPath != null )
                {
                    plugin = f.getPluginId();
                }
            }
            else
            {
                final ICategory category = (ICategory) element;

                iconPath = category.getIconPath();

                if( iconPath != null )
                {
                    plugin = category.getPlugin();
                }
            }

            if( iconPath == null )
            {
                return getDefaultImage();
            }
            else
            {
                return getImage( plugin, iconPath );
            }
        }
        
        private Image getImage( final String plugin,
                                final String iconPath )
        {
            final String key = plugin + ":" + iconPath;
            Image image = this.imageRegistry.get( key );

            if( image == null )
            {
                Bundle bundle = Platform.getBundle( plugin );
                URL url = bundle.getEntry( iconPath );
                
                if( url == null )
                {
                    final String msg 
                        = Resources.bind( Resources.iconNotFound, plugin,
                                          iconPath );
                    
                    final IStatus status
                        = new Status( IStatus.ERROR, FacetUiPlugin.PLUGIN_ID,
                                      0, msg, null );
                    
                    FacetUiPlugin.getInstance().getLog().log( status );
                    
                    image = getDefaultImage();
                    this.imageRegistry.put( key, image );
                }
                else
                {
                    this.imageRegistry.put( key, ImageDescriptor.createFromURL( url ) );
                    image = this.imageRegistry.get( key );
                }
            }
            
            return image;
        }
        
        private static final String DEFAULT_IMG_KEY = "#DEFAULT#";
        private static final String DEFAULT_IMG_LOCATION = "images/unknown.gif";
        
        private Image getDefaultImage()
        {
            Image image = this.imageRegistry.get( DEFAULT_IMG_KEY );

            if( image == null )
            {
                final Bundle bundle 
                    = Platform.getBundle( FacetUiPlugin.PLUGIN_ID );
                
                final URL url = bundle.getEntry( DEFAULT_IMG_LOCATION );
                
                this.imageRegistry.put( DEFAULT_IMG_KEY, 
                                        ImageDescriptor.createFromURL( url ) );
                
                image = this.imageRegistry.get( DEFAULT_IMG_KEY );
            }
            
            return image;
        }

        public Font getFont( final Object element )
        {
            if( element instanceof TableRowData &&
                ( (TableRowData) element ).isFixed() )
            {
                return FIXED_FONT;
            }
            
            return null;
        }

        public void dispose()
        {
            this.imageRegistry.dispose();
        }

        public boolean isLabelProperty( final Object obj,
                                        final String s )
        {
            return false;
        }
        
        public void addListener( final ILabelProviderListener listener ) {}
        public void removeListener( ILabelProviderListener listener ) {}
    }

    private final class CellModifier

        implements ICellModifier

    {
        public Object getValue( final Object element,
                                final String property )
        {
            final TableRowData trd = (TableRowData) element;

            if( property.equals( "version" ) )
            {
                final IProjectFacetVersion[] versions = trd.getVersions();

                for( int i = 0; i < versions.length; i++ )
                {
                    if( versions[ i ] == trd.getCurrentVersion() )
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
            return property.equals( "version" ) &&
                   element instanceof TableRowData &&
                   ( (TableRowData) element ).getVersions().length > 1;
        }

        public void modify( final Object element,
                            final String property,
                            final Object value )
        {
            final TreeItem item = (TreeItem) element;
            final TableRowData trd = (TableRowData) item.getData();

            if( property.equals( "version" ) )
            {
                final int index = ( (Integer) value ).intValue();

                if( index != -1 )
                {
                    final IProjectFacetVersion fv 
                        = trd.getVersions()[ index ];
                    
                    if( trd.getCurrentVersion() != fv )
                    {
                        trd.setCurrentVersion( trd.getVersions()[ index ] );
                        FacetsSelectionPanel.this.tree.refresh();
                        
                        if( trd.isSelected() )
                        {
                            FacetsSelectionPanel.this.presetsCombo.select( 0 );
                            refreshPresetsButtons();
                        }
    
                        updateValidationDisplay();
                        FacetsSelectionPanel.this.runtimesPanel.refresh();
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
            final Boolean fixed1 = Boolean.valueOf( getFixed( a ) );
            final Boolean fixed2 = Boolean.valueOf( getFixed( b ) );
            
            final String label1 = getLabel( a );
            final String label2 = getLabel( b );
            
            int res 
                = fixed1.equals( fixed2 ) 
                  ? 0 
                  : ( fixed1.booleanValue() ? -1 : 1 );
            
            if( res == 0 )
            {
                res = label1.compareToIgnoreCase( label2 );
            }
            
            return res;
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
        
        private static boolean getFixed( final Object obj )
        {
            if( obj instanceof TableRowData )
            {
                return ( (TableRowData) obj ).isFixed();
            }
            else
            {
                return false;
            }
        }
    }

    private final class ProblemsContentProvider

        implements IStructuredContentProvider

    {
        public Object[] getElements( final Object element )
        {
            return problems.getChildren();
        }

        public void dispose() { }

        public void inputChanged( final Viewer viewer,
                                  final Object oldObject,
                                  final Object newObject ) {}
    }

    private final class ProblemsLabelProvider

        implements ITableLabelProvider

    {
        private Image errorImage;

        public ProblemsLabelProvider()
        {
            final Bundle bundle = Platform.getBundle( FacetUiPlugin.PLUGIN_ID );
            final URL url = bundle.getEntry( "images/error.gif" );

            this.errorImage
                = ImageDescriptor.createFromURL( url ).createImage();
        }

        public String getColumnText( final Object element,
                                     final int column )
        {
            return ( (IStatus) element ).getMessage();
        }

        public Image getColumnImage( final Object element,
                                     final int column )
        {
            return this.errorImage;
        }

        public boolean isLabelProperty( final Object obj,
                                        final String s )
        {
            return false;
        }

        public void dispose()
        {
            this.errorImage.dispose();
        }

        public void addListener( final ILabelProviderListener listener ) {}
        public void removeListener( ILabelProviderListener listener ) {}
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String iconNotFound;
        
        static
        {
            initializeMessages( FacetsSelectionPanel.class.getName(), 
                                Resources.class );
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
    
    private static final GridData hspan( final GridData gd,
                                         final int span )
    {
        gd.horizontalSpan = span;
        return gd;
    }

}
