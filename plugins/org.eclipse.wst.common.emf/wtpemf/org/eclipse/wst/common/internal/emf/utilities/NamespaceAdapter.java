/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Aug 20, 2003
 *
 */
package org.eclipse.wst.common.internal.emf.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;


/**
 * Internal class used to hold namespaces parsed from an XML file using the xmlns:prefix="aURI"
 * syntax
 */
public class NamespaceAdapter extends AdapterImpl implements CloneablePublic {

	protected final static String ADAPTER_TYPE = NamespaceAdapter.class.getName();
	protected Map prefixesToNS;
	protected List namespaces;

	public final static EStructuralFeature NOTIFICATION_FEATURE = new EStructuralFeatureImpl() {
	};

	private static class Notification extends NotificationImpl {

		/**
		 * @param eventType
		 * @param oldValue
		 * @param newValue
		 */
		public Notification(int eventType, Object oldValue, Object newValue) {
			super(eventType, oldValue, newValue);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.emf.common.notify.impl.NotificationImpl#getFeature()
		 */
		@Override
		public Object getFeature() {
			return NOTIFICATION_FEATURE;
		}
	}

	protected NamespaceAdapter() {
		super();
	}

	public static void addNamespace(String prefix, String uri, EObject obj) {
		if (obj == null)
			return;
		NamespaceAdapter adapter = getAdapter(obj);
		if (adapter != null)
			adapter.addNamespace(prefix, uri);
	}

	public static void removeNamespace(String prefix, EObject obj) {
		if (obj == null)
			return;
		NamespaceAdapter adapter = retrieveExistingAdapter(obj);
		if (adapter != null)
			adapter.removeNamespace(prefix);
	}

	/**
	 * Facade method for resolving prefixes to an actual namespace URI. Used for objects contained
	 * by instances of {@link com.ibm.etools.emf2xml.TranslatorResource}. Walks up the object
	 * containment path until a namespace is found, or returns null.
	 */
	public static String getResolvedNamespaceURI(String prefix, EObject obj) {
		if (prefix == null || obj == null)
			return null;
		EObject container = obj;
		String nsURI;
		while (container != null) {
			nsURI = getNamespaceURIAtThisLevel(prefix, container);
			if (nsURI != null)
				return nsURI;
			container = container.eContainer();
		}
		return null;
	}

	public static String getNamespaceURIAtThisLevel(String prefix, EObject obj) {
		if (obj == null)
			return null;
		NamespaceAdapter adapter = retrieveExistingAdapter(obj);
		return adapter == null ? null : adapter.getNamespaceURI(prefix);
	}

	public static List getNamespaces(EObject obj) {
		if (obj == null)
			return Collections.EMPTY_LIST;
		NamespaceAdapter adapter = retrieveExistingAdapter(obj);
		return adapter == null ? null : adapter.getNamespaces();

	}

	protected static NamespaceAdapter retrieveExistingAdapter(EObject obj) {
		return (NamespaceAdapter) EcoreUtil.getExistingAdapter(obj, ADAPTER_TYPE);
	}

	protected static NamespaceAdapter getAdapter(EObject obj) {
		NamespaceAdapter adapter = retrieveExistingAdapter(obj);
		return adapter == null ? createAdapter(obj) : adapter;
	}

	protected static NamespaceAdapter createAdapter(EObject obj) {
		NamespaceAdapter adapter = new NamespaceAdapter();
		adapter.setTarget(obj);
		obj.eAdapters().add(adapter);
		return adapter;
	}

	protected void addNamespace(String prefix, String uri) {
		Namespace ns = new Namespace(prefix, uri);
		if (namespaces == null)
			namespaces = new ArrayList();
		if (prefixesToNS == null)
			prefixesToNS = new HashMap();

		prefixesToNS.put(prefix, ns);
		namespaces.add(ns);
		fireNotification(org.eclipse.emf.common.notify.Notification.ADD, null, ns);

	}

	protected void removeNamespace(String prefix) {
		Namespace ns = null;
		if (prefixesToNS != null)
			ns = (Namespace) prefixesToNS.get(prefix);

		if (ns != null)
			namespaces.remove(ns);
		fireNotification(org.eclipse.emf.common.notify.Notification.REMOVE, ns, null);
	}

	protected String getNamespaceURI(String prefix) {
		if (prefixesToNS == null)
			return null;
		Namespace ns = (Namespace) prefixesToNS.get(prefix);
		if (ns != null)
			return ns.getNsURI();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
	 */
	@Override
	public boolean isAdapterForType(Object type) {
		return ADAPTER_TYPE.equals(type);
	}

	public List getNamespaces() {
		return namespaces == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(namespaces);
	}

	protected void fireNotification(int type, Object oldValue, Object newValue) {
		if (target != null)
			target.eNotify(new Notification(type, oldValue, newValue));
	}

	public boolean hasNamespaces() {
		return namespaces != null && !namespaces.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf.ecore.utilities.CloneablePublic#clone()
	 */
	@Override
	public Object clone() {
		NamespaceAdapter result = new NamespaceAdapter();
		if (hasNamespaces()) {
			for (int i = 0; i < namespaces.size(); i++) {
				Namespace ns = (Namespace) namespaces.get(i);
				result.addNamespace(ns.getPrefix(), ns.getNsURI());
			}
		}
		return result;
	}

}
