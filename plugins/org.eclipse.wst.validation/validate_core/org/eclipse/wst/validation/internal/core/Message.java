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


import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.wst.validation.internal.provisional.core.IMessage;

/**
 * <p>
 * Default implementation of the IMessage interface, provided for the convenience of the
 * IValidators. If an IValidator needs to run in both AAT and WSAD then this IMessage implementation
 * should be used; if the IValidator runs in WSAD alone, the WSAD LocalizedMessage may be used in
 * place of this implementation.
 * <p>
 * @see org.eclipse.wst.validation.internal.provisional.core.IMessage
 * 
 * [issue: CS - I'd suggest splitting this class into Message and BundleMessage (where the latter inherits
 * from the former.  We have many messages that come (from xerces) pretranslated and don't require 'bundle'
 * related fields and methods. Splitting this class would make it easier to understand where bundle related
 * function is coming into play. Below I've listed out what would go into BundleMessage to demonstrate how 
 * we can simplify the 'Message' class by factoring out the bundle related details.
 * 
 * Message
 *	private Object targetObject = null;
 *	private String groupName = null;
 *	private int lineNumber = IMessage.LINENO_UNSET;
 *	private int length = IMessage.OFFSET_UNSET;
 *	private int offset = IMessage.OFFSET_UNSET;
 *
 *    
 * BundleMessage
 *	private String id = null;
 *	private String[] params = null;
 *	private String bundleName = null;
 *   
 *   - getId()
 *   - getBundle(...)
 *   - getBundleName()
 *   - getParams()
 *   - getText(Locale)
 *   - getText(ClassLoader)
 *   - getText(Locale, ClassLoader)
 *   - setId(String)
 *   - setBundleName(String)
 *   - setParams(String[])
 * ]
 */
public class Message implements IMessage {
	private String id;
	private String[] params;
	private int severity = MessageFilter.ANY_SEVERITY;
	private Object targetObject;
	private String bundleName;
	private String groupName;
	private int lineNumber = IMessage.LINENO_UNSET;
	private int length = IMessage.OFFSET_UNSET;
	private int offset = IMessage.OFFSET_UNSET;
	private HashMap messageAttributes;
	private String markerId;

	/**
	 * <p>
	 * Creates a default instance of the Message
	 * </p>
	 */
	public Message() {
		super();
	}

	/**
	 * <p>
	 * Creates a Message object with bundle name, severity and a unique id
	 * </p>
	 * 
	 * @param aBundleName 
	 * 			Must not be null or the empty string (""). 
	 * @param aSeverity 
	 * 			Must be one of the severities specified in IMessage. 
	 * @param anId 
	 * 			Must not be null or the empty string ("").
	 */
	public Message(String aBundleName, int aSeverity, String anId) {
		this(aBundleName, aSeverity, anId, null, null);
	}

	/**
	 * <p>
	 * Creates a Message object with bundle name, severity, a unique id, and 
	 * a list of parameters. 
	 * </p>
	 * 
	 * @param aBundleName 
	 * 			Must not be null or the empty string (""). 
	 * @param aSeverity 
	 * 			Must be one of the severities specified in IMessage.
	 * @param anId 
	 * 			Must not be null or the empty string ("").
	 * @param aParams 
	 * 			May be null, if there are no parameters in the message.
	 */
	public Message(String aBundleName, int aSeverity, String anId, String[] aParams) {
		this(aBundleName, aSeverity, anId, aParams, null);
	}
	
	/**
	 * <p>
	 * Creates a Message object with bundle name, severity, a unique id, and 
	 * a list of parameters and the target object.
	 * </p>
	 * 
	 * @param aBundleName 
	 * 			Must not be null or the empty string (""). 
	 * @param aSeverity 
	 * 			Must be one of the severities specified in IMessage.
	 * @param anId 
	 * 			Must not be null or the empty string ("").
	 * @param aParams 
	 * 			May be null, if there are no parameters in the message.
	 * @param targetObject 
	 * 			May be null, if the message does not pertain to a particular 
	 * 			object.
	 */
	public Message(String aBundleName, int aSeverity, String anId, String[] aParams, Object aTargetObject) {
		bundleName = aBundleName;
		severity = aSeverity;
		id = anId;
		params = aParams;
		targetObject = aTargetObject;
	}
	
	public Message(String aBundleName, int aSeverity, String anId, String[] aParams, Object aTargetObject, String aGroupName) {
		bundleName = aBundleName;
		severity = aSeverity;
		id = anId;
		params = aParams;
		targetObject = aTargetObject;
		groupName = aGroupName;
	}

