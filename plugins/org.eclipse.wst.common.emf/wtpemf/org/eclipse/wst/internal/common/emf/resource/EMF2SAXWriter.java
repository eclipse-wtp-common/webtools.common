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
package org.eclipse.wst.internal.common.emf.resource;


import java.util.List;
import java.util.Stack;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The EMF2SAXWriter handles the serialization of EMF Resources using SAX events. SAX events are
 * triggered to the content handler as the tree is being parsed. These events can then be written
 * into any stream wrapped by the ContentHandler.
 * 
 * @author mdelder
 */
public class EMF2SAXWriter {

	public static final String NAMESPACE = "";//"http://java.sun.com/xml/ns/j2ee"; //$NON-NLS-1$

	/* Used in those cases where no Attributes are necessary */
	private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();

	/**
	 * Serialize an EMF resource into an XML Stream using the given ContentHandler. Note that this
	 * method can also be used to copy a given EMF Resource if the EMF2SAXDocumentHandler is used as
	 * the given ContentHandler.
	 * 
	 * @param resource
	 * @param handler
	 */
	public void serialize(TranslatorResource resource, ContentHandler handler) throws SAXException {

		Translator rootTranslator = resource.getRootTranslator();
		EList contents = resource.getContents();

		if (contents.size() != 1) {
			throw new IllegalStateException("The contents of a resource may only contain one EMF Model Object."); //$NON-NLS-1$
		}
		handler.startDocument();
		EObject element = (EObject) contents.get(0);
		serialize(handler, element, rootTranslator, new WriterHints(resource));
		handler.endDocument();

	}

	private void serialize(ContentHandler handler, EObject target, Translator translator, WriterHints hints) throws SAXException {

		List mofChildren = null;
		Object rawValue = null;
		EObject newTarget = null;
		Translator currentChildTranslator = null;
		Translator nextTranslator = null;
		char[] characterData = null;
		String convertedValue = null;
		Attributes attributes = null;
		String childDomName = null;
		final int version = hints.getVersion();

		/*
		 * Processing hints are used to remember where are in the iteration of the translator's
		 * children. see the TranslatorFilter for more information on how this array is used.
		 */
		int[] processingHints = TranslatorFilter.createProcessingHints();

		String targetDomName = translator.getDOMName(target);

		attributes = getAttributes(translator, target, hints);

		handler.startElement(NAMESPACE, targetDomName, targetDomName, attributes);

		currentChildTranslator = TranslatorFilter.getNextObjectTranslator(translator, processingHints[TranslatorFilter.NEXT_START_HINT_INDX], processingHints, target, version);
		while (currentChildTranslator != null) {
			/* For each Child Translator of the Translator parameter passed into the method */

			/* Does the Translator have any MOF Children? */
			mofChildren = currentChildTranslator.getMOFChildren(target);
			openDomPathIfNecessary(handler, hints, currentChildTranslator, target, mofChildren);

			if (currentChildTranslator.isManagedByParent()) {
				/*
				 * Translators which are managed by their parents require less processing -- just
				 * convert their value to a string and write it out as the content of an XML element
				 */
				childDomName = currentChildTranslator.getDOMName(target);
				if (!currentChildTranslator.isEmptyTag()) {
					/* The Translator is not an Empty tag. Its text content is significant */

					if (mofChildren.size() > 0) {
						for (int j = 0; j < mofChildren.size(); j++) {

							/* Text only translators will not have open and close XML elements */
							if (!currentChildTranslator.isDOMTextValue())
								handler.startElement(NAMESPACE, childDomName, childDomName, EMPTY_ATTRIBUTES);

							rawValue = mofChildren.get(j);
							/* convertValueToString should always return a non-null String */
							convertedValue = currentChildTranslator.convertValueToString(rawValue, target);
							characterData = XMLEncoderDecoder.escape(convertedValue).toCharArray();
							handler.characters(characterData, 0, characterData.length);

							if (!currentChildTranslator.isDOMTextValue())
								handler.endElement(NAMESPACE, childDomName, childDomName);
						}
					}
				} else {
					/*
					 * The Translator is an Empty Element (its mere presence has significance) (e.g.
					 * <cascade-delete/>
					 */

					if (currentChildTranslator.isBooleanFeature()) {
						/* Boolean features may or may not be rendered */
						rawValue = mofChildren.get(0);
						if (rawValue != null && ((Boolean) rawValue).booleanValue()) {
							handler.startElement(NAMESPACE, childDomName, childDomName, EMPTY_ATTRIBUTES);
							handler.endElement(NAMESPACE, childDomName, childDomName);
						}

					} else {
						/* Always render any other Empty elements */
						handler.startElement(NAMESPACE, childDomName, childDomName, EMPTY_ATTRIBUTES);
						handler.endElement(NAMESPACE, childDomName, childDomName);
					}
				}
			} else {

				/* The Translator is a more complex feature, handle its processing recursively */
				for (int j = 0; j < mofChildren.size(); j++) {
					newTarget = (EObject) mofChildren.get(j);
					serialize(handler, newTarget, currentChildTranslator, hints);
				}
			}

			/* Fetch the next peer translator */
			nextTranslator = TranslatorFilter.getNextObjectTranslator(translator, processingHints[TranslatorFilter.NEXT_START_HINT_INDX], processingHints, target, version);

			closeDomPathIfNecessary(handler, hints, currentChildTranslator, nextTranslator, target, mofChildren);

			/*
			 * We needed to invoke closeDomPathIfNecessary() with the peer, now we move on to
			 * process that peer
			 */
			currentChildTranslator = nextTranslator;

		}
		handler.endElement(NAMESPACE, targetDomName, targetDomName);
	}

