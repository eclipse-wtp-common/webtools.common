/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.enablement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Rename this class to EnablementsUtil
 */

public final class Util {
	public final static SortedMap EMPTY_SORTED_MAP = Collections.unmodifiableSortedMap(new TreeMap());
	public final static SortedSet EMPTY_SORTED_SET = Collections.unmodifiableSortedSet(new TreeSet());
	public final static String ZERO_LENGTH_STRING = ""; //$NON-NLS-1$

	public static void assertInstance(Object object, Class c) {
		assertInstance(object, c, false);
	}

	public static void assertInstance(Object object, Class c, boolean allowNull) {
		if (object == null && allowNull)
			return;

		if (object == null || c == null)
			throw new NullPointerException();
		else if (!c.isInstance(object))
			throw new IllegalArgumentException();
	}

	public static int compare(boolean left, boolean right) {
		return left == false ? (right == true ? -1 : 0) : 1;
	}

	public static int compare(Comparable left, Comparable right) {
		if (left == null && right == null)
			return 0;
		else if (left == null)
			return -1;
		else if (right == null)
			return 1;
		else
			return left.compareTo(right);
	}

	public static int compare(Comparable[] left, Comparable[] right) {
		if (left == null && right == null)
			return 0;
		else if (left == null)
			return -1;
		else if (right == null)
			return 1;
		else {
			int l = left.length;
			int r = right.length;

			if (l != r)
				return l - r;
			for (int i = 0; i < l; i++) {
				int compareTo = compare(left[i], right[i]);

				if (compareTo != 0)
					return compareTo;
			}
			return 0;
		}
	}

	public static int compare(int left, int right) {
		return left - right;
	}

	public static int compare(List left, List right) {
		if (left == null && right == null)
			return 0;
		else if (left == null)
			return -1;
		else if (right == null)
			return 1;
		else {
			int l = left.size();
			int r = right.size();

			if (l != r)
				return l - r;
			for (int i = 0; i < l; i++) {
				int compareTo = compare((Comparable) left.get(i), (Comparable) right.get(i));

				if (compareTo != 0)
					return compareTo;
			}
			return 0;
		}
	}

	public static void diff(Map left, Map right, Set leftOnly, Set different, Set rightOnly) {
		if (left == null || right == null || leftOnly == null || different == null || rightOnly == null)
			throw new NullPointerException();

		Iterator iterator = left.keySet().iterator();

		while (iterator.hasNext()) {
			Object key = iterator.next();

			if (!right.containsKey(key))
				leftOnly.add(key);
			else if (!Util.equals(left.get(key), right.get(key)))
				different.add(key);
		}

		iterator = right.keySet().iterator();

		while (iterator.hasNext()) {
			Object key = iterator.next();

			if (!left.containsKey(key))
				rightOnly.add(key);
		}
	}

	public static void diff(Set left, Set right, Set leftOnly, Set rightOnly) {
		if (left == null || right == null || leftOnly == null || rightOnly == null)
			throw new NullPointerException();

		Iterator iterator = left.iterator();

		while (iterator.hasNext()) {
			Object object = iterator.next();

			if (!right.contains(object))
				leftOnly.add(object);
		}

		iterator = right.iterator();

		while (iterator.hasNext()) {
			Object object = iterator.next();

			if (!left.contains(object))
				rightOnly.add(object);
		}
	}

	public static boolean endsWith(List left, List right, boolean equals) {
		if (left == null || right == null)
			return false;
		int l = left.size();
		int r = right.size();
		if (r > l || !equals && r == l)
			return false;

		for (int i = 0; i < r; i++)
			if (!equals(left.get(l - i - 1), right.get(r - i - 1)))
				return false;
		return true;
	}

	public static boolean endsWith(Object[] left, Object[] right, boolean equals) {
		if (left == null || right == null)
			return false;
		int l = left.length;
		int r = right.length;
		if (r > l || !equals && r == l)
			return false;
		for (int i = 0; i < r; i++)
			if (!equals(left[l - i - 1], right[r - i - 1]))
				return false;
		return true;
	}

	public static boolean equals(boolean left, boolean right) {
		return left == right;
	}

	public static boolean equals(int left, int right) {
		return left == right;
	}

	public static boolean equals(Object left, Object right) {
		return left == null ? right == null : left.equals(right);
	}

	public static int hashCode(boolean b) {
		return b ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
	}

