package org.eclipse.wst.common.framework;

/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */


/**
 * Common interface for ArchiveRuntime and ArchiveWrapped exceptions, which can contain nested
 * exceptions
 */
public interface IWrappedException {
	/**
	 * Return the messages from this and all nested exceptions, in order from outermost to innermost
	 */
	public String[] getAllMessages();

	/**
	 * Return the messages from this and all nested exceptions, in order from outermost to
	 * innermost, concatenated as one
	 */
	public String getConcatenatedMessages();

	public Exception getInnerMostNestedException();

	public String getMessage();

	public java.lang.Exception getNestedException();

	public void printStackTrace();

	public void printStackTrace(java.io.PrintStream s);

	public void printStackTrace(java.io.PrintWriter s);
}