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
/*
 * Created on Jun 9, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.internal.emf.plugin;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.common.internal.emf.ResourceSynchronizedIsLoadingAdapterFactory;
import org.eclipse.wst.common.internal.emf.resource.RendererFactory;
import org.eclipse.wst.common.internal.emf.utilities.ResourceIsLoadingAdapterFactory;
import org.osgi.framework.BundleContext;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import java.lang.Throwable;
import org.eclipse.core.runtime.CoreException;

/**
 * @author DABERG
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class EcoreUtilitiesPlugin extends Plugin {
	//the ID for this plugin (added automatically by logging quickfix)
	public static final String PLUGIN_ID = "org.eclipse.wst.common.emf"; //$NON-NLS-1$
	public static final String ID = "org.eclipse.wst.common.emf"; //$NON-NLS-1$
	public static final String TRANSLATOR_EXTENSTION_POINT = "translatorExtension"; //$NON-NLS-1$

	/**
	 * @param descriptor
	 */
	public EcoreUtilitiesPlugin() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#startup()
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		RendererFactory.setDefaultHandler(PluginRendererFactoryDefaultHandler.INSTANCE);
		PackageURIMapReader reader = new PackageURIMapReader();
		reader.processExtensions();
		//use a synchronized loading adapter factory
		ResourceIsLoadingAdapterFactory.INSTANCE = new ResourceSynchronizedIsLoadingAdapterFactory();
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

	public static void logError(String message) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( createStatus(IStatus.ERROR, message));
	}

	public static void logWarning(String message) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log(createStatus(IStatus.WARNING, message));
	}
	
	public static void logWarning(Throwable exception) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( createStatus(IStatus.WARNING, exception.getMessage(), exception));
	}

}
