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

package org.eclipse.jst.common.project.facet.ui.libprov.internal;

import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gd;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.IPropertyChangeListener;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderActionType;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperationConfig;
import org.eclipse.jst.common.project.facet.ui.libprov.LibraryProviderOperationPanel;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.PageBook;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LibraryInstallPanel

    extends Composite
    
{
    private final LibraryInstallDelegate delegate;
    private final Group providersGroup;
    private final Combo providersCombo;
    private final PageBook providersPageBook;
    private final Map<ILibraryProvider,Control> providerPages;
    private final Composite noProviderComposite;
    
    public LibraryInstallPanel( final Composite parent,
                                final LibraryInstallDelegate delegate,
                                final String label )
    {
        super( parent, SWT.NONE );
        
        setLayout( gl( 1, 0, 0 ) );
        
        this.delegate = delegate;
        this.providerPages = new HashMap<ILibraryProvider,Control>();
        
        this.providersGroup = new Group( this, SWT.NONE );
        this.providersGroup.setLayoutData( gdhfill() );
        this.providersGroup.setLayout( gl( 1 ) );
        this.providersGroup.setText( label != null ? label : Resources.providersGroupLabel );
        
        final Composite providersComboComposite = new Composite( this.providersGroup, SWT.NONE );
        providersComboComposite.setLayoutData( gdhfill() );
        providersComboComposite.setLayout( gl( 2, 0, 0 ) );
        
        final Label providersComboLabel = new Label( providersComboComposite, SWT.NONE );
        providersComboLabel.setLayoutData( gd() );
        providersComboLabel.setText( Resources.providersComboLabel );
        
        this.providersCombo = new Combo( providersComboComposite, SWT.BORDER | SWT.READ_ONLY );
        this.providersCombo.setLayoutData( gdhfill() );
        
        this.providersCombo.addSelectionListener
        ( 
            new SelectionAdapter()
            {
                @Override
                public void widgetSelected( final SelectionEvent event )
                {
                    handleProviderSelected();
                }
            }
        );
        
        final Label separator = new Label( this.providersGroup, SWT.SEPARATOR | SWT.HORIZONTAL );
        separator.setLayoutData( gdhfill() );
        
        this.providersPageBook = new PageBook( this.providersGroup, SWT.NONE );
        this.providersPageBook.setLayoutData( gdhfill() );
        
        this.noProviderComposite = new Composite( this.providersPageBook, SWT.NONE );
        this.noProviderComposite.setLayout( gl( 1 ) );
        this.noProviderComposite.setVisible( false );
        
        final Label noProviderLabel = new Label( this.noProviderComposite, SWT.NONE );
        noProviderLabel.setText( Resources.noProvidersAvailable );
        noProviderLabel.setLayoutData( gdhfill() );
        
        handleProvidersSetChanged();
        
        final IPropertyChangeListener listener = new IPropertyChangeListener()
        {
            public void propertyChanged( final String property,
                                         final Object oldValue,
                                         final Object newValue )
            {
                handleOperationConfigChanged( property, oldValue, newValue );
            }
        }; 
        
        this.delegate.addListener( listener ); 
        
        this.addDisposeListener
        (
            new DisposeListener()
            {
                public void widgetDisposed( final DisposeEvent event )
                {
                    LibraryInstallPanel.this.delegate.removeListener( listener );
                }
            }
        );
    }
    
    private void handleProvidersSetChanged()
    {
        this.providerPages.clear();
        this.providersCombo.setItems( new String[ 0 ] );
        
        final List<ILibraryProvider> providers = this.delegate.getLibraryProviders();
        final ILibraryProvider selectedProvider = this.delegate.getLibraryProvider();
        
        for( ILibraryProvider provider : providers )
        {
            this.providersCombo.add( provider.getLabel() );
            
            final int index = this.providersCombo.getItemCount() - 1;
            this.providersCombo.setData( String.valueOf( index ), provider );
            
            final LibraryProviderOperationConfig cfg 
                = this.delegate.getLibraryProviderOperationConfig( provider );

            final LibraryProviderOperationPanel panel 
                = LibraryProviderFrameworkUiImpl.get().getOperationPanel( provider, LibraryProviderActionType.INSTALL );
            
            if( panel != null )
            {
                panel.setOperationConfig( cfg );
                
                final Control panelControl = panel.createControl( this.providersPageBook );
                panelControl.setLayoutData( gdfill() );
                panelControl.setVisible( false );
                
                this.providerPages.put( provider, panelControl );
            }

            if( provider.equals( selectedProvider ) )
            {
                this.providersCombo.select( index );
            }
        }

        handleProviderSelected();
    }
    
    private void handleProviderSelected()
    {
        final int selection = this.providersCombo.getSelectionIndex();
        
        if( selection == -1 )
        {
            this.delegate.setLibraryProvider( null );
            this.providersPageBook.showPage( this.noProviderComposite );
        }
        else
        {
            final ILibraryProvider provider 
                = (ILibraryProvider) this.providersCombo.getData( String.valueOf( selection ) );
         
            this.delegate.setLibraryProvider( provider );
            
            final Control providerPanelControl = this.providerPages.get( provider );
            this.providersPageBook.showPage( providerPanelControl );
        }
        
        getShell().layout( true, true );
    }
    
    private void handleOperationConfigChanged( final String property,
                                               final Object oldValue,
                                               final Object newValue )
    {
        if( getDisplay().getThread() != Thread.currentThread() )
        {
            getDisplay().asyncExec
            (
                new Runnable()
                {
                    public void run()
                    {
                        handleOperationConfigChanged( property, oldValue, newValue );
                    }
                }
            );
            
            return;
        }
        
        if( property.equals( LibraryInstallDelegate.PROP_AVAILABLE_PROVIDERS ) )
        {
            handleProvidersSetChanged();
        }
        else if( property.equals( LibraryInstallDelegate.PROP_SELECTED_PROVIDER ) )
        {
            final ILibraryProvider selectedProvider = this.delegate.getLibraryProvider();
            
            for( int i = 0, n = this.providersCombo.getItemCount(); i < n; i++ )
            {
                if( this.providersCombo.getData( String.valueOf( i ) ).equals( selectedProvider ) )
                {
                    this.providersCombo.select( i );
                    break;
                }
            }
            
            handleProviderSelected();
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String providersGroupLabel;
        public static String providersComboLabel;
        public static String noProvidersAvailable;
        
        static
        {
            initializeMessages( LibraryInstallPanel.class.getName(), 
                                Resources.class );
        }
    }
    
}