	/**
	 * @return the resource bundle which contains the messages, as identified by
	 * 
	 * @link #getBundleName()
	 */
	public ResourceBundle getBundle(Locale locale, ClassLoader classLoader) {
		ResourceBundle bundle = null;
		try {
			if (classLoader == null) {
				bundle = ResourceBundle.getBundle(getBundleName(), locale);
			} else {
				bundle = ResourceBundle.getBundle(getBundleName(), locale, classLoader);
			}
		} catch (MissingResourceException e) {
			//try to  load the bundle from the validation framework plugin
			bundle = getFrameworkBundle(locale);
		}
		return bundle;
	}

	private ResourceBundle getFrameworkBundle(Locale locale) {
		ResourceBundle bundle = null;
		try {
			bundle = ResourceBundle.getBundle(getBundleName(), locale, this.getClass().getClassLoader());
			
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}
		return bundle;
	}
	
	/**
	 * @see IMessage#getBundleName()
	 */
	public String getBundleName() {
		return bundleName;
	}

	/**
	 * @see IMessage#getGroupName()
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @see IMessage#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * @see IMessage#getLength()
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @see IMessage#getLineNumber()
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @see IMessage#getOffset()
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @see IMessage#getParams()
	 */
	public String[] getParams() {
		return params;
	}

	/**
	 * @see IMessage#getSeverity()
	 */
	public int getSeverity() {
		return severity;
	}

	/**
	 * @see IMessage#getTargetObject()
	 */
	public Object getTargetObject() {
		return targetObject;
	}

	/**
	 * @see IMessage#getText()
	 */
	public String getText() {
		return getText(Locale.getDefault(), null);
	}

	/**
	 * @see IMessage#getText(ClassLoader)
	 */
	public String getText(ClassLoader classLoader) {
		return getText(Locale.getDefault(), classLoader);
	}

	/**
	 * @see IMessage#getText(Locale)
	 */
	public String getText(Locale locale) {
		return getText(locale, null);
	}

	/**
	 * @see IMessage#getText(Locale, ClassLoader)
	 */
	public java.lang.String getText(Locale locale, ClassLoader classLoader) {
		String message = ""; //$NON-NLS-1$

		if (locale == null) {
			return message;
		}

		ResourceBundle bundle = getBundle(locale, classLoader);
		if (bundle == null) {
			return message;
		}

		try {
			message = bundle.getString(getId());

			if (getParams() != null) {
				message = java.text.MessageFormat.format(message, getParams());
			}
		} catch (MissingResourceException exc) {
			System.err.println(exc.getMessage());
			System.err.println(getId());
		} catch (NullPointerException exc) {
			System.err.println(exc.getMessage());
			System.err.println(getId());
		}

		return message;
	}

	/**
	 * @see IMessage#setBundleName(String)
	 */
	public void setBundleName(String aBundleName) {
		bundleName = aBundleName;
	}

	/**
	 * @see IMessage#setGroupName(String)
	 */
	public void setGroupName(String name) {
		groupName = name;
	}

	/**
	 * @see IMessage#setId(String)
	 */
	public void setId(String newId) {
		id = newId;
	}

	/**
	 * @see IMessage#setLength(int)
	 */
	public void setLength(int length) {
		if (length < 0) {
			length = IMessage.OFFSET_UNSET;
		}
		this.length = length;
	}

	/**
	 * @see IMessage#setLineNo(int)
	 */
	public void setLineNo(int lineNumber) {
		if (lineNumber < 0) {
			this.lineNumber = IMessage.LINENO_UNSET;
		} else {
			this.lineNumber = lineNumber;
		}
	}

	/**
	 * @see IMessage#setOffset(int)
	 */
	public void setOffset(int offset) {
		if (offset < 0) {
			offset = IMessage.OFFSET_UNSET;
		}
		this.offset = offset;
	}

	/**
	 * @see IMessage#setParams(String[])
	 */
	public void setParams(String[] newParams) {
		params = newParams;
	}

	/**
	 * @see IMessage#setSeverity(int)
	 */
	public void setSeverity(int newSeverity) {
		severity = newSeverity;
	}

	/**
	 * @see IMessage#setTargetObject(Object)
	 */
	public void setTargetObject(Object obj) {
		targetObject = obj;
	}

	public Object getAttribute(String attributeName) {
		if(messageAttributes != null) {
			return messageAttributes.get(attributeName);
		}
		return null;
	}

	public void setAttribute(String attributeName, Object value) {
		if(messageAttributes == null) {
			messageAttributes = new HashMap();
		}
		messageAttributes.put(attributeName,value);
	}
	
	public HashMap getAttributes() {
		return messageAttributes;
	}

	public String getMarkerId() {
		return markerId;
	}

	public void setMarkerId(String markerId) {
		this.markerId = markerId;
	}
}