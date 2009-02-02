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

package org.eclipse.jst.common.project.facet.ui.libprov;

import static org.eclipse.jst.common.project.facet.ui.libprov.LibraryProviderFrameworkUi.createInstallLibraryPanel;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.common.project.facet.core.libprov.IPropertyChangeListener;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryFacetInstallConfig;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public abstract class LibraryFacetInstallPage

    extends AbstractFacetWizardPage

{
    private LibraryFacetInstallConfig config = null;
    private LibraryInstallDelegate libraryInstallDelegate = null;
    private Composite rootComposite = null;
    
    public LibraryFacetInstallPage( final String name )
    {
        super( name );
    }
    
    public void setConfig( final Object config ) 
    {
        this.config = (LibraryFacetInstallConfig) config;
        this.libraryInstallDelegate = this.config.getLibraryInstallDelegate();
    }
    
    protected LibraryInstallDelegate getLibraryInstallDelegate()
    {
        return this.libraryInstallDelegate;
    }

    public final void createControl( final Composite parent )
    {
        this.rootComposite = new Composite( parent, SWT.NONE );
        this.rootComposite.setLayout( gl( 1 ) );
    
        final Control control = createPageContents( this.rootComposite );
        control.setLayoutData( gdhfill() );
        
        updateValidation();
        
        setControl( this.rootComposite );
    }
    
    /**
     * The method that creates the actual interesting page content. It can be overridden
     * as necessary to expand the scope of information managed by the page. 
     * 
     * @param parent the parent composite
     * @return the create control with all the page contents
     */
    
    protected Control createPageContents( final Composite parent )
    {
        final IPropertyChangeListener delegateListener = new IPropertyChangeListener()
        {
            public void propertyChanged( final String property,
                                         final Object oldValue,
                                         final Object newValue )
            {
                updateValidation();
            }
        };
        
        this.libraryInstallDelegate.addListener( delegateListener );
        
        this.rootComposite.addDisposeListener
        (
            new DisposeListener()
            {
                public void widgetDisposed( final DisposeEvent event )
                {
                    getLibraryInstallDelegate().removeListener( delegateListener );
                }
            }
        );
        
        return createInstallLibraryPanel( parent, this.libraryInstallDelegate );
    }
    
    protected final void updateValidation()
    {
        if( this.rootComposite.isDisposed() )
        {
            return;
        }
        
        if( this.rootComposite.getDisplay().getThread() != Thread.currentThread() )
        {
            this.rootComposite.getDisplay().asyncExec
            (
                new Runnable()
                {
                    public void run()
                    {
                        updateValidation();
                    }
                }
            );
            
            return;
        }
        
        final IStatus st = performValidation();
        final int severity = st.getSeverity();
        
        if( severity == IStatus.ERROR )
        {
            setMessage( st.getMessage(), ERROR );
            setPageComplete( false );
        }
        else
        {
            if( severity == IStatus.WARNING )
            {
                setMessage( st.getMessage(), WARNING );
            }
            else if( severity == IStatus.INFO )
            {
                setMessage( st.getMessage(), INFORMATION );
            }
            else
            {
                setMessage( null );
            }
            
            setPageComplete( true );
        }
    }
    
    /**
     * The method that performs validation of controls displayed on the page. It can be
     * overridden as necessary to incorporate validation of additional controls. The
     * subclass can call updateValidation() method to refresh page validation.
     * 
     * @return the validation result
     */
    
    protected IStatus performValidation()
    {
        if( this.libraryInstallDelegate != null )
        {
            return this.libraryInstallDelegate.validate();
        }
        
        return Status.OK_STATUS;
    }

}
