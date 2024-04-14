/******************************************************************************
 * Copyright (c) 2010, 2024 Oracle and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.ui.libprov.user.internal;

import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.log;
import static org.eclipse.jst.common.project.facet.ui.internal.FacetedProjectFrameworkJavaExtUiPlugin.IMG_PATH_WIZBAN_DOWNLOAD_LIBRARY;
import static org.eclipse.jst.common.project.facet.ui.internal.FacetedProjectFrameworkJavaExtUiPlugin.getImageDescriptor;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gd;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jst.common.project.facet.core.libprov.user.internal.DownloadableLibrary;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class DownloadLibraryWizardLicensePage 

    extends WizardPage 
    
{
    private Browser browser;
    private Button acceptLicenseCheckbox;
    
    public DownloadLibraryWizardLicensePage() 
    {
        super( "DownloadLibraryWizardLicensePage" ); //$NON-NLS-1$
        
        setTitle( Resources.pageTitle );
        setDescription( Resources.pageDescription );
        setImageDescriptor( getImageDescriptor( IMG_PATH_WIZBAN_DOWNLOAD_LIBRARY ) );
    }
    
    public void createControl( final Composite parent ) 
    {
        final Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( gl( 1 ) );
        
        this.browser = new Browser( composite, SWT.BORDER );
        this.browser.setLayoutData( gdfill() );
        
        this.acceptLicenseCheckbox = new Button( composite, SWT.CHECK );
        this.acceptLicenseCheckbox.setLayoutData( gd() );
        this.acceptLicenseCheckbox.setText( Resources.acceptLicenseCheckboxLabel );
        
        this.acceptLicenseCheckbox.addSelectionListener
        (
            new SelectionAdapter()
            {
                @Override
                public void widgetSelected( final SelectionEvent event )
                {
                    getContainer().updateButtons();
                }
            }
        );
        
        setControl( composite );
    }
    
    @Override
    public boolean isPageComplete()
    {
        return ( this.acceptLicenseCheckbox.getSelection() == true );
    }
    
	public void setLibrary(final DownloadableLibrary library) {
		String url = (library == null ? null : library.getLicenseUrl());
		if (!isValidURL(url)) {
			try {
				final Bundle plugin = Platform.getBundle(library.getPluginId());
				URL localUrl = plugin != null ? FileLocator.find(plugin, new Path(url), null) : null;
				if (localUrl != null) {
					url = FileLocator.resolve(localUrl).toString();
				}
			} catch (Exception e) {
				log(e);
			}
		}
		this.browser.setUrl(url != null ? url : ""); //$NON-NLS-1$
		this.acceptLicenseCheckbox.setSelection(url != null ? false : true);
	}

	private static boolean isValidURL(String url) {
		try {
			new URL(url).toURI();
			return true;
		} catch (MalformedURLException e) {
			return false;
		} catch (URISyntaxException e) {
			return false;
		}
	}

    private static final class Resources
    
        extends NLS
        
    {
        public static String pageTitle;
        public static String pageDescription;
        public static String acceptLicenseCheckboxLabel;
    
        static
        {
            initializeMessages( DownloadLibraryWizardLicensePage.class.getName(), 
                                Resources.class );
        }
    }
    
}
