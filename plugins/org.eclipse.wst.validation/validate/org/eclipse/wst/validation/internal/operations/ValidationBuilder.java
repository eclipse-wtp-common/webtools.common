/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.operations;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.InternalValidatorManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ResourceConstants;
import org.eclipse.wst.validation.internal.ResourceHandler;
import org.eclipse.wst.validation.internal.ValBuilderJob;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValOperationManager;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * Validation Framework Builder.
 * <p>
 * This builder is configured on J2EE IProjects automatically, and can be added to other types of
 * projects through the Properties page. It launches validation on the project if the project has
 * build validation enabled.
 * </p>
 * <p>
 * This launches a Job for the new V2 validators and also a Job for each of the Job based V1
 * validators. If there are any "in-line" V1 validations they are done as part of this builder.
 * Because of all the jobs that this builder spawns, the build will usually be finished long before
 * all the validation has finished.
 * </p>
 */
public class ValidationBuilder extends IncrementalProjectBuilder {
	public static final int NO_DELTA_CHANGE = -1;
	protected List<IProject> referencedProjects;
	protected IWorkbenchContext workbenchContext = null;
	
	/** All the jobs that the validation framework spawns will belong to this family. */
	public static final Object FAMILY_VALIDATION_JOB = new Object();

	public ValidationBuilder() {
	}

	private IProject[] getAllReferencedProjects(IProject project, Set<IProject> visitedProjects) {
		if (visitedProjects == null)visitedProjects = new HashSet<IProject>();
		else if (visitedProjects.contains(project))return getReferencedProjects();
		else visitedProjects.add(project);
		
		if (referencedProjects == null)referencedProjects = new ArrayList<IProject>();
		try {
			if (project.isAccessible()) {
				IProject[] refProjArray = project.getReferencedProjects();
				collectReferecedProject(refProjArray);
				for (IProject refProject : refProjArray) {
					getAllReferencedProjects(refProject, visitedProjects);
				}
			}
			return getReferencedProjects();
		} catch (CoreException e) {
			ValidationPlugin.getPlugin().handleException(e);
		}
		return null;
	}
	
	public IWorkbenchContext getWorkbenchContext() {
		if(workbenchContext == null) {
			workbenchContext = new WorkbenchContext();
			workbenchContext.setProject(getProject());
		}
		return workbenchContext;
	}

