/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;


import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.validation.core.Message;
import org.eclipse.wst.validation.core.SeverityEnum;
import org.eclipse.wst.validation.internal.operations.WorkbenchReporter;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

import com.ibm.wtp.common.logger.LogEntry;
import com.ibm.wtp.common.logger.proxy.Logger;

/**
 */
public final class InternalValidatorManager {
	private static InternalValidatorManager _inst = null;
	private static final String OP_GROUP = "ValidationOperation"; //$NON-NLS-1$ // when the ValidationOperation

	// adds a message to the task
	// list; e.g. cancel, or internal
	// error, this group name is used
	// to distinguish between the
	// messages that the validator
	// itself put, and the ones which
	// the validator owns, but the
	// operation put. //$NON-NLS-1$

	private InternalValidatorManager() {
		//default
	}

	public static InternalValidatorManager getManager() {
		if (_inst == null) {
			_inst = new InternalValidatorManager();
		}
		return _inst;
	}

	/**
	 * Return a new Set that contains all of the elements from the array.
	 */
	public static Set wrapInSet(Object[] obj) {
		Set result = new HashSet();
		if ((obj == null) || (obj.length == 0)) {
			return result;
		}

		for (int i = 0; i < obj.length; i++) {
			result.add(obj[i]);
		}

		return result;
	}

	/**
	 * If the current validator throws a Throwable, log the internal error to the task list.
	 * 
	 * This method is for use by the validation framework only.
	 */
	public void addInternalErrorTask(IProject project, ValidatorMetaData vmd, Throwable exc) {
		addOperationTask(project, vmd, ResourceConstants.VBF_EXC_INTERNAL, new String[]{project.getName(), vmd.getValidatorDisplayName(), ((exc.getMessage() == null) ? "" : exc.getMessage())}); //$NON-NLS-1$
	}

	/**
	 * If the user is cancelling validation on the current project/resource, Add an information task
	 * to the task list informing the user that validation has not been run on the current project.
	 * 
	 * If the current validator throws a Throwable, log the internal error to the task list.
	 */
	public void addOperationTask(IProject project, ValidatorMetaData vmd, String messageId, String[] parms) {
		Message message = ValidationPlugin.getMessage();
		message.setSeverity(SeverityEnum.LOW_SEVERITY);
		message.setId(messageId);
		message.setParams(parms);
		message.setGroupName(OP_GROUP);

		// Although the message is owned by the validator, the string of the message has to be
		// loaded by this class' ClassLoader
		WorkbenchReporter.addMessage(project, vmd.getValidatorUniqueName(), getClass().getClassLoader(), message);
	}


	/**
	 * If the user cancelled the previous validation with this validator, or if there was a
	 * Throwable caught during the last execution of this validator, and the validator is in the
	 * process of validating now, remove the former information task messages.
	 */
	public void removeOperationTasks(IProject project, ValidatorMetaData vmd) {
		WorkbenchReporter.removeMessageSubset(project, vmd.getValidatorUniqueName(), OP_GROUP);
	}

	/**
	 * Return an array of the fully-qualified names of the validator classes.
	 */
	public String[] getValidatorNames(ValidatorMetaData[] vmds) {
		Set temp = new HashSet();
		for (int i = 0; i < vmds.length; i++) {
			String[] names = vmds[i].getValidatorNames();
			for (int j = 0; j < names.length; j++) {
				temp.add(names[j]);
			}
		}

		String[] vmdNames = new String[temp.size()];
		temp.toArray(vmdNames);
		return vmdNames;
	}

	/**
	 * Return an array of the fully-qualified names of the validator classes.
	 */
	public String[] getValidatorNames(Collection vmds) {
		Set temp = new HashSet();
		Iterator iterator = vmds.iterator();
		while (iterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
			String[] names = vmd.getValidatorNames();
			for (int j = 0; j < names.length; j++) {
				temp.add(names[j]);
			}
		}

		String[] vmdNames = new String[temp.size()];
		temp.toArray(vmdNames);
		return vmdNames;
	}

	/**
	 * Return a list of validators that validate files with the given extension.
	 */
	public ValidatorMetaData[] getValidatorsForExtension(IProject project, String fileExtension) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);

			// Get all of the validators configured on the project for the given file extension
			ValidatorMetaData[] vmds = prjp.getValidators();

			// Construct a fake IFile type to represent a file with this extension.
			StringBuffer buffer = new StringBuffer(project.getName());
			buffer.append(IPath.SEPARATOR);
			buffer.append(fileExtension);
			IPath path = new Path(buffer.toString());
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

			ValidatorMetaData[] temp = new ValidatorMetaData[vmds.length];
			int count = 0;
			for (int i = 0; i < vmds.length; i++) {
				ValidatorMetaData vmd = vmds[i];
				if (vmd.isApplicableTo(file)) {
					temp[count++] = vmd;
				}
			}

			ValidatorMetaData[] result = new ValidatorMetaData[count];
			System.arraycopy(temp, 0, result, 0, count);
			return result;
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("InternalValidatorManager::getValidatorsForExtension(" + project.getName() + ", " + fileExtension + ")"); //$NON-NLS-1$  //$NON-NLS-2$ //$NON-NLS-3$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
			return new ValidatorMetaData[0];
		}
	}

	/**
	 * Return a list of validator names that validate files with the given extension.
	 */
	public String[] getValidatorNamesForExtension(IProject project, String fileExtension) {
		ValidatorMetaData[] vmds = getValidatorsForExtension(project, fileExtension);

		String[] names = new String[vmds.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = vmds[i].getValidatorUniqueName();
		}
		return names;
	}
}