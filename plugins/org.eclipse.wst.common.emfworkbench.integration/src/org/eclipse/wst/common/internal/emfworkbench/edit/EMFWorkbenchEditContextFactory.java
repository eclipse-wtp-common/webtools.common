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
 * Created on Mar 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emfworkbench.edit;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.integration.ResourceSetWorkbenchEditSynchronizer;

import com.ibm.wtp.emf.workbench.EMFWorkbenchContextBase;
import com.ibm.wtp.emf.workbench.ResourceSetWorkbenchSynchronizer;
import com.ibm.wtp.internal.emf.workbench.EMFWorkbenchContextFactory;

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

	protected EMFWorkbenchContextBase primCreateEMFContext(IProject aProject) {
		return new EMFWorkbenchContext(aProject);
	}

	public ResourceSetWorkbenchSynchronizer createSynchronizer(ResourceSet aResourceSet, IProject aProject) {
		return new ResourceSetWorkbenchEditSynchronizer(aResourceSet, aProject);
	}
}