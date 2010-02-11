/*******************************************************************************
 * Copyright (c) 2009 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.util;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

public class VirtualReferenceUtilities implements IModuleConstants {
	public static VirtualReferenceUtilities INSTANCE = new VirtualReferenceUtilities();
	private HashMap<String, String> mapping;
	VirtualReferenceUtilities() {
		mapping = new HashMap<String, String>();
		mapping.put(JST_APPCLIENT_MODULE, JAR_EXTENSION); //$NON-NLS-1$
		mapping.put(JST_WEB_MODULE, WAR_EXTENSION); //$NON-NLS-1$
		mapping.put(JST_EJB_MODULE, JAR_EXTENSION); //$NON-NLS-1$
		mapping.put(WST_WEB_MODULE, WAR_EXTENSION); //$NON-NLS-1$
		mapping.put(JST_EAR_MODULE, EAR_EXTENSION); //$NON-NLS-1$
		mapping.put(JST_CONNECTOR_MODULE, JAR_EXTENSION); //$NON-NLS-1$
		mapping.put(JST_UTILITY_MODULE, JAR_EXTENSION); //$NON-NLS-1$
		mapping.put(JST_WEBFRAGMENT_MODULE, JAR_EXTENSION); //$NON-NLS-1$
	}
	
	/**
	 * Add a mapping for some facet type to some default extension
	 * @param facet 
	 * @param extension
	 * @return
	 */
	public boolean addDefaultExtension(String facet, String extension) {
		if( !mapping.containsKey(facet)) {
			if( ProjectFacetsManager.isProjectFacetDefined(facet)) {
				mapping.put(facet, extension);
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * This is a utility method clients may or may not choose to make use of
	 * to ensure that all Project references have some archiveName associated with it.
	 */
	public void ensureReferencesHaveNames(IVirtualReference[] refs) {
		for( int i = 0; i < refs.length; i++ ) {
			if( refs[i].getArchiveName() == null || refs[i].getArchiveName().equals("")) { //$NON-NLS-1$
				refs[i].setArchiveName(getDefaultArchiveName(refs[i]));
			}
		}
	}

	/**
	 * return what the suggested archiveName is 
	 * @param ref
	 * @return
	 */
	public String getDefaultArchiveName(IVirtualReference ref) {
		if( !ref.getReferencedComponent().isBinary()) {
			return getDefaultProjectArchiveName(ref.getReferencedComponent());
		} 
		// binary
		return new Path(ref.getReferencedComponent().getDeployedName()).lastSegment();
	}
	
	public String getDefaultProjectArchiveName(IVirtualComponent component) {
		Iterator<String> i = mapping.keySet().iterator();
		String facet;
		while(i.hasNext()) {
			facet = i.next();
			if( FacetedProjectUtilities.isProjectOfType(component.getProject(), facet))
				return component.getName() + mapping.get(facet);
		}
		return JAR_EXTENSION; 
	}
}
