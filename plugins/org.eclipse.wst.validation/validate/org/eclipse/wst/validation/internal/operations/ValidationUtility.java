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
package org.eclipse.wst.validation.internal.operations;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.validation.internal.ConfigurationConstants;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.TaskListUtility;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;

/**
 * This class contains utility methods that are intended to be used by validators and code outside
 * of the validation framework.
 */
public final class ValidationUtility {
	//TODO Make the ValidationUtility constructor private in Milestone 4.

	/**
	 * Given an IResource and one of the org.eclipse.wst.validation.internal.core.core.SevertyEnum constants,
	 * return an array containing the fully-qualified class names of the validators which have
	 * reported messages, of the given severity, against the resource and the resource's children.
	 */
	public static String[] listValidatorClasses(IResource resource, int severity) {
		IMarker[] markers = TaskListUtility.getValidationTasks(resource, severity);
		if ((markers == null) || (markers.length == 0)) {
			return new String[0];
		}

		Set tempSet = new HashSet();
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			try {
				Object owner = marker.getAttribute(ConfigurationConstants.VALIDATION_MARKER_OWNER);
				if ((owner == null) || !(owner instanceof String)) {
					// The ValidationMigrator will remove any "unowned" validation markers.
					continue;
				}
				tempSet.add(owner);
			} catch (CoreException exc) {
				Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidationUtility.listValidatorClasses(" + resource.getName() + ", " + severity); //$NON-NLS-1$  //$NON-NLS-2$
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
		}


		if (tempSet.size() > 0) {
			String[] result = new String[tempSet.size()];
			tempSet.toArray(result);
			tempSet.clear();
			return result;
		}
		return new String[0];
	}

	/**
	 * @deprecated This instance method will be made static in Milestone 4.
	 */
	public boolean isEnabled(IProject project, String validatorClassName) {
		return isEnabled(project, new String[]{validatorClassName});
	}

	/**
	 * Return true if all of the validators, identified by their fully-qualified class names, are
	 * enabled on the project. If a validator isn't enabled, or if there's an internal error while
	 * retrieving the user's configuration, return false.
	 */
	public static boolean isEnabled(IProject project, String[] validatorClassNames) {
		if ((validatorClassNames == null) || (validatorClassNames.length == 0)) {
			return false;
		}

		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			for (int i = 0; i < validatorClassNames.length; i++) {
				if (!prjp.isEnabled(validatorClassNames[i])) {
					return false;
				}
			}
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationUtility::isEnabled(" + project.getName() + ", String[])"); //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
			return false;
		}

		return true;
	}
}