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
package org.eclispe.wst.common.framework.enablement;

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