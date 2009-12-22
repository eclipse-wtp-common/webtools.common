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

package org.eclipse.jst.common.project.facet.core.internal;

import org.eclipse.jst.common.project.facet.core.JavaFacetVersionChangeConfig;
import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JavaFacetVersionChangeConfigFactory

    implements IActionConfigFactory
    
{
    public Object create() 
    {
        return new JavaFacetVersionChangeConfig();
    }

}
