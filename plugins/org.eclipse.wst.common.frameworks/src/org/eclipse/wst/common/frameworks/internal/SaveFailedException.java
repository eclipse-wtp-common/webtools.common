package org.eclipse.wst.common.frameworks.internal;


/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */


/**
 * Runtime exception that could get thrown during save of an edit model; clients should use
 * {@link #getConcatenatedMessages}to get all the messages of this and all nested exceptions to
 * report the failure.
 */
public class SaveFailedException extends WrappedRuntimeException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -4640018901910731240L;

	/**
	 * SaveFailedException constructor comment.
	 */
	public SaveFailedException() {
		super();
	}

	/**
	 * SaveFailedException constructor comment.
	 * 
	 * @param e
	 *            java.lang.Exception
	 */
	public SaveFailedException(Exception e) {
		super(e);
	}

	/**
	 * SaveFailedException constructor comment.
	 * 
	 * @param s
	 *            java.lang.String
	 */
	public SaveFailedException(String s) {
		super(s);
	}

	/**
	 * SaveFailedException constructor comment.
	 * 
	 * @param s
	 *            java.lang.String
	 * @param e
	 *            java.lang.Exception
	 */
	public SaveFailedException(String s, Exception e) {
		super(s, e);
	}
}