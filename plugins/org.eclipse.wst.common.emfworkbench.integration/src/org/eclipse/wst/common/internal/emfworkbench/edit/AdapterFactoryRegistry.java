/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.edit;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.wst.common.internal.emfworkbench.integration.EMFWorkbenchEditPlugin;

import org.eclipse.jem.util.RegistryReader;
import org.eclipse.jem.util.logger.proxy.Logger;

/**
 * @author mdelder
 */
public class AdapterFactoryRegistry extends RegistryReader {

	public static final String ADAPTER_FACTORY = "adapterFactory"; //$NON-NLS-1$

	public static final String PACKAGE_URI = "packageURI"; //$NON-NLS-1$

	public static final String CLASS_NAME = "className"; //$NON-NLS-1$

	public static final String VIEW = "view"; //$NON-NLS-1$

	public static final String ID = "id"; //$NON-NLS-1$

	private Map descriptorMap = null;

	private static AdapterFactoryRegistry instance;

	private AdapterFactoryRegistry() {
		super(EMFWorkbenchEditPlugin.ID, EMFWorkbenchEditPlugin.ADAPTER_FACTORY_REGISTRY_EXTENSION_POINT);
	}

	public List getDescriptors(EPackage pkg, String viewID) {
		Collection all = getDescriptors(pkg);
		if (all == null)
			return null;

		Iterator iter = all.iterator();
		AdapterFactoryDescriptor desc = null;
		List result = new ArrayList(all.size());
		while (iter.hasNext()) {
			desc = (AdapterFactoryDescriptor) iter.next();
			if (desc.appliesTo(viewID))
				result.add(desc);
		}
		return result;
	}

	public Collection getDescriptors(EPackage registeredPackage) {
		return (Collection) getDescriptorMap().get(registeredPackage.getNsURI());
	}

	private Map getDescriptorMap() {
		if (descriptorMap == null)
			descriptorMap = new HashMap();
		return descriptorMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
	 */
	public boolean readElement(IConfigurationElement element) {
		try {
			if (element.getName().equals(ADAPTER_FACTORY)) {
				AdapterFactoryDescriptor descriptor = new AdapterFactoryDescriptor(element);
				mapDescriptor(descriptor);
				return true;
			}
		} catch (RuntimeException re) {
			Logger.getLogger().logError(re);
		}
		return false;
	}

	private void mapDescriptor(AdapterFactoryDescriptor descriptor) {
		String uri = descriptor.getPackageURI();
		Collection descriptors = (Collection) getDescriptorMap().get(uri);
		if (descriptors == null) {
			descriptors = new TreeSet();
			getDescriptorMap().put(uri, descriptors);
		}
		descriptors.add(descriptor);
	}

	/**
	 * @return Returns the instance.
	 */
	public static AdapterFactoryRegistry instance() {
		if (instance == null) {
			instance = new AdapterFactoryRegistry();
			instance.readRegistry();
		}
		return instance;
	}

}