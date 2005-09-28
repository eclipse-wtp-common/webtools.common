/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.runtime;

import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IRuntimeComponent

    extends IAdaptable
    
{
    IRuntimeComponentType getRuntimeComponentType();
    IRuntimeComponentVersion getRuntimeComponentVersion();
    Map getProperties();
    String getProperty( String key );
}
