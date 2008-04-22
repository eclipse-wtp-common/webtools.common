/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;


import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.expressions.ExpressionTagNames;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.validation.internal.delegates.ValidatorDelegatesRegistry;
import org.eclipse.wst.validation.internal.operations.IRuleGroup;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.osgi.framework.Bundle;

/**
 * ValidationRegistryReader is a singleton who reads the plugin registry for Validator extensions.
 * The read is done once (in the constructor), and the list of validators can be accessed by calling
 * "getValidatorMetaData(String)" on this class. The read is triggered by a call from
 * ValidatorManager's loadValidatorMetaData(IProject) method. ValidatorManager delegates the load
 * call to this class, and if this class is null, the singleton is new'ed up, and the registry is
 * read.
 * 
 * No Validator should need to know about this class. The only class which should call
 * ValidationRegistryReader is ValidatorManager.
 * 
 * The Validator itself is initialized in the "initializeValidator" method.
 * 
 * <extension point="org.eclipse.wst.validation.internal.provisional.core.core.validator" id="EJBValidator" name="EJB
 * Validator"> <validator><projectNature id="com.ibm.etools.j2ee.EJBNature" include="false"/>
 * <filter objectClass="org.eclipse.core.resources.IFile" nameFilter = "ejb-jar.xml"/> <filter
 * objectClass="org.eclipse.core.resources.IFile" nameFilter = "*.java"/> <helper
 * class="org.eclipse.wst.validation.internal.provisional.core.core.ejb.workbenchimpl.EJBHelper"/> <run
 * class="org.eclipse.wst.validation.internal.provisional.core.core.ejb.EJBValidator" incremental="false" enabled="false"
 * pass="fast,full" async="false"/> <aggregateValidator class="my.aggregate.ValidatorClass"/>
 * <migrate><validator from="old.class.name" to="new.class.name"/> </migrate> </validator>
 * </extension>
 */
public final class ValidationRegistryReader implements RegistryConstants {
	private static ValidationRegistryReader inst;
	
	/** list of all validators registered, with their associated ValidatorMetaData, indexed by project nature id */
	private Map<String,Set<ValidatorMetaData>> _validators;
	
	// list of all validators, indexed by validator class name,
	// with the validator's ValidatorMetaData as the value.
	// Needed by the WorkbenchReporter, because sometimes the
	// IValidator is not enough to remove all messages from the
	// task list.
	private Map<String, ValidatorMetaData> _indexedValidators; 
	
	private Set<ValidatorMetaData> _defaultEnabledValidators;
	
	// Since IProject's contents are all instances of IResource, every type filter for a validator
	// must be an instance of IResource. This applies to both the rebuildCache pass and to the
	// validation pass.
	private static final String IRESOURCE = "org.eclipse.core.resources.IResource"; //$NON-NLS-1$

	private static final String UNKNOWN_PROJECT = "UNKNOWN"; //$NON-NLS-1$ // This 'project nature id' is used as a key to get the validators which can run on a project type which hasn't been explicitly filtered in or out by any validator.
	private static final String EXCLUDED_PROJECT = "EXCLUDED"; //$NON-NLS-1$ // This 'project nature id' is used as a key to get the validators which are excluded on certain projects.
	
	public HashMap<IProject, Set<ValidatorMetaData>> projectValidationMetaData;

	/**
	 * The registry is read once - when this class is instantiated.
	 */
	private ValidationRegistryReader() {
		super();

		try {
			_validators = new HashMap<String,Set<ValidatorMetaData>>();
			_indexedValidators = new HashMap<String, ValidatorMetaData>();
			_defaultEnabledValidators = new HashSet<ValidatorMetaData>();

			// Read the registry and build a map of validators. The key into
			// the map is the IValidator instance and the value is the ValidatorMetaData
			// which describes the IValidator.
			readRegistry();

			// Once all of the validators have been read, the caches of the
			// validators need to be updated.
			buildCache();
		} catch (Exception e) {
			ValidationPlugin.getPlugin().handleException(e);
		}
	}

	/**
	 * Traverse over the list of VMDs which have been added and create copies of it. The copies are
	 * created to increase runtime performance.
	 */
	private void buildCache() {
		for (ValidatorMetaData vmd : _indexedValidators.values()) {
			buildProjectNatureCache(vmd);
			buildDefaultEnabledCache(vmd);
		}

		// Now add the validators which are configured on all projects,
		// and all projects but X.
		addRemainder();

		// this temporary list isn't needed any more. All of the excluded
		// projects have been added to the project natures which they don't exclude.
		_validators.remove(EXCLUDED_PROJECT);

		if (Tracing.isTraceV1()) {
			Tracing.log("ValidationRegistryReader-01: ", debug()); //$NON-NLS-1$
		}
	}

	/**
	 * Build the cache of VMDs which is indexed by project nature ids. If the validator is
	 * registered on all project types, the vmd's project nature filters will be null.
	 */
	private void buildProjectNatureCache(ValidatorMetaData vmd) {
		// Build the cache with the identified project natures in validators'
		// extensions.
		ValidatorNameFilter[] projNatureIds = vmd.getProjectNatureFilters();
		String[] facetFilters = vmd.getFacetFilters();
		if (projNatureIds == null) {
			if (facetFilters == null && vmd.getEnablementExpresion() == null) {
				add(UNKNOWN_PROJECT, vmd);
			}
		} else {
			boolean noneIncluded = true; // assume that the validator does not include any project
			// natures
			for (int i = 0; i < projNatureIds.length; i++) {
				ValidatorNameFilter pn = projNatureIds[i];
				if (pn.isInclude()) {
					noneIncluded = false;
					add(pn.getNameFilter(), vmd);
				}
			}

			if (noneIncluded) {
				// add it to the list of EXCLUDED projects
				// (that is, a validator which excludes project natures but doesn't
				// explicitly include any. This type of validator runs on any unrecognized (UNKNOWN)
				// projects, but the rest of the cache needs to be built before this is added
				// to the UNKNOWN list. See addExcludedRemainder().
				add(EXCLUDED_PROJECT, vmd);
			}
		}
	}
	/**
	 * Build the list of validators which are enabled by default.
	 */
	private void buildDefaultEnabledCache(ValidatorMetaData vmd) {
		if (vmd == null)return;

		if (vmd.isEnabledByDefault())_defaultEnabledValidators.add(vmd);
	}

