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

package org.eclipse.wst.common.project.facet.core;

/**
 * The compiled form of a version expression. A version expression is used to
 * specify one or more versions.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IVersionExpr
{
    /**
     * Contains the symbol used to represent the wildcard. The wildcard matches
     * any version. Note that it is not possible to use the wildcard to match
     * part of the version string. 
     */
    
    static final String WILDCARD_SYMBOL = "*"; //$NON-NLS-1$
    
    /**
     * Evaluates the version expression against the specified version. Returns
     * <code>true</code> if and only if the version expression matches the
     * specified version.
     * 
     * @param version the version object to check against the version expression
     * @return <code>true</code> if and only if the version expression matches
     *   the specified version
     */
    
    boolean check( IVersion version );
    
    /**
     * @deprecated use the check method instead
     */
     
    boolean evaluate( String version );
    
    /**
     * Returns human-readable form of the version expression that uses
     * descriptive terms rather than symbols.
     * 
     * @return human-readable form of the version expression
     */
    
    String toDisplayString();
    
}
