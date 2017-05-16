package org.eclipse.wst.common.tests.validation.guard;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;

/**
 * A test validator that we never expect to be activated. The reason we never expect it to be validated is that it has a bogus facet filter.
 * @author karasiuk
 *
 */
public class T3B extends AbstractValidator {
	
	private boolean _invoked;
	
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor){
		if (!_invoked){
			_invoked = true;
			throw new RuntimeException("The T3B validator should never be activated or called. If this validator was manually chnaged " +
				"though the preferences, and the bogus filter was removed, then this isn't a real error.");
		}
		
		return null;		
	}


}
