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
package org.eclipse.wst.common.emf.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.wst.common.internal.emf.utilities.Association;

/**
 * This class is used to copy a group of RefObjects and/or Resources. This group will ensure that
 * all non-composite relationships are deferred until all Resources and RefObjects are copied. This
 * allows you to make copies of a group of objects that may have non-composite relationships and
 * ensure that these relationships are pointing to the copied object if it is part of the group
 * (either directly or through a containment relationship). Creation date: (12/17/2000 1:21:17 PM)
 * 
 * @author: Administrator
 */
public class CopyGroup {
	protected String defaultIdSuffix;
	protected ResourceSet copyContext;
	protected List resources;
	protected List refObjects;
	protected List copiedResources;
	protected List copiedRefObjects;
	protected boolean preserveIds = false;

	/**
	 * CopyGroup constructor comment.
	 */
	public CopyGroup() {
		super();
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 1:28:16 PM)
	 * 
	 * @param aRefObject
	 *            org.eclipse.emf.ecore.EObject
	 */
	public boolean add(EObject aRefObject) {
		return add(aRefObject, null);
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 1:28:16 PM)
	 * 
	 * @param aRefObject
	 *            org.eclipse.emf.ecore.EObject
	 */
	public boolean add(EObject aRefObject, String idSuffix) {
		if (aRefObject != null && !containsRefObject(aRefObject)) {
			getRefObjects().add(createAssociation(aRefObject, idSuffix));
			return true;
		}
		return false;
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 1:27:32 PM)
	 * 
	 * @param aResources
	 *            org.eclipse.emf.ecore.resource.Resource
	 */
	public void add(Resource aResource) {
		add(aResource, null);
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 1:27:32 PM)
	 * 
	 * @param aResources
	 *            org.eclipse.emf.ecore.resource.Resource
	 */
	public void add(Resource aResource, String newUri) {
		if (aResource != null)
			getResources().add(createAssociation(aResource, newUri));
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 1:28:16 PM)
	 * 
	 * @param aRefObject
	 *            org.eclipse.emf.ecore.EObject
	 */
	public void addCopied(EObject aRefObject) {
		if (aRefObject != null)
			getCopiedRefObjects().add(aRefObject);
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 1:27:32 PM)
	 * 
	 * @param aResources
	 *            org.eclipse.emf.ecore.resource.Resource
	 */
	public void addCopied(Resource aResource) {
		if (aResource != null) {
			getCopiedResources().add(aResource);
			if (getCopyContext() != null)
				getCopyContext().getResources().add(aResource);
		}
	}

	protected boolean contains(List associations, Object anObject) {
		if (anObject == null)
			return false;
		int size = associations.size();
		Association assoc;
		for (int i = 0; i < size; i++) {
			assoc = (Association) associations.get(i);
			if (assoc.getKey() == anObject)
				return true;
		}
		return false;
	}

	/**
	 * Return true if
	 * 
	 * @aRefObject has been added to this group. Creation date: (12/17/2000 1:28:16 PM)
	 * @param aRefObject
	 *            org.eclipse.emf.ecore.EObject
	 */
	public boolean containsRefObject(EObject aRefObject) {
		return contains(getRefObjects(), aRefObject);
	}

	/**
	 * Return true if
	 * 
	 * @aResource has been added to this group. Creation date: (12/17/2000 1:28:16 PM)
	 * @param aRefObject
	 *            org.eclipse.emf.ecore.EObject
	 */
	public boolean containsResource(Resource aResource) {
		return contains(getResources(), aResource);
	}

	private Association createAssociation(Object key, Object value) {
		return new Association(key, value);
	}

	/**
	 * Returns a List of RefObjects that were copied. Creation date: (12/17/2000 1:25:46 PM)
	 * 
	 * @return java.util.List
	 */
	public java.util.List getCopiedRefObjects() {
		if (copiedRefObjects == null)
			copiedRefObjects = new ArrayList();
		return copiedRefObjects;
	}

	/**
	 * Returns a List of Resources that were copied. Creation date: (12/17/2000 1:25:46 PM)
	 * 
	 * @return java.util.List
	 */
	public java.util.List getCopiedResources() {
		if (copiedResources == null)
			copiedResources = new ArrayList();
		return copiedResources;
	}

	/**
	 * The context to add all copied resources into. Creation date: (12/17/2000 8:09:45 PM)
	 * 
	 * @return org.eclipse.emf.ecore.resource.ResourceSet
	 */
	public org.eclipse.emf.ecore.resource.ResourceSet getCopyContext() {
		return copyContext;
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 1:44:43 PM)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefaultIdSuffix() {
		return defaultIdSuffix;
	}

	/**
	 * Should the id be copied in the case where no suffix is specified? Defaults to false
	 */
	public boolean getPreserveIds() {
		return preserveIds;
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 1:25:46 PM)
	 * 
	 * @return java.util.List
	 */
	public java.util.List getRefObjects() {
		if (refObjects == null)
			refObjects = new ArrayList();
		return refObjects;
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 1:25:46 PM)
	 * 
	 * @return java.util.List
	 */
	public java.util.List getResources() {
		if (resources == null)
			resources = new ArrayList();
		return resources;
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 1:25:46 PM)
	 * 
	 * @return java.util.List
	 */
	public java.util.List primGetRefObjects() {
		return refObjects;
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 1:25:46 PM)
	 * 
	 * @return java.util.List
	 */
	public java.util.List primGetResources() {
		return resources;
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 1:28:16 PM)
	 * 
	 * @param aRefObject
	 *            org.eclipse.emf.ecore.EObject
	 * @return boolean
	 */
	public boolean remove(EObject aRefObject) {
		if (aRefObject != null && primGetRefObjects() != null) {
			Iterator it = primGetRefObjects().iterator();
			Association association;
			while (it.hasNext()) {
				association = (Association) it.next();
				if (association.getKey() == aRefObject)
					return primGetRefObjects().remove(association);
			}
		}
		return false;
	}

	/**
	 * Insert the method's description here. Creation date: (12/17/2000 1:27:32 PM)
	 * 
	 * @param aResources
	 *            org.eclipse.emf.ecore.resource.Resource
	 * @return boolean
	 */
	public boolean remove(Resource aResource) {
		if (aResource != null && primGetResources() != null) {
			Iterator it = primGetResources().iterator();
			Association association;
			while (it.hasNext()) {
				association = (Association) it.next();
				if (association.getKey() == aResource)
					return primGetResources().remove(association);
			}
		}
		return false;
	}

	/**
	 * Set the context to add all copied resources into. Creation date: (12/17/2000 8:09:45 PM)
	 * 
	 * @param newCopyContext
	 *            org.eclipse.emf.ecore.resource.Context
	 */
	public void setCopyContext(org.eclipse.emf.ecore.resource.ResourceSet newCopyContext) {
		copyContext = newCopyContext;
	}

	/**
	 * If an ID suffix is not defined for a EObject that is to be copied, this value will be used.
	 * Creation date: (12/17/2000 1:44:43 PM)
	 * 
	 * @param newDefaultIdSuffix
	 *            java.lang.String
	 */
	public void setDefaultIdSuffix(java.lang.String newDefaultIdSuffix) {
		defaultIdSuffix = newDefaultIdSuffix;
	}

	/**
	 * Should the id be copied in the case where no suffix is specified?
	 */
	public void setPreserveIds(boolean value) {
		preserveIds = value;
	}

	/**
	 * Subclasses should override this method if they require additional processing after the group
	 * has been copied.
	 * 
	 * @param copyUtil
	 */
	public void postCopy(EtoolsCopyUtility copyUtil) {
	}

}

