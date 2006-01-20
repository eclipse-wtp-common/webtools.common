/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $RCSfile: LogEntry.java,v $
 *  $Revision: 1.4 $  $Date: 2006/01/20 19:30:40 $ 
 */
package org.eclipse.jem.util.logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * This class should be used when logging information which should be grouped together. Instead of creating a new instance of this class every time it
 * is needed, for performance reasons, create an instance and reuse it.
 * <p>
 * Currently the only fields that are logged are the {@link #getText()} and {@link #getTargetException()}.
 * 
 * @since 1.0.0
 */
public class LogEntry {

	private int _executionMap = 0;

	private Throwable _caughtException = null;

	private String _propertiesFileName = null;

	private String localeOfOrigin = null;

	private String sourceIdentifier;

	private String elapsedTime;

	private String text;

	private String messageTypeIdentifier;

	/**
	 * The file name parameter must be a name which can be used by ResourceBundle to load the string from the .properties file. The parameter must not
	 * be null or the empty string.
	 * 
	 * @param propertiesFileName
	 * 
	 * @since 1.0.0
	 */
	public LogEntry(String propertiesFileName) {
		setPropertiesFileName(propertiesFileName);
	}

	/**
	 * Default Constructor
	 */
	public LogEntry() {
	}

	/**
	 * Get execution map
	 * 
	 * @return execution map
	 * 
	 * @since 1.0.0
	 */
	public int getExecutionMap() {
		return _executionMap;
	}

	/**
	 * Get the properties file name
	 * 
	 * @return properties file name or <code>null</code> if not set.
	 * 
	 * @since 1.0.0
	 */
	public String getPropertiesFileName() {
		return _propertiesFileName;
	}

	/**
	 * Get target exception
	 * 
	 * @return target exception or <code>null</code> if not set.
	 * 
	 * @since 1.0.0
	 */
	public Throwable getTargetException() {
		return _caughtException;
	}

	/**
	 * Get locale of origin
	 * 
	 * @return locale of origin or <code>null</code> if not set.
	 * 
	 * @since 1.0.0
	 */
	public String getLocaleOfOrigin() {
		return localeOfOrigin;
	}

	/**
	 * Get source identifier.
	 * 
	 * @return source identifier or <code>null</code> if not set.
	 * 
	 * @since 1.0.0
	 */
	public String getSourceidentifier() {
		return sourceIdentifier;
	}

	/**
	 * Get elapsed time
	 * 
	 * @return elapsed time
	 * 
	 * @since 1.0.0
	 */
	public String getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * Get the message type identifier
	 * 
	 * @return message type identifier or <code>null</code> if not set.
	 * 
	 * @since 1.0.0
	 */
	public String getMessageTypeIdentifier() {
		return messageTypeIdentifier;
	}

	/**
	 * Set execution map
	 * 
	 * @param map
	 * 
	 * @since 1.0.0
	 */
	public void setExecutionMap(int map) {
		_executionMap = map;
	}

	/**
	 * Set properties file name
	 * 
	 * @param fName
	 * 
	 * @since 1.0.0
	 */
	public void setPropertiesFileName(String fName) {
		_propertiesFileName = fName;
	}

	/**
	 * Set target exception
	 * 
	 * @param exc
	 * 
	 * @since 1.0.0
	 */
	public void setTargetException(Throwable exc) {
		_caughtException = exc;
	}

	/**
	 * Append stacktrace of current stack (at the time of call to this method) to the text buffer.
	 * 
	 * 
	 * @since 1.0.0
	 */
	public void appendStackTrace() {
		// Grab the stack trace from the Thread ...
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		PrintWriter printWriter = new PrintWriter(byteOutput);
		// Can't call Thread.dumpStack() because it doesn't take a writer as input.
		// Copy its mechanism instead.
		new Exception("Stack trace").printStackTrace(printWriter); //$NON-NLS-1$
		printWriter.flush();

		// and update the text to the LogEntry's text.
		StringBuffer buffer = new StringBuffer();
		buffer.append(getText());
		buffer.append("\n"); //$NON-NLS-1$
		buffer.append(byteOutput.toString());
		setText(buffer.toString());
	}

	/**
	 * Get the text.
	 * 
	 * @return text or or <code>null</code> if not set.
	 * 
	 * @since 1.0.0
	 */
	public String getText() {
		return text;
	}

	/**
	 * Set the text
	 * 
	 * @param string
	 * 
	 * @since 1.0.0
	 */
	public void setText(String string) {
		text = string;
	}

	/**
	 * Set every entry to the default value except the properties file name.
	 * 
	 * 
	 * @since 1.0.0
	 */
	public void reset() {
		setExecutionMap(0);
		setTargetException(null);
		localeOfOrigin = null;
		sourceIdentifier = null;
		elapsedTime = null;
		setText(null);
	}

	/**
	 * Set locale of origin.
	 * 
	 * @param origin
	 * 
	 * @since 1.0.0
	 */
	public void setLocaleOfOrigin(String origin) {
		localeOfOrigin = origin;
	}

	/**
	 * Set source id.
	 * 
	 * @param id
	 * 
	 * @since 1.0.0
	 */
	public void setSourceID(String id) {
		sourceIdentifier = id;
	}

	/**
	 * Set elapsed time.
	 * 
	 * @param time
	 * 
	 * @since 1.0.0
	 */
	public void setElapsedTime(long time) {
		elapsedTime = String.valueOf(time);
	}

	/**
	 * Set source identifier.
	 * 
	 * @param string
	 * 
	 * @since 1.0.0
	 */
	public void setSourceIdentifier(String string) {
		setSourceID(string);
	}

	/**
	 * Set message type identifier.
	 * 
	 * @param string
	 * 
	 * @since 1.0.0
	 * @deprecated Use {@link #setText(String)} instead and calling it with the result of {@link java.text.MessageFormat#format(java.lang.String, java.lang.Object[])}
	 */
	public void setMessageTypeIdentifier(String string) {
		messageTypeIdentifier = string;
	}

	/**
	 * Set message type id. Same as <code>setMessageTypeIdentifier.</code>
	 * @param string
	 * 
	 * @since 1.0.0
	 * @deprecated Use {@link #setText(String)} instead and calling it with the result of {@link java.text.MessageFormat#format(java.lang.String, java.lang.Object[])}
	 */
	public void setMessageTypeID(String string) {
		setMessageTypeIdentifier(string);
	}

	/**
	 * Set tokens. (Currently this is ignored).
	 * 
	 * @param strings
	 * 
	 * @since 1.0.0
	 * @deprecated Use {@link #setText(String)} instead and calling it with the result of {@link java.text.MessageFormat#format(java.lang.String, java.lang.Object[])}
	 */
	public void setTokens(String[] strings) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		PrintWriter printWriter = new PrintWriter(byteOutput);
		if (text != null)
			printWriter.println(text);
		if (_caughtException != null) {
			_caughtException.printStackTrace(printWriter);
		}
		printWriter.flush();
		return byteOutput.toString();
	}

}