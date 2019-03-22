/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
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
 *
 * @noextend
 * @see IDependencyIndex
 * @author karasiuk
 *
 */
public class DependentResource {
	private IResource 	_resource;
	private Validator	_validator;
	
	/**
	 * @noreference
	 */
	public DependentResource(IResource resource, Validator validator){
		_resource = resource;
		_validator = validator;
	}
	
	/**
	 * Answer the resource that is depended on.
	 */
	public IResource getResource() {
		return _resource;
	}
	
	/**
	 * Answer the validator that asserted the dependency.
	 */
	public Validator getValidator() {
		return _validator;
	}
}
