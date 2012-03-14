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

/**
 * This exception is thrown by
 * 
 * @see IReporter#addMessage(IValidator, IMessage) method if no more messages can be reported
 *      because the maximum number of messages has been reported.
 *  @deprecated This class is not longer used by the framework. The framework is
 *  not going to limit the number of messages displayed by a validator.
 */
public class MessageLimitException extends RuntimeException {
	private final static long serialVersionUID = -7034897190745766940L;
}
