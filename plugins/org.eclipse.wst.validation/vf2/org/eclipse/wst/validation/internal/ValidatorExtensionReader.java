/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.MessageSeveritySetting;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.model.FilterGroup;
import org.eclipse.wst.validation.internal.model.FilterRule;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * Process the validator (version 2) extension point.
 * 
 * @author karasiuk
 *
 */
public class ValidatorExtensionReader {
	
	private static ValidatorExtensionReader _me;
	
	public static ValidatorExtensionReader getDefault(){
		if (_me == null)_me = new ValidatorExtensionReader();
		return _me;
	}
	
	private ValidatorExtensionReader(){}
	
	/**
	 * Read the extensions.
	 */
	List<Validator> process() {
		List<Validator> list = new LinkedList<Validator>();
		IExtensionPoint extensionPoint = getExtensionPoint();
		if (extensionPoint == null)return list;
				
		for (IExtension ext : extensionPoint.getExtensions()){
			for (IConfigurationElement validator : ext.getConfigurationElements()){
				Validator v = processValidator(validator, ext.getUniqueIdentifier(), ext.getLabel(), null);
				if (v != null)list.add(v);
			}
		}
		return list;
		
	}
	
	/**
	 * Process the validator element in a validator extension.
	 * 
	 * @param validator
	 *            The validator element.
	 * 
	 * @param deep
	 *            If true load all the configuration elements for each
	 *            validator, if false do a shallow load, where only the
	 *            validator class, id and name's are loaded.
	 * 
	 * @param project
	 *            The project that you are defined in. This can be null which
	 *            means that you are a global validator.
	 * 
	 * @return a configured validator or null if there was an error.
	 */
	private Validator processValidator(IConfigurationElement validator, String id, String label, IProject project) {
		Validator.V2 v = null;
		try {
			v = Validator.create(validator, project).asV2Validator();
			v.setLevel(Validator.Level.Extension);
			v.setId(id);
			v.setName(label);
			v.setBuildValidation(getAttribute(validator, ExtensionConstants.build, true));
			v.setManualValidation(getAttribute(validator, ExtensionConstants.manual, true));
			v.setMarkerId(validator.getAttribute(ExtensionConstants.markerId));
			v.setVersion(getAttribute(validator, ExtensionConstants.version, 1));
			v.setSourceId(validator.getAttribute(ExtensionConstants.sourceId));
			IConfigurationElement[] children = validator.getChildren();
			for (int i=0; i<children.length; i++)processValidatorChildren(v, children[i]);
		}
		catch (Exception e){
			ValidationPlugin.getPlugin().handleException(e);
			IContributor contrib = validator.getContributor();
			String message = NLS.bind(ValMessages.ErrConfig, contrib.getName());
			ValidationPlugin.getPlugin().logMessage(IStatus.ERROR, message);
		}
		return v;
	}
	
	/**
	 * Answer all the messages that this validator has defined.
	 * @param v
	 * @return an empty list if the validator did not define any messages.
	 */
	public List<MessageSeveritySetting> addMessages(Validator v){
		List<MessageSeveritySetting> list = new LinkedList<MessageSeveritySetting>();
		IExtensionPoint extensionPoint = getExtensionPoint();
		if (extensionPoint == null)return list;
		IExtension ext = extensionPoint.getExtension(v.getId());
		if (ext == null)return list;
		
		for (IConfigurationElement elem : ext.getConfigurationElements()){
			for (IConfigurationElement ce : elem.getChildren(ExtensionConstants.MessageCategory.name)){
				list.add(processMessage(ce));
			}
		}

		return list;
	}

