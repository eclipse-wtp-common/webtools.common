/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;


import java.util.StringTokenizer;

import org.eclipse.core.resources.IResourceDelta;

/**
 * This class stores the value of the "action" attribute in the validator's plugin.xml contribution.
 */
public class ValidatorActionFilter {
	public static final String ADD = "add"; //$NON-NLS-1$ // if the resource delta is an addition; this value is used in plugin.xml
	public static final String CHANGE = "change"; //$NON-NLS-1$ // if the resource delta is a change; this value is used in plugin.xml
	public static final String DELETE = "delete"; //$NON-NLS-1$ // if the resource delta is a removal; this value is used in plugin.xml
	public static final int ALL_ACTIONS = (IResourceDelta.ADDED | IResourceDelta.CHANGED | IResourceDelta.REMOVED);

	private int _actionType = 0; // Default to 0, so that if an invalid filter is specified, then no

	// matter what the IResourceDelta is, the delta & _actionType will
	// always == 0. (i.e., the resource will never be filtered in)

	public ValidatorActionFilter() {
		super();
	}

	/**
	 * Return the hexadecimal number which represents the type(s) of actions which this filter
	 * allows in.
	 */
	public int getActionType() {
		// Since IResourceDelta's constants are hexadecimal numbers,
		// it's nicer to return a corresponding hexadecimal, for bitwise OR,
		// than it is to have three boolean methods on this class, i.e.,
		// isAdd, isChange, isDelete.
		return _actionType;
	}

	/**
	 * <p>
	 * Parse the incoming string, which is extracted from the plugin.xml file, to determine the
	 * value of the actionType.
	 * <p>
	 * The string can contain one, two, or three constants. If there is more than one constant, the
	 * constants should be separated by a comma.
	 * <p>
	 * These are the three constants: add, change, delete. The order that the constants are
	 * specified in does not matter. The constants are case-sensitive; i.e., ADD is not considered
	 * the same as add.
	 * <p>
	 * If the action attribute is not defined, the default behaviour is to filter in all types of
	 * actions: add, change, delete. (i.e., the same behaviour can be achieved by specifying "add,
	 * change, delete" as the action attribute's value.
	 * <p>
	 * If the action attribute is defined, and none of the constants are defined, then the filter is
	 * invalid, and will be ignored by the Validation Framework. (If none of the actions should be
	 * filtered in, then the filter itself should not exist.)
	 * <p>
	 * If the action attribute is defined, and one of the constants is defined, then the form of the
	 * action should be like this: <br>
	 * &nbsp;&nbsp;&nbsp;&lt;filter ... action="add"/>
	 * <p>
	 * If the action attribute is defined, and more than one constant is defined, then the form of
	 * the action should be like this: <br>
	 * &nbsp;&nbsp;&nbsp;&lt;filter ... action="add, delete"/>
	 * <p>
	 * If the action attribute is defined, and an unknown constant is defined, then the unknown
	 * constant will be ignored. For example, <br>
	 * &nbsp;&nbsp;&nbsp;&lt;filter ... action="ADD, delete"/> <br>
	 * is the same as specifying <br>
	 * &nbsp;&nbsp;&nbsp;&lt;filter ... action="delete"/> <br>
	 * and if all of the constants are unknown, the filter is invalid, and will be ignored by the
	 * Validation Framework. e.g., <br>
	 * &nbsp;&nbsp;&nbsp;&lt;filter ... action="ADD, DELETE"/> <br>
	 * is the same as not specifying a filter.
	 * <p>
	 * If the action attribute is defined, and a constant is defined more than once, the extra
	 * constant is ignored. For example, <br>
	 * &nbsp;&nbsp;&nbsp;&lt;filter ... action="add, change, add"/> <br>
	 * is the same as specifying <br>
	 * &nbsp;&nbsp;&nbsp;&lt;filter ... action="add, change"/>
	 */
	public void setActionTypes(String actions) {
		if (actions == null) {
			// user has not defined the "action" element, so default to everything
			_actionType = ALL_ACTIONS;
			return;
		}

		final String COMMA = ","; //$NON-NLS-1$
		StringTokenizer tokenizer = new StringTokenizer(actions, COMMA, false); // false means don't
		// return the comma
		// as part of the
		// string
		int isAdd = 0;
		int isChange = 0;
		int isDelete = 0;
		while (tokenizer.hasMoreTokens()) {
			String nextAction = tokenizer.nextToken().trim();
			if (nextAction.equals(ADD)) {
				isAdd = IResourceDelta.ADDED;
			} else if (nextAction.equals(CHANGE)) {
				isChange = IResourceDelta.CHANGED;
			} else if (nextAction.equals(DELETE)) {
				isDelete = IResourceDelta.REMOVED;
			}
		}
		_actionType = isAdd | isChange | isDelete;
	}

	public String toString() {
		final String ON = "on"; //$NON-NLS-1$
		final String OFF = "off"; //$NON-NLS-1$
		StringBuffer buffer = new StringBuffer();
		buffer.append("     ActionFilter:"); //$NON-NLS-1$
		buffer.append("          add: " + (((getActionType() & IResourceDelta.ADDED) != 0) ? ON : OFF)); //$NON-NLS-1$
		buffer.append("          change: " + (((getActionType() & IResourceDelta.CHANGED) != 0) ? ON : OFF)); //$NON-NLS-1$
		buffer.append("          delete: " + (((getActionType() & IResourceDelta.REMOVED) != 0) ? ON : OFF)); //$NON-NLS-1$
		return buffer.toString();
	}
}