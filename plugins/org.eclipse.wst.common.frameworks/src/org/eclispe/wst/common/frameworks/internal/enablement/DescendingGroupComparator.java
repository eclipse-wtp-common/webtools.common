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
/*
 * Created on Dec 2, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclispe.wst.common.frameworks.internal.enablement;

import java.util.Comparator;

/**
 * @author blancett
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class DescendingGroupComparator implements Comparator {

	private static DescendingGroupComparator singleton;

	public int compare(Object o1, Object o2) {
		if (o1 == null && o2 != null)
			return -1;
		if (o2 == null && o1 != null)
			return 1;
		if (o1 == null && o2 == null)
			return 0;

		FunctionGroup group1 = (FunctionGroup) o1;
		FunctionGroup group2 = (FunctionGroup) o2;

		if (group1.getPriority() > group2.getPriority())
			return -1;
		if (group1.getPriority() == group2.getPriority())
			return 0;
		if (group1.getPriority() < group2.getPriority())
			return 1;
		return 0;

	}

	public static Comparator singleton() {
		if (singleton == null)
			singleton = new DescendingGroupComparator();
		return singleton;
	}

}