/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.internal.common.emf.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMIResource;

public interface CompatibilityXMIResource extends XMIResource {
	int FORMAT_EMF1 = 0;
	/**
	 * format for MOF5 compatibility; note that this can NOT be used with resources usings the
	 * "platform:/plugin" protocol"
	 */
	int FORMAT_MOF5 = 1;

	void addOriginalPackageURI(String packageUri, String originalUri);

	int getFormat();

	/**
	 * Set the serialization format. By default it is FORMAT_EMF1.
	 * 
	 * @see CompatibilityXMIResource#FORMAT_EMF1
	 * @see CompatibilityXMIResource#FORMAT_MOF5
	 */
	void setFormat(int format);

	boolean usesDefaultFormat();

	/**
	 * @param rootObject
	 */
	void removePreservingIds(EObject rootObject);

}