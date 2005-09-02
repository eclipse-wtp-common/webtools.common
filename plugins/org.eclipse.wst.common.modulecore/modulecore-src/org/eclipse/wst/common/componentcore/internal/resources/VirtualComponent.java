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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.ComponentType;
import org.eclipse.wst.common.componentcore.internal.ComponentcoreFactory;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.DependencyType;
import org.eclipse.wst.common.componentcore.internal.Property;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.builder.DependencyGraphManager;
import org.eclipse.wst.common.componentcore.internal.impl.ModuleURIUtil;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;


public class VirtualComponent implements IVirtualComponent {
	IPath			runtimePath;
	ComponentHandle	componentHandle;
	IVirtualFolder	rootFolder;
	String componentTypeId;
	private int flag = 0;
	
	public VirtualComponent(IProject aProject, String aName, IPath aRuntimePath) {
		this(ComponentHandle.create(aProject, aName), aRuntimePath);
	}

	public VirtualComponent(ComponentHandle aComponentHandle, IPath aRuntimePath) {
		componentHandle = aComponentHandle;
		runtimePath = aRuntimePath;
		rootFolder = ComponentCore.createFolder(componentHandle.getProject(), componentHandle.getName(), new Path("/"));
	}
	
	public IVirtualComponent getComponent() {
		return this;
	}
	
	public String getName() {
		return getComponentHandle().getName();
	}
	
	public boolean exists() { 
		StructureEdit core = null;
		IProject project = getProject();
		try {
			if (project != null && getName() != null) {
				core = StructureEdit.getStructureEditForRead(project);
				if(core == null){
					return false;
				}
				WorkbenchComponent component = core.findComponentByName(getName()); 
				return component != null;
			}
		} finally {
			if(core != null)
				core.dispose();
		}
		return false;
	}
	
	public String getComponentTypeId() {
		if (null == componentTypeId) {
			StructureEdit core = null;
			try {
				if (getProject() == null || getName() == null)
					return null;
				core = StructureEdit.getStructureEditForRead(getProject());
				if (core == null)
					return null;
				WorkbenchComponent component = core.findComponentByName(getName());
				ComponentType cType = component == null ? null : component.getComponentType();
				componentTypeId = cType == null ? null : cType.getComponentTypeId();
			} finally {
				if (core != null)
					core.dispose();
			}
		}
		return componentTypeId;
	}

	public void setComponentTypeId(String aComponentTypeId) {

		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = core.findComponentByName(getName()); 
			ComponentType cType = component.getComponentType();
			if(cType == null) {
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

	public void setMetaProperties(List properties) {
        StructureEdit core = null;
        try {
            core = StructureEdit.getStructureEditForWrite(getProject());
            WorkbenchComponent component = core.findComponentByName(getName()); 
            ComponentType cType = component.getComponentType();
			if(cType == null) {
				cType = ComponentcorePackage.eINSTANCE.getComponentcoreFactory().createComponentType();
				component.setComponentType(cType);
			}            
            if(cType != null) {
                List propList = cType.getProperties();
                if (properties != null && !properties.isEmpty()) {  
                    for (int i = 0; i < properties.size(); i++) {
                    	propList.add(properties.get(i));
                    }
                }
            }
        } finally {
            if(core != null){
            	core.saveIfNecessary(null);
                core.dispose();
            }
        }
	}	
	
	public void addMetaProperty(Property property) {
        StructureEdit core = null;
        try {
            core = StructureEdit.getStructureEditForWrite(getProject());
            WorkbenchComponent component = core.findComponentByName(getName()); 
            ComponentType cType = component.getComponentType();
			if(cType == null) {
				cType = ComponentcorePackage.eINSTANCE.getComponentcoreFactory().createComponentType();
				component.setComponentType(cType);
			}              
            if(cType != null) {
                List propList = cType.getProperties();
                propList.add(property);
            }
        } finally {
            if(core != null){
            	core.saveIfNecessary(null);
                core.dispose();
            }
        }
	}	
	public IPath[] getMetaResources() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setMetaResources(IPath[] theMetaResourcePaths) {
		// TODO Auto-generated method stub

	}
	
	public int getType() {
		return IVirtualResource.COMPONENT;
	}
	

	public boolean isBinary(){
		boolean ret =  (flag & BINARY) == 1  ? true :false;
		return ret;		
	}	

	public void create(int updateFlags, IProgressMonitor aMonitor)
	throws CoreException {

		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = moduleCore
					.findComponentByName(getComponentHandle().getName());
			if (component == null)
				component = moduleCore
						.createWorkbenchModule(getComponentHandle().getName());
				
		} finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(null);
				moduleCore.dispose();
			}
		}
	}	

	public IVirtualReference[] getReferences() { 
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForRead(getProject());
			WorkbenchComponent component = core.findComponentByName(getName());
			List referencedComponents = component.getReferencedComponents();
			ReferencedComponent referencedComponent = null;
			
			List references = new ArrayList();
			for (Iterator iter = referencedComponents.iterator(); iter.hasNext();) {
				referencedComponent = (ReferencedComponent) iter.next();
				IVirtualReference vReference = StructureEdit.createVirtualReference(this, referencedComponent);
				if(vReference != null)
					references.add(vReference);
				 
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

				IVirtualComponent comp = references[i].getReferencedComponent();
				if( !comp.isBinary())
					referencedComponent.setHandle(ModuleURIUtil.fullyQualifyURI(references[i].getReferencedComponent().getProject(), references[i].getReferencedComponent().getName()));
				else
					referencedComponent.setHandle(ModuleURIUtil.archiveComponentfullyQualifyURI(references[i].getReferencedComponent().getName()));
				
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
			return getProject()!=null && getProject().equals(otherComponent.getProject()) && getName().equals(otherComponent.getName());
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

	public ComponentHandle getComponentHandle() {
		return componentHandle;
	}

	public IVirtualFolder getRootFolder() {
		return rootFolder;
	}

	public IProject getProject() {
		return componentHandle.getProject();
	}
	
	/**
	 * Return all components which have a reference to the passed in target component.
	 * 
	 * @param target
	 * @return array of components
	 */
	public IVirtualComponent[] getReferencingComponents() {
		ComponentHandle[] handles =  DependencyGraphManager.getInstance().getDependencyGraph().getReferencingComponents(this.getComponentHandle());
		IVirtualComponent[] result = new IVirtualComponent[handles.length];
		for (int i=0; i<handles.length; i++)
			result[i] = ComponentCore.createComponent(handles[i].getProject(),handles[i].getName());
		return result;
	}

}
