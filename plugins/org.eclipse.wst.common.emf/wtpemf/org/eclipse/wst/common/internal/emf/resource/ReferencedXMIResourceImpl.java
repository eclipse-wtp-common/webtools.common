/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.common.emf.utilities.IDUtil;

public class ReferencedXMIResourceImpl extends CompatibilityXMIResourceImpl implements ReferencedResource {
	//TODO Add toString hook
	private static final String TO_STRING = "ReferencedXMIResource, file = "; //$NON-NLS-1$
	private static final String READ_COUNT_TO_STRING = " R= "; //$NON-NLS-1$
	private static final String WRITE_COUNT_TO_STRING = " W= "; //$NON-NLS-1$

	private int readReferenceCount = 1;
	private int editReferenceCount = 0;
	protected boolean isNew = true;
	protected boolean forceRefresh;

	/**
	 * ReferencableXMIResourceImpl constructor comment.
	 */
	public ReferencedXMIResourceImpl() {
		super();
	}

	public ReferencedXMIResourceImpl(URI uri) {
		super(uri);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emf.resource.ReferencedResource#getReadCount()
	 */
	public int getReadCount() {
		return readReferenceCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.internal.emf.resource.ReferencedResource#getWriteCount()
	 */
	public int getWriteCount() {
		return editReferenceCount;
	}



	/*
	 * @see ReferencedResource#accessForRead
	 */
	public void accessForRead() {
		checkDeleted();
		if (!isNew())
			readReferenceCount++;
		isNew = false;
	}

	/*
	 * @see ReferencedResource#accessForWrite
	 */
	public void accessForWrite() {
		checkDeleted();
		editReferenceCount++;
		if (isNew)
			releaseFromRead();
		isNew = false;
		if (!isTrackingModification())
			setTrackingModification(true);
	}

	/*
	 * Check if this resource has been removed and throw an exception if it does not have a
	 * ResourceSet.
	 */
	protected void checkDeleted() {
		if (getResourceSet() == null)
			throw new RuntimeException(DELETED_ERROR_MSG);
	}

	/**
	 *  
	 */
	public void collectContainedObjects(java.util.List collecting, EObject parentObject) {

		java.util.Iterator children = parentObject.eContents().iterator();
		while (children.hasNext()) {
			EObject child = (EObject) children.next();
			collecting.add(child);
			collectContainedObjects(collecting, child);
		}
	}

	/**
	 *  
	 */
	public void ensureFullIDHydration() {
		List allRefObjects = new ArrayList();
		Iterator rootObjects = getContents().iterator();
		while (rootObjects.hasNext()) {
			EObject child = (EObject) rootObjects.next();
			allRefObjects.add(child);
			collectContainedObjects(allRefObjects, child);
		}

		Iterator iter = allRefObjects.iterator();
		while (iter.hasNext())
			IDUtil.assignID((EObject) iter.next(), this);
	}

	/**
	 * If the resource is no longer being accessed, then remove it from the resource set.
	 */
	public void unloadIfNecessary() {
		if ((getTotalReferenceCount() <= 0) || (editReferenceCount <= 0 && isModified()))
			unload();
	}

	/**
	 * Return the number of write accesses to this resource.
	 * 
	 * @return int The number of references.
	 */
	protected int getTotalReferenceCount() {
		return editReferenceCount + readReferenceCount;
	}

	/*
	 * @see ReferencedResource#isNew
	 */
	public boolean isNew() {
		return isNew;
	}

	/*
	 * @see ReferencedResource#isReadOnly
	 */
	public boolean isReadOnly() {
		return editReferenceCount <= 0;
	}

	/*
	 * @see ReferencedResource#isShared
	 */
	public boolean isShared() {
		return getTotalReferenceCount() > 1;
	}

	/*
	 * @see ReferencedResource#isSharedForWrite
	 */
	public boolean isSharedForWrite() {
		return editReferenceCount > 1;
	}

	/**
	 * @see ReferencedResource#preDelete
	 */
	public void preDelete() {
	}

	/*
	 * @see ReferencedResource#releaseFromRead
	 */
	public void releaseFromRead() {
		readReferenceCount--;
		if (readReferenceCount < 0)
			throw new RuntimeException("Read reference count error:  " + this.toString()); //$NON-NLS-1$
		unloadIfNecessary();
	}

	/*
	 * @see ReferencedResource#releaseFromWrite
	 */
	public void releaseFromWrite() {
		editReferenceCount--;
		if (editReferenceCount < 0)
			throw new RuntimeException("Write reference count error:  " + this.toString()); //$NON-NLS-1$
		unloadIfNecessary();
	}

	/*
	 * @see ReferencedResource#saveIfNecessary
	 */
	public void saveIfNecessary() throws Exception {
		if (!isSharedForWrite()) // caller is the only referencer
			save(Collections.EMPTY_MAP);
	}

	public String toString() {
		return TO_STRING + getURI().toString() + READ_COUNT_TO_STRING + new Integer(readReferenceCount) + WRITE_COUNT_TO_STRING + new Integer(editReferenceCount);
	}

	/*
	 * @see ReferencedResource#needsToSave()
	 */
	public boolean needsToSave() {
		return isModified() && !isSharedForWrite();
	}


	/**
	 * @see ReferencedResource#setForceRefresh(boolean)
	 */
	public void setForceRefresh(boolean b) {
		forceRefresh = b;
	}

	/**
	 * @see ReferencedResource#shouldForceRefresh()
	 */
	public boolean shouldForceRefresh() {
		return forceRefresh;
	}

	protected void basicDoLoad(InputStream arg0, Map arg1) throws IOException {
		boolean isTrackingMods = isTrackingModification();
		try {
			if (isTrackingMods)
				setTrackingModification(false);
			super.basicDoLoad(arg0, arg1);
		} finally {
			if (isTrackingMods)
				setTrackingModification(true);
		}
	}

	/**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#doUnload()
	 */
	protected void doUnload() {
		if (isTrackingModification() && editReferenceCount < 1) //do not turn off modification if
			// we still have a write count
			setTrackingModification(false);
		super.doUnload();
		setForceRefresh(false);
		setModified(false); //dcb - this is required to ensure that resources without files are
		// marked as not modified.
		if (readReferenceCount == 0 && editReferenceCount == 0)
			resetAsNew();
	}


	/**
	 * The resource has been unloaded, and there are no references. Treat the resource like a new
	 * Resource
	 */
	private void resetAsNew() {
		readReferenceCount = 1;
		isNew = true;
	}

	/**
	 * @see Resource#save(Object)
	 */
	public void save(Map options) throws IOException {
		super.save(options);
		notifySaved();
	}

	protected void notifySaved() {
		if (eNotificationRequired()) {
			Notification notification = new NotificationImpl(RESOURCE_WAS_SAVED, this, this) {
				public Object getNotifier() {
					return ReferencedXMIResourceImpl.this;
				}

				public int getFeatureID(Class expectedClass) {
					return RESOURCE_WAS_SAVED;
				}
			};
			eNotify(notification);
		}
	}


	/**
	 * @see com.ibm.etools.emf.workbench.ReferencedResource#wasReverted()
	 */
	public boolean wasReverted() {
		return false;
	}
	
   /*
   * THIS IS A HACK!!! REMOVE when bugzilla 80110 is fixed
   */
  // TODO REMOVE this!
  public void setTrackingModification(boolean isTrackingModification)
  {
    boolean oldIsTrackingModification = this.modificationTrackingAdapter != null;

    if (oldIsTrackingModification != isTrackingModification)
    {
      this.modificationTrackingAdapter = isTrackingModification ? createModificationTrackingAdapter() : null;

      if (isTrackingModification)
      {
        for (Iterator i = getContents().iterator(); i.hasNext(); )
        {
          attachedHelper((EObject)i.next());
        }
      }
      else
      {
        for (Iterator i = getContents().iterator(); i.hasNext(); )
        {
          detachedHelper((EObject)i.next());
        }
      }
    }

    if (eNotificationRequired())
    {
      Notification notification =
        new NotificationImpl(Notification.SET, oldIsTrackingModification, isTrackingModification)
        {
          public Object getNotifier()
          {
            return ReferencedXMIResourceImpl.this;
          }
          public int getFeatureID(Class expectedClass)
          {
            return RESOURCE__IS_TRACKING_MODIFICATION;
          }
        };
      eNotify(notification);
    }
  }

}