	/**
	 * Add vmd to the list of validators, indexed by validator class name
	 */
	private void add(ValidatorMetaData vmd) {
		if (vmd == null) {
			return;
		}

		_indexedValidators.put(vmd.getValidatorUniqueName(), vmd);
	}

	/*
	 * Some validators can run on any type of project. In order to have a static list, add the "any
	 * project" validators to each "project nature" validators' list. This avoids adding the "any
	 * project" validators to the "project nature" validators at runtime, which results in
	 * performance savings.
	 * 
	 * Some validators run on any type of project but X, where X is an excluded project nature.
	 * Those validators should also be added via this method.
	 */
	private void addRemainder() {
		// First, add all "can-run-on-any-project-type" to every registered project nature type in
		// the cache.
		addAnyRemainder();

		// Then add the "can-run-on-any-project-type-but-X" to every non-X registered project nature
		// type in the cache.
		addExcludedRemainder();
	}

	private void addExcludedRemainder() {
		Set<ValidatorMetaData> excludedProjVmds = _validators.get(EXCLUDED_PROJECT);
		if (excludedProjVmds == null) {
			// no excluded project natures
			return;
		}

		for (ValidatorMetaData vmd : excludedProjVmds) {

			// assume that, by default, if someone explicitly excludes
			// a project nature then they don't include any project natures
			boolean noneIncluded = true;
			
			// a project nature then they don't include any project natures
			for (String projId : _validators.keySet()) {
				if (projId.equals(UNKNOWN_PROJECT) || projId.equals(EXCLUDED_PROJECT)) {
					// Don't add list to a project nature which is excluded or applicable to all.
					continue;
				}

				ValidatorNameFilter filter = vmd.findProjectNature(projId);
				if (filter != null) {
					// Don't add list to itself (filter.isIncluded() == true) or
					// to a list from which it's excluded (filter.isIncluded() == false)
					if (filter.isInclude()) {
						noneIncluded = false;
					}
					continue;
				}

				add(projId, vmd);
			}

			if (noneIncluded) {
				// At this point, the "can-run-on-any-project" becomes
				// "not-excluded-on-these-projects". That is, if the project
				// nature id isn't in the list of _validators, then it isn't
				// included or excluded by any validators, so all validators
				// which can run on any project AND all validators which can
				// run on any but certain excluded projects can run on the
				// given IProject.
				add(UNKNOWN_PROJECT, vmd);
			}
		}
	}

	private void addAnyRemainder() {
		Set<ValidatorMetaData> anyProjVmds = _validators.get(UNKNOWN_PROJECT);
		if (anyProjVmds == null) {
			// no validators run on all projects
			return;
		}

		for (String projId : _validators.keySet()) {
			if (projId.equals(UNKNOWN_PROJECT) || projId.equals(EXCLUDED_PROJECT)) {
				// Don't add list to itself or to a project nature which is excluded.
				continue;
			}

			add(projId, anyProjVmds);
		}
	}

	private void add(String projectNatureId, Set<ValidatorMetaData> vmdList) {
		if ((vmdList == null) || (vmdList.size() == 0))return;

		// whether the validator includes or excludes this
		// project nature id, make sure that an entry is created for it in the table
		Set<ValidatorMetaData> pnVal = createSet(projectNatureId); 
		pnVal.addAll(vmdList);
		_validators.put(projectNatureId, pnVal);
	}

	private void add(String projectNatureId, ValidatorMetaData vmd) {
		if (vmd == null)return;

		// whether the validator includes or excludes this
		// project nature id, make sure that an entry is created for it in the table
		Set<ValidatorMetaData> pnVal = createSet(projectNatureId); 
		pnVal.add(vmd);
		_validators.put(projectNatureId, pnVal);
	}

	/**
	 * When a validator's class or helper class cannot be loaded, the vmd calls this method to
	 * disable the validator. The validator will be removed from the preference page, properties
	 * page, and enabled list of any project thereafter validated.
	 */
	public void disableValidator(ValidatorMetaData vmd) {
		_indexedValidators.remove(vmd.getValidatorUniqueName());
		_defaultEnabledValidators.remove(vmd);

		// The whole "on-any-project" and "exclude-this-project-nature" would take
		// a lot of processing time... Instead, traverse the list of proj nature ids,
		// and search the Set of that proj nature id, and remove the vmd if it's in the
		// Set.
		for (String projId : _validators.keySet()) {
			Set<ValidatorMetaData> value = _validators.get(projId);
			if (value == null)continue;

			if (value.contains(vmd)) {
				value.remove(vmd);
				_validators.put(projId, value);
			}
		}
	}

	private Set<ValidatorMetaData> createSet(String projNature) {
		Set<ValidatorMetaData> v = _validators.get(projNature);
		if (v == null) {
			v = new HashSet<ValidatorMetaData>();
		}
		return v;
	}

	/**
	 * Given an IConfigurationElement, if it has a project nature(s) specified, return the
	 * ValidatorNameFilters which represent those natures. Otherwise return null.
	 * 
	 * A project nature can be specified in plugin.xml to indicate what types of IProjects a
	 * validator can run on.
	 */
	private String[] getAggregateValidatorsNames(IConfigurationElement element) {
		IConfigurationElement[] filters = element.getChildren(TAG_AGGREGATE_VALIDATORS);
		if (filters.length == 0)
			return null;

		String[] names = new String[filters.length];
		for (int i = 0; i < names.length; i++) {
			// In order to speed up our String comparisons, load these
			// names into Java's constants space. This way, we'll be able to
			// use pointer comparison instead of the traditional
			// character-by-character comparison. Since these names should
			// never be set by anyone other than this class, and this class
			// sets them only once, it is safe to declare these Strings
			// constants.
			//
			// To load a String into the constants space, call intern() on the String.
			//
			String nameFilter = filters[i].getAttribute(ATT_CLASS);
			if (nameFilter != null) {
				nameFilter = nameFilter.intern();
			}
			names[i] = nameFilter;
		}
		return names;
	}
	
