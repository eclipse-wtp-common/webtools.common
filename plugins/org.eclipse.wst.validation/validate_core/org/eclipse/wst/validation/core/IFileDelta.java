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
package org.eclipse.wst.validation.core;


/**
 * This interface is used, for incremental validation, to encapsulate the change status of a
 * resource. IFileDelta contains the name of the file which has changed, and a flag which indicates
 * the type of change which has occurred.
 */
public interface IFileDelta {
	public static final int ADDED = 1; // the file has been added
	public static final int CHANGED = 2; // the file has been changed
	public static final int DELETED = 3; // the file has been deleted

	/**
	 * This method returns the flag which indicates the type of change which has occurred:
	 * IFileDelta.ADDED, IFileDelta.CHANGED, or IFileDelta.DELETED.
	 */
	public int getDeltaType();

	/**
	 * This method returns the name of the file which has changed. The value must not be null or the
	 * empty string ("").
	 */
	public String getFileName();
}