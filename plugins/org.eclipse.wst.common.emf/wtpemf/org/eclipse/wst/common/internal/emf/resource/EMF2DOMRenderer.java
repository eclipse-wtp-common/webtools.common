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
package org.eclipse.wst.common.internal.emf.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.internal.emf.utilities.DOMLoadOptions;
import org.eclipse.wst.common.internal.emf.utilities.DOMUtilities;
import org.eclipse.wst.common.internal.emf.utilities.Revisit;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class EMF2DOMRenderer extends AbstractRendererImpl implements Renderer {

	protected Map domAdapterRegistry;
	protected boolean needsToCreateDOM = true;
	protected Document document;

	/**
	 * Constructor for DOMRendererImpl.
	 */
	public EMF2DOMRenderer() {
		super();
		if (managesDOMAdapters())
			initDOMAdapterRegistry();
	}

	/**
	 * @see com.ibm.etools.emf2xml.Renderer#doLoad(InputStream, Map)
	 */
	public void doLoad(InputStream in, Map options) throws IOException {
		if ((in != null) || !useStreamsForIO()) {
			loadDocument(in, options);
			EMF2DOMAdapter adapter = createRootDOMAdapter();
			adapter.updateMOF();
		}
	}

	protected void loadDocument(InputStream in, Map options) throws IOException {
		try {
			DOMLoadOptions domOpts = new DOMLoadOptions();
			domOpts.setAllowJavaEncodings(true);
			domOpts.setExpandEntityRefererences(true);
			domOpts.setValidate(isValidating());
			document = DOMUtilities.loadDocument(in, domOpts, getResource().getEntityResolver());
			needsToCreateDOM = false;
		} catch (RuntimeException t_rex) {
			throw t_rex;
		} catch (IOException iox) {
			throw iox;
		} catch (Exception ex) {
			throw new WrappedException(ex);
		}
	}


	/**
	 * @see com.ibm.etools.emf2xml.Renderer#doSave(OutputStream, Map)
	 */
	public void doSave(OutputStream outputStream, Map options) throws IOException {
		createDOMTreeIfNecessary();
		serializeDocument(outputStream);
	}

	/**
	 * Subclasses should override if adapters are not cached within this renderer, e.g., they are
	 * stored in notifying Nodes
	 */
	protected boolean managesDOMAdapters() {
		return true;
	}

	protected void initDOMAdapterRegistry() {
		if (domAdapterRegistry == null)
			domAdapterRegistry = new HashMap();
	}

	public void registerDOMAdapter(Node node, EMF2DOMAdapter adapter) {
		domAdapterRegistry.put(node, adapter);
	}

	public EMF2DOMAdapter getExistingDOMAdapter(Node node) {
		return (EMF2DOMAdapter) domAdapterRegistry.get(node);
	}

	public void removeDOMAdapter(Node aNode, EMF2DOMAdapter anAdapter) {
		domAdapterRegistry.remove(aNode);
	}

	/**
	 * @see com.ibm.etools.emf2xml.Renderer#prepareToAddContents()
	 */
	public void prepareToAddContents() {
		// createDOMTreeIfNecessary();
	}

	protected Node createDOMTree() {
		createDocument();
		EMF2DOMAdapter adapter = createRootDOMAdapter();
		adapter.updateDOM();
		needsToCreateDOM = false;
		return document;
	}


	protected EMF2DOMAdapter createRootDOMAdapter() {
		EMF2DOMAdapter root = new EMF2DOMAdapterImpl(getResource(), document, this, getResource().getRootTranslator());
		registerDOMAdapter(document, root);
		return root;
	}


	protected void createDOMTreeIfNecessary() {
		if (needsToCreateDOM)
			createDOMTree();
	}

	/**
	 * Create a new Document given
	 * 
	 * @aResource.
	 */
	protected void createDocument() {
		TranslatorResource res = getResource();
		res.setDefaults();
		try {
			document = DOMUtilities.createNewDocument(res.getDoctype(), res.getPublicId(), res.getSystemId());
		} catch (ParserConfigurationException e) {
			throw new WrappedException(e);
		} catch (SAXException e) {
			throw new WrappedException(e);
		} catch (IOException e) {
			throw new WrappedException(e);
		}
	}

	public void serializeDocument(OutputStream out) throws IOException {
		/*
		 * OutputFormat format = createOutputFormat(); Serializer serializer =
		 * SerializerFactory.getSerializerFactory(Method.XML).makeSerializer(out, format);
		 * serializer.asDOMSerializer().serialize(document);
		 */
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			/*
			 * try { factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA); } catch
			 * (IllegalArgumentException x) { }
			 */
			Transformer transformer = factory.newTransformer();

			transformer.setOutputProperty(OutputKeys.ENCODING, getResource().getEncoding());
			transformer.setOutputProperty(OutputKeys.VERSION, getResource().getXMLVersion());
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); //$NON-NLS-1$
			if (getResource().getPublicId() != null)
				transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, getResource().getPublicId());
			if (getResource().getSystemId() != null)
				transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, getResource().getSystemId());
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$            
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$
			DOMSource source = new DOMSource(document.getDocumentElement());
			/* source.setSystemId(getResource().getSystemId()); */
			transformer.transform(source, new StreamResult(out));
		} catch (TransformerConfigurationException e) {
			Logger.getLogger().logError(e);
		} catch (TransformerFactoryConfigurationError e) {
			Logger.getLogger().logError(e);
		} catch (TransformerException e) {
			Logger.getLogger().logError(e);
		} finally {
		}
	}

	/*
	 * protected OutputFormat createOutputFormat() { OutputFormat format = new OutputFormat();
	 * format.setIndenting(true); format.setLineSeparator(DOMUtilities.NEWLINE_STRING);
	 * //$NON-NLS-1$ format.setEncoding(getResource().getEncoding());
	 * format.setVersion(getResource().getXMLVersion()); return format; }
	 */

	public void replaceDocumentType(String docTypeName, String publicId, String systemId) {
		Revisit.revisit();
		Document newDoc = null;
		// Need be able to update the doctype directly on the existing document; right now can't
		// because
		// of limitations on parser neutral apis

		try {
			newDoc = DOMUtilities.createNewDocument(docTypeName, publicId, systemId);
		} catch (ParserConfigurationException e) {
			throw new WrappedException(e);
		} catch (SAXException e) {
			throw new WrappedException(e);
		} catch (IOException e) {
			throw new WrappedException(e);
		}


		replaceNode(document.getDocumentElement(), newDoc, newDoc);
		readapt(document, newDoc);
		document = newDoc;
	}

	protected void replaceNode(Node oldChild, Node newParent, Document newDoc) {
		Node newChild = newDoc.importNode(oldChild, false);
		newParent.appendChild(newChild);
		readapt(oldChild, newChild);
		NodeList children = oldChild.getChildNodes();
		int length = children.getLength();
		for (int i = 0; i < length; i++) {
			replaceNode(children.item(i), newChild, newDoc);
		}
	}

	public void preUnload() {
		EMF2DOMAdapter adapter = (EMF2DOMAdapter) EcoreUtil.getAdapter(resource.eAdapters(), EMF2DOMAdapter.ADAPTER_CLASS);
		if (adapter != null) {
			adapter.removeAdapters(adapter.getNode());
		}
	}

	protected void readapt(Node oldChild, Node newChild) {
		EMF2DOMAdapter adapter = getExistingDOMAdapter(oldChild);
		if (adapter != null) {
			registerDOMAdapter(newChild, adapter);
			// Some nodes are managed by the parent and thus the
			// node should not be set on the parent adapter
			if (adapter.getNode() == oldChild)
				adapter.setNode(newChild);
		}
	}

	public int getVersionID() {
		return getResource().getVersionID();
	}

}
