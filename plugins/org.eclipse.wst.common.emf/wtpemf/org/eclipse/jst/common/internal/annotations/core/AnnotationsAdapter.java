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
package org.eclipse.jst.common.internal.annotations.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.common.internal.emf.utilities.CloneablePublic;



/**
 * @author mdelder
 *  
 */
public class AnnotationsAdapter extends AdapterImpl implements CloneablePublic {

	public static final String GENERATED = "generated"; //$NON-NLS-1$

	protected final static String ADAPTER_TYPE = AnnotationsAdapter.class.getName();

	public final static EStructuralFeature NOTIFICATION_FEATURE = new EStructuralFeatureImpl() {
		// anonymous inner class
	};

	private Map annotationsMap;

	/**
	 *  
	 */
	public AnnotationsAdapter() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone() { // throws CloneNotSupportedException {
		//return super.clone();
		return null;
	}

	/**
	 * @param emfObject
	 * @param string
	 */
	public static void addAnnotations(EObject emfObject, String name, Object value) {
		if (emfObject == null)
			return;
		AnnotationsAdapter adapter = getAdapter(emfObject);
		adapter.addAnnotations(name, value);
	}


	/**
	 * @param emfObject
	 * @param string
	 */
	public static Object getAnnotations(EObject emfObject, String name) {
		if (emfObject == null)
			return null;
		return internalGetAnnotations(emfObject, name);
	}

	protected static Object internalGetAnnotations(EObject emfObject, String name) {
		if (emfObject == null)
			return null;
		AnnotationsAdapter adapter = getAdapter(emfObject);
		return (adapter == null) ? internalGetAnnotations(emfObject.eContainer(), name) : adapter.getAnnotations(name);
	}


	/**
	 * @param emfObject
	 * @param string
	 */
	public static Object removeAnnotations(EObject emfObject, String name) {
		if (emfObject == null)
			return null;
		AnnotationsAdapter adapter = getAdapter(emfObject);
		return adapter.removeAnnotations(name);
	}

	/**
	 * @param name
	 * @param value
	 */
	protected void addAnnotations(String name, Object value) {
		getAnnnotations().put(name, value);
	}

	protected Object getAnnotations(String name) {
		return getAnnnotations().get(name);
	}

	protected Object removeAnnotations(String name) {
		return getAnnnotations().remove(name);
	}

	/**
	 * @return
	 */
	protected Map getAnnnotations() {
		if (annotationsMap == null)
			annotationsMap = new HashMap();
		return annotationsMap;
	}

	/**
	 * @param emfObject
	 * @return
	 */
	protected static AnnotationsAdapter getAdapter(EObject emfObject) {
		AnnotationsAdapter adapter = retrieveExistingAdapter(emfObject);
		return adapter == null ? createAdapter(emfObject) : adapter;
	}

	/**
	 * @param emfObject
	 * @return
	 */
	protected static AnnotationsAdapter createAdapter(EObject emfObject) {
		AnnotationsAdapter adapter = new AnnotationsAdapter();
		adapter.setTarget(emfObject);
		emfObject.eAdapters().add(adapter);
		return adapter;
	}

	/**
	 * @param emfObject
	 * @return
	 */
	protected static AnnotationsAdapter retrieveExistingAdapter(EObject emfObject) {
		return (AnnotationsAdapter) EcoreUtil.getExistingAdapter(emfObject, ADAPTER_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
	 */
	public boolean isAdapterForType(Object type) {
		return ADAPTER_TYPE.equals(type);
	}

}