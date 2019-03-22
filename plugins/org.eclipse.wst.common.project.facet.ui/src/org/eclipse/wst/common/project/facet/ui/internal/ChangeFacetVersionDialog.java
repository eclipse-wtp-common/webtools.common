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

import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gd;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhspan;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdvindent;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdwhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glmargins;

import java.util.SortedSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ChangeFacetVersionDialog

    extends Dialog
    
{
	private final IProjectFacet facet;
	private final IProjectFacetVersion oldVersion;
	private IProjectFacetVersion newVersion;
	private final SortedSet<IProjectFacetVersion> availableVersions;
	private Combo versionCombo;
    
    private ChangeFacetVersionDialog( final Shell shell,
                                      final IProjectFacet facet,
                                      final IProjectFacetVersion currentVersion,
                                      final SortedSet<IProjectFacetVersion> availableVersions )
    {
        super( shell );
        
        this.facet = facet;
        this.oldVersion = currentVersion;
        this.newVersion = currentVersion;
        this.availableVersions = availableVersions;
    }
    
    public static IProjectFacetVersion showDialog( final Shell shell,
    		                                       final IProjectFacet facet,
    		                                       final IProjectFacetVersion currentVersion,
    		                                       final SortedSet<IProjectFacetVersion> availableVersions )
    {
        final ChangeFacetVersionDialog dialog 
        	= new ChangeFacetVersionDialog( shell, facet, currentVersion, availableVersions );
        
        IProjectFacetVersion selectedVersion = null;

        if( dialog.open() == IDialogConstants.OK_ID )
        {
        	selectedVersion = dialog.newVersion;
        }
        
        return selectedVersion;
    }

    protected void configureShell( final Shell shell )
    {
        super.configureShell( shell );
        shell.setText( Resources.dialogTitle );
    }

    protected Control createDialogArea( final Composite parent ) 
    {
        final Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( glmargins( gl( 2 ), 5, 5, 5, 10 ) );
        composite.setLayoutData( gdwhint( gdfill(), 300 ) );
        
        final Label promptLabel = new Label( composite, SWT.WRAP );
        promptLabel.setLayoutData( gdhspan( gdhfill(), 2 ) );
        promptLabel.setText( Resources.bind( Resources.dialogPrompt, this.facet.getLabel() ) );
        
        final Label versionFieldLabel = new Label( composite, SWT.NONE );
        versionFieldLabel.setLayoutData( gdvindent( gd(), 8 ) );
        versionFieldLabel.setText( Resources.versionFieldLabel );

        this.versionCombo = new Combo( composite, SWT.DROP_DOWN | SWT.READ_ONLY );
        this.versionCombo.setLayoutData( gdvindent( gd(), 8 ) );
        
        for( IProjectFacetVersion fv : this.availableVersions )
        {
        	this.versionCombo.add( fv.getVersionString() );
        	
        	if( fv == this.oldVersion )
        	{
        		this.versionCombo.select( this.versionCombo.getItemCount() - 1 );
        	}
        }
        
        this.versionCombo.addSelectionListener
        (
        	new SelectionAdapter()
        	{
        		public void widgetSelected( final SelectionEvent event ) 
        		{
        			handleVersionSelected();
        		}
        	}
        );
        
        return composite;
    }
    
    private void handleVersionSelected()
    {
    	final int index = this.versionCombo.getSelectionIndex();
    	
    	int i = 0;
    	
    	for( IProjectFacetVersion fv : this.availableVersions )
    	{
    		if( i == index )
    		{
    			this.newVersion = fv;
    		}
    		
    		i++;
    	}
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String dialogTitle;
        public static String dialogPrompt;
        public static String versionFieldLabel;
        
        static
        {
            initializeMessages( ChangeFacetVersionDialog.class.getName(), 
                                Resources.class );
        }
    }
    
}
