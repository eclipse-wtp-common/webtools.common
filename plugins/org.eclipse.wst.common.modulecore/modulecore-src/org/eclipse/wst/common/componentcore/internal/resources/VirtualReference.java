/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resolvers.IReferenceResolver;
import org.eclipse.wst.common.componentcore.resolvers.ReferenceResolverUtil;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.frameworks.internal.HashUtil;

public class VirtualReference implements IVirtualReference {
	
	private IVirtualComponent referencedComponent;
	private IVirtualComponent enclosingComponent;
	private IPath runtimePath;
	private int dependencyType;
	private String archiveName;
	private boolean derived;
	public VirtualReference() {
		
	} 
	
	public VirtualReference(IVirtualComponent anEnclosingComponent, IVirtualComponent aReferencedComponent) {
		this(anEnclosingComponent, aReferencedComponent, new Path(String.valueOf(IPath.SEPARATOR)), DEPENDENCY_TYPE_USES, false); 
	}
	
	public VirtualReference(IVirtualComponent anEnclosingComponent, IVirtualComponent aReferencedComponent, IPath aRuntimePath) {
		this(anEnclosingComponent, aReferencedComponent, aRuntimePath, DEPENDENCY_TYPE_USES, false);
	}

	public VirtualReference(IVirtualComponent anEnclosingComponent, IVirtualComponent aReferencedComponent, 
			IPath aRuntimePath, int aDependencyType) {
		this(anEnclosingComponent, aReferencedComponent, aRuntimePath, aDependencyType, false);
	}
	
	public VirtualReference(IVirtualComponent anEnclosingComponent, IVirtualComponent aReferencedComponent, 
			IPath aRuntimePath, int aDependencyType, boolean isDerived) {
		enclosingComponent = anEnclosingComponent;
		referencedComponent = aReferencedComponent;
		runtimePath = aRuntimePath;
		dependencyType = aDependencyType;
		derived = isDerived;
	}

	public void create(int updateFlags, IProgressMonitor aMonitor) { 
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForWrite(enclosingComponent.getProject());
			WorkbenchComponent component = core.getComponent();
			List referencedComponents = component.getReferencedComponents();
			IReferenceResolver resolver = ReferenceResolverUtil.getDefault().getResolver(this);
			ReferencedComponent refComp = resolver.resolve(this);
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
				IReferenceResolver resolver = ReferenceResolverUtil.getDefault().getResolver(this);
				URI uri = resolver.resolve(this).getHandle();
				actualReferencedComponent.setHandle(uri);
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
	
	public boolean isDerived() {
		return derived;
	}
	
	public void setDerived(boolean val) {
		derived = val;
	}

	public boolean equals(Object anOther) {
		if(anOther == null || !(anOther instanceof IVirtualReference)) return false;
		if(anOther == this) return true;
		IVirtualReference otherRef = (IVirtualReference) anOther;
		return (getArchiveName() != null ? getArchiveName().equals(otherRef.getArchiveName()) : (otherRef.getArchiveName() == null ? true : false)) && 
			   getRuntimePath().equals(otherRef.getRuntimePath()) && 
			   getEnclosingComponent().equals(otherRef.getEnclosingComponent()) && 
			   getReferencedComponent().equals(otherRef.getReferencedComponent()) && 
			   getDependencyType() == otherRef.getDependencyType();
		
	}

	public int hashCode() {
		
		int hash = HashUtil.SEED;
		hash = HashUtil.hash(hash, getArchiveName());
		hash = HashUtil.hash(hash, getRuntimePath());
		hash = HashUtil.hash(hash, getEnclosingComponent());
		hash = HashUtil.hash(hash, getReferencedComponent());
		hash = HashUtil.hash(hash, getDependencyType());
		return hash;
	}
}
