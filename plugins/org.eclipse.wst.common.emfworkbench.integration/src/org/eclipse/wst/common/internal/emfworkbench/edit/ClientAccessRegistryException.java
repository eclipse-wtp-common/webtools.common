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
/*
 * Created on Oct 2, 2003
 *  
 */
package org.eclipse.wst.common.internal.emfworkbench.edit;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchEditResourceHandler;


/**
 * The ClientAccessRegistryException accepts a WeakHashSet registry and a Set of all Snapshots that
 * should be contained in the registry. It will perform the necessary set difference in order to
 * determine which Snapshots exist in the baseSet but not in the registry.
 * 
 * @author mdelder
 */
public class ClientAccessRegistryException extends RuntimeException {

	public static final int UNKNOWN = 0;
	public static final int DANGLING_REFERENCE = 1;
	public static final int INVALID_ACCESS_KEY = 2;
	private WeakHashMap registry = null;
	private Set baseSet = null;
	private boolean processed = false;
	private int type = UNKNOWN;

	public ClientAccessRegistryException(WeakHashMap registry, Set baseSet) {
		this.registry = registry;
		this.baseSet = new HashSet();
		this.baseSet.addAll(baseSet);
		this.type = DANGLING_REFERENCE;
	}

	public ClientAccessRegistryException(String msg, Object key) {
		super(msg + " : " + key); //$NON-NLS-1$
		this.type = INVALID_ACCESS_KEY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
	 */
	public void printStackTrace(PrintStream s) {

		s.println(toString());
		super.printStackTrace(s);

		if (this.registry != null) {
			Snapshot snapshot = null;
			if (!processed) {
				Object key = null;
				Iterator keyIterator = this.registry.keySet().iterator();
				while (keyIterator.hasNext()) {
					key = keyIterator.next();
					snapshot = (Snapshot) this.registry.get(key);
					this.baseSet.remove(snapshot);
				}
				processed = true;
			}
			s.println(EMFWorkbenchEditResourceHandler.getString("ClientAccessRegistryException_UI_0", new Object[]{new Integer(baseSet.size()).toString()})); //$NON-NLS-1$

			for (Iterator possibleCulpritsItr = baseSet.iterator(); possibleCulpritsItr.hasNext();) {
				snapshot = (Snapshot) possibleCulpritsItr.next();
				snapshot.printStackTrace(s);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
	 */
	public void printStackTrace(PrintWriter s) {

		s.println(toString());
		super.printStackTrace(s);
		if (this.registry != null) {
			Snapshot snapshot = null;
			if (!processed) {
				Object key = null;
				Iterator keyIterator = this.registry.keySet().iterator();
				while (keyIterator.hasNext()) {
					key = keyIterator.next();
					snapshot = (Snapshot) this.registry.get(key);
					this.baseSet.remove(snapshot);
				}
				processed = true;
			}
			s.println(EMFWorkbenchEditResourceHandler.getString("", new Object[]{new Integer(baseSet.size()).toString()})); //$NON-NLS-1$

			for (Iterator possibleCulpritsItr = baseSet.iterator(); possibleCulpritsItr.hasNext();) {
				snapshot = (Snapshot) possibleCulpritsItr.next();
				snapshot.printStackTrace(s);
			}

		}
	}

	public String getType() {
		switch (type) {
			case DANGLING_REFERENCE :
				return "DANGLING_REFERENCE"; //$NON-NLS-1$
			case INVALID_ACCESS_KEY :
				return "INVALID_ACCESS_KEY"; //$NON-NLS-1$
			default :
				return "UNKNOWN"; //$NON-NLS-1$
		}
	}

	public String toString() {
		StringBuffer result = new StringBuffer(super.toString()).append("\r\n"); //$NON-NLS-1$
		result.append(EMFWorkbenchEditResourceHandler.getString("ClientAccessRegistryException_UI_1", new Object[]{getType()})); //$NON-NLS-1$
		//		if (this.badReferenceLocation != null) {
		//			result.append("The invalid access occurred somewhere in the following stack
		// trace.").append("\n");
		//			result.append(this.badReferenceLocation.getStackTraceString());
		//		}
		return result.toString();
	}

	public static void main(String[] args) {
		System.out.println(new ClientAccessRegistryException("test message", "-somekeyobj-").toString()); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("LINEBREAK"); //$NON-NLS-1$
		System.out.println(new ClientAccessRegistryException(new WeakHashMap(), new HashSet()).toString());
		System.out.println("LINEBREAK"); //$NON-NLS-1$
		new ClientAccessRegistryException("test message", "-somekeyobj-").printStackTrace(); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("LINEBREAK"); //$NON-NLS-1$
		new ClientAccessRegistryException(new WeakHashMap(), new HashSet()).printStackTrace();

	}

}