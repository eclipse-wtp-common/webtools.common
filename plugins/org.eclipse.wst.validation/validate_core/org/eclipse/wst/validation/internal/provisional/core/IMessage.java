/*******************************************************************************
 * Copyright (c) 2001, 2012 IBM Corporation and others.
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
 * </p> 
 */
public interface IMessage {
	
	int OFFSET_UNSET = -1; // see getLength(), getOffset()
	int LINENO_UNSET = 0;
	
	/**
	 * Typically used to specify error messages.
	 */
	int HIGH_SEVERITY = 0x0001;
	/**
	 * Typically used to specify warning messages.
	 */
	int NORMAL_SEVERITY = 0x0002;
	/**
	 * Typically used to specify information messages.
	 */
	int LOW_SEVERITY = 0x0004;
	/**
	 * Specify high (error) and normal (warning) messages. Typically used with a MessageFilter, to
	 * filter out information messages.
	 */
	int ERROR_AND_WARNING = HIGH_SEVERITY | NORMAL_SEVERITY;
	/**
	 * Specify all types of messages. Typically used with a MessageFilter.
	 */
	int ALL_MESSAGES = ERROR_AND_WARNING | LOW_SEVERITY;
	
	/** 
	 * TargetResource - The key to use when associating a resource with a message via an
	 * attribute. 
	 * <p>
	 * Normally, the target is stored using the setTargetObject() method, but
	 * some of the legacy validators used this field for objects that where not IResources.
	 * In order to associate the message with the proper IResource the validator can store
	 * the IResource as an attribute and use this string as the key.
	 * </p>
	 */
	String TargetResource = "TargetResource"; //$NON-NLS-1$

	/**
	 * @return the name of the bundle which this message is contained in.
	 */
	String getBundleName();

	/**
	 * To support removal of a subset of validation messages, an IValidator may assign group names
	 * to IMessages. An IMessage subset will be identified by the name of its group. Default (null)
	 * means no group.
	 * 
	 * @return the name of the group to which the message belongs.
	 */
	String getGroupName();

	/**
	 * Returns the id of the message. Message ids are used as the constants in property bundles
	 * which localize the description of the message in a locale-independent fashion. The id is the
	 * key, in the resource bundle, which identifies the string to be loaded. The id may not be null
	 * or the empty string.
	 * 
	 * @return the id of the message
	 */
	String getId();

	/**
	 * @return the length of the problem area, starting from the offset. If unset, value =
	 * IMessage.OFFSET_UNSET.
	 * 
	 * @see #getOffset()
	 */
	int getLength();

	/**
	 * If there is a target object associated with this IMessage, and that target object is a file,
	 * then an optional line number may be set. The line number identifies the location of the
	 * problem identified by this message. If no line number has been set, #LINENO_UNSET will be
	 * returned.
	 * 
	 * @return line number of the location of the problem.
	 */
	int getLineNumber();

	/**
	 * Returns the offset of the message. If unset, value = IMessage.OFFSET_UNSET. For example, if
	 * the java compiler were a validator, and it reported that a variable name was unknown, offset
	 * would identify the position of the first character of the unknown variable. The position is
	 * identified by the number of characters from the start of the file.
	 * 
	 * @return offset of the message
	 */
	int getOffset();

	/**
	 * Returns the parameters of the message. Message parameters are the values which are
	 * substituted into parameter slots in localized message text descriptions.
	 * 
	 * @return parameters of the message
	 */
	String[] getParams();

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
	int getSeverity();

	/**
	 * Return the object that this IMessage is reporting the problem against, if any. null will be
	 * returned if this is a general message which does not apply to a particular object; for
	 * example, "internal error".
	 * 
	 * @return the target object for the message
	 */
	Object getTargetObject();

	/**
	 * @return a text representation of this message formatted in the default Locale, with the
	 * bundle loaded by the default ClassLoader.
	 */
	String getText();

	/**
	 * @param classLoader 
	 * 				The ClassLoader which will be used to load the ResourceBundle.
	 *            
	 * @return a text representation of this message formatted in the default locale, with the
	 * bundle loaded by the specified ClassLoader.
	 */
	String getText(ClassLoader classLoader);

	/**
	 * @param locale
	 *            The locale to translate the message text into.
	 *            
	 * @return a text representation of this message formatted in the specified locale, with the
	 * bundle loaded by the default ClassLoader.
	 */
	String getText(Locale locale);

