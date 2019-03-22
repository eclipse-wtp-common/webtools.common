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

package org.eclipse.wst.common.project.facet.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

/**
 * Exception that can be thrown in various places in the framework instead of a more generic
 * {@link CoreException}. 
 * 
 * @since 1.4
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectFrameworkException

    extends CoreException
    
{
    private static final long serialVersionUID = 1L;
    
    private boolean expected;
    
    /**
     * Creates a new faceted project framework exception instance.
     * 
     * @param status the status object with information about the failure
     */

    public FacetedProjectFrameworkException( final IStatus status ) 
    {
        super( status );
        
        this.expected = false;
    }
    
    /**
     * Returns whether this exception is expected from thrower's perspective. This can be used
     * as a signal by handlers to alter the way the exception is presented to the user.
     * 
     * @return <code>true</code> if the exception is expected from thrower's perspective
     */
    
    public boolean isExpected()
    {
        return this.expected;
    }
    
    /**
     * Sets whether this exception is expected from thrower's perspective. This can be used
     * as a signal by handlers to alter the way the exception is presented to the user.
     * 
     * @param expected if the exception is expected from thrower's perspective
     */
    
    public void setExpected( final boolean expected )
    {
        this.expected = expected;
    }

}
