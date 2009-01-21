package org.eclipse.wst.validation.tests;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;

/**
 * A validator that tests the order of the validate calls.
 * @author karasiuk
 *
 */
public final class T7A extends AbstractValidator {
	
	private static List<ValEntryPoint> _list = new LinkedList<ValEntryPoint>();
	
	public static void resetList(){
		_list.clear();
	}
	
	public static List<ValEntryPoint> getList(){
		return _list;
	}
	
	public static ValEntryPoint[] getArray(){
		ValEntryPoint[] array = new ValEntryPoint[_list.size()];
		_list.toArray(array);
		return array;
	}
	
	@Override
	public void validationStarting(IProject project, ValidationState state,	IProgressMonitor monitor) {
		_list.add(new ValEntryPoint(EntryType.Starting, project));
	}
	
	@Override
	public void validationFinishing(IProject project, ValidationState state, IProgressMonitor monitor) {
		_list.add(new ValEntryPoint(EntryType.Finishing, project));
	}
	
	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		_list.add(new ValEntryPoint(EntryType.Normal, resource));
		return null;
	}
	
	@Override
	public void clean(IProject project, ValidationState state, IProgressMonitor monitor) {
		_list.add(new ValEntryPoint(EntryType.Clean, project));
	}
	
	/**
	 * An immutible object that records an entry into the validator.
	 * @author karasiuk
	 *
	 */
	public final static class ValEntryPoint {
		private final EntryType 	_type;
		private final IResource 	_resource;
		
		public ValEntryPoint(EntryType type, IResource resource){
			_type = type;
			_resource = resource;
		}
		
		@Override
		public String toString() {
			String resource = _resource == null ? "null" : _resource.getName();
			return "ValEntryPoint: " + _type + " " + resource;
		}
		
		public EntryType getType() {
			return _type;
		}

		public IResource getResource() {
			return _resource;
		}		
	}
	
	public enum EntryType {Starting, Normal, Finishing, Clean}

}
