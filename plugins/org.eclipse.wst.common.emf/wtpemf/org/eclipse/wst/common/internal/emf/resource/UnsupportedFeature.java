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
 * Created on Apr 13, 2004
 */
package org.eclipse.wst.common.internal.emf.resource;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EAttributeImpl;

/**
 * @author cbridgha
 */
public class UnsupportedFeature extends EAttributeImpl {
	/**
	 * This is used to capture features that are no longer supported
	 */
	public static boolean isUnsupported(EClass anEClass, String featureName) {

		if (featureName.equals("isZeroParams") && anEClass.getName().equals("MethodElement")) //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		return false;
	}

	public UnsupportedFeature() {
		super();
	}
}