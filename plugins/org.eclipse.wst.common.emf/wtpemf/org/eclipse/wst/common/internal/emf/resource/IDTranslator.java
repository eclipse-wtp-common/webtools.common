/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Mar 19, 2003
 *
 */
package org.eclipse.wst.common.internal.emf.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.xmi.XMIResource;

/**
 * @author schacher
 */
public class IDTranslator extends Translator {
	public class NoResourceException extends RuntimeException {

		public NoResourceException() {
			super();
		}

		public NoResourceException(String s) {
			super(s);
		}
	}

	static final public EStructuralFeature ID_FEATURE = EcorePackage.eINSTANCE.getEClass_EIDAttribute();
	static final public IDTranslator INSTANCE = new IDTranslator();

	public IDTranslator() {
		super("id", ID_FEATURE, DOM_ATTRIBUTE); //$NON-NLS-1$
	}

	public void setMOFValue(EObject emfObject, Object value) {
		XMIResource res = (XMIResource) emfObject.eResource();
		if (res == null)
			throw new NoResourceException();
		String id = res.getID(emfObject);
		if (id == null && value == null)
			return;
		if ((id != null && !id.equals(value)) || (value != null && !value.equals(id)))
			res.setID(emfObject, (String) value);
	}

	public Object getMOFValue(EObject emfObject) {
		if (emfObject == null)
			throw new NoResourceException();
		XMIResource res = (XMIResource) emfObject.eResource();
		if (res == null)
			throw new NoResourceException();
		return res.getID(emfObject);
	}


	public boolean featureExists(EObject emfObject) {
		return true;
	}

	public boolean isIDMap() {
		return true;
	}



}
