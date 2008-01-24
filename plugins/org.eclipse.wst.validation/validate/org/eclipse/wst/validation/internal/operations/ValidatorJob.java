/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.operations;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;
import org.eclipse.wst.validation.IPerformanceMonitor;
import org.eclipse.wst.validation.PerformanceCounters;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.Misc;
import org.eclipse.wst.validation.internal.ResourceConstants;
import org.eclipse.wst.validation.internal.ResourceHandler;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;

public class ValidatorJob extends Job {


	private IProject			project;
	private IWorkbenchContext 	helper;
	private IValidatorJob		validator;
	
	public ValidatorJob(String name) {
		super(name);
	}
	   
	public ValidatorJob( IValidatorJob validator, String displayName, String name, IProject project, IWorkbenchContext aHelper  ){
		super(displayName);
		this.project = project;
		this.helper = aHelper;
		this.validator = validator;
	}
	
	//revisit reporter in the code  below
	//subtask information is displayed in the monitor created by the Job
	//error information is reported by the IReporter
	
	protected IStatus run(IProgressMonitor monitor) {
		
		IPerformanceMonitor pm = ValidationFramework.getDefault().getPerformanceMonitor();
		long elapsed = -1;
		long cpuTime = -1;
		if (pm.isCollecting()){
			elapsed = System.currentTimeMillis();
			cpuTime = Misc.getCPUTime();
		}

		monitor.beginTask("Validating", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
		IStatus status = IValidatorJob.OK_STATUS;
		WorkbenchReporter	reporter = new WorkbenchReporter( project, monitor );

		ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(validator);
		
		try {

			String message = ResourceHandler.getExternalizedMessage(
				ResourceConstants.VBF_STATUS_STARTING_VALIDATION,
				new String[]{helper.getProject().getName(), vmd.getValidatorDisplayName()});
			
			monitor.subTask(message);		
			status = validator.validateInJob(helper, reporter);
		
			message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_ENDING_VALIDATION,
				new String[]{helper.getProject().getName(), vmd.getValidatorDisplayName()});
			monitor.subTask(message);
 
		} catch (OperationCanceledException exc) {
			throw exc;
		} catch (ValidationException exc) {
			// First, see if a validator just caught all Throwables and
			// accidentally wrapped a MessageLimitException instead of
			// propagating it.
			if (exc.getAssociatedException() != null) {
				if (exc.getAssociatedException() instanceof ValidationException) {
					ValidationException vexc = (ValidationException) exc.getAssociatedException();
					vexc.setClassLoader(validator.getClass().getClassLoader()); 
				}
			}
			// If there is a problem with this particular validator, log the
			// error and continue with the next validator.
			exc.setClassLoader(validator.getClass().getClassLoader());

			ValidationPlugin.getPlugin().handleException(exc);
			ValidationPlugin.getPlugin().handleException(exc.getAssociatedException());
			String message = ResourceHandler.getExternalizedMessage(
					ResourceConstants.VBF_STATUS_ENDING_VALIDATION_ABNORMALLY,
					new String[]{helper.getProject().getName(), vmd.getValidatorDisplayName()});
			
			monitor.subTask(message);
			if (exc.getAssociatedMessage() != null) {
				reporter.addMessage(validator, exc.getAssociatedMessage());
			}
		} catch (Exception e) {
			ValidationPlugin.getPlugin().handleException(e);
			String mssg = ResourceHandler.getExternalizedMessage(
				ResourceConstants.VBF_STATUS_ENDING_VALIDATION_ABNORMALLY,
				new String[]{helper.getProject().getName(), vmd.getValidatorDisplayName() });
			
			monitor.subTask(mssg);
			
		} finally {
			try {
				validator.cleanup(reporter);
			} catch (OperationCanceledException e) {
				throw e;
			} catch (Exception exc) {
				ValidationPlugin.getPlugin().handleException(exc);
				
				String[] msgParm = {exc.getClass().getName(), vmd.getValidatorDisplayName(), (exc.getMessage() == null ? "" : exc.getMessage())}; //$NON-NLS-1$				
				Message message = ValidationPlugin.getMessage();
				message.setSeverity(IMessage.NORMAL_SEVERITY);
				message.setId(ResourceConstants.VBF_EXC_RUNTIME);
				message.setParams(msgParm);				
				status = WTPCommonPlugin.createErrorStatus(message.getText());
				return status;
			} finally {
				try {
					helper.cleanup(reporter);
					vmd.removeHelper( validator );
				}catch (OperationCanceledException e) {
					throw e;
				} catch (Exception exc) {
					ValidationPlugin.getPlugin().handleException(exc);
					String[] msgParm = {exc.getClass().getName(), vmd.getValidatorDisplayName(), (exc.getMessage() == null ? "" : exc.getMessage())}; //$NON-NLS-1$
					Message message = ValidationPlugin.getMessage();
					message.setSeverity(IMessage.NORMAL_SEVERITY);
					message.setId(ResourceConstants.VBF_EXC_RUNTIME);
					message.setParams(msgParm);
					reporter.addMessage(validator, message);
	
					status = WTPCommonPlugin.createErrorStatus(message.getText());	
					return status;
				} finally {
					helper.setProject(null);
					vmd.removeHelper( validator );
					helper = null;
					reporter = null;
				}
			}
			//reporter.getProgressMonitor().worked(((delta == null) ? 1 : delta.length)); // One
			//monitor.worked(((delta == null) ? 1 : delta.length)); // One
			monitor.done();
		}
		if (pm.isCollecting()){
			if (cpuTime != -1){
				cpuTime = Misc.getCPUTime() - cpuTime;
			}
			String projectName = null;
			if (project != null)projectName = project.getName();
			pm.add(new PerformanceCounters(vmd.getValidatorUniqueName(), 
				vmd.getValidatorDisplayName(),projectName, -1, 
				System.currentTimeMillis()-elapsed, cpuTime));
		}
		return status;
	}

	public boolean belongsTo(Object family) {
		return (project.getName() + ValidatorManager.VALIDATOR_JOB_FAMILY).equals(family);
	}	
}
