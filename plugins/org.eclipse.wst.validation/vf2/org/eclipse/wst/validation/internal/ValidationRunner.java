/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.model.IValidatorVisitor;

/**
 * Run the validators on a selected set of resources.
 * <p>
 * This is used to run manual validations (i.e. the user selects the Validate menu item), 
 * or it is invoked programmatically by a third party through the ValidationFramework API.
 * It is not used for the build based invocation.
 * </p> 
 * @author karasiuk
 *
 */
public final class ValidationRunner implements IWorkspaceRunnable {
	
	private Map<IProject, Set<IResource>>		_projects;
	private	ValType			_valType;
	private ValOperation	_valOperation;
	
	/**
	 * Validate the selected projects and/or resources.
	 * 
	 * @param projects
	 *            The selected projects. The key is an IProject and the value is
	 *            the Set of IResources that were selected. Often this will be
	 *            every resource in the project.
	 * 
	 * @param valType
	 *            The type of validation that has been requested.
	 * 
	 * @param monitor
	 *            Progress monitor.
	 * 
	 * @param atomic
	 *            Run as an atomic workspace operation?
	 */
	public static ValOperation validate(Map<IProject, Set<IResource>> projects, ValType valType, 
		IProgressMonitor monitor, boolean atomic) throws CoreException{
		ValidationRunner me = new ValidationRunner(projects, valType);
		if (atomic)ResourcesPlugin.getWorkspace().run(me, null, IWorkspace.AVOID_UPDATE, monitor);
		else me.execute(monitor);
		return me._valOperation;
	}
	
	/**
	 * Validate the selected file. This is a convenience method, it simply calls the more flexible 
	 * validate with Map method. 
	 * 
	 * @param file
	 *            The file to be validated.
	 * 
	 * @param valType
	 *            The type of validation that has been requested.
	 * 
	 * @param monitor
	 *            Progress monitor.
	 * 
	 * @param atomic
	 *            Run as an atomic workspace operation?
	 */
	public static ValOperation validate(IFile file, ValType valType, IProgressMonitor monitor, boolean atomic) throws CoreException{
	    final Map<IProject, Set<IResource>> map = new HashMap<IProject, Set<IResource>>(1);
	      
	    Set<IResource> set = new HashSet<IResource>(1);
	    set.add(file);
	    map.put(file.getProject(), set);
	    return validate(map, valType, monitor, atomic);		
	}
	
	private ValidationRunner(Map<IProject, Set<IResource>> projects, ValType valType){
		_projects = projects;
		_valType = valType;
		
	}
	
	private ValOperation execute(IProgressMonitor monitor){
		_valOperation = new ValOperation();
		ValManager manager = ValManager.getDefault();
		
		IValidatorVisitor startingVisitor = new IValidatorVisitor(){
			public void visit(Validator validator, IProject project, ValType valType,
				ValOperation operation, IProgressMonitor monitor) {
				validator.validationStarting(project, operation.getState(), monitor);
			}			
		};
		
		IValidatorVisitor finishedVisitor = new IValidatorVisitor(){

			public void visit(Validator validator, IProject project, ValType valType,
				ValOperation operation, IProgressMonitor monitor) {

				validator.validationFinishing(project, operation.getState(), monitor);				
			}			
		};
		
		manager.accept(startingVisitor, null, _valType, _valOperation, monitor);
				
		for (Map.Entry<IProject, Set<IResource>> me : _projects.entrySet()){
			if (monitor.isCanceled()){
				_valOperation.setCanceled(true);
				return _valOperation;
			}
			IProject project = me.getKey();
			manager.accept(startingVisitor, project, _valType, _valOperation, monitor);
			for (IResource resource : me.getValue()){
				try {
					manager.validate(project, resource, IResourceDelta.NO_CHANGE, _valType, 
							IncrementalProjectBuilder.AUTO_BUILD, _valOperation, monitor);
				}
				catch (ResourceUnavailableError error){
					// if the resource is no longer available, we can't validate it, so we should just move on. 
				}
			}
			manager.accept(finishedVisitor, project, _valType, _valOperation, monitor);
		}
		manager.accept(finishedVisitor, null, _valType, _valOperation, monitor);
		return _valOperation;
	}

	public void run(IProgressMonitor monitor) throws CoreException {
		execute(monitor);		
	}

}
