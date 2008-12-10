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

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.model.IValidatorVisitor;

/**
 * Keep track of a validation operation when it is triggered as part of a build.
 * @author karasiuk
 *
 */
public class ValOperationManager implements IResourceChangeListener {
	
	/*
	 * I tried various tests to see what sort of change events I would get. This is what I 
	 * observed using Eclipse 3.4:
	 * 
	 * Auto Build On
	 * 
	 *   Clean All
	 *     - workspace, clean, pre
	 *     - workspace, clean post
	 *     - workspace, auto, post
	 *     
	 *   Clean Some
	 *     - project1, clean, pre
	 *     - project1, clean, post
	 *     - project2, clean, pre
	 *     - project2, clean, post
	 *     - workspace, Auto, post
	 *     
	 *   Build Working Set - NA
	 *   Build Project - NA
	 *   Build All - NA
	 *   
	 *   Ctrl-S
	 *     - workspace, auto, pre
	 *     - workspace, auto, post
	 *     
	 * Auto build Off
	 * 
	 *   Clean All
	 *     - same as (auto build on), but no workspace,auto,post event
	 *     
	 *   Clean Some 
	 *     - same as (auto build on), but no workspace,auto,post event
	 *     
	 *   Build Working Set
	 *     - project1, incremental, pre
	 *     - project1, incremental, post
	 *     - project2, incremental, pre
	 *     - project2, incremental, post
	 *     
	 *   Build Project
	 *     - same as above
	 *     
	 *   Build All
	 *     - workspace, incremental, pre
	 *     - workspace, incremental, post
	 *     
	 *   Ctrl-S - NA
	 *   
	 * For the case where a subset of the projects are built there is no way to guess whether they are part of the
	 * same operation or not. Eclipse threats them as independent events, and so will the validation framework.
	 * 
	 * So for example, if the user selected two projects (p1 and p2) and built them, the framework would call the
	 * validators like this:
	 * 
	 * validation starting on null
	 * validation starting on P1
	 *  - individual events per resource
	 * validation finished on P1
	 * validation finished on null 
	 * 
	 * validation starting on null
	 * validation starting on P2
	 *  - individual events per resource
	 * validation finished on P2
	 * validation finished on null 
	 */
	
	/**
	 * This operation is in affect for a build cycle. At the end of the build it is reinitialized.
	 */
	private ValOperation 	_operation;
	
	
	/**
	 * In the very common case of doing a clean all (with auto build turned on), Eclipse signals two 
	 * workspace, auto build, post events. One at the end of the clean and one at the end of the
	 * real build.
	 * 
	 * If we are doing a clean all, with auto build turned on, we increment this by one, 
	 * so that we know to throw away the first workspace, auto build, post event.
	 */
	private AtomicInteger _discardAutoPost = new AtomicInteger();

	public static ValOperationManager getDefault(){
		return Singleton.valOperationManager;
	}
	
	private ValOperationManager(){}
				
	public void resourceChanged(IResourceChangeEvent event) {
		int type = event.getType();
		int kind = event.getBuildKind();
		
		if (kind == IncrementalProjectBuilder.CLEAN_BUILD && ((type & IResourceChangeEvent.PRE_BUILD) != 0)){
			processClean(event);
		}
		
		if (isBuildStarting(event)){
			synchronized(this){
				_operation = new ValOperation(true);
			}
			IValidatorVisitor visitor = new IValidatorVisitor(){

				public void visit(Validator validator, IProject project, ValType valType, 
					ValOperation operation, IProgressMonitor monitor) {
					
					validator.validationStarting(project, operation.getState(), monitor);					
				}				
			};
			ValManager.getDefault().accept(visitor, null, ValType.Build, _operation, new NullProgressMonitor());
			
		}
		
		if (isBuildFinished(event)){
			ValOperationJob finished = new ValOperationJob(getOperation());
			finished.schedule();
			synchronized(this){
				_operation = null;
			}
		}
		
		
		if (Tracing.isLogging()){
			String kindName = null;
			if (kind == IncrementalProjectBuilder.AUTO_BUILD)kindName = "Auto"; //$NON-NLS-1$
			else if (kind == IncrementalProjectBuilder.CLEAN_BUILD)kindName = "Clean"; //$NON-NLS-1$
			else if (kind == IncrementalProjectBuilder.FULL_BUILD)kindName = "Full"; //$NON-NLS-1$
			else if (kind == IncrementalProjectBuilder.INCREMENTAL_BUILD)kindName = "Incremental"; //$NON-NLS-1$
			else kindName = String.valueOf(kind);
			
			StringBuffer b = new StringBuffer(100);
			
			String sourceName = "unknown"; //$NON-NLS-1$
			if (event.getSource() instanceof IResource) {
				IResource res = (IResource) event.getSource();
				sourceName = res.getName();
			}
			else if (event.getSource() instanceof IWorkspace) {
				sourceName = "Workspace";			 //$NON-NLS-1$
			}
			b.append("ValOperationManager-01: A resource has changed, source="+sourceName+", kind="+kindName+", event type=("+type); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if ((type & IResourceChangeEvent.POST_BUILD) != 0)b.append(", post build"); //$NON-NLS-1$
			if ((type & IResourceChangeEvent.PRE_BUILD) != 0){
				b.append(", pre build"); //$NON-NLS-1$
			}
			b.append(')');
			IResourceDelta rd = event.getDelta();
			if (rd == null)b.append(", there was no resource delta"); //$NON-NLS-1$
			
			Tracing.log(b);
		}
		
	}
	
