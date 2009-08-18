/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.wst.common.internal.emf.plugin.EcoreUtilitiesPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A class containing common dom manipulation and search functions.
 */
public class DOMUtilities {
	// Handy Constants
	public static final String INDENT_STRING = "\t"; //$NON-NLS-1$
	public static final String NEWLINE_STRING = System.getProperty("line.separator"); //$NON-NLS-1$
	//Hack to be removed when the DOM apis change such that there is an easier
	//way to fluff up and set the doctype
	private static final String DUMMY_ENTITY_STRING = "dummy"; //$NON-NLS-1$
	private static final String DUMMY_ENTITY_NODE_STRING = "<dummy/>"; //$NON-NLS-1$
	private static DocumentBuilder defaultDocumentBuilder;
	private static EntityResolver defaultEntityResolver;

	/**
	 * Returns an iterator that iterates over the sub nodes of a path.
	 */
	static public Iterator createPathIterator(String path) {
		String tPath = path.startsWith("/") ? path.substring(1) : path; //$NON-NLS-1$
		if (tPath.length() == 0)
			tPath = null;
		final String aPath = tPath;

		return new Iterator() {
			int prevIndex = 0;
			int curIndex = 0;
			String pathString = aPath;

			public boolean hasNext() {
				return pathString != null && prevIndex != -1;
			}

			public Object next() {
				curIndex = pathString.indexOf('/', prevIndex);
				String nodeString = null;
				if (curIndex != -1)
					nodeString = pathString.substring(prevIndex, curIndex++);
				else
					nodeString = pathString.substring(prevIndex);
				prevIndex = curIndex;
				return nodeString;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Get the text for the passed in node.
	 */
	static public String getChildText(Node node) {
		Text textNode = getChildTextNode(node);
		if (textNode != null)
			return textNode.getData();
		return null;
	}

	/**
	 * Get the text for the passed in node.
	 */
	static public Text getChildTextNode(Node node) {
		Node textNode = node.getFirstChild();
		while (textNode != null && DOMUtilities.isTextNode(textNode)) {
			if (!isWhitespace(textNode))
				return (Text) textNode;
			textNode = textNode.getNextSibling();
		}
		return null;
	}

	/**
	 * Return a string representing the current indentation of the node.
	 */
	static public String getIndentString(Node node) {
		Revisit.toDo();
		return ""; //$NON-NLS-1$
	}

	/**
	 * Get the last non-text child of a node.
	 * 
	 * @return org.w3c.dom.Node The last non-text child node of
	 * @node.
	 * @param node
	 *            org.w3c.dom.Node The node
	 */
	public static Node getLastNodeChild(Node node) {
		if (node == null)
			return null;
		Node child = node.getLastChild();
		while (child != null && child.getNodeType() == Node.TEXT_NODE)
			child = child.getPreviousSibling();
		return child;
	}

	/**
	 * Get the next non-text sibling after a node.
	 * 
	 * @return org.w3c.dom.Node The first non-text sibling node after
	 * @node. If there is no next non-text sibling, null is returned.
	 * @param node
	 *            org.w3c.dom.Node The node
	 */
	public static Node getNextNodeSibling(Node node) {
		Node sibling = node.getNextSibling();
		while (sibling != null && sibling.getNodeType() != Node.ELEMENT_NODE)
			sibling = sibling.getNextSibling();
		return sibling;
	}

	/**
	 * Get the first child Node with the specified name
	 */
	static public Node getNodeChild(Node node, String nodeName) {
		Node child = null;
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(nodeName)) {
				child = n;
				break;
			}
		}
		return child;
	}

	/**
	 * Traverses the path passed in <pathName>. The path is a string in the form
	 * 'node1/node2/node3'. This method starts at node.
	 */
	static public Node getNodeChildForPath(Node parent, String pathName) {

		Node curNode = parent;
		Iterator i = DOMUtilities.createPathIterator(pathName);
		while (i.hasNext()) {
			String child = (String) i.next();
			curNode = DOMUtilities.getNodeChild(curNode, child);
			if (curNode == null)
				return null;
		}
		return curNode;
	}

	/**
	 * Get the Node children with the specified names
	 */
	static public List getNodeChildren(Node node, String[] nodeNames) {
		NodeList childNodes = node.getChildNodes();
		ArrayList results = new ArrayList();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node n = childNodes.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				boolean found = false;
				for (int j = 0; j < nodeNames.length; j++) {
					if (nodeNames[j].equals(n.getNodeName())) {
						found = true;
						break;
					}
				}
				if (found)
					results.add(n);
			}
		}
		return results;
	}

	/**
	 * Get the Node children with the specified name
	 */
	static public List getNodeChildren(Node node, String nodeName) {
		NodeList childNodes = node.getChildNodes();
		ArrayList results = new ArrayList();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node n = childNodes.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(nodeName))
				results.add(n);
		}
		return results;
	}

	/**
	 * Get the first non-text sibling before a node.
	 * 
	 * @return org.w3c.dom.Node The first non-text sibling node before
	 * @node. If there is no previous non-text sibling, null is returned.
	 * @param node
	 *            org.w3c.dom.Node The node
	 */
	public static Node getPreviousNodeSibling(Node node) {
		if (node == null)
			return null;
		Node sibling = node.getPreviousSibling();
		while (sibling != null && DOMUtilities.isTextNode(sibling))
			sibling = sibling.getPreviousSibling();
		return sibling;
	}

	/**
	 * Get the first text node before a node.
	 * 
	 * @return org.w3c.dom.Node The first text node before
	 * @node. Null if no such node exist.
	 * @param node
	 *            org.w3c.dom.Node The node
	 */
	public static Text getPreviousText(Node node) {
		Text sibling = getPreviousTextSibling(node);

		if (sibling == null && node.getParentNode() != null)
			sibling = getPreviousText(node.getParentNode());

		return sibling;
	}

	/**
	 * Get the first text sibling before a node.
	 * 
	 * @return org.w3c.dom.Node The first text sibling node before
	 * @node. If there is no previous text sibling, null is returned.
	 * @param node
	 *            org.w3c.dom.Node The node
	 */
	public static Text getPreviousTextSibling(Node node) {
		Assert.isNotNull(node);

		Node sibling = node.getPreviousSibling();
		Node lastText = null;
		while (sibling != null && sibling.getNodeType() == Node.TEXT_NODE) {
			lastText = sibling;
			sibling = sibling.getPreviousSibling();
		}
		return (Text) lastText;
	}

	/**
	 * Get the first text sibling before a node.
	 * 
	 * @return org.w3c.dom.Node The first text sibling node before
	 * @node. If there is no previous text sibling, null is returned.
	 * @param node
	 *            org.w3c.dom.Node The node
	 */
	public static String getTrailingWhitespace(Text node) {
		Assert.isNotNull(node);

		String text = node.getData();
		if (text.length() == 0)
			return ""; //$NON-NLS-1$

		int i = text.length() - 1;
		for (; i >= 0; i--) {
			if (!Character.isWhitespace(text.charAt(i))) {
				break;
			}
		}

		return text.substring(++i);
	}

	/**
	 * Inserts <newNode>into <parent>after <refNode>. If <refNode>is null then the node is inserted
	 * to the beginning of the parent's child nodes.
	 * 
	 * @param parent
	 *            org.w3c.dom.Node
	 * @param newNode
	 *            org.w3c.dom.Node
	 * @param refNode
	 *            org.w3c.dom.Node
	 */
	public static void insertAfterNode(Node parent, Node newNode, Node refNode) {
		Node insertBeforeNode = null;
		if (refNode != null) {
			insertBeforeNode = refNode.getNextSibling();
		}
		if (refNode == null)
			insertBeforeNode(parent, newNode, parent.getFirstChild());
		else
			insertBeforeNode(parent, newNode, insertBeforeNode);
	}

	/**
	 * Insert a <newNode>into <parent>before <refNode>. This utility method is used to ensure that
	 * the insertion does not result in two adjacent text nodes. The DOM model does not handle
	 * adjacent text nodes. They must be joined together.
	 * 
	 * @param newNode
	 *            org.w3c.dom.Node
	 * @param newNode
	 *            org.w3c.dom.Node
	 * @param refNode
	 *            org.w3c.dom.Node
	 */
	static public void insertBeforeNode(Node parent, Node newNode, Node refNode) {
		if (newNode.getNodeType() == Node.TEXT_NODE) {
			Text textNewNode = (Text) newNode;

			// If the insert before node is text, join it with the new node.
			if (refNode != null && refNode.getNodeType() == Node.TEXT_NODE) {
				Text textRefNode = (Text) refNode;
				textRefNode.setData(textNewNode.getData() + textRefNode.getData());
				return;
			}
			// If the node we are inserting after is text,
			// join it with the new node.
			Node insertAfterNode = (refNode == null) ? parent.getLastChild() : refNode.getPreviousSibling();
			if (insertAfterNode != null && insertAfterNode.getNodeType() == Node.TEXT_NODE) {
				Text textInsertAfterNode = (Text) insertAfterNode;
				textInsertAfterNode.setData(textInsertAfterNode.getData() + textNewNode.getData());
				return;
			}
		}
		// There is no text node to join to, simple insert the node.
		parent.insertBefore(newNode, refNode);
	}

	/**
	 * Insert a <newNode>into <parent>before <refNode>. This method will also insert the node before
	 * any whitespace nodes that appear in the tree before <refNode>. This method will also ensure
	 * that the insertion does not result in two adjacent text nodes. The DOM model does not handle
	 * adjacent text nodes. They must be joined together.
	 * 
	 * @param newNode
	 *            org.w3c.dom.Node
	 * @param newNode
	 *            org.w3c.dom.Node
	 * @param refNode
	 *            org.w3c.dom.Node
	 */
	static public void insertBeforeNodeAndWhitespace(Node parent, Node newNode, Node refNode) {
		Node curNode = (refNode == null) ? parent.getLastChild() : refNode.getPreviousSibling();
		Node lastNode = refNode;

		while (curNode != null && (DOMUtilities.isWhitespace(curNode) || DOMUtilities.isComment(curNode))) {
			lastNode = curNode;
			curNode = curNode.getPreviousSibling();
		}

		insertBeforeNode(parent, newNode, lastNode);
	}

	/**
	 * Return whether the node is a text node.
	 * 
	 * @return boolean Answer true if the node is a text node, false otherwise.
	 * @param node
	 *            org.w3c.dom.Node The node to check
	 */
	static public boolean isTextNode(Node node) {
		Assert.isNotNull(node);
		return (node.getNodeType() == Node.TEXT_NODE) || (node.getNodeType() == Node.CDATA_SECTION_NODE);
	}

	/**
	 * Return whether the node is entirely comment or not.
	 * 
	 * @return boolean Answer true if the node is whitespace, false otherwise.
	 * @param node
	 *            org.w3c.dom.Node The node to check
	 */
	static public boolean isComment(Node node) {
		Assert.isNotNull(node);

		return node.getNodeType() == Node.COMMENT_NODE;
	}

	/**
	 * Return whether the node is entirely whitepace or not.
	 * 
	 * @return boolean Answer true if the node is whitespace, false otherwise.
	 * @param node
	 *            org.w3c.dom.Node The node to check
	 */
	static public boolean isWhitespace(Node node) {
		Assert.isNotNull(node);

		if (node.getNodeType() != Node.TEXT_NODE)
			return false;

		Text textNode = (Text) node;
		String text = textNode.getData();
		if (text == null)
			return false;

		for (int i = 0; i < text.length(); i++) {
			if (!Character.isWhitespace(text.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Remove all the children of <node>
	 */
	static public void removeAllChildren(Node node) {
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			node.removeChild(list.item(i));
		}
	}

	// traverses the DOM starting at the specified node and returns a list
	// of nodes matching the search string

	static public ArrayList getAllNodes(Node node, String nodeName) {
		ArrayList nodeList = new ArrayList();

		String[] nodeNames = {nodeName};
		findAllNodes(node, nodeNames, nodeList);

		return nodeList;
	}

	// traverses the DOM starting at the specified node and returns a list
	// of nodes matching the search strings

	static public ArrayList getAllNodes(Node node, String[] nodeNamesArray) {
		ArrayList nodeList = new ArrayList();
		findAllNodes(node, nodeNamesArray, nodeList);

		return nodeList;
	}

	// recursive helper for getAllNodes
	static private void findAllNodes(Node node, String[] nodeNames, ArrayList results) {

		NodeList nodes = node.getChildNodes();
		if (nodes != null) {
			for (int i = 0; i < nodes.getLength(); i++) {
				for (int j = 0; j < nodeNames.length; j++) {
					if (nodes.item(i).getNodeName().equals(nodeNames[j])) {
						results.add(nodes.item(i));
					}
				}
				findAllNodes(nodes.item(i), nodeNames, results);
			}
		}
	}

	/**
	 * Returns the system defined JAXP document builder
	 */
	static public DocumentBuilder newDefaultDocumentBuilder(DOMLoadOptions options) throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(options.isValidate());
		dbf.setNamespaceAware(options.isValidate());
		/*
		 * Causes errors in IBM JDK try { dbf.setAttribute(Renderer.JAXP_SCHEMA_LANGUAGE,
		 * Renwderer.W3C_XML_SCHEMA); } catch (IllegalArgumentException x) { }
		 */
		try {
			dbf.setAttribute("http://apache.org/xml/features/allow-java-encodings", new Boolean(options.isAllowJavaEncodings())); //$NON-NLS-1$	        
		} catch (IllegalArgumentException ignore) {
			EcoreUtilitiesPlugin.logWarning("Warning: Parser does not support \"http://apache.org/xml/features/allow-java-encodings\"."); //$NON-NLS-1$
		}
		try {
			dbf.setAttribute("http://apache.org/xml/features/validation/schema", new Boolean(options.isValidate())); //$NON-NLS-1$
		} catch (IllegalArgumentException ignore) {
			dbf.setValidating(false);
			EcoreUtilitiesPlugin.logWarning("Warning: Parser does not support \"http://apache.org/xml/features/validation/schema\". Validation will be disabled."); //$NON-NLS-1$
		}
		try {
			dbf.setAttribute("http://apache.org/xml/features/dom/defer-node-expansion", Boolean.FALSE); //$NON-NLS-1$
		} catch (IllegalArgumentException ignore) {
			EcoreUtilitiesPlugin.logWarning("Warning: Parser does not support \"http://apache.org/xml/features/dom/defer-node-expansion\"."); //$NON-NLS-1$
		}
		dbf.setExpandEntityReferences(options.isExpandEntityRefererences());
		DocumentBuilder result = dbf.newDocumentBuilder();
		result.setErrorHandler(new ErrorHandler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
			 */
			public void error(SAXParseException arg0) throws SAXException {
				throw arg0;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
			 */
			public void fatalError(SAXParseException arg0) throws SAXException {
				throw arg0;
			}

			public void warning(SAXParseException arg0) throws SAXException {
				EcoreUtilitiesPlugin.logWarning(arg0);
			}

		});
		return result;
	}

	/**
	 * Creates a stub document, where the DocumentType is defined by the parameters.
	 */
	static public Document createNewDocument(String doctype, String publicId, String systemId) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = getDefaultDocumentBuilder();
		InputStream in = createHeaderInputStream(doctype, publicId, systemId, true);
		Document result = builder.parse(in);
		removeDummyEntity(result);
		removeExtraneousComments(result);
		return result;
	}

	public static Document loadDocument(InputStream in, DOMLoadOptions options, EntityResolver resolver) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DOMUtilities.newDefaultDocumentBuilder(options);
		builder.setEntityResolver(resolver);
		Document result = builder.parse(in);
		removeExtraneousComments(result);
		return result;
	}

	/**
	 * At the time of this writing, the DOM Level 2 APIs are not advanced enough for setting the
	 * document type; so the only parser independent way of accomplishing this is by creating a
	 * stream and parsing it.
	 */
	public static InputStream createHeaderInputStream(String doctype, String publicId, String systemId) {
		return createHeaderInputStream(doctype, publicId, systemId, false);
	}


	
	private static InputStream createHeaderInputStream(String doctype, String publicId, String systemId, boolean includeDummy) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// The prior code (which is still in the catch block), uses
		// the system default encoding [System.getProperty("file.encoding")];
		// on Z/OS this is Cp1047. The combination of "UTF-8" in the header
		// and "Cp1047" in the writer create an unusable input stream.

		PrintWriter writer;

		try {
			OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream, "UTF-8"); //$NON-NLS-1$
			// throws UnsupportedEncodingException
			writer = new PrintWriter(outputWriter);
		} catch (UnsupportedEncodingException e) {
			// Should never get here (earlier code)
			writer = new PrintWriter(outputStream); 
		}

		writeHeader(writer, doctype, publicId, systemId);
		if (includeDummy)
			addDummyEntity(writer);
		writer.flush();
		writer.close();

		byte[] bytes = outputStream.toByteArray();
		return new ByteArrayInputStream(bytes);
	}


	private static void writeHeader(PrintWriter writer, String doctype, String publicId, String systemId) {
		writer.write("<?xml version=\""); //$NON-NLS-1$
		writer.write("1.0"); //$NON-NLS-1$
		writer.write("\" encoding=\""); //$NON-NLS-1$
		writer.write("UTF-8"); //$NON-NLS-1$
		writer.write("\"?>"); //$NON-NLS-1$
		writer.println();

		if (doctype != null) {
			writer.write("<!DOCTYPE "); //$NON-NLS-1$
			writer.write(doctype);
			writer.write(" PUBLIC \""); //$NON-NLS-1$
			writer.write(publicId);
			writer.write("\" \""); //$NON-NLS-1$
			writer.write(systemId);
			writer.write("\">"); //$NON-NLS-1$
			writer.println();
		}
	}

	private static void addDummyEntity(PrintWriter writer) {
		Revisit.revisit();
		writer.println(DUMMY_ENTITY_NODE_STRING);
		//Major hack because we can not parse an empty document
	}

	private static void removeDummyEntity(Document doc) {
		doc.removeChild(getNodeChild(doc, DUMMY_ENTITY_STRING));
	}

	private static void removeExtraneousComments(Document doc) {
		//another major hack because of a bug in XML4J 4.0.7 that added all the
		//comments from the dtd to the document. Can be removed after we move up
		//Xerces levels
		Node aNode = doc.getFirstChild();
		while (aNode != null) {
			Node nextNode = aNode.getNextSibling();
			if (aNode.getNodeType() == Node.COMMENT_NODE)
				doc.removeChild(aNode);
			aNode = nextNode;
		}
	}

	/**
	 * For performance, cache a static instance of the JAXP registered document builder. Validation
	 * is disabled for this instance. If you need validation, use
	 * {@link #newDefaultDocumentBuilder(boolean, boolean, boolean)}
	 * 
	 * @return DocumentBuilder
	 * @throws ParserConfigurationException
	 *             if JAXP is not configured correctly
	 */
	public static DocumentBuilder getDefaultDocumentBuilder() throws ParserConfigurationException {
		if (defaultDocumentBuilder == null) {
			DOMLoadOptions opts = new DOMLoadOptions();
			opts.setAllowJavaEncodings(true);
			opts.setExpandEntityRefererences(true);
			opts.setValidate(false);
			defaultDocumentBuilder = newDefaultDocumentBuilder(opts);
			defaultDocumentBuilder.setEntityResolver(defaultEntityResolver);
			defaultDocumentBuilder.setErrorHandler(new ErrorHandler() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
				 */
				public void error(SAXParseException exception) throws SAXException {

				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
				 */
				public void fatalError(SAXParseException exception) throws SAXException {

				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
				 */
				public void warning(SAXParseException exception) throws SAXException {

				}

			});
		}

		return defaultDocumentBuilder;
	}

	/**
	 * @return
	 */
	public static EntityResolver getDefaultEntityResolver() {
		return defaultEntityResolver;
	}

	/**
	 * @param resolver
	 */
	public static void setDefaultEntityResolver(EntityResolver resolver) {
		defaultEntityResolver = resolver;
	}

}
