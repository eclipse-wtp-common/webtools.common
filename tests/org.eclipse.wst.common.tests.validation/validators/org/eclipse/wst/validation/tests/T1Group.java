package org.eclipse.wst.validation.tests;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.IValidatorGroupListener;
import org.eclipse.wst.validation.ValidationState;

public class T1Group implements IValidatorGroupListener {
	
	private int _starting;
	private int	_finishing;
	private static T1Group _me;
	
	public static T1Group getGroup(){
		return _me;
	}
	
	public T1Group(){
		if (_me != null)throw new IllegalStateException("Only one instance can be constructed.");
		_me = this;
	}

	public void validationFinishing(IResource resource,
			IProgressMonitor monitor, ValidationState state) {
		_starting++;

	}

	public void validationStarting(IResource resource,
			IProgressMonitor monitor, ValidationState state) {
		_finishing++;

	}

	public int getStarting() {
		return _starting;
	}

	public int getFinishing() {
		return _finishing;
	}

	public void reset() {
		_starting = 0;
		_finishing = 0;
		
	}

}
