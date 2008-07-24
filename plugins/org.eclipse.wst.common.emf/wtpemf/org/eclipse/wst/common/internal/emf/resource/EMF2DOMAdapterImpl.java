/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.common.internal.emf.utilities.Assert;
import org.eclipse.wst.common.internal.emf.utilities.DOMUtilities;
import org.eclipse.wst.common.internal.emf.utilities.EtoolsCopySession;
import org.eclipse.wst.common.internal.emf.utilities.ExtendedEcoreUtil;
import org.eclipse.wst.common.internal.emf.utilities.FeatureValueConversionException;
import org.eclipse.wst.common.internal.emf.utilities.Revisit;
import org.eclipse.wst.common.internal.emf.utilities.StringUtil;
import org.eclipse.wst.common.internal.emf.utilities.TranslatorService;
import org.eclipse.wst.common.internal.emf.utilities.WFTUtilsResourceHandler;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


public class EMF2DOMAdapterImpl extends AdapterImpl implements EMF2DOMAdapter {

	final protected static boolean fDebug = false;

	protected boolean fNotificationEnabled = true;

	protected Node fNode;

	protected Translator fTranslator;

	protected EMF2DOMRenderer fRenderer;

	protected Translator[] childTranslators;

	protected boolean isRoot = false;
	
	private static final String PLATFORM = "org.eclipse.core.runtime.Platform"; //$NON-NLS-1$
	private static final String ISRUNNING = "isRunning"; //$NON-NLS-1$

	private class DependencyAdapter extends org.eclipse.emf.common.notify.impl.AdapterImpl {

		static final String KEY = "EMF2DOMDependencyAdapter"; //$NON-NLS-1$

		public void notifyChanged(Notification msg) {
			EMF2DOMAdapterImpl.this.notifyChanged(msg);
		}

		/**
		 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(Object)
		 */
		public boolean isAdapterForType(Object type) {
			return KEY.equals(type);
		}
	}

	/**
	 * Constructor for root adapters.
	 */
	public EMF2DOMAdapterImpl(TranslatorResource resource, Document document, EMF2DOMRenderer renderer, Translator translator) {
		this((Notifier) resource, (Node) document, renderer, translator);
		isRoot = true;
	}

	/**
	 * Construct an Adapter given an EObject and a node
	 */
	public EMF2DOMAdapterImpl(Notifier object, Node node, EMF2DOMRenderer renderer, Translator translator) {
		super();
		setTarget(object);
		fNode = node;
		fRenderer = renderer;
		fTranslator = translator;
		initChildTranslators();
		addEMFAdapter();
		addDOMAdapter();
		addDependencyAdapterIfNecessary();
	}

	/**
	 * Construct an adapter from a DOM Node. The EObject will be created
	 */
	public EMF2DOMAdapterImpl(Node node, EMF2DOMRenderer renderer, Translator translator) {
		fNode = node;
		fRenderer = renderer;
		fTranslator = translator;
		setTargetFromNode();
		initChildTranslators();
		addEMFAdapter();
		addDOMAdapter();
		addDependencyAdapterIfNecessary();

	}

	protected void addDependencyAdapterIfNecessary() {
		if (!fTranslator.isDependencyParent())
			return;
		EObject child = fTranslator.basicGetDependencyObject(getEObject());
		if (child != null)
			addDependencyAdapter(child);
	}

	protected void initChildTranslators() {
		List children = new ArrayList();
		boolean isRunning = false; //is the OSGI platform running ?
		try {
			// If the Platform class can be found, then continue to check if the OSGI platform is running
			Class clazz = Class.forName(PLATFORM);
			Method m = clazz.getMethod(ISRUNNING, null);
			isRunning = ((Boolean)m.invoke(clazz, null)).booleanValue();
		} catch (ClassNotFoundException e) {
		     // Ignore because this must be in a non_OSGI environment
		} catch (SecurityException e) {
			 // Ignore because this must be in a non_OSGI environment
		} catch (NoSuchMethodException e) {
			 // Ignore because this must be in a non_OSGI environment
		} catch (IllegalArgumentException e) {
			 // Ignore because this must be in a non_OSGI environment
		} catch (IllegalAccessException e) {
			 // Ignore because this must be in a non_OSGI environment
		} catch (InvocationTargetException e) {
			 // Ignore because this must be in a non_OSGI environment
		}	
		//Check for extended child translators because we are in OSGI mode
		if (isRunning) {
			Translator[] extendedChildren = TranslatorService.getInstance().getTranslators();
	        for (int i = 0; i < extendedChildren.length; i++) {
	        	if (extendedChildren[i] != null)
	            	children.add(extendedChildren[i]);
	        }
		}
		
		
		children.addAll(Arrays.asList(fTranslator.getChildren(getTarget(), fRenderer.getVersionID())));

		VariableTranslatorFactory factory = fTranslator.getVariableTranslatorFactory();
		if (factory != null) {
			String domName = null;
			NamedNodeMap map = fNode.getAttributes();

			if (map != null) {
				int length = map.getLength();
				for (int i = 0; i < length; i++) {
					Node attrNode = map.item(i);
					domName = attrNode.getNodeName();
					//Handle variable translators
					Translator t = fTranslator.findChild(domName, getTarget(), fRenderer.getVersionID());
					if (t != null && !children.contains(t))
						children.add(t);
				}
			}
			List childrenFromEMF = factory.create(getTarget());
			if (childrenFromEMF != null)
				children.addAll(childrenFromEMF);
		}

		childTranslators = (Translator[]) children.toArray(new Translator[children.size()]);
	}

	/**
	 * Set to false and notification of changes from both the DOM node and the MOF object will be
	 * ignored.
	 */
	public boolean isNotificationEnabled() {
		return fNotificationEnabled;
	}

	/**
	 * Set to false and notification of changes from both the DOM node and the MOF object will be
	 * ignored.
	 */
	public void setNotificationEnabled(boolean isEnabled) {
		fNotificationEnabled = isEnabled;
	}

	public boolean isAdapterForType(Object type) {
		return EMF2DOMAdapter.ADAPTER_CLASS == type;
	}

	protected void addEMFAdapter() {
		target.eAdapters().add(this);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(shortClassName(this));
		sb.append('(');
		sb.append(getNode().getNodeName());
		sb.append(',');
		sb.append(shortClassName(getTarget()));
		sb.append(')');
		return sb.toString();
	}

	protected String shortClassName(Object o) {
		if (o != null) {
			String cn = o.getClass().getName();
			int i = cn.lastIndexOf('.');
			return cn.substring(i + 1, cn.length());
		}
		return null;
	}

