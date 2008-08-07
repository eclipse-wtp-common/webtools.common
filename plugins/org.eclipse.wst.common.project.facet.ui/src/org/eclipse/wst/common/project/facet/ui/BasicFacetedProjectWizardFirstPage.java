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

package org.eclipse.wst.common.project.facet.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 3.1
 */

public class BasicFacetedProjectWizardFirstPage

    extends WizardNewProjectCreationPage
    
{
    public BasicFacetedProjectWizardFirstPage( final String pageName ) 
    {
        super( pageName );
    }
    
    public void createControl( final Composite parent ) 
    {
        super.createControl( parent );
        
        final Composite composite = (Composite) getControl();
        
        createWorkingSetGroup( composite, ( (BasicFacetedProjectWizard) getWizard() ).getSelection(),
                               new String[] { "org.eclipse.ui.resourceWorkingSetPage" } ); //$NON-NLS-1$
        
        Dialog.applyDialogFont( composite );
    }

}
