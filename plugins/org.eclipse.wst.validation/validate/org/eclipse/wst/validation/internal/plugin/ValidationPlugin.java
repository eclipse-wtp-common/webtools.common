/***************************************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.validation.internal.plugin;

import java.util.Locale;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.wst.common.frameworks.internal.WTPPlugin;
import org.eclipse.wst.validation.internal.EventManager;
import org.eclipse.wst.validation.internal.TimeEntry;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class ValidationPlugin extends WTPPlugin {
	public static final String VALIDATION_PROP_FILE_NAME = "validate_base"; //$NON-NLS-1$
	public static final String PLUGIN_ID = "org.eclipse.wst.validation"; //$NON-NLS-1$
	private static ValidationPlugin _plugin = null;
	private static TimeEntry _tEntry = null;
	private static LogEntry _entry = null;
	private static Message _message = null;
	public static final String VALIDATION_BUILDER_ID = PLUGIN_ID + ".validationbuilder"; //$NON-NLS-1$// plugin id of the validation builder
	public static final String VALIDATOR_EXT_PT_ID = "validator"; //$NON-NLS-1$// extension point declaration of the validator 

	/**
	 * ValidationPlugin constructor comment.
	 * 
	 * @param descriptor
	 *            org.eclipse.core.runtime.IPluginDescriptor
	 */
	public ValidationPlugin() {
		super();
		if (_plugin == null) {
			_plugin = this;
			//Commenting off the following lines the Plugin is not activated and the
			//getMsgLogger tries to access the Plugin and the plugin is in a active pending state.
			//getMsgLogger();
			//logger.log(Level.CONFIG, null);

		}
	}

	public static String getBundleName() {
		return VALIDATION_PROP_FILE_NAME;
	}

	public static LogEntry getLogEntry() {
		if (_entry == null) {
			_entry = new LogEntry(VALIDATION_PROP_FILE_NAME);
		} else {
			_entry.reset();
		}
		// Always set the log entry's Locale before you use it
		// because the user can reset it on the fly.
		_entry.setLocaleOfOrigin(Locale.getDefault().toString());
		return _entry;
	}

	public static TimeEntry getTimeEntry() {
		if (_tEntry == null) {
			_tEntry = new TimeEntry();
		}
		_tEntry.reset();
		return _tEntry;
	}

	public static Message getMessage() {
		if (_message == null) {
			_message = new Message();
			_message.setBundleName(getBundleName());
		}
		// clear the message for reuse
		_message.setId(null);
		_message.setParams(null);
		_message.setTargetObject(null);
		_message.setGroupName(null);
		_message.setSeverity(IMessage.LOW_SEVERITY);
		return _message;
	}

	/**
	 * Retrieves a hashtable of a logger's preferences initially from the
	 * com.ibm.etools.logging.util.loggingDefaults extension point if specified in the
	 * com.ibm.etools.logging.util plugin.xml file. If specified, the
	 * com.ibm.etools.logging.util.loggingOptions extension point preferences in the parameter
	 * plugin's plugin.xml file are returned.
	 * 
	 * The logger's preferences are stored in the return hashtable using the static instance
	 * variables in LoggerStateHashKeys as keys.
	 * 
	 * @param plugin
	 *            the Plugin polled for their logger's preferences in the plugin.xml file
	 * @return hashtable of a logger's preferences
	 */
	/*
	 * public Hashtable getMsgLoggerConfig(Plugin plugin) { return (new
	 * PluginHelperImpl().getMsgLoggerConfig(plugin));
	 */
	public static ValidationPlugin getPlugin() {
		return _plugin;
	}

	/**
	 * Sets the logger's preferences based on values in the parameter hashtable.
	 * 
	 * The logger's preferences are stored in the parameter hashtable using the static instance
	 * variables in LoggerStateHashKeys as keys.
	 * 
	 * @param msgLoggerConfig
	 *            hashtable of the logger's preferences
	 */
	/*
	 * public void setMsgLoggerConfig(Hashtable msgLoggerConfig) {
	 * getMsgLogger().setMsgLoggerConfig(msgLoggerConfig); }
	 */
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
	}

	/**
	 * @see org.eclipse.core.runtime.Plugin#shutdown()
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
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
	
	
}