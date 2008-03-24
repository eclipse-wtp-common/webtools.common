/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
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
 * This interface should not be referenced directly by clients. Client code should only
 * reference <code>IProjectFacetVersion</code> and <code>IRuntimeComponentVersion</code>,
 * which extend this interface.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public abstract interface IVersion

    extends Comparable
    
{
    String getVersionString();
}
