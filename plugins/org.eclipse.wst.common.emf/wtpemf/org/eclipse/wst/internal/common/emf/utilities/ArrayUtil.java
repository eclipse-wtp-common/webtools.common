/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.internal.common.emf.utilities;



public class ArrayUtil {

	public static Object[] concat(Object[] array1, Object[] array2) {
		Class componentType = null;
		if (array1.getClass().getComponentType() == array2.getClass().getComponentType())
			componentType = array1.getClass().getComponentType();
		else
			componentType = Object.class;

		return concat(array1, array2, componentType);
	}

	public static Object[] concat(Object[] array1, Object[] array2, Class componentType) {
		Object[] result = (Object[]) java.lang.reflect.Array.newInstance(componentType, array1.length + array2.length);
		System.arraycopy(array1, 0, result, 0, array1.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);
		return result;
	}

	public static Object[] concatAll(java.util.List arrays, Class componentType) {
		int resultLength = 0;
		for (int i = 0; i < arrays.size(); i++) {
			resultLength += ((Object[]) arrays.get(i)).length;
		}
		Object[] result = (Object[]) java.lang.reflect.Array.newInstance(componentType, resultLength);
		int pos = 0;
		for (int i = 0; i < arrays.size(); i++) {
			Object[] array = (Object[]) arrays.get(i);
			System.arraycopy(array, 0, result, pos, array.length);
			pos += array.length;
		}
		return result;
	}
}