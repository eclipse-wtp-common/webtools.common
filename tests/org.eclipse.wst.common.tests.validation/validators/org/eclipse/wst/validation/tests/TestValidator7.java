package org.eclipse.wst.validation.tests;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.tests.validation.Activator;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;

/**
 * A validator that has similar rules as the Test1 Validator, used to test the suspend validation API. 
 * @author karasiuk
 *
 */
public class TestValidator7 extends AbstractValidator {
	
	public static String id(){
		return Activator.PLUGIN_ID +".Test7";
	}

	
	private Set<IResource> _set = new HashSet<IResource>(100);
	
	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		ValidationResult result = new ValidationResult();
		_set.add(resource);
		result.setSuspendValidation(resource.getProject());
		return result;
	}

	public Set<IResource> getSet() {
		return _set;
	}

}
