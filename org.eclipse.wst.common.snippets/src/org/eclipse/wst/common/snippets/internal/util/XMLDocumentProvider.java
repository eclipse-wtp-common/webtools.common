/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal.util;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.wst.common.snippets.internal.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * An XML Creator/Reader/Writer that 1) Ignores any DocumentType Nodes found
 * within the document (as well as any entities) 2) Ignores any
 * errors/exceptions from Xerces when loading a document 3) Can load Documents
 * from within a .JAR file (***read-only***)
 */

public class XMLDocumentProvider {
	/**
	 * 
	 * @param args
	 *            java.lang.String[]
	 */
	public static void main(String[] args) {
		if (args.length < 2)
			return;
		XMLDocumentProvider p = new XMLDocumentProvider();
		p.setFileName(args[0]);
		p.setRootElementName(args[1]);
		p.getDocument();

	}

	protected Document document = null;
	protected ErrorHandler errorHandler = null;
	protected String fileName = null;
	protected boolean fValidating = true;
	protected InputStream inputStream = null;
	protected EntityResolver resolver = null;

	protected Node rootElement = null;
	protected String rootElementName = null;


	public XMLDocumentProvider() {
		super();
	}

	protected Document _getParsedDocumentDOM2() {
		Document result = null;

		InputStream is = null;
		try {
			DocumentBuilder builder = getDocumentBuilder();
			// DOMParser parser = new DOMParser();
			// parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error",
			// false);//$NON-NLS-1$
			// parser.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
			// false);//$NON-NLS-1$
			// parser.setErrorHandler(getNullErrorHandler());
			// parser.setEntityResolver(getNullEntityResolver());
			// is = getInputStream();
			builder.setEntityResolver(getNullEntityResolver());
			builder.setErrorHandler(getNullErrorHandler());
			is = getInputStream();
			if (is != null)
				result = builder.parse(is);
		}
		catch (SAXException e) {
			result = null;
			// parsing exception, notify the user?
			Logger.logException(e);
		}
		catch (FileNotFoundException e) {
			System.out.print(""); //$NON-NLS-1$
			// NOT an "exceptional case"; do not Log
		}
		catch (IOException e) {
			Logger.logException(e);
		}
		finally {
			if (is != null) {
				try {
					is.close();
				}
				catch (Exception e) {
					// what can be done?
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @return Document
	 */
	public Document getDocument() {
		if (document == null)
			load();
		return document;
	}

	protected DocumentBuilder getDocumentBuilder() {
		return CommonXML.getDocumentBuilder(isValidating());
	}

	protected DOMImplementation getDomImplementation() {
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (ParserConfigurationException e1) {
			Logger.logException(e1);
		}
		catch (FactoryConfigurationError e1) {
			Logger.logException(e1);
		}
		DOMImplementation impl = builder.getDOMImplementation();
		return impl;
	}

	/*************************************************************************
	 * Takes a single string of the form "a/b/c" and ensures that that
	 * structure exists below the head element, down through 'c', and returns
	 * a <em>single</em> element 'c'. For multiple elements (such as
	 * multiple &lt;macro&gt; elements contained within a single
	 * &lt;macros&gt; element, full DOM access is required for searching and
	 * child element manipulation.
	 ************************************************************************/
	public Element getElement(String name) {
		if (document == null)
			load();
		Element element = null;
		if (document != null)
			element = (Element) getNode(getRootElement(), name);
		return element;
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Returns and input stream to use as the source of the Document 1) from
	 * an InputStream set on this instance 2) from a JAR file with the given
	 * entry name 3) from a normal file
	 * 
	 * @return InputStream
	 */
	public InputStream getInputStream() throws FileNotFoundException {
		if (inputStream != null)
			return inputStream;
		else {
			return new FileInputStream(getFileName());
		}
	}

	protected Node getNamedChild(Node parent, String childName) {
		if (parent == null) {
			return null;
		}
		NodeList childList = parent.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
			if (childList.item(i).getNodeName().equals(childName))
				return childList.item(i);
		}
		return null;
	}

	protected Document getNewDocument() {
		Document result = null;
		try {
			result = getDomImplementation().createDocument("http://www.w3.org/XML/1998/namespace", getRootElementName(), null); //$NON-NLS-1$
			NodeList children = result.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				result.removeChild(children.item(i));
			}
			// we're going through this effort to avoid a NS element
			Element settings = result.createElementNS("http://www.w3.org/XML/1998/namespace", getRootElementName()); //$NON-NLS-1$
			result.appendChild(settings);
			return result;
		}
		catch (DOMException e) {
			Logger.logException(e);
		}
		return null;
	}

