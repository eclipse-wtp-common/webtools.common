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

import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SavePresetDialog

    extends Dialog
    
{
    private static final String WIDTH = "width"; //$NON-NLS-1$
    private static final String HEIGHT = "height"; //$NON-NLS-1$

    private IDialogSettings settings;
    private Text nameTextField;
    private Text descTextField;
    private Label msgLabel;
    private String name;
    private String description;
    
    protected SavePresetDialog( final Shell shell )
    {
        super( shell );
        
        setShellStyle( getShellStyle() | SWT.RESIZE );
    }
    
    public static IPreset showDialog( final Shell shell,
                                      final Set<IProjectFacetVersion> facets )
    {
        final SavePresetDialog dialog = new SavePresetDialog( shell );
        
        IPreset preset = null;

        if( dialog.open() == IDialogConstants.OK_ID )
        {
            final String name = dialog.name;
            final String desc = dialog.description;
            
            preset = ProjectFacetsManager.definePreset( name, desc, facets );
        }
        
        return preset;
    }

    protected void configureShell( final Shell shell )
    {
        super.configureShell( shell );
        shell.setText( Resources.dialogTitle );
    }

    protected Control createDialogArea( final Composite parent ) 
    {
        final IDialogSettings root
            = FacetUiPlugin.getInstance().getDialogSettings();
    
        IDialogSettings temp = root.getSection( getClass().getName() );
    
        if( temp == null )
        {
            temp = root.addNewSection( getClass().getName() );
        }
        
        if( temp.get( WIDTH ) == null ) temp.put( WIDTH, 300 );
        if( temp.get( HEIGHT ) == null ) temp.put( HEIGHT, 160 );

        this.settings = temp;
        
        final Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout( 1, false ) );
        
        final GridData gd = gdfill();
        gd.widthHint = this.settings.getInt( WIDTH );
        gd.heightHint = this.settings.getInt( HEIGHT );
        
        composite.setLayoutData( gd );
        
        composite.addListener
        (
            SWT.Resize,
            new Listener()
            {
                public void handleEvent( final Event event )
                {
                    final Point size = composite.getSize();
                    
                    SavePresetDialog.this.settings.put( WIDTH, size.x );
                    SavePresetDialog.this.settings.put( HEIGHT, size.y );
                }
            }
        );
        
        final Label nameLabel = new Label( composite, SWT.NONE );
        nameLabel.setLayoutData( gdhfill() );
        nameLabel.setText( Resources.nameLabel );
        
        this.nameTextField = new Text( composite, SWT.NONE | SWT.BORDER );
        this.nameTextField.setLayoutData( gdhfill() );
        
        this.nameTextField.addModifyListener
        (
            new ModifyListener()
            {
                public void modifyText( final ModifyEvent e )
                {
                    handleNameChange();
                }
            }
        );
        
        final Label descLabel = new Label( composite, SWT.NONE );
        descLabel.setLayoutData( gdhfill() );
        descLabel.setText( Resources.descLabel );
        
        this.descTextField 
            = new Text( composite, SWT.NONE | SWT.V_SCROLL | SWT.BORDER | SWT.WRAP );
        
        this.descTextField.setLayoutData( gdfill() );
        
        this.descTextField.addModifyListener
        (
            new ModifyListener()
            {
                public void modifyText( final ModifyEvent e )
                {
                    handleDescriptionChange();
                }
            }
        );
        
        this.msgLabel = new Label( composite, SWT.NONE );
        this.msgLabel.setLayoutData( gdhfill() );
        this.msgLabel.setForeground( color( SWT.COLOR_RED ) );
        
        handleNameChange();
        
        return composite;
    }
    
    private void handleNameChange()
    {
        this.name = this.nameTextField.getText().trim();
        
        boolean okButtonEnabled = false;
        boolean conflictDetected = false;
        
        if( this.name.length() > 0 )
        {
            for( IPreset preset : ProjectFacetsManager.getPresets() )
            {
                if( preset.getLabel().equals( this.name ) )
                {
                    conflictDetected = true;
                    break;
                }
            }
            
            if( ! conflictDetected )
            {
                okButtonEnabled = true;
            }
        }
        
        if( conflictDetected )
        {
            this.msgLabel.setText( Resources.nameInUseMessage );
        }
        else
        {
            this.msgLabel.setText( "" ); //$NON-NLS-1$
        }
        
        setOkButtonEnabled( okButtonEnabled );
    }
    
    private void handleDescriptionChange()
    {
        this.description = this.descTextField.getText().trim();
    }
    
    private void setOkButtonEnabled( final boolean enabled )
    {
        Button button = getButton( IDialogConstants.OK_ID );
        
        if( button != null )
        {
            button.setEnabled( enabled );
        }
        else
        {
            // If the button has not been created yet (this happens during the
            // execution of the createDialogArea method), spin off a thread to
            // wait for it's creation.
            
            final Display display = Display.getCurrent();
            
            final Thread t = new Thread()
            {
                public void run()
                {
                    Button b = getButton( IDialogConstants.OK_ID );
                    
                    while( b == null )
                    {
                        try
                        {
                            Thread.sleep( 50 );
                        }
                        catch( InterruptedException e ) {}
                        
                        b = getButton( IDialogConstants.OK_ID );
                    }
                    
                    final Button finalButton = b;
                    
                    display.asyncExec( new Runnable()
                    {
                        public void run()
                        {
                            finalButton.setEnabled( enabled );
                        }
                    } );
                }
            };
            
            t.start();
        }
    }
    
    private static GridData gdfill()
    {
        return new GridData( SWT.FILL, SWT.FILL, true, true );
    }

    private static GridData gdhfill()
    {
        return new GridData( GridData.FILL_HORIZONTAL );
    }
    
    public static Color color( final int id )
    {
        return Display.getCurrent().getSystemColor( id );       
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String dialogTitle;
        public static String nameLabel;
        public static String descLabel;
        public static String nameInUseMessage;
        
        static
        {
            initializeMessages( SavePresetDialog.class.getName(), 
                                Resources.class );
        }
    }
    
}
