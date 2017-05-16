package org.eclipse.wst.validation.tests;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;

/**
 * This validator never gets called, it just serves as a definition for a delegating validator.
 * @author karasiuk
 *
 */
public class TestValidator5 extends AbstractValidator {
	
	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		return null;
	}

}
