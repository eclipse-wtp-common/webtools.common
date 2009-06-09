/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

public class RootTranslator extends Translator {

	/**
	 * Constructor for RootTranslator.
	 * 
	 * @param domNameAndPath
	 * @param eClass
	 */
	public RootTranslator(String domNameAndPath, EClass eClass) {
		super(domNameAndPath, eClass);
	}

	/**
	 * @see com.ibm.etools.emf2xml.impl.Translator#setMOFValue(Notifier, Object, int)
	 */
	@Override
	public void setMOFValue(Notifier owner, Object value, int newIndex) {
		((Resource) owner).getContents().add(newIndex, (EObject)value);
	}

	/**
	 * @see com.ibm.etools.emf2xml.impl.Translator#removeMOFValue(Notifier, Object)
	 */
	@Override
	public void removeMOFValue(Notifier owner, Object value) {
		((Resource) owner).getContents().remove(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#setMOFValue(org.eclipse.emf.ecore.EObject,
	 *      java.lang.Object)
	 */
	@Override
	public void setMOFValue(Resource res, Object value) {
		if (res != null && value != null)
			res.getContents().add((EObject)value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#isMultiValued()
	 */
	@Override
	public boolean isMultiValued() {
		return true;
	}


}