	/*
	 * Prints out a MOF notification for debugging
	 */
	protected void debugMOFNotify(Notification msg) {
		if (fDebug) {
			String notifType = ""; //$NON-NLS-1$
			switch (msg.getEventType()) {
				case Notification.ADD :
					notifType = "ADD"; //$NON-NLS-1$
					break;
				case Notification.REMOVE :
					notifType = "REMOVE"; //$NON-NLS-1$
					break;
				case Notification.ADD_MANY :
					notifType = "ADD_MANY"; //$NON-NLS-1$
					break;
				case Notification.REMOVE_MANY :
					notifType = "REMOVE_MANY"; //$NON-NLS-1$
					break;
				case Notification.SET : {
					if (msg.getPosition() == Notification.NO_INDEX)
						notifType = "REPLACE"; //$NON-NLS-1$
					else
						notifType = "SET"; //$NON-NLS-1$
					break;
				}
				case Notification.UNSET :
					notifType = "UNSET"; //$NON-NLS-1$
					break;
			}

			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("MOF Change: " + notifType); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tnotifier      : " + msg.getNotifier()); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tchangedFeature: " + msg.getFeature()); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\toldValue      : " + msg.getOldValue()); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tnewValue      : " + msg.getNewValue()); //$NON-NLS-1$
		}
	}

	/*
	 * Override this method to create the feature maps for the adapter.
	 */
	protected Translator[] getChildTranslators() {
		return childTranslators;
	}

	protected Translator findTranslator(Notification not) {
		if (not.getFeature() == null)
			return null;
		Translator[] maps = getChildTranslators();
		for (int i = 0; i < maps.length; i++) {
			if (maps[i].isMapFor(not.getFeature(), not.getOldValue(), not.getNewValue()))
				return maps[i];
		}
		return null;
	}

	/**
	 * Update all the children of the target MOF object in the relationship described by
	 * 
	 * @map.
	 * 
	 * @param map
	 *            com.ibm.etools.mof2dom.AttributeTranslator Describes the mapping from the MOF
	 *            attribute name to the DOM node name
	 */
	protected void primUpdateDOMMultiFeature(Translator map, Node node, List mofChildren, List domChildren, Notifier owner) {

		//Used for inserting primitives
		List inorderDOMChildren = null;
		if (!map.isObjectMap() || map.isManagedByParent()) {
			inorderDOMChildren = new ArrayList();
			inorderDOMChildren.addAll(domChildren);
		}
		Node parent = findDOMPath(node, map, false);

		// Go though the MOF children checking to see if the corresponding
		// MOF Adapter children exists. If not, create the adapter.
		// Also handles reordering children that have moved.
		int i = 0;
		for (; i < mofChildren.size(); i++) {
			Object child = mofChildren.get(i);
			EObject mofChild = null;

			EMF2DOMAdapter adapter = null;

			// Check to see if the child is a MOF Object.
			if (!map.isManagedByParent() && child instanceof EObject) {
				mofChild = (EObject) mofChildren.get(i);
				adapter = getExistingAdapter(mofChild);
			}

			if (adapter != null && i < domChildren.size() && domChildren.get(i) == adapter.getNode())
				continue;
			if (adapter != null) {
				if (domChildren.isEmpty())
					continue;
				// A node has been reordered in the list
				Node reorderNode = adapter.getNode();
				Node insertBeforeNode = reorderNode;
				if (i < domChildren.size() && domChildren.get(i) != reorderNode) {
					insertBeforeNode = (Node) domChildren.get(i);
				}
				domChildren.remove(reorderNode);
				domChildren.add(i, reorderNode);
				if (reorderNode != insertBeforeNode) {
					reorderDOMChild(parent, reorderNode, insertBeforeNode, map);
				}
			} else {
				// A new node has been added, create it
				parent = createDOMPath(node, map);
				if (mofChild != null) {
					adapter = createAdapter(mofChild, map);
					Node newNode = adapter.getNode();
					Node insertBeforeNode = findInsertBeforeNode(parent, map, mofChildren, i, domChildren);
					DOMUtilities.insertBeforeNodeAndWhitespace(parent, newNode, insertBeforeNode);
					domChildren.add(i, newNode);
					boolean notificationFlag = adapter.isNotificationEnabled();
					adapter.setNotificationEnabled(false);
					try {
						indent(newNode, map);
					} finally {
						adapter.setNotificationEnabled(notificationFlag);
					}
					adapter.updateDOM();
				} else {
					// The mof feature is a collection of primitives.
					// create a new dom node and listen to it.
					Element newNode = createNewNode(null, map);
					Node insertBeforeNode = findInsertBeforeNode(parent, map, mofChildren, i, inorderDOMChildren);
					DOMUtilities.insertBeforeNodeAndWhitespace(parent, newNode, insertBeforeNode);
					indent(newNode, map);
					addDOMAdapter(newNode); // Hook up listeners
					domChildren.add(i, newNode);
					inorderDOMChildren.add(newNode);
					Text newText = parent.getOwnerDocument().createTextNode(map.convertValueToString(child, (EObject) owner));
					DOMUtilities.insertBeforeNode(newNode, newText, null);
				}
			}
		}

		// Remove any remaining adapters.
		for (; i < domChildren.size(); i++) {
			removeDOMChild(parent, (Element) domChildren.get(i));
		}

		// If there are no MOF children, remove any unnecessary DOM node paths
		if (mofChildren.size() == 0 && map.hasDOMPath()) {
			if (map.shouldRenderEmptyDOMPath((EObject) owner))
				createDOMPath(node, map);
			else
				removeDOMPath(node, map);
		}

	}

	/**
	 * Update all the children of the target MOF object in the relationship described by
	 * 
	 * @map.
	 * 
	 * @param map
	 *            com.ibm.etools.mof2dom.AttributeTranslator Describes the mapping from the MOF
	 *            attribute name to the DOM node name
	 */
	protected void primUpdateMOFMultiFeature(Translator map, Node node, List mofChildren, List domChilren) {

		Hashtable nodeToAdapter = new Hashtable();

		for (int i = 0; i < mofChildren.size(); i++) {
			EMF2DOMAdapter adapter = getExistingAdapter((EObject) mofChildren.get(i));
			if (adapter != null)
				nodeToAdapter.put(adapter.getNode(), adapter);
		}

		// Go though the DOM children checking to see if the corresponding
		// MOF Adapter children exists. If not, create the adapter.
		// Also handles reordering children that have moved.
		int i = 0;
		int mofIndex = 0;
		List adaptersToUpdate = new ArrayList();
		for (; i < domChilren.size(); i++) {
			Element childNode = (Element) domChilren.get(i);
			EMF2DOMAdapter adapter = i < mofChildren.size() ? getExistingAdapter((EObject) mofChildren.get(i)) : null;
			if (adapter != null && !adapter.isMOFProxy() && adapter.getNode() == childNode) {
				// Because the adapter is processing STRUCTURE_CHANGED from the
				// DOM, we
				// must update all the way down the tree since anything under
				// the notifying
				// DOM node could have changed.
				adapter.updateMOF();
				mofIndex++;
				continue;
			}

			adapter = (EMF2DOMAdapter) nodeToAdapter.get(childNode);
			if (adapter != null) {
				reorderIfNecessary((EList) mofChildren, adapter.getEObject(), mofIndex);
				mofIndex++;
			} else {
				adapter = createAdapter(childNode, map);
				if (adapter != null) {
					try {
						//We don't want to push anything back to the child dom
						adapter.setNotificationEnabled(false);
						map.setMOFValue(getTarget(), adapter.getTarget(), mofIndex);
					} finally {
						adapter.setNotificationEnabled(true);
					}

					adaptersToUpdate.add(adapter);
					mofIndex++;
				}
			}
		}

		// Remove any remaining adapters.
		//make a copy so we remove all items - bug 192468 
				Object[] childrenArray = mofChildren.toArray();
				for (; i < childrenArray.length; i++) {
					removeMOFValue((EObject) childrenArray[i], map);
		 		}

		// The adapters cannot be updated as they created. We must wait until
		// all of the adapters are created and removed before updating,
		// otherwise
		// we can get in a state where there are adapters fighting with
		// eachother
		// (one for the old node and one for the new node).
		for (int j = 0; j < adaptersToUpdate.size(); j++) {
			((EMF2DOMAdapter) adaptersToUpdate.get(j)).updateMOF();
		}
	}

	/**
	 * Removes a feature's value.
	 * 
	 * @param childAdapter
	 *            com.ibm.etools.mof2dom.EMF2DOMAdapter The child to remove
	 * @param mofAttributeName
	 *            String The name of the mofAttribute to remove the child from.
	 */
	protected void removeMOFValue(EObject value, Translator translator) {

		if (value == null)
			return;

		EMF2DOMAdapter adapter = (EMF2DOMAdapter) EcoreUtil.getExistingAdapter(value, EMF2DOMAdapter.ADAPTER_CLASS);
		if (adapter != null) {
			// Remove the adapter from BOTH the MOF Object and the DOM Nodes
			removeAdapters(adapter.getNode());
			value.eAdapters().remove(adapter);
		}

		EStructuralFeature feature = translator.getFeature();
		boolean doUnload = feature == null || (translator.isObjectMap() && ((EReference) feature).isContainment());

		// translator.removeMOFValue() was here originally

		// Unload the objects.
		if (doUnload)
			ExtendedEcoreUtil.unload(value);

		// Remove the MOF value
		translator.removeMOFValue(getTarget(), value);
	}

	protected void reorderIfNecessary(EList emfChildren, EObject eObj, int emfIndex) {
		int currIndex = emfChildren.indexOf(eObj);
		if (currIndex > -1 && currIndex != emfIndex)
			emfChildren.move(emfIndex, eObj);
	}

	/**
	 * default is to do nothing; subclasses can override
	 */
	protected void indent(Node newNode, Translator map) {

	}

	protected Node findInsertBeforeNode(Node parentNode, Translator map, List mofList, int mofInx, List domList) {
		Node insertBeforeNode = null;

		// If there are no current dom children for this map, find the initial
		// insert pos.
		if (domList.size() == 0)
			return findInitialInsertBeforeNode(parentNode, map);

		// If some dom nodes then find the correct one to insert before.
		int i = mofInx + 1;
		while (i < mofList.size() && insertBeforeNode == null) {
			// Start at the mofInx passed in and look forward for the first
			// adapted
			// MOF object. Use that node as the insert before node.
			Object o = mofList.get(i);
			if (!map.isObjectMap() || map.isManagedByParent())
				break;
			EObject tMOFObject = (EObject) o;
			EMF2DOMAdapter tAdapter = (EMF2DOMAdapter) EcoreUtil.getExistingAdapter(tMOFObject, EMF2DOMAdapter.ADAPTER_CLASS);
			if (tAdapter != null) {
				insertBeforeNode = tAdapter.getNode();
			}
			i++;
		}

		// Handle inserting at the end of the list
		if (insertBeforeNode == null)
			insertBeforeNode = DOMUtilities.getNextNodeSibling((Node) domList.get(domList.size() - 1));
		if (insertBeforeNode == null)
			insertBeforeNode = ((Node) domList.get(domList.size() - 1)).getNextSibling();

		return insertBeforeNode;
	}

	protected Node findInitialInsertBeforeNode(Node parentNode, Translator mapNode) {
		Translator[] maps = getChildTranslators();

		// First, skip past all the maps in the ordered collection
		// of maps. We want to begin the search with this node.
		int i = 0;
		for (; i < maps.length; i++) {
			if (maps[i] == mapNode)
				break;
		}

		// Now search go through each map node until a child node matching
		// its DOM name is found.
		Node insertBeforeNode = null;
		for (int j = i; j < maps.length && insertBeforeNode == null; j++) {
			NodeList childNodes = parentNode.getChildNodes();
			Translator nodeToFindMap = maps[j];
			for (int k = 0; k < childNodes.getLength(); k++) {
				Node node = childNodes.item(k);
				if (nodeToFindMap.isMapFor(node.getNodeName())) {
					insertBeforeNode = node;
					break;
				}
			}
		}
		return insertBeforeNode;
	}

	/*
	 * Traverses the path that <map> specifies. Returns the last node of the path that was able to
	 * be traversed or null if the path could not be traversed. The <addAdapters> boolean is used to
	 * determine if the receiver is added as an adapter to every node found on the path.
	 */
	protected Node findDOMPath(Node parent, Translator map, boolean addAdapters) {

		String path = map.getDOMPath();
		Node curNode = parent;
		Iterator iter = DOMUtilities.createPathIterator(path);

		while (curNode != null && iter.hasNext()) {
			String nodeName = (String) iter.next();
			curNode = DOMUtilities.getNodeChild(curNode, nodeName);
			if (addAdapters && curNode != null) {
				addDOMAdapter(curNode);
			}
		}
		return curNode;
	}

	/**
	 * Return the list of DOM node children that currently exist with the specified tagname.
	 */
	protected List getDOMChildren(Node node, Translator map) {
		Node parent = findDOMPath(node, map, true);
		if (parent != null)
			return DOMUtilities.getNodeChildren(parent, map.getDOMNames());
		return new ArrayList();
	}

	protected EMF2DOMAdapter getExistingAdapter(EObject refObject) {
		EMF2DOMAdapter adapter = (EMF2DOMAdapter) EcoreUtil.getExistingAdapter(refObject, EMF2DOMAdapter.ADAPTER_CLASS);
		if (adapter != null && adapter.isMOFProxy()) {
			removeDOMAdapter(adapter.getNode(), adapter);
			refObject.eAdapters().remove(adapter);
			adapter = null;
		}
		return adapter;
	}

	protected void primAddDOMAdapter(Node aNode, EMF2DOMAdapter anAdapter) {
		fRenderer.registerDOMAdapter(aNode, anAdapter);
	}

	protected EMF2DOMAdapter primGetExistingAdapter(Node aNode) {
		return fRenderer.getExistingDOMAdapter(aNode);
	}

	protected void removeDOMAdapter(Node aNode, EMF2DOMAdapter anAdapter) {
		fRenderer.removeDOMAdapter(aNode, anAdapter);
	}

	protected void addDOMAdapter(Node childNode) {

		// Only add the adapter if this is an child node that will not be
		// adapted. For instance a subtree that maps to a MOF attribute
		// setting.
		if (childNode.getNodeType() == Node.ELEMENT_NODE) {
			EMF2DOMAdapter attrAdapter = primGetExistingAdapter(childNode);

			if (attrAdapter == null || attrAdapter.getNode() != getNode()) {
				// If the node is adapted, but not by this adapter then remove
				// it. This happens to non-object children when the parent tag
				// name is changed.
				removeDOMAdapter(childNode, attrAdapter);

				if (fDebug) {
					org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tCHILD: Adding DOM adapter: " + this); //$NON-NLS-1$
					org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\t\tto: " + childNode); //$NON-NLS-1$
				}
				primAddDOMAdapter(childNode, this);
			}
		}
	}

	/**
	 * Reorder a child before a given node
	 */
	protected void reorderDOMChild(Node parentNode, Node childNode, Node insertBeforeNode, Translator map) {
		try {
			removeDOMChild(parentNode, childNode, false);
			parentNode.insertBefore(childNode, insertBeforeNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected String getNewlineString(Node node) {
		return DOMUtilities.NEWLINE_STRING;
	}

	protected String primGetIndentString(Node node) {
		return DOMUtilities.getIndentString(node);
	}

	/**
	 * Remove a child node
	 */
	protected void removeDOMChild(Node parentNode, Node childNode) {
		removeDOMChild(parentNode, childNode, true);
	}

	/**
	 * Remove a child node
	 */
	protected void removeDOMChild(Node parentNode, Node childNode, boolean removeAdapter) {
		try {
			if (childNode == null)
				return;
			// Look for any whitespace preceeding the node being
			// removed and remove it as well.
			Text prevText = DOMUtilities.getPreviousTextSibling(childNode);
			if (prevText != null && DOMUtilities.isWhitespace(prevText)) {
				parentNode.removeChild(prevText);
			}
			// Remove the node.
			if (removeAdapter)
				removeAdapters(childNode);
			parentNode.removeChild(childNode);
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}

	/**
	 * Remove the DOM adapters from the node AND all its child nodes, recursively.
	 */
	public void removeAdapters(Node node) {
		EMF2DOMAdapter adapter = primGetExistingAdapter(node);
		if (adapter != null) {
			// Remove the adapter from both the DOM node and the MOF Object.
			removeDOMAdapter(node, adapter);
			if (adapter.getNode() == node) {
				Notifier localTarget = adapter.getTarget();
				if (localTarget != null)
					localTarget.eAdapters().remove(adapter);
			}
		}
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			removeAdapters(n);
		}
	}

	/**
	 * Creates the path specified by <map>under <node>. Only the portion of the path that does not
	 * exist (if any) is created
	 * 
	 * @param node
	 *            org.w3c.dom.Node
	 * @param map
	 *            com.ibm.etools.mof2dom.Translator
	 */
	protected Node createDOMPath(Node node, Translator map) {
		Iterator i = DOMUtilities.createPathIterator(map.getDOMPath());
		Node curNode = node;
		while (i.hasNext()) {
			String nodeName = (String) i.next();
			curNode = findOrCreateNode(node, map, nodeName);
		}
		return curNode;
	}

	protected Element findOrCreateNode(Node parent, Translator map, String segment) {
		Node node = DOMUtilities.getNodeChild(parent, segment);
		if (node == null) {
			// The node did not already exist, create it.
			Document doc = parent.getOwnerDocument();
			node = doc.createElement(segment);
			if (map.isEmptyTag())
				setEmptyTag((Element) node);

			Node insertBeforeNode = findInitialInsertBeforeNode(parent, map);
			DOMUtilities.insertBeforeNodeAndWhitespace(parent, node, insertBeforeNode);
			indent(node, map);
			addDOMAdapter(node); // Hook up listeners
		}
		return (Element) node;
	}

	/**
	 * Remove the node passed in if it has only whitespace nodes as children
	 * 
	 * @param node
	 *            org.w3c.dom.Node The node to check
	 */
	protected void removeIfEmpty(Node node) {
		NodeList nl = node.getChildNodes();

		// Run through all the nodes children. If a non-whitespace node
		// pis found, bail.
		for (int i = 0; i < nl.getLength(); i++) {
			Node childNode = nl.item(i);
			if (!DOMUtilities.isWhitespace(childNode))
				return;
		}

		// We only get here if there are no non-whitespace chars, so
		// simply remove the node.
		removeDOMChild(node.getParentNode(), node);
	}

	/**
	 * Remove the DOM path specified by <map>from <node>
	 */
	protected void removeDOMPath(Node node, Translator map) {
		Node childNode = findDOMPath(node, map, false);
		while (childNode != null && childNode != node) {
			removeIfEmpty(childNode);
			childNode = childNode.getParentNode();
		}
	}

	/**
	 * Create an adapter for a child DOM node
	 * 
	 * @param node
	 *            org.w3c.dom.Node The node to create the adapter for.
	 */
	protected EMF2DOMAdapter createAdapter(EObject mofObject, Translator childMap) {
		//	Assert.isNotNull(childMap.getChildAdapterClass());
		Assert.isNotNull(mofObject);

		EMF2DOMAdapter adapter = (EMF2DOMAdapter) EcoreUtil.getAdapter(mofObject.eAdapters(), EMF2DOMAdapter.ADAPTER_CLASS);

		if (adapter != null && adapter.isMOFProxy()) {
			removeAdapters(adapter.getNode());
			mofObject.eAdapters().remove(adapter);
			adapter = null;
		}
		if (adapter == null)
			adapter = primCreateAdapter(mofObject, childMap);
		return adapter;
	}

	/**
	 * Create an adapter for a child DOM node
	 * 
	 * @param node
	 *            org.w3c.dom.Node The node to create the adapter for.
	 */
	protected EMF2DOMAdapter primCreateAdapter(EObject mofObject, Translator childMap) {

		Element newNode = createNewNode(mofObject, childMap);
		return new EMF2DOMAdapterImpl(mofObject, newNode, fRenderer, childMap);
	}

	/**
	 * Create an adapter for a child DOM node
	 * 
	 * @param node
	 *            org.w3c.dom.Node The node to create the adapter for.
	 */
	protected EMF2DOMAdapter primCreateAdapter(Node node, Translator childMap) {
		return new EMF2DOMAdapterImpl(node, fRenderer, childMap);
	}

	/**
	 * Create an adapter for a child DOM node
	 * 
	 * @param node
	 *            org.w3c.dom.Node The node to create the adapter for.
	 */
	protected EMF2DOMAdapter createAdapter(Node node, Translator childMap) {

		//Assert.isNotNull(childMap.getChildAdapterClass());
		Assert.isNotNull(node);

		EMF2DOMAdapter adapter = primGetExistingAdapter(node);

		if (adapter != null) {
			if (adapter.isMOFProxy() || adapter.getTarget() == null) {
				removeDOMAdapter(node, adapter);
				if (adapter.getTarget() != null) {
					adapter.getTarget().eAdapters().remove(adapter);
				}
				adapter = null;
			}
		} else {
			adapter = primCreateAdapter(node, childMap);
		}
		return adapter;
	}

	protected Element createNewNode(EObject mofObject, Translator childMap) {
		Node node = getNode();
		Document doc = (node instanceof Document) ? (Document) node : node.getOwnerDocument();

		Element element = doc.createElement(childMap.getDOMName(mofObject));
		if (childMap.isEmptyTag())
			setEmptyTag(element);

		return element;
	}

	protected void setEmptyTag(Element element) {
		Revisit.toDo();
		//Need to figure out how to do this with pure DOM apis, if it is
		// possible
	}

	/*
	 * Return true if MOF object is a proxy.
	 */
	public boolean isMOFProxy() {
		if (isRoot || target == null)
			return false;
		return ((InternalEObject) target).eIsProxy();
	}

	public EObject getEObject() {
		if (isRoot)
			return null;
		return (EObject) target;
	}

	/**
	 * Return the DOM node that the target of this adapter maps to. If the target MOF object maps to
	 * more than one DOM node, this node is the top-most node.
	 */
	public Node getNode() {
		return fNode;
	}

	public void setNode(Node aNode) {
		fNode = aNode;
	}

	public void updateDOM() {
		if (!isNotificationEnabled())
			return;
		primUpdateDOM();
	}

	public void updateMOF() {
		if (!isNotificationEnabled())
			return;
		primUpdateMOF();
	}

	protected void primUpdateDOM() {
		if (isRoot)
			updateDOMRootFeature();
		else {
			Translator[] maps = getChildTranslators();
			for (int i = 0; i < maps.length; i++) {
				updateDOMFeature(maps[i], getNode(), getEObject());
			}
		}
	}

	public void primUpdateMOF() {
		if (isRoot)
			updateMOFRootFeature();
		else {
			Translator[] maps = getChildTranslators();
			for (int i = 0; i < maps.length; i++) {
				updateMOFFeature(maps[i], getNode(), getEObject());
			}
		}
	}

	protected void updateDOMRootFeature() {
		boolean notificationFlag = isNotificationEnabled();
		try {
			setNotificationEnabled(false);
			primUpdateDOMMultiFeature(fTranslator, fNode, getResourceContents(), getDOMChildren(fNode, fTranslator), null);
			updateDOMDocumentType();
		} finally {
			setNotificationEnabled(notificationFlag);
		}
	}

	protected void updateMOFRootFeature() {
		boolean notificationFlag = isNotificationEnabled();
		try {
			setNotificationEnabled(false);
			updateMOFDocumentType();
			primUpdateMOFMultiFeature(fTranslator, fNode, getResourceContents(), getDOMChildren(fNode, fTranslator));
		} finally {
			setNotificationEnabled(notificationFlag);
		}
	}

	protected DocumentType getDocumentType() {
		return ((Document) fNode).getDoctype();
	}

	protected TranslatorResource getResource() {
		return (TranslatorResource) getTarget();
	}

	protected EList getResourceContents() {
		if (!isRoot)
			throw new IllegalStateException();
		return ((Resource) getTarget()).getContents();
	}

	protected void updateDOMDocumentType() {

		DocumentType docType = getDocumentType();
		String publicId = null, systemId = null, oldPublicId, oldSystemId;
		oldPublicId = docType == null ? null : docType.getPublicId();
		oldSystemId = docType == null ? null : docType.getSystemId();
		TranslatorResource resource = getResource();
		if (resource != null) {
			publicId = resource.getPublicId();
			systemId = resource.getSystemId();
		}
		if (!(StringUtil.stringsEqual(publicId, oldPublicId) && StringUtil.stringsEqual(systemId, oldSystemId)))
			fRenderer.replaceDocumentType(resource.getDoctype(), publicId, systemId);
	}

	protected void updateDOMDocumentType(Notification msg) {
		if (msg.getFeature() == TranslatorResource.DOC_TYPE_FEATURE)
			updateDOMDocumentType();
	}

	protected void updateMOFDocumentType() {

		TranslatorResource resource = getResource();
		if (resource == null)
			return;

		String publicId = null, systemId = null;
		DocumentType docType = getDocumentType();

		if (docType != null) {
			publicId = docType.getPublicId();
			systemId = docType.getSystemId();
		}
		if ((!(StringUtil.stringsEqual(publicId, resource.getPublicId()) && StringUtil.stringsEqual(systemId, resource.getSystemId()))) || 
				((resource.getPublicId() == null) || resource.getSystemId() == null))
			resource.setDoctypeValues(publicId, systemId);
	}

	public void notifyChanged(Notification msg) {

		if (isRoot) {
			notifyChangedForRoot(msg);
			return;
		}
		if (isDependencyFeature(msg))
			handleDependencyFeature(msg);

		if (!isNotificationEnabled())
			return;

		debugMOFNotify(msg);

		switch (msg.getEventType()) {
			case Notification.ADD :
			case Notification.REMOVE :
			case Notification.ADD_MANY :
			case Notification.REMOVE_MANY :
			case Notification.SET :
			case Notification.UNSET :
			case Notification.MOVE :
				Translator translator = findTranslator(msg);
				if (translator == null)
					translator = addVariableTranslatorIfNecessary(msg);
				if (translator != null)
					updateDOMFeature(translator, getNode(), getEObject());
				break;
		}
	}

	protected void notifyChangedForRoot(Notification msg) {
		if (!isNotificationEnabled())
			return;

		debugMOFNotify(msg);

		switch (msg.getEventType()) {
			case Notification.ADD :
			case Notification.REMOVE :
			case Notification.ADD_MANY :
			case Notification.REMOVE_MANY :
				primUpdateDOM();
				break;
			case Notification.SET :
				updateDOMDocumentType(msg);
				break;
			case EtoolsCopySession.RESOURCE_COPIED :
				updateDOM();
				break;
		}
	}

	protected void addDependencyAdapter(EObject child) {
		Adapter existing = EcoreUtil.getExistingAdapter(child, DependencyAdapter.KEY);
		if (existing != null)
			return;
		DependencyAdapter forwarder = new DependencyAdapter();
		child.eAdapters().add(forwarder);
		forwarder.setTarget(child);
	}

	protected void addDOMAdapter() {

		primAddDOMAdapter(fNode, this);
		if (fDebug) {
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("Adding DOM adapter: " + this); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tto: " + fNode); //$NON-NLS-1$
		}

		// Go through the maps. All of the DOM nodes that are not listened
		// to by another DOM Node adapter, must be listened to by this adapter.
		NodeList childNodes = fNode.getChildNodes();
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = childNodes.item(j);
			int nodeType = childNode.getNodeType();
			if (!DOMUtilities.isTextNode(childNode) && nodeType != Node.COMMENT_NODE) {
				Translator map = findTranslator(childNode.getNodeName(), false);
				if (map != null && map.isManagedByParent())
					addDOMAdapter(childNode);
			}
		}
	}

	protected Translator addVariableTranslatorIfNecessary(Notification msg) {
		VariableTranslatorFactory fact = fTranslator.getVariableTranslatorFactory();
		Translator trans = null;
		if (fact != null && fact.accepts(msg)) {
			trans = fact.create(msg);
			if (trans != null)
				childTranslators = (Translator[]) Translator.concat(childTranslators, trans);
		}
		return trans;
	}

	protected Text createTextNode(Document doc, Translator map, String text) {
		String nonnulltext = (text != null) ? text : ""; //$NON-NLS-1$
		return map.isCDATAContent() ? doc.createCDATASection(nonnulltext) : doc.createTextNode(nonnulltext);
	}

	protected String extractReadAheadName() {
		if (!fTranslator.hasReadAheadNames())
			return null;
		String readAheadName = null;

		ReadAheadHelper helper = fTranslator.getReadAheadHelper(fNode.getNodeName());
		if (helper == null)
			return null;

		Node child = null;
		String[] names = helper.getValues();
		if (helper.getChildDOMName() == null) {
			for (int i = 0; i < names.length; i++) {
				child = DOMUtilities.getNodeChild(fNode, names[i]);
				if (child != null) {
					readAheadName = names[i];
					break;
				}
			}
		} else {
			child = DOMUtilities.getNodeChild(fNode, helper.getChildDOMName());
			if (child != null)
				readAheadName = DOMUtilities.getChildText(child);
		}
		if (readAheadName == null)
			readAheadName = names[0];
		return readAheadName;
	}

	/**
	 * Extracts the text from <node>and converts it to an object suitable for setting into <feature>
	 */
	protected Object extractValue(Node node, Translator map, EObject emfObject) {

		// Extract the value from the text child
		Node textNode = DOMUtilities.getChildTextNode(node);
		String trimmedValue = null;
		if (textNode != null)
			trimmedValue = textNode.getNodeValue();
		try {
			return map.convertStringToValue(trimmedValue, emfObject);
		} catch (FeatureValueConversionException ex) {
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError(ex);
			handleFeatureValueConversionException(ex);
			return null;
		}
	}

	/**
	 * @param ex
	 */
	protected void handleFeatureValueConversionException(FeatureValueConversionException ex) {
		throw ex;

	}

	/*
	 * Finds the child node that <map> specifies. If there is more than one child that satisfies
	 * <map> then the first one is returned.
	 * 
	 * This method traverses past the domPath if one is specified in the <map>.
	 */
	protected Node findDOMNode(Node parent, Translator map) {
		return findDOMNode(parent, map, false);
	}

	/*
	 * Finds the child node that <map> specifies. If there is more than one child that satisfies
	 * <map> then the first one is returned. The <addAdapters> boolean is used to determine if the
	 * receiver is added as an adapter to every node found on the path.
	 * 
	 * This method traverses past the domPath if one is specified in the <map>.
	 */
	protected Node findDOMNode(Node parent, Translator map, boolean addAdapters) {

		// First, trace down the path
		Node curNode = findDOMPath(parent, map, addAdapters);
		if (map.isDOMTextValue() || map.isDOMAttribute() || curNode == null)
			return curNode;

		// Now look for the first DOM name we can find
		String[] domNames = map.getDOMNames();
		Node node = null;
		for (int i = 0; i < domNames.length; i++) {
			String nodeName = domNames[i];
			List nodes = DOMUtilities.getNodeChildren(curNode, nodeName);
			if (nodes != null && !nodes.isEmpty()) {
				if (nodes.size() > 1)
					handleInvalidMultiNodes(nodeName);
				node = (Node) nodes.get(0);
				if (node != null) {
					if (addAdapters && (map != null || map.isManagedByParent()))
						addDOMAdapter(curNode);
					break;
				}
			}
		}
		return node;
	}

	protected void handleInvalidMultiNodes(String nodeName) {
		throw new IllegalStateException(WFTUtilsResourceHandler.getString(WFTUtilsResourceHandler.EMF2DOMAdapterImpl_ERROR_0, new Object[]{nodeName}));
	}

	/**
	 * Creates the path specified by <map>under <node>. Only the portion of the path that does not
	 * exist (if any) is created
	 * 
	 * @param node
	 *            org.w3c.dom.Node
	 * @param map
	 *            com.ibm.etools.mof2dom.Translator
	 */
	protected Text findOrCreateTextNode(Node parent, Translator map, String text) {
		Text textNode = DOMUtilities.getChildTextNode(parent);
		if (textNode != null) {
			textNode.setData(text);
		} else {
			if (!isEmptyTag((Element) parent)) {
				Text newNode = createTextNode(parent.getOwnerDocument(), map, text);
				DOMUtilities.insertBeforeNode(parent, newNode, null);
				return newNode;
			}
		}
		return textNode;
	}

	protected Translator findTranslator(String tagName, boolean attributeMap) {
		Translator[] maps = getChildTranslators();
		for (int i = 0; i < maps.length; i++) {
			Translator map = maps[i];
			if (map.isMapFor(tagName) && attributeMap == map.isDOMAttribute())
				return maps[i];
		}

		return null;
	}

	protected EMF2DOMAdapter getExistingAdapter(Node aNode) {
		EMF2DOMAdapter adapter = primGetExistingAdapter(aNode);
		if (adapter != null && adapter.isMOFProxy()) {
			removeDOMAdapter(aNode, adapter);
			adapter.getTarget().eAdapters().remove(adapter);
			adapter = null;
		}
		return adapter;
	}

	protected void handleDependencyFeature(Notification msg) {
		if (msg.getOldValue() != null)
			removeDependencyAdapter((EObject) msg.getOldValue());
		if (msg.getNewValue() != null)
			addDependencyAdapter((EObject) msg.getNewValue());
	}

	protected boolean isDependencyFeature(Notification msg) {
		switch (msg.getEventType()) {
			case Notification.SET :
				return fTranslator.isDependencyParent() && fTranslator.getDependencyFeature() == msg.getFeature();
			default :
				return false;
		}
	}

	protected boolean isEmptyTag(Element parent) {
		Revisit.toDo();
		//Determine how to implement this with pure DOM apis, if possible.
		return false;
	}

	protected void postUpdateDOMFeature(Translator map, Node node, EObject mofObject) {
	}

	protected void preUpdateDOMFeature(Translator map, Node node, EObject mofObject) {
	}

	/**
	 * Update an attribute of the target DOM object from with the values currently stored in the MOF
	 * object. The
	 * 
	 * @map specifies the name of the MOF attribute to update and the name of the DOM node.
	 * 
	 * @param map
	 *            com.ibm.etools.mof2dom.AttributeTranslator
	 */
	protected void primUpdateDOMFeature(Translator map, Node node, EObject mofObject) {
		Object attrValue = null;
		boolean isSet = false;
		if (map.isIDMap()) {
			try {
				attrValue = map.getMOFValue(mofObject);
			} catch (IDTranslator.NoResourceException ex) {
				//If the object has been removed from the resource,
				//No need to update
				return;
			}
			isSet = attrValue != null;
		} else {
			attrValue = map.getMOFValue(mofObject);
			isSet = map.isSetMOFValue(mofObject);
		}

		if (map.isDOMAttribute()) {
			// An attribute of the MOF object maps to an attribute of the
			// DOM node. Get the value of the MOF attribute and set it
			// into DOM node.
			Element e = (Element) createDOMPath(node, map);
			if (attrValue != null && isSet)
				e.setAttribute(map.getDOMName(mofObject), map.convertValueToString(attrValue, mofObject));
			else
				e.removeAttribute(map.getDOMName(mofObject));
		} else {
			updateDOMSubtree(map, node, mofObject, attrValue);
		}
	}

	/**
	 * Update an attribute of the target DOM object from with the values currently stored in the MOF
	 * object. The
	 * 
	 * @map specifies the name of the MOF attribute to update and the name of the DOM node.
	 */
	protected void primUpdateDOMLinkFeature(Translator map, Node node, EObject mofObject) {
		LinkUpdaterTarget.INSTANCE.updateDOM(map, node, mofObject);

	}

	/**
	 * Update all the children of the target MOF object in the relationship described by
	 * 
	 * @map.
	 */
	protected void primUpdateDOMMultiFeature(Translator map, Node node, EObject mofObject) {

		List mofChildren = map.getMOFChildren(mofObject);
		List domChildren = getDOMChildren(node, map);

		primUpdateDOMMultiFeature(map, node, mofChildren, domChildren, mofObject);
	}

	/**
	 * Update an attribute of the target MOF object from the DOM node subtree. The
	 * 
	 * @map specifies the name of the MOF attribute to update and the name of the DOM node.
	 */
	protected boolean primUpdateMOFFeature(Translator map, Node node, EObject mofObject) {
		if (!map.featureExists(mofObject))
			return false;
		Object domValue = null;
		boolean updateMOFAttAdapter = false;
		boolean isUnset = false;
		EMF2DOMAdapter attrAdapter = null;

		Node child = findDOMNode(node, map, true);

		if (map.isDOMAttribute() && child != null) {
			// An attribute of the MOF object maps to an attribute of the
			// DOM node. Get the value of the DOM attribute and set it
			// into the MOF object.

			Attr domAttr = (Attr) child.getAttributes().getNamedItem(map.getDOMName(mofObject));
			if (domAttr != null) {
				domValue = domAttr.getValue();
				domValue = map.convertStringToValue((String) domValue, mofObject);
			} else
				isUnset = true;
		} else {
			// An attribute of the MOF object is actually a sub-element
			// of the DOM node. Search for the first sub-element with
			// the correct name to use as an attribute.
			if (child != null) {
				// Check to see if this is a single valued attribute that has
				// a MOF object as its value
				if (!map.isManagedByParent()) {
					attrAdapter = createAdapter(child, map);
					updateMOFAttAdapter = true;
					domValue = attrAdapter.getTarget();
				} else {
					// Check to make sure the child is adapted. If not, adapt
					// it.
					addDOMAdapter(child);

					// Extract the value from the text child
					domValue = extractValue(child, map, mofObject);
				}
			} else
				isUnset = true;
		}

		// Set the attribute extracted from the DOM to the MOF object.
		boolean hasChanged = true;
		try {
			if (map.isIDMap())
				map.setMOFValue(mofObject, domValue);
			else {
				Object oldValue = null;
				oldValue = map.getMOFValue(mofObject);
				boolean isSet = map.isSetMOFValue(mofObject);
				//In the case of enums with default values, we need to trip
				// the attribute from
				//default to a set value
				if (oldValue == domValue) {
					if (oldValue == null || isSet)
						hasChanged = false;
				} else if (domValue == null && !isSet)
					//If the domValue is null and the feature is not set, then
					// we don't need
					//to do anything
					hasChanged = false;
				else if (oldValue != null && oldValue.equals(domValue) && isSet)
					hasChanged = false;
				if (oldValue == null && domValue == null && map.isSetMOFValue(mofObject) == isUnset)
					hasChanged = true;
				if (hasChanged) {
					if (!(map.isDataType()) && !map.isShared())
						removeMOFValue((EObject) oldValue, map);
					if (domValue == null)
						map.unSetMOFValue(mofObject);
					else
						map.setMOFValue(mofObject, domValue);

					if ((domValue == null && !(map.isEnumFeature())) || isUnset)
						map.unSetMOFValue(mofObject); //unset
					// null
					// for
					// non
					// enum
					// features

					if (updateMOFAttAdapter)
						attrAdapter.updateMOF();
				}
			}
		} catch (FeatureValueConversionException ex) {
			handleFeatureValueConversionException(ex);
			map.unSetMOFValue(mofObject);
		}
		return hasChanged;
	}

	/**
	 * Update an attribute of the target MOF object from the DOM node subtree. This method is only
	 * called if the DOM node changes and the map is an object link map. The
	 * 
	 * @map specifies the name of the MOF attribute to update and the name of the DOM node.
	 * 
	 * @param map
	 *            com.ibm.etools.mof2dom.AttributeTranslator
	 * @return Return true if the MOF feature was updated, false if no update was done.
	 */
	protected void primUpdateMOFLinkFeature(Translator map, Node node, EObject mofObject) {
		LinkUpdaterTarget.INSTANCE.updateMOF(map, node, mofObject);
	}

	/**
	 * Update all the children of the target MOF object in the relationship described by
	 * 
	 * @map.
	 * 
	 * @param map
	 *            com.ibm.etools.mof2dom.AttributeTranslator Describes the mapping from the MOF
	 *            attribute name to the DOM node name
	 */
	protected void primUpdateMOFMultiFeature(Translator map, Node node, EObject mofObject) {
		// If the feature is a collection of strings or ints, call a special
		// method
		// that handles this.
		if (map.isManagedByParent()) {
			updateMOFMultiPrimitiveFeature(map, node, mofObject);
			return;
		}

		List nodeChildren = getDOMChildren(node, map);
		List mofChildren = map.getMOFChildren(mofObject);

		primUpdateMOFMultiFeature(map, node, mofChildren, nodeChildren);
	}

	protected void removeDependencyAdapter(EObject obj) {
		Adapter existing = EcoreUtil.getExistingAdapter(obj, DependencyAdapter.KEY);
		if (existing != null)
			obj.eAdapters().remove(existing);
	}

	/**
	 * Removes all the DOM children from <parent>that are represented by <map>.
	 */
	protected void removeDOMChildren(Node parent, Translator map) {
		String[] domNames = map.getDOMNames();
		HashSet domNamesSet = new HashSet(domNames.length);
		for (int i = 0; i < domNames.length; i++)
			domNamesSet.add(domNames[i]);

		// Walk through all the children and find any that match the map.
		NodeList nl = parent.getChildNodes();
		List toRemove = new ArrayList();
		for (int i = 0; i < nl.getLength(); i++) {
			Node childNode = nl.item(i);
			if (domNamesSet.contains(childNode.getNodeName()))
				toRemove.add(childNode);
		}

		// Remove any children that were found.
		for (int i = 0; i < toRemove.size(); i++) {
			Node childNode = (Node) toRemove.get(i);
			removeDOMChild(parent, childNode, true);
		}
	}

	protected void setTargetFromNode() {
		setTarget(fTranslator.createEMFObject(fNode.getNodeName(), extractReadAheadName()));
	}

	/**
	 * Update an attribute of the target DOM object from with the values currently stored in the MOF
	 * object. The
	 * 
	 * @map specifies the name of the MOF attribute to update and the name of the DOM node.
	 * 
	 * @param map
	 *            com.ibm.etools.mof2dom.AttributeTranslator
	 */
	final public void updateDOMFeature(Translator map, Node node, EObject mofObject) {
		if (!isNotificationEnabled())
			return;
		try {
			preUpdateDOMFeature(map, node, mofObject);
			if (map.isMultiValued()) {
				updateDOMMultiFeature(map, node, mofObject);
				return;
			}

			if (fDebug) {
				org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("Updating DOM Node: " + node); //$NON-NLS-1$
				org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tfrom: " + mofObject); //$NON-NLS-1$
				org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tmap : " + map); //$NON-NLS-1$
			}
			boolean notificationFlag = isNotificationEnabled();
			try {
				setNotificationEnabled(false);
				primUpdateDOMFeature(map, node, mofObject);
			} finally {
				setNotificationEnabled(notificationFlag);
			}

			if (map.isTargetLinkMap()) {
				updateDOMLinkFeature(map, node, mofObject);
			}
		} finally {
			postUpdateDOMFeature(map, node, mofObject);
		}
	}

	/**
	 * Update an attribute of the target DOM object from the values currently stored in the MOF
	 * object. The
	 * 
	 * @map specifies the name of the MOF attribute to update and the name of the DOM node.
	 * 
	 * @param map
	 *            com.ibm.etools.mof2dom.AttributeTranslator
	 */
	final protected void updateDOMLinkFeature(Translator map, Node node, EObject mofObject) {
		if (fDebug) {
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("Updating DOM Node (link): " + node); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tfrom: " + mofObject); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tmap : " + map); //$NON-NLS-1$
		}
		primUpdateDOMLinkFeature(map, node, mofObject);
	}

	/**
	 * Update all the children of the target MOF object in the relationship described by
	 * 
	 * @map.
	 * 
	 * @param map
	 *            com.ibm.etools.mof2dom.AttributeTranslator Describes the mapping from the MOF
	 *            attribute name to the DOM node name
	 */
	final protected void updateDOMMultiFeature(Translator map, Node node, EObject mofObject) {
		if (fDebug) {
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("Updating DOM Node (multi): " + node); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tfrom: " + mofObject); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tmap : " + map); //$NON-NLS-1$
		}
		boolean notificationFlag = isNotificationEnabled();
		try {
			setNotificationEnabled(false);
			primUpdateDOMMultiFeature(map, node, mofObject);
		} finally {
			setNotificationEnabled(notificationFlag);
		}
	}

	/**
	 * Update a DOM subtree to reflect the mofObject and map passed in. The subtree is of <node>is
	 * updated.
	 */
	protected void updateDOMSubtree(Translator map, Node node, EObject mofObject, Object attrValue) {

		if (map.featureExists(mofObject)) {
			if ((map.isEnumFeature() || map.isBooleanFeature()) && (map.isUnsettable() && !map.isSetMOFValue(mofObject)))
				attrValue = null;
		} else
			attrValue = map.extractStringValue(mofObject);

		// Create and/or update the DOM subtree
		if (attrValue != null) {
			Node parent = createDOMPath(node, map);
			if (map.isManagedByParent()) {
				// Handle the case where the mof value is not another
				// mof object (primitive)
				if (map.getDOMName(mofObject) != null && map.getDOMName(mofObject).startsWith("#")) //$NON-NLS-1$
					return;

				Element child = map.isDOMTextValue() ? (Element) parent : findOrCreateNode(parent, map, map.getDOMName(mofObject));

				findOrCreateTextNode(child, map, map.convertValueToString(attrValue, mofObject));
			} else {
				// Handle the case were the mof value is a mof object.
				EObject mofValue = (EObject) attrValue;
				EMF2DOMAdapter valueAdapter = (EMF2DOMAdapter) EcoreUtil.getExistingAdapter(mofValue, EMF2DOMAdapter.ADAPTER_CLASS);
				if (valueAdapter != null)
					valueAdapter.updateDOM();
				else {
					removeDOMChildren(parent, map);
					EMF2DOMAdapter adapter = createAdapter(mofValue, map);
					List mofChildren = map.getMOFChildren(mofObject);
					List domChildren = getDOMChildren(parent, map);

					Node insertBeforeNode = findInsertBeforeNode(parent, map, mofChildren, 0, domChildren);
					DOMUtilities.insertBeforeNodeAndWhitespace(parent, adapter.getNode(), insertBeforeNode);
					boolean notificationFlag = adapter.isNotificationEnabled();
					adapter.setNotificationEnabled(false);
					try {
						indent(adapter.getNode(), map);
					} finally {
						adapter.setNotificationEnabled(notificationFlag);
					}
					adapter.updateDOM();
				}
			}
		} else {
			// The attribute value was set to null or unset. Remove any
			// existing DOM nodes.
			Node child = findDOMNode(node, map);
			if (child != null)
				removeDOMChild(child.getParentNode(), child);
		}
	}

	/**
	 * Update a feature that is set by linking to another existing object. This method is called
	 * when the MOF object is updated in order to update DOM nodes.
	 */
	final protected void updateMOFLinkFeature(Translator map, Node node, EObject mofObject) {
		if (fDebug) {
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("Updating MOFObject (link): " + mofObject); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tfrom: " + node); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tmap : " + map); //$NON-NLS-1$
		}
		boolean notificationFlag = isNotificationEnabled();
		try {
			setNotificationEnabled(false);
			primUpdateMOFLinkFeature(map, node, mofObject);
		} finally {
			setNotificationEnabled(notificationFlag);
		}
	}

	/**
	 * Update all the children of the target MOF object in the relationship described by
	 * 
	 * @map.
	 * 
	 * @param map
	 *            com.ibm.etools.mof2dom.AttributeTranslator Describes the mapping from the MOF
	 *            attribute name to the DOM node name
	 */
	final protected void updateMOFMultiFeature(Translator map, Node node, EObject mofObject) {
		if (fDebug) {
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("Updating MOFObject (multi): " + mofObject); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tfrom: " + node); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tmap : " + map); //$NON-NLS-1$
		}
		boolean notificationFlag = isNotificationEnabled();
		try {
			setNotificationEnabled(false);
			primUpdateMOFMultiFeature(map, node, mofObject);
		} finally {
			setNotificationEnabled(notificationFlag);
		}
	}

	/**
	 * Update all the children of the target MOF object in the relationship described by
	 * 
	 * @map. The relationship MUST BE a collection of string for this method to work.
	 */
	protected void updateMOFMultiPrimitiveFeature(Translator map, Node node, EObject mofObject) {
		List nodeChildren = getDOMChildren(node, map);

		map.clearList(mofObject);

		// Go through the list of nodes and update the MOF collection
		int addIndex = 0;
		for (int i = 0; i < nodeChildren.size(); i++) {
			Node child = (Node) nodeChildren.get(i);
			Object attributeValue = extractValue(child, map, mofObject);
			boolean advanceAddIndex = true;
			if (attributeValue != null){
				if(map.getFeature() != null && map.getFeature().isUnique() && mofObject.eGet(map.getFeature()) != null && mofObject.eGet(map.getFeature()) instanceof List && ((List) mofObject.eGet(map.getFeature())).contains(attributeValue)){
					advanceAddIndex = false;
					String domName = map.domNameAndPath != null ? map.domNameAndPath : "attribute"; //$NON-NLS-1$
					org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError(new IllegalArgumentException("The 'no duplicates' constraint is violated by "+domName+" = "+attributeValue));
					handleInvalidMultiNodes(child.getNodeName());
				} else {
					map.setMOFValue(mofObject, attributeValue, addIndex);
				}
				if(advanceAddIndex){
					addIndex ++;
				}
			}
			
			// Adapt the node so update will occur.
			addDOMAdapter(child);
		}
		if (map.hasDOMPath() && nodeChildren.isEmpty() && findDOMPath(node, map, false) != null)
			map.setMOFValueFromEmptyDOMPath(mofObject);
	}

	/**
	 * Update an attribute of the target MOF object from the DOM node subtree. The
	 * 
	 * @map specifies the name of the MOF attribute to update and the name of the DOM node.
	 * 
	 * @param map
	 *            com.ibm.etools.mof2dom.AttributeTranslator
	 */
	public void updateMOFFeature(Translator map, Node node, EObject mofObject) {
		if (!isNotificationEnabled())
			return;
		if (map.isMultiValued()) {
			updateMOFMultiFeature(map, node, mofObject);
			return;
		} else if (map.isComment()) {
			updateMOFCommentFeature(map, node, mofObject);
			return;
		}

		// TODO MDE Add a map.isComment() and updateMOFCommentFeature(map, node, mofObject);

		if (fDebug) {
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("Updating MOFObject: " + mofObject); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tfrom: " + node); //$NON-NLS-1$
			org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tmap : " + map); //$NON-NLS-1$
		}
		boolean notificationFlag = isNotificationEnabled();
		boolean hasChanged = false;
		try {
			setNotificationEnabled(false);
			hasChanged = primUpdateMOFFeature(map, node, mofObject);
		} finally {
			setNotificationEnabled(notificationFlag);
		}

		if (map.isTargetLinkMap() && hasChanged)
			updateMOFLinkFeature(map, node, mofObject);
	}

	/**
	 * @param map
	 * @param node
	 * @param mofObject
	 */
	public void updateMOFCommentFeature(Translator map, Node node, EObject mofObject) {
		Node commentNode = node;
		/* scan up the dom to find the first comment node before this node */
		while ((commentNode = commentNode.getPreviousSibling()) != null && commentNode.getNodeType() != Node.COMMENT_NODE) {
			/* no comment available */
			if (commentNode.getNodeType() == Node.ELEMENT_NODE)
				return;
		}
		if (commentNode != null)
			map.setMOFValue(mofObject, commentNode.getNodeValue());

	}

}
