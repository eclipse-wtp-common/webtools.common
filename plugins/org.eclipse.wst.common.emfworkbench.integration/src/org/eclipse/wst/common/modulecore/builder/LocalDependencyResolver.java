package org.eclipse.wst.common.modulecore.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.modulecore.IModuleConstants;

public class LocalDependencyResolver extends IncrementalProjectBuilder implements IModuleConstants{
	/**
	 * Builder id of this incremental project builder.
	 */
	public static final String BUILDER_ID = LOCAL_DEPENDENCY_RESOLVER_ID;
    /**
     * 
     */
    public LocalDependencyResolver() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.internal.events.InternalBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

}