	/*************************************************************************
	 * Takes a single string of the form "a/b/c" and ensures that that
	 * structure exists below the head element, down through 'c', and returns
	 * the element 'c'.
	 ************************************************************************/
	protected Node getNode(Node node, String name) {
		StringTokenizer tokenizer = new StringTokenizer(name, "/"); //$NON-NLS-1$
		String token = null;
		while (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (getNamedChild(node, token) == null)
				node.appendChild(document.createElement(token));
			node = getNamedChild(node, token);
		}
		return node;
	}

	/**
	 * Returns an EntityResolver that won't try to load and resolve ANY
	 * entities
	 */
	private EntityResolver getNullEntityResolver() {
		if (resolver == null) {
			resolver = new EntityResolver() {
				public InputSource resolveEntity(String param1, String param2) throws SAXException, IOException {
					return new InputSource(new StringReader("")); //$NON-NLS-1$
				}
			};
		}
		return resolver;
	}

	/**
	 * Returns an ErrorHandler that will not stop the parser on reported
	 * errors
	 */
	private ErrorHandler getNullErrorHandler() {
		if (errorHandler == null) {
			errorHandler = new ErrorHandler() {
				public void error(SAXParseException exception) {
					Logger.logException(exception);
				}

				public void fatalError(SAXParseException exception) {
					Logger.logException(exception);
				}

				public void warning(SAXParseException exception) {
					Logger.logException(exception);
				}
			};
		}
		return errorHandler;
	}

	protected Document getParsedDocument() {
		Document result = null;
		if (inputStream == null) {
			File existenceTester = new File(getFileName());
			if (!existenceTester.exists())
				return null;
		}

		result = _getParsedDocumentDOM2();

		return result;

	}

	/**
	 * Returns the root Element of the current document
	 * 
	 * @return org.w3c.dom.Element
	 */
	public Node getRootElement() {
		return getRootElement(getDocument());
	}

	/**
	 * Returns the/a root Element for the current document
	 * 
	 * @return org.w3c.dom.Element
	 */
	protected Node getRootElement(Document doc) {
		if (doc == null)
			return null;
		if (doc.getDocumentElement() != null)
			return doc.getDocumentElement();
		try {
			Element newRootElement = doc.createElement(getRootElementName());
			doc.appendChild(newRootElement);
			return newRootElement;
		}
		catch (DOMException e) {
			Logger.logException(e);
		}
		return null;
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public String getRootElementName() {
		return rootElementName;
	}

	/**
	 * @return
	 */
	public boolean isValidating() {
		return fValidating;
	}

	public void load() {
		// rootElementName and fileName are expected to be defined at this
		// point
		document = getParsedDocument();
		if (document != null) {
			rootElement = getRootElement(document);
		}

		if (document == null || rootElement == null) {
			document = getNewDocument();
			if (document != null) {
				NodeList children = document.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					if (children.item(i).getNodeType() == Node.ELEMENT_NODE && children.item(i).getNodeName().equals(getRootElementName()))
						rootElement = (Element) children.item(i);
				}
				if (rootElement == null) {
					for (int i = 0; i < children.getLength(); i++) {
						if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
							rootElement = (Element) children.item(i);
							break;
						}
					}
				}
			}
		}
	}

	protected void saveDocument(Document odocument, OutputStream stream) throws IOException {
		CommonXML.serialize(odocument, stream);
	}

	/**
	 * 
	 * @param newFileName
	 *            java.lang.String
	 */
	public void setFileName(String newFileName) {
		fileName = newFileName;
	}

	/**
	 * Sets the inputStream for which to provide a Document.
	 * 
	 * @param inputStream
	 *            The inputStream to set
	 */
	public void setInputStream(InputStream newInputStream) {
		this.inputStream = newInputStream;
	}

	/**
	 * 
	 * @param newRootElementName
	 *            java.lang.String
	 */
	public void setRootElementName(String newRootElementName) {
		rootElementName = newRootElementName;
	}

	/**
	 * @param b
	 */
	public void setValidating(boolean b) {
		fValidating = b;
	}

	public void store() {
		if (rootElement == null) {
			document = getNewDocument();
			rootElement = document.getDocumentElement();
		}

		try {
			OutputStream ostream = new FileOutputStream(getFileName());

			storeDocument(document, ostream);

			ostream.flush();
			ostream.close();
		}
		catch (IOException e) {
			Logger.logException("Exception saving document " + getFileName(), e); //$NON-NLS-1$
		}
	}

	protected void storeDocument(Document odocument, OutputStream ostream) {
		try {
			saveDocument(odocument, ostream);
		}
		catch (IOException e) {
			Logger.logException(e);
		}
	}
}