/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
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
package org.eclipse.wst.common.frameworks.internal.enablement;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface IEnablementIdentifierListener {
	/**
	 * Notifies that one or more properties of an instance of <code>IIdentifier</code> have
	 * changed. Specific details are described in the <code>IdentifierEvent</code>.
	 * 
	 * @param identifierEvent
	 *            the identifier event. Guaranteed not to be <code>null</code>.
	 */
	void identifierChanged(EnablementIdentifierEvent identifierEvent);
}
