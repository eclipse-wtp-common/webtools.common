/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.integration;

import java.io.FileNotFoundException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.wst.common.emf.utilities.ExtendedEcoreUtil;
import org.eclipse.wst.common.internal.emfworkbench.EMFAdapterFactory;
import org.eclipse.wst.common.internal.emfworkbench.PassthruResourceSet;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;

import com.ibm.wtp.emf.workbench.WorkbenchResourceHelperBase;

/**
 * The main plugin class to be used in the desktop.
 */
public class EMFWorkbenchEditPlugin extends Plugin {
	public static final String ID = "org.eclipse.wst.common.emfworkbench.integration"; //$NON-NLS-1$

	public static final String EDIT_MODEL_FACTORIES_EXTENSION_POINT = "editModel"; //$NON-NLS-1$
	public static final String EDIT_MODEL_EXTENSION_REGISTRY_EXTENSION_POINT = "editModelExtension"; //$NON-NLS-1$
	public static final String ADAPTER_FACTORY_REGISTRY_EXTENSION_POINT = "adapterFactory"; //$NON-NLS-1$


	//The shared instance.
	private static EMFWorkbenchEditPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public EMFWorkbenchEditPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.eclipse.wst.common.internal.emfworkbench.integration.EMFWorkbenchEditPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static EMFWorkbenchEditPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = EMFWorkbenchEditPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#startup()
	 */
	public void startup() throws CoreException {
		super.startup();
		ExtendedEcoreUtil.setFileNotFoundDetector(new ExtendedEcoreUtil.FileNotFoundDetector() {
			public boolean isFileNotFound(WrappedException wrappedEx) {
				return WorkbenchResourceHelperBase.isResourceNotFound(wrappedEx) || wrappedEx.exception() instanceof FileNotFoundException;
			}
		});
		WorkbenchResourceHelper.initializeFileAdapterFactory();

		IAdapterManager manager = Platform.getAdapterManager();
		manager.registerAdapters(new EMFAdapterFactory(), EObject.class);
	}

	public static ResourceSet createIsolatedResourceSet(IProject project) {
		return new PassthruResourceSet(project);
	}

	public static ResourceSet createWorkspacePassthruResourceSet() {
		return new PassthruResourceSet();
	}


}