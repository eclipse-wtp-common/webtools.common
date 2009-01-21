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
public final class ValOperation {
	
	private final ValidationState 	_state = new ValidationState();
	private final ValidationResult	_result = new ValidationResult();
	
	/**
	 * Each project can have a set of validators that are suspended for the duration of the validation operation.
	 * The set contains the validator's id.
	 */
	private final Map<IProject, Set<String>> _suspended = new HashMap<IProject, Set<String>>(40);
	
	/** The time that the operation started. */
	private final long	_started = System.currentTimeMillis();
	
	/** 
	 * Are we in a multi project validation? That is, could we be validating several
	 * projects at the same time? This can be triggered by either clean all or 
	 * if auto build is turned off, a build all. 
	 */
	private final boolean	_multiProject;
	
	/** 
	 * Holds all the resources that have been validated as a side-effect of running other validations.
	 * The key is the validator id and the value is a Set of IResources.
	 */
	private final Map<String, Set<IResource>> 	_validated = new HashMap<String, Set<IResource>>(20);
	
	public ValOperation(){
		_multiProject = false;
	}
	
	/**
	 * 
	 * @param multiProject Set to true if we could be validating several projects at the same time.
	 */
	public ValOperation(boolean multiProject){
		_multiProject = multiProject;
	}
	
	public ValidationState getState() {
		return _state;
	}
	
	/**
	 * Answer a summary of the validation results.
	 * @return
	 */
	public ValidationResultSummary getResult() {
		synchronized(_result){
			ValidationResultSummary vrs = new ValidationResultSummary(_result.getSeverityError(), 
				_result.getSeverityWarning(), _result.getSeverityInfo());
			return vrs;
		}
	}
		
	/**
	 * Answer a copy of the ValidationResult.
	 * @return
	 */
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
		synchronized(_validated){
			Set<IResource> set = _validated.get(id);
			if (set == null){
				set = new HashSet<IResource>(20);
				_validated.put(id, set);
			}
			set.add(resource);
		}
	}
	
	/**
	 * Answer if this resource has already been validated as a side-effect of some other validation by the
	 * given validator.
	 * 
	 * @param id
	 * @param resource
	 */
	public boolean isValidated(String id, IResource resource){
		synchronized(_validated){
			Set<IResource> set = _validated.get(id);
			if (set == null)return false;
			
			return set.contains(resource);
		}
	}

	/**
	 * Has this validator been suspended for the duration of this operation on this project?
	 * 
	 * @param val
	 *            The validator that is being checked.
	 * @param project
	 *            Can be null, in which case we return false.
	 * 
	 * @return true if this validator should not run on this project.
	 */
	public boolean isSuspended(Validator val, IProject project) {
		if (project == null)return false;
		synchronized(_suspended){
			Set<String> set = getSuspended(project);		
			return set.contains(val.getId());
		}
	}
	
	private Set<String> getSuspended(IProject project){
		Set<String> set = _suspended.get(project);
		if (set == null){
			set = new HashSet<String>(5);
			_suspended.put(project, set);
		}
		return set;
	}

	void suspendValidation(IProject project, Validator validator) {
		if (project == null)return;
		if (validator == null)return;
		getSuspended(project).add(validator.getId());
	}

	public long getStarted() {
		return _started;
	}

	public boolean isMultiProject() {
		return _multiProject;
	}

	/**
	 * Indicate if the operation was canceled.
	 * 
	 * @param canceled
	 * 		Set to true if it was canceled and false if it was not canceled.
	 */
	public void setCanceled(boolean canceled) {
		synchronized (_result) {
			_result.setCanceled(canceled);
		}
		
	}

	/**
	 * Was the operation canceled before it completed? For example if the validation is being run through the
	 * user interface, the end user can cancel the operation through the progress monitor.
	 * 
	 * @return true if the operation was canceled
	 */
	public boolean isCanceled() {
		synchronized (_result) {
			return _result.isCanceled();
		}
	}

	public void mergeResults(ValidationResult vr) {
		synchronized (_result) {
			_result.mergeResults(vr);
		}
	}
}
