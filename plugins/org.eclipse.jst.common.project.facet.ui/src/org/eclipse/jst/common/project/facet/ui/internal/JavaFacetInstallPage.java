/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.ui.internal;

import static org.eclipse.jst.common.project.facet.ui.internal.FacetedProjectFrameworkJavaExtUiPlugin.IMG_PATH_JAVA_WIZBAN;
import static org.eclipse.jst.common.project.facet.ui.internal.FacetedProjectFrameworkJavaExtUiPlugin.IMG_PATH_SOURCE_FOLDER;
import static org.eclipse.jst.common.project.facet.ui.internal.FacetedProjectFrameworkJavaExtUiPlugin.getImageDescriptor;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gd;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhspan;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdvalign;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glmargins;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.common.project.facet.core.JavaFacetInstallConfig;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.util.IEventListener;
import org.eclipse.wst.common.project.facet.core.util.internal.ObjectReference;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JavaFacetInstallPage

	extends AbstractFacetWizardPage
	
{
	private static final String WIZARD_PAGE_NAME = "java.facet.install.page"; //$NON-NLS-1$
	
	private static final String IMG_KEY_SOURCE_FOLDER = "source.folder"; //$NON-NLS-1$
	
	private JavaFacetInstallConfig installConfig = null;
	private ImageRegistry imageRegistry = null;
	private Text defaultOutputFolderTextField = null;
	private TreeViewer sourceFoldersTreeViewer = null;
    private Tree sourceFoldersTree = null;
    private Button addButton = null;
    private Button editButton = null;
    private Button removeButton = null;
	
	public JavaFacetInstallPage() 
	{
		super( WIZARD_PAGE_NAME );
		
        setTitle( Resources.pageTitle );
        setDescription( Resources.pageDescription );
        setImageDescriptor( getImageDescriptor( IMG_PATH_JAVA_WIZBAN ) );
        
        this.imageRegistry = new ImageRegistry();
        this.imageRegistry.put( IMG_KEY_SOURCE_FOLDER, getImageDescriptor( IMG_PATH_SOURCE_FOLDER ) );
	}
	
	public JavaFacetInstallConfig getConfig()
	{
	    return this.installConfig;
	}

	public void setConfig( final Object config ) 
	{
		this.installConfig = (JavaFacetInstallConfig) config;
	}

	public void createControl( final Composite parent) 
	{
		final Composite composite = new Composite( parent, SWT.NONE );
		composite.setLayoutData( gdfill() );
		composite.setLayout( gl( 2 ) );

        composite.addDisposeListener
        (
            new DisposeListener()
            {
                public void widgetDisposed( final DisposeEvent e )
                {
                    handleDisposeEvent();
                }
            }
        );
		
        final Label sourceFoldersLabel = new Label( composite, SWT.NONE );
        sourceFoldersLabel.setLayoutData( gdhspan( gd(), 2 ) );
        sourceFoldersLabel.setText( Resources.sourceFoldersLabel );
        
        this.sourceFoldersTreeViewer = new TreeViewer( composite, SWT.BORDER );
        this.sourceFoldersTree = this.sourceFoldersTreeViewer.getTree();
        this.sourceFoldersTree.setLayoutData( gdfill() );
        
        this.sourceFoldersTreeViewer.setContentProvider( new SourceFoldersContentProvider() );
        this.sourceFoldersTreeViewer.setLabelProvider( new SourceFoldersLabelProvider() );
        this.sourceFoldersTreeViewer.setInput( new Object() );
        
        this.sourceFoldersTreeViewer.addSelectionChangedListener
        (
            new ISelectionChangedListener()
            {
                public void selectionChanged( final SelectionChangedEvent event ) 
                {
                    updateButtonEnablement();
                }
            }
        );
        
        this.installConfig.addListener
        (
            new IEventListener<JavaFacetInstallConfig.ChangeEvent>()
            {
                public void handleEvent( final JavaFacetInstallConfig.ChangeEvent event ) 
                {
                    JavaFacetInstallPage.this.sourceFoldersTreeViewer.refresh();
                }
            },
            JavaFacetInstallConfig.ChangeEvent.Type.SOURCE_FOLDERS_CHANGED
        );
        
        final Composite buttons = new Composite( composite, SWT.NONE );
        buttons.setLayoutData( gdvalign( gd(), SWT.TOP ) );
        buttons.setLayout( glmargins( gl( 1 ), 0, 0 ) );
        
        this.addButton = new Button( buttons, SWT.PUSH );
        this.addButton.setLayoutData( gdhfill() );
        this.addButton.setText( Resources.addFolderButton );
        
        this.addButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent event ) 
                {
                    handleAddButtonPressed();
                }
            }
        );

        this.editButton = new Button( buttons, SWT.PUSH );
        this.editButton.setLayoutData( gdhfill() );
        this.editButton.setText( Resources.editButton );
        
        this.editButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent event ) 
                {
                    handleEditButtonPressed();
                }
            }
        );
        
        this.removeButton = new Button( buttons, SWT.PUSH );
        this.removeButton.setLayoutData( gdhfill() );
        this.removeButton.setText( Resources.removeButton );
        
        this.removeButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent event ) 
                {
                    handleRemoveButtonPressed();
                }
            }
        );
        
        updateButtonEnablement();

        final Label defaultOutputFolderLabel = new Label( composite, SWT.NONE );
        defaultOutputFolderLabel.setLayoutData( gdhspan( gd(), 2 ) );
		defaultOutputFolderLabel.setText( Resources.defaultOutputFolderLabel );
		
		this.defaultOutputFolderTextField = new Text( composite, SWT.BORDER );
		this.defaultOutputFolderTextField.setLayoutData( gdhspan( gdhfill(), 2 ) );
		
		bindUiToModel();
		
		setControl( composite );
	}
	
    private void bindUiToModel()
	{
	    bindDefaultOutputFolder();
	}
	
	private void bindDefaultOutputFolder()
	{
	    final JavaFacetInstallConfig installConfig = this.installConfig;
	    final Text defaultOutputFolderTextField = this.defaultOutputFolderTextField;
	    
	    final ObjectReference<Boolean> updating = new ObjectReference<Boolean>( false );
	    
	    this.defaultOutputFolderTextField.addModifyListener
	    (
	        new ModifyListener()
	        {
                public void modifyText( final ModifyEvent e ) 
                {
                    if( updating.get() )
                    {
                        return;
                    }
                    
                    updating.set( true );

                    try
                    {
                        final String newValue = defaultOutputFolderTextField.getText();
                        installConfig.setDefaultOutputFolder( new Path( newValue ) );
                    }
                    finally
                    {
                        updating.set( false );
                    }
                }
	        }
	    );
	    
	    final IEventListener<JavaFacetInstallConfig.ChangeEvent> modelEventListener
	        = new IEventListener<JavaFacetInstallConfig.ChangeEvent>()
        {
            public void handleEvent( final JavaFacetInstallConfig.ChangeEvent event ) 
            {
                if( updating.get() )
                {
                    return;
                }
                
                updating.set( true );

                try
                {
                    final String newValue = convertToString( installConfig.getDefaultOutputFolder() );
                    defaultOutputFolderTextField.setText( newValue );
                }
                finally
                {
                    updating.set( false );
                }
            }
        };

        this.installConfig.addListener
	    (
	        modelEventListener,
	        JavaFacetInstallConfig.ChangeEvent.Type.DEFAULT_OUTPUT_FOLDER_CHANGED
	    );
        
        modelEventListener.handleEvent( null );
	}
	
    private ImageRegistry getImageRegistry()
    {
        return this.imageRegistry;
    }
    
    private IPath getSelectedSourceFolder()
    {
        final IStructuredSelection sel = (IStructuredSelection) this.sourceFoldersTreeViewer.getSelection();
        return (IPath) sel.getFirstElement();
    }

	private String convertToString( final IPath path )
	{
	    return ( path == null ? "" : path.toOSString() ); //$NON-NLS-1$
	}
	
	private void updateButtonEnablement()
	{
	    final boolean haveSelection = ! this.sourceFoldersTreeViewer.getSelection().isEmpty();
	    
	    this.editButton.setEnabled( haveSelection );
	    this.removeButton.setEnabled( haveSelection );
	}
	
    private void handleAddButtonPressed() 
    {
        final InputDialog dialog 
            = new InputDialog( this.addButton.getShell(), Resources.addSourceFolderDialogTitle,
                               Resources.addSourceFolderDialogMessage, null,
                               createSourceFolderInputValidator() );
        
        if( dialog.open() == Window.OK )
        {
            final String path = dialog.getValue();
            this.installConfig.addSourceFolder( new Path( path ) );
        }
    }

    private void handleEditButtonPressed() 
    {
        final IPath selectedSourceFolder = getSelectedSourceFolder();
        
        final InputDialog dialog 
            = new InputDialog( this.addButton.getShell(), Resources.editSourceFolderDialogTitle,
                               Resources.editSourceFolderDialogMessage,
                               selectedSourceFolder.toOSString(),
                               createSourceFolderInputValidator() );
        
        if( dialog.open() == Window.OK )
        {
            final IPath newSourceFolder = new Path( dialog.getValue() );
            final List<IPath> sourceFolders = new ArrayList<IPath>( this.installConfig.getSourceFolders() );
            final int position = sourceFolders.indexOf( selectedSourceFolder );
            sourceFolders.set( position, newSourceFolder );
            this.installConfig.setSourceFolders( sourceFolders );
        }
    }

    private void handleRemoveButtonPressed() 
    {
        final IPath selectedSourceFolder = getSelectedSourceFolder();
        this.installConfig.removeSourceFolder( selectedSourceFolder );
    }

    private void handleDisposeEvent()
    {
        this.imageRegistry.dispose();
    }

    private IInputValidator createSourceFolderInputValidator()
    {
        final IWorkspace ws = ResourcesPlugin.getWorkspace();
        final IFacetedProjectWorkingCopy fpjwc = this.installConfig.getFacetedProjectWorkingCopy();
        final String projectName = fpjwc.getProjectName();
        
        final IInputValidator validator = new IInputValidator()
        {
            public String isValid( final String newText ) 
            {
                final String fullPath = "/" + projectName + "/" + newText; //$NON-NLS-1$ //$NON-NLS-2$
                final IStatus result = ws.validatePath( fullPath, IResource.FOLDER );
                
                if( result.getSeverity() == IStatus.ERROR )
                {
                    return result.getMessage();
                }
                else
                {
                    return null;
                }
            }
        };
        
        return validator;
    }
    private final class SourceFoldersContentProvider

        implements ITreeContentProvider
    
    {
        public Object[] getElements( final Object element )
        {
            return getConfig().getSourceFolders().toArray();
        }
    
        public Object[] getChildren( final Object parent )
        {
            return new Object[ 0 ];
        }
    
        public Object getParent( final Object element )
        {
            return null;
        }
    
        public boolean hasChildren( final Object element )
        {
            return false;
        }
    
        public void dispose() { }
    
        public void inputChanged( final Viewer viewer,
                                  final Object oldObject,
                                  final Object newObject ) {}
    }
	
    private final class SourceFoldersLabelProvider

        implements ILabelProvider
    
    {
        public String getText( final Object element ) 
        {
            return ( (IPath) element ).toOSString();
        }

        public Image getImage( final Object element ) 
        {
            return getImageRegistry().get( IMG_KEY_SOURCE_FOLDER );
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
        public static String pageTitle;
        public static String pageDescription;
        public static String defaultOutputFolderLabel;
        public static String sourceFoldersLabel;
        public static String addFolderButton;
        public static String editButton;
        public static String removeButton;
        public static String addSourceFolderDialogTitle;
        public static String addSourceFolderDialogMessage;
        public static String editSourceFolderDialogTitle;
        public static String editSourceFolderDialogMessage;

        static 
        {
            initializeMessages( JavaFacetInstallPage.class.getName(), Resources.class );
        }
    }

}
