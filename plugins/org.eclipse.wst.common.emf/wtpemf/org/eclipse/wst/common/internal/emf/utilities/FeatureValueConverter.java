/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;



import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Insert the type's description here. Creation date: (5/10/2001 2:46:54 PM)
 * 
 * @author: Administrator
 */
public interface FeatureValueConverter {
	static final FeatureValueConverter DEFAULT = new DefaultFeatureValueConverter();

	/**
	 * Convert
	 * 
	 * @aValue to the type of
	 * @aFeature.
	 */
	Object convertValue(Object aValue, EStructuralFeature aFeature);
}