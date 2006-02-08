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
package org.eclipse.wst.validation.internal.provisional.core;


import java.util.HashMap;
import java.util.Locale;

/**
 * <p>
 * This interface represents a locale-independent validation message.
 * </p>
 * <p>
 * Given these three values as input, this interface can be queried for the final message string.
 * (i.e., the message with the parameters already substituted in.) Either the default Locale can be
 * used, or the caller can specify a Locale.
 * 
 * @plannedfor 1.0
 * </p>
 * 
 * [issue: CS - The 'IMessage' interface would be simpler without the bundle related details.  I'd like to understand
 * why we can 'precompute' the translated message. Is there some benefit to perform 'late computation' of these messages?
 * How does this compare to the eclipse Marker (as far as I know the Marker's text() is preLocalized).
 * Here's a list of the methods that are related to 'bundle specific' aspects and could be separate out (or designed away)?
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
 * 
 *   - void setAttribute(String attributeName, Object value)
 *   - Object getAttribute(String attributeName);
 * ]
 */
public interface IMessage {
	public static final int OFFSET_UNSET = -1; // see getLength(), getOffset()
	public static final int LINENO_UNSET = 0;
	/**
	 * Typically used to specify error messages.
	 */
	public static final int HIGH_SEVERITY = 0x0001;
	/**
	 * Typically used to specify warning messages.
	 */
	public static final int NORMAL_SEVERITY = 0x0002;
	/**
	 * Typically used to specify information messages.
	 */
	public static final int LOW_SEVERITY = 0x0004;
	/**
	 * Specify high (error) and normal (warning) messages. Typically used with a MessageFilter, to
	 * filter out information messages.
	 */
	public static final int ERROR_AND_WARNING = HIGH_SEVERITY | NORMAL_SEVERITY;
	/**
	 * Specify all types of messages. Typically used with a MessageFilter.
	 */
	public static final int ALL_MESSAGES = ERROR_AND_WARNING | LOW_SEVERITY;

	/**
	 * @return the name of the bundle which this message is contained in.
	 */
	public String getBundleName();

	/**
	 * <p>
	 * To support removal of a subset of validation messages, an IValidator may assign group names
	 * to IMessages. An IMessage subset will be identified by the name of its group. Default (null)
	 * means no group.
	 * </p>
	 * @return the name of the group to which the message belongs.
	 */
	public String getGroupName();

	/**
	 * <p>
	 * Returns the id of the message. Message ids are used as the constants in property bundles
	 * which localize the description of the message in a locale-independent fashion. The id is the
	 * key, in the resource bundle, which identifies the string to be loaded. The id may not be null
	 * or the empty string.
	 * </p>
	 * @return the id of the message
	 */
	public java.lang.String getId();

	/**
	 * @return the length of the problem area, starting from the offset. If unset, value =
	 * IMessage.OFFSET_UNSET.
	 * 
	 * @see #getOffset()
	 */
	public int getLength();

	/**
	 * <p>
	 * If there is a target object associated with this IMessage, and that target object is a file,
	 * then an optional line number may be set. The line number identifies the location of the
	 * problem identified by this message. If no line number has been set, #LINENO_UNSET will be
	 * returned.
	 * </p>
	 * @return line number of the location of the problem.
	 * 
	 */
	public int getLineNumber();

	/**
	 * <p>
	 * Returns the offset of the message. If unset, value = IMessage.OFFSET_UNSET. For example, if
	 * the java compiler were a validator, and it reported that a variable name was unknown, offset
	 * would identify the position of the first character of the unknown variable. (The position is
	 * identified by the number of characters from the start of the file.)
	 * </p>
	 * 
	 * @return offset of the message
	 */
	public int getOffset();

	/**
	 * <p>
	 * Returns the parameters of the message. Message parameters are the values which are
	 * substituted into parameter slots in localized message text ddscriptions.
	 * </p>
	 * 
	 * @return parameters of the message
	 */
	public java.lang.String[] getParams();

	/**
	 * <p>
	 * Returns the severity level of the message. One of SeverityEnum constants.
	 * 
	 * @see IMessage#HIGH_SEVERITY
	 * @see IMessage#NORMAL_SEVERITY
	 * @see IMessage#LOW_SEVERITY
	 * </p>
	 * 
	 * @return the severity level of the message
	 */
	public int getSeverity();

	/**
	 * <p>
	 * Return the object that this IMessage is reporting the problem against, if any. null will be
	 * returned if this is a general message which does not apply to a particular object; for
	 * example, "internal error".
	 * </p>
	 * 
	 * @return the target object for the message
	 */
	public Object getTargetObject();

	/**
	 * @return a text representation of this message formatted in the default Locale, with the
	 * bundle loaded by the default ClassLoader.
	 */
	public java.lang.String getText();

