/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.internal.common.emf.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.XMLSave;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.wst.common.emf.utilities.ExtendedEcoreUtil;
import org.eclipse.wst.common.emf.utilities.IDUtil;


public class CompatibilityXMIResourceImpl extends XMIResourceImpl implements CompatibilityXMIResource {
	protected static final String DEFAULT_ENCODING = "UTF-8"; //$NON-NLS-1$
	protected int format = FORMAT_EMF1;
	protected Map originalPackageURIs = new HashMap();
	private boolean preserveIDs = false;

	private static final String PLATFORM_PROTOCOL = "platform"; //$NON-NLS-1$
	private static final String PLATFORM_PLUGIN = "plugin"; //$NON-NLS-1$

	/**
	 * Constructor for MappableXMIResourceImpl.
	 */
	public CompatibilityXMIResourceImpl() {
		super();
		initDefaultSaveOptions();
	}

	public CompatibilityXMIResourceImpl(URI uri) {
		super(uri);
		initDefaultSaveOptions();
	}

	/**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLHelper()
	 */
	protected final XMLHelper createXMLHelper() {
		MappedXMIHelper helper = doCreateXMLHelper();
		helper.setPackageURIsToPrefixes(getPackageURIsToPrefixes());
		return helper;
	}

	protected MappedXMIHelper doCreateXMLHelper() {
		return new MappedXMIHelper(this, getPrefixToPackageURIs());
	}

	/**
	 * Subclasses should not need to override this method.
	 * 
	 * @see CompatibilityPackageMappingRegistry#getPrefixToPackageURIs()
	 */
	protected Map getPrefixToPackageURIs() {
		return CompatibilityPackageMappingRegistry.INSTANCE.getPrefixToPackageURIs();
	}

	/**
	 * Subclasses should not need to override this method.
	 * 
	 * @see CompatibilityPackageMappingRegistry#getPrefixToPackageURIs()
	 */
	protected Map getPackageURIsToPrefixes() {
		return CompatibilityPackageMappingRegistry.INSTANCE.getPackageURIsToPrefixes();
	}

	public void addOriginalPackageURI(String packageUri, String originalUri) {
		originalPackageURIs.put(packageUri, originalUri);
	}

	public int getFormat() {
		return format;
	}

	public void setFormat(int format) {
		if (!isPlatformPluginResourceURI())
			this.format = format;
	}

	private boolean isPlatformPluginResourceURI() {
		URI aURI = getURI();

		return aURI != null && PLATFORM_PROTOCOL.equals(uri.scheme()) && PLATFORM_PLUGIN.equals(uri.segment(0));
	}

	/**
	 * @see org.eclipse.emf.ecore.resource.Resource#getURIFragment(EObject)
	 */
	public String getURIFragment(EObject eObject) {
		if (usesDefaultFormat())
			return super.getURIFragment(eObject);
		return IDUtil.getOrAssignID(eObject, this);
	}

	public boolean usesDefaultFormat() {
		return format == CompatibilityXMIResource.FORMAT_EMF1;
	}

	/**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLSave()
	 */
	protected XMLSave createXMLSave() {
		if (usesDefaultFormat())
			return super.createXMLSave();
		return new CompatibilityXMISaveImpl(createXMLHelper());
	}

	/**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#doSave(OutputStream, Map)
	 */
	public void doSave(OutputStream outputStream, Map options) throws IOException {
		super.doSave(outputStream, options);
	}

	/**
	 * Method initDefaultOptions.
	 */
	protected void initDefaultSaveOptions() {
		if (defaultSaveOptions == null) {
			getDefaultSaveOptions();
		}
	}

	/**
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#getEObjectByID(String)
	 */
	protected EObject getEObjectByID(String id) {
		if (idToEObjectMap != null) {
			EObject eObject = (EObject) idToEObjectMap.get(id);
			if (eObject != null) {
				return eObject;
			}
		}
		return null;
	}

	/**
	 * Called when the object is unloaded. This implementation
	 * {@link InternalEObject#eSetProxyURI sets}the object to be a proxy and clears the
	 * {@link #eAdapters adapters}.
	 */
	protected void unloaded(InternalEObject internalEObject) {
		//overridden from the super class; call super.getURIFragment instead of the implementation
		//at this level, to avoid ID generation during unload
		//internalEObject.eSetProxyURI(uri.appendFragment(getURIFragment(internalEObject)));
		internalEObject.eSetProxyURI(uri.appendFragment(super.getURIFragment(internalEObject)));
		internalEObject.eAdapters().clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#doLoad(java.io.InputStream,
	 *      java.util.Map)
	 */
	public final void doLoad(InputStream inputStream, Map options) throws IOException {
		ExtendedEcoreUtil.addLoadingTag(this);
		try {
			basicDoLoad(inputStream, options);
		} finally {
			ExtendedEcoreUtil.removeLoadingTag(this);
		}
	}

	protected void basicDoLoad(InputStream inputStream, Map options) throws IOException {
		super.doLoad(inputStream, options);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#init()
	 */
	protected void init() {
		super.init();
		setEncoding(DEFAULT_ENCODING);
	}

	protected XMLLoad createXMLLoad() {
		return new CompatibilityXMILoadImpl(createXMLHelper());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.internal.common.emf.resource.CompatibilityXMIResource#removePreservingIds(org.eclipse.emf.ecore.EObject)
	 */
	public void removePreservingIds(EObject rootObject) {
		setPreserveIDs(true);
		getContents().remove(rootObject);
	}

	/**
	 * @return Returns the preserveIDs.
	 */
	public boolean isPreserveIDs() {
		return preserveIDs;
	}

	/**
	 * @param preserveIDs
	 *            The preserveIDs to set.
	 */
	public void setPreserveIDs(boolean preserveIDs) {
		this.preserveIDs = preserveIDs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#detachedHelper(org.eclipse.emf.ecore.EObject)
	 */
	protected void detachedHelper(EObject eObject) {
		if (modificationTrackingAdapter != null) {
			eObject.eAdapters().remove(modificationTrackingAdapter);
		}

		if (useUUIDs()) {
			DETACHED_EOBJECT_TO_ID_MAP.put(eObject, getID(eObject));
		}

		if (!isPreserveIDs() && idToEObjectMap != null && eObjectToIDMap != null) {
			idToEObjectMap.remove(eObjectToIDMap.remove(eObject));
		}
	}
}