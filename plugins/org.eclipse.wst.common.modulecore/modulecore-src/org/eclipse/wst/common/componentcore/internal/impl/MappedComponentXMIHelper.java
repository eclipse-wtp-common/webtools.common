package org.eclipse.wst.common.componentcore.internal.impl;

import java.util.Map;

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
			IVirtualFile virtualFile = component.getRootFolder().getFile(new Path(relative.toString()));
			if (virtualFile != null) {
				IPath resolvingPath = virtualFile.getWorkspaceRelativePath();
				if (resolvingPath !=null) 
					return URI.createPlatformResourceURI(resolvingPath.toString());
			}
		}
		return null;
	}
	
	private IVirtualComponent getComponent(URI base) {
		IVirtualResource[] virtualResources = ComponentCore.createResources(WorkbenchResourceHelper.getFile(WorkbenchResourceHelperBase.getResource(base)));
		if (virtualResources.length>0)
			return virtualResources[0].getComponent();
		return null;
	}
}