	/**
	 * @param locale
	 *            The locale to translate the message text into.
	 * @param classLoader
	 *            The ClassLoader which will be used to load the ResourceBundle.
	 *            
	 * @return a text representation of this message formatted in the specified locale, with the
	 * bundle loaded by the specified ClassLoader.
	 */
	String getText(Locale locale, ClassLoader classLoader);
	
	/**
	 * Provides a way to store some additional attributes that a message would like to store
	 * that can used by some other parties that are interested in those attribute values.
	 * 
	 * @param attributeName
	 * @return an Object basically the value associated with the object name.
	 */
	
	 Object getAttribute(String attributeName);
	
	/**
	 * Set the attributeName and value as key value pair.
	 * 
	 * @see #getAttribute(String attributeName)
	 * @param attributeName
	 */
	
	void setAttribute(String attributeName, Object value);
	   

	/**
	 * Set the name of the bundle which this message is contained in.
	 * 
	 * @param bundleName 
	 * 			Name of the bundle which contains the message.
	 */
	void setBundleName(String bundleName);

	/**
	 * To support removal of a subset of validation messages, an IValidator may assign group names
	 * to IMessages. An IMessage subset will be identified by the name of its group. Default (null)
	 * means no group.
	 * 
	 * @param name
	 * 			Name of the group.
	 */
	void setGroupName(String name);

	/**
	 * Set the id of the message. Message ids are used as the constants in property bundles which
	 * localize the description of the message in a locale-independent fashion. The id is the key,
	 * in the resource bundle, which identifies the string to be loaded. The id may not be null or
	 * the empty string.
	 * 
	 * @param newId
	 *            Id of the message.
	 */
	void setId(String newId);

	/**
	 * Sets the length of the problem, starting from the offset. If unset, value =
	 * IMessage.OFFSET_UNSET.
	 * 
	 * @see #setOffset(int)
	 * 
	 * @param length 
	 *			Sets the length. 		
	 */
	void setLength(int length);

	/**
	 * If there is a target object associated with this IMessage, and that target object is a file,
	 * then an optional line number may be set. The line number identifies the location of the
	 * problem identified by this message. To indicate no line number, use #LINENO_UNSET.
	 * 
	 * @param lineNumber 
	 *			Sets the line number.
	 */
	void setLineNo(int lineNumber);

	/**
	 * Sets the offset of the message. If unset, value = IMessage.OFFSET_UNSET. For example, if the
	 * java compiler were a validator, and it reported that a variable name was unknown, offset
	 * would identify the position of the first character of the unknown variable. The position is
	 * identified by the number of characters from the start of the file.
	 * 
	 * @param offset 
	 *			Sets the offset of the message.
	 */
	void setOffset(int offset);

	/**
	 * Sets the parameters of the message. Message parameters are the values which are substituted
	 * into parameter slots in localized message text descriptions.
	 * <p>
	 * For example, if getId() returns "MY_ID", then the ResourceBundle identified by
	 * getBundleName() is searched for the message named "MY_ID", and if found,
	 * has it's parameters set.
	 * </p>
	 * 
	 * @see java.text.MessageFormat#format(String, Object[]) is invoked on the message, with
	 *      <code>newParams</code> passed in as the Object[].
	 *      
	 * @param newParams
	 *            Parameters of the message.
	 */
	void setParams(String[] newParams);

	/**
	 * Sets the severity level of the message. One of SeverityEnum constants.
	 * 
	 * @see IMessage#HIGH_SEVERITY
	 * @see IMessage#NORMAL_SEVERITY
	 * @see IMessage#LOW_SEVERITY
	 * 
	 * @param newSeverity
	 *            Severity level of the message.
	 */
	void setSeverity(int newSeverity);

	/**
	 * Associate this IMessage with the object that has the problem. A null value is permitted. If a
	 * message is applicable to one object, then this value should be set. Otherwise, the default
	 * value (null) should be kept. Given this target object, it should be possible to calculate the
	 * exact location of the problem.
	 * 
	 * @param obj
	 *         The object that has the problem.
	 */
	void setTargetObject(Object obj);
	
	/**
	 * Return all the attributes of a Message object.
	 */
	HashMap getAttributes();
	
	/**
	 * Return the marker id if one is set on this object when created.
	 */
	String getMarkerId();
	
	/**
	 * Set the marker id on a Message object.
	 */
	 void setMarkerId(String markerId);
}
