/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.ui.AddRemoveFacetsWizard;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class AddRemoveFacetsAction

    implements IObjectActionDelegate
    
{
    private Shell shell = null;
    private ISelection selection = null;
    
    public void setActivePart( final IAction action, 
                               final IWorkbenchPart targetPart ) 
    {
        this.shell = targetPart.getSite().getShell();
    }

    public void run( final IAction action ) 
    {
        if( this.selection instanceof IStructuredSelection )
        {
            final IStructuredSelection ssel 
                = (IStructuredSelection) this.selection;
            
            final IProject project = (IProject) ssel.getFirstElement();
            final IFacetedProject fproj;
            
            try
            {
                fproj = ProjectFacetsManager.create( project );
            }
            catch( CoreException e )
            {
                ErrorDialog.openError( this.shell, Resources.errDlgTitle,
                                       e.getMessage(), e.getStatus() );
                
                FacetUiPlugin.log( e );
                
                return;
            }
            
            final IWizard wizard = new AddRemoveFacetsWizard( fproj );
            final WizardDialog dialog = new WizardDialog( this.shell, wizard );
            
            dialog.open();
        }
    }

    public void selectionChanged( final IAction action, 
                                  final ISelection selection ) 
    {
        this.selection = selection;
    }

    private static final class Resources
    
        extends NLS
        
    {
        public static String errDlgTitle;
        
        static
        {
            initializeMessages( AddRemoveFacetsAction.class.getName(), 
                                Resources.class );
        }
    }
    
}
