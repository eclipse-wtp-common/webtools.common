/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.emf.ecore.EObject;

public class EmfPackagePropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver == null || !(receiver instanceof EObject) || expectedValue == null || !(expectedValue instanceof String))
			return false;
		
		EObject eObject = (EObject) receiver;
		String emfPackage = (String)expectedValue;
		return emfPackage.equals(eObject.eClass().getEPackage().getNsURI());
	}

}
