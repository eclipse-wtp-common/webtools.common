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

package org.eclipse.jst.common.project.facet.ui.libprov;

import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.jst.common.project.facet.ui.libprov.internal.LibraryProviderFrameworkUiImpl;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * The root entry point for working with the UI portion of the Library Provider Framework.
 * 
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public final class LibraryProviderFrameworkUi
{
    private LibraryProviderFrameworkUi() {}
    
    /**
     * Creates a library install panel where the user can select and configure a library.
     * This method variant will always use the default label for the library selection
     * panel.
     * 
     * @param parent the parent composite
     * @param delegate the install delegate that the panel should bind to
     * @return the created panel control
     */
    
    public static Control createInstallLibraryPanel( final Composite parent,
                                                     final LibraryInstallDelegate delegate )
    {
        return createInstallLibraryPanel( parent, delegate, null );
    }
    
    /**
     * Creates a library install panel where the user can select and configure a library.
     * 
     * @param parent the parent composite
     * @param delegate the install delegate that the panel should bind to
     * @param label the label to use for the panel or <code>null</code> to use the default
     * @return the created panel control
     */
    
    public static Control createInstallLibraryPanel( final Composite parent,
                                                     final LibraryInstallDelegate delegate,
                                                     final String label )
    {
        return LibraryProviderFrameworkUiImpl.get().createInstallLibraryPanel( parent, delegate, label );
    }
    
}
