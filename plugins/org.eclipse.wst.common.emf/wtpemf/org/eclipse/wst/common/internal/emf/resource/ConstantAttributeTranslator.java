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
 * Created on Apr 15, 2003
 *
 */
package org.eclipse.wst.common.internal.emf.resource;

import org.eclipse.emf.ecore.EObject;

/**
 * @author schacher
 */
public class ConstantAttributeTranslator extends Translator {

	protected String attributeValue;

	/**
	 * @param domNameAndPath
	 * @param eClass
	 */
	public ConstantAttributeTranslator(String domNameAndPath, String attributeValue) {
		super(domNameAndPath, null, DOM_ATTRIBUTE);
		this.attributeValue = attributeValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#getMOFValue(org.eclipse.emf.ecore.EObject)
	 */
	public Object getMOFValue(EObject mofObject) {
		return attributeValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#isSetMOFValue(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isSetMOFValue(EObject emfObject) {
		return true;
	}


}