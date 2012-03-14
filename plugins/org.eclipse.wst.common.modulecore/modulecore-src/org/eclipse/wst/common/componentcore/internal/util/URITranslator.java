/*******************************************************************************
 * Copyright (c) 2003, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.wst.common.internal.emf.resource.Translator;
import org.eclipse.wst.common.internal.emf.resource.TranslatorPath;

public class URITranslator extends Translator {

	public URITranslator(String aDomPath, EStructuralFeature aFeature) {
		super(aDomPath, aFeature);
	}
	
	public URITranslator(String aDomPath, EStructuralFeature aFeature, TranslatorPath aTranslatorPath) {
		super(aDomPath, aFeature, new TranslatorPath[]{aTranslatorPath});
	}
	
	public URITranslator(String aDomPath, EStructuralFeature aFeature, TranslatorPath[] theTranslatorPaths) {
		super(aDomPath, aFeature, theTranslatorPaths);
	}
	
	public URITranslator(String aDomPath, EStructuralFeature aFeature, int aStyleMask) {
		super(aDomPath, aFeature, aStyleMask);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emf.resource.Translator#convertStringToValue(java.lang.String,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public Object convertStringToValue(String aValue, EObject anOwner) { 
		return URI.createURI(aValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emf.resource.Translator#convertValueToString(java.lang.Object,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public String convertValueToString(Object aValue, EObject anOwner) { 
		if(aValue instanceof URI) 
			return ((URI)aValue).toString();
		return super.convertValueToString(aValue, anOwner);
	}

}
