/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
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
	String PLUGIN_ID = ValidationPlugin.PLUGIN_ID;
	
	/** validator - extension point declaration of the validator */
	String VALIDATOR_EXT_PT_ID = "validator"; //$NON-NLS-1$

	/** run - identifies the Validator class */
	String TAG_RUN_CLASS = "run"; //$NON-NLS-1$
	
	/** 
	 * filter - identifies a filter -- type and/or name -- used to filter out resources which are
	 * not to be validated. (i.e., if the resource doesn't pass this filter test, don't validate it.) 
	 */ 
	String TAG_FILTER = "filter"; //$NON-NLS-1$
	
	/**
	 * identifies a filter -- type and/or name -- used to filter out resources which are not to be validated. (i.e., if the resource doesn't pass this filter test, don't validate it.)
	 */
	String TAG_CONTENTTYPE = "contentTypeBinding"; //$NON-NLS-1$ 
	
	/**
	 *  identifies a type
	 */
	String ATT_CONTENTTYPEID = "contentTypeId"; //$NON-NLS-1$ 
	/** helper - IValidationContext which loads the MOF model for the IValidator */ 
	String TAG_HELPER_CLASS = "helper"; //$NON-NLS-1$
	
	/** projectNature - identifies the projects which the validator should run on */
	String TAG_PROJECT_NATURE = "projectNature"; //$NON-NLS-1$
	
	/** 
	 * aggregateValidator - identifies a validator(s) which this validator aggregates. This value is used 
	 * to remove all messages owned by a particular validator. Aggregate validators cannot be shared. 
	 * only one validator may use an aggregate of that type. */
	String TAG_AGGREGATE_VALIDATORS = "aggregateValidator"; //$NON-NLS-1$ 

	/** runStrategy - identifies the run strategy of  Validator*/
	String TAG_RUN_STRATEGY = "runStrategy"; //$NON-NLS-1$
	
	/** objectClass - identifies a type */ 
	String ATT_OBJECT_CLASS = "objectClass"; //$NON-NLS-1$
	
	/** nameFilter - identifies a name (may include the '*' wildcard anywhere in the name) */
	String ATT_NAME_FILTER = "nameFilter"; //$NON-NLS-1$
	
	/** id - identifies a unique id to filter on */
	String ATT_ID = "id"; //$NON-NLS-1$
	
	/** class - identifies a class name of a tag, e.g. "helper class", or "run class" */
	String ATT_CLASS = "class"; //$NON-NLS-1$
	
	/** action - identifies the incremental validation actions for which a resource should be filtered in */ 
	String ATT_ACTION_FILTER = "action"; //$NON-NLS-1$
	
	/** 
	 * incremental - identifies whether or not the validator supports incremental build validation. 
	 * Default is true (i.e., incremental builds are supported). 
	 */
	String ATT_INCREMENTAL = "incremental"; //$NON-NLS-1$
	
	/** true - The incremental default. */
	boolean ATT_INCREMENTAL_DEFAULT = true;
	
	/** 
	 * fullBuild - identifies whether or not the validator supports full build validation. 
	 * Default is true (i.e., full build validation is supported).
	 */ 
	String ATT_FULLBUILD = "fullBuild"; //$NON-NLS-1$ 
	
	/** true - The build default. */
	boolean ATT_FULLBUILD_DEFAULT = true;
	
	/** enabled - identifies whether or not the validator is enabled by default. Default is "true" (enabled). */ 
	String ATT_ENABLED = "enabled"; //$NON-NLS-1$
	
	/** true - The "enabled" default. */
	boolean ATT_ENABLED_DEFAULT = true;
	
	/** include - Include projects with this nature */
	String ATT_INCLUDE = "include"; //$NON-NLS-1$
	
	/** 
	 * true - By default, if "include" is not specified in the projectNature element, 
	 * then assume that the nature is included. */
	boolean ATT_INCLUDE_DEFAULT = true;
	
	/** 
	 * ruleGroup - identifies the different validation passes which this validator recognizes. 
	 * The values are identified in IRuleGroup.
	 */
	String ATT_RULE_GROUP = "ruleGroup"; //$NON-NLS-1$
	
	/** the default pass includes only the FAST PASS. FULL can be invoked only explicitly by an operation. */
	int ATT_RULE_GROUP_DEFAULT = IRuleGroup.PASS_FAST;
	
	/** caseSensitive */
	String ATT_CASE_SENSITIVE = "caseSensitive"; //$NON-NLS-1$
	
	/** async - Is the validator thread-safe? */
	String ATT_ASYNC = "async"; //$NON-NLS-1$
	
	/** false - The "can validator run asynchronously" default. In the future this may be changed to true. */
	boolean ATT_ASYNC_DEFAULT = false;

	/** 
	 * project - identifies whether or not the validator is called per project. 
	 * Default is false (i.e. the validator is called per resource). 
	 */
	String ATT_PROJECT = "project"; //$NON-NLS-1$
	
	/** false - The project default. */
	boolean ATT_PROJECT_DEFAULT = false;
	
	
	/** migrate - the "migrate" section of the validator */
	String TAG_MIGRATE = "migrate"; //$NON-NLS-1$
	
	/** validator - the "validator" element of the "migrate" section */
	String TAG_VALIDATOR = "validator"; //$NON-NLS-1$
	
	/** from - the fully-qualified class name of the former validator class */
	String ATT_FROM = "from"; //$NON-NLS-1$
	
	/** to - the fully-qualified class name of the current validator class */
	String ATT_TO = "to"; //$NON-NLS-1$
	String DEP_VALIDATOR = "dependentValidator"; //$NON-NLS-1$
	String DEP_VAL_VALUE = "depValValue"; //$NON-NLS-1$
	String MARKER_ID = "markerId"; //$NON-NLS-1$
	String MARKER_ID_VALUE = "markerIdValue"; //$NON-NLS-1$
	boolean DEP_VAL_VALUE_DEFAULT = false;
	String FACET = "facet"; //$NON-NLS-1$
	String FACET_ID ="facetId"; //$NON-NLS-1$
}
