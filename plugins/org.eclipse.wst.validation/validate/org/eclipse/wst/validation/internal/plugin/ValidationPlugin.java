/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.plugin;

import java.util.Locale;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.wst.common.frameworks.internal.WTPPlugin;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.DependencyIndex;
import org.eclipse.wst.validation.internal.EventManager;
import org.eclipse.wst.validation.internal.TimeEntry;
import org.eclipse.wst.validation.internal.ValOperationManager;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The plug-in's activator class.
 *
 */
public class ValidationPlugin extends WTPPlugin {
	public static final String 	VALIDATION_PROP_FILE_NAME = "validate_base"; //$NON-NLS-1$
	
	/** org.eclipse.wst.validation - the plug-in id */
	public static final String 	PLUGIN_ID = "org.eclipse.wst.validation"; //$NON-NLS-1$
	private static ValidationPlugin _plugin;
	private static TimeEntry 	_tEntry;
	private static LogEntry 	_entry;
	private static Message 		_message;
	public static final String VALIDATION_BUILDER_ID = PLUGIN_ID + ".validationbuilder"; //$NON-NLS-1$// plugin id of the validation builder
	public static final String VALIDATOR_EXT_PT_ID = "validator"; //$NON-NLS-1$// extension point declaration of the validator 

	public ValidationPlugin() {
		super();
		if (_plugin == null)_plugin = this;
	}
	
	/**
	 * Answer the name of the bundle's property file.
	 * 
	 * @deprecated Use getBundlePropertyFileName() instead.
	 */
	public static String getBundleName() {
		return getBundlePropertyFileName();
	}

	/**
	 * Answer the name of the bundle's property file.
	 */
	public static String getBundlePropertyFileName() {
		return VALIDATION_PROP_FILE_NAME;
	}

	public static LogEntry getLogEntry() {
		if (_entry == null)_entry = new LogEntry(VALIDATION_PROP_FILE_NAME);
		else _entry.reset();
		
		// Always set the log entry's Locale before you use it
		// because the user can reset it on the fly.
		_entry.setLocaleOfOrigin(Locale.getDefault().toString());
		return _entry;
	}

	public static TimeEntry getTimeEntry() {
		if (_tEntry == null)_tEntry = new TimeEntry();
		_tEntry.reset();
		return _tEntry;
	}

	public static Message getMessage() {
		if (_message == null) {
			_message = new Message();
			_message.setBundleName(getBundlePropertyFileName());
		}
		// clear the message for reuse
		_message.setId(null);
		_message.setParams(null);
		_message.setTargetObject(null);
		_message.setGroupName(null);
		_message.setSeverity(IMessage.LOW_SEVERITY);
		return _message;
	}

	public static ValidationPlugin getPlugin() {
		return _plugin;
	}

	public static boolean isActivated() {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		if (bundle != null)
			return bundle.getState() == Bundle.ACTIVE;
		return false;
	}

	/**
	 * @see Plugin#startup()
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(EventManager.getManager(), IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.PRE_BUILD | IResourceChangeEvent.POST_CHANGE);

		DependencyIndex di = (DependencyIndex)ValidationFramework.getDefault().getDependencyIndex();
		IWorkspace ws = ResourcesPlugin.getWorkspace();
		ws.addSaveParticipant(this, di);
		ws.addResourceChangeListener(ValOperationManager.getDefault(), 
			IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.PRE_BUILD);

	}

	/**
	 * @see org.eclipse.core.runtime.Plugin#stop(BundleContext context)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener( EventManager.getManager() );		
		ResourcesPlugin.getWorkspace().removeResourceChangeListener( ValOperationManager.getDefault() );		
		EventManager.getManager().shutdown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.WTPPlugin#getPluginID()
	 */
	public String getPluginID() {
		return PLUGIN_ID;
	}
	
	/**
	 * Write this exception to the log.
	 * 
	 * We are in the transition of moving to a new approach for localized messages. This is the new 
	 * approach for exceptions.
	 */
	public void handleException(Exception e){
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