	/**
	 * Determines whether or not a DOM Path should be rendered. This method is particularly useful
	 * for determining whether Empty XML elements are relevant and should be written to the XML
	 * stream.
	 * 
	 * @param target
	 *            The EMF Target of the Translation
	 * @param currentChildTranslator
	 *            The current Translator
	 * @param mofChildren
	 *            The mofChildren that were found for the Translator on the Target
	 * @return
	 */
	private boolean shouldRenderDomPath(EObject target, Translator currentChildTranslator, List mofChildren) {
		return !currentChildTranslator.isEmptyContentSignificant() || (currentChildTranslator.shouldRenderEmptyDOMPath(target) || mofChildren.size() > 0);
	}

	/**
	 * openDomPathIfNecessary will write the current DOM Path to the serialization stream if it has
	 * not been written by a previous peer translator. The processing results in the collapse of
	 * Peer Translators with matching DOM Paths into a single XML parent element.
	 * 
	 * @param handler
	 *            The ContentHandler which is writing the XML result
	 * @param hints
	 *            A Global container for information specific to a single XML document
	 * @param currentChildTranslator
	 *            The active Translator being processed
	 * @param target
	 *            The EMF Target of the Translation
	 * @throws SAXException
	 */
	private void openDomPathIfNecessary(ContentHandler handler, WriterHints hints, Translator currentChildTranslator, EObject target, List mofChildren) throws SAXException {

		/* If the translator does not have a DOM Path, then we do nothing */
		if (currentChildTranslator.hasDOMPath() && shouldRenderDomPath(target, currentChildTranslator, mofChildren)) {

			String childDomPath = currentChildTranslator.getDOMPath();

			/*
			 * IsDomPathActive() will verify whether this DOM Path has already been written to the
			 * XML stream
			 */
			if (!hints.isDomPathActive(childDomPath)) {

				/*
				 * Write an open element for the DOM Path and "remember" that we have written it
				 */
				handler.startElement(NAMESPACE, childDomPath, childDomPath, EMPTY_ATTRIBUTES);
				hints.pushDomPath(childDomPath);
			}

		}
	}

	/**
	 * closeDomPathIfNecessary will determine whether the next peer Translator shares the active DOM
	 * Path of the current Translator. If the next peer Translator has the same DOM Path, no action
	 * will be taken (hence condensing the elements into a single XML parent). However, if the DOM
	 * Path differs (including the Next Peer Translator has no DOM Path) then the current DOM Path
	 * will be closed (a close XML element is generated.
	 * 
	 * @param handler
	 *            The ContentHandler which is writing the XML result
	 * @param hints
	 *            A Global container for information specific to a single XML document
	 * @param currentChildTranslator
	 *            The last Translator to have completed processing
	 * @param nextTranslator
	 *            The next peer Translator that will become active
	 * @param target
	 *            The EMF Target of the Translation
	 * @throws SAXException
	 */
	private void closeDomPathIfNecessary(ContentHandler handler, WriterHints hints, Translator currentChildTranslator, Translator nextTranslator, EObject target, List mofChildren) throws SAXException {

		if (currentChildTranslator.hasDOMPath() && shouldRenderDomPath(target, currentChildTranslator, mofChildren)) {
			String childDomPath = currentChildTranslator.getDOMPath();
			if (nextTranslator != null) { /*
										   * There are more peers after this element, we can peek
										   * ahead
										   */
				String nextPeerDomPath = nextTranslator.getDOMPath();
				if (nextPeerDomPath == null || !nextPeerDomPath.equals(childDomPath)) {
					handler.endElement(NAMESPACE, childDomPath, childDomPath);
					hints.popDomPath();
				}

			} else { /* This was the last child element, we must close the dompath */
				handler.endElement(NAMESPACE, childDomPath, childDomPath);
				hints.popDomPath();
			}
		}
	}

