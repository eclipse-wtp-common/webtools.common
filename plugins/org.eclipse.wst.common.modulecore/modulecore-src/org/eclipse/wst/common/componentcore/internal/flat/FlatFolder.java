/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.flat;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * An implementation of {@link IFlatFolder} for physical folders on disk or in the
 * workspace.
 */
public class FlatFolder extends FlatResource implements IFlatFolder {
	private static final FlatResource[] EMPTY_RESOURCE_ARRAY = new FlatResource[0];

	private IContainer container;
	private String name;
	private IPath path;
	private IFlatResource[] members;

	/**
	 * Creates a module folder.
	 * 
	 * @param container the container, or <code>null</code> for unknown container
	 * @param name a name
	 * @param path the module relative path to the folder
	 */
	public FlatFolder(IContainer container, String name, IPath path) {
		if (name == null)
			throw new IllegalArgumentException();
		this.container = container;
		this.name = name;
		this.path = path;
	}

	/**
	 * Sets the members (contents) of this folder.
	 * 
	 * @param members the members
	 */
	public void setMembers(IFlatResource[] members) {
		this.members = members;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleResource#getModuleRelativePath()
	 */
	public IPath getModuleRelativePath() {
		return path;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleResource#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleFolder#members()
	 */
	public IFlatResource[] members() {
		if (members == null)
			return EMPTY_RESOURCE_ARRAY;
		return members;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (!(obj instanceof FlatFolder))
			return false;
		
		FlatFolder mf = (FlatFolder) obj;
		if (!name.equals(mf.name))
			return false;
		if (!path.equals(mf.path))
			return false;
		return true;
	}

	public int hashCode() {
		return name.hashCode() * 37 + path.hashCode();
	}

	public Object getAdapter(Class cl) {
		if (IContainer.class.equals(cl) || IFolder.class.equals(cl) || IResource.class.equals(cl))
			return container;
		if( File.class.equals(cl))
			return container.getLocation().toFile();
		return null;
	}

	public String toString() {
		return "DeployableFolder [" + name + ", " + path + "]";
	}
}