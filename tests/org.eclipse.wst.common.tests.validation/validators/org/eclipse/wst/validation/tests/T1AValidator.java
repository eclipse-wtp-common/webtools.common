package org.eclipse.wst.validation.tests;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.tests.validation.Activator;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.validation.internal.Tracing;
import org.eclipse.wst.validation.tests.testcase.FileNames;

/**
 * Test validating a side file.
 * @author karasiuk
 *
 */
public class T1AValidator extends AbstractValidator {
	
	public static String id(){
		return Activator.PLUGIN_ID +".T1A";
	}
	
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor){
		Tracing.log("T1AValidator-01: is validating: " + resource.getName());
		ValidationResult vr = new ValidationResult();
		clearTest(resource.getProject(), vr);
		return vr;
	}
	
	
	private void clearTest(IProject project, ValidationResult vr) {
		IResource resource = project.findMember("source/" + FileNames.firstTest2x);
		try {
			ValidationFramework.getDefault().clearMessages(resource, id());
		}
		catch (CoreException e){
			throw new RuntimeException(e);
		}
		String msg = Tracing.timestampIt("Side effect validation from T1A");
		ValidatorMessage vm = ValidatorMessage.create(msg, resource);
		vm.setAttribute(IMarker.LINE_NUMBER, 1);
		vm.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		vr.add(vm);	
		vr.setValidated(new IResource[]{resource});
	}
	
	public String getId(){
		return id();
	}
	
	public String getName(){
		return "T1AValidator";
	}
	
}
