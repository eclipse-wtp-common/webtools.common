/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationResults;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.Validator;

/**
 * This represents a validation operation, i.e. the running of a set of validators in response to some change. 
 * It may be a large operation, as would happen on a clean build, or it may be the validation of just a single
 * resource.
 * <p>
 * The operation can, but doesn't need to, span multiple projects.
 * </p>
 * <p>
 * Once the operation has finished, this object goes away.
 * </p>     
 * @author karasiuk
 *
 */
public class ValOperation {
	
	private ValidationState 	_state = new ValidationState();
	private ValidationResult	_result = new ValidationResult();
	private Map<IProject, Set<Validator>> _suspended = 
		Collections.synchronizedMap(new HashMap<IProject, Set<Validator>>(40));
	
	/** The time that the operation started. */
	private long	_started = System.currentTimeMillis();
	
	/** 
	 * Are we in a multi project validation? This can be triggered by either clean all or 
	 * if auto build is turned off, a build all. 
	 */
	private boolean	_multiProject;
	
	/** 
	 * Holds all the resources that have been validated as a side-effect of running other validations.
	 * The key is the validator id and the value is a Set of IResources.
	 */
	private Map<String, Set<IResource>> 	_validated = new HashMap<String, Set<IResource>>(20);
	
	public ValOperation(){
	}
	
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

	/**
	 * Has this validator been suspended for the duration of this operation?
	 *   
	 * @param val
	 * @param project can be null, in which case we return false
	 * 
	 * @return true if we already know that this validator should not run on this project.
	 */
	public boolean isSuspended(Validator val, IProject project) {
		if (project == null)return false;
		Set<Validator> set = getSuspended(project);		
		return set.contains(val);
	}
	
	private Set<Validator> getSuspended(IProject project){
		Set<Validator> set = _suspended.get(project);
		if (set == null){
			set = new HashSet<Validator>(5);
			_suspended.put(project, set);
		}
		return set;
	}

	void suspendValidation(IProject project, Validator validator) {
		if (project == null)return;
		getSuspended(project).add(validator);
	}

	public long getStarted() {
		return _started;
	}

	public boolean isMultiProject() {
		return _multiProject;
	}

	/** 
	 * Are we in a multi project validation? That is, could we be validating several
	 * projects at the same time? This can be triggered by either clean all or 
	 * if auto build is turned off, a build all. 
	 */
	public void setMultiProject(boolean multiProject) {
		_multiProject = multiProject;
	}	
}
