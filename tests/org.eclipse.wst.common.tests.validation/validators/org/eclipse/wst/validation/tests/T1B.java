package org.eclipse.wst.validation.tests;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.tests.validation.Activator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;

/**
 * Used to test custom markers.
 * @author karasiuk
 *
 */
public class T1B extends TestValidator {

	public static final String MarkerId = Activator.PLUGIN_ID+".t1bmarker";
	
	public static String id(){
		return Activator.PLUGIN_ID +".T1B";
	}
	
	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		ValidationResult vr = super.validate(resource, kind, state, monitor);
		
		for (ValidatorMessage vm : vr.getMessages()){
			vm.setType(MarkerId);
		}
		return vr;
	}
}
