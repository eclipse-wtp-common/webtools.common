/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.operations;

import org.eclipse.core.resources.IResource;
import org.eclispe.wst.validation.internal.core.FileDelta;
import org.eclispe.wst.validation.internal.core.IFileDelta;


/**
 * Eclipse-specific implementation of FileDelta which also caches the IResource which is associated
 * with the FileDelta.
 */
public class WorkbenchFileDelta extends FileDelta {
	private IResource _resource = null;
	private Object _changedResource = null;

	private static final String FILE_NAME = "FILE NAME:"; //$NON-NLS-1$
	private static final String DELTA = "  DELTA: "; //$NON-NLS-1$
	private static final String RESOURCE = "  RESOURCE: "; //$NON-NLS-1$
	private static final String OBJECT = "  OBJECT: "; //$NON-NLS-1$

	public WorkbenchFileDelta(String aFileName, int aFileDelta, IResource resource) {
		super(aFileName, aFileDelta);
		setResource(resource);
		setObject(resource);
	}

	public WorkbenchFileDelta(Object changedResource) {
		super(null, IFileDelta.CHANGED);
		setObject(changedResource);
	}

	public IResource getResource() {
		return _resource;
	}

	public void setResource(IResource resource) {
		_resource = resource;
	}

	/**
	 * If the changed resource is not an IResource (e.g., a RefObject), then the getFileName method
	 * will return null, getResource will return null, and this method will return the object. If
	 * the changedResource is an IResource, then both this method and the getResource method will
	 * return the resource.
	 */
	public Object getObject() {
		return _changedResource;
	}

	/**
	 * @see getObject()
	 */
	public void setObject(Object o) {
		_changedResource = o;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer(FILE_NAME);
		buffer.append(String.valueOf(getFileName()));
		buffer.append(DELTA);
		buffer.append(getDeltaType());
		buffer.append(RESOURCE);
		buffer.append(String.valueOf(getResource()));
		buffer.append(OBJECT);
		buffer.append(String.valueOf(getObject()));
		return buffer.toString();
	}
}