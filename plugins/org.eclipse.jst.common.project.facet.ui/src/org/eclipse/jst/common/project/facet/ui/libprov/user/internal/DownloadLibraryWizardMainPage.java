/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.ui.libprov.user.internal;

import static org.eclipse.jst.common.project.facet.ui.internal.FacetedProjectFrameworkJavaExtUiPlugin.IMG_PATH_WIZBAN_DOWNLOAD_LIBRARY;
import static org.eclipse.jst.common.project.facet.ui.internal.FacetedProjectFrameworkJavaExtUiPlugin.getImageDescriptor;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gd;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhspan;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.UserLibraryManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jst.common.project.facet.core.libprov.user.internal.DownloadableLibrary;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@SuppressWarnings( "restriction" )

public final class DownloadLibraryWizardMainPage 

    extends WizardPage 
    
{
    private final DownloadLibraryWizard wizard;
    private final List<DownloadableLibrary> libraries;
    
    private TableViewer librariesTableViewer;
    private Table librariesTable;
    private Label libraryNameLabel;
    private Text libraryNameTextField;
    private Label downloadDestinationLabel;
    private Text downloadDestinationTextField;
    private Button downloadDestinationBrowseButton;
    
    public DownloadLibraryWizardMainPage( final DownloadLibraryWizard wizard,
                                          final List<DownloadableLibrary> libraries ) 
    {
        super( "DownloadableLibrariesWizardMainPage" ); //$NON-NLS-1$
        
        this.wizard = wizard;
        this.libraries = libraries;
        
        setTitle( Resources.pageTitle );
        setDescription( Resources.pageDescription );
        setImageDescriptor( getImageDescriptor( IMG_PATH_WIZBAN_DOWNLOAD_LIBRARY ) );
    }
    
    public DownloadableLibrary getSelectedLibrary()
    {
        final IStructuredSelection sel = (IStructuredSelection) this.librariesTableViewer.getSelection();
        return (DownloadableLibrary) sel.getFirstElement();
    }
    
    public String getLibraryName()
    {
        return this.libraryNameTextField.getText().trim();
    }
    
    public String getDownloadDestination()
    {
        return this.downloadDestinationTextField.getText().trim();
    }
    
    private List<DownloadableLibrary> getLibraries()
    {
        return this.libraries;
    }
    
    public void createControl( final Composite parent ) 
    {
        final Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( gl( 2 ) );
        
        this.librariesTableViewer = new TableViewer( composite, SWT.BORDER | SWT.FULL_SELECTION );
        this.librariesTable = this.librariesTableViewer.getTable();
        
        this.librariesTable.setLayoutData( gdhspan( gdfill(), 2 ) );
        this.librariesTable.setLinesVisible( false );
        this.librariesTable.setHeaderVisible( true );
        
        final TableViewerColumn libraryNameViewerColumn = new TableViewerColumn( this.librariesTableViewer, SWT.NONE );
        final TableColumn libraryNameColumn = libraryNameViewerColumn.getColumn();
        
        libraryNameColumn.setText( Resources.libraryNameColumnTitle );
        libraryNameColumn.setResizable( true );
        libraryNameColumn.setWidth( 300 );
        
        libraryNameViewerColumn.setLabelProvider
        (
            new ColumnLabelProvider()
            {
                @Override
                public String getText( final Object element )
                {
                    final DownloadableLibrary library = (DownloadableLibrary) element;
                    return library.getName();
                }
            }
        );
        
        final TableViewerColumn downloadProviderViewerColumn = new TableViewerColumn( this.librariesTableViewer, SWT.NONE );
        final TableColumn downloadProviderColumn = downloadProviderViewerColumn.getColumn();
        
        downloadProviderColumn.setText( Resources.downloadProviderColumnTitle );
        downloadProviderColumn.setResizable( true );
        downloadProviderColumn.setWidth( 300 );
        
        downloadProviderViewerColumn.setLabelProvider
        (
            new ColumnLabelProvider()
            {
                @Override
                public String getText( final Object element )
                {
                    final DownloadableLibrary library = (DownloadableLibrary) element;
                    return library.getDownloadProvider();
                }
            }
        );

        final IStructuredContentProvider contentProvider = new IStructuredContentProvider()
        {
            public Object[] getElements( final Object inputElement )
            {
                return getLibraries().toArray();
            }

            public void dispose()
            {
            }

            public void inputChanged( final Viewer viewer,
                                      final Object oldInput,
                                      final Object newInput )
            {
            }
        };
        
        final ViewerSorter nameAscendingSorter = new ViewerSorter()
        {
            @Override
            public int compare( final Viewer viewer, 
                                final Object lib1, 
                                final Object lib2 )
            {
                final String name1 = ( (DownloadableLibrary) lib1 ).getName();
                final String name2 = ( (DownloadableLibrary) lib2 ).getName();

                return name1.compareTo( name2 );
            }
        };
        
        final ViewerSorter nameDescendingSorter = new ViewerSorter()
        {
            @Override
            public int compare( final Viewer viewer, 
                                final Object lib1, 
                                final Object lib2 )
            {
                final String name1 = ( (DownloadableLibrary) lib1 ).getName();
                final String name2 = ( (DownloadableLibrary) lib2 ).getName();

                return name2.compareTo( name1 );
            }
        };
        
        final ViewerSorter providerAscendingSorter = new ViewerSorter()
        {
            @Override
            public int compare( final Viewer viewer, 
                                final Object lib1, 
                                final Object lib2 )
            {
                final String provider1 = ( (DownloadableLibrary) lib1 ).getDownloadProvider();
                final String provider2 = ( (DownloadableLibrary) lib2 ).getDownloadProvider();

                return provider1.compareTo( provider2 );
            }
        };
        
        
        final ViewerSorter providerDescendingSorter = new ViewerSorter()
        {
            @Override
            public int compare( final Viewer viewer, 
                                final Object lib1, 
                                final Object lib2 )
            {
                final String provider1 = ( (DownloadableLibrary) lib1 ).getDownloadProvider();
                final String provider2 = ( (DownloadableLibrary) lib2 ).getDownloadProvider();

                return provider2.compareTo( provider1 );
            }
        };
        
        this.librariesTableViewer.setSorter( nameAscendingSorter );
        this.librariesTable.setSortColumn( libraryNameColumn );
        this.librariesTable.setSortDirection( SWT.UP );
        
        libraryNameColumn.addSelectionListener
        (
            new SelectionAdapter()
            {
                @Override
                public void widgetSelected( final SelectionEvent event )
                {
                    final TableViewer tableViewer = DownloadLibraryWizardMainPage.this.librariesTableViewer;
                    final Table table = tableViewer.getTable();
                    
                    if( tableViewer.getSorter() == nameAscendingSorter )
                    {
                        tableViewer.setSorter( nameDescendingSorter );
                        table.setSortDirection( SWT.DOWN );
                    }
                    else
                    {
                        tableViewer.setSorter( nameAscendingSorter );
                        table.setSortColumn( libraryNameColumn );
                        table.setSortDirection( SWT.UP );
                    }
                }
            }
        );
        
        downloadProviderColumn.addSelectionListener
        (
            new SelectionAdapter()
            {
                @Override
                public void widgetSelected( final SelectionEvent event )
                {
                    final TableViewer tableViewer = DownloadLibraryWizardMainPage.this.librariesTableViewer;
                    final Table table = tableViewer.getTable();
                    
                    if( tableViewer.getSorter() == providerAscendingSorter )
                    {
                        tableViewer.setSorter( providerDescendingSorter );
                        table.setSortDirection( SWT.DOWN );
                    }
                    else
                    {
                        tableViewer.setSorter( providerAscendingSorter );
                        table.setSortColumn( downloadProviderColumn );
                        table.setSortDirection( SWT.UP );
                    }
                }
            }
        );
        
        this.librariesTableViewer.setContentProvider( contentProvider );
        this.librariesTableViewer.setInput( new Object() );
        
        this.librariesTableViewer.addSelectionChangedListener
        (
            new ISelectionChangedListener()
            {
                public void selectionChanged( final SelectionChangedEvent event )
                {
                    handleLibrarySelectionChanged();
                }
            }
        );
        
        final ModifyListener modifyListener = new ModifyListener()
        {
            public void modifyText( final ModifyEvent event )
            {
                updateValidation();
            }
        };
        
        this.libraryNameLabel = new Label( composite, SWT.NONE );
        this.libraryNameLabel.setLayoutData( gd() );
        this.libraryNameLabel.setText( Resources.localLibraryNameLabel );
        
        this.libraryNameTextField = new Text( composite, SWT.BORDER );
        this.libraryNameTextField.setLayoutData( gdhfill() );
        this.libraryNameTextField.addModifyListener( modifyListener );
        
        this.downloadDestinationLabel = new Label( composite, SWT.NONE );
        this.downloadDestinationLabel.setLayoutData( gd() );
        this.downloadDestinationLabel.setText( Resources.downloadDestinationLabel );
        
        final Composite downloadDestinationComposite = new Composite( composite, SWT.NONE );
        downloadDestinationComposite.setLayoutData( gdhfill() );
        downloadDestinationComposite.setLayout( gl( 2, 0, 0 ) );
        
        this.downloadDestinationTextField = new Text( downloadDestinationComposite, SWT.BORDER );
        this.downloadDestinationTextField.setLayoutData( gdhfill() );
        this.downloadDestinationTextField.addModifyListener( modifyListener );
        
        this.downloadDestinationBrowseButton = new Button( downloadDestinationComposite, SWT.PUSH );
        this.downloadDestinationBrowseButton.setLayoutData( gd() );
        this.downloadDestinationBrowseButton.setText( Resources.browseButtonLabel );
        
        this.downloadDestinationBrowseButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                @Override
                public void widgetSelected( final SelectionEvent event )
                {
                    handleBrowseForDestination();
                }
            }
        );
        
        setControl( composite );
        updateValidation();
    }
    
    private void handleLibrarySelectionChanged()
    {
        final DownloadableLibrary lib = getSelectedLibrary();
        
        if( lib == null )
        {
            this.libraryNameTextField.setText( "" ); //$NON-NLS-1$
            this.downloadDestinationTextField.setText( "" ); //$NON-NLS-1$
        }
        else
        {
            String localLibraryName = null;
            
            int counter = -1;
            
            do
            {
                counter++;

                localLibraryName = lib.getName();
                
                if( counter > 0 )
                {
                    localLibraryName = localLibraryName + " (" + counter + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            while( isUserLibraryDefined( localLibraryName ) );
            
            this.libraryNameTextField.setText( localLibraryName );
            
            final IWorkspace ws = ResourcesPlugin.getWorkspace();
            final IPath baseDestPath = ws.getRoot().getLocation().append( "libraries" ); //$NON-NLS-1$
            IPath destPath = null;
            
            counter = -1;
            
            do
            {
                counter++;
                
                String name = lib.getName();
                
                if( counter > 0 )
                {
                    name = name + " (" + counter + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                }
                
                destPath = baseDestPath.append( name );
            }
            while( destPath.toFile().exists() );
            
            this.downloadDestinationTextField.setText( destPath.toOSString() );
        }
        
        this.wizard.getLicensePage().setLibrary( lib );
        
        updateValidation();
        getContainer().updateButtons();
    }
    
    private void handleBrowseForDestination()
    {
        File initialLocation = new File( this.downloadDestinationTextField.getText() );
        
        if( initialLocation.isAbsolute() )
        {
            while( initialLocation != null && ! initialLocation.exists() )
            {
                initialLocation = initialLocation.getParentFile();
            }
        }
        else
        {
            initialLocation = null;
        }
        
        final DirectoryDialog dlg = new DirectoryDialog( Display.getDefault().getActiveShell() );
        dlg.setMessage( Resources.destinationFolderBrowseDialogMessage );
        dlg.setFilterPath( initialLocation.getPath() );
        
        final String result = dlg.open();
        
        if( result != null )
        {
            this.downloadDestinationTextField.setText( result );
        }
    }
    
    private void updateValidation()
    {
        final boolean enabled;

        if( getSelectedLibrary() == null )
        {
            setMessage( null );
            setPageComplete( false );
            
            enabled = false;
        }
        else
        {
            enabled = true;
            
            final String localLibraryName = getLibraryName();
            final String destPath = getDownloadDestination();
            
            if( localLibraryName.length() == 0 )
            {
                setMessage( Resources.nameMustBeSpecified, ERROR );
                setPageComplete( false );
            }
            else if( destPath.length() == 0 )
            {
                setMessage( Resources.destinationFolderMustBeSpecified, ERROR );
                setPageComplete( false );
            }
            else
            {
                if( isUserLibraryDefined( localLibraryName ) )
                {
                    setMessage( Resources.nameConflict, WARNING );
                    setPageComplete( true );
                }
                else
                {
                    final File destFolder = new File( destPath );
                    
                    if( destFolder.exists() )
                    {
                        setMessage( Resources.destinationFolderExists, WARNING );
                        setPageComplete( true );
                    }
                    else if( ! destFolder.isAbsolute() )
                    {
                        setMessage( Resources.destinationFolderPathMustBeAbsolute, ERROR );
                        setPageComplete( false );
                    }
                    else
                    {
                        setMessage( null );
                        setPageComplete( true );
                    }
                }
            }
        }
        
        this.libraryNameLabel.setEnabled( enabled );
        this.libraryNameTextField.setEnabled( enabled );
        this.downloadDestinationLabel.setEnabled( enabled );
        this.downloadDestinationTextField.setEnabled( enabled );
        this.downloadDestinationBrowseButton.setEnabled( enabled );
    }
    
    private static boolean isUserLibraryDefined( final String name )
    {
        final UserLibraryManager userLibraryManager = JavaModelManager.getUserLibraryManager();
        return ( userLibraryManager.getUserLibrary( name ) != null );
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String pageTitle;
        public static String pageDescription;
        public static String libraryNameColumnTitle;
        public static String downloadProviderColumnTitle;
        public static String localLibraryNameLabel;
        public static String downloadDestinationLabel;
        public static String nameMustBeSpecified;
        public static String nameConflict;
        public static String destinationFolderMustBeSpecified;
        public static String destinationFolderExists;
        public static String destinationFolderPathMustBeAbsolute;
        public static String destinationFolderBrowseDialogMessage;
        public static String browseButtonLabel;
    
        static
        {
            initializeMessages( DownloadLibraryWizardMainPage.class.getName(), 
                                Resources.class );
        }
    }
    
}
