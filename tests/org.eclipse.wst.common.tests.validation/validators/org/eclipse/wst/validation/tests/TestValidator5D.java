package org.eclipse.wst.validation.tests;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;

/**
 * This is a delegating validator.
 * @author karasiuk
 *
 */
public class TestValidator5D extends AbstractValidator {
	
	private static int _calledCount;
	private static ValCounters _counters = new ValCounters();
	
	public static ValCounters getCounters() {
		return _counters;
	}

	@Override
	public void validationStarting(IProject project, ValidationState state, IProgressMonitor monitor) {
		if (project == null)_counters.startingCount++;
		else _counters.startingProjectCount++;
	}
	
	@Override
	public void validationFinishing(IProject project, ValidationState state, IProgressMonitor monitor) {
		if (project == null)_counters.finishedCount++;
		else _counters.finishedProjectCount++;
	}

	public String getName() {
		return "TestValidator5D";
	}

	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		ValidationResult vr = new ValidationResult();
		_calledCount++;
		return vr;
	}

	public static int getCalledCount() {
		return _calledCount;
	}

}
