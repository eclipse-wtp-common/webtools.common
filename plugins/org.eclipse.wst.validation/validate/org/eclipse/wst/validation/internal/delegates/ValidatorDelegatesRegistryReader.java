/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.validation.internal.delegates;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.internal.ValMessages;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * This class reads the plug-in extension registry and registers each delegating
 * validator descriptor with the delegates registry.
 * 
 * @see ValidatorDelegatesRegistry
 */
class ValidatorDelegatesRegistryReader {
	/**
	 * The delegate class attribute.
	 */
	static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

	/**
	 * The delegate element name.
	 */
	private static final String DELEGATE_ELEMENT = "delegate"; //$NON-NLS-1$

	/**
	 * The validator delegates extension point id.
	 */
	private static final String EXTENSION_POINT_ID = "validatorDelegates"; //$NON-NLS-1$

	/**
	 * The delegate name attribute.
	 */
	private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$

	private static final String PLUGIN_ID = "org.eclipse.wst.validation"; //$NON-NLS-1$

	/**
	 * The target id attribute name.
	 */
	private static final String TARGET_ATTRIBUTE = "target"; //$NON-NLS-1$

	/**
	 * The validator registry where the descriptors being read will be placed.
	 */
	private ValidatorDelegatesRegistry registry;

	/**
	 * Constructor.
	 * 
	 * @param registry
	 *            The registry where the descriptors being read will be placed.
	 */
	public ValidatorDelegatesRegistryReader(ValidatorDelegatesRegistry registry) {
		this.registry = registry;
	}

	/**
	 * Reads a configuration element.
	 * 
	 * @param element
	 *            The platform configuration element being read.
	 */
	private void readElement(IConfigurationElement element) {
		String elementName = element.getName();

		if (elementName.equals(DELEGATE_ELEMENT)) {
			String delegateID = element.getAttribute(CLASS_ATTRIBUTE);
			String delegateName = element.getAttribute(NAME_ATTRIBUTE);
			String targetValidatorID = element.getAttribute(TARGET_ATTRIBUTE);

			ValidatorDelegateDescriptor descriptor = new ValidatorDelegateDescriptor(
					delegateID, element, delegateName, targetValidatorID);

			registry.add(descriptor);
		}
	}

	/**
	 * Read from the extensions registry and parse it.
	 */
	void readRegistry() {
		IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
		IExtensionPoint point = pluginRegistry.getExtensionPoint(PLUGIN_ID,
				EXTENSION_POINT_ID);

		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (IConfigurationElement configurationElement : elements) {
				try {
					readElement(configurationElement);
				} catch (Exception e) {
					// we don't want all the validators to be rendered helpless
					// by some
					// rogue contribution, so, we catch any exception that
					// occurs during
					// initialization, log it, and continue on.
					IContributor contributor = configurationElement.getContributor();
					String msg = NLS.bind(ValMessages.RogueValidator, contributor);
					ValidationPlugin.getPlugin().logMessage(IStatus.ERROR, msg);
				}
			}
		}
	}
}
