package org.eclipse.wst.common.frameworks.internal.operations;


/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */

/**
 * Simple implementer that ignores messages and always allows actions to continue
 */
public class NullOperationHandler implements IOperationHandler {
	/**
	 * NullOperationHandler constructor comment.
	 */
	public NullOperationHandler() {
		super();
	}

	/**
	 * A decision needs to made as to whether an action/operation can continue
	 */
	public boolean canContinue(String message) {
		return true;
	}

	/**
	 * A decision needs to made as to whether an action/operation can continue
	 */
	public boolean canContinue(String message, String[] items) {
		return true;
	}

	/**
	 * A decision needs to made as to whether an action/operation can continue. The boolean array
	 * will return two booleans. The first indicates their response to the original question and the
	 * second indicates if they selected the apply to all check box.
	 * 
	 * Return the return code for the dialog. 0 = Yes, 1 = Yes to all, 2 = No
	 */
	public int canContinueWithAllCheck(java.lang.String message) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.j2ee.operations.IOperationHandler#canContinueWithAllCheckAllowCancel(java.lang.String)
	 */
	public int canContinueWithAllCheckAllowCancel(String message) {
		return 0;
	}

	/**
	 * An error has occurred
	 */
	public void error(String message) {
		System.err.println(message);
	}

	/**
	 * An informational message needs to be presented
	 */
	public void inform(String message) {
	}

	/**
	 * @see com.ibm.etools.j2ee.operations.IOperationHandler#getContext()
	 */
	public Object getContext() {
		return null;
	}


}