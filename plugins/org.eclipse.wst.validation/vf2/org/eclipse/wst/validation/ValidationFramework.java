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
package org.eclipse.wst.validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.validation.internal.ContentTypeWrapper;
import org.eclipse.wst.validation.internal.DebugConstants;
import org.eclipse.wst.validation.internal.DependencyIndex;
import org.eclipse.wst.validation.internal.DisabledResourceManager;
import org.eclipse.wst.validation.internal.DisabledValidatorManager;
import org.eclipse.wst.validation.internal.MarkerManager;
import org.eclipse.wst.validation.internal.Misc;
import org.eclipse.wst.validation.internal.PerformanceMonitor;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValOperation;
import org.eclipse.wst.validation.internal.ValType;
import org.eclipse.wst.validation.internal.ValidationRunner;
import org.eclipse.wst.validation.internal.operations.ValidationBuilder;
import org.eclipse.wst.validation.internal.operations.WorkbenchReporter;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;


/**
 * The central class of the Validation Framework.
 * <p>
 * This is a singleton class that is accessed through the getDefault() method. 
 * @author karasiuk
 *
 */
public final class ValidationFramework {
	
	private IDependencyIndex 			_dependencyIndex;
	private IPerformanceMonitor			_performanceMonitor;
	
	private Set<IProject> 				_suspendedProjects;
	private boolean 					_suspendAllValidation;

	private static ValidationFramework 	_me;
	
	/** 
	 * Answer the singleton, default instance of this class.
	 */
	public static ValidationFramework getDefault(){
		if (_me == null)_me = new ValidationFramework();
		return _me;
	}
	
	private ValidationFramework(){}
	
	/**
	 * Clear any validation markers that may have been set by this validator.
	 *  
	 * @param resource the resource that may have it's markers cleared
	 * @param validatorId the id of validator that created the marker
	 */
	public void clearMessages(IResource resource, String validatorId) throws CoreException {
		Validator v = getValidator(validatorId, null);
		if (v != null)MarkerManager.getDefault().clearMarker(resource, v);
	}
	
	/**
	 * Disable all validation for the given resource. This method instructions
	 * the framework to not run any validators on the given resource or any of
	 * it's children. This setting is persistent. Currently this only works with version 2
	 * validators.
	 * 
	 * Use the enableValidation method to restore validation.
	 * 
	 * @param resource
	 *            The resource that is having validation disabled. It must be an IFolder or an IFile.
	 * 
	 * @see #enableValidation(IResource)
	 */
	public void disableValidation(IResource resource){
		assert resource != null;
		DisabledResourceManager.getDefault().disableValidation(resource);
	}
	
	/**
	 * Enable validation for the given resource. If the resource was not previously
	 * disabled this method call has no effect. Currently this only works with version 2 
	 * validators.
	 * 
	 * @param resource The resource that is having validation re-enabled.
	 * 
	 * @see #disableValidation(IResource)
	 */
	public void enableValidation(IResource resource){
		DisabledResourceManager.getDefault().enableValidation(resource);
	}
	
	/**
	 * Answer the dependency index. Validators can use this to determine which resources depend on which
	 * other resources.
	 */
	public IDependencyIndex getDependencyIndex(){
		if (_dependencyIndex != null)return _dependencyIndex;
		return getDependencyIndex2();
	}

	private synchronized IDependencyIndex getDependencyIndex2() {
		if (_dependencyIndex == null)_dependencyIndex = new DependencyIndex();
		return _dependencyIndex;
	}
	
	public synchronized IPerformanceMonitor getPerformanceMonitor(){
		if (_performanceMonitor == null){
			boolean traceTimes = Misc.debugOptionAsBoolean(DebugConstants.TraceTimes);
			String traceFile = Platform.getDebugOption(DebugConstants.TraceTimesFile);
			boolean useDoubles = Misc.debugOptionAsBoolean(DebugConstants.TraceTimesUseDoubles);

			_performanceMonitor = PerformanceMonitor.create(traceTimes, traceFile, useDoubles);
		}
		return _performanceMonitor;
	}
	
	/**
	 * Answer the preference store that holds the global validation settings.
	 */
	public IEclipsePreferences getPreferenceStore(){
		return new InstanceScope().getNode(ValidationPlugin.PLUGIN_ID);
	}
	
	public IReporter getReporter(IProject project, IProgressMonitor monitor){
		return new WorkbenchReporter(project, monitor);
	}
	
