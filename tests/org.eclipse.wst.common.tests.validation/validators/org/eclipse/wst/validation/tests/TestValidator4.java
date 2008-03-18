package org.eclipse.wst.validation.tests;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.tests.validation.Activator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;

/**
 * A slower validator. This validator takes at least 2 seconds to run.
 * 
 * It looks at files with a file extension of test4.
 * @author karasiuk
 *
 */
public class TestValidator4 extends TestValidator {
	
	private static ValCounters _counters = new ValCounters();
	
	public static String id(){
		return Activator.PLUGIN_ID +".Test4";
	}
	
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
		
	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		ValidationResult vr = super.validate(resource, kind, state, monitor);
		long j = 0;
		try {
			for (long i=0; i< 10000000; i++)j = i + 1;
			Thread.sleep(2000);
		}
		catch (InterruptedException e){
			// eat it
		}
		j++; // just to get rid of the compiler warning
		return vr;
	}
}
