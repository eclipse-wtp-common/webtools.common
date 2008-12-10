package org.eclipse.wst.validation.tests;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.tests.validation.Activator;
import org.eclipse.wst.validation.ValidationEvent;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;

/**
 * This validator is used to test the new validate entry point that includes dependency information
 * @author karasiuk
 *
 */
public class T6A extends TestValidator {
	
	private static AtomicInteger _countSimple = new AtomicInteger();
	private static AtomicInteger _countComplex = new AtomicInteger();
	
	public static void resetCounters(){
		_countSimple.set(0);
		_countComplex.set(0);
	}
	
	public static int getCountSimple(){
		return _countSimple.get();
	}
	
	public static int getCountComplex(){
		return _countComplex.get();
	}
	
	public String getName() {
		return "T6A";
	}
	
	public static String id(){
		return Activator.PLUGIN_ID +".T6A";
	}
	
	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		_countSimple.incrementAndGet();
		return super.validate(resource, kind, state, monitor);
	}
	
	@Override
	public ValidationResult validate(ValidationEvent event, ValidationState state, IProgressMonitor monitor) {
		_countComplex.incrementAndGet();
		return super.validate(event.getResource(), event.getKind(), state, monitor);
	}
}