	/**
	 * Answer all the validators that are applicable for the given resource, even if the
	 * validator has been turned off by the user.
	 * <p>
	 * The caller may still need to test if the validator has been turned off by the
	 * user, by using the isBuildValidation() and isManualValidation() methods.
	 * </p>
	 * @param resource the resource that determines which validators are applicable.
	 * 
	 * @param isManual if true then the validator must be turned on for manual validation. 
	 * If false then the isManualValidation setting isn't used to filter out validators.
	 *   
	 * @param isBuild if true then the validator must be turned on for build based validation.
	 * If false then the isBuildValidation setting isn't used to filter out validators.  
	 * 
	 * @see Validator#isBuildValidation()
	 * @see Validator#isManualValidation()
	 */
	public Validator[] getValidatorsFor(IResource resource, boolean isManual, boolean isBuild){
		IProject project = resource.getProject();
		List<Validator> list = new LinkedList<Validator>();
		ContentTypeWrapper ctw = new ContentTypeWrapper();
		for (Validator val : ValManager.getDefault().getValidators(project)){
			if (val.shouldValidate(resource, isManual, isBuild, ctw))list.add(val);
		}
		
		Validator[] result = new Validator[list.size()];
		list.toArray(result);
		return result;
	}
	
	/**
	 * Answer all the validators that should not validate the resource, either because
	 * their filters don't support the resource, or the validator has been disabled for
	 * both build validation and manual validation.
	 * @param resource
	 */
	public Set<Validator> getDisabledValidatorsFor(IResource resource){
		return DisabledValidatorManager.getDefault().getDisabledValidatorsFor(resource);
	}
	
	/**
	 * Answer the global validator with the given id.
	 * 
	 * @deprecated Use getValidator(String id, IProject project) with a null project instead.
	 * 
	 * @param id
	 * @return null if the validator is not found
	 */
	public Validator getValidator(String id){
		return ValManager.getDefault().getValidatorWithId(id, null);
	}
	
	/**
	 * Answer the validator with the given id that is in effect for the given project.
	 * <p>
	 * Individual projects may override the global validation preference settings. If this is allowed and if
	 * the project has it's own settings, then those validators are returned via this method.
	 * </p>
	 * <p>
	 * The following approach is used. For version 1 validators, the validator is only returned if it
	 * is defined to operate on this project type. This is the way that the previous version of the framework
	 * did it. For version 2 validators, they are all returned.
	 * </p>
	 * 
	 * @param id validator id.
	 * @param project this can be null, in which case all the registered validators are checked.
	 * @return null if the validator is not found
	 */
	public Validator getValidator(String id, IProject project){
		return ValManager.getDefault().getValidatorWithId(id, project);
	}
	
	/**
	 * Answer all the validators that are applicable for the given resource.
	 * 
	 * @param resource the resource that determines which validators are applicable.
	 */
	public Validator[] getValidatorsFor(IResource resource){
		List<Validator> list = new LinkedList<Validator>();
		for (Validator v : getValidatorsFor(resource, false, false)){
			if (v.isBuildValidation() || v.isManualValidation())list.add(v);
		}
		Validator[] vals = new Validator[list.size()];
		return list.toArray(vals);
	}
	
	/**
	 * Answer true if the resource has any enabled validators.
	 * 
	 * @param resource a file, folder or project.
	 * 
	 * @param isManual if true then the validator must be turned on for manual validation. 
	 * If false then the isManualValidation setting isn't used to filter out validators.
	 *   
	 * @param isBuild if true then the validator must be turned on for build based validation.
	 * If false then the isBuildValidation setting isn't used to filter out validators.  
	 */
	public boolean hasValidators(IResource resource, boolean isManual, boolean isBuild){
		return ValManager.getDefault().hasValidators(resource, isManual, isBuild);
	}
	
