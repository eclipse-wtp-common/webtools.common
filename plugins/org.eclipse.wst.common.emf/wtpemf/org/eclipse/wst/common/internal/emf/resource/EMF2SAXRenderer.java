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

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.util.WrappedException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;

import com.ibm.wtp.common.logger.proxy.Logger;

/**
 * @author mdelder
 */
public class EMF2SAXRenderer extends AbstractRendererImpl {

	/**
	 *  
	 */
	public EMF2SAXRenderer() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#doLoad(java.io.InputStream, java.util.Map)
	 */
	public void doLoad(InputStream in, Map options) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(isValidating());
			factory.setNamespaceAware(true);
			/*
			 * Causes errors in IBM JDK try { factory.setAttribute(JAXP_SCHEMA_LANGUAGE,
			 * W3C_XML_SCHEMA); } catch (IllegalArgumentException x) { }
			 */
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			EMF2SAXDocumentHandler handler = new EMF2SAXDocumentHandler(this.getResource());
			try {
				reader.setFeature("http://xml.org/sax/features/validation", isValidating()); //$NON-NLS-1$
			} catch (SAXNotRecognizedException snre) {
			}
			try {
				reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true); //$NON-NLS-1$
			} catch (SAXNotRecognizedException snre) {
			}
			try {
				reader.setFeature("http://apache.org/xml/features/validation/schema", isValidating()); //$NON-NLS-1$
			} catch (SAXNotRecognizedException e) {
				reader.setFeature("http://xml.org/sax/features/validation", false); //$NON-NLS-1$
				Logger.getLogger().log("Warning: Parser does not support \"http://apache.org/xml/features/validation/schema\". Validation will be disabled."); //$NON-NLS-1$
			}
			try {
				reader.setFeature("http://apache.org/xml/features/allow-java-encodings", true); //$NON-NLS-1$
			} catch (SAXNotRecognizedException e) {
				Logger.getLogger().log("Warning: Parser does not support \"http://apache.org/xml/features/allow-java-encodings\"."); //$NON-NLS-1$
			}
			/*
			 * try { reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler); }
			 * catch (SAXNotRecognizedException e) { }
			 */
			reader.setContentHandler(handler);
			reader.setErrorHandler(handler);
			//reader.setDTDHandler(handler);
			reader.setEntityResolver(handler);
			InputSource testsource = new InputSource(in);
			reader.parse(testsource);
		} catch (RuntimeException t_rex) {
			throw t_rex;
		} catch (Exception ex) {
			throw new WrappedException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#doSave(java.io.OutputStream, java.util.Map)
	 */
	public void doSave(OutputStream outputStream, Map options) throws IOException {

		/*
		 * try { Serializer serializer =
		 * SerializerFactory.getSerializerFactory(Method.XML).makeSerializer(outputStream,
		 * createOutputFormat()); serializer.setOutputByteStream(outputStream); ContentHandler
		 * handler = serializer.asContentHandler();
		 */

		TransformerHandler handler = null;
		try {
			try {
				SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactory.newInstance();
				handler = factory.newTransformerHandler();

				handler.setResult(new StreamResult(outputStream));
				Transformer transformer = handler.getTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
				transformer.setOutputProperty(OutputKeys.ENCODING, getResource().getEncoding());
				transformer.setOutputProperty(OutputKeys.VERSION, getResource().getXMLVersion());
				transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); //$NON-NLS-1$                
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$

				if (getResource().getPublicId() != null)
					transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, getResource().getPublicId());
				if (getResource().getSystemId() != null)
					transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, getResource().getSystemId());

			} catch (TransformerConfigurationException e) {
				Logger.getLogger().logError(e);
			} catch (TransformerFactoryConfigurationError e) {
				Logger.getLogger().logError(e);
			}
			if (handler == null) {
				Logger.getLogger("SAX Writer is null"); //$NON-NLS-1$
				return;
			}
			EMF2SAXWriter writer = new EMF2SAXWriter();
			writer.serialize(this.resource, handler);
		} catch (SAXException saxe) {
			throw new WrappedException(saxe);
		}
	}

	/*
	 * protected OutputFormat createOutputFormat() { OutputFormat format = new OutputFormat();
	 * format.setIndenting(true); format.setLineSeparator(DOMUtilities.NEWLINE_STRING);
	 * //$NON-NLS-1$ format.setEncoding(getResource().getEncoding());
	 * format.setVersion(getResource().getXMLVersion()); if (this.resource != null)
	 * format.setDoctype(this.resource.getPublicId(), this.resource.getSystemId()); return format; }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#prepareToAddContents()
	 */
	public void prepareToAddContents() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#getVersionID()
	 */
	public int getVersionID() {
		return getResource().getVersionID();
	}
}