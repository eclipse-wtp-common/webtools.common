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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.xml.sax.Attributes;


/**
 * CacheEventNodes (CENOs) store information collected from SAX Events. This information can then be
 * used once all necessary SAX Events have been generated to create and/or set values on EMF model
 * objects.
 * 
 * CacheEventNodes (CENOs) have a simple lifecycle: initialize, collect data, commit, discard. When
 * initialized, CENOs will attempt to find the appropriate translator for a given XML element name,
 * and also create/set any necessary EMF model values. Data is collected as SAX character() events
 * are generated. On the SAX endElement event, the CENO is committed(), which is where it will
 * complete its processing to create EMF model features. In those cases where it cannot complete its
 * processing, it will defer its processing to the updateEMF() method of its parent. Defered
 * processing is necessary to handle EMF features that require read ahead cues from the XML. CENOs
 * will add themselves to their parents as children in a tree data structure. When an CENO
 * determines it is the golden piece of information required to instantiate its parent feature, it
 * will trigger its parent CENO to process the rest of the cached CENO tree. As mentioned, the
 * building of a CENO tree will only occur for nodes with read ahead cues.
 * 
 * discard() is invoked by init() to ensure that no junk state is left from a previous use of the
 * CENO. commit() will call discard as needed. Because of the use of discard, CENOs can be pooled
 * and reused. If a CENO determines that it is contained in a pool, it will manage its own release
 * from that pool. Self- management is necessary because of the way in which CENOs might cache
 * certain children while waiting to create the parent EMF feature.
 * 
 * @author mdelder
 */
public class CacheEventNode {

	public static final String ROOT_NODE = "EMF_ROOT_NODE"; //$NON-NLS-1$

	private String nodeName = null;
	private Translator translator = null;
	private Notifier emfOwner = null;
	private StringBuffer buffer = null;
	private List children = null;
	private int versionID;

	/*
	 * The internal data structure used to store the attributes is a String[]. The choice was made
	 * to use an array to avoid the creation of another object (probably a Hashtable) and to exploit
	 * array-access times to get the name and value of the attributes (opposed to full fledged
	 * method invocations).
	 *  
	 */
	private String[] attributes = null;
	private CacheEventNode parent = null;
	private CacheEventPool containingPool = null;
	private Boolean ignorable = null;

	public CacheEventNode(CacheEventPool containingPool) {
		this.containingPool = containingPool;
	}

	/**
	 * Lifecycle method. init(TranslatorResource) will configure this Adapter as a ROOT node.
	 * 
	 * This method will invoke discard() before completing its tasks.
	 */
	public void init(TranslatorResource resource) {
		this.discard();
		this.setEmfOwner(resource);
		this.setTranslator(resource.getRootTranslator());
		this.setVersionID(resource.getVersionID());
		this.nodeName = CacheEventNode.ROOT_NODE;
	}

	/**
	 * Lifecycle method. init(CacheEventNode, String, Attributes) will configure this Adapter to be
	 * a non-ROOT node of the Adapter data structure
	 * 
	 * This method will invoke discard() before completing its tasks.
	 */
	public void init(CacheEventNode parentArg, String nodeNameArg, Attributes attributesArg) {
		this.discard();
		this.nodeName = nodeNameArg;
		init(parentArg, attributesArg);
	}

	private void init(CacheEventNode parentRecord, Attributes attributesArg) {
		setParent(parentRecord);

		setAttributes(attributesArg);
		if (parent != null) {
			/* I am not the root */

			/*
			 * If the parent is part of the DOM Path, then we ignore it and interact with the grand
			 * parent
			 */
			if (parent.translator != null && parent.isInDOMPath()) {
				setParent(parent.getParent());
			}

			setVersionID(parent.getVersionID());
			if (parent.getEmfOwner() != null && parent.getTranslator() != null) {

				/* My parent had enough information to create themself */

				if (parent.getParent() != null) {
					setTranslator(parent.getTranslator().findChild(nodeName, parent.getEmfOwner(), getVersionID()));

				} else {
					setTranslator(parent.getTranslator());
				}

				if (this.translator == null) {
					/* Our translator is null! Ahh! Run! */
					throw new IllegalStateException("Parent Translator (" + parent.getTranslator() + //$NON-NLS-1$
								") did not find a Child Translator for \"" + //$NON-NLS-1$ 
								nodeName + "\"."); //$NON-NLS-1$
				}

				if (this.translator.getReadAheadHelper(nodeName) == null && !this.translator.isManagedByParent()) {
					/*
					 * I do not require a read ahead cue, and I am not managed by my parent so I can
					 * create an instance of my EMF object
					 */

					Notifier myEmfOwner = this.translator.createEMFObject(getNodeName(), null);
					setEmfOwner(myEmfOwner);
					this.translator.setMOFValue(parent.getEmfOwner(), myEmfOwner);
				}
				/*
				 * Else I require a read ahead value or I am being managed by my parent, so I have
				 * no need to create an EMF object
				 */
			}
			/*
			 * Else I am not mapped to the EMF stack (XML Elements found in the DOMPath are ignored)
			 */
		}
		/* processAttributes is guarded and will not execute unless ready */
		processAttributes();

	}

