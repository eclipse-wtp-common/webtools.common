package org.eclipse.wst.validation.internal.provisional.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.ValidationState;

public interface IValidatorExtender {

	void validationFinishing(IProject project, ValidationState state, IProgressMonitor monitor);

}
