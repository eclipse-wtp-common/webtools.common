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
package org.eclipse.wst.common.internal.emf.utilities;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

/**
 * Insert the type's description here. Creation date: (12/15/2000 5:25:43 PM)
 * 
 * @author: Administrator
 */
public abstract class DeferredReferenceUtilityAction {
	protected EReference reference;
	protected Object referenceValue;
	protected EObject copyContainer;
	protected String idSuffix;

	/**
	 * DeferredCopy constructor comment.
	 */
	public DeferredReferenceUtilityAction(EReference aReference, Object aValue, String aSuffix, EObject aCopyContainer) {
		reference = aReference;
		referenceValue = aValue;
		idSuffix = aSuffix;
		copyContainer = aCopyContainer;
	}

	/**
	 * Insert the method's description here. Creation date: (12/16/2000 9:32:28 AM)
	 * 
	 * @return org.eclipse.emf.ecore.EObject
	 */
	public org.eclipse.emf.ecore.EObject getCopyContainer() {
		return copyContainer;
	}

	/**
	 * Insert the method's description here. Creation date: (12/15/2000 5:34:56 PM)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getIdSuffix() {
		return idSuffix;
	}

	/**
	 * Insert the method's description here. Creation date: (12/15/2000 5:34:56 PM)
	 * 
	 * @return org.eclipse.emf.ecore.EReference
	 */
	public org.eclipse.emf.ecore.EReference getReference() {
		return reference;
	}

	/**
	 * Insert the method's description here. Creation date: (12/16/2000 9:32:28 AM)
	 * 
	 * @return java.lang.Object
	 */
	public Object getReferenceValue() {
		return referenceValue;
	}

	public abstract void performAction();
}

