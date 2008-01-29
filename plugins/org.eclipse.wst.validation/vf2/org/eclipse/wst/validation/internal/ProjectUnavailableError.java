package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IProject;

/**
 * This internal error is used to signal that a project is now unavailable.
 * <p>
 * We could be in the middle of validating a large project, and the project could be closed. 
 * This error is used to "exit" the validation framework.
 * <p>
 * This is an error rather than a runtime exception, because some parts of Eclipse like to
 * trap RuntimeExceptions and log them.
 * @author karasiuk
 *
 */
public class ProjectUnavailableError extends Error {
	
	private IProject _project;

	private static final long serialVersionUID = 200801281118L;
	
	public ProjectUnavailableError(IProject project){
		super();
		_project = project;
	}

	public IProject getProject() {
		return _project;
	}

}
