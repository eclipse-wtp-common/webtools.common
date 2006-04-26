/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.internal.emf.resource.Translator;
import org.eclipse.wst.common.internal.emf.resource.TranslatorPath;

public class HRefTranslator extends Translator {

	public HRefTranslator(String domNameAndPath, EClass eClass) {
		super(domNameAndPath, eClass);
		// TODO Auto-generated constructor stub
	}

	public HRefTranslator(String domNameAndPath, EStructuralFeature aFeature) {
		super(domNameAndPath, aFeature);
		// TODO Auto-generated constructor stub
	}

	public HRefTranslator(String domNameAndPath, EStructuralFeature aFeature,
			EClass eClass) {
		super(domNameAndPath, aFeature, eClass);
		// TODO Auto-generated constructor stub
	}

	public HRefTranslator(String domNameAndPath, EStructuralFeature aFeature,
			TranslatorPath path) {
		super(domNameAndPath, aFeature, path);
		// TODO Auto-generated constructor stub
	}

	public HRefTranslator(String domNameAndPath, EStructuralFeature aFeature,
			TranslatorPath[] paths) {
		super(domNameAndPath, aFeature, paths);
		// TODO Auto-generated constructor stub
	}

	public HRefTranslator(String domNameAndPath, EStructuralFeature aFeature,
			int style) {
		super(domNameAndPath, aFeature, style);
		// TODO Auto-generated constructor stub
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emf.resource.Translator#convertStringToValue(java.lang.String,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public Object convertStringToValue(String aValue, EObject anOwner) { 
		WorkbenchComponent earComp = (WorkbenchComponent)anOwner.eContainer();
		IVirtualComponent virtualComp = ComponentCore.createComponent(StructureEdit.getContainingProject(earComp));
		ArtifactEdit edit = (ArtifactEdit)virtualComp.getAdapter(ArtifactEdit.class);
		if (edit == null) return null;
		if( edit.getContentModelRoot() == null ) return null;
		Resource res = edit.getContentModelRoot().eResource();
		return res.getEObject(aValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emf.resource.Translator#convertValueToString(java.lang.Object,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public String convertValueToString(Object aValue, EObject anOwner) { 
		String frag = null;
		if (((EObject)aValue).eResource() != null)
			frag = ((EObject)aValue).eResource().getURIFragment((EObject)aValue);
		else
			frag = EcoreUtil.getID((EObject)aValue);
		return frag;
	}

}
