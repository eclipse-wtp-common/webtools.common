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
 * Created on Apr 1, 2003
 *
 */
package org.eclipse.wst.common.internal.emf.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author schacher
 */
public class SourceLinkTranslator extends Translator {


	/**
	 * @param domNameAndPath
	 * @param aFeature
	 * @param path
	 */
	public SourceLinkTranslator(String domNameAndPath, EStructuralFeature aFeature, TranslatorPath path) {
		super(domNameAndPath, aFeature, path);
	}

	/**
	 * @param domNameAndPath
	 * @param aFeature
	 * @param path
	 */
	public SourceLinkTranslator(String domNameAndPath, EStructuralFeature aFeature, TranslatorPath path, int style) {
		super(domNameAndPath, aFeature, path);
		fStyle |= style;
	}

	/**
	 * @param domNameAndPath
	 * @param aFeature
	 * @param style
	 */
	public SourceLinkTranslator(String domNameAndPath, EStructuralFeature aFeature, int style) {
		super(domNameAndPath, aFeature, style);
	}

	public Object convertStringToValue(String strValue, EObject owner) {

		Object value = null;
		if (strValue != null)
			// Find the object with the name that matches matchName
			value = fTranslatorPaths[0].findObject(owner, strValue.trim());
		if ((fStyle & Translator.STRING_RESULT_OK) != 0)
			return ((value != null) ? value : strValue);
		return value;
	}

	public String convertValueToString(Object value, EObject owner) {
		TranslatorPath path = fTranslatorPaths[0];
		Object attrValue = path.getLastMap().getMOFValue((EObject) value);
		return path.getLastMap().convertValueToString(attrValue, owner);
	}


}