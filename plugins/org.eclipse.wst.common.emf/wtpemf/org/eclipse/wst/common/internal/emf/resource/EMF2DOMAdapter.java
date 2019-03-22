/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;


import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.w3c.dom.Node;

public interface EMF2DOMAdapter extends Adapter {


	Class ADAPTER_CLASS = EMF2DOMAdapter.class;

	/**
	 * Return the DOM node that the target of this adapter maps to. If the target MOF object maps to
	 * more than one DOM node, this node is the top-most node.
	 */
	Node getNode();

	void setNode(Node aNode);

	/**
	 * Set to false and notification of changes from both the DOM node and the MOF object will be
	 * ignored.
	 */
	boolean isNotificationEnabled();

	/**
	 * Set to false and notification of changes from both the DOM node and the MOF object will be
	 * ignored.
	 */
	void setNotificationEnabled(boolean isEnabled);

	/**
	 * Updates the DOM tree for this adapter from the current values of the MOF Object. This method
	 * updates ALL the DOM nodes from all the MOF attributes.
	 */
	void updateDOM();

	/**
	 * Updates the MOF Object from the DOM tree. All the children of the DOM tree are updated into
	 * the MOF object.
	 */
	void updateMOF();

	public void updateDOMFeature(Translator map, Node node, EObject mofObject);

	public void updateMOFFeature(Translator map, Node node, EObject mofObject);

	EObject getEObject();

	/**
	 * Return true if MOF object is a proxy.
	 */
	boolean isMOFProxy();

	/**
	 * Remove the DOM adapters from the node AND all its child nodes, recursively.
	 */
	void removeAdapters(Node node);
}