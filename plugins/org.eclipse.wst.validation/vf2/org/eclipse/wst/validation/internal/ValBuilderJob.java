/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.wst.validation.DependentResource;
import org.eclipse.wst.validation.Friend;
import org.eclipse.wst.validation.IDependencyIndex;
import org.eclipse.wst.validation.ValidationEvent;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.model.IValidatorVisitor;
import org.eclipse.wst.validation.internal.operations.ValidationBuilder;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;


/**
 * Run all the v2 validators through this job.
 * <p>
 * This is the main class for supporting build based validation. When triggered it looks at all of the
 * resource changes and determines what needs to be validated. 
 * @author karasiuk
 *
 */
public class ValBuilderJob extends WorkspaceJob implements IResourceDeltaVisitor, IResourceVisitor {
	
	private static ValBuilderJob _job;
	private static Queue<ValidationRequest> _work = new LinkedList<ValidationRequest>();
	
	/** The monitor to use while running the build. */
	private IProgressMonitor	_monitor;
	
	private SubMonitor			_subMonitor;
	
	private ValidationRequest	_request;
	
	/** The types of changes we are interested in. */
	private final static int	InterestedFlags = IResourceDelta.CONTENT | IResourceDelta.ENCODING |
		IResourceDelta.MOVED_FROM | IResourceDelta.MOVED_TO;
	
	public static synchronized void validateProject(IProject project, IResourceDelta delta, int buildKind, ValOperation operation){
		ValidationRequest request = new ValidationRequest(project, delta, buildKind, operation);
		if (_job == null){
			_job = new ValBuilderJob();
			_job.add(request);
			_job.schedule();
		}
		else {
			_job.add(request);
		}
	}
	
	private static synchronized ValidationRequest getRequest(){
		ValidationRequest request = _work.poll();
		if (request == null){
			_job = null;
		}
		return request;
	}
	
	/**
	 * Each validation run is done in it's own job.
	 * 
	 * @param project the project that is being validated
	 * @param delta the delta that is being validated. This may be null, in which case we do a 
	 * full validation of the project.
	 * 
	 * @param buildKind the kind of build.
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#AUTO_BUILD
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#CLEAN_BUILD
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#FULL_BUILD
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#INCREMENTAL_BUILD
	 * 
	 * @param operation some global context for the validation operation
	 * 
	 */
	private ValBuilderJob(){
		super(ValMessages.JobName);
	}
	
	private void add(ValidationRequest request){
		_work.add(request);
	}
	
	public boolean belongsTo(Object family) {
		if (family == ResourcesPlugin.FAMILY_MANUAL_BUILD)return true;
		if (family == ValidationBuilder.FAMILY_VALIDATION_JOB){
			return true;
		}
			
		return super.belongsTo(family);
	}

	public IStatus runInWorkspace(IProgressMonitor monitor) {
		Tracing.log("ValBuilderJob-01: Starting"); //$NON-NLS-1$
		_monitor = monitor;
		
		ValidationRequest request = getRequest();
		while(request != null){
			_request = request;
			run();
			request = getRequest();
		}
		_request = null;
		
		Tracing.log("ValBuilderJob-02: Finished"); //$NON-NLS-1$
		return Status.OK_STATUS;
	}
	
	private void run(){
		setName(ValMessages.JobName + " " + _request.getProject().getName()); //$NON-NLS-1$
		try {		
	        IValidatorVisitor startingVisitor = new IValidatorVisitor(){

	            public void visit(Validator validator, IProject project, ValType valType, 
	                ValOperation operation, IProgressMonitor monitor) {
	                
	                validator.validationStarting(project, operation.getState(), monitor);                   
	            }               
	        };

	        ValManager.getDefault().accept(startingVisitor, _request.getProject(), ValType.Build, _request.getOperation(), _monitor);
		  
			if (_request.getDelta() == null)fullBuild();
			else deltaBuild();

			
		}
		catch (ProjectUnavailableError e){
			ValidationPlugin.getPlugin().handleProjectUnavailableError(e);
		}
		catch (ResourceUnavailableError e){
			ValidationPlugin.getPlugin().handleResourceUnavailableError(e);
		}
		catch (CoreException e){
			ValidationPlugin.getPlugin().handleException(e);
		}
		finally {
		  IValidatorVisitor finishedVisitor = new IValidatorVisitor(){

		    public void visit(Validator validator, IProject project, ValType valType,
		      ValOperation operation, IProgressMonitor monitor) {

		      validator.validationFinishing(project, operation.getState(), monitor);              
		    }           
		  };
          ValManager.getDefault().accept(finishedVisitor, _request.getProject(), ValType.Build, _request.getOperation(), _monitor);
		}
		
	}

