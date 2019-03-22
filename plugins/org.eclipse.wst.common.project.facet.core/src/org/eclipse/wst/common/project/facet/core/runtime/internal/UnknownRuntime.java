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

package org.eclipse.wst.common.project.facet.core.runtime.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;

/**
 * Used to represent a runtime that's referenced by a project, but is not
 * defined in the workspace.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class UnknownRuntime

    extends AbstractRuntime
    
{
    public UnknownRuntime( final String name )
    {
        setName( name );
    }
    
    public Map<String,String> getProperties()
    {
        return Collections.emptyMap();
    }

    public List<IRuntimeComponent> getRuntimeComponents()
    {
        return Collections.emptyList();
    }

    public boolean supports( final IProjectFacetVersion fv )
    {
        return true;
    }
}
