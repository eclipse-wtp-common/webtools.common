/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.enablement;


/**
 * @author mdelder
 *  
 */
public class IdentifiableComparator implements java.util.Comparator {

	protected static final int GREATER_THAN = 1;

	protected static final int LESS_THAN = -1;

	protected static final int EQUAL = 0;

	protected static final IdentifiableComparator instance = new IdentifiableComparator();

	private static final int DEFAULT_PRIORITY = Integer.MAX_VALUE;

	public static IdentifiableComparator getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		Identifiable lvalue = null;
		Identifiable rvalue = null;
		if (o1 instanceof Identifiable)
			lvalue = (Identifiable) o1;
		if (o2 instanceof Identifiable)
			rvalue = (Identifiable) o2;

		if (rvalue == null)
			return GREATER_THAN;
		if (lvalue == null)
			return LESS_THAN;

		if ((lvalue.getID() == null && rvalue.getID() == null) || (getPriority(lvalue) == getPriority(rvalue)))
			return compareLoadOrder(lvalue, rvalue);
		/* R - L implies 0 is the highest priority */
		return getPriority(lvalue) - getPriority(rvalue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof IdentifiableComparator;
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	private int compareLoadOrder(Identifiable lvalue, Identifiable rvalue) {
		/* R - L implies 0 is the highest priority */
		return lvalue.getLoadOrder() - rvalue.getLoadOrder();
	}

	public int getPriority(Identifiable identifiable) {
		if (identifiable.getID() != null && identifiable.getID().length() > 0)
			return FunctionGroupRegistry.getInstance().getFunctionPriority(identifiable.getID());
		return DEFAULT_PRIORITY;
	}

	
}
