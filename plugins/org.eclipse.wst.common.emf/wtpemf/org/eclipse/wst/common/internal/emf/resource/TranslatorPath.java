/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;


import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;


public class TranslatorPath {
	protected Translator[] mapInfoPath;

	/**
	 * Construct with an array of Translator that specifies the path to follow from an object to
	 * another object
	 */
	public TranslatorPath(Translator[] path) {
		mapInfoPath = path;
	}

	public List findObjects(EObject startObject) {
		Object cur = startObject;
		for (int i = 0; i < mapInfoPath.length; i++) {
			Translator curMap = mapInfoPath[i];
			if (cur instanceof EObject) {
				EStructuralFeature curAttr = curMap.getFeature();
				if (curAttr == Translator.CONTAINER_FEATURE) {
					curAttr = ((EObject) cur).eContainmentFeature();
					cur = ((EObject) cur).eContainer();
				} else if (curAttr == Translator.ROOT_FEATURE) {
					cur = ((TranslatorResource) startObject.eResource()).getRootObject();
				} else {
					cur = ((EObject) cur).eGet(curAttr);
				}
				if (curMap.isMultiValued()) {
					return (List) cur;
				}
			}
		}
		return null;
	}

	public Object findObject(EObject startObject, Object matchValue) {
		List objects = findObjects(startObject);
		if (objects == null)
			return null;
		return findObject(objects, getLastMap(), matchValue);
	}

	private Object findObject(List objectList, Translator map, Object matchValue) {
		for (Iterator iter = objectList.iterator(); iter.hasNext();) {
			EObject mofObject = (EObject) iter.next();
			Object curMatchValue = mofObject.eGet(map.getFeature());
			if (matchValue.equals(curMatchValue))
				return mofObject;
		}
		return null;
	}

	public Translator getLastMap() {
		return mapInfoPath[mapInfoPath.length - 1];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String cn = getClass().getName();
		int i = cn.lastIndexOf('.');
		cn = cn.substring(++i, cn.length());
		sb.append(cn);
		sb.append('(');
		sb.append(mapInfoPath[0]);
		for (int j = 1; j < mapInfoPath.length; j++) {
			sb.append('\n');
			sb.append(mapInfoPath[j]);
		}
		sb.append(')');
		return sb.toString();
	}

}