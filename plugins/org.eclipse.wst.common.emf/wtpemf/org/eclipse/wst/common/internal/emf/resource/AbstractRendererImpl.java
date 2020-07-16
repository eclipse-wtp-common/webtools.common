/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;


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
	@Override
	public void setResource(TranslatorResource aResource) {
		resource = aResource;
	}

	/**
	 * @see com.ibm.etools.emf2xml.Renderer#getResource()
	 */
	@Override
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
	@Override
	public void accessForRead() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#accessForWrite()
	 */
	@Override
	public void accessForWrite() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#isModified()
	 */
	@Override
	public boolean isModified() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#isShared()
	 */
	@Override
	public boolean isShared() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#isSharedForWrite()
	 */
	@Override
	public boolean isSharedForWrite() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#preDelete()
	 */
	@Override
	public void preDelete() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#preUnload()
	 */
	@Override
	public void preUnload() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#releaseFromRead()
	 */
	@Override
	public void releaseFromRead() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#releaseFromWrite()
	 */
	@Override
	public void releaseFromWrite() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.Renderer#setBatchMode(boolean)
	 */
	@Override
	public void setBatchMode(boolean isBatch) {
	}

	@Override
	public boolean useStreamsForIO() {
		return true;
	}

	@Override
	public boolean isBatchMode() {
		return false;
	}

	public boolean isReverting() {
		return false;
	}
}
