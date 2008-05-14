package org.eclipse.wst.validation.tests;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.tests.validation.Activator;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;

/**
 * A simple test validator that always returns an error marker on line 1. This can be extended by other testcases.
 * 
 * Test8 is used to test the file filter. The both files named file.specific should be validated, where as only 
 * source/full.specific should be validated. 
 * @author karasiuk
 *
 */
public class TestValidator8 extends AbstractValidator {
	
	public String getName() {
		return "TestValidator8";
	}
	
	public static String id(){
		return Activator.PLUGIN_ID +".Test8";
	}
		
	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor){
		ValidationResult vr = new ValidationResult();
		ValidatorMessage vm = ValidatorMessage.create("A sample error from " + getName(), resource);
		vm.setAttribute(IMarker.LINE_NUMBER, 1);
		vm.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		vr.add(vm);				
		return vr;
	}
	
}
