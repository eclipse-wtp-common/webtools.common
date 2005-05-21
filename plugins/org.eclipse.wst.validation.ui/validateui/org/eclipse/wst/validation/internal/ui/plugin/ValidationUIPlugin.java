/***************************************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.validation.internal.ui.plugin;

import java.util.logging.Level;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.wst.common.frameworks.internal.operations.IHeadlessRunnableWithProgress;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;
import org.eclipse.wst.validation.internal.operations.ValidationOperation;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.osgi.framework.BundleContext;



public class ValidationUIPlugin extends WTPUIPlugin {
	private static ValidationUIPlugin _plugin = null;

	public final static String VALIDATION_PROP_FILE_NAME = "validate_ui"; //$NON-NLS-1$
	public static final String VALIDATION_PLUGIN_ID = "org.eclipse.wst.validation.ui"; //$NON-NLS-1$

	public ValidationUIPlugin() {
		super();
		if (_plugin == null) {
			_plugin = this;
		}
	}

	public static String getBundleName() {
		return VALIDATION_PROP_FILE_NAME;
	}

	public static LogEntry getLogEntry() {
		return ValidationPlugin.getLogEntry();
	}


	public static ValidationUIPlugin getPlugin() {
		return _plugin;
	}

	/**
	 * Returns the translated String found with the given key.
	 * 
	 * @param key
	 *            java.lang.String
	 * @return java.lang.String
	 */
	public static String getResourceString(String key) {
		try {
			return Platform.getResourceString(Platform.getBundle(VALIDATION_PLUGIN_ID), key);
		} catch (Exception e) {
			e.printStackTrace();
			Logger logger = WTPUIPlugin.getLogger();
			if (logger.isLoggingLevel(Level.FINE)) {
				LogEntry entry = getLogEntry();
				entry.setSourceID("ValidationUIPlugin.getResourceString(String)"); //$NON-NLS-1$
				entry.setText("Missing resource for key" + key); //$NON-NLS-1$
				logger.write(Level.FINE, entry);
			}

			return key;
		}
	}

	/**
	 * This method should be called whenever you need to run one of our headless operations in the
	 * UI.
	 */
	public static IRunnableWithProgress getRunnableWithProgress(IWorkspaceRunnable aWorkspaceRunnable) {
		return new RunnableWithProgressWrapper(aWorkspaceRunnable);
	}

	public static IRunnableWithProgress getRunnableWithProgress(IHeadlessRunnableWithProgress aHeadlessRunnableWithProgress) {
		return new RunnableWithProgressWrapper(aHeadlessRunnableWithProgress);
	}

	// Need a third, ValidationOperation version of this method, because ValidationOperation
	// is both an IWorkspaceRunnable and an IHeadlessRunnableWithProgress. This method will
	// exist only while IHeadlessRunnableWithProgress exists.
	public static IRunnableWithProgress getRunnableWithProgress(ValidationOperation op) {
		return new RunnableWithProgressWrapper(op);
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		//	org.eclipse.wst.validation.internal.operations.ValidatorManager.setResourceUtilClass(org.eclipse.wst.validation.internal.operations.ui.UIResourceUtil.class);
	}
}