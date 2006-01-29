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
package org.eclipse.wst.validation.internal;


import org.eclipse.wst.validation.internal.operations.IRuleGroup;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;


/**
 * Validation constants needed to declare an extension point, and to implement an extension.
 */
public interface RegistryConstants {
	public static final String PLUGIN_ID = ValidationPlugin.PLUGIN_ID;
	public static final String VALIDATOR_EXT_PT_ID = "validator"; //$NON-NLS-1$ // extension point declaration of the validator

	/* package */static final String TAG_RUN_CLASS = "run"; //$NON-NLS-1$ // identifies the Validator class
	/* package */static final String TAG_FILTER = "filter"; //$NON-NLS-1$ // identifies a filter -- type and/or name -- used to filter out resources which are not to be validated. (i.e., if the resource doesn't pass this filter test, don't validate it.)
	/* package */static final String TAG_HELPER_CLASS = "helper"; //$NON-NLS-1$ // IValidationContext which loads the MOF model for the IValidator
	/* package */static final String TAG_PROJECT_NATURE = "projectNature"; //$NON-NLS-1$ // identifies the projects which the validator should run on
	/* package */static final String TAG_AGGREGATE_VALIDATORS = "aggregateValidator"; //$NON-NLS-1$ // identifies a validator(s) which this validator aggregates. This value is used to remove all messages owned by a particular validator. Aggregate validators cannot be shared. Only one validator may use an aggregate of that type.

	/* package */static final String ATT_OBJECT_CLASS = "objectClass"; //$NON-NLS-1$ // identifies a type
	/* package */static final String ATT_NAME_FILTER = "nameFilter"; //$NON-NLS-1$ // identifies a name (may include the '*' wildcard anywhere in the name)
	/* package */static final String ATT_ID = "id"; //$NON-NLS-1$ // identifies a unique id to filter on
	/* package */static final String ATT_CLASS = "class"; //$NON-NLS-1$ // identifies a class name of a tag, e.g. "helper class", or "run class"
	/* package */static final String ATT_ACTION_FILTER = "action"; //$NON-NLS-1$ // identifies the incremental validation actions for which a resource should be filtered in
	/* package */static final String ATT_INCREMENTAL = "incremental"; //$NON-NLS-1$ // identifies whether or not the validator supports incremental build validation. Default is true (i.e., incremental builds are supported).
	/* package */static final boolean ATT_INCREMENTAL_DEFAULT = true; // The incremental default.
	/* package */static final String ATT_FULLBUILD = "fullBuild"; //$NON-NLS-1$ // identifies whether or not the validator supports full build validation. Default is true (i.e., full build validation is supported).
	/* package */static final boolean ATT_FULLBUILD_DEFAULT = true; // The build default.
	/* package */static final String ATT_ENABLED = "enabled"; //$NON-NLS-1$ // identifies whether or not the validator is enabled by default. Default is "true" (enabled).
	/* package */static final boolean ATT_ENABLED_DEFAULT = true; // The "enabled" default.
	/* package */static final String ATT_INCLUDE = "include"; //$NON-NLS-1$ // Include projects with this nature
	/* package */static final boolean ATT_INCLUDE_DEFAULT = true; // By default, if "include" is not
	// specified in the projectNature
	// element, then assume that the
	// nature is included.
	/* package */static final String ATT_RULE_GROUP = "ruleGroup"; //$NON-NLS-1$ // identifies the different validation passes which this validator recognizes. The values are identified in IRuleGroup.
	/* package */static final int ATT_RULE_GROUP_DEFAULT = IRuleGroup.PASS_FAST; // the default pass
	// includes only the
	// FAST PASS. FULL
	// can be invoked
	// only explicitly by
	// an operation.
	/* package */static final String ATT_CASE_SENSITIVE = "caseSensitive"; //$NON-NLS-1$
	/* package */static final String ATT_ASYNC = "async"; //$NON-NLS-1$ // Is the validator thread-safe? 
	/* package */static final boolean ATT_ASYNC_DEFAULT = false; // The "can validator run
	// asynchronously" default. Initially
	// set to false, but in future this
	// will be changed to true.


	/* package */static final String TAG_MIGRATE = "migrate"; //$NON-NLS-1$ // the "migrate" section of the validator
	/* package */static final String TAG_VALIDATOR = "validator"; //$NON-NLS-1$ // the "validator" element of the "migrate" section
	/* package */static final String ATT_FROM = "from"; //$NON-NLS-1$ // the fully-qualified class name of the former validator class
	/* package */static final String ATT_TO = "to"; //$NON-NLS-1$ // the fully-qualified class name of the current validator class
	static final String DEP_VALIDATOR = "dependentValidator"; //$NON-NLS-1$
	static final String DEP_VAL_VALUE = "depValValue"; //$NON-NLS-1$
	static final String MARKER_ID = "markerId"; //$NON-NLS-1$
	static final String MARKER_ID_VALUE = "markerIdValue"; //$NON-NLS-1$
	static final boolean DEP_VAL_VALUE_DEFAULT = false;
	static final String FACET = "facet"; //$NON-NLS-1$
	static final String FACET_ID ="facetId"; //$NON-NLS-1$
}