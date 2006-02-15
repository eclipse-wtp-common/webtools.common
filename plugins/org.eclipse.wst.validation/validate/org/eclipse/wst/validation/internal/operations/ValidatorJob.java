package org.eclipse.wst.validation.internal.operations;

import java.util.logging.Level;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;
import org.eclipse.wst.validation.internal.ResourceConstants;
import org.eclipse.wst.validation.internal.ResourceHandler;
import org.eclipse.wst.validation.internal.ValidationFactoryImpl;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;

public class ValidatorJob extends Job {


	private IProject			project;
	private String				validatorUniqueName;
	private IWorkbenchContext 	helper;
	
	public ValidatorJob(String name) {
		super(name);
	}


	   
	   
	public ValidatorJob(String displayName, String name, IProject project, IWorkbenchContext aHelper  ){
		super(displayName);
		validatorUniqueName = name;
		this.project = project;
		this.helper = aHelper;
	}
	
	//revisit reporter in the code  below
	//subtask information is displayed in the monitor created by the Job
	//error information is reported by the IReporter
	
	protected IStatus run(IProgressMonitor monitor) {

		IStatus status = IValidatorJob.OK_STATUS;
		WorkbenchReporter	reporter = new WorkbenchReporter( project, monitor );

		IValidatorJob	validator = null;

		
		try {
			validator = (IValidatorJob) ValidationFactoryImpl.getInstance().getValidator( validatorUniqueName );
		} catch (InstantiationException e1) {
			Logger.getLogger().logError(e1);

		}

		ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(validator);
		
//		try {
//			helper = vmd.getHelper(project);
//		} catch (InstantiationException e1) {
//			Logger.getLogger().logError(e1);
//		}
		
		Logger logger = ValidationPlugin.getPlugin().getMsgLogger();

		try {

			String message = ResourceHandler.getExternalizedMessage(
				ResourceConstants.VBF_STATUS_STARTING_VALIDATION,
				new String[]{helper.getProject().getName(), vmd.getValidatorDisplayName()});
			
			monitor.subTask(message);
			
		
			status = validator.validateInJob(helper, reporter);
		
			//to remove, if error is returned from the validator, the job stays back in the UI...
			//should we even return error status if error is found in the validator
			//status = IValidatorJob.OK_STATUS;	
			
			
			
			message = ResourceHandler.getExternalizedMessage(
					ResourceConstants.VBF_STATUS_ENDING_VALIDATION,
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
			// error and continue
			// with the next validator.
			exc.setClassLoader(validator.getClass().getClassLoader());

			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidatorJob.run()"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
				if (exc.getAssociatedException() != null) {
					entry.setTargetException(exc.getAssociatedException());
					logger.write(Level.SEVERE, entry);
				}
			}
			String message = ResourceHandler.getExternalizedMessage(
						ResourceConstants.VBF_STATUS_ENDING_VALIDATION_ABNORMALLY,
						new String[]{helper.getProject().getName(), vmd.getValidatorDisplayName()});
			
			monitor.subTask(message);
			if (exc.getAssociatedMessage() != null) {
				reporter.addMessage(validator, exc.getAssociatedMessage());
			}
		} catch (Throwable exc) {
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidatorJob.run()"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
				IStatus stat = new Status(IStatus.ERROR,
			    		      ValidationPlugin.getPlugin().PLUGIN_ID, 0, "", exc );
					logger.write(Level.SEVERE, stat);
				
			}
			String mssg = ResourceHandler.getExternalizedMessage(
						ResourceConstants.VBF_STATUS_ENDING_VALIDATION_ABNORMALLY,
						new String[]{helper.getProject().getName(), vmd.getValidatorDisplayName() });
			
			monitor.subTask(mssg);
			
			String[] msgParm = {exc.getClass().getName(), vmd.getValidatorDisplayName(), (exc.getMessage() == null ? "" : exc.getMessage())}; //$NON-NLS-1$
			Message message = ValidationPlugin.getMessage();
			message.setSeverity(IMessage.NORMAL_SEVERITY);
			message.setId(ResourceConstants.VBF_EXC_RUNTIME);
			message.setParams(msgParm);
			reporter.addMessage(validator, message);
			
		} finally {
			try {
				validator.cleanup(reporter);
			} catch (OperationCanceledException e) {
				throw e;
			} catch (Throwable exc) {
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidatorJob.run()"); //$NON-NLS-1$
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
				String[] msgParm = {exc.getClass().getName(), vmd.getValidatorDisplayName(), (exc.getMessage() == null ? "" : exc.getMessage())}; //$NON-NLS-1$
				Message message = ValidationPlugin.getMessage();
				message.setSeverity(IMessage.NORMAL_SEVERITY);
				message.setId(ResourceConstants.VBF_EXC_RUNTIME);
				message.setParams(msgParm);
				reporter.addMessage(validator, message);

				
				status = WTPCommonPlugin.createErrorStatus(message.getText());
				return status;
			}
			try {
				helper.cleanup(reporter);
			}catch (OperationCanceledException e) {
				throw e;
			} catch (Throwable exc) {
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidatorJob.run()"); //$NON-NLS-1$
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
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
			}
			//reporter.getProgressMonitor().worked(((delta == null) ? 1 : delta.length)); // One
			//monitor.worked(((delta == null) ? 1 : delta.length)); // One
		}
		return status;
	}

	public boolean belongsTo(Object family) {
		return (project.getName() + ValidatorManager.VALIDATOR_JOB_FAMILY).equals(family);
	}	
}
