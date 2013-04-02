/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.text.MessageFormat;
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
 * </p>
 * @author karasiuk
 *
 */
public final class ValBuilderJob extends WorkspaceJob {
	
	private static ValBuilderJob _job;
	private static Queue<ValidationRequest> _work = new LinkedList<ValidationRequest>();
	
	private final ValOperation _operation = new ValOperation();
		
	/** The types of changes we are interested in. */
	private final static int	InterestedFlags = IResourceDelta.CONTENT | IResourceDelta.ENCODING |
		IResourceDelta.MOVED_FROM | IResourceDelta.MOVED_TO;
	
	public static synchronized void validateProject(IProject project, IResourceDelta delta, int buildKind){
		ValidationRequest request = new ValidationRequest(project, delta, buildKind);
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
	
	@Override
	public boolean belongsTo(Object family) {
		if (family == ResourcesPlugin.FAMILY_MANUAL_BUILD)return true;
		if (family == ValidationBuilder.FAMILY_VALIDATION_JOB){
			return true;
		}
			
		return super.belongsTo(family);
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) {
		Tracing.log("ValBuilderJob-01: Starting"); //$NON-NLS-1$
		
		try {
			startingValidation(monitor);
			
			ValidationRequest request = getRequest();
			while(request != null){
				run(request, monitor);
				request = getRequest();
			}
		}
		finally {
			finishingValidation(monitor);
		}
		
		Tracing.log("ValBuilderJob-02: Finished"); //$NON-NLS-1$
		return Status.OK_STATUS;
	}

	private void startingValidation(IProgressMonitor monitor) {
        IValidatorVisitor startingVisitor = new IValidatorVisitor(){

            public void visit(Validator validator, IProject project, ValType valType, 
                ValOperation operation, IProgressMonitor monitor) {
                
                validator.validationStarting(project, operation.getState(), monitor);                   
            }               
        };

        ValManager.getDefault().accept(startingVisitor, null, ValType.Build, getOperation(), monitor);
	}
	
	private void finishingValidation(IProgressMonitor monitor) {
		
		IValidatorVisitor finishedVisitor = new IValidatorVisitor(){

		    public void visit(Validator validator, IProject project, ValType valType,
		      ValOperation operation, IProgressMonitor monitor) {

		      validator.validationFinishing(project, operation.getState(), monitor);              
		    }           
		  };
		  ValManager.getDefault().accept(finishedVisitor, null, ValType.Build, getOperation(), monitor);
	}

	private void run(ValidationRequest request, IProgressMonitor monitor){
		setName(MessageFormat.format(ValMessages.JobNameWithProjectName,new Object[] { request.getProject().getName() })); 
		try {		
	        IValidatorVisitor startingVisitor = new IValidatorVisitor(){

	            public void visit(Validator validator, IProject project, ValType valType, 
	                ValOperation operation, IProgressMonitor monitor) {
	                
	                validator.validationStarting(project, operation.getState(), monitor);                   
	            }               
	        };

	        ValManager.getDefault().accept(startingVisitor, request.getProject(), ValType.Build, getOperation(), monitor);
		  
			if (request.getDelta() == null)fullBuild(request, monitor);
			else deltaBuild(request, monitor);

			
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
          ValManager.getDefault().accept(finishedVisitor, request.getProject(), ValType.Build, getOperation(), monitor);
		}
		
	}

	private void deltaBuild(ValidationRequest request, IProgressMonitor monitor) throws CoreException {
		ResourceCounter counter = new ResourceCounter();
		request.getDelta().accept(counter);
		SubMonitor subMonitor = SubMonitor.convert(monitor, counter.getCount());
		Visitor vistitor = new Visitor(request, subMonitor, monitor, getOperation());
		request.getDelta().accept(vistitor);		
	}

	private void fullBuild(ValidationRequest request, IProgressMonitor monitor) throws CoreException {
		ResourceCounter counter = new ResourceCounter();
		request.getProject().accept(counter, 0);
		SubMonitor subMonitor = SubMonitor.convert(monitor, counter.getCount());
		Visitor vistitor = new Visitor(request, subMonitor, monitor, getOperation());
		request.getProject().accept(vistitor);
		
	}
	
	private ValOperation getOperation(){
		return _operation;
	}

	static final class ResourceCounter implements IResourceProxyVisitor, IResourceDeltaVisitor {
		
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
	
	static final class ValidationRequest {
		/** The project that is being built. */
		private final IProject 			_project;
		
		/** The resource delta that triggered the build, it will be null for a full build. */
		private final IResourceDelta	_delta;
		
		/** 
		 * The kind of build.
		 * 
		 *  @see org.eclipse.core.resources.IncrementalProjectBuilder
		 */
		private final int					_buildKind;
		
		public ValidationRequest(IProject project, IResourceDelta delta, int buildKind){
			_project = project;
			_delta = delta;
			_buildKind = buildKind;
		}

		public IProject getProject() {
			return _project;
		}

		public IResourceDelta getDelta() {
			return _delta;
		}

		public int getBuildKind() {
			return _buildKind;
		}
	}
	
	private final static class Visitor implements IResourceDeltaVisitor, IResourceVisitor{
		
		private final ValidationRequest 	_request;
		private final SubMonitor 			_subMonitor;
		private final IProgressMonitor 		_monitor;
		private final ValOperation			_operation;
		
		public Visitor(ValidationRequest request, SubMonitor subMonitor, IProgressMonitor monitor, ValOperation operation){
			_request = request;
			_subMonitor = subMonitor;
			_monitor = monitor;
			_operation = operation;
		}
		
		public boolean visit(IResource resource) throws CoreException {
			try {
				if (DisabledResourceManager.getDefault().isDisabled(resource)){
					MarkerManager.getDefault().deleteMarkers(resource, _operation.getStarted(), IResource.DEPTH_INFINITE);
					return false;
				}
				ValManager.getDefault().validate(_request.getProject(), resource, IResourceDelta.NO_CHANGE, ValType.Build, 
					_request.getBuildKind(), _operation, _subMonitor.newChild(1));
			}
			catch (ResourceUnavailableError e){
				if (Tracing.isLogging())Tracing.log("ValBuilderJob-02: " + e.toString()); //$NON-NLS-1$
				return false;
			}
			return true;
		}
		
		@SuppressWarnings("deprecation")
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			if (DisabledResourceManager.getDefault().isDisabled(resource)){
				MarkerManager.getDefault().deleteMarkers(resource, _operation.getStarted(), IResource.DEPTH_INFINITE);
				return false;
			}
			int kind = delta.getKind();
			boolean isChanged = (kind & IResourceDelta.CHANGED) != 0;
			if (isChanged &&  (delta.getFlags() & InterestedFlags) == 0)return true;
			
			// Check for file ADDED and REMOVED events, which means that the file may have moved to a new
			// project. To be safe we clear it's cached list of validators.
			if (((kind & (IResourceDelta.ADDED | IResourceDelta.REMOVED)) != 0)	&& resource.getType() == IResource.FILE) {
				ValManager.getDefault().clearValProperty(resource.getProject());
			}

			if ((kind & (IResourceDelta.ADDED | IResourceDelta.CHANGED)) != 0){
				ValManager.getDefault().validate(_request.getProject(), resource, delta.getKind(), ValType.Build,
						_request.getBuildKind(), _operation, _subMonitor.newChild(1));
			}
			
			if ((kind & (IResourceDelta.REMOVED)) != 0){
				
				IResource project = resource.getProject();
				if (!_operation.isValidatedProject(project))
				{
					ValManager.getDefault().validate(_request.getProject(), delta.getResource(), IResourceDelta.REMOVED, ValType.Build,
						_request.getBuildKind(), _operation, _subMonitor.newChild(1));
					_operation.addValidatedProject(project);
				}				
			}
					
			IDependencyIndex index = ValidationFramework.getDefault().getDependencyIndex();
			if (index.isDependedOn(resource)){
				MarkerManager mm = MarkerManager.getDefault();
				for (DependentResource dr : index.get(resource)){
					Validator val = dr.getValidator();
					if (Friend.shouldValidate(val, dr.getResource(), ValType.Build, new ContentTypeWrapper())){
						_operation.getState().put(ValidationState.TriggerResource, resource);
						ValidationEvent event = new ValidationEvent(dr.getResource(), IResourceDelta.NO_CHANGE, delta);
						if (val.shouldClearMarkers(event))mm.clearMarker(dr.getResource(), val);
							ValManager.getDefault().validate(val, _operation, dr.getResource(),
									IResourceDelta.NO_CHANGE, _monitor, event);
						}
					}
				}
			return true;
		}
	}
}
