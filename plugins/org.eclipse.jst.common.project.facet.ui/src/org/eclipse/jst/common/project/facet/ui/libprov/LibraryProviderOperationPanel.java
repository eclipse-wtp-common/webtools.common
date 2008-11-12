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

import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperationConfig;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This class should be subclassed in order to implement a configuration panel for library
 * provider operations. It is used together with the 
 * org.eclipse.jst.common.project.facet.ui.libraryProviderActionPanels extension point.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public abstract class LibraryProviderOperationPanel
{
    private LibraryProviderOperationConfig config = null;
    
    /**
     * Returns the library provider operation config that this operation panel is bound to.
     * 
     * @return the library provider operation config that this operation panel is bound to
     */
    
    public LibraryProviderOperationConfig getOperationConfig()
    {
        return this.config;
    }
    
    /**
     * Sets the library provider operation config that this panel should bind to.
     * 
     * @param config the library provider operation config that this panel should bind to
     */
    
    public void setOperationConfig( final LibraryProviderOperationConfig config )
    {
        this.config = config;
    }
    
    /**
     * Creates the panel control.
     * 
     * @param parent the parent composite
     * @return the created control
     */
    
    public abstract Control createControl( final Composite parent );
}
