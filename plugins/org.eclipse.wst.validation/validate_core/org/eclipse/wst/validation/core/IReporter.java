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



/*
 * CCM - Reporter is now passed locale-independent messages.
 *       Messages should only be translated to a locale upon access.
 *       Or in special locale-dependent reporter implementations (console logger).
 */

/**
 * The interface used by IValidator's to report messages. The implementation of the IReporter could
 * simply log the messages to stdout, a file, or retain them in a buffer for later access by a user
 * interface.
 * 
 * Reporter implementations should keep non-localized versions of their messages
 * 
 * Any messages which need to be displayed to the user are done through this class, and if the user
 * cancels the current function, this class is the one which registers the cancellation.
 * 
 * [issue: CS - I think it would be useful to say something about the IReporter life cycle.  
 * is an instance created when validation begins and then removed when validation ends?  Does the 
 * IReporter provide access to all messages ... or just the ones created by the associated validator? 
 * ] 
 */
public interface IReporter extends SeverityEnum {
	/**
	 * <p>
	 * Add a locale-independent validation message. It will be displayed later, with all of the
	 * other validation messages.
	 * </p>
	 * <p>
	 * The IValidator passed in is needed for incremental validation (when a message needs to be
	 * removed, one validator should not remove messages entered by another validator.) The
	 * validator is also queried for information about its resource bundle, to enable support for
	 * localization of messages in a client-server environment.
	 * </p>
	 * <p>
	 * Both parameters must not be null.
	 * </p>
	 * @param origin
	 *            The validator which is the source of the message.
	 * @param message
	 *            A message to be reported
	 * @exception MessageLimitException
	 *                is thrown when the total number of messages reported exceeds the maximum
	 *                allowed.
	 */
	public abstract void addMessage(IValidator origin, IMessage message) throws MessageLimitException;

	/**
	 * <p>
	 * Show a text representation of this message, formatted in the default Locale, to the user
	 * immediately. This message indicates which subtask is currently being processed. The message
	 * is not stored. 
	 * </p>
	 * <p>
	 * Both parameters must not be null. 
	 * </p>
	 * 
	 * @param IValidator
	 *            validator The validator issuing the subtask message.
	 * @param IMessage
	 *            message The message to be displayed to the user.
	 *            
	 * [issue : CS - I don't understand this one.  Perhaps an example would help?]           
	 */
	public abstract void displaySubtask(IValidator validator, IMessage message);

	/**
	 * @return the message access interface to this reporter, or null if message access is not
	 * supported.
	 * 
	 * [issue : CS - Seems like overkill to have an IMessageAccess class. Why not just getMessages() to get a list.]    
	 */
	public IMessageAccess getMessageAccess();

	/**
	 * <p>
	 * Return true if the user cancelled validation, and false otherwise. This method should be
	 * called by IValidators periodically, because no event is fired to notify IValidators that the
	 * user cancelled validation. If a validator does not check this method, a cancellation request
	 * is ignored.
	 * </p>
	 * 
	 * @return true if the user cancelled validation, and false otherwise.
	 * 
	 * [issue : CS - this seems like it should be a method on the IValidator and not the IReporter.
	 *  seems a bit strange that the reporter would be controlling the execution of validator.]    
	 */
	public abstract boolean isCancelled();

	/**
	 * <p>
	 * Remove all validation messages entered by the identified validator. This method is provided
	 * for incremental validation. 
	 * </p>
	 * <p>
	 * The IValidator parameter must not be null.
	 * </p>
	 * @param origin
	 * 			originator validator of the message.
	 *  
	 * 	[issue : CS - it would be very nice if we could give the impression that there's just 'one reporter per validator'. 
	 *  This would let us simplify many of our methods and enforce that an IValidator is only modifying its own reports.]  
	 */
	public abstract void removeAllMessages(IValidator origin);

	/**
	 * Remove all validation messages, entered by the identified validator, pertaining to the Object
	 * provided. This method is provided for incremental validation. <br>
	 * <br>
	 * If <code>object</code> is null, then this method should remove all messages owned by the
	 * validator. (i.e., the same behaviour as the removeAllMessages(IValidator) method.) <br>
	 * <br>
	 * <p>
	 * The IValidator parameter must not be null.
	 * </p>
	 * @param origin
	 * 			originator validator of the message.
	 * @param object
	 * 			Object to which the message belongs.
	 * 
	 * 	[issue : CS - it would help to say a bit more about the 'object'.  Is this object something that only
	 *  needs to exist for the life span of the validation or is this something that's longer lasting?  I assume
	 *  the object is transient and doesn't need to be persisted? If the object a significant part of the API why
	 *  is it not associated with an IMessage or passed in as a parameter to addMessage()?  It seems strange that the 
	 *  'object' only comes into play when removing messages.]  
 	 */
	public abstract void removeAllMessages(IValidator origin, Object object);

	/**
	 * To support removal of a subset of validation messages, an IValidator may assign group names
	 * to IMessages. An IMessage subset will be identified by the name of its group. This method
	 * will remove only the IMessage's that are in the group identified by groupName. <br>
	 * <br>
	 * <br>
	 * 
	 * The IValidator parameter must not be null. <br>
	 * <br>
	 * 
	 * If <code>object</code> is null, then this method should remove all messages owned by the
	 * validator. (i.e., the same behaviour as the removeAllMessages(IValidator) method.)
	 * 
	 * If groupName is null, that's the same as no group (i.e., the same behaviour as the
	 * <code>removeAllMessages(IValidator, Object)</code> method.) <br>
	 * 
	 * @param origin
	 * 			originator validator of the message.
	 * @param object
	 * 			Object to which the message belongs. 
	 * @param groupName
	 * 			name of the group to which the message belongs. 
	 */
	public void removeMessageSubset(IValidator validator, Object obj, String groupName);
}