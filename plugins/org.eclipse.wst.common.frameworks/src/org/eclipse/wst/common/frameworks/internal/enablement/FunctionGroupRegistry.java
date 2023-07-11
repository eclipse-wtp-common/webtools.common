/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.enablement;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.core.util.RegistryReader;
import org.eclipse.wst.common.frameworks.internal.WTPResourceHandler;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

/**
 * The FunctionGroupRegistry will consume Configuration elements conforming to the FunctionGroup
 * Extension Point schema.
 */
public class FunctionGroupRegistry extends RegistryReader {

	private static FunctionGroupRegistry INSTANCE = null;

	public static final String GROUP_ELEMENT = "functionGroup"; //$NON-NLS-1$
	public static final String GROUP_ID_ATTR = "functionGroupID"; //$NON-NLS-1$
	public static final String FUNCTION_GROUP_PATTERN_BINDING_ELMT = "functionGroupPatternBinding"; //$NON-NLS-1$
	public static final String PATTERN_ATTR = "pattern"; //$NON-NLS-1$

	private Map groupMapById;
	private List knownGroups;
	private Map patternBindingsByGroupId;

	private FunctionGroupRegistry() {
		super(WTPCommonPlugin.PLUGIN_ID, WTPCommonPlugin.GROUP_REGISTRY_EXTENSION_POINT);
		patternBindingsByGroupId = new HashMap();

	}

