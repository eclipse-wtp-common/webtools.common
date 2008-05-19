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
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
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
