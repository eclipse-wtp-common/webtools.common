/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * This interface is implemented by the wizard pages associated with project
 * facet actions.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IFacetWizardPage

    extends IWizardPage
    
{
    /**
     * The framework will call this method in order to provide the wizard
     * context to the wizard page. The wizard context can be used to find out
     * about other actions being configured by the wizard.
     * 
     * @param context the wizard context
     */
    
    void setWizardContext( IWizardContext context );
    
    /**
     * The framework will call this method in order to provide the action config
     * object that the wizard page should save user selection into. The
     * populated config object will then be passed to the action delegate.
     * 
     * @param config the action config object
     */
    
    void setConfig( Object config );
    
    /**
     * This method is called after the user has pressed the <code>Finish</code>
     * button. It allows the wizard page to transfer user selection into the
     * config object. Alternative, instead of using this method, the wizard
     * page could update the model on the fly as the user is making changes.
     */

    void transferStateToConfig();
    
}
