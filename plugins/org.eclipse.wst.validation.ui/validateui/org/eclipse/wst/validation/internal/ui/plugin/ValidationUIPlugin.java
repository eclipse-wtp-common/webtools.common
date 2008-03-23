/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.ui.plugin;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;
import org.eclipse.wst.validation.internal.operations.ValidationOperation;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;



public class ValidationUIPlugin extends WTPUIPlugin {
	private static ValidationUIPlugin _plugin = null;

	public final static String VALIDATION_PROP_FILE_NAME = "validate_ui"; //$NON-NLS-1$
	public static final String VALIDATION_PLUGIN_ID = "org.eclipse.wst.validation.ui"; //$NON-NLS-1$
	public static final String[] ICON_DIRS = new String[]{"icons"}; //$NON-NLS-1$

	public ValidationUIPlugin() {
		super();
		if (_plugin == null) {
			_plugin = this;
		}
	}

	public static String getBundleName() {
		return VALIDATION_PROP_FILE_NAME;
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
			ValidationUIPlugin.getPlugin().handleException(e);
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

//	public static IRunnableWithProgress getRunnableWithProgress(IHeadlessRunnableWithProgress aHeadlessRunnableWithProgress) {
//		return new RunnableWithProgressWrapper(aHeadlessRunnableWithProgress);
//	}

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
	
	/**
	 * Get a .gif from the image registry.
	 */
	public Image getImage(String key) {
		ImageRegistry imageRegistry = getImageRegistry();
		Image image = imageRegistry.get(key);
		if (image == null || image.isDisposed()) {
			ImageDescriptor descriptor = getImageDescriptor(key);
			if (descriptor != null) {
				image = descriptor.createImage();
				imageRegistry.put(key, image);
			}
		}
		return image;
	}
	
	/**
	 * This gets a .gif from the icons folder.
	 */
	public ImageDescriptor getImageDescriptor(String key) {
		ImageDescriptor imageDescriptor = null;
		URL gifImageURL = getImageURL(key);
		if (gifImageURL != null)
			imageDescriptor = ImageDescriptor.createFromURL(gifImageURL);
		return imageDescriptor;
	}
	
	/**
	 * @param key
	 * @return
	 */
	private URL getImageURL(String key) {
		return ValidationUIPlugin.getImageURL(key, getBundle());
	}
	
	/**
	 * This gets a .gif from the icons folder.
	 */
	public static URL getImageURL(String key, Bundle bundle) {
		String gif = "/" + key + ".gif"; //$NON-NLS-1$ //$NON-NLS-2$
		IPath path = null;
		for (int i = 0; i < ICON_DIRS.length; i++) {
			path = new Path(ICON_DIRS[i]).append(gif);
			if (Platform.find(bundle,path) == null)
				continue;
			try {
				return new URL( bundle.getEntry("/"), path.toString()); //$NON-NLS-1$ 
			} catch (MalformedURLException exception) {
				exception.printStackTrace();
				continue;
			}
		}
		return null;
	}
	
	
	public void handleException(Throwable e){
		Status status = new Status(IStatus.ERROR, PLUGIN_ID, e.getLocalizedMessage(), e);
		getLog().log(status);
	}
	
	/** 
	 * Write a message into the log. 
	 * 
	 * We are in the transition of moving to a new approach for localized messages. This is the new 
	 * approach for exceptions.
	 * 
	 * @param severity message severity, see IStaus
	 * @param message a localized message
	 */
	public void logMessage(int severity, String message){
		Status status = new Status(severity, PLUGIN_ID, message);
		getLog().log(status);
		
	}	

}
