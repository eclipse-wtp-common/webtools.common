/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.integration;


import java.util.HashMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.wst.common.internal.emf.utilities.FeatureValueConversionException;
import org.eclipse.wst.common.internal.emf.utilities.FeatureValueConverter;

/**
 * Insert the type's description here. Creation date: (4/6/2001 3:23:36 PM)
 * 
 * @author: Administrator
 */
public class ModifierHelper {

	private EObject owner;
	private ModifierHelper ownerHelper;
	private OwnerProvider ownerProvider;
	private Object value;
	private EStructuralFeature feature;
	private HashMap attributes;
	private boolean shouldUnsetValue = false;
	private String valueXSITypeName;
	private FeatureValueConverter featureValueConverter;

	public static final int ACTION_SET = 0;
	public static final int ACTION_UNSET = 1;
	public static final int ACTION_BOTH = 2;

	/**
	 * J2EEModifierHelper constructor comment.
	 */
	public ModifierHelper() {
		super();
	}

	/**
	 * J2EEModifierHelper constructor comment.
	 */
	public ModifierHelper(EObject anOwner, EStructuralFeature aFeature, Object aValue) {
		setOwner(anOwner);
		setFeature(aFeature);
		setValue(aValue);
	}

	/**
	 * J2EEModifierHelper constructor comment.
	 */
	public ModifierHelper(ModifierHelper anOwnerHelper, EStructuralFeature aFeature, Object aValue) {
		setOwnerHelper(anOwnerHelper);
		setFeature(aFeature);
		setValue(aValue);
	}

	/**
	 * J2EEModifierHelper constructor comment.
	 */
	public ModifierHelper(OwnerProvider anOwnerProvider, EStructuralFeature aFeature, Object aValue) {
		setOwnerProvider(anOwnerProvider);
		setFeature(aFeature);
		setValue(aValue);
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 3:28:16 PM)
	 * 
	 * @return java.util.HashMap
	 */
	public void addAttribute(EStructuralFeature aFeature, Object aValue) {
		if (aFeature != null && aValue != null)
			getAttributes().put(aFeature, aValue);
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 3:28:16 PM)
	 * 
	 * @param newValue
	 *            java.lang.Object
	 */
	protected Object convertValue(java.lang.Object newValue) {
		return getFeatureValueConverter().convertValue(newValue, getFeature());
	}

	//Calling this will cause the value to be removed or unset from the owner.
	public void doUnsetValue() {
		shouldUnsetValue = true;
		if (value != SetCommand.UNSET_VALUE && feature != null && !feature.isMany())
			primSetValue(SetCommand.UNSET_VALUE);
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 3:28:16 PM)
	 * 
	 * @return java.util.HashMap
	 */
	public java.util.HashMap getAttributes() {
		if (attributes == null)
			attributes = new HashMap();
		return attributes;
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 3:28:16 PM)
	 * 
	 * @return org.eclipse.emf.ecore.EFactory
	 */
	protected org.eclipse.emf.ecore.EFactory getFactory() {
		return getPackage().getEFactoryInstance();
	}

	protected EPackage getPackage() {
		return ((EClassImpl) getFeatureType()).getEPackage();
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 3:28:16 PM)
	 * 
	 * @return org.eclipse.emf.ecore.EStructuralFeature
	 */
	public org.eclipse.emf.ecore.EStructuralFeature getFeature() {
		return feature;
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 3:28:16 PM)
	 * 
	 * @return EObject
	 */
	protected EObject getFeatureType() {
		return getFeature().getEType();
	}

	/**
	 * Insert the method's description here. Creation date: (5/10/2001 4:51:58 PM)
	 * 
	 * @return com.ibm.etools.j2ee.commands.FeatureValueConverter
	 */
	public FeatureValueConverter getFeatureValueConverter() {
		if (featureValueConverter == null)
			featureValueConverter = FeatureValueConverter.DEFAULT;
		return featureValueConverter;
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 3:28:16 PM)
	 * 
	 * @return String
	 */
	protected String getNewValueTypeName() {
		if (getValueXSITypeName() != null && getValueXSITypeName().length() > 0)
			return getValueXSITypeName();
		return (((EClass) getFeatureType()).getName());
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 3:28:16 PM)
	 * 
	 * @return org.eclipse.emf.ecore.EObject
	 */
	public org.eclipse.emf.ecore.EObject getOwner() {
		if (owner == null) {
			OwnerProvider provider = getOwnerProvider();
			EObject providerOwner = null;
			if (provider != null)
				providerOwner = provider.getOwner();
			if (providerOwner == null && getOwnerHelper() != null)
				providerOwner = (EObject) getOwnerHelper().getValue();
			return providerOwner;
		}
		return owner;
	}