	/**
	 * Determine if we are starting a new build cycle.
	 * @param event
	 * @return
	 */
	private boolean isBuildStarting(IResourceChangeEvent event) {
		int type = event.getType();
		int kind = event.getBuildKind();
		boolean isWorkspace = event.getSource() instanceof IWorkspace;
		boolean preBuild = (type & IResourceChangeEvent.PRE_BUILD) != 0;
		
		if (ResourcesPlugin.getWorkspace().isAutoBuilding()){
			if (isWorkspace && preBuild && kind == IncrementalProjectBuilder.CLEAN_BUILD){
				_discardAutoPost.set(1);
				return true;
			}
			
			if (isWorkspace && preBuild && kind == IncrementalProjectBuilder.AUTO_BUILD)return true;
		}
		else {
			if (isWorkspace && preBuild && kind == IncrementalProjectBuilder.INCREMENTAL_BUILD)return true;
		}
		return false;
	}

	/**
	 * Determine if we are at the end of a build cycle. This will give callers the ability to
	 * clear caches etc.
	 *  
	 * @param event
	 * @return return true if we are just finishing a build.
	 */
	private boolean isBuildFinished(IResourceChangeEvent event) {
		synchronized(this){
			if (_operation == null)return false;
		}
		
		int type = event.getType();
		int kind = event.getBuildKind();
		boolean isWorkspace = event.getSource() instanceof IWorkspace;
		boolean postBuild = (type & IResourceChangeEvent.POST_BUILD) != 0;

		
		if (ResourcesPlugin.getWorkspace().isAutoBuilding()){
			if (isWorkspace && postBuild && kind == IncrementalProjectBuilder.AUTO_BUILD){
				if (!_discardAutoPost.compareAndSet(1, 0))return true;
			}
		}
		else {
			if (isWorkspace && postBuild && kind == IncrementalProjectBuilder.INCREMENTAL_BUILD)return true;
		}
		
		return false;
	}
	
	private void processClean(IResourceChangeEvent event){
		// Originally I was using this to monitor IProject build requests as well, but that is not not needed
		// since these will be handled by the IncrementalProjectBuilder.clean() method.
		IProgressMonitor monitor = new NullProgressMonitor();
		Object source = event.getSource();
		if (source instanceof IWorkspace) {
			ValManager.getDefault().clean(null, getOperation(), monitor);
		}
		
	}

	/**
	 * Answer the current validation operation. If we are not in a multiple project validation
	 * we will return a new one. 
	 */
	public synchronized ValOperation getOperation() {
		/*
		 * If we don't have a current operation, we create a new one. The only time we save
		 * the operation is when we are sure that we are in a multi project validation.
		 */
		if (_operation == null)return new ValOperation();
		return _operation;
	}
	
	/**
	 * Store the singleton for the ValOperationManager. This approach is used to avoid having to synchronize the
	 * ValOperationManager.getDefault() method.
	 * 
	 * @author karasiuk
	 *
	 */
	private static class Singleton {
		static ValOperationManager valOperationManager = new ValOperationManager();
	}


}
