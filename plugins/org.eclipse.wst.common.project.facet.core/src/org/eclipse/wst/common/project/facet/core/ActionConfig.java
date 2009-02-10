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

package org.eclipse.wst.common.project.facet.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @since 3.0
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class ActionConfig
{
    private IFacetedProjectWorkingCopy fpjwc = null;
    private IProjectFacetVersion fv = null;
    
    public IFacetedProjectWorkingCopy getFacetedProjectWorkingCopy()
    {
        return this.fpjwc;
    }
    
    public void setFacetedProjectWorkingCopy( final IFacetedProjectWorkingCopy fpjwc )
    {
        this.fpjwc = fpjwc;
    }
    
    public IProjectFacetVersion getProjectFacetVersion()
    {
        return this.fv;
    }
    
    public void setProjectFacetVersion( final IProjectFacetVersion fv )
    {
        this.fv = fv;
    }
    
    /**
     * Returns the set of files that might be modified during execution of this action. This
     * allows calls to IWorkspace.validateEdit() to be batched, resulting in fewer prompts to the
     * user. The default implementation returns an empty (modifiable) set.
     * 
     * @return the set of files that might be modified during the execution of the action
     * @since 1.4
     */
    
    public Set<IFile> getValidateEditFiles()
    {
        return new HashSet<IFile>();
    }
    
    public IStatus validate()
    {
        return Status.OK_STATUS;
    }
    
    /**
     * Called when the action config object is not longer needed by the framework. Implementations
     * can override to perform cleanup of allocated resources or registered listeners.
     * 
     * @since 1.4
     */
    
    public void dispose()
    {
    }
}
