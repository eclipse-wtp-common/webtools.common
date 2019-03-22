/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;


import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.xml.sax.EntityResolver;

public interface TranslatorResource extends ReferencedResource {
	/*
	 * must make a subclass because the constructor for EStructuralFeature is protected
	 */
	static class DocTypeFeature extends EStructuralFeatureImpl {
		protected DocTypeFeature() {
			super();
		}
	}


	EStructuralFeature DOC_TYPE_FEATURE = new DocTypeFeature();

	/**
	 * The public id of the XML document, if specified.
	 */
	String getPublicId();

	/**
	 * Return the first element in the EList.
	 */
	EObject getRootObject();

	/**
	 * The system id of the XML document, if specified.
	 */
	String getSystemId();

	/**
	 * Sets the public id and system id of the XML document.
	 */
	void setDoctypeValues(String publicId, String systemId);

	/**
	 * Sets the default public/system ids if necessary
	 */
	void setDefaults();

	/**
	 * Returns the name that will be in the document type header of the serialized xml file
	 */
	String getDoctype();

	/**
	 * Returns the XML version of this document
	 */
	String getXMLVersion();

	Translator getRootTranslator();

	/**
	 * Entity resolver that can be used when using standard parsers to read the resource
	 */
	EntityResolver getEntityResolver();

	boolean usesDTD();

	public int getVersionID();

	void setVersionID(int i);

	Renderer getRenderer();
}