/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;

public class IDUtil {

	/**
	 * This method is invoked to assign a unique ID to the object. The object must be contained in a
	 * resource.
	 */
	public static void assignID(EObject object) {
		Resource aResource = object.eResource();
		if (aResource instanceof XMLResource)
			assignID(object, (XMLResource) aResource);

	}

	public static String getOrAssignID(EObject object) {
		Resource aResource = object.eResource();
		if (aResource instanceof XMLResource)
			return getOrAssignID(object, (XMLResource) aResource);
		return null;
	}

	public static String getOrAssignID(EObject object, XMLResource aResource) {
		String id = aResource.getID(object);
		if (id == null)
			return assignID(object, aResource);
		return id;
	}

	public static String assignID(EObject object, XMLResource aResource) {
		String name = getBaseIDForAssignment(object);
		if (name == null)
			return null;
		String id = ensureUniqueID(aResource, name);
		aResource.setID(object, id);
		return id;
	}

	protected static String ensureUniqueID(XMLResource aResource, String baseIDName) {
		String innerBaseIDName = baseIDName;
		innerBaseIDName += "_"; //$NON-NLS-1$
		//Change to use the current time instead of incremental numbers to help
		//support team development.
		long currentTime = System.currentTimeMillis();
		String id = innerBaseIDName + currentTime;
		while (aResource.getEObject(id) != null) {
			++currentTime;
			id = innerBaseIDName + currentTime;
		}
		return id;
	}

	protected static String getBaseIDForAssignment(EObject object) {
		EClass metaO = object.eClass();
		return metaO.getName();
	}

}
