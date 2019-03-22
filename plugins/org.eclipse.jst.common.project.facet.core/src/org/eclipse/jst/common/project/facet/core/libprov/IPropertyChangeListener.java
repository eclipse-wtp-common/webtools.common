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

/**
 * The common interface that's used throughout the Library Provider Framework to implement
 * listeners.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public interface IPropertyChangeListener
{
    /**
     * Called when property is changed.
     * 
     * @param property the name of the property
     * @param oldValue the old value
     * @param newValue the new value
     */
    
    void propertyChanged( String property,
                          Object oldValue,
                          Object newValue );
}