	/**
	 * commit() is invoked only if the CacheEventNode (CENO) has all the information they need and
	 * should be able to determine what to do to the EMF feature.
	 * 
	 * The commit() method will invoke discard() when it has completed its tasks, if needed. Thus,
	 * after invoking this method, the CENO may have no meaningful state. If discard() is invoked,
	 * all previously held reference will be released in order to be made eligible for Garbage
	 * Collection.
	 *  
	 */
	public void commit() {

		if (parent == null || this.isIgnorable()) {
			discard();
			releaseFromContainingPool();
			return;
		}

		ReadAheadHelper helper = null;
		Translator activeTranslator = getTranslator();
		Translator parentTranslator = getParent().getTranslator();

		if (parent != null && parent.getEmfOwner() == null) {

			/*
			 * Not enough information yet, add the CacheEventNode to the DOM Cache tree
			 */

			parent.appendToBuffer(this);
			if ((helper = getParent().getReadAheadHelper()) != null) {
				/*
				 * If the parentRecord's emfOwner is null, then it must not be initialized therefore
				 * it or one of its ancestors must require read ahead clues
				 * 
				 * The following if statement checks if the parent is the node waiting for a
				 * readAhead cue
				 */
				EObject parentOwner = null;
				if (helper.nodeValueIsReadAheadName(getNodeName())) {
					/* The readAheadName is the value of the qName child node */

					/* We have enough information to create the EmfOwner in the parent! */
					parentOwner = parentTranslator.createEMFObject(getParent().getNodeName(), getBuffer());

					/*
					 * Now we need to parse the cached DOM tree and update the emfOwner of the
					 * parent
					 */
					getParent().updateEMF(parentOwner);

				} else if (helper.nodeNameIsReadAheadName(getNodeName())) {
					/* The readAheadName is the actual name of the child node (qName) */

					/* We have enough information to create the EmfOwner in the parent! */
					parentOwner = parentTranslator.createEMFObject(getParent().getNodeName(), getNodeName());

					/*
					 * Now we need to parse the cached DOM tree and update the emfOwner of the
					 * parent
					 */
					getParent().updateEMF(parentOwner);
				}

			} /* Else an ancestor of the parent is waiting */

		} else {
			if (activeTranslator != null) {
				if (activeTranslator.isManagedByParent()) {

					Object value = activeTranslator.convertStringToValue(getNodeName(), null, getBuffer(), getParent().getEmfOwner());
					activeTranslator.setMOFValue(getParent().getEmfOwner(), value);
					processAttributes();
				} else {

					activeTranslator.setTextValueIfNecessary(getBuffer(), getEmfOwner(), getVersionID());
				}

			}
			discard();
			releaseFromContainingPool();
		}
	}

	/**
	 * Instruct the CacheEventNode to discard all references to make everything eligible for garbage
	 * collection. This should ONLY be called after commit has succeeded. In the case of EMF
	 * features that require a readAheadName, process not be completed in commit(), but rather will
	 * be defered to the updateEMF() method. This method was made private specifically because it
	 * could erase all information contained in the CacheEventNode before it has been processed.
	 *  
	 */
	private void discard() {
		translator = null;
		emfOwner = null;
		buffer = null;
		if (children != null)
			children.clear();
		children = null;
		attributes = null;
		parent = null;
	}

	private void releaseFromContainingPool() {
		if (containingPool != null)
			containingPool.releaseNode(this);
	}

