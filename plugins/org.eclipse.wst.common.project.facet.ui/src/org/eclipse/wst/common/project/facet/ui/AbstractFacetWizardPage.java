/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui;

import org.eclipse.jface.wizard.WizardPage;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public abstract class AbstractFacetWizardPage

    extends WizardPage
    implements IFacetWizardPage
    
{
    protected IWizardContext context;
    
    public AbstractFacetWizardPage( final String name )
    {
        super( name );
    }
    
    public final void setWizardContext( final IWizardContext context )
    {
        this.context = context;
    }
    
    public void transferStateToConfig()
    {
        
    }
    
}