	private String[] getContentTypeBindings(IConfigurationElement element){
		IConfigurationElement[] bindings = element.getChildren(TAG_CONTENTTYPE);
		if(bindings.length == 0)
			return null;
		String[] cTypeIDs = new String[bindings.length];
		for (int i = 0; i < bindings.length; i ++){
			
			cTypeIDs[i] = bindings[i].getAttribute(ATT_CONTENTTYPEID);
		}
		
		return cTypeIDs;
		
	}

	/**
	 * Given an IConfigurationElement from plugin.xml, if it has any filter tags, construct the
	 * appropriate ValidatorFilters to represent those tags; else return null.
	 * 
	 * A filter can be specified in plugin.xml to filter out certain resources.
	 */
	private ValidatorFilter[] getFilters(IConfigurationElement element) {
		IConfigurationElement[] filters = element.getChildren(TAG_FILTER);
		if (filters.length == 0)
			return null;

		ValidatorFilter[] vf = new ValidatorFilter[filters.length];
		for (int i = 0; i < filters.length; i++) {
			vf[i] = new ValidatorFilter(IRESOURCE);

			// In order to speed up our String comparisons, load these
			// names into Java's constants space. This way, we'll be able to
			// use pointer comparison instead of the traditional
			// character-by-character comparison. Since these names should
			// never be set by anyone other than this class, and this class
			// sets them only once, it is safe to declare these Strings
			// constants.
			//
			// To load a String into the constants space, call intern() on the String.
			//
			String nameFilter = filters[i].getAttribute(ATT_NAME_FILTER);
			if (nameFilter != null) {
				nameFilter = nameFilter.intern();
			}
			String isCaseSensitive = filters[i].getAttribute(ATT_CASE_SENSITIVE);
			vf[i].setNameFilter(nameFilter, isCaseSensitive);

			String objectClass = filters[i].getAttribute(ATT_OBJECT_CLASS);
			if (objectClass != null) {
				objectClass = objectClass.intern();
			}
			vf[i].setTypeFilter(objectClass);

			String actionFilter = filters[i].getAttribute(ATT_ACTION_FILTER);
			if (actionFilter != null) {
				actionFilter = actionFilter.intern();
			}
			vf[i].setActionFilter(actionFilter);
		}
		return vf;
	}

	public boolean getDependentValidatorValue(IConfigurationElement element) {
		IConfigurationElement[] depValidatorElement = element.getChildren(DEP_VALIDATOR);
		if (depValidatorElement.length == 0)
			return false;
		String depValue = depValidatorElement[0].getAttribute(DEP_VAL_VALUE);
		boolean depBoolValue = (new Boolean(depValue)).booleanValue();
		return depBoolValue;
	}

	/**
	 * Return the name of the marker ID associated with the IValidator.
	 */
	public String[] getMarkerIdsValue(IConfigurationElement element) {
		IConfigurationElement[] markerId = element.getChildren(MARKER_ID);
		if (markerId.length == 0)
			return null;
		String markerIds[] = new String[markerId.length];
		for(int i = 0; i < markerIds.length; i++) {
			markerIds[i] = markerId[i].getAttribute(MARKER_ID_VALUE);
		}
		return markerIds;
	}
	
	public String[] getFacetIds(IConfigurationElement element) {
		IConfigurationElement[] facets = element.getChildren(FACET);
		if (facets.length == 0)
			return null;
		String[] facetIds = new String[facets.length];
		for (int i = 0; i < facets.length; i++) {
			facetIds[i] = facets[i].getAttribute(FACET_ID);
		}
		return facetIds;
	}

	/**
	 * Return the name of the helper class associated with the IValidator.
	 */
	private String getHelperName(IConfigurationElement element) {
		IConfigurationElement[] helpers = element.getChildren(TAG_HELPER_CLASS);
		if (helpers.length == 0)
			return null;

		return helpers[0].getAttribute(ATT_CLASS);
	}

