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

package org.eclipse.wst.common.project.facet.core;

import java.util.Set;

/**
 * A group is a named collection collection of {@see IProjectFacetVersion} 
 * objects. It's used for a variety of purposes including as a parameter to 
 * the "one-of" constraint. A given project facet version can belong to
 * several groups.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IGroup 
{
    /**
     * Returns the id of this set.
     * 
     * @return the id of this set
     */
    
    String getId();
    
    /**
     * Returns the set of member project facets.
     * 
     * @return the set of member project facets (element type: {@link 
     * IProjectFacetVersion})
     */
    
    Set getMembers();

}
