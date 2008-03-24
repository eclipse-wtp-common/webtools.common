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

import java.util.Set;

/**
 * A group is a named collection of {@see IProjectFacetVersion}  objects. It is 
 * used primarily as a parameter to the "requires" and "conflicts" constraints 
 * and allows a level of indirection where a facet does not need to know about 
 * all the members of the group. A given project facet version can belong to 
 * several groups.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IGroup 
{
    /**
     * Returns the id of this group.
     * 
     * @return the id of this group
     */
    
    String getId();
    
    /**
     * Returns the group label. The label should be used when presenting the
     * group to the user.
     * 
     * @return the group label
     */

    String getLabel();
    
    /**
     * Returns the group description.
     * 
     * @return the group description
     */

    String getDescription();
    
    /**
     * Returns the set of member project facets.
     * 
     * @return the set of member project facets
     */
    
    Set<IProjectFacetVersion> getMembers();

}
