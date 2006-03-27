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
package org.eclipse.wst.common.componentcore.internal.resources;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.DependencyType;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.impl.ModuleURIUtil;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public class VirtualReference implements IVirtualReference {
	
	private IVirtualComponent referencedComponent;
	private IVirtualComponent enclosingComponent;
	private IPath runtimePath;
	private int dependencyType;
	private String archiveName;

	public VirtualReference() {
		
	} 
	
	public VirtualReference(IVirtualComponent anEnclosingComponent, IVirtualComponent aReferencedComponent) {
		this(anEnclosingComponent, aReferencedComponent, new Path(String.valueOf(IPath.SEPARATOR)), DEPENDENCY_TYPE_USES); 
	}
	
	public VirtualReference(IVirtualComponent anEnclosingComponent, IVirtualComponent aReferencedComponent, IPath aRuntimePath) {
		this(anEnclosingComponent, aReferencedComponent, aRuntimePath, DEPENDENCY_TYPE_USES);
	}

	public VirtualReference(IVirtualComponent anEnclosingComponent, IVirtualComponent aReferencedComponent, IPath aRuntimePath, int aDependencyType) {
		enclosingComponent = anEnclosingComponent;
		referencedComponent = aReferencedComponent;
		runtimePath = aRuntimePath;
		dependencyType = aDependencyType;
	}

	public void create(int updateFlags, IProgressMonitor aMonitor) { 
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForWrite(enclosingComponent.getProject());
			WorkbenchComponent component = core.getComponent();
			List referencedComponents = component.getReferencedComponents();
			ReferencedComponent refComp = ComponentcorePackage.eINSTANCE.getComponentcoreFactory().createReferencedComponent();
			if( !referencedComponent.isBinary())
				refComp.setHandle(ModuleURIUtil.fullyQualifyURI(referencedComponent.getProject()));
			else
				refComp.setHandle(ModuleURIUtil.archiveComponentfullyQualifyURI(referencedComponent.getName())); 
			refComp.setRuntimePath(runtimePath);
			refComp.setDependencyType(DependencyType.get(dependencyType));
			refComp.setArchiveName(archiveName);
			if(!referencedComponents.contains(refComp)){
				referencedComponents.add(refComp);
			}
		}
		finally{
			if(null != core){
				core.saveIfNecessary(aMonitor);
				core.dispose();
			}
		}
	}

	public void setRuntimePath(IPath aRuntimePath) { 
		runtimePath = aRuntimePath;
	}

	public IPath getRuntimePath() { 
		return runtimePath;
	}

	public void setDependencyType(int aDependencyType) {
		dependencyType = aDependencyType;
	}

	public int getDependencyType() { 
		return dependencyType;
	}

	public boolean exists() { 
		return false;
	}

	public IVirtualComponent getEnclosingComponent() { 
		return enclosingComponent;
	}

	public IVirtualComponent getReferencedComponent() { 
		return referencedComponent;
	}
	
	/**
	 * This is a helper method to update the actual referenceComponent on the .component file for this virtual reference.
	 */
	public void setReferencedComponent(IVirtualComponent aReferencedComponent, EObject dependentObject) {
		if (aReferencedComponent == null)
			return;
		StructureEdit enclosingCore = null;
		StructureEdit refCore = null;
		try {
			enclosingCore = StructureEdit.getStructureEditForWrite(enclosingComponent.getProject());
			refCore = StructureEdit.getStructureEditForWrite(referencedComponent.getProject());
			WorkbenchComponent enclosingComp = enclosingCore.getComponent();
			WorkbenchComponent refComp = refCore.getComponent();
			ReferencedComponent actualReferencedComponent = enclosingCore.findReferencedComponent(enclosingComp, refComp);
			if (actualReferencedComponent != null) {
				referencedComponent = aReferencedComponent;
				if(!referencedComponent.isBinary())
					actualReferencedComponent.setHandle(ModuleURIUtil.fullyQualifyURI(referencedComponent.getProject()));
				else
					actualReferencedComponent.setHandle(ModuleURIUtil.archiveComponentfullyQualifyURI(referencedComponent.getName()));
				actualReferencedComponent.setDependentObject(dependentObject);
			}
		} finally {
			if (enclosingCore != null) {
				enclosingCore.saveIfNecessary(new NullProgressMonitor());
				enclosingCore.dispose();
			}
			if (refCore != null) {
				refCore.saveIfNecessary(new NullProgressMonitor());
				refCore.dispose();
			}
		}
	}

	public String getArchiveName() {
		return archiveName;
	}

	public void setArchiveName(String archiveName) {
		this.archiveName = archiveName;
	}
}
