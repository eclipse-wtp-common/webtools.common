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


/**
 * This exception is thrown by
 * 
 * @see IReporter#addMessage(IValidator, IMessage) method if no more messages can be reported
 *      because the maximum number of messages has been reported.
 * [issue: LM - Going along with my comment on the addMessage method in IReporter I 
 *  think this class should be internal to the validation framework.
 *  I don't think there's a need for this class to be exposed to clients. If too many
 *  messages are added the framework should handle this without notifying the client
 *  validator. ]
 */
public class MessageLimitException extends RuntimeException {
	private final static long serialVersionUID = -7034897190745766940L;
}