	/**
	 * Add the projects from refProjArray to the list of referenced projects (if they are not
	 * already in the list).
	 * @param refProjArray
	 */
	private void collectReferecedProject(IProject[] refProjArray) {
		for (IProject project : refProjArray) {
			if (!referencedProjects.contains(project))referencedProjects.add(project);
		}
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		newClean(monitor);
		IProject currentProject = getProject();
		if (currentProject == null || !currentProject.isAccessible())return;
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(currentProject);
			ValidatorMetaData[] vmds = prjp.getValidators();
			for (int i = 0; i < vmds.length; i++) {
				ValidatorMetaData vmd = vmds[i];
				// For validators who aren't going to run, clear their messages from the task list.
				// Don't need to check for duplicate entries because each Validator must be unique.
				// The uniqueness of each Validator is checked by the plugin registry.
				WorkbenchReporter.removeAllMessages(currentProject, vmd.getValidatorNames(), null);
			}
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
		}

	}

	private IProject[] getReferencedProjects() {
		IProject[] refProjArray = new IProject[referencedProjects.size()];
		return referencedProjects.toArray(refProjArray);
	}
	
	@SuppressWarnings("unchecked")
	public IProject[] build(int kind, Map parameters, IProgressMonitor monitor) {
		IResourceDelta delta = null;
		IProject project = getProject();
		// GRK I wonder why this builder needs to know about all the other referenced projects?
		// won't they have builders of their own.
		IProject[] referenced = getAllReferencedProjects(project, null);
		if (ValidationFramework.getDefault().isSuspended(project) || 
			ValManager.getDefault().isDisabled(project))return referenced;

		try {
			newBuild(kind, monitor);

			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			delta = getDelta(project);
			boolean doFullBuild = (kind == FULL_BUILD);
			
			// It is possible for kind to == AUTO_BUILD while delta is null
			// (saw this when creating a project by copying another project.)
			// However, a "Rebuild Project" will invoke this builder with
			// kind==FULL_BUILD and a null delta, and validation should run in that case.
			if (!doFullBuild && delta == null) {
				if (isReferencedProjectInDelta(referenced)) {
					performFullBuildForReferencedProjectChanged(monitor, prjp);
				} else {
					String[] msgParms = new String[]{project.getName()};
					monitor.subTask(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_NULL_DELTA, msgParms));
					// A null delta means that a full build must be performed,
					// but this builder was invoked with an incremental or automatic
					// build kind. Return without doing anything so that the user
					// doesn't have to wait forever.
				}
				return referenced;
			}
			if (doFullBuild) {
				cleanupReferencedProjectsMarkers(prjp, referenced);
				performFullBuild(monitor, prjp);
			} else {
				if (delta.getAffectedChildren().length == 0) {
					if (isReferencedProjectInDelta(referenced))
						cleanupReferencedProjectsMarkers(prjp, referenced);
						performFullBuildForReferencedProjectChanged(monitor, prjp);
					return referenced;
				}
				EnabledIncrementalValidatorsOperation operation = new EnabledIncrementalValidatorsOperation(project, delta, true);
				operation.run(monitor);
			}
			return referenced;
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
			return referenced;
		} catch (Exception e) {
			ValidationPlugin.getPlugin().handleException(e);
			return referenced;
		} finally {
			referencedProjects = null;
		}
	}
	
	private void cleanupReferencedProjectsMarkers(final ProjectConfiguration prjp, IProject[] referenced){
		//When a project references one or more project, performing a clean build on referenced
		//causes delta to be invoked on referencee, aka, parent. This causes following code to
		//be invoked.
		//The following code is trying to fix a case where Ejb project references a utility project,
		//and the clean build on utility project causes the code to come here, the ejb validator runs
		//on the ejb  project due to performFullBuildForReferencedProjectChanged() below, but it also
		//causes marker to be generated for the util project, but the markers for util project are not
		//cleaned up.   
		
		if( referenced == null || referenced.length == 0 )return;
		
		try{
			ValidatorMetaData[] enabledValidators = prjp.getEnabledFullBuildValidators(true, false);
 
			Set<ValidatorMetaData>  set = new HashSet<ValidatorMetaData>();
			set.addAll( Arrays.asList( enabledValidators ) );
			for (IProject p : referenced) {
				ProjectConfiguration refProjectCfg = ConfigurationManager.getManager().getProjectConfiguration(p);
		
				ValidatorMetaData[] refEnabledValidators = refProjectCfg.getEnabledFullBuildValidators(true, false);
				
				//remove from the set the validators which are also in child
				for(ValidatorMetaData vmd : refEnabledValidators)set.remove(vmd);
				
				for(ValidatorMetaData vmd : set)WorkbenchReporter.removeAllMessages(p, vmd.getValidator());		
			}	
		}catch (Exception exc) {
			ValidationPlugin.getPlugin().logMessage(IStatus.ERROR, exc.toString());
	}
}

	private boolean isReferencedProjectInDelta(IProject[] referenced) {
		IProject p = null;
		for (int i = 0; i < referenced.length; i++) {
			p = referenced[i];
			IResourceDelta delta = getDelta(p);
			if (delta != null && delta.getAffectedChildren().length > 0)
				return true;
		}
		return false;
	}

	private void performFullBuildForReferencedProjectChanged(IProgressMonitor monitor, ProjectConfiguration prjp) throws InvocationTargetException {
		performFullBuild(monitor, prjp, true);
	}

	private void performFullBuild(IProgressMonitor monitor, ProjectConfiguration prjp) throws InvocationTargetException {
		performFullBuild(monitor, prjp, false);
	}

	private void performFullBuild(IProgressMonitor monitor, ProjectConfiguration prjp, boolean onlyDependentValidators) throws InvocationTargetException {
		ValidatorMetaData[] enabledValidators = prjp.getEnabledFullBuildValidators(true, onlyDependentValidators);
		if ((enabledValidators != null) && (enabledValidators.length > 0)) {
			Set<ValidatorMetaData> enabledValidatorsSet = InternalValidatorManager.wrapInSet(enabledValidators);
			EnabledValidatorsOperation op = new EnabledValidatorsOperation(getProject(), enabledValidatorsSet, true);
			op.run(monitor);
		}
	}
	
	/**
	 * Run the new validation builder. This is a transition method, while we continue to have
	 * the old and new validation builders.
	 * 
	 * @param kind the kind of build
	 * 
	 * @see IncrementalProjectBuilder#AUTO_BUILD
	 * @see IncrementalProjectBuilder#CLEAN_BUILD
	 * @see IncrementalProjectBuilder#FULL_BUILD
	 * @see IncrementalProjectBuilder#INCREMENTAL_BUILD
	 */
	private void newBuild(int kind, IProgressMonitor monitor)	throws CoreException {

		IResourceDelta delta = null;
		IProject project = getProject();
		
		switch (kind){
			case AUTO_BUILD:
			case INCREMENTAL_BUILD:
				delta = getDelta(project);
				break;
		}
		
		ValBuilderJob.validateProject(project, delta, kind, ValOperationManager.getDefault().getOperation());		
	}
	
	
	/**
	 * Run the new clean method. This is a transition method, while we continue to have
	 * the old and new validation builders.
	 */
	private void newClean(IProgressMonitor monitor) throws CoreException {
		ValManager.getDefault().clean(getProject(), monitor);
	}	
	
}
