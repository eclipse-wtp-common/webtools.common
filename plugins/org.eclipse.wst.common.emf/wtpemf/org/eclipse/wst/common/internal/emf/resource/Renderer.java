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
package org.eclipse.wst.common.internal.emf.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Interface that defines the api for rendering an EMF object to XML and vice versa
 */
public interface Renderer {

	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage"; //$NON-NLS-1$

	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$

	void setResource(TranslatorResource aResource);

	TranslatorResource getResource();

	void doLoad(InputStream in, Map options) throws IOException;

	void doSave(OutputStream outputStream, Map options) throws IOException;

	boolean useStreamsForIO();

	void prepareToAddContents();

	int getVersionID();

	boolean isModified();

	void accessForWrite();

	void accessForRead();

	void releaseFromRead();

	void releaseFromWrite();

	void preDelete();

	void preUnload();

	boolean isShared();

	boolean isSharedForWrite();

	void setBatchMode(boolean isBatch);
}