	static IWorkbenchContext createHelper(IConfigurationElement element, String helperClassName) {
		IWorkbenchContext wh = null;
		try {
			wh = (IWorkbenchContext) element.createExecutableExtension(TAG_HELPER_CLASS);
		} catch (Exception exc) {
			ValidationPlugin.getPlugin().handleException(exc);
			String result = MessageFormat.format(
				ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_SYNTAX_NO_HELPER_THROWABLE), 
				new Object[]{helperClassName});
			ValidationPlugin.getPlugin().logMessage(IStatus.ERROR, result);	
			return null;
		}
		return wh;
	}

	static IValidator createValidator(IConfigurationElement element, String validatorClassName) {
		IValidator validator = null;
		try {
			validator = (IValidator) element.createExecutableExtension(ATT_CLASS);
		} catch (Exception e) {
			String result = MessageFormat.format(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_SYNTAX_NO_VAL_THROWABLE), 
				new Object[]{validatorClassName});
			ValidationPlugin.getPlugin().logMessage(IStatus.ERROR, result);	
			ValidationPlugin.getPlugin().handleException(e);
		}

		if (validator == null) {
			if (Tracing.isTraceV1()) {
				Tracing.log("ValidationRegistryReader-02: ",  //$NON-NLS-1$
					NLS.bind(ValMessages.VbfExcSyntaxNoValNull, validatorClassName));
			}
			return null;
		}

		return validator;
	}

	/**
	 * Given an IConfigurationElement from plugin.xml, return whether or not the validator is
	 * enabled by default.
	 * 
	 * If no enabled attribute is specified, the default, true (i.e., enabled) is returned.
	 */
	private boolean getEnabledByDefault(IConfigurationElement element) {
		IConfigurationElement[] runChildren = element.getChildren(TAG_RUN_CLASS);
		// Don't need to check if runChildren is null or empty, because that was checked in the
		// initializeValidator method.

		String inc = runChildren[0].getAttribute(ATT_ENABLED);
		if (inc == null) {
			return RegistryConstants.ATT_ENABLED_DEFAULT;
		}

		return Boolean.valueOf(inc.trim().toLowerCase()).booleanValue(); // this will return true
		// if, and only if, the
		// attribute value is
		// "true". For example,
		// "yes" will be considered
		// "false".
	}

	/**
	 * Given an IConfigurationElement from plugin.xml, return whether or not the validator supports
	 * incremental validation.
	 * 
	 * If no incremental attribute is specified, the default, true (i.e., incremental is supported)
	 * is returned.
	 */
	private boolean getIncremental(IConfigurationElement element) {
		IConfigurationElement[] runChildren = element.getChildren(TAG_RUN_CLASS);
		// Don't need to check if runChildren is null or empty, because that was checked in the
		// initializeValidator method.

		String inc = runChildren[0].getAttribute(ATT_INCREMENTAL);
		if (inc == null) {
			return RegistryConstants.ATT_INCREMENTAL_DEFAULT;
		}

		return Boolean.valueOf(inc.trim().toLowerCase()).booleanValue(); // this will return true
		// if, and only if, the
		// attribute value is
		// "true". For example,
		// "yes" will be considered
		// "false".
	}

	/**
	 * Given an IConfigurationElement from plugin.xml, return whether or not the validator supports
	 * full build validation.
	 * 
	 * If no incremental attribute is specified, the default, true (i.e., incremental is supported)
	 * is returned.
	 */
	private boolean getFullBuild(IConfigurationElement element) {
		IConfigurationElement[] runChildren = element.getChildren(TAG_RUN_CLASS);
		// Don't need to check if runChildren is null or empty, because that was checked in the
		// initializeValidator method.

		String fb = runChildren[0].getAttribute(ATT_FULLBUILD);
		if (fb == null) {
			return RegistryConstants.ATT_FULLBUILD_DEFAULT;
		}

		return Boolean.valueOf(fb.trim().toLowerCase()).booleanValue(); // this will return true if,
		// and only if, the
		// attribute value is
		// "true". For example,
		// "yes" will be considered
		// "false".
	}

	/**
	 * Given an IConfigurationElement from plugin.xml, return whether or not the validator supports
	 * asynchronous validation.
	 * 
	 * If no async attribute is specified, the default, true (i.e., the validator is thread-safe) is
	 * returned.
	 */
	private boolean getAsync(IConfigurationElement element) {
		IConfigurationElement[] runChildren = element.getChildren(TAG_RUN_CLASS);
		// Don't need to check if runChildren is null or empty, because that was checked in the
		// initializeValidator method.

		String async = runChildren[0].getAttribute(ATT_ASYNC);
		if (async == null) {
			return RegistryConstants.ATT_ASYNC_DEFAULT;
		}

		return Boolean.valueOf(async.trim().toLowerCase()).booleanValue(); // this will return true
		// if, and only if, the
		// attribute value is
		// "true". For example,
		// "yes" will be
		// considered "false".
	}

	/**
	 * Given an IConfigurationElement from plugin.xml, return the types of validation passes, as
	 * defined in IRuleGroup, that the validator performs.
	 * 
	 * If no pass attribute is specified, the default, IRuleGroup.PASS_FULL, is returned.
	 */
	private int getRuleGroup(IConfigurationElement element) {
		IConfigurationElement[] runChildren = element.getChildren(TAG_RUN_CLASS);
		// Don't need to check if runChildren is null or empty, because that was checked in the
		// initializeValidator method.

		String pass = runChildren[0].getAttribute(ATT_RULE_GROUP);
		if (pass == null) {
			return RegistryConstants.ATT_RULE_GROUP_DEFAULT;
		}

		final String COMMA = ","; //$NON-NLS-1$
		StringTokenizer tokenizer = new StringTokenizer(pass, COMMA, false); // false means don't
		// return the comma as
		// part of the string
		int result = 0; // no passes identified
		while (tokenizer.hasMoreTokens()) {
			String nextAction = tokenizer.nextToken().trim();
			if (nextAction.equals(IRuleGroup.PASS_FAST_NAME)) {
				result = result | IRuleGroup.PASS_FAST;
			} else if (nextAction.equals(IRuleGroup.PASS_FULL_NAME)) {
				result = result | IRuleGroup.PASS_FULL;
			}
		}

		if (result == 0) {
			// No recognized passes. Return the default.
			return RegistryConstants.ATT_RULE_GROUP_DEFAULT;
		}

		return result;
	}

	private ValidatorMetaData.MigrationMetaData getMigrationMetaData(IConfigurationElement element, ValidatorMetaData vmd) {
		IConfigurationElement[] runChildren = element.getChildren(TAG_MIGRATE);
		if ((runChildren == null) || (runChildren.length == 0)) {
			return null;
		}

		// Only supposed to be one "migrate" section in a validator, so ignore the rest
		IConfigurationElement migrate = runChildren[0];

		// Now look for the "validator" elements. Zero or more can be specified.
		IConfigurationElement[] migrateChildren = migrate.getChildren(TAG_VALIDATOR);
		if ((migrateChildren == null) || (migrateChildren.length == 0)) {
			return null;
		}

		ValidatorMetaData.MigrationMetaData mmd = vmd.new MigrationMetaData();
		for (int i = 0; i < migrateChildren.length; i++) {
			IConfigurationElement migrateChild = migrateChildren[i];
			String from = migrateChild.getAttribute(ATT_FROM);
			if (from == null) {
				continue;
			}

			String to = migrateChild.getAttribute(ATT_TO);
			if (to == null) {
				continue;
			}
			mmd.addId(from, to);
		}
		return mmd;
	}

	/**
	 * Given an IConfigurationElement, if it has a project nature(s) specified, return the
	 * ValidatorNameFilters which represent those natures. Otherwise return null.
	 * 
	 * A project nature can be specified in plugin.xml to indicate what types of IProjects a
	 * validator can run on.
	 */
	private ValidatorNameFilter[] getProjectNatureFilters(IConfigurationElement element) {
		IConfigurationElement[] filters = element.getChildren(TAG_PROJECT_NATURE);
		if (filters.length == 0) {
			return null;
		}

		ValidatorNameFilter[] vf = new ValidatorNameFilter[filters.length];
		for (int i = 0; i < filters.length; i++) {
			vf[i] = new ValidatorNameFilter();
			// In order to speed up our String comparisons, load these
			// names into Java's constants space. This way, we'll be able to
			// use pointer comparison instead of the traditional
			// character-by-character comparison. Since these names should
			// never be set by anyone other than this class, and this class
			// sets them only once, it is safe to declare these Strings
			// constants.
			//
			// To load a String into the constants space, call intern() on the String.
			//
			String nameFilter = filters[i].getAttribute(ATT_ID);
			if (nameFilter != null) {
				nameFilter = nameFilter.intern();
			}
			vf[i].setNameFilter(nameFilter);

			String include = filters[i].getAttribute(ATT_INCLUDE);
			vf[i].setInclude(include);
		}
		return vf;
	}

	/**
	 * Returns the singleton ValidationRegistryReader.
	 */
	public static ValidationRegistryReader getReader() {
		if (inst == null) {
			inst = new ValidationRegistryReader();

			EventManager.getManager().setActive(true);
		}
		return inst;
	}

	public static boolean isActivated() {
		// Whether the registry has been read or not is kept in the EventManager
		// class instead of this class in order to work around a shutdown problem.
		// See the comment in the isActive() method of the EventManager class
		// for details.
		return EventManager.getManager().isActive();
	}

	/**
	 * Returns the Validator extension point
	 */
	private IExtensionPoint getValidatorExtensionPoint() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(PLUGIN_ID, VALIDATOR_EXT_PT_ID);
		if (extensionPoint == null) {
			// If this happens it means that someone removed the "validator" extension point
			// declaration from our plugin.xml file.
			if (Tracing.isTraceV1()) {
				String result = MessageFormat.format(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_MISSING_VALIDATOR_EP),
						new Object[]{ValidationPlugin.PLUGIN_ID + "." + VALIDATOR_EXT_PT_ID}); //$NON-NLS-1$
				Tracing.log("ValidationRegistryReader-03: ", result);		 //$NON-NLS-1$
			}
		}
		return extensionPoint;
	}

	/**
	 * It's okay to return a handle to the ValidatorMetaData because the vmd can't be modified by
	 * any code not in this package.
	 */
	public ValidatorMetaData getValidatorMetaData(IValidator validator) {
		// retrieval will be in log(n) time
		if (validator == null) {
				String message = ResourceHandler.getExternalizedMessage(
					ResourceConstants.VBF_EXC_ORPHAN_IVALIDATOR, new String[]{"null"}); //$NON-NLS-1$
				ValidationPlugin.getPlugin().logMessage(IStatus.ERROR, message);
			return null;
		}

		String validatorClassName = validator.getClass().getName();
		ValidatorMetaData vmd = getValidatorMetaData(validatorClassName);
		if (vmd != null) {
			return vmd;
		}

		// If we got here, then vmd is neither a root nor an aggregate validator,
		// yet the IValidator exists. Internal error.
		String message = ResourceHandler.getExternalizedMessage(
				ResourceConstants.VBF_EXC_ORPHAN_IVALIDATOR, new String[]{validatorClassName});
		ValidationPlugin.getPlugin().logMessage(IStatus.ERROR, message);
		return null;
	}

	public Set<ValidatorMetaData> getValidatorMetaData(IWorkspaceRoot root) {
		// Every validator on the Preferences page must be returned
		Set<ValidatorMetaData> copy = new HashSet<ValidatorMetaData>();
		clone(_indexedValidators.values(), copy);
		return copy;
	}

	/**
	 * Return a collection of Validators configured on a certain type of IProject (e.g. EJB Project
	 * vs. Web Project).
	 * 
	 * This is a long-running process. If you can, cache the result.
	 */
	public Set<ValidatorMetaData> getValidatorMetaData(IProject project) {
		Set<ValidatorMetaData> copy = new HashSet<ValidatorMetaData>();
		getValidatorMetaData(project, copy);
		return copy;
	}

	/**
	 * Copy the set of configured validator metadata into the Set.
	 */
	public void getValidatorMetaData(IProject project, Set<ValidatorMetaData> vmds) {
		if (vmds == null)return;
		vmds.clear();
		int executionMap = 0x0;
		try {
			if (Tracing.isTraceV1()) {
				Tracing.log("ValidationRegistryReader-04: IProject is " + String.valueOf(project)); //$NON-NLS-1$
			}
			if (project == null) {
				executionMap |= 0x1;
				// vmds is already clear
				return;
			}
			String[] projectNatures = null;
			try {
				projectNatures = project.getDescription().getNatureIds();
			} catch (CoreException e) {
				executionMap |= 0x2;
				// vmds is already clear
				ValidationPlugin.getPlugin().handleException(e);
				return;
			}
			// If there are no project natures on a particular project,
			// or if this project nature has no validators configured
			// on it, return the validators which are configured on all
			// projects.
			if ((projectNatures == null) || (projectNatures.length == 0)) {
				executionMap |= 0x4;
				
				// Also include the validators which are enabled through enablement
				// expression for this project.
				// Note that the API isFacetEnabled(vmd, project) works properly 
				// only when the plugin containing the property tester is activated.
				// forcePluginActivation="true" may be needed in the declaration of 
				// the enablement in the validator extension point.
		        // <enablement>
		        //  <test forcePluginActivation="true" property="foo.testProperty"/>
		        // </enablement> 
				
				Set<ValidatorMetaData> validatorsWithEnablementExpression = new HashSet<ValidatorMetaData>();
				for (ValidatorMetaData vmd : getAllValidators()) {
					if (isFacetEnabled(vmd, project)) {
						validatorsWithEnablementExpression.add(vmd);
					}
				}
				if(validatorsWithEnablementExpression.size() > 0 ){
					validatorsWithEnablementExpression.addAll( getValidatorMetaDataUnknownProject());
					clone(validatorsWithEnablementExpression, vmds);
				}
				else
					clone(getValidatorMetaDataUnknownProject(), vmds);


			} else {
				executionMap |= 0x8;
				if (Tracing.isTraceV1()) {
					Tracing.log("ValidationRegistryReader-05: ", projectNatures.toString()); //$NON-NLS-1$
				}
				calculateVmdsForNatureAndFacets(vmds, projectNatures,project);
				// Now filter out the validators which must not run on this project
				removeExcludedProjects(project, vmds);
				if (vmds.size() == 0) {
					executionMap |= 0x20;
					clone(getValidatorMetaDataUnknownProject(), vmds);
				}
			}
		} finally {
			if (Tracing.isTraceV1()) {
				StringBuffer buffer = new StringBuffer();
				for (ValidatorMetaData vmd : vmds) {
					buffer.append(vmd.getValidatorUniqueName());
					buffer.append("\n"); //$NON-NLS-1$
				}
				Tracing.log("ValidationRegistryReader-06: ", buffer.toString()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @param project
	 * @param vmds
	 * @param projectNatures
	 */
	private void calculateVmdsForNatureAndFacets(Set<ValidatorMetaData> vmds, String[] projectNatures, IProject project) {
		Set<ValidatorMetaData> projVmds;
		String[] projectFacetIds = getProjectFacetIds(project);
		for (ValidatorMetaData vmd : getAllValidators()) {
			if (containsProjectFacet(vmd, projectFacetIds) || isFacetEnabled(vmd, project)) {
				vmds.add(vmd);
			}
		}
		for (String projectNatureId : projectNatures) {
			projVmds = _validators.get(projectNatureId);
			if (projVmds == null)continue;

			for (ValidatorMetaData vmd : projVmds) {
				if (!vmds.contains(vmd) && (vmd.getFacetFilters() == null || vmd.getFacetFilters().length == 0)) {
					if (vmd.getEnablementExpresion() == null)vmds.add(vmd);
					else if (isFacetEnabled(vmd, project))vmds.add(vmd);
				}
			}
		}
	}

	private boolean containsProjectFacet(ValidatorMetaData vmd, String[] projectFacetIds) {
		String[] validatorFacets = vmd.getFacetFilters();
		if (validatorFacets != null && validatorFacets.length > 0) {
			if (projectFacetIds != null && projectFacetIds.length > 0) {
				if (Arrays.asList(projectFacetIds).containsAll(Arrays.asList(validatorFacets)))
					return true;
			}
		}
		return false;
	}
	
	private boolean isFacetEnabled(ValidatorMetaData vmd, IProject project) {
		try {
			Expression expression = vmd.getEnablementExpresion();
			if (expression != null) {
				EvaluationContext context = new EvaluationContext(null, project);
				context.setAllowPluginActivation(true);
				EvaluationResult result = expression.evaluate(context);
				return result == EvaluationResult.TRUE;
			}
		} catch (CoreException ce) {
		}
		return false;
	}

	private String[] getProjectFacetIds(IProject project) {
		try {
			IFacetedProject fProject = ProjectFacetsManager.create(project);
			if (fProject != null) {
				Object[] projectFacets = fProject.getProjectFacets().toArray();
				String[] projectFacetIds = new String[projectFacets.length];
				for (int i = 0; i < projectFacets.length; i++) {
					IProjectFacet projectFacet = ((IProjectFacetVersion) projectFacets[i]).getProjectFacet();
					projectFacetIds[i] = projectFacet.getId();
				}
				return projectFacetIds;
			}
		} catch (CoreException ce) {
		}

		return null;
	}

	/*
	 * If one project nature on the project includes a particular validator, but another project
	 * nature excludes that validator, then the validator needs to be removed from the vmd set.
	 * 
	 * For example, if AValidator can run on any java project but not on a J2EE project, which is an
	 * instance of a java project, then the AValidator is included by the java nature and excluded
	 * by the J2EE nature. The AValidator would have to be removed from the set.
	 */
	private void removeExcludedProjects(IProject project, Set<ValidatorMetaData> vmds) {
		if (Tracing.isTraceV1()) {
			StringBuffer buffer = new StringBuffer("\nValidationRegistryReader-12: before:\n"); //$NON-NLS-1$
			for (ValidatorMetaData vmd : vmds) {
				buffer.append(vmd.getValidatorUniqueName());
				buffer.append("\n"); //$NON-NLS-1$
			}
			Tracing.log(buffer);
		}

		String[] projectNatures = null;
		try {
			projectNatures = project.getDescription().getNatureIds();
		} catch (CoreException e) {
			ValidationPlugin.getPlugin().handleException(e);
			return;
		}
		if ((projectNatures == null) || (projectNatures.length == 0)) {
			// nothing needs to be removed from the list
			return;
		}
		for (int i = 0; i < projectNatures.length; i++) {
			String nature = projectNatures[i];
			Iterator<ValidatorMetaData> iterator = vmds.iterator();
			while (iterator.hasNext()) {
				ValidatorMetaData vmd = iterator.next();
				ValidatorNameFilter[] natureFilters = vmd.getProjectNatureFilters();
				if (natureFilters == null) {
					// Can run on any project
					continue;
				}

				for (ValidatorNameFilter pn : natureFilters) {
					if (nature.equals(pn.getNameFilter()) && !pn.isInclude()) {
						iterator.remove();
						break;
					}
				}
			}
		}

		if (Tracing.isTraceV1()) {
			StringBuffer buffer = new StringBuffer("\nValidationRegistryReader-13: after:\n"); //$NON-NLS-1$
			for (ValidatorMetaData vmd : vmds) {
				buffer.append(vmd.getValidatorUniqueName());
				buffer.append("\n"); //$NON-NLS-1$
			}
			Tracing.log(buffer);
		}
	}

	@SuppressWarnings("unchecked")
	private Collection clone(Collection input, Collection copy) {
		if (input == null || copy == null)return null;
		copy.clear();
		copy.addAll(input);
		return copy;
	}

	public String debug() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Project nature => validators configured"); //$NON-NLS-1$
		buffer.append("\n"); //$NON-NLS-1$
		for (String projId : _validators.keySet()) {
			buffer.append("projId: "); //$NON-NLS-1$
			buffer.append(projId);
			buffer.append("\n"); //$NON-NLS-1$
			Set<ValidatorMetaData> validators = _validators.get(projId);
			for (ValidatorMetaData vmd : validators) {
				buffer.append("\t"); //$NON-NLS-1$
				buffer.append(vmd.getValidatorUniqueName());
				buffer.append("\n"); //$NON-NLS-1$
			}
		}
		buffer.append("\n"); //$NON-NLS-1$

		buffer.append("Enable/disable validator by default"); //$NON-NLS-1$
		buffer.append("\n"); //$NON-NLS-1$
		for (ValidatorMetaData vmd : _indexedValidators.values()) {
			buffer.append(vmd.getValidatorUniqueName());
			buffer.append(" enabled? "); //$NON-NLS-1$
			buffer.append(vmd.isEnabledByDefault());
			buffer.append("\n"); //$NON-NLS-1$
		}

		return buffer.toString();
	}

	public boolean isConfiguredOnProject(ValidatorMetaData vmd, IProject project) {
		if (projectValidationMetaData == null)
			projectValidationMetaData = new HashMap<IProject, Set<ValidatorMetaData>>();

		Set<ValidatorMetaData> vmds = projectValidationMetaData.get(project);
		if (vmds != null) {
			return vmds.contains(vmd);
		} else {
			Set<ValidatorMetaData> prjVmds = getValidatorMetaData(project);
			if (prjVmds == null || prjVmds.size() == 0)return false;
			projectValidationMetaData.put(project, prjVmds);
			return prjVmds.contains(vmd);
		}
	}

	/**
	 * Return a set of ValidatorMetaData which are configured on all projects or which run on any
	 * projects except certain project types.
	 * 
	 * Unlike other get methods, because this method is private it doesn't return a clone.
	 * 
	 * @see addExcludedRemainder()
	 */
	private Set<ValidatorMetaData> getValidatorMetaDataUnknownProject() {
		Set<ValidatorMetaData> projVmds = _validators.get(UNKNOWN_PROJECT);
		if (projVmds == null) {
			projVmds = new HashSet<ValidatorMetaData>();
		}
		return projVmds;
	}

	/**
	 * Return a set of ValidatorMetaData which are enabled by default.
	 */
	public Set<ValidatorMetaData> getValidatorMetaDataEnabledByDefault() {
		Set<ValidatorMetaData> copy = new HashSet<ValidatorMetaData>();
		clone(_defaultEnabledValidators, copy);
		return copy;
	}

	public ValidatorMetaData[] getValidatorMetaDataArrayEnabledByDefault() {
		ValidatorMetaData[] result = new ValidatorMetaData[_defaultEnabledValidators.size()];
		_defaultEnabledValidators.toArray(result);
		return result;
	}

	/**
	 * This method should be called ONLY by the validation framework, UI, or TVT plugin. In general,
	 * only the validation framework and the validation TVT should handle ValidatorMetaData objects.
	 * 
	 * Given a string which identifies a fully-qualified class name of a validator, return the
	 * ValidatorMetaData that uses a validator of that name, if it exists.
	 * 
	 * It's okay to return a handle to the ValidatorMetaData because the vmd can't be modified by
	 * any code not in this package.
	 */
	public ValidatorMetaData getValidatorMetaData(String validatorClassName) {
		if (validatorClassName == null)return null;

		ValidatorMetaData vmd2 = _indexedValidators.get(validatorClassName);
		if (vmd2 != null)return vmd2;

		// Check for an aggregate validator
		for (ValidatorMetaData vmd : _indexedValidators.values()) {
			if (vmd == null)continue;

			if (vmd.getValidatorUniqueName().equals(validatorClassName))return vmd;

			String[] aggregateNames = vmd.getAggregatedValidatorNames();
			if (aggregateNames != null) {
				for (String aggregateName : aggregateNames) {
					if (validatorClassName.equals(aggregateName))return vmd;
				}
			}

			// Current name of validator doesn't match; has this validator been
			// migrated from another package?
			ValidatorMetaData.MigrationMetaData mmd = vmd.getMigrationMetaData();
			if (mmd == null) {
				// Validator class name hasn't been migrated
				continue;
			}

			Set<String[]> idList = mmd.getIds();
			if (idList == null) {
				// Invalid <migrate> element.
				continue;
			}

			for (String[] ids : idList) {
				if (ids.length != 2) {
					// log
					continue;
				}

				String from = ids[0];
				if (from == null) {
					// log
					continue;
				}

				if (from.equals(validatorClassName)) {
					return vmd;
				}
			}
		}

		// If we got to this point, no validator using that class name is loaded.
		return null;
	}

	/**
	 * Return true if the named validator is installed, otherwise false.
	 */
	public boolean isExistingValidator(String validatorClassName) {
		return (getValidatorMetaData(validatorClassName) != null);
	}

	/**
	 * Initialize the validator with the static metadata (runtime metadata is initialized in the
	 * ValidationOperation class).
	 */
	private ValidatorMetaData initializeValidator(IConfigurationElement element, String validatorName, String pluginId) {
		IConfigurationElement[] runChildren = element.getChildren(TAG_RUN_CLASS);
		if ((runChildren == null) || (runChildren.length < 1)) {
			// How can an IValidatorImpl be created when there no class name to instantiate?
			if (Tracing.isLogging()) {
				Tracing.log("ValidationRegistryReader-07: ", NLS.bind(ValMessages.VbfExcSyntaxNoValRun, validatorName));				 //$NON-NLS-1$
			}
			return null;
		}

		//WTP Bugzilla defect: 82338
		//Using the Unique Identifier give the flexibility of the same validator class used by other validator extensions without writing a new validation class
		//Reverting the fix back as the class name defined in the ext is unique to this validator and has to be used for the unique id in the validation metadata
		String validatorImplName = runChildren[0].getAttribute(ATT_CLASS);
		
		if (validatorImplName == null) {
			// Same as before; how can we instantiate when...
			if (Tracing.isLogging()) {
				Tracing.log("ValidationRegistryReader-08: ", NLS.bind(ValMessages.VbfExcSyntaxNoValClass, validatorName)); //$NON-NLS-1$
			}
			return null;
		}

		String helperImplName = getHelperName(element);
		if (helperImplName == null) {
			// Same as before; how can we instantiate when...
			if (Tracing.isLogging()) {
				Tracing.log("ValidationRegistryReader-09: ", NLS.bind(ValMessages.VbfExcSyntaxNoValRun, validatorImplName)); //$NON-NLS-1$
			}
			return null;
		}

		// In order to speed up our String comparisons, load these
		// names into Java's constants space. This way, we'll be able to
		// use pointer comparison instead of the traditional
		// character-by-character comparison. Since these names should
		// never be set by anyone other than this class, and this class
		// sets them only once, it is safe to declare these Strings
		// constants.
		//
		// To load a String into the constants space, call intern() on the String.
		//
		ValidatorMetaData vmd = new ValidatorMetaData();
		vmd.addFilters(getFilters(element)); // validator may, or may not, have filters
		vmd.addProjectNatureFilters(getProjectNatureFilters(element)); // validator may, or may not, specify a project nature
		vmd.addFacetFilters(getFacetIds(element));//validator may or may not specify the facet
		vmd.setEnablementElement(getEnablementElement(element));
		vmd.addAggregatedValidatorNames(getAggregateValidatorsNames(element)); // if a validator
		// aggregated another validator, it should identify
		// the sub-validator(s)' class name
		vmd.setValidatorDisplayName(validatorName.intern()); // validator must have a display name.
		vmd.setValidatorUniqueName(validatorImplName.intern());
		vmd.setPluginId(pluginId);
		vmd.setIncremental(getIncremental(element));
		vmd.setFullBuild(getFullBuild(element));
		vmd.setAsync(getAsync(element));
		vmd.setRuleGroup(getRuleGroup(element));
		vmd.setEnabledByDefault(getEnabledByDefault(element));
		vmd.setMigrationMetaData(getMigrationMetaData(element, vmd));
		vmd.setHelperClass(element, helperImplName);
		vmd.setValidatorClass(runChildren[0]); // associate the above attributes with the validator
		vmd.addDependentValidator(getDependentValidatorValue(element));
		vmd.setContentTypeIds(getContentTypeBindings(element));
		initializeValidatorCustomMarkers(element, pluginId, vmd);
		
		if (Tracing.isTraceV1()) {
			Tracing.log("ValidationRegistryReader-10: validator loaded: " + validatorImplName); //$NON-NLS-1$
		}

		return vmd;
	}

	/**
	 * @param element
	 * @param pluginId
	 * @param vmd
	 */
	private void initializeValidatorCustomMarkers(IConfigurationElement element, String pluginId, ValidatorMetaData vmd) {
		String[] customMarkerIds = getMarkerIdsValue(element);
		if (customMarkerIds != null && customMarkerIds.length > 0) {
			String[] qualifiedMarkerIds = new String[customMarkerIds.length];
			for (int i = 0; i < customMarkerIds.length; i++) {
				String markerid = customMarkerIds[i];
				if (markerid.lastIndexOf(".") != -1) { //$NON-NLS-1$
					String pluginID = markerid.substring(0, markerid.lastIndexOf(".")); //$NON-NLS-1$
					Bundle bundle = Platform.getBundle(pluginID);
					if (bundle == null)
						qualifiedMarkerIds[i] = pluginId + "." + customMarkerIds[i]; //$NON-NLS-1$
					else
						qualifiedMarkerIds[i] = customMarkerIds[i];
				} else
					qualifiedMarkerIds[i] = pluginId + "." + customMarkerIds[i]; //$NON-NLS-1$
			}
			vmd.setMarkerIds(qualifiedMarkerIds);
		}
	}

	private Expression getEnablementElement(IConfigurationElement element) {
		IConfigurationElement[] enablements = element.getChildren(ExpressionTagNames.ENABLEMENT);
		if (enablements.length == 0)
			return null;
		try {
			return ExpressionConverter.getDefault().perform(enablements[0]);
		} catch (CoreException e) {
			ValidationPlugin.getPlugin().handleException(e);
		}
		return null;
	}

	/**
	 * This method should be called ONLY BY THE VALIDATION FRAMEWORK! The value from this method is
	 * used to populate the validation preference page.
	 */
	public Collection<ValidatorMetaData> getAllValidators() {
		Set<ValidatorMetaData> validators = new HashSet<ValidatorMetaData>(50);
		clone(_indexedValidators.values(), validators);
		return validators;
	}

	public int numberOfValidators() {
		return _indexedValidators.size();
	}

	/**
	 * Reads one extension by looping through its configuration elements.
	 */
	private void readExtension(IExtension extension) {
		IConfigurationElement[] elements = extension.getConfigurationElements();

		for (int i = 0; i < elements.length; i++) {
			IConfigurationElement element = elements[i];

			String label = extension.getLabel();
			if (label == null || label.equals("")) { //$NON-NLS-1$
				if (Tracing.isTraceV1()) {
					String[] msgParm = {extension.getUniqueIdentifier()};
					String result = MessageFormat.format(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_VALIDATORNAME_IS_NULL),
							(Object[])msgParm);
					Tracing.log("ValidationRegistryReader-11: ", result); //$NON-NLS-1$
				}
			} else {
				// If getLabel() returns an empty string, this is an illegal validator.
				// The PropertyPage, and other status messages, need to have a displayable name for
				// the validator.
				String pluginId = extension.getContributor().getName();
				ValidatorMetaData vmd = initializeValidator(element, label, pluginId);

				if (vmd != null) {
					// Add this validator to the list of validators; if vmd is null, the validator
					// couldn't be created.
					add(vmd);
				}
			}
		}
	}

	/**
	 * Reads the registry to find the Validators which have been implemented.
	 */
	private void readRegistry() {
		_validators.clear();

		// Get the extensions that have been registered.
		IExtensionPoint validatorEP = getValidatorExtensionPoint();
		if (validatorEP == null) {
			return;
		}
		IExtension[] extensions = validatorEP.getExtensions();

		// find all runtime implementations
		for (int i = 0; i < extensions.length; i++) {
			readExtension(extensions[i]);
		}
    
    // Force the delegate validators registry to be read early to avoid
    // the non-synchronized singleton issue which occurs when two delegating
    // validators race to load the registry.
    
    ValidatorDelegatesRegistry.getInstance();
	}

	public IValidator getValidator(String validatorClassName) throws InstantiationException {
		ValidatorMetaData vmd = _indexedValidators.get(validatorClassName);
		if(vmd != null)
			return vmd.getValidator();
		return null;
	}

}