	public static FunctionGroupRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FunctionGroupRegistry();
			INSTANCE.readRegistry();
		}
		return INSTANCE;
	}

	/*
	 * (non-Javadoc) Read all the elements first, then set the pattern bindings on the function
	 * groups
	 */
	@Override
	public void readRegistry() {
		super.readRegistry();
		setPatternBindings();
	}

	private void setPatternBindings() {
		Iterator iter = patternBindingsByGroupId.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Entry) iter.next();
			String groupId = (String) entry.getKey();
			Set value = (Set) entry.getValue();
			FunctionGroup aGroup = getGroupByID(groupId);
			if (aGroup != null)
				aGroup.setFunctionGroupPatternBindings(value);
		}

	}

	@Override
	public boolean readElement(IConfigurationElement element) {
		if (element.getName().equals(GROUP_ELEMENT)) {
			readGroup(element);
			return true;
		} else if (element.getName().equals(FUNCTION_GROUP_PATTERN_BINDING_ELMT)) {
			readPatternBinding(element);
			return true;
		}
		return false;
	}

	/**
	 * @param element
	 */
	private void readPatternBinding(IConfigurationElement element) {
		String groupID = element.getAttribute(GROUP_ID_ATTR);
		String pattern = element.getAttribute(PATTERN_ATTR);
		if (!isNullOrEmpty(groupID) && !isNullOrEmpty(pattern)) {
			Pattern aPattern = Pattern.compile(pattern);
			FunctionGroupPatternBinding binding = new FunctionGroupPatternBinding(groupID, aPattern);
			addPatternBinding(groupID, binding);
		}

	}

	/**
	 * @param binding
	 */
	private void addPatternBinding(String groupID, FunctionGroupPatternBinding binding) {
		Set bindings = (Set) patternBindingsByGroupId.get(groupID);
		if (bindings == null) {
			bindings = new HashSet();
			patternBindingsByGroupId.put(groupID, bindings);
		}
		bindings.add(binding);
	}

	private boolean isNullOrEmpty(String aString) {
		return aString == null || aString.length() == 0;
	}

	private void readGroup(IConfigurationElement element) {
		String groupID = element.getAttribute(GROUP_ID_ATTR);
		if (!isNullOrEmpty(groupID)) {
			FunctionGroup group = new FunctionGroup(groupID, element);
			getGroupMapById().put(groupID, group);
			getKnownGroups().add(group);
		}
	}

	private List getAscendingSortedGroups(IProject project) {
		Comparator ascendingGrpComparator = AscendingGroupComparator.singleton();
		List groupList = getKnownGroups(project);
		Collections.sort(groupList, ascendingGrpComparator);
		return groupList;
	}

	private List getDescendingSortedGroups(IProject project) {
		Comparator descendingGrpComparator = DescendingGroupComparator.singleton();
		List groupList = getKnownGroups(project);
		Collections.sort(groupList, descendingGrpComparator);
		return groupList;
	}

	public List getKnownGroups(IProject project) {
		ArrayList groupByProjectList = new ArrayList(getKnownGroups().size());
		List groupList = getKnownGroups();
		for (int i = 0; i < groupList.size(); i++) {
			FunctionGroup group = (FunctionGroup) groupList.get(i);
			if (group.isEnabled(project))
				groupByProjectList.add(group);
		}
		return groupByProjectList;
	}

	public List getAscendingPriorityGroupNames(IProject project) {
		List sortedGroup = getAscendingSortedGroups(project);
		return getGroupListNames(sortedGroup, new ArrayList(sortedGroup.size()));

	}

	public List getDescendingPriorityGroupNames(IProject project) {
		List sortedGroup = getDescendingSortedGroups(project);
		return getGroupListNames(sortedGroup, new ArrayList(sortedGroup.size()));
	}

	private List getGroupListNames(List sortedGroup, List sortedGroupNames) {
		for (int i = 0; i < sortedGroup.size(); i++) {
			FunctionGroup grp = (FunctionGroup) sortedGroup.get(i);
			sortedGroupNames.add(grp.getGroupID());
		}
		return sortedGroupNames;
	}

	public Iterator getGroupIDs() {
		return getGroupMapById().keySet().iterator();
	}

	public FunctionGroup getGroupByID(String groupID) {
		return (FunctionGroup) getGroupMapById().get(groupID);
	}

	public boolean isGroupEnabled(IProject project, String groupID) {
		FunctionGroup group = getGroupByID(groupID);
		if (group != null)
			return group.isEnabled(project);
		return false;
	}

	public List getKnownGroups() {
		if (knownGroups == null)
			knownGroups = new ArrayList();
		return knownGroups;
	}

	/**
	 * @return Returns the groupMapById.
	 */
	protected Map getGroupMapById() {
		if (groupMapById == null)
			groupMapById = new HashMap();
		return groupMapById;
	}

	/**
	 * Check the priority of the two groups referenced by
	 * 
	 * @groupID1 and
	 * @groupID2
	 * 
	 * @param groupID1
	 * @param groupID2
	 * @return 0 if the two groups are equal, 1 if
	 * @groupID1 has a higher precedence, otherwise -1
	 */
	public int compare(String groupID1, String groupID2) {
		FunctionGroup group1 = getGroupByID(groupID1);
		FunctionGroup group2 = getGroupByID(groupID2);
		if (group1 == null) {
			WTPCommonPlugin.logError(new IllegalArgumentException(WTPResourceHandler.getString("28", new Object[]{groupID1}))); //$NON-NLS-1$
			return -1;
		}
		if (group2 == null) {
			WTPCommonPlugin.logError(new IllegalArgumentException(WTPResourceHandler.getString("28", new Object[]{groupID2}))); //$NON-NLS-1$
			return 1;
		}

		return group1.compareTo(group2);
	}

	public int getGroupPriority(String grpId) {
		FunctionGroup group = getGroupByID(grpId);
		return (group != null) ? group.getPriority() : -1;
	}

	public int getFunctionPriority(String enablementID) {
		int priority = Integer.MAX_VALUE;
		Iterator iter = getKnownGroups().iterator();
		while (iter.hasNext()) {
			FunctionGroup group = (FunctionGroup) iter.next();
			if (group.isMatch(enablementID))
				priority = Math.min(priority, group.getPriority());
		}
		return priority;

	}

}
