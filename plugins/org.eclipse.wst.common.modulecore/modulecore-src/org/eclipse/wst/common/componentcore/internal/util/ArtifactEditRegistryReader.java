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
/*
 * Created on Mar 29, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.componentcore.internal.util;

import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author cbridgha
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public class ArtifactEditRegistryReader extends RegistryReader {
	/**
	 * @param registry
	 * @param plugin
	 * @param extensionPoint
	 */
	Hashtable typeRegistry = new Hashtable();
	static final String ARTIFACT_EDIT_EXTENSION_POINT = "artifactedit"; //$NON-NLS-1$
	static final String ARTIFACTEDIT = "artifactedit"; //$NON-NLS-1$
	static final String TYPE = "typeID"; //$NON-NLS-1$
	static final String ARTIFACTEDITCLASS = "class"; //$NON-NLS-1$
	private static ArtifactEditRegistryReader instance;
	
	/**
	 * @return Returns the instance.
	 */
	public static ArtifactEditRegistryReader instance() {
		if (instance == null) {
			instance = new ArtifactEditRegistryReader();
			instance.readRegistry();
		}
		return instance;
	}
	
	public ArtifactEditRegistryReader() {
		super(ModulecorePlugin.PLUGIN_ID, ARTIFACT_EDIT_EXTENSION_POINT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
	 */
	public boolean readElement(IConfigurationElement element) {
		if (!element.getName().equals(ARTIFACTEDIT))
			return false;

		IArtifactEditFactory staticCaller = null;
		String typeID = null;
		try {
			typeID = element.getAttribute(TYPE);
			staticCaller = (IArtifactEditFactory) element.createExecutableExtension(ARTIFACTEDITCLASS);
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (staticCaller != null)
			addArtifactEdit(typeID,staticCaller);
		return true;
	}

	private void addArtifactEdit(String typeID, IArtifactEditFactory staticCaller) {
		typeRegistry.put(typeID,staticCaller);
	}
	
	public IArtifactEditFactory getArtifactEdit(String typeID) {
		return (IArtifactEditFactory)typeRegistry.get(typeID);
	}
	
	public IArtifactEditFactory getArtifactEdit(IProject project) {
		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			Iterator keys = typeRegistry.keySet().iterator();
			while (keys.hasNext()) {
				String typeID = (String) keys.next();
				try {
					IProjectFacet projectFacet = ProjectFacetsManager.getProjectFacet(typeID);
					if (projectFacet != null && facetedProject.hasProjectFacet(projectFacet))
						return getArtifactEdit(typeID);
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			//Just return null
		}
		return null;
	}

}
