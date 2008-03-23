/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IResource;

/**
 * This internal error is used to signal that a resource is now unavailable.
 * <p>
 * This error is used to "exit" the validation framework. 
 * <p>
 * This is an error rather than a runtime exception, because some parts of Eclipse like to
 * trap RuntimeExceptions and log them.
 * @author karasiuk
 *
 */
public class ResourceUnavailableError extends Error {

	private static final long serialVersionUID = 200801290853L;
	
	private IResource _resource;
	
	public ResourceUnavailableError(IResource resource){
		super();
		_resource = resource;
	}

	public IResource getResource() {
		return _resource;
	}

}
