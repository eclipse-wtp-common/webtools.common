/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

public class DataModelExtensionReader extends RegistryReader {

	private static final String EXTENSION = "DataModelProviderExtension"; //$NON-NLS-1$

	private static final String PROVIDER_ELEMENT = "DataModelProvider"; //$NON-NLS-1$
	private static final String DEFINES_TYPE_ELEMENT = "ProviderDefinesType"; //$NON-NLS-1$
	private static final String IMPLEMENTS_TYPE_ELEMENT = "ProviderImplementsType"; //$NON-NLS-1$

	private static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
	private static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTRIBUTE_PROVIDER_TYPE = "providerType"; //$NON-NLS-1$
	private static final String ATTRIBUTE_PROVIDER_ID = "providerID"; //$NON-NLS-1$
//	private static final String ATTRIBUTE_FG = "functionGroupID"; //$NON-NLS-1$

	private HashMap providerExtensions;
	private HashMap definesExtensions;
	private HashMap implementsExtensions;

	private boolean hasInitialized = false;

	public DataModelExtensionReader() {
		super(WTPCommonPlugin.PLUGIN_ID, EXTENSION);
	}

	public boolean readElement(IConfigurationElement element) {
		if (element.getName().equals(PROVIDER_ELEMENT)) {
			String id = element.getAttribute(ATTRIBUTE_ID);
			if (null == id || id.trim().length() == 0) {
				Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + PROVIDER_ELEMENT + " is missing " + ATTRIBUTE_ID)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			String className = element.getAttribute(ATTRIBUTE_CLASS);
			if (null == className || className.trim().length() == 0) {
				Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + PROVIDER_ELEMENT + " is missing " + ATTRIBUTE_CLASS)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			addProviderExtension(id, element);
		} else if (element.getName().equals(DEFINES_TYPE_ELEMENT)) {
			String type = element.getAttribute(ATTRIBUTE_PROVIDER_TYPE);
			if (null == type || type.trim().length() == 0) {
				Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + DEFINES_TYPE_ELEMENT + " is missing " + ATTRIBUTE_PROVIDER_TYPE)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			String id = element.getAttribute(ATTRIBUTE_PROVIDER_ID);
			if (null == id || id.trim().length() == 0) {
				Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + DEFINES_TYPE_ELEMENT + " is missing " + ATTRIBUTE_PROVIDER_ID)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			addDefinesExtension(type, id);
		} else if (element.getName().equals(IMPLEMENTS_TYPE_ELEMENT)) {
			String type = element.getAttribute(ATTRIBUTE_PROVIDER_TYPE);
			if (null == type || type.trim().length() == 0) {
				Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + DEFINES_TYPE_ELEMENT + " is missing " + ATTRIBUTE_PROVIDER_TYPE)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			String id = element.getAttribute(ATTRIBUTE_PROVIDER_ID);
			if (null == id || id.trim().length() == 0) {
				Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + DEFINES_TYPE_ELEMENT + " is missing " + ATTRIBUTE_PROVIDER_ID)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			addImplementsExtension(type, id);
		}
		return true;
	}

	private void addProviderExtension(String id, IConfigurationElement element) {
		if (providerExtensions.containsKey(id)) {
			Logger.getLogger().logError(new RuntimeException("Duplicate " + PROVIDER_ELEMENT + " " + ATTRIBUTE_ID + " " + id)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		providerExtensions.put(id, element);
	}

	private void addDefinesExtension(String type, String id) {
		if (definesExtensions.containsKey(type)) {
			Logger.getLogger().logError(new RuntimeException("Duplicate " + PROVIDER_ELEMENT + " " + ATTRIBUTE_PROVIDER_TYPE + " " + type)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		definesExtensions.put(type, id);
	}

	private void addImplementsExtension(String type, String id) {
		List cache;
		if (implementsExtensions.containsKey(type))
			cache = (List) implementsExtensions.get(type);
		else
			cache = new ArrayList();
		cache.add(id);
		implementsExtensions.put(type, cache);
	}

	protected IConfigurationElement getProviderExtension(String id) {
		readRegistryIfNecessary();
		IConfigurationElement element = (IConfigurationElement) providerExtensions.get(id);
		if (null == element) {
			Logger.getLogger().log(new RuntimeException("Extension:" + EXTENSION + " Element:" + PROVIDER_ELEMENT + " not found for " + ATTRIBUTE_ID + ": " + id)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		return element;
	}

	protected String getDefinesExtension(String providerType) {
		readRegistryIfNecessary();
		String element = (String) definesExtensions.get(providerType);
		if (null == element) {
			Logger.getLogger().log(new RuntimeException("Extension:" + EXTENSION + " Element:" + DEFINES_TYPE_ELEMENT + " not found for " + ATTRIBUTE_PROVIDER_TYPE + ": " + providerType)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		return element;
	}

	public String[] getProviderDescriptorsForProviderKind(String providerType) {
		readRegistryIfNecessary();
		List providerList = new ArrayList();
		providerList.add(getDefinesExtension(providerType));
		if (implementsExtensions.containsKey(providerType)) {
			List implementsIds = (List) implementsExtensions.get(providerType);
			if (implementsIds != null && !implementsIds.isEmpty()) {
				providerList.addAll(implementsIds);
			}
		}
		String[] providerArray = new String[providerList.size()];
		for (int i = 0; i < providerArray.length; i++) {
			providerArray[i] = (String) providerList.get(i);
		}
		return providerArray;
	}

	private void readRegistryIfNecessary() {
		if (!hasInitialized) {
			providerExtensions = new HashMap();
			definesExtensions = new HashMap();
			implementsExtensions = new HashMap();
			readRegistry();
			hasInitialized = true;
		}
	}

	public IDataModelProvider getProvider(String id) {
		IDataModelProvider provider = null;
		IConfigurationElement element = getProviderExtension(id);
		if (element == null)
			return null;
		try {
			provider = (IDataModelProvider) element.createExecutableExtension(ATTRIBUTE_CLASS);
		} catch (CoreException e) {
			Logger.getLogger().logError(e);
		}
		return provider;
	}

}