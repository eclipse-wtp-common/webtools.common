package org.eclipse.wst.common.componentcore.internal.impl;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.URIConverterImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.jem.util.emf.workbench.ProjectResourceSet;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.internal.emf.resource.MappedXMIHelper;

public class MappedComponentXMIHelper extends MappedXMIHelper {

	public MappedComponentXMIHelper(XMLResource resource, Map prefixesToURIs) {
		super(resource, prefixesToURIs);
		// TODO Auto-generated constructor stub
	}

	public URI resolve(URI relative, URI base) {

		URI resolved = null;
		boolean isMapped = false;
		ResourceSet set = getResource().getResourceSet();
		if (set != null) {
			URI localresourceURI = null;
			if (relative.hasFragment())
				localresourceURI = relative.trimFragment();
			else
				localresourceURI = relative;
			isMapped = !(((URIConverterImpl.URIMap) set.getURIConverter().getURIMap()).getURI(localresourceURI).equals(localresourceURI));
		}
		
		if (!isMapped) {
			String compName = null;
			ComponentResource theResource = null;
			URI compPath = relative;
			StructureEdit se = null;
			try {
				se = StructureEdit.getStructureEditForRead(getProject());
				theResource = se.findResourcesBySourcePath(WorkbenchResourceHelperBase.getPathInProject(getProject(),new Path(WorkbenchResourceHelperBase.getNonPlatformURIString(base))))[0];
				if (theResource != null) {
					compPath = URI.createURI((theResource.getComponent().findResourcesByRuntimePath(new Path(relative.path()))[0]).getSourcePath().toOSString());
					if (set != null) {
					resolved = set.getURIConverter().normalize(compPath);
					}
				}
			} catch (UnresolveableURIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (se != null)
					se.dispose();
			}
			
			
		} else {
			resolved = relative;
		}
		return resolved == null ? super.resolve(relative, base) : resolved;
	
	}

	private IProject getProject() {
		return ((ProjectResourceSet)getResource().getResourceSet()).getProject();
	}

}