	public static int hashCode(int i) {
		return i;
	}

	public static int hashCode(Object object) {
		return object != null ? object.hashCode() : 0;
	}

	public static List safeCopy(List list, Class c) {
		return safeCopy(list, c, false);
	}

	public static List safeCopy(List list, Class c, boolean allowNullElements) {
		if (list == null || c == null)
			throw new NullPointerException();

		List safeList = Collections.unmodifiableList(new ArrayList(list));
		Iterator iterator = safeList.iterator();

		while (iterator.hasNext())
			assertInstance(iterator.next(), c, allowNullElements);

		return safeList;
	}

	public static Map safeCopy(Map map, Class keyClass, Class valueClass) {
		return safeCopy(map, keyClass, valueClass, false, false);
	}

	public static Map safeCopy(Map map, Class keyClass, Class valueClass, boolean allowNullKeys, boolean allowNullValues) {
		if (map == null || keyClass == null || valueClass == null)
			throw new NullPointerException();

		Map safeMap = Collections.unmodifiableMap(new HashMap(map));
		Iterator iterator = safeMap.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			assertInstance(entry.getKey(), keyClass, allowNullKeys);
			assertInstance(entry.getValue(), valueClass, allowNullValues);
		}

		return safeMap;
	}

	public static Set safeCopy(Set set, Class c) {
		return safeCopy(set, c, false);
	}

	public static Set safeCopy(Set set, Class c, boolean allowNullElements) {
		if (set == null || c == null)
			throw new NullPointerException();

		Set safeSet = Collections.unmodifiableSet(new HashSet(set));
		Iterator iterator = safeSet.iterator();

		while (iterator.hasNext())
			assertInstance(iterator.next(), c, allowNullElements);

		return safeSet;
	}

	public static SortedMap safeCopy(SortedMap sortedMap, Class keyClass, Class valueClass) {
		return safeCopy(sortedMap, keyClass, valueClass, false, false);
	}

	public static SortedMap safeCopy(SortedMap sortedMap, Class keyClass, Class valueClass, boolean allowNullKeys, boolean allowNullValues) {
		if (sortedMap == null || keyClass == null || valueClass == null)
			throw new NullPointerException();

		SortedMap safeSortedMap = Collections.unmodifiableSortedMap(new TreeMap(sortedMap));
		Iterator iterator = safeSortedMap.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			assertInstance(entry.getKey(), keyClass, allowNullKeys);
			assertInstance(entry.getValue(), valueClass, allowNullValues);
		}

		return safeSortedMap;
	}

	public static SortedSet safeCopy(SortedSet sortedSet, Class c) {
		return safeCopy(sortedSet, c, false);
	}

	public static SortedSet safeCopy(SortedSet sortedSet, Class c, boolean allowNullElements) {
		if (sortedSet == null || c == null)
			throw new NullPointerException();

		SortedSet safeSortedSet = Collections.unmodifiableSortedSet(new TreeSet(sortedSet));
		Iterator iterator = safeSortedSet.iterator();

		while (iterator.hasNext())
			assertInstance(iterator.next(), c, allowNullElements);

		return safeSortedSet;
	}

	public static boolean startsWith(List left, List right, boolean equals) {
		if (left == null || right == null)
			return false;
		int l = left.size();
		int r = right.size();
		if (r > l || !equals && r == l)
			return false;
		for (int i = 0; i < r; i++)
			if (!equals(left.get(i), right.get(i)))
				return false;
		return true;
	}

	public static boolean startsWith(Object[] left, Object[] right, boolean equals) {
		if (left == null || right == null)
			return false;
		int l = left.length;
		int r = right.length;
		if (r > l || !equals && r == l)
			return false;

		for (int i = 0; i < r; i++)
			if (!equals(left[i], right[i]))
				return false;
		return true;
	}

	public static String translateString(ResourceBundle resourceBundle, String key) {
		return Util.translateString(resourceBundle, key, key, true, true);
	}

	public static String translateString(ResourceBundle resourceBundle, String key, String string, boolean signal, boolean trim) {
		if (resourceBundle != null && key != null)
			try {
				final String translatedString = resourceBundle.getString(key);

				if (translatedString != null)
					return trim ? translatedString.trim() : translatedString;
			} catch (MissingResourceException eMissingResource) {
				if (signal)
					System.err.println(eMissingResource);
			}

		return trim ? string.trim() : string;
	}

	private Util() {
	}
}
