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
package org.eclipse.wst.common.componentcore.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * A deployable file
 */
public class ExportableFile extends ExportableResource implements IExportableFile {
	private InputStream stream;
	private IFile file;
	private File file2;
	private String name;
	private IPath path;
	private long stamp = -1;

	/**
	 * Creates a workspace module file with the current modification stamp.
	 * 
	 * @param file a file in the workspace
	 * @param name a name
	 * @param path the path to the file
	 */
	public ExportableFile(IFile file, String name, IPath path) {
		if (name == null)
			throw new IllegalArgumentException();
		this.file = file;
		this.name = name;
		this.path = path;
		if (file != null)
			stamp = file.getModificationStamp() + file.getLocalTimeStamp();
	}

	/**
	 * Creates an external module file 
	 * 
	 * @param file
	 * @param name
	 * @param path
	 */
	public ExportableFile(File file, String name, IPath path) {
		if (name == null)
			throw new IllegalArgumentException();
		this.file2 = file;
		this.name = name;
		this.path = path;
		if (file2 != null)
			stamp = file2.lastModified();
	}

	/**
	 * Creates an unknown module file with the current modification stamp.
	 * 
	 * @param file
	 * @param name
	 * @param path
	 */
	public ExportableFile(InputStream is, String name, IPath path) {
		if (name == null)
			throw new IllegalArgumentException();
		this.stream = is;
		this.name = name;
		this.path = path;
		if (file2 != null)
			stamp = file2.lastModified();
	}

	/**
	 * Creates a module file with a specific modification stamp and no
	 * file reference.
	 * 
	 * @param name
	 * @param path
	 * @param stamp
	 */
	public ExportableFile(String name, IPath path, long stamp) {
		if (name == null)
			throw new IllegalArgumentException();
		this.name = name;
		this.path = path;
		this.stamp = stamp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleFile#getModificationStamp()
	 */
	public long getModificationStamp() {
		return stamp;
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

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (!(obj instanceof ExportableFile))
			return false;
		
		ExportableFile mf = (ExportableFile) obj;
		if (!name.equals(mf.getName()))
			return false;
		if (!path.equals(mf.getModuleRelativePath()))
			return false;
		return true;
	}

	public int hashCode() {
		return name.hashCode() * 37 + path.hashCode();
	}

	public Object getAdapter(Class cl) {
		if (IFile.class.equals(cl) || IResource.class.equals(cl))
			return file;
		else if (File.class.equals(cl)) {
			if( file2 != null )
				return file2;
			if( file != null )
				return file.getLocation().toFile();
		}
		else if( InputStream.class.equals(cl)) {
			try {
				if( stream != null )
					return stream;
				if( file2 != null && file2.exists()) 
					return new FileInputStream(file2);
				if( file != null && file.exists()) 
					return new FileInputStream(file.getLocation().toFile());
			} catch( IOException ioe) {
				// Do Nothing
			}
		}
		return null;
	}

	public String toString() {
		return "DeployableFile [" + name + ", " + path + ", " + stamp + "]";
	}
}