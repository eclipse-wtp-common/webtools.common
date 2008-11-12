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

package org.eclipse.jst.common.project.facet.ui.libprov.user;

import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gd;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.UserLibraryManager;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
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
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.wst.common.project.facet.ui.internal.util.EnhancedHyperlink;

/**
 * The install operation panel corresponding to the user-library-provider that uses JDT user library facility
 * for managing libraries. This class can be subclassed by those wishing to extend the base implementation
 * supplied by the framework.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public class UserLibraryProviderInstallPanel

    extends LibraryProviderOperationPanel
    
{
    private Composite rootComposite = null;
    private CheckboxTableViewer libsTableViewer = null;
    
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
        
        final Table libsTable = new Table( this.rootComposite, SWT.CHECK | SWT.BORDER );
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
        
        final Composite manageLibrariesHyperlinkComposite = new Composite( this.rootComposite, SWT.NONE );
        manageLibrariesHyperlinkComposite.setLayoutData( gdhfill() );
        manageLibrariesHyperlinkComposite.setLayout( gl( 3, 0, 0 ) );
        
        final EnhancedHyperlink manageLibrariesHyperlink 
            = new EnhancedHyperlink( manageLibrariesHyperlinkComposite, SWT.NONE );
        
        manageLibrariesHyperlink.setLayoutData( gd() );
        manageLibrariesHyperlink.setText( Resources.manageLibrariesLink );
        
        manageLibrariesHyperlink.addHyperlinkListener
        (
            new HyperlinkAdapter()
            {
                public void linkActivated( final HyperlinkEvent event )
                {
                    final String id = UserLibraryPreferencePage.ID;
                    final Shell shell = manageLibrariesHyperlink.getShell();
                    
                    final PreferenceDialog dialog 
                        = PreferencesUtil.createPreferenceDialogOn( shell, id, new String[] { id }, null );
                    
                    if( dialog.open() == Window.OK )
                    {
                        UserLibraryProviderInstallPanel.this.libsTableViewer.refresh();
                    }
                }
            }
        );
        
        final Label spacer = new Label( manageLibrariesHyperlinkComposite, SWT.NONE );
        spacer.setLayoutData( gdhfill() );
        
        final Control controlNextToManageHyperlink 
            = createControlNextToManageHyperlink( manageLibrariesHyperlinkComposite );
        
        controlNextToManageHyperlink.setLayoutData( gd() );
        
        return this.rootComposite;
    }
    
    /**
     * This method can be overridden to create a control to the right of the manage libraries
     * hyperlink. The default implementation creates an invisible filler control.
     * 
     * @param parent the parent composite 
     * @return the created control
     */
    
    protected Control createControlNextToManageHyperlink( final Composite parent )
    {
        return new Label( parent, SWT.NONE );
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
        public Image getImage( final Object element )
        {
            return JavaPluginImages.get( JavaPluginImages.IMG_OBJS_LIBRARY );
        }

        public String getText( final Object element )
        {
            return (String) element;
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String manageLibrariesLink;

        static
        {
            initializeMessages( UserLibraryProviderInstallPanel.class.getName(), 
                                Resources.class );
        }
    }

}