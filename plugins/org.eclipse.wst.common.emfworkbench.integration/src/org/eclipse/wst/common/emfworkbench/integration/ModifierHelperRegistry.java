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
/*
 * Created on Apr 27, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.emfworkbench.integration;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.ibm.wtp.common.logger.proxy.Logger;

/**
 * @author jsholl
 *  
 */
public class ModifierHelperRegistry {
	private static final String PLUGIN_ID = "org.eclipse.wst.common.emfworkbench.integration"; //$NON-NLS-1$
	private static final String EXTENSION_POINT = "ModifierHelperFactory"; //$NON-NLS-1$
	private static final String FACTORY_CLASS = "class"; //$NON-NLS-1$
	private static final String PACKAGE = "package"; //$NON-NLS-1$
	private static final String PACKAGE_URI = "uri"; //$NON-NLS-1$
	private static final String NAME = "name"; //$NON-NLS-1$
	private static final String TYPE = "type"; //$NON-NLS-1$
	private static final String FEATURE = "feature"; //$NON-NLS-1$
	private static final String FEATURE_ACTION = "action"; //$NON-NLS-1$
	private static final String FEATURE_ACTION_SET = "set"; //$NON-NLS-1$
	private static final String FEATURE_ACTION_UNSET = "unset"; //$NON-NLS-1$
	private static final String FEATURE_ACTION_BOTH = "both"; //default //$NON-NLS-1$
	private static ModifierHelperRegistry INSTANCE = null;
	// Hashtable mapping features to a list of FactoryHolders
	private Hashtable featureHash = new Hashtable();
	private Hashtable factoryHash = new Hashtable();

	private class FactoryHolder {
		private int actionType;
		private IConfigurationElement element;

		public FactoryHolder(IConfigurationElement element, int actionType) {
			this.element = element;
			this.actionType = actionType;
		}

		public ModifierHelperFactory getFactory(int actionTypeArg) {
			if (this.actionType == actionTypeArg || this.actionType == ModifierHelper.ACTION_BOTH) {
				String hashKey = getFactoryHash(element);
				ModifierHelperFactory factory = (ModifierHelperFactory) factoryHash.get(hashKey);
				if (null == factory) {
					try {
						factory = (ModifierHelperFactory) element.createExecutableExtension(FACTORY_CLASS);
						factoryHash.put(hashKey, factory);
					} catch (CoreException e) {
						Logger.getLogger().logError(e);
					}
				}
				return factory;
			}
			return null;
		}

		public boolean equals(Object obj) {
			if (super.equals(obj)) {
				return true;
			}
			FactoryHolder holder = (FactoryHolder) obj;
			return getFactoryHash(element).equals(getFactoryHash(holder.element));
		}
	}

	private ModifierHelperRegistry() {
		readExtensions();
	}

	private void readExtensions() {
		IExtensionPoint point = Platform.getPluginRegistry().getExtensionPoint(PLUGIN_ID, EXTENSION_POINT);
		if (point == null)
			return;
		IConfigurationElement[] elements = point.getConfigurationElements();
		for (int i = 0; i < elements.length; i++) {
			readFactory(elements[i]);
		}
	}

