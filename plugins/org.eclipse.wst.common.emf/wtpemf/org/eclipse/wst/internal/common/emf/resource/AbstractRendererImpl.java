/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.internal.common.emf.resource;


public abstract class AbstractRendererImpl implements Renderer {


	protected TranslatorResource resource;
	private int versionId;
	private boolean validating = true;

	/**
	 * Constructor for AbstractRendererImpl.
	 */
	public AbstractRendererImpl() {
		super();
	}

	/**
	 * @see com.ibm.etools.emf2xml.Renderer#setResource(TranslatorResource)
	 */
	public void setResource(TranslatorResource aResource) {
		resource = aResource;
	}

	/**
	 * @see com.ibm.etools.emf2xml.Renderer#getResource()
	 */
	public TranslatorResource getResource() {
		return resource;
	}

	public int getVersionId() {
		return this.versionId;
	}

	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}

	/**
	 * @return
	 */
	public boolean isValidating() {
		return validating;
	}

	/**
	 * @param b
	 */
	public void setValidating(boolean b) {
		validating = b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#accessForRead()
	 */
	public void accessForRead() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#accessForWrite()
	 */
	public void accessForWrite() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#isModified()
	 */
	public boolean isModified() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#isShared()
	 */
	public boolean isShared() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#isSharedForWrite()
	 */
	public boolean isSharedForWrite() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#preDelete()
	 */
	public void preDelete() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#preUnload()
	 */
	public void preUnload() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#releaseFromRead()
	 */
	public void releaseFromRead() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#releaseFromWrite()
	 */
	public void releaseFromWrite() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#setBatchMode(boolean)
	 */
	public void setBatchMode(boolean isBatch) {
	}

	public boolean useStreamsForIO() {
		return true;
	}
}