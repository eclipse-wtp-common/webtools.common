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

package org.eclipse.jst.common.project.facet.core.libprov;

import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.PLUGIN_ID;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectBase;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * The install operation config corresponding to the no-op-library-provider that allows library
 * configuration to be disabled by user. This class can be subclassed by those wishing to extend 
 * the base implementation supplied by the framework.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public class NoOpLibraryProviderInstallOperationConfig

    extends LibraryProviderOperationConfig
    
{
    public static final String PARAM_WARNING = "warning";  //$NON-NLS-1$
    
    private String warning = null;
    
    /**
     * Constructs the no-op library provider install operation config.
     */
    
    public NoOpLibraryProviderInstallOperationConfig()
    {
        this.warning = null;
    }

    /**
     * Initializes the operation config. This method is called soon after the provider
     * is instantiated. Extenders can override in order to add to the initialization, but
     * have make sure to forward the init call up the inheritance chain.
     * 
     * @param fpj the faceted project (or a working copy)
     * @param fv the project facet that is making the request for libraries
     * @param provider the library provider (useful if the same operation config class
     *   is re-used between multiple providers)
     */
    
    @Override
    public synchronized void init( final IFacetedProjectBase fproj,
                                   final IProjectFacetVersion fv,
                                   final ILibraryProvider provider )
    {
        super.init( fproj, fv, provider );
        
        this.warning = provider.getParams().get( PARAM_WARNING );
    }
    
    /**
     * Validates the state of this operation config object and returns a status object
     * describing any problems. If no problems are detected, this should return OK
     * status.
     * 
     * @return the result of validating this operation config
     */
    
    @Override
    public synchronized IStatus validate()
    {
        IStatus st = Status.OK_STATUS;
        
        if( this.warning != null )
        {
            st = new Status( IStatus.WARNING, PLUGIN_ID, this.warning );
        }
        
        return st;
    }
    
}
