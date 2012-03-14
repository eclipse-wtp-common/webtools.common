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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.wst.common.internal.emf.resource.Translator;
import org.eclipse.wst.common.internal.emf.resource.TranslatorPath;

public class IPathTranslator extends Translator {

	public IPathTranslator(String aDomPath, EStructuralFeature aFeature) {
		super(aDomPath, aFeature);
	}
	
	public IPathTranslator(String aDomPath, EStructuralFeature aFeature, TranslatorPath aTranslatorPath) {
		super(aDomPath, aFeature, new TranslatorPath[]{aTranslatorPath});
	}
	
	public IPathTranslator(String aDomPath, EStructuralFeature aFeature, TranslatorPath[] theTranslatorPaths) {
		super(aDomPath, aFeature, theTranslatorPaths);
	}
	
	public IPathTranslator(String aDomPath, EStructuralFeature aFeature, int aStyleMask) {
		super(aDomPath, aFeature, aStyleMask);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emf.resource.Translator#convertStringToValue(java.lang.String,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public Object convertStringToValue(String aValue, EObject anOwner) { 
		return new Path(aValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emf.resource.Translator#convertValueToString(java.lang.Object,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public String convertValueToString(Object aValue, EObject anOwner) { 
		if(aValue instanceof IPath) 
			return ((IPath)aValue).toString();
		return super.convertValueToString(aValue, anOwner);
	}

}