	public boolean isIgnorable() {
		if (ignorable == null) {
			boolean result = false;
			if (this.translator != null) {
				if (this.translator.isEmptyContentSignificant()) {
					result = false;
				} else {
					String domPath = this.translator.getDOMPath();
					result = (domPath != null) ? domPath.indexOf(this.nodeName) >= 0 : false;
				}
			}
			ignorable = result ? Boolean.TRUE : Boolean.FALSE;
		}
		return ignorable.booleanValue();
	}

	/**
	 * Determines if a given child has a translator.
	 * 
	 * @param childNodeName
	 *            the name of the current XML child node
	 * @return true only if the childNodeName can be ignored (e.g. it is part of the DOM Path)
	 */
	public boolean isChildIgnorable(String childNodeName) {
		boolean result = false;

		Translator childTranslator = null;
		if (this.getTranslator() != null) {
			childTranslator = this.getTranslator().findChild(childNodeName, this.getEmfOwner(), this.getVersionID());

			if (childTranslator != null) {
				if (childTranslator.isEmptyContentSignificant()) {
					result = false;
				} else {
					String temp = null;
					result = ((temp = childTranslator.getDOMPath()) != null) ? temp.indexOf(childNodeName) >= 0 : false;
				}
			}
		}

		return result;
	}

	public boolean isInDOMPath() {
		boolean result = false;

		if (this.getTranslator() != null) {

			result = this.getNodeName().equals(this.getTranslator().getDOMPath());
		}

		return result;
	}

	public String toString() {
		StringBuffer output = new StringBuffer("CacheEventNode[");//$NON-NLS-1$
		output.append("nodeName=");//$NON-NLS-1$
		output.append(nodeName);
		output.append("; translator=");//$NON-NLS-1$
		output.append(translator);
		output.append("; emfOwner=");//$NON-NLS-1$
		try {
			output.append(emfOwner);
		} catch (RuntimeException re) {
			output.append("Could not render as string!");//$NON-NLS-1$
		}
		output.append("; buffer=");//$NON-NLS-1$
		output.append(this.buffer);
		output.append("; hasChildren=");//$NON-NLS-1$
		output.append((children != null && children.size() > 0));
		if (children != null) {
			for (int i = 0; i < this.children.size(); i++) {
				output.append("\n\tchildren(");//$NON-NLS-1$
				output.append(i);
				output.append("): ");//$NON-NLS-1$
				output.append(this.children.get(i));
			}
		}
		output.append("]");//$NON-NLS-1$
		return output.toString();
	}

	/**
	 * Updates the EMF model by creating EMF Features as necessary from the DOM Tree Cache
	 * 
	 * @param owner
	 */
	public void updateEMF(EObject owner) {
		this.setEmfOwner(owner);
		if (this.parent != null) {
			this.translator.setMOFValue((EObject) this.parent.getEmfOwner(), owner);
			this.processAttributes();
		}

		this.updateEMF();
	}

	/**
	 * The translator and the owner of the parent CENO passed to this method should be nonnull
	 */
	public void updateEMF() {
		if (this.children == null)
			return;

		CacheEventNode child = null;
		Translator childTranslator = null;
		Object value = null;
		if (this.getEmfOwner() != null) {
			Notifier parentOwner = this.getEmfOwner();
			Translator parentTranslator = this.getTranslator();
			for (int i = 0; i < this.children.size(); i++) {

				child = (CacheEventNode) this.children.get(i); /* Create the EMF feature */
				if (this.isChildIgnorable(child.getNodeName())) {
					this.addChildren(child.getChildren());
				} else {
					childTranslator = parentTranslator.findChild(child.getNodeName(), parentOwner, child.getVersionID());
					child.setTranslator(childTranslator);

					value = childTranslator.convertStringToValue(child.getNodeName(), null, child.getBuffer(), parentOwner);
					childTranslator.setMOFValue(parentOwner, value);

					if (childTranslator.isObjectMap()) {
						child.setEmfOwner((Notifier) value);
						childTranslator.setTextValueIfNecessary(child.getBuffer(), child.getEmfOwner(), getVersionID());
					}

					child.processAttributes();
					child.updateEMF(); /* update the EMF of the child */

				}
				child.discard();
				child.releaseFromContainingPool();
			}
			this.children = null;
		}
	}

