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
 * Default implementation of IFileDelta.
 */
public class FileDelta implements IFileDelta {
	private int fileDelta = 0;
	private String fileName = null;

	public FileDelta() {
		super();
	}

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
	 * Set the flag which indicates the type of change that the file has undergone.
	 */
	public void setDeltaType(int deltaType) {
		fileDelta = deltaType;
	}

	/**
	 * Set the name of the file which has changed.
	 */
	public void setFileName(String aFileName) {
		fileName = aFileName;
	}
}