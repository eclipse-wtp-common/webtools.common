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
package org.eclipse.wst.validation.core;


import java.util.Locale;

/**
 * This interface represents a locale-independent validation message.
 * 
 * Given these three values as input, this interface can be queried for the final message string.
 * (i.e., the message with the parameters already substituted in.) Either the default Locale can be
 * used, or the caller can specify a Locale.
 */
public interface IMessage {
	public static final int OFFSET_UNSET = -1; // see getLength(), getOffset()
	public static final int LINENO_UNSET = 0;

	/**
	 * Return the name of the bundle which this message is contained in.
	 */
	public String getBundleName();

	/**
	 * To support removal of a subset of validation messages, an IValidator may assign group names
	 * to IMessages. An IMessage subset will be identified by the name of its group. Default (null)
	 * means no group.
	 */
	public String getGroupName();

	/**
	 * Returns the id of the message. Message ids are used as the constants in property bundles
	 * which localize the description of the message in a locale-independent fashion. The id is the
	 * key, in the resource bundle, which identifies the string to be loaded. The id may not be null
	 * or the empty string.
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getId();

	/**
	 * Returns the length of the problem area, starting from the offset. If unset, value =
	 * IMessage.OFFSET_UNSET.
	 * 
	 * @return int
	 * @see #getOffset()
	 */
	public int getLength();

	/**
	 * If there is a target object associated with this IMessage, and that target object is a file,
	 * then an optional line number may be set. The line number identifies the location of the
	 * problem identified by this message. If no line number has been set, #LINENO_UNSET will be
	 * returned.
	 */
	public int getLineNo();

	/**
	 * Returns the offset of the message. If unset, value = IMessage.OFFSET_UNSET. For example, if
	 * the java compiler were a validator, and it reported that a variable name was unknown, offset
	 * would identify the position of the first character of the unknown variable. (The position is
	 * identified by the number of characters from the start of the file.)
	 * 
	 * @return int
	 */
	public int getOffset();

	/**
	 * Returns the parameters of the message. Message parameters are the values which are
	 * substituted into parameter slots in localized message text ddscriptions.
	 * 
	 * @return java.lang.String[]
	 */
	public java.lang.String[] getParams();

	/**
	 * Returns the severity level of the message. One of SeverityEnum.XXX constants.
	 * 
	 * @see SeverityEnum#HIGH_SEVERITY
	 * @see SeverityEnum#NORMAL_SEVERITY
	 * @see SeverityEnum#LOW_SEVERITY
	 * 
	 * @return int
	 */
	public int getSeverity();

	/**
	 * Return the object that this IMessage is reporting the problem against, if any. null will be
	 * returned if this is a general message which does not apply to a particular object; for
	 * example, "internal error".
	 */
	public Object getTargetObject();

	/**
	 * Returns a text representation of this message formatted in the default Locale, with the
	 * bundle loaded by the default ClassLoader.
	 */
	public java.lang.String getText();

	/**
	 * Returns a text representation of this message formatted in the default locale, with the
	 * bundle loaded by the specified ClassLoader.
	 * 
	 * @param classLoader
	 *            The ClassLoader which will be used to load the ResourceBundle.
	 */
	public java.lang.String getText(ClassLoader classLoader);

	/**
	 * Returns a text representation of this message formatted in the specified locale, with the
	 * bundle loaded by the default ClassLoader.
	 * 
	 * @param locale
	 *            The locale to translate the message text into.
	 */
	public java.lang.String getText(Locale locale);

	/**
	 * Returns a text representation of this message formatted in the specified locale, with the
	 * bundle loaded by the specified ClassLoader.
	 * 
	 * @param locale
	 *            The locale to translate the message text into.
	 * @param classLoader
	 *            The ClassLoader which will be used to load the ResourceBundle.
	 */
	public java.lang.String getText(Locale locale, ClassLoader classLoader);

	/**
	 * Set the name of the bundle which this message is contained in.
	 */
	public void setBundleName(String bundleName);

	/**
	 * To support removal of a subset of validation messages, an IValidator may assign group names
	 * to IMessages. An IMessage subset will be identified by the name of its group. Default (null)
	 * means no group.
	 */
	public void setGroupName(String name);

	/**
	 * Set the id of the message. Message ids are used as the constants in property bundles which
	 * localize the description of the message in a locale-independent fashion. The id is the key,
	 * in the resource bundle, which identifies the string to be loaded. The id may not be null or
	 * the empty string.
	 * 
	 * @param newId
	 *            java.lang.String
	 */
	public void setId(java.lang.String newId);

	/**
	 * Sets the length of the problem, starting from the offset. If unset, value =
	 * IMessage.OFFSET_UNSET.
	 * 
	 * @see #setOffset(int)
	 */
	public void setLength(int length);

	/**
	 * If there is a target object associated with this IMessage, and that target object is a file,
	 * then an optional line number may be set. The line number identifies the location of the
	 * problem identified by this message. To indicate no line number, use #LINENO_UNSET.
	 */
	public void setLineNo(int lineNumber);

	/**
	 * Sets the offset of the message. If unset, value = IMessage.OFFSET_UNSET. For example, if the
	 * java compiler were a validator, and it reported that a variable name was unknown, offset
	 * would identify the position of the first character of the unknown variable. (The position is
	 * identified by the number of characters from the start of the file.)
	 */
	public void setOffset(int offset);

	/**
	 * Sets the parameters of the message. Message parameters are the values which are substituted
	 * into parameter slots in localized message text descriptions.
	 * 
	 * For example, if getId() returns "MY_ID", then the ResourceBundle identified by
	 * getBundleName() is searched for the message named "MY_ID", and if found,
	 * 
	 * @see java.text.MessageFormat#format(String, Object[]) is invoked on the message, with
	 *      <code>newParams</code> passed in as the Object[].
	 * @param newParams
	 *            java.lang.String[]
	 */
	public void setParams(java.lang.String[] newParams);

	/**
	 * Sets the severity level of the message. One of SeverityEnum.XXX constants.
	 * 
	 * @see SeverityEnum#HIGH_SEVERITY
	 * @see SeverityEnum#NORMAL_SEVERITY
	 * @see SeverityEnum#LOW_SEVERITY
	 * 
	 * @param newType
	 *            int
	 */
	public void setSeverity(int newSeverity);

	/**
	 * Associate this IMessage with the object that has the problem. A null value is permitted. If a
	 * message is applicable to one object, then this value should be set. Otherwise, the default
	 * value (null) should be kept. Given this target object, it should be possible to calculate the
	 * exact location of the problem.
	 */
	public void setTargetObject(Object obj);
}