/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.internal.emf.utilities.ExtendedEcoreUtil;
import org.eclipse.wst.common.internal.emfworkbench.EMFAdapterFactory;
import org.eclipse.wst.common.internal.emfworkbench.PassthruResourceSet;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.osgi.framework.BundleContext;

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
	public void start(BundleContext context) throws Exception {
		super.start(context);
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
