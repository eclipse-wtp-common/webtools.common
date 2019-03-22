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

package org.eclipse.wst.common.project.facet.core.events.internal;

import org.eclipse.wst.common.project.facet.core.IFacetedProject;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProjectModifiedEvent

    extends FacetedProjectEvent
    
{
    public ProjectModifiedEvent( IFacetedProject project )
    {
        super( project, Type.PROJECT_MODIFIED );
    }
    
}
