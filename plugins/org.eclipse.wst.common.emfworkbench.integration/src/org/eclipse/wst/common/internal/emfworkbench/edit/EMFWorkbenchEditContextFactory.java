/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Mar 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emfworkbench.edit;

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.internal.jobs.LockManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jem.internal.util.emf.workbench.EMFWorkbenchContextFactory;
import org.eclipse.jem.util.emf.workbench.EMFWorkbenchContextBase;
import org.eclipse.jem.util.emf.workbench.IEMFContextContributor;
import org.eclipse.jem.util.emf.workbench.ResourceSetWorkbenchSynchronizer;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.integration.ResourceSetWorkbenchEditSynchronizer;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EMFWorkbenchEditContextFactory extends EMFWorkbenchContextFactory {
	
	
	/**
	 *  
	 */
	public EMFWorkbenchEditContextFactory() {
		super();
	}

	@Override
	protected EMFWorkbenchContextBase primCreateEMFContext(IProject aProject) {
		return new EMFWorkbenchContext(aProject);
	}

	@Override
	public ResourceSetWorkbenchSynchronizer createSynchronizer(ResourceSet aResourceSet, IProject aProject) {
		return new ResourceSetWorkbenchEditSynchronizer(aResourceSet, aProject);
	}
	
	protected static LockManager lockManager = new LockManager();
	protected static Map projectLocks = new Hashtable();

	public static ILock getProjectLockObject(IProject aProject){
		if(null == aProject){
			return null;
		}
		Integer hashCode = new Integer(aProject.hashCode());
		synchronized (projectLocks) {
			ILock lock = (ILock)projectLocks.get(hashCode);
			if(lock == null){
				lock = lockManager.newLock();
				projectLocks.put(hashCode, lock);
			}
			return lock;
		}
	}
	
	@Override
	public EMFWorkbenchContextBase createEMFContext(IProject aProject, IEMFContextContributor contributor) {
		ILock lock = getProjectLockObject(aProject);
		try{
			if(null != lock){
				lock.acquire();
			}
			return super.createEMFContext(aProject, contributor);
		} finally{
			if(null != lock){
				lock.release();
			}
		}
	}
	
	@Override
	protected EMFWorkbenchContextBase getCachedEMFContext(IProject aProject) {
		ILock lock = getProjectLockObject(aProject);
		try{
			if(null != lock){
				lock.acquire();
			}
			return super.getCachedEMFContext(aProject);
		} finally{
			if(null != lock){
				lock.release();
			}
		}
	}
}
