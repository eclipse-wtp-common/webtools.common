package org.eclipse.wst.common.componentcore.resolvers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.DependencyType;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.impl.ModuleURIUtil;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public class DefaultReferenceResolver implements IReferenceResolver {
	// Does not need to implement, 
	// default is always called as last resort
	public boolean canResolve(IVirtualComponent context,
			ReferencedComponent referencedComponent) {
		return false;
	}

	// Does not need to implement, 
	// default is always called as last resort
	public boolean canResolve(IVirtualReference reference) {
		return false;
	}

	public IVirtualReference resolve(IVirtualComponent context,
			ReferencedComponent referencedComponent) {
		IVirtualComponent targetComponent = null;
		IProject targetProject = null;
		URI uri = referencedComponent.getHandle();
		if (uri == null)
			return null;
		boolean isClassPathURI = ModuleURIUtil.isClassPathURI(uri);
		if( !isClassPathURI ){
			try { 
				targetProject = StructureEdit.getContainingProject(uri);
			} catch(UnresolveableURIException uurie) {
				//Ignore
			} 
			// if the project cannot be resolved, assume it's local - really it probably deleted 
			
			targetComponent = ComponentCore.createComponent(targetProject);  
				

		}else{
			String archiveType = ""; //$NON-NLS-1$
			String archiveName = ""; //$NON-NLS-1$
			try {
				archiveType = ModuleURIUtil.getArchiveType(uri);
				archiveName = ModuleURIUtil.getArchiveName(uri);
				
			} catch (UnresolveableURIException e) {
				//Ignore
			}
			targetComponent = ComponentCore.createArchiveComponent(context.getProject(), archiveType + IPath.SEPARATOR + archiveName ); 
		}
		VirtualReference vRef = new VirtualReference(context, targetComponent, referencedComponent.getRuntimePath(), referencedComponent.getDependencyType().getValue());
		vRef.setArchiveName(referencedComponent.getArchiveName());
		return vRef;
	}

	public ReferencedComponent resolve(IVirtualReference reference) {
		IVirtualComponent referencedComponent = reference.getReferencedComponent();
		ReferencedComponent refComp = ComponentcorePackage.eINSTANCE.getComponentcoreFactory().createReferencedComponent();
		refComp.setRuntimePath(reference.getRuntimePath());
		refComp.setDependencyType(DependencyType.get(reference.getDependencyType()));
		refComp.setArchiveName(reference.getArchiveName());
		if( referencedComponent != null ) {
			if( !referencedComponent.isBinary())
				refComp.setHandle(ModuleURIUtil.fullyQualifyURI(referencedComponent.getProject()));
			else
				refComp.setHandle(ModuleURIUtil.archiveComponentfullyQualifyURI(referencedComponent.getName())); 
		}
		return refComp;
	}


}