	public void addChild(CacheEventNode child) {
		if (this.children == null) {
			this.children = new ArrayList();
		}
		if (parent != null && this.isIgnorable()) {
			parent.addChild(child);
		} else {
			this.children.add(child);
		}
	}

	protected void addChildren(List childrenArg) {
		if (this.children == null) {
			this.children = new ArrayList();
		}
		this.children.addAll(childrenArg);
	}

	public boolean removeChild(CacheEventNode child) {
		if (this.children == null) {
			return false;
		}
		return this.children.remove(child);
	}

	public List getChildren() {
		return this.children;
	}

	public ReadAheadHelper getReadAheadHelper() {
		if (this.translator != null && this.translator.hasReadAheadNames()) {
			return translator.getReadAheadHelper(nodeName);
		}
		return null;
	}


	/* See the documentation for the attributes field for info on how it is structured */
	public void setAttributes(Attributes attr) {

		/*
		 * The attributes returned from the parser may be stored by reference, so we must copy them
		 * over to a local data store
		 */
		if (attr != null && attr.getLength() > 0) {

			if (this.attributes == null) {
				this.attributes = new String[attr.getLength() * 2];
			}
			for (int i = 0; i < attr.getLength(); i++) {
				this.attributes[i] = attr.getQName(i);
				this.attributes[i + attr.getLength()] = attr.getValue(i);
			}

		}
	}

	/**
	 * processAttributes may be invoked multiple times. It is configured to only carry out the
	 * processing one time. After it successfully completes the construction of Translators and
	 * values it will discard the value of the attributes field by setting it to null.
	 *  
	 */
	public void processAttributes() {
		/* See the documentation for the attributes field for info on how it is structured */
		if (this.attributes != null && this.attributes.length > 0) {

			if (this.emfOwner != null && this.translator != null) {
				Translator attrTranslator = null;
				final int limit = this.attributes.length / 2;
				Object value = null;
				for (int i = 0; i < limit; i++) {

					/* Find the attribute translator by using the attribute name (attributes[i]) */
					attrTranslator = this.translator.findChild(this.attributes[i], this.emfOwner, this.versionID);

					if (attrTranslator != null) {

						/*
						 * Convert the value of corresponding attribute value (attributes[i+limit])
						 * to a meaningful value
						 */
						value = attrTranslator.convertStringToValue(this.attributes[i + limit], (EObject) this.emfOwner);
						attrTranslator.setMOFValue((EObject) this.emfOwner, value);
					}
				}

				/* Forget the attributes so we do not process them again */
				this.attributes = null;
			}
		}
	}

	/**
	 * Appends data to the buffer stored by this CENO. Text will be extracted from the data array
	 * begining at positiong <i>start </i> and ending at position <i>start+length </i>.
	 * 
	 * @param data
	 * @param start
	 * @param length
	 */
	public void appendToBuffer(char[] data, int start, int length) {

		if (parent != null && this.isIgnorable()) {
			parent.appendToBuffer(data, start, length);
			return;
		}

		if (buffer == null) {
			this.buffer = new StringBuffer();
		}

		/*
		 * acts as a more efficient form of "append". Using this method avoids the need to copy the
		 * data into its own data structure (e.g. String) before being added to the buffer
		 */
		this.buffer.insert(buffer.length(), data, start, length);

	}

	/**
	 * Add the given CENO as a child of this CENO.
	 * 
	 * @param record
	 */
	public void appendToBuffer(CacheEventNode record) {

		this.addChild(record);
	}

	public String getBuffer() {
		if (this.buffer == null) {
			return null;
		}
		return this.buffer.toString();
	}

	public Notifier getEmfOwner() {
		return emfOwner;
	}

	public CacheEventNode getParent() {
		return parent;
	}

	private void setParent(CacheEventNode record) {
		this.parent = record;
	}

	public Translator getTranslator() {
		return this.translator;
	}

	public void setEmfOwner(Notifier notifier) {

		this.emfOwner = notifier;
	}

	public void setTranslator(Translator translator) {
		this.translator = translator;
	}

	public String getNodeName() {
		return nodeName;
	}

	public int getVersionID() {

		if (this.parent == null) {
			try {
				return ((TranslatorResource) this.getEmfOwner()).getVersionID();

			} catch (RuntimeException re) {
			}
		}
		return this.versionID;
	}

	public void setVersionID(int i) {
		versionID = i;
	}


}