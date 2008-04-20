/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.IValidatorGroupListener;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * Process the validator group (for use with validator version 2 only) extension point.
 * 
 * @author nitin
 * 
 */
public class ValidatorGroupExtensionReader {

	private static final String DOT = "."; //$NON-NLS-1$
	private static ValidatorGroupExtensionReader _instance;

	public static ValidatorGroupExtensionReader getDefault() {
		if (_instance == null)_instance = new ValidatorGroupExtensionReader();
		return _instance;
	}

	/**
	 * Map of group IDs to configuration elements
	 */
	private Map<String, List<IConfigurationElement>> _map;
	
	/**
	 * Map of group ID's to resolved configuration elements.
	 */
	private Map<String, IValidatorGroupListener[]> _resolved;

	private ValidatorGroupExtensionReader() {
		init();
	}

	/**
	 * Answer the listeners with this group id.
	 * @param groupID
	 * @return an empty array there are no listeners for this group id.
	 */
	IValidatorGroupListener[] createListeners(String groupID) throws CoreException {
		IValidatorGroupListener[] result = _resolved.get(groupID);
		if (result != null)return result;
				
		List<IConfigurationElement> elements = _map.get(groupID);
		if (elements == null){
			_resolved.put(groupID, new IValidatorGroupListener[0]);
			String msg = NLS.bind("Configuration error, there is no validation listener group with id: {0}", groupID); //$NON-NLS-1$
			Status status = new Status(IStatus.ERROR, ValidationPlugin.PLUGIN_ID, msg);
			throw new CoreException(status);
		}
		
		List<IValidatorGroupListener> listeners = new ArrayList<IValidatorGroupListener>(elements.size());
		for (IConfigurationElement element : elements) {
			IValidatorGroupListener listener;
			try {
				listener = (IValidatorGroupListener) element.createExecutableExtension(ExtensionConstants.Group.attClass);
				listeners.add(listener);
			}
			catch (Exception e) {
				ValidationPlugin.getPlugin().handleException(e);
				listeners = new ArrayList<IValidatorGroupListener>();
			}
		}

		result = listeners.toArray(new IValidatorGroupListener[listeners.size()]);
		_resolved.put(groupID, result);
		return result;
	}

	/**
	 * Answer the extension point for the validatorGroups.
	 * 
	 * @return null if there is a problem or no extensions.
	 */
	private IExtensionPoint getExtensionPoint() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		return registry.getExtensionPoint(ValidationPlugin.PLUGIN_ID, ExtensionConstants.group);
	}

	private void init() {
		_map = new HashMap<String, List<IConfigurationElement>>(4);
		_resolved = new HashMap<String, IValidatorGroupListener[]>(4);

		IExtensionPoint extensionPoint = getExtensionPoint();
		if (extensionPoint != null) {
			for (IExtension ext : extensionPoint.getExtensions()) {
				for (IConfigurationElement group : ext.getConfigurationElements()) {
					processGroupElement(group);
				}
			}
		}
	}

	private void processGroupElement(IConfigurationElement element) {
		if (!ExtensionConstants.Group.elementGroup.equals(element.getName()))return;
		
		String id = element.getAttribute(ExtensionConstants.Group.attId);
		if (id == null)throw new IllegalStateException("Configuration error, the " +  //$NON-NLS-1$
			ExtensionConstants.Group.attId + " is required"); //$NON-NLS-1$
		// force the use of a qualified ID
		if (id.indexOf(DOT) < 0) {
			id = element.getContributor().getName() + DOT + id;
		}
		IConfigurationElement[] newElements = element.getChildren(ExtensionConstants.Group.elementListener);
		if (newElements.length > 0) {
			List<IConfigurationElement> elements = _map.get(id);
			if (elements == null) {
				elements = new ArrayList<IConfigurationElement>();
				_map.put(id, elements);
			}
			elements.addAll(Arrays.asList(newElements));
		}
	}
}
