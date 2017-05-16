/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal.util;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.wst.common.snippets.internal.Logger;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 */
public class CommonXML {

	/**
	 * Returns a DocumentBuilder capable of creating a DOM Document from
	 * input.
	 * 
	 * Example usage: Document document = null; try { DocumentBuilder builder =
	 * CommonXML.getDocumentBuilder(); if (builder != null) { InputStream fis =
	 * new FileInputStream(getFilename()); document = builder.parse(new
	 * InputSource(fis)); } else { Logger.log(Logger.ERROR, "Couldn't obtain a
	 * DocumentBuilder"); //$NON-NLS-1$ } } catch (FileNotFoundException e) { //
	 * typical of new workspace, don't log it document = null; } catch
	 * (IOException e) { Logger.logException("Could not load document", e);
	 * //$NON-NLS-1$ return definitions; } catch (SAXException e) {
	 * Logger.logException("Could not load document", e); //$NON-NLS-1$ return
	 * definitions; }
	 * 
	 * @return
	 */
	public synchronized static DocumentBuilder getDocumentBuilder() {
		DocumentBuilder result = null;
		try {
			result = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			result.setEntityResolver(getEntityResolver());
		}
		catch (ParserConfigurationException e) {
			Logger.logException(e);
		}
		return result;
	}

	public synchronized static DocumentBuilder getDocumentBuilder(boolean validating) {
		DocumentBuilder result = null;
		try {
			DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
			instance.setValidating(validating);
			instance.setExpandEntityReferences(false);
			instance.setCoalescing(true);
			result = instance.newDocumentBuilder();
			result.setEntityResolver(getEntityResolver());
		}
		catch (ParserConfigurationException e) {
			Logger.logException(e);
		}
		return result;
	}

	/**
	 * Transforms a DOM document into a lightly-formatted UTF-16 represntation
	 * and outputs it to an outputstream
	 * 
	 * @param document
	 * @param ostream
	 * @throws IOException
	 */
	public static void serialize(Document document, OutputStream ostream) throws IOException {
		Source domSource = new DOMSource(document);
		try {
			Transformer serializer = TransformerFactory.newInstance().newTransformer();
			try {
				serializer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
				serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$
				serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-16"); //$NON-NLS-1$
			}
			catch (IllegalArgumentException e) {
				// unsupported properties
			}
			serializer.transform(domSource, new StreamResult(ostream));
		}
		catch (TransformerConfigurationException e) {
			throw new IOException(e.getMessage());
		}
		catch (TransformerFactoryConfigurationError e) {
			throw new IOException(e.getMessage());
		}
		catch (TransformerException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Returns an EntityResolver that won't try to load and resolve ANY
	 * entities
	 */
	private static EntityResolver getEntityResolver() {
		EntityResolver resolver = new EntityResolver() {
			public InputSource resolveEntity(String publicID, String systemID) throws SAXException, IOException {
				InputSource 					result = new InputSource(new ByteArrayInputStream(new byte[0]));
				result.setPublicId(publicID);
				result.setSystemId(systemID != null ? systemID : "/_" + getClass().getName()); //$NON-NLS-1$
				return result;
			}
		};
		return resolver;
	}
}
