package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;

public interface IProjectValidationHelper {
	
	public IContainer[] getOutputContainers(IProject project);
	
	public IContainer[] getSourceContainers(IProject project);

}
