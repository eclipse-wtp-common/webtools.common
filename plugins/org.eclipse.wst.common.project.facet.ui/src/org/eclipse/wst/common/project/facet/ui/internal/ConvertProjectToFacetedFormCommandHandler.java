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

package org.eclipse.wst.common.project.facet.ui.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ConvertProjectToFacetedFormCommandHandler

    extends AbstractHandler
    
{
    public Object execute( final ExecutionEvent event )
    
        throws ExecutionException
        
    {
        IProject project = null;
        final ISelection currentSelection = HandlerUtil.getCurrentSelection( event );
        
        if( currentSelection instanceof IStructuredSelection ) 
        {
            final Object element = ( (IStructuredSelection) currentSelection).getFirstElement();
            project = (IProject) Platform.getAdapterManager().getAdapter( element, IProject.class );
        } 

        if( project == null )
        {
            return null;
        }
        
        final IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindow( event );
        
        if( activeWorkbenchWindow != null )
        {
            final Shell shell = activeWorkbenchWindow.getShell();
            
            ConvertProjectToFacetedFormRunnable.runInProgressDialog( shell, project );
            
            final PreferenceDialog dialog 
                = PreferencesUtil.createPropertyDialogOn( shell, project, FacetsPropertyPage.ID, 
                                                          null, null, PreferencesUtil.OPTION_NONE );
            
            if( dialog != null ) 
            {
                dialog.open();
            }
        }
        
        return null;
    }
    
}
