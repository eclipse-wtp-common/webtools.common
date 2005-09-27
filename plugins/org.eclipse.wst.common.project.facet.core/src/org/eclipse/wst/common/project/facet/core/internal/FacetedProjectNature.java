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

package org.eclipse.wst.common.project.facet.core.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectNature

    implements IProjectNature

{
    public static final String NATURE_ID 
        = "org.eclipse.wst.common.project.facet.core.nature";
    
    private IProject project;
    
    public IProject getProject()
    {
        return this.project;
    }
    
    public void setProject( final IProject project )
    {
        this.project = project;
    }
    
    public void configure() {}
    
    public void deconfigure() {}
    
}
