package org.eclipse.wst.validation.tests;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.validation.internal.Tracing;

/**
 * A validator that processes *.html and *.htm files. 
 * @author karasiuk
 *
 */
public class TestValidator3 extends AbstractValidator {
	
	private static final String Name = "TestValidator3";
	static final String ID = "org.eclipse.wst.validation.tests.TestValidator3";
	
	public String getName() {
		return Name;
	}
	
	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor){
		Tracing.log(Name+"-04: validating: " + resource);
		checkState(state);
		
		if (resource.getName().equals("test.html")){
			ValidationResult vr = new ValidationResult();
			ValidatorMessage vm = ValidatorMessage.create("A specific test.html error", resource);
			vm.setAttribute(IMarker.LINE_NUMBER, 1);
			vm.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			vr.add(vm);
			
			IResource other = resource.getParent().findMember("test2.html");
			if (other != null)vr.setValidated(new IResource[]{other});
			
			IResource depends = resource.getParent().findMember("master.html");
			if (depends != null)vr.setDependsOn(new IResource[]{depends});
			return vr;
		}

		ValidationResult vr = new ValidationResult();
		ValidatorMessage vm = ValidatorMessage.create("A sample message from " + getName(), resource);
		vm.setAttribute(IMarker.LINE_NUMBER, 1);
		vm.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		vr.add(vm);

		vm = ValidatorMessage.create("A different message from " + getName(), resource);
		vm.setAttribute(IMarker.LINE_NUMBER, 3);
		vm.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
		vr.add(vm);
				
		return vr;
	}
	
	@Override
	public void validationStarting(IProject project, ValidationState state, IProgressMonitor monitor) {
		String name = project == null ? "NULL" : project.getName();
		Tracing.log(Name+"-05: thinks validation is starting for project: ", name);
		if (!checkState(state)){
			state.put(ID, "my state");
		}
		
		
	}
	
	@Override
	public void validationFinishing(IProject project, ValidationState state, IProgressMonitor monitor) {
		String name = project == null ? "NULL" : project.getName();
		Tracing.log(Name+"-01: thinks validation is finishing for project: ", name);
		checkState(state);
	}
	
	@Override
	public void clean(IProject project, ValidationState state, IProgressMonitor monitor) {
		String name = project == null ? "NULL" : project.getName();
		Tracing.log(Name+"-02: thinks a clean has been requested for project: ", name);
		checkState(state);
	}
	
	private boolean checkState(ValidationState state){
		if (state.get(ID) != null){
			Tracing.log(Name+"-03: has state information");
			return true;
		}
		return false;
	}

}
