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
package org.eclipse.wst.common.internal.emfworkbench.integration;

import org.eclipse.emf.ecore.EObject;


/**
 * Insert the type's description here. Creation date: (6/20/2001 10:24:46 PM)
 * 
 * @author: Administrator
 */
public interface OwnerProvider {
	/**
	 * Return the EObject that will serve as the owner of a given J2EEModifierHelper.
	 */
	EObject getOwner();

	/**
	 * Return a J2EEModifierHelper for the owner if the owner does not yet exist.
	 */
	ModifierHelper getOwnerHelper();
}