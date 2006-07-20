/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;



import org.eclipse.emf.ecore.resource.Resource;

public interface ReferencedResource extends Resource, CompatibilityXMIResource {
	//TODO - rename packaged

	public static final String DELETED_ERROR_MSG = "This resource has been deleted and can no longer be used."; //$NON-NLS-1$

	public static final int RESOURCE_WAS_SAVED = 601;
	
	/**
	 * A flag used to indicate a ReferencedResource is about
	 * to save.
	 */
	public static final int RESOURCE_ABOUT_TO_SAVE = 602;
	
	/**
	 * A flag used to indicate a ReferencedResource has failed
	 * to save.
	 */
	public static final int RESOURCE_SAVE_FAILED = 603;

	/**
	 * Access this resource for read only. This call increments the use read count of this resource.
	 * Clients should call this method before they use the resource. They should call
	 * releaseFromRead() after they are done modifying this resource.
	 */
	void accessForRead();

	/**
	 * Access this resource for write only. This call increments the use write count of this
	 * resource. Clients should call this method before they modify the resource. They should call
	 * releaseFromWrite() after they are done modifying this resource.
	 */
	void accessForWrite();

	/**
	 * Return true if this resource has just been loaded and not yet accessed for read or write.
	 */
	boolean isNew();

	/**
	 * Return true if this resource does not have any write references.
	 */
	boolean isReadOnly();

	/**
	 * Return true if this resource is shared for read or write by more than one accessor.
	 */
	boolean isShared();

	/**
	 * Return true if this resource is shared for write by more than one accessor.
	 */
	boolean isSharedForWrite();

	/**
	 * Release read the access to this resource. This call decrements the use count of this resource
	 * and will remove the resource from its resource set if the use count goes to 0. Clients should
	 * call this method when they are done accessing the resource and only after they have called
	 * accessForRead() to obtain access.
	 */
	void releaseFromRead();

	/**
	 * Release write the access to this resource. This call decrements the write count of this
	 * resource and will remove the resource from its resource set if the use count goes to 0.
	 * Clients should call this method when they are done accessing the resource and only after they
	 * have called accessForWrite() to obtain access.
	 */
	void releaseFromWrite();

	/**
	 * Saves this resource only if the write count is equal to 1.
	 */
	void saveIfNecessary() throws Exception;

	/**
	 * Return true if this resource is dirty and is not shared for write.
	 */
	boolean needsToSave();



	/**
	 * Set whether we should allow a refresh to take place even when this resource is dirty.
	 * 
	 * @param b
	 */
	void setForceRefresh(boolean b);

	/**
	 * Return whether we should allow a refresh to take place even when this resource is dirty.
	 */
	boolean shouldForceRefresh();

	boolean wasReverted();

	/**
	 * Returns the number of open read accesses on this resource
	 * 
	 * @return
	 */
	int getReadCount();

	/**
	 * Returns the number of open write accesses on this resource
	 * 
	 * @return
	 */
	int getWriteCount();

	/**
	 * If the resource is no longer being accessed, then remove it from the resource set.
	 */
	void unloadIfNecessary();
}