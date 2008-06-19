/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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
import org.eclipse.wst.common.internal.emf.resource.CompatibilityXMIResource;
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
		Object retVal = null;
		if (aValue != null)
		{
			Resource res = null;
			try {
				WorkbenchComponent earComp = (WorkbenchComponent)anOwner.eContainer();
				IVirtualComponent virtualComp = ComponentCore.createComponent(StructureEdit.getContainingProject(earComp));
				ArtifactEdit edit = (ArtifactEdit)virtualComp.getAdapter(ArtifactEdit.class);
				if (edit != null)
				{
					EObject contentModelRoot = edit.getContentModelRoot(); 
					if( contentModelRoot != null )
					{
						res = contentModelRoot.eResource();
						if (res != null && res instanceof CompatibilityXMIResource)
							retVal = res.getEObject(aValue);
					}
				}
			} finally {
//			if ((res != null) && res.getResourceSet() != null) {
//				res.getResourceSet().getResources().remove(res);
//				res.unload();
//			}
			}
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emf.resource.Translator#convertValueToString(java.lang.Object,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public String convertValueToString(Object aValue, EObject anOwner) { 
		String frag = null;
		Resource theResource = ((EObject)aValue).eResource();
		if (theResource != null)
		{
			if (theResource instanceof CompatibilityXMIResource)
				frag = theResource.getURIFragment((EObject)aValue);
			else
				frag = null;
		}
		else
			frag = EcoreUtil.getID((EObject)aValue);
		return frag;
	}

}
