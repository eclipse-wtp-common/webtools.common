/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Feb 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclispe.wst.common.frameworks.internal.enablement;

import java.util.Set;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface IEnablementIdentifier {


	/**
	 * Registers an instance of <code>IIdentifierListener</code> to listen for changes to
	 * properties of this instance.
	 * 
	 * @param identifierListener
	 *            the instance to register. Must not be <code>null</code>. If an attempt is made
	 *            to register an instance which is already registered with this instance, no
	 *            operation is performed.
	 */
	void addIdentifierListener(IEnablementIdentifierListener identifierListener);

	/**
	 * Returns the set of function group ids that this instance matches.
	 * <p>
	 * Notification is sent to all registered listeners if this property changes.
	 * </p>
	 * 
	 * @return the set of activity ids that this instance matches. This set may be empty, but is
	 *         guaranteed not to be <code>null</code>. If this set is not empty, it is guaranteed
	 *         to only contain instances of <code>String</code>.
	 */
	Set getFunctionGroupIds();

	/**
	 * Returns the identifier of this instance.
	 * 
	 * @return the identifier of this instance. Guaranteed not to be <code>null</code>.
	 */
	String getId();

	/**
	 * Returns whether or not this instance is enabled.
	 * <p>
	 * Notification is sent to all registered listeners if this property changes.
	 * </p>
	 * 
	 * @return true, iff this instance is enabled.
	 */
	boolean isEnabled();

	/**
	 * Unregisters an instance of <code>IEnablementIdentifierListener</code> listening for changes
	 * to properties of this instance.
	 * 
	 * @param identifierListener
	 *            the instance to unregister. Must not be <code>null</code>. If an attempt is
	 *            made to unregister an instance which is not already registered with this instance,
	 *            no operation is performed.
	 */
	void removeIdentifierListener(IEnablementIdentifierListener identifierListener);


}