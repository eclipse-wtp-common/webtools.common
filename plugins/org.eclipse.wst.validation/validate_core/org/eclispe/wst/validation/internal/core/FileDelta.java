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
package org.eclispe.wst.validation.internal.core;

import org.eclipse.wst.validation.internal.provisional.core.IFileDelta;


/**
 * <p>
 * Default implementation of the IFileDelta
 * </p>
 * 
 * @see org.eclipse.wst.validation.internal.provisional.core.IFileDelta
 * 
 *  [issue: CS - is there a reason that we need to expose this impl class as an API?
 *   It would seem better to only expose IFileDelta.  I can't think of a reason where 
 *   a typical client would need to create one of these.]
 */
public class FileDelta implements IFileDelta {
	private int fileDelta = 0;
	private String fileName = null;

	/**
	 * <p>
	 * Creates a default instance of the FileDelta
	 * </p>
	 */
	public FileDelta() {
		super();
	}
	
	/**
	 * <p>
	 * Constructor is used to initialize the fields. 
	 * </p>
	 * 
	 * @param aFileName specifies the file name
	 * @param aFileDelta specifies the 
	 */
	public FileDelta(String aFileName, int aFileDelta) {
		fileName = aFileName;
		fileDelta = aFileDelta;
	}

	/**
	 * @see IFileDelta#getDeltaType()
	 */
	public int getDeltaType() {
		return fileDelta;
	}

	/**
	 * @see IFileDelta#getFileName()
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * <p>
	 * Set the flag which indicates the type of change that the file 
	 * has undergone.
	 * </p>
	 */
	public void setDeltaType(int deltaType) {
		fileDelta = deltaType;
	}

	/**
	 * <p>
	 * Set the name of the file which has changed.
	 * </p>
	 */
	public void setFileName(String aFileName) {
		fileName = aFileName;
	}
}