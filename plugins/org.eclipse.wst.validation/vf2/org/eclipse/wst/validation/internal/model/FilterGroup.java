/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.validation.internal.ContentTypeWrapper;
import org.eclipse.wst.validation.internal.Deserializer;
import org.eclipse.wst.validation.internal.ExtensionConstants;
import org.eclipse.wst.validation.internal.Serializer;
import org.eclipse.wst.validation.internal.ValMessages;

/**
 * An immutable group of filter rules.
 * @author karasiuk
 *
 */
public abstract class FilterGroup implements IAdaptable {
	
	private final FilterRule[] _rules;
	
	/** The version number of the serialization (in case we ever need to change this) */
	private static final int SerializationVersion = 1;

	/**
	 * Answer a filter group based on the type of the group.
	 * 
	 * @param name either "include" or "exclude"
	 * 
	 * @return null if the name parameter isn't correct.
	 */
	public static FilterGroup create(String name, FilterRule[] rules) {
		if (ExtensionConstants.include.equals(name))return new FilterIncludeGroup(rules);
		if (ExtensionConstants.exclude.equals(name))return new FilterExcludeGroup(rules);
		return null;
	}
	
	/**
	 * Answer a filter group from a deserializer.
	 * @param des
	 * 
	 * @see FilterGroup#save(Serializer)
	 */
	public static FilterGroup create(Deserializer des){
		des.getInt(); // get the version
		String type = des.getString();
		
		int numberRules = des.getInt();
		List<FilterRule> list = new LinkedList<FilterRule>();
		for (int i=0; i<numberRules; i++)list.add(FilterRule.create(des));
		FilterRule[] rules = new FilterRule[list.size()];
		list.toArray(rules);

		return create(type, rules);
	}

	/**
	 * Answer a new filter group.
	 * 
	 * @param exclude if true an exclusion group is returned, otherwise an inclusion group is returned.
	 */
	public static FilterGroup create(boolean exclude, FilterRule[] rules){
		if (exclude) return new FilterExcludeGroup(rules);
		return new FilterIncludeGroup(rules);
	}

	/**
	 * Answer true if this is a supported type of group.
	 * @param name Type of group that is being tested.
	 * @return
	 */
	public static boolean isKnownName(String name) {
		if (ExtensionConstants.include.equals(name))return true;
		if (ExtensionConstants.exclude.equals(name))return true;
		return false;
	}

	
	private FilterGroup(FilterRule[] rules){
		_rules = rules;
	}
		
	/**
	 * The rules in the group.
	 */
	public final FilterRule[] getRules(){
		FilterRule[] rules = new FilterRule[_rules.length];
		System.arraycopy(_rules, 0, rules, 0, _rules.length);
		return rules;
	}
		
	/**
	 * Answer the internal type of group, e.g. "include" or "exclude".
	 */
	public abstract String getType();
	
	/** Answer the type as a type that can be displayed to a user, that is it has been localized. */
	public abstract String getDisplayableType();
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
	
	public static final class FilterIncludeGroup extends FilterGroup {
		
		private FilterIncludeGroup(FilterRule[] rules){
			super(rules);
		}

		public String getType() {
			return ExtensionConstants.include;
		}
		
		public String getDisplayableType() {
			return ValMessages.GroupInclude;
		}
		
		public boolean isInclude() {
			return true;
		}
		
	}
	
	
	public static final class FilterExcludeGroup extends FilterGroup {
		
		private FilterExcludeGroup(FilterRule[] rules){
			super(rules);
		}
		public String getType() {
			return ExtensionConstants.exclude;
		}
		
		public String getDisplayableType() {
			return ValMessages.GroupExclude;
		}
		
		public boolean isExclude() {
			return true;
		}		
	}
		
	/**
	 * Save your settings into the serializer.
	 * @param ser
	 */
	public void save(Serializer ser){
		ser.put(SerializationVersion);
		ser.put(getType());
		ser.put(_rules.length);
		for (FilterRule rule : _rules)rule.save(ser);		
	}

	/**
	 * Answer whether or not we should validate the resource based on the filters in this group.
	 * 
	 * @param project the project that is being validated.
	 * @param resource the resource that is being validated. This can be null, in which case
	 * only the project level checks are performed.
	 */
	public boolean shouldValidate(IProject project, IResource resource, ContentTypeWrapper contentTypeWrapper) {
		boolean exclude = isExclude();
		boolean include = isInclude();
		int count = 0;
		for (FilterRule rule : _rules){
			if (resource != null){
				Boolean match = rule.matchesResource(resource, contentTypeWrapper);
				if (match != null)count++;
				if (exclude && match != null && match)return false;
				if (include && match != null && match)return true;
			}
			
			Boolean match = rule.matchesProject(project);
			if (match != null)count++;
			if (exclude && match != null && match)return false;
			if (include && match != null && match)return true;
		}
		if (exclude)return true;
		if (count == 0)return true;
		return false;
	}

	/** 
	 * Answer true if this is an inclusion filter, that is at least one of the rules must
	 * match in order to validate the resource.
	 */
	public boolean isInclude() {
		return false;
	}

	/**
	 * Answer true if this is an exclusion filter, that is if any of the rules match the 
	 * resource is not validated.
	 */
	public boolean isExclude() {
		return false;
	}
	
	public int hashCodeForConfig() {
		int h = 0;
		if (isExclude())h += 13;
		for (FilterRule fr : _rules)h += fr.hashCodeForConfig();
		return h;
	}

	/**
	 * Create a new group by adding a rule to an existing group.
	 * @param baseGroup The group that holds the existing rules.
	 * @param rule The new rule that is being added
	 * @return
	 */
	public static FilterGroup addRule(FilterGroup baseGroup, FilterRule rule) {
		List<FilterRule> list = new LinkedList<FilterRule>();
		for (FilterRule r : baseGroup._rules)list.add(r);
		list.add(rule);
		
		FilterRule[] rules = new FilterRule[list.size()];
		list.toArray(rules);
		return FilterGroup.create(baseGroup.isExclude(), rules);
	}

	/**
	 * Create a new group by removing a rule from an existing group.
	 * @param baseGroup The group that holds the existing rules.
	 * @param rule The rule that is being removed
	 * @return
	 */
	public static FilterGroup removeRule(FilterGroup baseGroup,	FilterRule rule) {
		List<FilterRule> list = new LinkedList<FilterRule>();
		for (FilterRule r : baseGroup._rules){
			if (!r.equals(rule))list.add(r);
		}
		
		FilterRule[] rules = new FilterRule[list.size()];
		list.toArray(rules);
		return FilterGroup.create(baseGroup.isExclude(), rules);
	}	

}