	/**
	 * @param classLoader 
	 * 				The ClassLoader which will be used to load the ResourceBundle.
	 *            
	 * @return a text representation of this message formatted in the default locale, with the
	 * bundle loaded by the specified ClassLoader.
	 */
	public java.lang.String getText(ClassLoader classLoader);

	/**
	 * @param locale
	 *            The locale to translate the message text into.
	 *            
	 * @return a text representation of this message formatted in the specified locale, with the
	 * bundle loaded by the default ClassLoader.
	 */
	public java.lang.String getText(Locale locale);

	/**
	 * @param locale
	 *            The locale to translate the message text into.
	 * @param classLoader
	 *            The ClassLoader which will be used to load the ResourceBundle.
	 *            
	 * @return a text representation of this message formatted in the specified locale, with the
	 * bundle loaded by the specified ClassLoader.
	 */
	public java.lang.String getText(Locale locale, ClassLoader classLoader);
	
	/**
	 * Provides a way to store some additional attributes that a message would like to store
	 * that can used by some other parties that are interested in those attribute values. Basically
	 * a convienince to pass object values around that can consumed by other Objects it they need it
	 * @param attributeName
	 * @return an Object basically the value associated with the object name.
	 */
	
	public Object getAttribute(String attributeName);
	
	/**
	 * Set the attributeName and value as key value pair
	 * @see getAttribute(String attributeName).
	 * @param attributeName
	 */
	
	void setAttribute(String attributeName, Object value);
	   

	/**
	 * Set the name of the bundle which this message is contained in.
	 * 
	 * @param bundleName 
	 * 			Name of the bundle which contains the message.
	 */
	public void setBundleName(String bundleName);

	/**
	 * <p>
	 * To support removal of a subset of validation messages, an IValidator may assign group names
	 * to IMessages. An IMessage subset will be identified by the name of its group. Default (null)
	 * means no group.
	 * </p>
	 * 
	 * @param name
	 * 			Name of the group.
	 */
	public void setGroupName(String name);

	/**
	 * <p>
	 * Set the id of the message. Message ids are used as the constants in property bundles which
	 * localize the description of the message in a locale-independent fashion. The id is the key,
	 * in the resource bundle, which identifies the string to be loaded. The id may not be null or
	 * the empty string.
	 * </p>
	 * 
	 * @param newId
	 *            Id of the message.
	 */
	public void setId(java.lang.String newId);

	/**
	 * <p>
	 * Sets the length of the problem, starting from the offset. If unset, value =
	 * IMessage.OFFSET_UNSET.
	 * 
	 * @see #setOffset(int)
	 * </p>
	 * 
	 * @param length 
	 *			sets the length 		
	 */
	public void setLength(int length);

	/**
	 * <p>
	 * If there is a target object associated with this IMessage, and that target object is a file,
	 * then an optional line number may be set. The line number identifies the location of the
	 * problem identified by this message. To indicate no line number, use #LINENO_UNSET.
	 * </p>
	 * @param lineNumber 
	 *			sets the line no. 	
	 */
	public void setLineNo(int lineNumber);

	/**
	 * <p>
	 * Sets the offset of the message. If unset, value = IMessage.OFFSET_UNSET. For example, if the
	 * java compiler were a validator, and it reported that a variable name was unknown, offset
	 * would identify the position of the first character of the unknown variable. (The position is
	 * identified by the number of characters from the start of the file.)
	 * </p>
	 * @param offset 
	 *			sets the offset of the message.
	 */
	public void setOffset(int offset);

	/**
	 * <p>
	 * Sets the parameters of the message. Message parameters are the values which are substituted
	 * into parameter slots in localized message text descriptions.
	 * </p>
	 * <p>
	 * For example, if getId() returns "MY_ID", then the ResourceBundle identified by
	 * getBundleName() is searched for the message named "MY_ID", and if found,
	 * </p>
	 * 
	 * @see java.text.MessageFormat#format(String, Object[]) is invoked on the message, with
	 *      <code>newParams</code> passed in as the Object[].
	 * @param newParams
	 *            parameters of the message.
	 */
	public void setParams(java.lang.String[] newParams);

	/**
	 * Sets the severity level of the message. One of SeverityEnum constants.
	 * 
	 * @see IMessage#HIGH_SEVERITY
	 * @see IMessage#NORMAL_SEVERITY
	 * @see IMessage#LOW_SEVERITY
	 * 
	 * @param newSeverity
	 *            severity level of the message
	 */
	public void setSeverity(int newSeverity);

	/**
	 * <p>
	 * Associate this IMessage with the object that has the problem. A null value is permitted. If a
	 * message is applicable to one object, then this value should be set. Otherwise, the default
	 * value (null) should be kept. Given this target object, it should be possible to calculate the
	 * exact location of the problem.
	 * </p>
	 * 
	 * @param obj
	 *         The object that has the problem.
	 */
	public void setTargetObject(Object obj);
	
	/**
	  * return all the attributes of a Message object
	  * @return
	  */
	public HashMap getAttributes();

}