	/**
	 * Waits until all validation jobs are finished.  This method will block the 
	 * calling thread until all such jobs have finished executing, or until this thread is
	 * interrupted.   If there are no validation jobs in 
	 * that are currently waiting, running, or sleeping, this method returns 
	 * immediately.  Feedback on how the join is progressing is provided to a progress monitor.
	 * <p>
	 * If this method is called while the job manager is suspended, only jobs
	 * that are currently running will be joined; Once there are no jobs
	 * in the family in the {@link Job#RUNNING} state, this method returns.
	 * </p>
	 * <p>
	 * Note that there is a deadlock risk when using join.  If the calling thread owns
	 * a lock or object monitor that the joined thread is waiting for, deadlock 
	 * will occur. This method can also result in starvation of the current thread if
	 * another thread continues to add jobs of the given family, or if a
	 * job in the given family reschedules itself in an infinite loop.
	 * </p>
	 * 
	 * @param monitor Progress monitor for reporting progress on how the
	 * wait is progressing, or <code>null</code> if no progress monitoring is required.
	 * @exception InterruptedException if this thread is interrupted while waiting
	 * @exception OperationCanceledException if the progress monitor is canceled while waiting
	 */
	public void join(IProgressMonitor monitor) throws InterruptedException, OperationCanceledException {
		Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, monitor);
		Job.getJobManager().join(ValidationBuilder.FAMILY_VALIDATION_JOB, monitor);
	}
	
	/**
	 * Suspends, or undoes the suspension of, validation on the current project. If "suspend" is
	 * true then validation is suspended and if it's "false" then validation is not suspended on the
	 * project. The value of this variable is not persisted.
	 * <p>
	 * Be VERY CAREFUL when you use this method! Turn validation back on in a finally block because
	 * if the code which suspended validation crashes, the user has no way to reset the suspension.
	 * The user will have to shut down and restart the workbench to get validation to work again.
	 * </p>
	 * @param project the project that is to be suspended or unsuspended.
	 * @param suspend if true validation on the project will be suspend. If false it will not be suspended.
	 */
	public void suspendValidation(IProject project, boolean suspend) {
		if (project == null)return;
		if (suspend)getSuspendedProjects().add(project);
		else getSuspendedProjects().remove(project);
	}
	
	private synchronized Set<IProject> getSuspendedProjects(){
		if (_suspendedProjects == null)_suspendedProjects = Collections.synchronizedSet(new HashSet<IProject>(20));
		return _suspendedProjects;
	}

	/**
	 * Suspends, or undoes the suspension of, validation on all projects in the workbench. If
	 * "suspend" is true then validation is suspended and if it's "false" then validation is not suspended.
	 * The value of this variable is not persisted.
	 * <p>
	 * Be VERY CAREFUL when you use this method! Turn validation back on in a finally block because
	 * if the code which suspended validation crashes, the user has no way to reset the suspension.
	 * The user will have to shut down and restart the workbench to get validation to work again.
	 */
	public void suspendAllValidation(boolean suspend) {
		_suspendAllValidation = suspend;
	}

	/**
	 * Return true if "suspend all" is enabled, false otherwise.
	 */
	public boolean isSuspended() {
		return _suspendAllValidation;
	}

	/**
	 * Returns true if validation will not run on the project because it's been suspended. This
	 * method checks only the suspension status; if validation cannot run for some other reason (for
	 * example, there are no enabled validators), yet the IProject is not suspended, this method
	 * will return true even though validation will not run.
	 */
	public boolean isSuspended(IProject project) {
		if (_suspendAllValidation)return true;
		if (project == null)return false;
		return getSuspendedProjects().contains(project);
	}

	/**
	 * This method should be called by any code that is preparing to suspend validation on a
	 * project. Rather than calling isSuspended(IProject), which will also return true if all validation
	 * has been suspended. 
	 * 
	 * @param project the project that is being tested
	 * @return boolean true if the project has been suspended
	 */
	public boolean isProjectSuspended(IProject project) {
		if (project == null)return false;
		return getSuspendedProjects().contains(project);
	}
	
	/**
	 * Validate the projects. Exactly one of isManual or isBuild needs to be true.
	 * 
	 * @param projects
	 *            The projects to be validated.
	 * 
	 * @param isManual
	 *            Is this being done as part of a manual validation? i.e. did
	 *            the user select the Validate menu item?
	 * 
	 * @param isBuild
	 *            Is this being done as part of a build?
	 * 
	 * @param monitor
	 * 
	 * @return the validation result which is the combined result for all the
	 *         resources that were validated.
	 */
	public ValidationResults validate(IProject[] projects, final boolean isManual, final boolean isBuild,
		IProgressMonitor monitor) throws CoreException{
		ValType type = ValType.Build;
		if (isManual)type = ValType.Manual;
		ValOperation vo = ValidationRunner.validate(createMap(projects), type, monitor, true);
		return vo.getResults();
	}
	
	/**
	 * Answer all the resources in the projects as a map.
	 * @param projects
	 */
	private Map<IProject, Set<IResource>> createMap(IProject[] projects) throws CoreException{
		final HashMap<IProject, Set<IResource>> map = new HashMap<IProject, Set<IResource>>(1000);
			
		for (IProject p : projects){
			Set<IResource> set = new HashSet<IResource>(1000);
			ResourceAdder ra = new ResourceAdder(set);
			p.accept(ra);
			map.put(p, set);
		}
		return map;
	}
	
	public static class ResourceAdder implements IResourceVisitor {
		
		private Set<IResource> _set;
		
		/**
		 * A class that knows how to add resources to a set.
		 * @param set the set where the resources are added.
		 */
		public ResourceAdder(Set<IResource> set){
			_set = set;
		}

		public boolean visit(IResource resource) throws CoreException {
			// [225839] the older validators only expect files and folders.
			int type = resource.getType();
			if (type == IResource.FILE || type == IResource.FOLDER)_set.add(resource);
			return true;
		}
		
	}

}