	/**
	 * Answer the extension point for the validators.
	 * 
	 * @return null if there is a problem or no extensions.
	 */
	private IExtensionPoint getExtensionPoint() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		return registry.getExtensionPoint(ValidationPlugin.PLUGIN_ID, ExtensionConstants.validator);
	}
	

	/**
	 * Process a message element for the validator, by creating a MessageCategory for it.
	 * 
	 * @param ce a MessageCategory element.
	 */
	private MessageSeveritySetting processMessage(IConfigurationElement ce) {
		String s = ce.getAttribute(ExtensionConstants.MessageCategory.severity);
		MessageSeveritySetting.Severity sev = null;
		if (ExtensionConstants.MessageCategory.sevError.equals(s))sev = MessageSeveritySetting.Severity.Error;
		else if (ExtensionConstants.MessageCategory.sevWarning.equals(s))sev = MessageSeveritySetting.Severity.Warning;
		else if (ExtensionConstants.MessageCategory.sevIgnore.equals(s))sev = MessageSeveritySetting.Severity.Ignore;
		
		return new MessageSeveritySetting(ce.getAttribute(ExtensionConstants.MessageCategory.id), 
			ce.getAttribute(ExtensionConstants.MessageCategory.label), sev);		
	}

	/** 
	 * Process the children of the validator tag, i.e. include and exclude groups.
	 * 
	 *  @param v the validator that we are building up
	 *  @param group the include and exclude elements
	 */
	private void processValidatorChildren(Validator.V2 v, IConfigurationElement group) {
		FilterGroup fg = FilterGroup.create(group.getName());
		if (fg == null)return;			
		
		IConfigurationElement[] rules = group.getChildren(ExtensionConstants.rules);
		// there should only be one
		for (int i=0; i<rules.length; i++){
			IConfigurationElement[] r = rules[i].getChildren();
			for(int j=0; j<r.length; j++){
				processRule(fg, r[j]);
			}
		}
		v.add(fg);
	}

	/**
	 * Process a rule in one of the rule groups.
	 * 
	 * @param fg the filter group that we are building up 
	 * @param rule a rule in the group, like fileext.
	 */
	private void processRule(FilterGroup fg, IConfigurationElement rule) {
		FilterRule fr = FilterRule.create(rule.getName());
		if (fr == null)throw new IllegalStateException(ValMessages.ErrFilterRule);
		fr.setData(rule);
		fg.add(fr);
	}
	
	/**
	 * Determine if any of the validators need to be migrated, and if so answer a new
	 * Validator array.
	 * 
	 * @param validators the existing validators (from the preferences).
	 *  
	 * @return null if no validators needed to be migrated.
	 */
	Validator[] migrate(Validator[] validators, IProject project) {
		int count = 0;
		Map<String, Validator> map = new HashMap<String, Validator>(validators.length);
		for (Validator v : validators)map.put(v.getId(), v);
		
		IExtensionPoint extensionPoint = getExtensionPoint();
		if (extensionPoint == null)return null;
				
		for (IExtension ext : extensionPoint.getExtensions()){
			for (IConfigurationElement validator : ext.getConfigurationElements()){
				Validator v = processValidator(validator, ext.getUniqueIdentifier(), ext.getLabel(), project);
				if (v == null)continue;
				Validator old = map.get(v.getId());
				if (old == null || old.getVersion() < v.getVersion()){
					//TODO we may be replacing user preferences, at some point we may want to do a real migration.
					map.put(v.getId(), v);
					count++;
				}
			}
		}
		
		if (count > 0){
			Validator[] vals = new Validator[map.size()];
			map.values().toArray(vals);
			return vals;
		}
		return null;
	}
	
	private boolean getAttribute(IConfigurationElement element, String name, boolean dft){
		String v = element.getAttribute(name);
		if (v == null)return dft;
		if ("true".equalsIgnoreCase(v))return true; //$NON-NLS-1$
		if ("false".equalsIgnoreCase(v))return false; //$NON-NLS-1$
		return dft;
	}
	
	private int getAttribute(IConfigurationElement element, String name, int dft){
		String v = element.getAttribute(name);
		if (v == null)return dft;
		try {
			return Integer.parseInt(v);
		}
		catch (Exception e){
			// eat it.
		}
		return dft;
	}
	
//	/**
//	 * This method is only used for debugging.
//	 * @param elem
//	 */
//	private static void dump(IConfigurationElement elem){
//		String name = elem.getName();
//		String[] attribs = elem.getAttributeNames();
//		String[] vals = new String[attribs.length];
//		for (int i=0; i<vals.length; i++)vals[i] = elem.getAttribute(attribs[i]);
//		String v = elem.getValue();
//		IConfigurationElement[] children = elem.getChildren();
//		for (int i=0; i<children.length; i++)dump(children[i]);
//	}
}
