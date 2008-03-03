/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.internal;

import org.eclipse.wst.common.project.facet.core.IDefaultVersionProvider;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * Defaults the java facet version to align with workspace java compiler
 * level settings.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class JavaFacetDefaultVersionProvider

    implements IDefaultVersionProvider
    
{
    public IProjectFacetVersion getDefaultVersion()
    {
        final String compilerLevel = JavaFacetUtil.getCompilerLevel();
        return JavaFacetUtil.compilerLevelToFacet( compilerLevel );
    }
}
