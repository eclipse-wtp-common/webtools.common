/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Mar 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emfworkbench;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jem.util.emf.workbench.ResourceSetWorkbenchSynchronizer;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.jem.util.emf.workbench.WorkbenchURIConverterImpl;
import org.eclipse.wst.common.internal.emf.resource.CompatibilityURIConverter;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CompatibilityWorkbenchURIConverterImpl extends WorkbenchURIConverterImpl implements CompatibilityURIConverter {
	/**
	 *  
	 */
	public CompatibilityWorkbenchURIConverterImpl() {
		super();
	}

	/**
	 * @param anInputContainer
	 */
	public CompatibilityWorkbenchURIConverterImpl(IContainer anInputContainer) {
		super(anInputContainer);
	}

	/**
	 * @param aContainer
	 * @param aSynchronizer
	 */
	public CompatibilityWorkbenchURIConverterImpl(IContainer aContainer, ResourceSetWorkbenchSynchronizer aSynchronizer) {
		super(aContainer, aSynchronizer);
	}

	/**
	 * @param anInputContainer
	 * @param anOutputContainer
	 */
	public CompatibilityWorkbenchURIConverterImpl(IContainer anInputContainer, IContainer anOutputContainer) {
		super(anInputContainer, anOutputContainer);
	}

	/**
	 * @param anInputContainer
	 * @param anOutputContainer
	 * @param aSynchronizer
	 */
	public CompatibilityWorkbenchURIConverterImpl(IContainer anInputContainer, IContainer anOutputContainer, ResourceSetWorkbenchSynchronizer aSynchronizer) {
		super(anInputContainer, anOutputContainer, aSynchronizer);
	}

	/**
	 * @see com.ibm.etools.xmi.helpers.CompatibilityURIConverter#deNormalize(URI)
	 */
	@Override
	public URI deNormalize(URI uri) {
		if (WorkbenchResourceHelperBase.isPlatformResourceURI(uri)) {
			IFile aFile = WorkbenchResourceHelper.getPlatformFile(uri);
			if (aFile != null) {
				IProject fileProject = aFile.getProject();
				//If it is not in the same project then just return the URI as is.
				if (resourceSetSynchronizer.getProject() == fileProject)
					return getContainerRelativeURI(aFile);
			}
		}
		return uri;
	}

}
