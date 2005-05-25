/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.wst.common.componentcore.internal.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.ComponentType;
import org.eclipse.wst.common.componentcore.internal.ComponentcoreFactory;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.DependencyType;
import org.eclipse.wst.common.componentcore.internal.Property;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.impl.ModuleURIUtil;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public class VirtualComponent extends VirtualContainer implements IVirtualComponent {

	public VirtualComponent(IProject aProject, String aName, IPath aRuntimePath) {
		super(aProject, aName, aRuntimePath);
	}

	public VirtualComponent(ComponentHandle aComponentHandle, IPath aRuntimePath) {
		super(aComponentHandle, aRuntimePath);
	}
	
	public IVirtualComponent getComponent() {
		return this;
	}
	
	public String getName() {
		return getComponentHandle().getName();
	}
	
	public boolean exists() { 
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForRead(getProject());
			WorkbenchComponent component = core.findComponentByName(getName()); 
			return component != null;
		} finally {
			if(core != null)
				core.dispose();
		}
		
	}
	
	public String getComponentTypeId() { 

		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForRead(getProject());
			WorkbenchComponent component = core.findComponentByName(getName()); 
			ComponentType cType = component.getComponentType();
			return cType != null ? cType.getComponentTypeId() : ""; 
		} finally {
			if(core != null)
				core.dispose();
		}
	}

	public void setComponentTypeId(String aComponentTypeId) {

		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = core.findComponentByName(getName()); 
			ComponentType cType = component.getComponentType();
			if(cType != null) {
				cType = ComponentcorePackage.eINSTANCE.getComponentcoreFactory().createComponentType();
				component.setComponentType(cType);
			}
			cType.setComponentTypeId(aComponentTypeId);
		} finally {
			if(core != null) {
				core.saveIfNecessary(null);
				core.dispose();
			}
		}
	}

	public Properties getMetaProperties() {
        StructureEdit core = null;
        try {
            core = StructureEdit.getStructureEditForRead(getProject());
            WorkbenchComponent component = core.findComponentByName(getName()); 
            ComponentType cType = component.getComponentType();
            Properties props = new Properties();
            if(cType != null) {
                List propList = cType.getProperties();
                if(propList != null) {
                    for (int i = 0; i < propList.size(); i++) {
                        props.setProperty(((Property)propList.get(i)).getName(), ((Property)propList.get(i)).getValue());
                    }
                }
            }
            return props; 
        } finally {
            if(core != null)
                core.dispose();
        }
	}

	public IPath[] getMetaResources() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setMetaResources(IPath[] theMetaResourcePaths) {
		// TODO Auto-generated method stub

	}
	
	public IVirtualResource[] getResources(String aResourceType) {
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForRead(getProject());
			WorkbenchComponent component = core.findComponentByName(getName());
			List currentResources = component.getResources();
			List foundResources = new ArrayList();
			
			for (Iterator iter = currentResources.iterator(); iter.hasNext();) {
				ComponentResource resource = (ComponentResource) iter.next();
				if(aResourceType == null || aResourceType.equals(resource.getResourceType())) {
					IVirtualResource vres = createVirtualResource(resource);
					if(vres != null)
						foundResources.add(vres);
				}
				
			}
			return (IVirtualResource[]) foundResources.toArray(new IVirtualResource[foundResources.size()]);
		} finally {
			if(core != null)
				core.dispose();
		}
	}

	private IVirtualResource createVirtualResource(ComponentResource aComponentResource) {
		IResource resource = StructureEdit.getEclipseResource(aComponentResource);
		switch(resource.getType()) {
			case IResource.FILE:
				return ComponentCore.createFile(getProject(), getName(), aComponentResource.getRuntimePath());
			case IResource.FOLDER:
				return ComponentCore.createFolder(getProject(), getName(), aComponentResource.getRuntimePath());
		}
		return null;
	}

	public IVirtualReference[] getReferences() { 
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForRead(getProject());
			WorkbenchComponent component = core.findComponentByName(getName());
			List referencedComponents = component.getReferencedComponents();
			ReferencedComponent referencedComponent = null;
			
			List references = new ArrayList();
			IVirtualComponent targetComponent = null;
			IProject targetProject = null;
			String targetComponentName = null;
			for (Iterator iter = referencedComponents.iterator(); iter.hasNext();) {
				referencedComponent = (ReferencedComponent) iter.next();
				try { 
					targetProject = StructureEdit.getContainingProject(referencedComponent.getHandle());
				} catch(UnresolveableURIException uurie) { } 
				// if the project cannot be resolved, assume it's local
				if(targetProject == null)
					targetProject = getProject();
				
				try {
					targetComponentName = StructureEdit.getDeployedName(referencedComponent.getHandle());
					targetComponent = ComponentCore.createComponent(targetProject, targetComponentName); 
					references.add(new VirtualReference(this, targetComponent, referencedComponent.getRuntimePath(), referencedComponent.getDependencyType().getValue()));
					
				} catch (UnresolveableURIException e) { 
				}
				 
			}
			
			return (IVirtualReference[]) references.toArray(new IVirtualReference[references.size()]);
		} finally {
			if(core != null)
				core.dispose();
		}		
	}

	public void setReferences(IVirtualReference[] references) { 
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = core.findComponentByName(getName());
			List referencedComponents = component.getReferencedComponents();
			ReferencedComponent referencedComponent = null;
			  
			component.getReferencedComponents().clear();
			ComponentcoreFactory factory = ComponentcorePackage.eINSTANCE.getComponentcoreFactory();
			for (int i=0; i<references.length; i++) {
				referencedComponent = factory.createReferencedComponent();				
				referencedComponent.setDependencyType(DependencyType.get(references[i].getDependencyType()));
				referencedComponent.setRuntimePath(references[i].getRuntimePath());
				referencedComponent.setHandle(ModuleURIUtil.fullyQualifyURI(references[i].getReferencedComponent().getProject(), references[i].getReferencedComponent().getName()));
				component.getReferencedComponents().add(referencedComponent);
			}
			 
		} finally {
			if(core != null) {
				core.saveIfNecessary(null);
				core.dispose();
			}
		}	
	}
	
	public boolean equals(Object anOther) { 
		if(anOther instanceof IVirtualComponent) {
			IVirtualComponent otherComponent = (IVirtualComponent) anOther;
			return getProject().equals(otherComponent.getProject()) && getName().equals(otherComponent.getName());
		}
		return false;
	}

	public IVirtualReference getReference(String aComponentName) {
		IVirtualReference[] refs = getReferences();
		for (int i = 0; i < refs.length; i++) {
			IVirtualReference reference = refs[i];
			if (reference.getReferencedComponent().getName().equals(aComponentName))
				return reference;
		}
		return null;
	}

	public String getVersion(){
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForRead(getProject());
			WorkbenchComponent component = core.findComponentByName(getName()); 
			ComponentType compType = component.getComponentType();
			if( compType != null)
				return compType.getVersion();
		} finally {
			if(core != null)
				core.dispose();
}
		return "";
	}
}
