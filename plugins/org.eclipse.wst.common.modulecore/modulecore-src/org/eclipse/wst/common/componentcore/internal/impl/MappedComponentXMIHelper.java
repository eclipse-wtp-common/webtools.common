/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.impl;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.URIConverterImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.internal.emf.resource.MappedXMIHelper;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;

public class MappedComponentXMIHelper extends MappedXMIHelper {

	public MappedComponentXMIHelper(XMLResource resource, Map prefixesToURIs) {
		super(resource, prefixesToURIs);
	}

	public URI resolve(URI relative, URI base) {
		URI resolved = null;
		if (!isMapped(relative))
			resolved = resolveURIFromComponent(relative,base);
		else
			resolved = relative;
		
		return resolved == null ? super.resolve(relative, base) : resolved;
	}
	
	private boolean isMapped(URI relative) {
		boolean isMapped = false;
		ResourceSet set = getResource().getResourceSet();
		if (set != null) {
			URI localresourceURI = relative;
			if (relative.hasFragment())
				localresourceURI = relative.trimFragment();
			isMapped = !((URIConverterImpl.URIMap) set.getURIConverter().getURIMap()).getURI(localresourceURI).equals(localresourceURI);
		}
		return isMapped;
	}
	
	private URI resolveURIFromComponent(URI relative, URI base) {
		IVirtualComponent component = getComponent(base);
		if (component != null) {
			// If the relative URI has a fragment, remove it before resolving. 
			boolean hasFragment = relative.hasFragment();
			URI tmpURI = hasFragment?relative.trimFragment():relative;
			IVirtualFile virtualFile = component.getRootFolder().getFile(new Path(tmpURI.toString()));
			if (virtualFile != null) {
				IPath resolvingPath = virtualFile.getWorkspaceRelativePath();
				if (resolvingPath !=null) {
					URI result = URI.createPlatformResourceURI(resolvingPath.toString());
					// If the original URI had a fragment, add the fragment to the result.
					if (hasFragment){
						result = result.appendFragment(relative.fragment());
					}
					return result;
				}
			}
		}
		return null;
	}
	
	private IVirtualComponent getComponent(URI base) {
		ResourceSet set = getResource().getResourceSet();
		if (set == null || set.getURIConverter()==null)
			return null;
		URI normalized = set.getURIConverter().normalize(base);
		if (WorkbenchResourceHelperBase.isPlatformResourceURI(normalized)) {
			IFile file = WorkbenchResourceHelper.getPlatformFile(normalized);
			if (file !=null) {
				IVirtualResource[] virtualResources = ComponentCore.createResources(file);
				if (virtualResources.length>0)
					return virtualResources[0].getComponent();
			}
		}
		return null;
	}
}