	private void readFactory(IConfigurationElement element) {
		String factoryClassName = element.getAttribute(FACTORY_CLASS);
		if (null == factoryClassName) {
			logError(element, "No " + FACTORY_CLASS + " defined."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		IConfigurationElement[] packages = element.getChildren(PACKAGE);
		if (packages.length == 0) {
			logError(element, "No " + PACKAGE + " defined."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		for (int j = 0; j < packages.length; j++) {
			readPackage(element, packages[j]);
		}
	}

	private void readPackage(IConfigurationElement factoryElement, IConfigurationElement element) {
		String packageURI = element.getAttribute(PACKAGE_URI);
		if (null == packageURI) {
			logError(element, "No " + PACKAGE_URI + " defined."); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageURI);
		if (null == ePackage) {
			logError(element, PACKAGE + " " + packageURI + " can not be found."); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		IConfigurationElement[] types = element.getChildren(TYPE);
		if (types.length == 0) {
			logError(element, "No " + TYPE + " defined."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		for (int i = 0; i < types.length; i++) {
			readType(factoryElement, ePackage, types[i]);
		}
	}

	private void readType(IConfigurationElement factoryElement, EPackage ePackage, IConfigurationElement element) {
		String typeName = element.getAttribute(NAME);
		if (null == typeName) {
			logError(element, "No " + NAME + " defined."); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		EClassifier eClassifier = ePackage.getEClassifier(typeName);
		if (null == eClassifier) {
			logError(element, TYPE + " " + typeName + " can not be found in " + PACKAGE + " " + ePackage.getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return;
		}
		EClass eClass = (EClass) eClassifier;
		IConfigurationElement[] features = element.getChildren(FEATURE);
		if (features.length == 0) {
			logError(element, "No " + FEATURE + " defined."); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		for (int i = 0; i < features.length; i++) {
			readFeature(factoryElement, eClass, features[i]);
		}
	}

	private void readFeature(IConfigurationElement factoryElement, EClass eClass, IConfigurationElement element) {
		String featureName = element.getAttribute(NAME);
		if (null == featureName) {
			logError(element, "No " + NAME + " defined."); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		String action = element.getAttribute(FEATURE_ACTION);
		if (null == action) {
			action = FEATURE_ACTION_BOTH;
		}
		int actionType = -1;
		if (action.equalsIgnoreCase(FEATURE_ACTION_BOTH)) {
			actionType = ModifierHelper.ACTION_BOTH;
		} else if (action.equalsIgnoreCase(FEATURE_ACTION_SET)) {
			actionType = ModifierHelper.ACTION_SET;
		} else if (action.equalsIgnoreCase(FEATURE_ACTION_UNSET)) {
			actionType = ModifierHelper.ACTION_UNSET;
		}
		if (actionType == -1) {
			logError(element, "Invalid " + FEATURE_ACTION + "=" + action + " defined.  Valid values are: " + FEATURE_ACTION_BOTH + ", " + FEATURE_ACTION_SET + ", or " + FEATURE_ACTION_UNSET); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			return;
		}
		EStructuralFeature feature = null;
		EList allFeatures = eClass.getEAllStructuralFeatures();
		EStructuralFeature tempFeature = null;
		Iterator iterator = allFeatures.iterator();
		while (null == feature && iterator.hasNext()) {
			tempFeature = (EStructuralFeature) iterator.next();
			if (tempFeature.getName().equals(featureName)) {
				feature = tempFeature;
			}
		}
		if (feature == null) {
			logError(element, FEATURE + " " + featureName + " can not be found in " + TYPE + " " + eClass.getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return;
		}
		List factoryHolderList = (List) featureHash.get(feature);
		if (null == factoryHolderList) {
			factoryHolderList = new ArrayList();
			featureHash.put(feature, factoryHolderList);
		}
		FactoryHolder factoryHolder = new FactoryHolder(factoryElement, actionType);
		if (factoryHolderList.contains(factoryHolder)) {
			logError(element, "Duplicate" + FEATURE + ":" + featureName + " defined for " + FACTORY_CLASS + ":" + factoryElement.getAttribute(FACTORY_CLASS)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			return;
		}
		factoryHolderList.add(factoryHolder);
	}

	private String getFactoryHash(IConfigurationElement factoryElement) {
		return factoryElement.getDeclaringExtension().getDeclaringPluginDescriptor().getUniqueIdentifier() + factoryElement.getAttribute(FACTORY_CLASS);
	}

	public static void logError(IConfigurationElement element, String text) {
		IExtension extension = element.getDeclaringExtension();
		IPluginDescriptor descriptor = extension.getDeclaringPluginDescriptor();
		StringBuffer buf = new StringBuffer();
		buf.append("Plugin " + descriptor.getUniqueIdentifier() + ", extension " + extension.getExtensionPointUniqueIdentifier()); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\n" + text); //$NON-NLS-1$
		Logger.getLogger().logError(buf.toString());
	}

	public static ModifierHelperRegistry getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new ModifierHelperRegistry();
		}
		return INSTANCE;
	}

	/**
	 * returns a list of ModifierHelpers
	 * 
	 * @param baseHelper
	 * @param actionFlag
	 * @return
	 */
	public List getHelpers(ModifierHelper baseHelper) {
		int actionFlag = baseHelper.shouldUnsetValue() ? ModifierHelper.ACTION_UNSET : ModifierHelper.ACTION_SET;
		EStructuralFeature feature = baseHelper.getFeature();
		List factoryList = getFactories(feature, actionFlag);
		if (null == factoryList) {
			return null;
		}
		ArrayList helpers = new ArrayList();
		Iterator it = factoryList.iterator();
		ModifierHelperFactory factory = null;
		while (it.hasNext()) {
			factory = (ModifierHelperFactory) it.next();
			Object helper = factory.getHelper(baseHelper, actionFlag);
			if (null != helper) {
				helpers.add(helper);
			}
		}
		return helpers;
	}

	private List getFactories(EStructuralFeature feature, int actionFlag) {
		List factoryHolderList = (List) featureHash.get(feature);
		if (null == factoryHolderList) {
			return null;
		}
		List factoryList = new ArrayList();
		ModifierHelperFactory factory = null;
		for (int i = 0; i < factoryHolderList.size(); i++) {
			factory = ((FactoryHolder) factoryHolderList.get(i)).getFactory(actionFlag);
			if (null != factory) {
				factoryList.add(factory);
			}
		}
		return factoryList;
	}
}