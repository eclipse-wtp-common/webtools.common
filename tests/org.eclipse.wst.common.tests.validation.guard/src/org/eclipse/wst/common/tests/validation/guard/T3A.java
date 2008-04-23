package org.eclipse.wst.common.tests.validation.guard;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;

/**
 * A test validator that we never expect to be activated.
 * @author karasiuk
 *
 */
public class T3A extends AbstractValidator {
	
	private boolean _invoked;
	
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor){
		if (!_invoked){
			_invoked = true;
			throw new RuntimeException("The T3A validator should never be activated or called.");
		}
		
		return null;		
	}


}
