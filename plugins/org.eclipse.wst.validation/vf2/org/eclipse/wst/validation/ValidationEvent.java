/*******************************************************************************
 * Copyright (c) 2008, 2012 IBM Corporation and others.
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
import org.eclipse.core.resources.IResourceDelta;

/**
 * An object that describes which object should be validated and what triggered its validation.
 * @author karasiuk
 *
 */

public final class ValidationEvent {
	
	private IResource 		_resource;
	private int				_kind;
	private IResourceDelta 	_dependsOn;
	
	/**
	 * Create an object that describes what should be validated.
	 * 
	 * @param resource
	 *            The resource to be validated.
	 * @param kind
	 *            The way the resource changed. It uses the same values as the
	 *            kind parameter in IResourceDelta.
	 * @param dependsOn
	 *            If the resource is being validated because one of it's
	 *            dependencies has changed, that change is described here. This
	 *            can be null.
	 */
	public ValidationEvent(IResource resource, int kind, IResourceDelta dependsOn){
		_resource = resource;
		_kind = kind;
		_dependsOn = dependsOn;
	}

	/**
	 * The resource to be validated.
	 */
	public IResource getResource() {
		return _resource;
	}

	/**
	 * The way the resource changed. It uses the same values as the kind
	 * parameter in IResourceDelta.
	 */
	public int getKind() {
		return _kind;
	}

	/**
	 * If the resource is being validated because one of it's dependencies has changed, that change is described here.
	 * This method will return null when the trigger is not because of a dependency change.
	 */
	public IResourceDelta getDependsOn() {
		return _dependsOn;
	}
}
