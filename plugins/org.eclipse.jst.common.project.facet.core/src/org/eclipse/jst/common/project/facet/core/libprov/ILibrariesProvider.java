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

import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface ILibrariesProvider

    extends Comparable<ILibrariesProvider>
    
{
    String getId();
    String getPluginId();
    String getLabel();
    ILibrariesProvider getBaseProvider();
    ILibrariesProvider getRootProvider();
    boolean isAbstract();
    boolean isHidden();
    int getPriority();
    boolean isEnabledFor( IFacetedProjectWorkingCopy fpjwc, IProjectFacetVersion fv );
    boolean isActionSupported( LibrariesProviderActionType type );
}
