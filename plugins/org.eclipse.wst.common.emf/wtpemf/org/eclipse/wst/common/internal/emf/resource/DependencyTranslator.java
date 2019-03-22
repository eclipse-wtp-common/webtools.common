/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Mar 25, 2003
 *
 */
package org.eclipse.wst.common.internal.emf.resource;

import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author schacher
 */
public class DependencyTranslator extends Translator {

	/**
	 * @param domNameAndPath
	 * @param aFeature
	 */
	public DependencyTranslator(String domNameAndPath, EStructuralFeature aFeature, EStructuralFeature aDependentFeature) {
		super(domNameAndPath, aFeature);
		dependencyFeature = aDependentFeature;
	}

	public EObject getChild(EObject parent) {
		EObject child = basicGetDependencyObject(parent);
		if (child == null)
			return createChild(parent);
		return child;
	}


	protected EObject createChild(EObject parent) {
		EObject child = createEMFObject(dependencyFeature);
		parent.eSet(dependencyFeature, child);
		return child;
	}

	@Override
	public List getMOFChildren(EObject mofObject) {
		//return super.getMOFChildren(getChild(mofObject));
		return super.getMOFChildren(mofObject);
	}

	@Override
	public Object getMOFValue(EObject mofObject) {
		return super.getMOFValue(getChild(mofObject));
	}

	@Override
	public void setMOFValue(EObject emfObject, Object value) {
		//super.setMOFValue(getChild(emfObject), value);
		super.setMOFValue(getChild(emfObject), value, -1);
	}

	@Override
	public void setMOFValue(Notifier owner, Object value, int newIndex) {
		super.setMOFValue(getChild((EObject) owner), value, newIndex);
	}

	@Override
	public void removeMOFValue(Notifier owner, Object value) {
		super.removeMOFValue(getChild((EObject) owner), value);
	}

	@Override
	public boolean isSetMOFValue(EObject emfObject) {
		return super.isSetMOFValue(getChild(emfObject));
	}

	@Override
	public void unSetMOFValue(EObject emfObject) {
		super.unSetMOFValue(getChild(emfObject));
	}


	@Override
	public boolean isDependencyChild() {
		return true;
	}

	@Override
	public boolean featureExists(EObject emfObject) {
		return super.featureExists(getChild(emfObject));
	}

	@Override
	public void clearList(EObject mofObject) {
		super.clearList(getChild(mofObject));
	}


}