	private void deltaBuild() throws CoreException {
		ResourceCounter counter = new ResourceCounter();
		_request.getDelta().accept(counter);
		_subMonitor = SubMonitor.convert(_monitor, counter.getCount());
		_request.getDelta().accept(this);		
	}

	private void fullBuild() throws CoreException {
		ResourceCounter counter = new ResourceCounter();
		_request.getProject().accept(counter, 0);
		_subMonitor = SubMonitor.convert(_monitor, counter.getCount());
		_request.getProject().accept(this);
		
	}

	@SuppressWarnings("deprecation")
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		if (DisabledResourceManager.getDefault().isDisabled(resource)){
			MarkerManager.getDefault().deleteMarkers(resource, _request.getOperation().getStarted(), IResource.DEPTH_INFINITE);
			return false;
		}
		int kind = delta.getKind();
		boolean isChanged = (kind & IResourceDelta.CHANGED) != 0;
		if (isChanged &&  (delta.getFlags() & InterestedFlags) == 0)return true;
		
		if ((kind & (IResourceDelta.ADDED | IResourceDelta.CHANGED)) != 0){
			ValManager.getDefault().validate(_request.getProject(), resource, delta.getKind(), ValType.Build, 
				_request.getBuildKind(), _request.getOperation(), _subMonitor.newChild(1));
		}
				
		IDependencyIndex index = ValidationFramework.getDefault().getDependencyIndex();
		if (index.isDependedOn(resource)){
			MarkerManager mm = MarkerManager.getDefault();
			for (DependentResource dr : index.get(resource)){
				Validator val = dr.getValidator();
				if (Friend.shouldValidate(val, dr.getResource(), ValType.Build, new ContentTypeWrapper())){
					_request.getOperation().getState().put(ValidationState.TriggerResource, resource);
					ValidationEvent event = new ValidationEvent(dr.getResource(), IResourceDelta.NO_CHANGE, delta);
					if (val.shouldClearMarkers(event))mm.clearMarker(dr.getResource(), val); 
					ValManager.getDefault().validate(val, _request.getOperation(), dr.getResource(), 
						IResourceDelta.NO_CHANGE, _monitor, event);
				}
			}
		}
				
		return true;
	}

	public boolean visit(IResource resource) throws CoreException {
		try {
			if (DisabledResourceManager.getDefault().isDisabled(resource)){
				MarkerManager.getDefault().deleteMarkers(resource, _request.getOperation().getStarted(), IResource.DEPTH_INFINITE);
				return false;
			}
			ValManager.getDefault().validate(_request.getProject(), resource, IResourceDelta.NO_CHANGE, ValType.Build, 
				_request.getBuildKind(), _request.getOperation(), _subMonitor.newChild(1));
		}
		catch (ResourceUnavailableError e){
			if (Tracing.isLogging())Tracing.log("ValBuilderJob-02: " + e.toString()); //$NON-NLS-1$
			return false;
		}
		return true;
	}
	
	static class ResourceCounter implements IResourceProxyVisitor, IResourceDeltaVisitor {
		
		private int _count;

		public int getCount() {
			return _count;
		}

		public boolean visit(IResourceProxy proxy) throws CoreException {
			_count++;
			return true;
		}

		public boolean visit(IResourceDelta delta) throws CoreException {
			_count++;
			return true;
		}		
	}
	
	static class ValidationRequest {
		/** The project that is being built. */
		private IProject 			_project;
		
		/** The resource delta that triggered the build, it will be null for a full build. */
		private IResourceDelta		_delta;
		
		private ValOperation		_operation;
		
		/** 
		 * The kind of build.
		 * 
		 *  @see org.eclipse.core.resources.IncrementalProjectBuilder
		 */
		private int					_buildKind;
		
		public ValidationRequest(IProject project, IResourceDelta delta, int buildKind, ValOperation operation){
			_project = project;
			_delta = delta;
			_buildKind = buildKind;
			_operation = operation;
		}

		public IProject getProject() {
			return _project;
		}

		public IResourceDelta getDelta() {
			return _delta;
		}

		public ValOperation getOperation() {
			return _operation;
		}

		public int getBuildKind() {
			return _buildKind;
		}
	}

}
