package org.eclipse.wst.common.componentcore.internal.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

public class ComponentUtil {

	public ComponentUtil() {
		super();
		// TODO Auto-generated constructor stub
	}
	public static IVirtualComponent findComponent(EObject anObject) {
	WorkbenchComponent module = null;
		IProject project = ProjectUtilities.getProject(anObject);
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			URI uri = WorkbenchResourceHelperBase.getNonPlatformURI(anObject.eResource().getURI());
			ComponentResource[] resources = moduleCore.findResourcesBySourcePath(uri);
			for (int i=0; i<resources.length; i++) {
				module = resources[i].getComponent();
				if (module !=null)
					break;
			}
		} catch (UnresolveableURIException e) {
			//Ignore
		} finally {
			if (moduleCore !=null)
				moduleCore.dispose();
		}
	return ComponentCore.createComponent(project,module.getName());
	}
	public static ArtifactEdit getArtifactEditForRead(IVirtualComponent comp) {
		ArtifactEditRegistryReader reader = ArtifactEditRegistryReader.instance();
		IArtifactEditFactory factory = reader.getArtifactEdit(comp.getComponentTypeId());
		return factory.createArtifactEditForRead(comp);
	}
	public static ArtifactEdit getArtifactEditForWrite(IVirtualComponent comp) {
		ArtifactEditRegistryReader reader = ArtifactEditRegistryReader.instance();
		IArtifactEditFactory factory = reader.getArtifactEdit(comp.getComponentTypeId());
		return factory.createArtifactEditForWrite(comp);
	}
}
