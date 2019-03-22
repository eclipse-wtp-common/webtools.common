/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;


import java.io.IOException;

import org.eclipse.wst.common.internal.emf.plugin.EcoreUtilitiesPlugin;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The EMF2SAXDocumentHandler is utilized by the SAX parser to announce XML Events, such as
 * beginning and end of XML elements and the contents of those elements.
 * 
 * @author mdelder
 */
public class EMF2SAXDocumentHandler extends DefaultHandler {

	private TranslatorResource resource = null;
	private final CacheEventStack eventStack = new CacheEventStack();
	private CacheEventPool availableEventPool = new CacheEventPool();

	/**
	 * Create an EMF2SAXDocumentHandler to populate the given resource.
	 *  
	 */
	public EMF2SAXDocumentHandler(TranslatorResource resource) {
		this.resource = resource;
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#resolveEntity(java.lang.String, java.lang.String)
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
		InputSource result = null;
		this.resource.setDoctypeValues(publicId, systemId);

		try {
			EntityResolver entityResolver = this.resource.getEntityResolver();

			if (entityResolver != null)
				result = entityResolver.resolveEntity(publicId, systemId);
			else
				result = super.resolveEntity(publicId, systemId);
		} catch (IOException ioe) {
			throw new SAXException(ioe);
		}

		return result;
	}

	/**
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		/*
		 * The endDocument() method should have frozen the pool, or it may not be warmed yet. In
		 * either case, this method call will do as little work as necessary
		 */
		availableEventPool.warmPool();

		/* This line should not be necessary, but is left for safty */
		eventStack.clear();
		this.createRoot(this.resource);

	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String,
	 *      java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		addToStack(qName, attributes);
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] data, int start, int length) throws SAXException {

		CacheEventNode currentRecord = getCurrentRecord();
		if (currentRecord != null) {
			currentRecord.appendToBuffer(data, start, length);
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		CacheEventNode currentRecord = null;

		/*
		 * This should only happen in the case where the DOMPath was ignored so the stack does not
		 * quite match with the XML data structure. In this case we do nothing
		 */
		if (qName.equals(this.getCurrentRecord().getNodeName())) {
			currentRecord = this.removeCurrentRecord();
			if (currentRecord != null) {
				currentRecord.commit();
			}
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
		CacheEventNode lastRecord = this.removeCurrentRecord();
		lastRecord.commit();
		availableEventPool.freezePool();
	}

	/**
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	@Override
	public void error(SAXParseException ex) throws SAXException {
		throw ex;
	}

	/**
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	@Override
	public void fatalError(SAXParseException ex) throws SAXException {
		throw ex;
	}

	/**
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	@Override
	public void warning(SAXParseException ex) throws SAXException {
		EcoreUtilitiesPlugin.logWarning(ex);
	}

	/**
	 * @return
	 */
	public TranslatorResource getResource() {
		return resource;
	}

	protected void createRoot(TranslatorResource resourceArg) {
		this.eventStack.push(availableEventPool.createCacheEventNode(resourceArg));
	}

	protected void addToStack(String nodeName, Attributes attributes) {
		CacheEventNode parent = this.getCurrentRecord();
		if (!parent.isChildIgnorable(nodeName)) {
			this.eventStack.push(availableEventPool.createCacheEventNode(parent, nodeName, attributes));
		}
	}

	/**
	 * Return the current CENO without removing it from the event stack.
	 * 
	 * @return the current CENO without removing it
	 */
	protected CacheEventNode getCurrentRecord() {
		CacheEventNode result = null;
		if (!this.eventStack.isEmpty()) {
			result = this.eventStack.peek();
		}
		return result;
	}

	/**
	 * Return the current CENO and remove it from the event stack.
	 * 
	 * @return the current CENO and remove it
	 */
	protected CacheEventNode removeCurrentRecord() {
		CacheEventNode result = null;
		if (!this.eventStack.isEmpty()) {
			result = this.eventStack.pop();
		}
		return result;
	}

	//	private final void printStack() {
	//		// System.out.println("Printing stack ...");
	//		// for (int i = 0; i < this.eventStack.size(); i++) {
	//		// debug("stack[" + i + "]: " + eventStack.get(i));
	//		// }
	//		// System.out.println("... Printed stack");
	//	}
	//
	//	private final static void debug(Object obj) {
	//		// System.out.println(obj);
	//	}
	//
	//	private final static void warn(Object obj) {
	//		//System.err.println(obj);
	//	}

}
