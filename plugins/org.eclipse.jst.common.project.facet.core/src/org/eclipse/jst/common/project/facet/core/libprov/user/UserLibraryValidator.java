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

package org.eclipse.jst.common.project.facet.core.libprov.user;

import java.util.List;

import org.eclipse.core.runtime.IStatus;

/**
 * <p>Used to provide custom validation for user library providers. To associate a validator 
 * with a user library provider declaration, use syntax like the following. Only one validator
 * can be registered per provider, but any number of validator parameters can be specified using 
 * naming convention starting with 0.</p>
 *
 * <pre>&lt;extension point="org.eclipse.jst.common.project.facet.core.libraryProviders">
 *   &lt;provider id="..." extends="user-library-provider">
 *     ...
 *     &lt;param name="validator" value="org.eclipse.jst.common.project.facet.core.libprov.user.KeyClassesValidator"/>
 *     &lt;param name="validator.param.0" value="javax.persistence.Entity"/>
 *     ...
 *   &lt;/provider>
 * &lt;/extension></pre>
 * 
 * @see KeyClassesValidator
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public abstract class UserLibraryValidator
{
    /**
     * Called by the framework to initialize the validator. The list of parameters is computed
     * based on values of library provider parameters with names that follow "validator.param.x"
     * convention, where 'x' is a number starting with 0.
     * 
     * <p>The default implementation does not do anything.</p>
     * 
     * @param params the validator parameters
     */
    
    public void init( final List<String> params )
    {
    }

    /**
     * Called by the framework to validate the user library provider install operation config.
     * 
     * @param config the user library provider install operation config
     * @return result of validation
     */
    
    public abstract IStatus validate( final UserLibraryProviderInstallOperationConfig config );

}
