/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.validateedit;


import java.util.List;

public interface ResourceStateInputProvider {
	/**
	 * Return true if any of the controlled resources or files has been modified.
	 * 
	 * @return boolean
	 */
	boolean isDirty();

	/**
	 * Return a <code>List</code> of the MOF Resources that are being managed. Synchronization
	 * checking will only work if you are using the emf.workbench plugin apis for loading resources.
	 * This will ensure that you get an instance of a <code>ReferencedResource</code>. This
	 * resource type is capable of caching its last known synchronization stamp that may be used to
	 * test if the resource is consitent with the underlying IFile.
	 * 
	 * @return List
	 */
	List getResources();

	/**
	 * Return a <code>List</code> of IFiles that are not MOF Resources that are also being
	 * modified.
	 * 
	 * @return List
	 */
	List getNonResourceFiles();

	/**
	 * Return a subset of the List from getNonResourceFiles() that are inconsistent with the
	 * underlying java.io.File.
	 * 
	 * @return List
	 * @see ResourceStateInputProvider#getNonResourceFiles()
	 */
	List getNonResourceInconsistentFiles();

	/**
	 * It is the responsibility of the provider to cache the synchronization stamp for the List of
	 * <code>roNonResourceFiles</code>. This stamp will be used to determine the inconsistent
	 * files. This is only necessary of IFiles that are not MOF resources.
	 * 
	 * @param roNonResourceFiles
	 * @see ResourceStateInputProvider#getNonResourceInconsistentFiles()
	 */
	void cacheNonResourceValidateState(List roNonResourceFiles);
}