	/**
	 * Insert the method's description here. Creation date: (4/8/2001 2:47:54 PM)
	 * 
	 * @return com.ibm.etools.j2ee.commands.J2EEModifierHelper
	 */
	public ModifierHelper getOwnerHelper() {
		if (ownerHelper == null) {
			if (getOwnerProvider() != null && getOwnerProvider().getOwner() == null)
				return getOwnerProvider().getOwnerHelper();
		}
		return ownerHelper;
	}

	public ModifierHelper primGetOwnerHelper() {
		return ownerHelper;
	}

	/**
	 * Insert the method's description here. Creation date: (9/18/2001 1:31:14 PM)
	 * 
	 * @return com.ibm.etools.j2ee.ui.J2EEOwnerProvider
	 */
	public OwnerProvider getOwnerProvider() {
		return ownerProvider;
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 3:28:16 PM)
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.Object getValue() {
		return value;
	}

	/**
	 * Insert the method's description here. Creation date: (4/10/2001 3:39:31 PM)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getValueXSITypeName() {
		return valueXSITypeName;
	}

	/**
	 * This will automatically get called from the J2EEModelModifier before executing so it is not
	 * necessary to call it directly.
	 */
	public boolean isComplete() {
		boolean result = true;
		if (getOwnerHelper() != null)
			result = getOwnerHelper().isComplete();
		if (!mustCreateValue())
			result = getValue() != null || shouldUnsetValue();
		else
			result = getFeatureType() != null;
		return result && getFeature() != null && (getOwner() != null || getOwnerHelper() != null);
	}

	public boolean mustCreateValue() {
		return getValue() == null && getFeature() != null && !shouldUnsetValue();
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 3:28:16 PM)
	 * 
	 * @param newValue
	 *            java.lang.Object
	 */
	public void primSetValue(java.lang.Object newValue) {
		value = newValue;
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 3:28:16 PM)
	 * 
	 * @param newFeature
	 *            org.eclipse.emf.ecore.EStructuralFeature
	 */
	public void setFeature(org.eclipse.emf.ecore.EStructuralFeature newFeature) {
		feature = newFeature;
	}

	/**
	 * Insert the method's description here. Creation date: (5/10/2001 4:51:58 PM)
	 * 
	 * @param newFeatureValueConverter
	 *            com.ibm.etools.j2ee.commands.FeatureValueConverter
	 */
	public void setFeatureValueConverter(FeatureValueConverter newFeatureValueConverter) {
		featureValueConverter = newFeatureValueConverter;
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 3:28:16 PM)
	 * 
	 * @param newOwner
	 *            org.eclipse.emf.ecore.EObject
	 */
	public void setOwner(org.eclipse.emf.ecore.EObject newOwner) {
		owner = newOwner;
	}

	/**
	 * Insert the method's description here. Creation date: (4/8/2001 2:47:54 PM)
	 * 
	 * @param newOwnerHelper
	 *            com.ibm.etools.j2ee.commands.J2EEModifierHelper
	 */
	public void setOwnerHelper(ModifierHelper newOwnerHelper) {
		ownerHelper = newOwnerHelper;
	}

	/**
	 * Insert the method's description here. Creation date: (9/18/2001 1:31:14 PM)
	 * 
	 * @param newOwnerProvider
	 *            com.ibm.etools.j2ee.ui.J2EEOwnerProvider
	 */
	public void setOwnerProvider(OwnerProvider newOwnerProvider) {
		ownerProvider = newOwnerProvider;
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 3:28:16 PM)
	 * 
	 * @param newValue
	 *            java.lang.Object
	 */
	public void setValue(java.lang.Object newValue) {
		try {
			primSetValue(convertValue(newValue));
		} catch (FeatureValueConversionException featureException) {
			EMFWorkbenchEditPlugin.logError(featureException);
			primSetValue(null);
		}
	}

	/**
	 * Treat an empty String as a null value. Creation date: (4/6/2001 3:28:16 PM)
	 * 
	 * @param newValue
	 *            java.lang.Object
	 */
	public void setValueFromWidget(String newValue) {
		Object data = newValue;
		if (newValue != null && newValue.length() == 0)
			data = null;
		setValue(data);
		if (data == null)
			doUnsetValue();
		else
			shouldUnsetValue = false;
	}

	/**
	 * Insert the method's description here. Creation date: (4/10/2001 3:39:31 PM)
	 * 
	 * @param newValueXSITypeName
	 *            java.lang.String
	 */
	public void setValueXSITypeName(java.lang.String newValueXSITypeName) {
		valueXSITypeName = newValueXSITypeName;
	}

	public boolean shouldUnsetValue() {
		return shouldUnsetValue;
	}

	public EObject createNewObjectFromFeature() {
		EClass metaClass = (EClass) getPackage().getEClassifier(getNewValueTypeName());
		return getFactory().create(metaClass);
	}
}
