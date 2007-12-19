package org.eclipse.wst.validation.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationResults;
import org.eclipse.wst.validation.ValidationState;

/**
 * This represents a validation operation, i.e. the running of a set of validators in response to some change. 
 * It may be a large operation, as would happen on a clean build, or it may be the validation of just a single
 * resource.
 * <p>
 * The operation can, but doesn't need to, span multiple projects.
 * <p>
 * Once the operation has finished, this object goes away.     
 * @author karasiuk
 *
 */
public class ValOperation {
	
	private ValidationState 	_state = new ValidationState();
	private ValidationResult	_result = new ValidationResult();
	
	/** Holds all the resources that have been validated as a side-effect of running other validations.
	 * The key is the validator id and the value is a Set of IResources.
	 */
	private Map<String, Set<IResource>> 	_validated = new HashMap<String, Set<IResource>>(20);
	
	public ValOperation(){}
	
	public ValidationState getState() {
		return _state;
	}
	public void setState(ValidationState state) {
		_state = state;
	}
	public ValidationResult getResult() {
		return _result;
	}
	
	public void setResult(ValidationResult result) {
		_result = result;
	}
	
	public ValidationResults getResults(){
		return new ValidationResults(_result);
	}
	
	/**
	 * Remember that this resource has already been validated as a side-effect.
	 *  
	 * @param id id of the validator
	 * @param resource resource that has been validated.
	 */
	public void addValidated(String id, IResource resource){
		Set<IResource> set = _validated.get(id);
		if (set == null){
			set = new HashSet<IResource>(20);
			_validated.put(id, set);
		}
		set.add(resource);
	}
	
	/**
	 * Answer if this resource has already been validated as a side-effect of some other validation by the
	 * given validator.
	 * 
	 * @param id
	 * @param resource
	 */
	public boolean isValidated(String id, IResource resource){
		Set set = (Set)_validated.get(id);
		if (set == null)return false;
		
		return set.contains(resource);
	}
	
}
