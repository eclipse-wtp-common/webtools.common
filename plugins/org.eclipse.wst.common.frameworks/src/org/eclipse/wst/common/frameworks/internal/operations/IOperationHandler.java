package org.eclipse.wst.common.frameworks.internal.operations;

/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */

/**
 * Passed to complex methods that may require reporting of messages, or interactions from the user;
 * Defined as an interface because the actions may run in headless environments
 */
public interface IOperationHandler {
	int YES = 0;
	int YES_TO_ALL = 1;
	int NO = 2;
	int CANCEL = 3;

	/**
	 * A decision needs to made as to whether an action/operation can continue
	 */
	public boolean canContinue(String message);

	/**
	 * A decision needs to made as to whether an action/operation can continue. <code>items</code>
	 * is an array of details that accompany the <code>message</code>.
	 */
	public boolean canContinue(String message, String[] items);

	/**
	 * A decision needs to made as to whether an action/operation can continue. The boolean array
	 * will return two booleans. The first indicates their response to the original question and the
	 * second indicates if they selected the apply to all check box.
	 * 
	 * Return the return code for the dialog. 0 = Yes, 1 = Yes to all, 2 = No
	 */
	int canContinueWithAllCheck(String message);

	int canContinueWithAllCheckAllowCancel(String message);

	/**
	 * An error has occurred
	 */
	public void error(String message);

	/**
	 * An informational message needs to be presented
	 */
	public void inform(String message);

	public Object getContext();
}