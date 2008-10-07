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

package org.eclipse.jst.common.project.facet.core.libprov;

/**
 * The common interface that's used throughout the Library Provider Framework to implement
 * listeners.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since WTP 3.1
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

