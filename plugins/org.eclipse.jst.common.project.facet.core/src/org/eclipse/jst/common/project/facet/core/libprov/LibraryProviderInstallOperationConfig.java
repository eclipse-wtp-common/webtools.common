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

package org.eclipse.jst.common.project.facet.core.libprov;


/**
 * This class should be subclassed in order to implement parameterization of the library
 * provider install operations. If no additional parameters are necessary, this class can also be 
 * used directly.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public class LibraryProviderInstallOperationConfig

    extends LibraryProviderOperationConfig
    
{
    private LibraryInstallDelegate libraryInstallDelegate;
    
    /**
     * Initializes the operation config. This method is called soon after the provider
     * is instantiated. Extenders can override in order to add to the initialization, but
     * have make sure to forward the init call up the inheritance chain.
     * 
     * @param libraryInstallDelegate the library install delegate that created this config object
     * @param provider the library provider (useful if the same operation config class
     *   is re-used between multiple providers)
     */
    
    public void init( final LibraryInstallDelegate libraryInstallDelegate,
                      final ILibraryProvider provider )
    {
        this.libraryInstallDelegate = libraryInstallDelegate;
        
        init( this.libraryInstallDelegate.getFacetedProject(),
              this.libraryInstallDelegate.getProjectFacetVersion(),
              provider );
    }

    /**
     * Returns the library install delegate that created this install operation config and is
     * controlling it's lifecycle.
     * 
     * @return the library install delegate that is controlling this operation config object
     */
    
    public final LibraryInstallDelegate getLibraryInstallDelegate()
    {
        return this.libraryInstallDelegate;
    }
    
}
