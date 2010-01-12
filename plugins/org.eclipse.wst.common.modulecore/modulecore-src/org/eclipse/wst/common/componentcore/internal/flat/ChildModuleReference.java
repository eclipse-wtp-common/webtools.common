/*******************************************************************************
 * Copyright (c) 2009 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.flat;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

/**
 * Clients of this class will need to pay attention to 
 * if this module is binary or not. If it's binary, you 
 * should get the file, and copy it directly to the URI provided.
 * 
 * If it's not binary, you should use the provided uri
 * as a base from which you can append it's member resources
 *  
 * String parentRelative = module.getRelativeURI(); //  path/to/childmod.jar
 * String uri = parentLoc.append(parentRelative);
 * if( module.isBinary()) {
 *    File f = module.getFile();
 *    // copy file f to uri;
 * } else {
 *    ExportModel em = new ExportModel(module.getComponent());
 *    ExportableResource[] members = em.fetchResources();
 *    String uri1 = uri.append(members[1].getModuleRelativePath());
 *    String uri2 = ....
 * }
 * @author rob
 *
 */
public class ChildModuleReference implements IChildModuleReference {
	private File file;
	private IVirtualComponent component;
	private IVirtualReference reference;
	private IPath uri;
	public ChildModuleReference(IFlatFile f) {
		this.file = f == null ? null : (File)f.getAdapter(File.class);
		if( f != null && file != null ) {
			this.uri = f.getModuleRelativePath().append(f.getName());
		}
	}
	
	public ChildModuleReference(IVirtualReference reference, IPath root) {
		this.reference = reference;
		this.component = reference.getReferencedComponent();
		if( component.isBinary() ) {
			File f = (File)component.getAdapter(File.class);
			if( f.exists() && f.isFile()) {
				this.file = f;
			}
		}
		this.uri = root.append(reference.getRuntimePath()).append(reference.getArchiveName());
	}
	
	/**
	 * Will return a file if this can be tracked to one
	 * @return
	 */
	public File getFile() {
		return file;
	}
	
	public IVirtualReference getReference() {
		return reference;
	}
	
	/**
	 * Return the component if it exists
	 * @return
	 */
	public IVirtualComponent getComponent() {
		return component;
	}
	
	/**
	 * If this is a single file, return true.
	 * If a more complex component, return false
	 * @return
	 */
	public boolean isBinary() {
		return component == null || component.isBinary();
	}
	
	/**
	 * Get the relative URI to it's direct parent, 
	 * including path + filename + extension
	 * @return
	 */
	public IPath getRelativeURI() {
		return uri;
	}
}
