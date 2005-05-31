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
package org.eclipse.wst.common.componentcore;

import org.eclipse.emf.common.util.URI;

/**
 * <p>
 * Thrown whenever a URI cannot be appropriately resolved.
 * </p>
 *  
 * @since 1.0
 */
public class UnresolveableURIException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnresolveableURIException(URI anUnresolveableURI) {
		super("Could not resolve: " + anUnresolveableURI);
	}
}
