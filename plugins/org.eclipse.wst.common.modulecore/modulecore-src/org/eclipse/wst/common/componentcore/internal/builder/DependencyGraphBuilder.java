package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;

public class DependencyGraphBuilder extends IncrementalProjectBuilder {
	
	 /**
     * Builder id of this incremental project builder.
     */
    public static final String BUILDER_ID = IModuleConstants.DEPENDENCY_GRAPH_BUILDER_ID;

	public DependencyGraphBuilder() {
		super();
	}

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		DependencyGraphManager.getInstance().construct(getProject());
		return null;
	}
	
	

}
