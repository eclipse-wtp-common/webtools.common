/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.integration;

import java.io.FileNotFoundException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.internal.emf.utilities.ExtendedEcoreUtil;
import org.eclipse.wst.common.internal.emfworkbench.PassthruResourceSet;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.osgi.framework.BundleContext;
import java.lang.Throwable;

/**
 * The main plugin class to be used in the desktop.
 */
public class EMFWorkbenchEditPlugin extends Plugin {
	//the ID for this plugin (added automatically by logging quickfix)
	public static final String PLUGIN_ID = "org.eclipse.wst.common.emfworkbench.integration"; //$NON-NLS-1$

	public static final String ID = "org.eclipse.wst.common.emfworkbench.integration"; //$NON-NLS-1$

	public static final String EDIT_MODEL_FACTORIES_EXTENSION_POINT = "editModel"; //$NON-NLS-1$
	public static final String EDIT_MODEL_EXTENSION_REGISTRY_EXTENSION_POINT = "editModelExtension"; //$NON-NLS-1$
	public static final String ADAPTER_FACTORY_REGISTRY_EXTENSION_POINT = "adapterFactory"; //$NON-NLS-1$


	//The shared instance.
	private static EMFWorkbenchEditPlugin plugin; 

	/**
	 * The constructor.
	 */
	public EMFWorkbenchEditPlugin() {
		super();
		plugin = this; 
	}

	/**
	 * Returns the shared instance.
	 */
	public static EMFWorkbenchEditPlugin getDefault() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#startup()
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ExtendedEcoreUtil.setFileNotFoundDetector(new ExtendedEcoreUtil.FileNotFoundDetector() {
			public boolean isFileNotFound(WrappedException wrappedEx) {
				return WorkbenchResourceHelperBase.isResourceNotFound(wrappedEx) || wrappedEx.exception() instanceof FileNotFoundException;
			}
		});
		WorkbenchResourceHelper.initializeFileAdapterFactory();
	}

	public static ResourceSet createIsolatedResourceSet(IProject project) {
		return new PassthruResourceSet(project);
	}

	public static ResourceSet createWorkspacePassthruResourceSet() {
		return new PassthruResourceSet();
	}

	public static IStatus createStatus(int severity, String message, Throwable exception) {
		return new Status(severity, PLUGIN_ID, message, exception);
	}

	public static IStatus createStatus(int severity, String message) {
		return createStatus(severity, message, null);
	}

	public static void logError(Throwable exception) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( createStatus(IStatus.ERROR, exception.getMessage(), exception));
	}

	public static void logError(CoreException exception) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( exception.getStatus() );
	}

	public static void logWarning(String message) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log(createStatus(IStatus.WARNING, message));
	}

	public static void logWarning(Throwable exception) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( createStatus(IStatus.WARNING, exception.getMessage(), exception));
	}

	public static void logError(String message) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( createStatus(IStatus.ERROR, message));
	}


}
