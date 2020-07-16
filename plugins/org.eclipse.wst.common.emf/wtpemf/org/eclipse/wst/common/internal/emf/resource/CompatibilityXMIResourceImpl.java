/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.XMLSave;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.wst.common.internal.emf.utilities.IDUtil;
import org.eclipse.wst.common.internal.emf.utilities.ResourceIsLoadingAdapter;
import org.eclipse.wst.common.internal.emf.utilities.ResourceIsLoadingAdapterFactory;


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
	@Override
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

	@Override
	public void addOriginalPackageURI(String packageUri, String originalUri) {
		originalPackageURIs.put(packageUri, originalUri);
	}

	@Override
	public int getFormat() {
		return format;
	}

	@Override
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
	@Override
	public String getURIFragment(EObject eObject) {
		if (usesDefaultFormat())
			return super.getURIFragment(eObject);
		return IDUtil.getOrAssignID(eObject, this);
	}

	@Override
	public boolean usesDefaultFormat() {
		return format == CompatibilityXMIResource.FORMAT_EMF1;
	}

	/**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLSave()
	 */
	@Override
	protected XMLSave createXMLSave() {
		if (usesDefaultFormat())
			return super.createXMLSave();
		return new CompatibilityXMISaveImpl(createXMLHelper());
	}

	/**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#doSave(OutputStream, Map)
	 */
	@Override
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
	@Override
	protected EObject getEObjectByID(String id) {
		if (idToEObjectMap != null) {
			EObject eObject = idToEObjectMap.get(id);
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
	@Override
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
	@Override
	public final void doLoad(InputStream inputStream, Map options) throws IOException {
		basicDoLoad(inputStream, options);
	}

	/**
	 * @deprecated Use {@link #doLoad(InputStream, Map)} instead.
	 */
	protected void basicDoLoad(InputStream inputStream, Map options) throws IOException {
		super.doLoad(inputStream, options);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#init()
	 */
	@Override
	protected void init() {
		super.init();
		setEncoding(DEFAULT_ENCODING);
	}

	@Override
	protected XMLLoad createXMLLoad() {
		return new CompatibilityXMILoadImpl(createXMLHelper());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emf.resource.CompatibilityXMIResource#removePreservingIds(org.eclipse.emf.ecore.EObject)
	 */
	@Override
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
	@Override
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
	
	@Override
	public void load(Map options) throws IOException {

        ResourceIsLoadingAdapter adapter = null;
        if (isLoaded) {
            adapter = ResourceIsLoadingAdapter.findAdapter(this);
            if (adapter != null) 
                adapter.waitForResourceToLoad();
            return;
        }
        synchronized (this) {            
            adapter = ResourceIsLoadingAdapter.findAdapter(this);
            if (adapter == null && !isLoaded) 
                addSynchronizationLoadingAdapter();
        }
        if(adapter != null)
            adapter.waitForResourceToLoad();
        else {
            try {
                super.load(options);
            } catch(IOException ioe) {
                removeLoadingSynchronizationAdapter();
                throw ioe;
            } catch(RuntimeException re) {
                removeLoadingSynchronizationAdapter();
                throw re;
            } catch(Error e) {
                removeLoadingSynchronizationAdapter();
                throw e;
            }
        }
    }
	public void loadExisting(Map options) throws IOException {

		
        ResourceIsLoadingAdapter adapter = null;
        if (isLoaded) {
            adapter = ResourceIsLoadingAdapter.findAdapter(this);
            if (adapter != null) 
                adapter.waitForResourceToLoad();
            return;
        }
        synchronized (this) {            
            adapter = ResourceIsLoadingAdapter.findAdapter(this);
            if (adapter == null && !isLoaded) 
                addSynchronizationLoadingAdapter();
        }
        if(adapter != null)
            adapter.waitForResourceToLoad();
        else {
            try {
                load((InputStream) null, options);
            } catch(IOException ioe) {
                removeLoadingSynchronizationAdapter();
                throw ioe;
            } catch(RuntimeException re) {
                removeLoadingSynchronizationAdapter();
                throw re;
            } catch(Error e) {
                removeLoadingSynchronizationAdapter();
                throw e;
            }
        }
    }
	
	  /**
     * 
     */
    protected void addSynchronizationLoadingAdapter() {
    	synchronized (eAdapters()) {
        if (ResourceIsLoadingAdapter.findAdapter(this) == null)
            eAdapters().add(ResourceIsLoadingAdapterFactory.INSTANCE.createResourceIsLoadingAdapter());
    	}
    }

    /**
     * 
     */
    protected void removeLoadingSynchronizationAdapter() {
        ResourceIsLoadingAdapter adapter = ResourceIsLoadingAdapter.findAdapter(this);
        if (adapter != null) {
            adapter.forceRelease();
            eAdapters().remove(adapter);
        }
    }

    /**
     * Case 1: LOAD RESOURCE FROM DISK this.isLoaded == false AND isLoaded ==
     * true (which means we entered the load() method, but have not completed
     * the load), and we're loading from a resource on disk, then we add the
     * adapter Case 2: RESOURCE CREATION (NOT A LOAD) Case 4: RESOURCE CREATION,
     * UNLOADED, NEW CONTENTS (NOT A LOAD) Resource is created but not from a
     * resource on disk, so contents is null AND not empty, so no adapter: THIS
     * IS NOT A LOAD Case 3: RESOURCE HAS BEEN UNLOADED, BEING RELOADED FROM
     * DISK Contents is NOT null, but it is Empty and the resource is being
     * loaded from disk. We must add the adapter.
     * 
     */
    public boolean isResourceBeingLoaded(boolean isLoaded) {
        return (!this.isLoaded && isLoaded) && (contents == null || contents.isEmpty());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#getContents()
     */
    @Override
	public EList getContents() {
        waitForResourceToLoadIfNecessary();
        return super.getContents();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.emf.ecore.resource.impl.ResourceImpl#isLoaded()
     */
    @Override
	public boolean isLoaded() {
        waitForResourceToLoadIfNecessary();
        return super.isLoaded();
    }

    /**
     * 
     */
    protected final void waitForResourceToLoadIfNecessary() {
        ResourceIsLoadingAdapter loadingAdapter = ResourceIsLoadingAdapter.findAdapter(this);
        if (loadingAdapter != null) loadingAdapter.waitForResourceToLoad();
    }



}
