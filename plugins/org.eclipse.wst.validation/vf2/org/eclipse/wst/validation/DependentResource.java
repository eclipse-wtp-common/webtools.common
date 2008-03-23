/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;

import org.eclipse.core.resources.IResource;

/**
 * A resource that is dependent on another resource.
 * <p>
 * This is returned by the IDependencyIndex.
 * @author karasiuk
 *
 */
public class DependentResource {
	private IResource 	_resource;
	private Validator	_validator;
	
	public DependentResource(IResource resource, Validator validator){
		_resource = resource;
		_validator = validator;
	}
	
	public IResource getResource() {
		return _resource;
	}
	public Validator getValidator() {
		return _validator;
	}
}
