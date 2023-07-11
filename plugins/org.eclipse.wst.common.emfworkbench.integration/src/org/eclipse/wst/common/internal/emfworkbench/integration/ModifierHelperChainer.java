/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
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
import org.eclipse.emf.ecore.EStructuralFeature;



/**
 * @version 1.0
 * @author
 */
public class ModifierHelperChainer {

	ModifierHelper helper;

	public ModifierHelperChainer(EStructuralFeature feature, EObject owner, Object value) {
		helper = new ModifierHelper();

		if (owner != null)
			helper.setOwner(owner);

		helper.setFeature(feature);

		if (value != null)
			helper.setValue(value);

	}

	public ModifierHelper getHelper() {
		return helper;
	}

	public void setOwnerBasedOnType(Object owner) {
		if (owner instanceof EObject)
			helper.setOwner((EObject) owner);
		else if (owner instanceof ModifierHelper)
			helper.setOwnerHelper((ModifierHelper) owner);
	}
}