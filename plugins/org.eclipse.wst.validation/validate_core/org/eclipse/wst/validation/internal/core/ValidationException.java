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
package org.eclipse.wst.validation.internal.core;


import java.util.Locale;

import org.eclipse.wst.validation.internal.provisional.core.IMessage;

/**
 * <p>
 * This exception is the only exception which should be thrown by IValidators. The message in this
 * exception must be suitable for showing to the user. All ValidationExceptions will have their
 * message extracted when they're caught, and the message will be shown to the user.
 * 
 * @plannedfor 1.0
 * </p>
 */
public class ValidationException extends Exception {
	private Throwable _lowLevelException = null;
	private IMessage _message = null;
	private ClassLoader _loader = null;
	private final static long serialVersionUID = -3387516993124229949L;


	
	/**
	 * Constructs a new exception with a given message string. <br>
	 * <br>
	 * 
	 * @param message
	 *            IMessage, which is Locale-independent, which contains the message to be shown to
	 *            be shown to the user.
	 */
	public ValidationException(IMessage message) {
		this(message, null);
	}

	/**
	 * Constructs a new exception with a given message string, and low-level exception. <br>
	 * <br>
	 * 
	 * @param message
	 *            IMessage Locale-independent message to be shown to the user.
	 * @param exception
	 *            relevant low-level exception, or <code>null</code> if none. <br>
	 *            &nbsp;&nbsp;&nbsp For example, when a method fails because of a network
	 *            communications &nbsp;&nbsp;&nbsp problem, this would be the
	 *            <code>java.io.IOException</code> &nbsp;&nbsp;&nbsp describing the exact nature
	 *            of the problem.
	 */
	public ValidationException(IMessage message, Throwable exception) {
		super();
		_message = message;
		_lowLevelException = exception;
	}

	/**
	 * @return the low-level exception associated with this ValidationException.
	 */
	public Throwable getAssociatedException() {
		return _lowLevelException;
	}

	/**
	 * @return the IMessage to be shown to the user, or null if this exception should be handled
	 * internally.
	 */
	public IMessage getAssociatedMessage() {
		return _message;
	}

	/**
	 * @return if the IValidator which threw this exception was loaded by a different ClassLoader than the
	 * framework, this method returns the ClassLoader of the IValidator.
	 */
	public ClassLoader getClassLoader() {
		return _loader;
	}

	/**
	 * @return the error message string of this <code>Throwable</code> object if it was
	 *         {@link java.lang.Throwable#Throwable(String) created}with an error message string;
	 *         or <code>null</code> if it was {@link java.lang.Throwable#Throwable() created}with
	 *         no error message.
	 *  
	 */
	public String getMessage() {
		return _message.getText(getClassLoader());
	}

	/**
	 * @param locale
	 * 			The locale of which to get the message.
	 * @return the error message string of this <code>Throwable</code> object if it was
	 *         {@link java.lang.Throwable#Throwable(String) created}with an error message string;
	 *         or <code>null</code> if it was {@link java.lang.Throwable#Throwable() created}with
	 *         no error message.
	 */
	public String getMessage(Locale locale) {
		return _message.getText(locale, getClassLoader());
	}

	/**
	 * <p>
	 * If the IValidator which threw this exception was loaded by a different ClassLoader than the
	 * framework, this method should set the ClassLoader to be the ClassLoader of the IValidator.
	 * </p>
	 * @param loader
	 *  		ClassLoader of the validator
	 */
	public void setClassLoader(ClassLoader loader) {
		_loader = loader;
	}

	/**
	 * <p>
	 * Returns a short description of this throwable object. If this <code>Throwable</code> object
	 * was {@link java.lang.Throwable#Throwable(String) created}with an error message string, then
	 * the result is the concatenation of three strings:
	 * <ul>
	 * <li>The name of the actual class of this object
	 * <li>": " (a colon and a space)
	 * <li>The result of the {@link #getMessage}method for this object
	 * </ul>
	 * If this <code>Throwable</code> object was {@link java.lang.Throwable#Throwable() created}
	 * with no error message string, then the name of the actual class of this object is returned.
	 * </p>
	 * 
	 * @return a string representation of this <code>Throwable</code>.
	 */
	public String toString() {
		return toString(Locale.getDefault());
	}

	/**
	 * <p>
	 * Returns a short description of this throwable object. If this <code>Throwable</code> object
	 * was {@link java.lang.Throwable#Throwable(String) created}with an error message string, then
	 * the result is the concatenation of three strings:
	 * <ul>
	 * <li>The name of the actual class of this object
	 * <li>": " (a colon and a space)
	 * <li>The result of the {@link #getMessage}method for this object
	 * </ul>
	 * If this <code>Throwable</code> object was {@link java.lang.Throwable#Throwable() created}
	 * with no error message string, then the name of the actual class of this object is returned.
	 * </p>
	 * 
	 * @return a string representation of this <code>Throwable</code>.
	 */
	public String toString(Locale locale) {
		String s = getClass().getName();
		String message = getMessage(locale);
		return (message != null) ? (s + ": " + message) : s; //$NON-NLS-1$
	}
}