	/**
	 * Aggregate the Attribute translator children from a given translator. This method will request
	 * the AttributesImpl object from the WriterHints object. The WriterHints maintains this
	 * reusable collection to limit the requirement for new object creation.
	 * 
	 * @param translator
	 * @param target
	 * @param hints
	 * @return an initialized set of Attributes for the given Translator and EMF Target
	 */
	private Attributes getAttributes(Translator translator, EObject target, WriterHints hints) {

		AttributesImpl attributes = hints.getAttributeHolder();
		int version = hints.getVersion();
		Object rawValue = null;
		String convertedValue = null;
		String childDomName = null;
		Translator attributeTranslator = null;
		int[] processingHints = TranslatorFilter.createProcessingHints();

		while ((attributeTranslator = TranslatorFilter.getNextAttributeTranslator(translator, processingHints[TranslatorFilter.NEXT_START_HINT_INDX], processingHints, target, version)) != null) {

			List mofChildren = attributeTranslator.getMOFChildren(target);
			if (mofChildren.size() > 0) {
				for (int j = 0; j < mofChildren.size(); j++) {

					childDomName = attributeTranslator.getDOMName(target);
					rawValue = mofChildren.get(j);
					convertedValue = attributeTranslator.convertValueToString(rawValue, target);
					convertedValue = XMLEncoderDecoder.escape(convertedValue);
					attributes.addAttribute(NAMESPACE, childDomName, childDomName, "String", convertedValue); //$NON-NLS-1$
				}

			} else {
				childDomName = attributeTranslator.getDOMName(target);
				convertedValue = (String) attributeTranslator.getMOFValue(target);
				if (convertedValue != null)
					attributes.addAttribute(NAMESPACE, childDomName, childDomName, "String", convertedValue); //$NON-NLS-1$
			}
		}
		return attributes;
	}

	/**
	 * WriterHints is used to "remember" certain pieces of information while the writer is
	 * processing. Of particular interest are the version and the state of the DOM Path output.
	 * Consecutive elements with consistent (identical) DOM Paths are collapsed under a single XML
	 * element.
	 * 
	 * The WriterHints provides global state between recursive invocations of serialize(). It should
	 * be not be used to store local data (e.g. data that is only relevant to a single Translator in
	 * a given context).
	 * 
	 * The WriterHints also stores an AttributesImpl object that is re-used to store attributes. The
	 * getAttributes() method will request the Attributes Holder.
	 * 
	 * @author mdelder
	 */
	public final class WriterHints {
		private final TranslatorResource resource;
		private final Stack domStack = new Stack();
		private final AttributesImpl attributesImpl = new AttributesImpl();

		public WriterHints(TranslatorResource res) {
			this.resource = res;
		}

		/**
		 * Push a new domPath onto the stack
		 * 
		 * @param domPath
		 *            a DOMPath which has been written to the XML stream
		 */
		public void pushDomPath(String domPath) {

			if (domPath != null && domPath.length() > 0)
				domStack.push(domPath);
		}

		/**
		 * Pop the current domPath from the Array
		 */
		public void popDomPath() {

			if (!domStack.isEmpty())
				domStack.pop();
		}

		/**
		 * Determines if the given DOMPath has already been written to the XML stream
		 * 
		 * @param domPath
		 * @return true if the given DOMPath has already been written to the XML stream
		 */
		public boolean isDomPathActive(String domPath) {
			boolean result = false;
			if (!domStack.isEmpty()) {

				String currentDomPath = (String) domStack.peek();
				if (currentDomPath != null && domPath != null)
					result = currentDomPath.equals(domPath);
				else if (!(currentDomPath == null ^ domPath == null))
					result = true;
			}

			return result;
		}

		/**
		 * @return the version of the EMF Resource
		 */
		public int getVersion() {
			return this.resource.getVersionID();
		}

		/**
		 * Returns an empty AttributesImpl object to store attributes. Within the context of a given
		 * WriterHints object (and hence single XML document), the object returned is a singleton.
		 * The same AttributesImpl object is cleared and reused for each invocation.
		 * 
		 * @return an empty AttributesImpl object to store attributes
		 */
		public AttributesImpl getAttributeHolder() {
			this.attributesImpl.clear();
			return this.attributesImpl;
		}

	}
}