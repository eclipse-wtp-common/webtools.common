package org.eclipse.wst.validation.internal.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.validation.internal.ExtensionConstants;
import org.eclipse.wst.validation.internal.ValMessages;

public abstract class FilterGroup implements IAdaptable {
	
	/** A list of FilterRule's for this group. */
	List<FilterRule> _rules = new LinkedList<FilterRule>();
	FilterRule[] _rulesArray;

	/**
	 * Answer a filter group based on the type of the group.
	 * 
	 * @param name either "include" or "exclude"
	 * 
	 * @return null if the name parameter isn't correct.
	 */
	public static FilterGroup create(String name) {
		if (ExtensionConstants.include.equals(name))return new FilterIncludeGroup();
		if (ExtensionConstants.exclude.equals(name))return new FilterExcludeGroup();
		return null;
	}
	
	/**
	 * Answer a new filter group.
	 * 
	 * @param exclude if true an exclusion group is returned, otherwise an inclusion group is returned.
	 */
	public static FilterGroup create(boolean exclude){
		if (exclude) return new FilterExcludeGroup();
		return new FilterIncludeGroup();
	}

	public void add(FilterRule fr) {
		_rulesArray = null;
		_rules.add(fr);
	}
	
	/**
	 * If you can, remove this rule from yourself.
	 * 
	 * @param fr the rule that is being removed
	 * 
	 * @return true if the rule was removed, and false if it was not. If you didn't include the rule in the
	 * first place, false would be returned.
	 */
	public synchronized boolean remove(FilterRule fr){
		if (_rules.remove(fr)){
			_rulesArray =  null;
			return true;
		}
		return false;
	}
	
	public FilterRule[] getRules(){
		FilterRule[] rules = _rulesArray;
		if (rules == null){
			rules = new FilterRule[_rules.size()];
			_rules.toArray(rules);
			_rulesArray = rules;
		}
		return rules;
	}
	
	public abstract String getType();
	
	/** Answer the type as a type that can be displayed to a user, that is it has been localized. */
	public abstract String getDisplayableType();
	
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
	
	public static class FilterIncludeGroup extends FilterGroup {

		public String getType() {
			return ValMessages.TypeInclude;
		}
		
		public String getDisplayableType() {
			return ValMessages.GroupInclude;
		}
		
		protected boolean isInclude() {
			return true;
		}
		
		protected FilterGroup create() {
			return new FilterIncludeGroup();
		}
		
	}
	
	
	public static class FilterExcludeGroup extends FilterGroup {
		public String getType() {
			return ValMessages.TypeExclude;
		}
		
		protected FilterGroup create() {
			return new FilterExcludeGroup();
		}
		
		public String getDisplayableType() {
			return ValMessages.GroupExclude;
		}
		
		protected boolean isExclude() {
			return true;
		}
		
	}

	/**
	 * Answer whether or not we should validate the resource based on the filters in this group.
	 * 
	 * @param project the project that is being validated.
	 * @param resource the resource that is being validated. This can be null, in which case
	 * only the project level checks are performed.
	 */
	public boolean shouldValidate(IProject project, IResource resource) {
		FilterRule[] rules = getRules();
		boolean exclude = isExclude();
		boolean include = isInclude();
		for (FilterRule rule : rules){
			if (resource != null){
				Boolean match = rule.matchesResource(resource);
				if (exclude && match != null && match)return false;
				if (include && match != null && match)return true;
			}
			
			Boolean match = rule.matchesProject(project);
			if (exclude && match != null && match)return false;
			if (include && match != null && match)return true;
		}
		if (exclude)return true;
		return false;
	}

	/** 
	 * Answer true if this is an inclusion filter, that is at least one of the rules must
	 * match in order to validate the resource.
	 */
	protected boolean isInclude() {
		return false;
	}

	/**
	 * Answer true if this is an exclusion filter, that is if any of the rules match the 
	 * resource is not validated.
	 */
	protected boolean isExclude() {
		return false;
	}
	
	protected abstract FilterGroup create();

	/** Answer a deep copy of yourself. */
	public FilterGroup copy() {
		FilterGroup fg = create();
		FilterRule[] rules = getRules();
		fg._rulesArray = new FilterRule[rules.length];
		for (int i=0; i<rules.length; i++){
			fg._rulesArray[i] = rules[i].copy();
			fg._rules.add(fg._rulesArray[i]);
		}
		return fg;
	}
	

}
