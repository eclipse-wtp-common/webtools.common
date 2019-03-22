/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * Validation constants needed to implement the marker extension point and the user's preferences.
 * 
 * Only the validation framework should use this interface.
 */
public interface ConfigurationConstants {
	String DISABLE_ALL_VALIDATION_SETTING = "disableAllValidation"; //$NON-NLS-1$ // boolean
	String SAVE_AUTOMATICALLY_SETTING = "saveAutomatically"; //$NON-NLS-1$ // boolean

	// Defaults for the preference and project values
	
	String CURRENT_VERSION = (String) ValidationPlugin.getPlugin().getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);  // this is a constant, so it should be intern
	boolean DEFAULT_ASYNC = true;
	boolean DEFAULT_DISABLE_VALIDATION_SETTING = false;
	ValidatorMetaData[] DEFAULT_ENABLED_VALIDATORS = ValidationRegistryReader.getReader().getValidatorMetaDataArrayEnabledByDefault();  // store a copy here so that we don't modify the original and don't create a copy every time we need this value
	int DEFAULT_MAXNUMMESSAGES = 50;
	int DEPTH_INFINITE = IResource.DEPTH_INFINITE;
	int DEPTH_ZERO = IResource.DEPTH_ZERO;
	String ELEMENT_SEPARATOR = ";"; //$NON-NLS-1$ // separates the name of one IValidator from the next in the list of enabled validators for a project or preference
	String DELEGATES_SEPARATOR = "="; //$NON-NLS-1$ // Separates the delegating validator id from the delegate validator id in the list of delegates

	// The following values must match the attributes in the preference marker as shown in plugin.xml
	// Even though the plugin.xml values are not used to create new Preference or Project markers,
	// maintaining one local name ensures that there's no confusion writing the migration code.
	// These are the QualifiedNames used to persist the user's settings.
	String ENABLED_MANUAL_VALIDATORS = "enabledManualValidatorList"; //$NON-NLS-1$ // String
	String ENABLED_BUILD_VALIDATORS = "enabledBuildValidatorList"; //$NON-NLS-1$ // String
	String DELEGATE_VALIDATORS = "delegateValidatorList"; //$NON-NLS-1$ // String
	String J2EE_PLUGIN_ID = "org.eclipse.jst.j2ee"; //$NON-NLS-1$ // For 4.03, this is the plugin id that the validation constants were declared in.
	String MAXNUMMESSAGES = "maxNumMessages"; //$NON-NLS-1$ // integer
	String PLUGIN_ID = ValidationPlugin.PLUGIN_ID;
	String PREF_PROJECTS_CAN_OVERRIDE = "projectsCanOverride"; //$NON-NLS-1$ // boolean
	// end validation message marker constants

	// Preference and Project constants
	String PREFERENCE_MARKER = PLUGIN_ID + ".preferencemarker"; //$NON-NLS-1$
	String PRJ_MARKER = PLUGIN_ID + ".projectmarker"; //$NON-NLS-1$ // The extension which saves user validation preferences for a particular project (e.g. which validators run on the project)
	String PRJ_MARKER_403 = J2EE_PLUGIN_ID + ".projectmarker"; //$NON-NLS-1$ // The extension which saves user validation preferences for a particular project (e.g. which validators run on the project)
	String PRJ_OVERRIDEGLOBAL = "overrideGlobalPreferences"; //$NON-NLS-1$ // boolean (Use the global preferences or override)

	QualifiedName USER_PREFERENCE = new QualifiedName(PLUGIN_ID, "ValidationConfiguration"); //$NON-NLS-1$ // ValidationConfiguration for the IResource
	QualifiedName USER_MANUAL_PREFERENCE = new QualifiedName(PLUGIN_ID, "ValidationManualConfiguration"); //$NON-NLS-1$ // ValidationConfiguration for the IResource
	QualifiedName USER_BUILD_PREFERENCE = new QualifiedName(PLUGIN_ID, "ValidationBuildConfiguration"); //$NON-NLS-1$ // ValidationConfiguration for the IResource
	QualifiedName DELEGATES_PREFERENCE = new QualifiedName(PLUGIN_ID, "ValidationDelegatesConfiguration"); //$NON-NLS-1$ // ValidationConfiguration for the IResource

	// Validation message marker constants
	
	/** 
	 * org.eclipse.wst.validation.problemmarker - The marker id which is used to add v1 validation markers 
	 * to the task list */
	String VALIDATION_MARKER = PLUGIN_ID + ".problemmarker"; //$NON-NLS-1$ 
	
	/** For incremental validation, this field associates a message with a group, so that a subset of messages may be removed from a file. */
	String VALIDATION_MARKER_GROUP = "groupName"; //$NON-NLS-1$
	
	/**  Persist the message id of the message, not just the translated text. */
	String VALIDATION_MARKER_MESSAGEID = "messageId"; //$NON-NLS-1$
	
	// The following values must match the attributes in the validation message marker as shown in plugin.xml
	String VALIDATION_MARKER_OWNER = "owner"; //$NON-NLS-1$ // The IValidator who owns the IMarker on the task list
	String VALIDATION_MARKER_SEVERITY = "validationSeverity"; //$NON-NLS-1$ // one of the SeverityEnum values
	String VALIDATION_MARKER_TARGETOBJECT = "targetObject"; //$NON-NLS-1$ // When more than one target object resolves to the same IResource, this field identifies which targetObject owns a particular message.
	String VERSION = "version"; //$NON-NLS-1$

	// WSAD versions which created markers of these types
	String VERSION4_03 = "4.03"; //$NON-NLS-1$
	String VERSION5_0 = "5.0"; //$NON-NLS-1$
	String VERSION5_01 = "5.0.1"; //$NON-NLS-1$
	// end preference and project defaults
}
