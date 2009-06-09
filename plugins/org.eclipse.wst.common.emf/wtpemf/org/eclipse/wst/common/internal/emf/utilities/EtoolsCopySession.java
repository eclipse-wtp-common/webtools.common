/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;

/**
 * Insert the type's description here. Creation date: (05/04/01 11:25:59 PM)
 * 
 * @author: Administrator
 */
public class EtoolsCopySession extends EtoolsCopyUtility {
	public static final int RESOURCE_COPIED = 500;
	private EtoolsCopyUtility parentCopyUtility;
	private XMLResource currentResource;
	private XMLResource currentCopyResource;

	/**
	 * EtoolsCopySession constructor comment.
	 */
	public EtoolsCopySession(EtoolsCopyUtility aCopyUtility) {
		parentCopyUtility = aCopyUtility;
		setPreserveIds(aCopyUtility.preserveIds());
		setCopyAdapters(aCopyUtility.isCopyAdapters());
	}

	/**
	 * Copy all Resources and RefObjects within <code>aGroup</code> and add them to
	 * <code>aGroup</code>. Non composite references will be deferred until all objects are
	 * copied from <code>aGroup</code>.
	 * 
	 * Copy Resources first and then copy RefObjects.
	 */
	@Override
	public void copy(CopyGroup aGroup) {
		if (aGroup != null) {
			copyResources(aGroup);
			copyRefObjects(aGroup);
			executeDeferredCopyActions();
			aGroup.postCopy(this);
			notifyNewResourcesCopied(aGroup);
		}
	}

	/**
	 * This method should be used if you are only going to copy <code>aRefObject</code> in this
	 * copy execution.
	 */
	@Override
	public EObject copy(EObject aRefObject, String idSuffix) {
		EObject copied = containmentCopy(aRefObject, idSuffix);
		executeDeferredCopyActions();
		return copied;
	}

	/**
	 * This method should be used if you are only going to copy <code>aRefObject</code> in this
	 * copy execution. This method only copies <code>aRefObject</code> attributes.
	 */
	@Override
	public EObject copyObject(EObject aRefObject, String idSuffix) {
		EObject copied = containmentCopyObject(aRefObject, idSuffix);
		executeDeferredCopyActions();
		return copied;
	}

	/**
	 * This method should be used if you are only going to copy <code>aResource</code> in this
	 * copy execution. The copied Resource will have a URI equal to <code>newUri</code>.
	 */
	@Override
	public Resource copy(Resource aResource, String newUri) {
		Resource copied = containmentCopy(aResource, newUri);
		executeDeferredCopyActions();
		copyModificationFlag(aResource, copied);
		notifyNewResourceCopied(copied);
		return copied;
	}

	protected void notifyNewResourcesCopied(CopyGroup group) {
		List copied = group.getCopiedResources();
		for (int i = 0; i < copied.size(); i++) {
			notifyNewResourceCopied((Resource) copied.get(i));
		}
	}

	protected void notifyNewResourceCopied(Resource copied) {
		copied.eNotify(new NotificationImpl(RESOURCE_COPIED, null, null));
	}

	/**
	 * @see com.ibm.etools.emf.ecore.utilities.copy.EtoolsCopyUtility#primCopyObject(EObject,
	 *      String)
	 */
	@Override
	protected EObject primCopyObject(EObject aRefObject, String idSuffix) {
		EObject copy = super.primCopyObject(aRefObject, idSuffix);
		copyIdIfNecessary(aRefObject, copy, idSuffix);
		return copy;
	}

	protected void copyIdIfNecessary(EObject obj, EObject copy, String idSuffix) {
		if ((idSuffix == null && !preserveIds) || currentResource == null)
			return;
		String id = currentResource.getID(obj);
		if (id == null)
			return;
		id = idSuffix == null ? id : id + idSuffix;
		currentCopyResource.setID(copy, id);
	}


	/**
	 * Set delivery back on for all the cached objects and push them to the parentCopyUtility.
	 */
	public void flush() {
		Iterator it = getCopiedObjects().entrySet().iterator();
		Map parentCopies = getParentCopyUtility().getCopiedObjects();
		Map.Entry entry;
		EObject copy;
		while (it.hasNext()) {
			entry = (Map.Entry) it.next();
			copy = (EObject) entry.getValue();
			copy.eSetDeliver(true);
			parentCopies.put(entry.getKey(), copy);
		}
		copiedObjects = null;
	}

	/**
	 * Return a cached copy.
	 */
	@Override
	public EObject getCopy(EObject anObject) {
		EObject copied = super.getCopy(anObject);
		if (copied == null)
			copied = getParentCopyUtility().getCopy(anObject);
		return copied;
	}

	/**
	 * Insert the method's description here. Creation date: (05/04/01 11:28:07 PM)
	 * 
	 * @return com.ibm.etools.emf.ecore.utilities.copy.EtoolsCopyUtility
	 */
	protected EtoolsCopyUtility getParentCopyUtility() {
		return parentCopyUtility;
	}

	/**
	 * Return an instance of EObject that is the same type as <code>aRefObject</code>.
	 */
	@Override
	public EObject newInstance(EObject aRefObject) {
		EObject newType = super.newInstance(aRefObject);
		newType.eSetDeliver(false);
		return newType;
	}

	/**
	 * @see com.ibm.etools.emf.ecore.utilities.copy.EtoolsCopyUtility#newInstance(Resource, String)
	 */
	@Override
	public Resource newInstance(Resource aResource, String newUri) {
		Resource copy = super.newInstance(aResource, newUri);
		if (aResource instanceof XMLResource) {
			currentResource = (XMLResource) aResource;
			currentCopyResource = (XMLResource) copy;
		} else {
			currentResource = null;
			currentCopyResource = null;
		}
		return copy;
	}

}

