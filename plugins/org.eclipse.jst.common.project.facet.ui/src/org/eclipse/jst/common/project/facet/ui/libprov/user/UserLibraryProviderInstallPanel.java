/******************************************************************************
 * Copyright (c) 2009 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.ui.libprov.user;

import static org.eclipse.jst.common.project.facet.ui.internal.FacetedProjectFrameworkJavaExtUiPlugin.IMG_PATH_BUTTON_DOWNLOAD;
import static org.eclipse.jst.common.project.facet.ui.internal.FacetedProjectFrameworkJavaExtUiPlugin.IMG_PATH_BUTTON_MANAGE_LIBRARIES;
import static org.eclipse.jst.common.project.facet.ui.internal.FacetedProjectFrameworkJavaExtUiPlugin.IMG_PATH_OBJECTS_LIBRARY;
import static org.eclipse.jst.common.project.facet.ui.internal.FacetedProjectFrameworkJavaExtUiPlugin.getImageDescriptor;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdvfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.UserLibraryManager;
import org.eclipse.jdt.internal.ui.preferences.UserLibraryPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.common.project.facet.core.libprov.IPropertyChangeListener;
import org.eclipse.jst.common.project.facet.core.libprov.user.UserLibraryProviderInstallOperationConfig;
import org.eclipse.jst.common.project.facet.ui.libprov.LibraryProviderOperationPanel;
import org.eclipse.jst.common.project.facet.ui.libprov.user.internal.DownloadLibraryWizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * The install operation panel corresponding to the user-library-provider that uses JDT user library facility
 * for managing libraries. This class can be subclassed by those wishing to extend the base implementation
 * supplied by the framework.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

@SuppressWarnings( "restriction" )

public class UserLibraryProviderInstallPanel

    extends LibraryProviderOperationPanel
    
{
    private Composite rootComposite = null;
    private CheckboxTableViewer libsTableViewer = null;
    private MenuItem downloadLibraryMenuItem = null;
    private ToolItem downloadLibraryButton = null;
    private boolean downloadCommandEnabled = true;
    
    /**
     * Creates the panel control.
     * 
     * @param parent the parent composite
     * @return the created control
     */
    
    @Override
    public Control createControl( final Composite parent )
    {
        this.rootComposite = new Composite( parent, SWT.NONE );
        this.rootComposite.setLayout( gl( 1, 0, 0 ) );
        
        final Composite tableComposite = new Composite( this.rootComposite, SWT.NONE );
        tableComposite.setLayoutData( gdhfill() );
        tableComposite.setLayout( gl( 2, 0, 0 ) );
        
        final Table libsTable = new Table( tableComposite, SWT.CHECK | SWT.BORDER );
        libsTable.setLayoutData( gdhhint( gdhfill(), 60 ) );
        
        this.libsTableViewer = new CheckboxTableViewer( libsTable );
        this.libsTableViewer.setContentProvider( new LibrariesContentProvider() );
        this.libsTableViewer.setLabelProvider( new LibrariesLabelProvider() );
        this.libsTableViewer.setComparator( new ViewerComparator() );
        this.libsTableViewer.setInput( new Object() );
        
        final UserLibraryProviderInstallOperationConfig cfg 
            = (UserLibraryProviderInstallOperationConfig) getOperationConfig();
    
        this.libsTableViewer.setCheckedElements( cfg.getLibraryNames().toArray() );
        
        this.libsTableViewer.addCheckStateListener
        (
            new ICheckStateListener()
            {
                public void checkStateChanged( final CheckStateChangedEvent event )
                {
                    final List<String> libs = new ArrayList<String>();
                    
                    for( Object element : UserLibraryProviderInstallPanel.this.libsTableViewer.getCheckedElements() )
                    {
                        libs.add( (String) element );
                    }
                    
                    cfg.setLibraryNames( libs );
                }
            }
        );
        
        final IPropertyChangeListener listener = new IPropertyChangeListener()
        {
            public void propertyChanged( final String property,
                                         final Object oldValue,
                                         final Object newValue )
            {
                handleLibraryNamesChanged();
            }
        };
        
        cfg.addListener( listener, UserLibraryProviderInstallOperationConfig.PROP_LIBRARY_NAMES );
        
        final Image manageLibrariesImage = getImageDescriptor( IMG_PATH_BUTTON_MANAGE_LIBRARIES ).createImage();
        final Image downloadLibraryImage = getImageDescriptor( IMG_PATH_BUTTON_DOWNLOAD ).createImage();
        
        final Menu menu = new Menu( libsTable );
        libsTable.setMenu( menu );
        
        final ToolBar toolBar = new ToolBar( tableComposite, SWT.FLAT | SWT.VERTICAL );
        toolBar.setLayoutData( gdvfill() );
        
        final SelectionAdapter manageLibrariesListener = new SelectionAdapter()
        {
            @Override
            public void widgetSelected( final SelectionEvent event )
            {
                final String id = UserLibraryPreferencePage.ID;
                final Shell shell = libsTable.getShell();
                
                final PreferenceDialog dialog 
                    = PreferencesUtil.createPreferenceDialogOn( shell, id, new String[] { id }, null );
                
                if( dialog.open() == Window.OK )
                {
                    UserLibraryProviderInstallPanel.this.libsTableViewer.refresh();
                    
                    // We need to send an event up the listener chain since validation needs to be
                    // refreshed. This not an ideal solution, but it does work. The name of the 
                    // property is not important since the listener that does validation is global.
                    
                    final List<String> libNames = cfg.getLibraryNames();
                    cfg.notifyListeners( "validation", libNames, libNames ); //$NON-NLS-1$
                }
            }
        };

        final MenuItem manageLibrariesMenuItem = new MenuItem( menu, SWT.PUSH );
        manageLibrariesMenuItem.setText( Resources.manageLibrariesMenuItem );
        manageLibrariesMenuItem.setImage( manageLibrariesImage );
        manageLibrariesMenuItem.addSelectionListener( manageLibrariesListener );

        final ToolItem manageLibrariesButton = new ToolItem( toolBar, SWT.PUSH );
        manageLibrariesButton.setImage( manageLibrariesImage );
        manageLibrariesButton.setToolTipText( Resources.manageLibrariesButtonToolTip );
        manageLibrariesButton.addSelectionListener( manageLibrariesListener );
        
        final SelectionAdapter downloadLibraryListener = new SelectionAdapter()
        {
            @Override
            public void widgetSelected( final SelectionEvent event )
            {
                final UserLibraryProviderInstallOperationConfig cfg
                    = (UserLibraryProviderInstallOperationConfig) getOperationConfig();
                
                final String downloadedLibraryName = DownloadLibraryWizard.open( cfg);
                
                if( downloadedLibraryName != null )
                {
                    refreshLibrariesList();
                    cfg.addLibraryName( downloadedLibraryName );
                }
            }
        };

        this.downloadLibraryMenuItem = new MenuItem( menu, SWT.PUSH );
        this.downloadLibraryMenuItem.setText( Resources.downloadLibraryMenuItem );
        this.downloadLibraryMenuItem.setImage( downloadLibraryImage );
        this.downloadLibraryMenuItem.setEnabled( this.downloadCommandEnabled );
        this.downloadLibraryMenuItem.addSelectionListener( downloadLibraryListener );

        this.downloadLibraryButton = new ToolItem( toolBar, SWT.PUSH );
        this.downloadLibraryButton.setImage( downloadLibraryImage );
        this.downloadLibraryButton.setToolTipText( Resources.downloadLibraryButtonToolTip );
        this.downloadLibraryButton.setEnabled( this.downloadCommandEnabled );
        this.downloadLibraryButton.addSelectionListener( downloadLibraryListener );
        
        final Control footerControl = createFooter( this.rootComposite );
        
        if( footerControl != null )
        {
            footerControl.setLayoutData( gdhfill() );
        }
        
        this.rootComposite.addDisposeListener
        (
            new DisposeListener()
            {
                public void widgetDisposed( final DisposeEvent event )
                {
                    cfg.removeListener( listener );
                    manageLibrariesImage.dispose();
                    downloadLibraryImage.dispose();
                }
            }
        );
        
        return this.rootComposite;
    }
    
    /**
     * This method can be overridden to create a control beneath the libraries table. The default
     * implementation doesn't create a control and returns <code>null</code>.
     * 
     * @param parent the parent composite 
     * @return the created control
     */
    
    protected Control createFooter( final Composite parent )
    {
        return createControlNextToManageHyperlink( parent );
    }

    /**
     * @deprecated override createFooter method instead
     */
    
    protected Control createControlNextToManageHyperlink( final Composite parent )
    {
        return null;
    }

    /**
     * Controls enablement of the download command. This can be useful to extenders
     * of this class. The download command might be surfaced to users as a button,
     * a menu item or via other means. This method controls enablement of all of these
     * manifestations.
     * 
     * @param enabled <code>true</code>, if the download command should be enabled and
     *   <code>false</code> otherwise
     */
    
    protected void setDownloadCommandEnabled( final boolean enabled )
    {
        this.downloadCommandEnabled = enabled;
        
        if( this.downloadLibraryButton != null )
        {
            this.downloadLibraryButton.setEnabled( enabled );
        }
        
        if( this.downloadLibraryMenuItem != null )
        {
            this.downloadLibraryMenuItem.setEnabled( enabled );
        }
    }

    private void handleLibraryNamesChanged()
    {
        if( this.rootComposite.getDisplay().getThread() != Thread.currentThread() )
        {
            this.rootComposite.getDisplay().asyncExec
            (
                new Runnable()
                {
                    public void run()
                    {
                        handleLibraryNamesChanged();
                    }
                }
            );
            
            return;
        }
        
        final UserLibraryProviderInstallOperationConfig cfg 
            = (UserLibraryProviderInstallOperationConfig) getOperationConfig();

        this.libsTableViewer.setCheckedElements( cfg.getLibraryNames().toArray() );        
    }
    
    private void refreshLibrariesList()
    {
        this.libsTableViewer.refresh();
    }
    
    private static final class LibrariesContentProvider
    
        implements IStructuredContentProvider
        
    {

        public Object[] getElements( Object inputElement )
        {
            final UserLibraryManager userLibManager = JavaModelManager.getUserLibraryManager();
            return userLibManager.getUserLibraryNames();
        }

        public void inputChanged( final Viewer viewer,
                                  final Object oldInput,
                                  final Object newInput )
        {
        }
        
        public void dispose()
        {
        }
    }
    
    private static final class LibrariesLabelProvider
    
        extends LabelProvider
        
    {
        private final Image libraryImage = getImageDescriptor( IMG_PATH_OBJECTS_LIBRARY ).createImage();
        
        public Image getImage( final Object element )
        {
            return this.libraryImage;
        }

        public String getText( final Object element )
        {
            return (String) element;
        }

        @Override
        public void dispose()
        {
            this.libraryImage.dispose();
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String manageLibrariesMenuItem;
        public static String manageLibrariesButtonToolTip;
        public static String downloadLibraryMenuItem;
        public static String downloadLibraryButtonToolTip;

        static
        {
            initializeMessages( UserLibraryProviderInstallPanel.class.getName(), 
                                Resources.class );
        }
    }

}
