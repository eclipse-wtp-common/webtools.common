/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internal.common.emf.utilities;


import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;


/**
 * @deprecated This class will be deleted. If you still need to use this class, please contact the
 *             WCCM team. A class which can be used in conjunction with Encoder/Decoders to save
 *             resources if they are made dirty by automatic encoding support. Usage Example:
 * 
 * <pre>
 * 
 * 
 * // Use the standard WebSphere password value encoder/decoder.
 * EncoderDecoderRegistry.getDefaultRegistry().setDefaultEncoderDecoder(new com.ibm.ejs.security.util.WASEncoderDecoder());
 * // Begin tracking changes...
 * WriteBackHelper.begin();
 * // Load a resource which may have un-encoded values...
 * // Note: The WCCM will attempt to detect un-encoded values.  If unencoded values
 * // are found, the value will be encoded, and the resource will be added to the
 * // WriteBackHelper.
 * Resource res = resourceSet.load(&quot;myResource&quot;);
 * // Ensure that any changes due to encoding are written back out.
 * WriteBackHelper.end();
 * </pre>
 */
public class WriteBackHelper {
	private Set dirtyObjects = new HashSet();
	private boolean trackingChanges = false;
	private static WriteBackHelper _instance;
	static {
		//Deprecated class
		Revisit.deleteMe();
	};

	/**
	 * Private constructor ensures proper usage through singleton.
	 */
	private WriteBackHelper() {
		super();
	}

	/**
	 * Adds a resource which is dirty, and needs to be saved.
	 */
	public void addDirtyObject(EObject dirtyObject) {
		dirtyObjects.add(dirtyObject);
	}

	/**
	 * Begin collecting objects which have changed.
	 */
	public void begin() {
		trackingChanges = true;
	}

	/**
	 * Attempts to save all dirty resources (if possible), then marks the resources as non-dirty.
	 */
	public void end() {
		saveDirtyResources();
		reset();
		trackingChanges = false;
	}

	/**
	 * Returns true if changes to mof objects are currently being tracked.
	 */
	public boolean isActive() {
		return trackingChanges;
	}

	/**
	 * Clears the list of dirty resources.
	 */
	protected void reset() {
		dirtyObjects.clear();
	}

	/**
	 * Attempts to save all dirty resources (if possible), then marks the resources as non-dirty.
	 */
	protected void saveDirtyResources() {
		Set dirtyResources = new HashSet();
		Iterator dirtyObjIter = dirtyObjects.iterator();
		while (dirtyObjIter.hasNext()) {
			EObject dirtyObject = (EObject) dirtyObjIter.next();
			if (dirtyObject.eResource() != null && !dirtyResources.contains(dirtyObject)) {
				dirtyResources.add(dirtyObject.eResource());
			}
		}
		Iterator dirtyIter = dirtyResources.iterator();
		while (dirtyIter.hasNext()) {
			Resource dirtyResource = (Resource) dirtyIter.next();
			try {
				dirtyResource.save(Collections.EMPTY_MAP);
			} catch (Exception e) {
				warn(dirtyResource, e);
			}
		}
	}

	/**
	 * Adds a resource which is dirty, and needs to be saved.
	 */
	public static WriteBackHelper singleton() {
		if (_instance == null) {
			_instance = new WriteBackHelper();
		}
		return _instance;
	}

	/**
	 * Warn the user of problems during save.
	 */
	protected void warn(Resource res, Exception e) {
		System.err.println(WFTUtilsResourceHandler.getString("Warning__Could_not_write_b_WARN_", new Object[]{res.getURI()})); //$NON-NLS-1$
	}
}