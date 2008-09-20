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

package org.eclipse.jst.common.project.facet.core.libprov.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.common.project.facet.core.libprov.LibrariesProviderOperation;
import org.eclipse.jst.common.project.facet.core.libprov.LibrariesProviderOperationConfig;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class UnknownProviderUninstallOperation

    extends LibrariesProviderOperation
    
{
    public void execute( final IFacetedProject project,
                         final LibrariesProviderOperationConfig config,
                         final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        // The operation is a no-op.
    }
    
}
