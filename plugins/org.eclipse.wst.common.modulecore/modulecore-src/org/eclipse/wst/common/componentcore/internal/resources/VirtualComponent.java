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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.ComponentcoreFactory;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.DependencyType;
import org.eclipse.wst.common.componentcore.internal.Property;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.builder.DependencyGraphManager;
import org.eclipse.wst.common.componentcore.internal.impl.ModuleURIUtil;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;


public class VirtualComponent implements IVirtualComponent {
	IPath			runtimePath;
	IProject	componentProject;
	IVirtualFolder	rootFolder;
	String componentTypeId;
	private int flag = 0;
	

	public VirtualComponent(IProject aProject, IPath aRuntimePath) {
		componentProject = aProject;
		runtimePath = aRuntimePath;
		rootFolder = ComponentCore.createFolder(componentProject, new Path("/")); //$NON-NLS-1$
	}
	
	public IVirtualComponent getComponent() {
		return this;
	}
	
	public String getName() {
		return getProject().getName();
	}
	
	public String getDeployedName() {
		StructureEdit core = null;
		IProject project = getProject();
		try {
			if (project != null && getName() != null) {
				core = StructureEdit.getStructureEditForRead(project);
				if(core != null && core.getComponent() != null){
					WorkbenchComponent component = core.getComponent();
					if (component.getName()!=null && component.getName().length()>0)
						return component.getName();
				}
			}
		} finally {
			if(core != null)
				core.dispose();
		}
		return getProject().getName();
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
				WorkbenchComponent component = core.getComponent(); 
				return component != null;
			}
		} finally {
			if(core != null)
				core.dispose();
		}
		return false;
	}
	
//	public String getComponentTypeId() {
//		if (null == componentTypeId) {
//			StructureEdit core = null;
//			try {
//				if (getProject() == null || getName() == null)
//					return null;
//				core = StructureEdit.getStructureEditForRead(getProject());
//				if (core == null)
//					return null;
//				WorkbenchComponent component = core.getComponent();
//				ComponentType cType = component == null ? null : component.getComponentType();
//				componentTypeId = cType == null ? null : cType.getComponentTypeId();
//			} finally {
//				if (core != null)
//					core.dispose();
//			}
//		}
//		return componentTypeId;
//	}

//	public void setComponentTypeId(String aComponentTypeId) {
//
//		StructureEdit core = null;
//		try {
//			core = StructureEdit.getStructureEditForWrite(getProject());
//			WorkbenchComponent component = core.getComponent(); 
//			ComponentType cType = component.getComponentType();
//			if(cType == null) {
//				cType = ComponentcorePackage.eINSTANCE.getComponentcoreFactory().createComponentType();
//				component.setComponentType(cType);
//			}
//			cType.setComponentTypeId(aComponentTypeId);
//		} finally {
//			if(core != null) {
//				core.saveIfNecessary(null);
//				core.dispose();
//			}
//		}
//	}

	public Properties getMetaProperties() {
        StructureEdit core = null;
        Properties props = new Properties();
        try {
            core = StructureEdit.getStructureEditForRead(getProject());
            if (core == null)
            	return props;
            WorkbenchComponent component = core.getComponent(); 
            if (component == null) return props;
            List propList = component.getProperties();
            if(propList != null) {
                for (int i = 0; i < propList.size(); i++) {
                    props.setProperty(((Property)propList.get(i)).getName(), ((Property)propList.get(i)).getValue());
                }
            }
            return props; 
        } finally {
            if(core != null)
                core.dispose();
        }
	}

	public void setMetaProperties(Properties properties) {
        StructureEdit core = null;
        try {
            core = StructureEdit.getStructureEditForWrite(getProject());
            WorkbenchComponent component = core.getComponent(); 
            
            List propList = component.getProperties();
			if (properties != null && !properties.isEmpty()) {
		        for(Enumeration itr = properties.keys(); itr.hasMoreElements();) {
		            final String key = (String) itr.nextElement();
		            final Property prop = ComponentcoreFactory.eINSTANCE.createProperty();
		            prop.setName(key);
		            prop.setValue(properties.getProperty(key));
		            // Remove existing property first
		            for (int i=0; i<propList.size(); i++) {
		            	Property existing = (Property) propList.get(i);
		            	if (existing.getName().equals(key)) {
		            		propList.remove(existing);
		            		break;
		            	}
		            }
		            // Add new property
		            propList.add(prop);
		         }
			} 
        } finally {
            if(core != null){
            	core.saveIfNecessary(null);
                core.dispose();
            }
        }
	}
	
	public void setMetaProperty(String key, String value) {
        StructureEdit core = null;
        try {
            core = StructureEdit.getStructureEditForWrite(getProject());
            WorkbenchComponent component = core.getComponent();
            //Remove existing property first
            List properties = component.getProperties();
            for (int i=0; i<properties.size(); i++) {
            	Property existing = (Property) properties.get(i);
            	if (existing.getName().equals(key)) {
            		properties.remove(existing);
            		break;
            	}
            }
        	//Set new property
            final Property prop = ComponentcoreFactory.eINSTANCE.createProperty();
			prop.setName(key);
			prop.setValue(value);
			component.getProperties().add(prop);
            
        } finally {
            if(core != null){
            	core.saveIfNecessary(null);
                core.dispose();
            }
        }
	}
	
	public IPath[] getMetaResources() {
		StructureEdit moduleCore = null;
		List metaResources = new ArrayList();
		try {
			moduleCore = StructureEdit.getStructureEditForRead(getProject());
			WorkbenchComponent component = moduleCore.getComponent();
			if (component != null)
				metaResources.addAll(component.getMetadataResources());
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
			}
		}
		return (IPath[]) metaResources.toArray(new IPath[metaResources.size()]);
	}

	public void setMetaResources(IPath[] theMetaResourcePaths) {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = moduleCore.getComponent();
			if (component != null) {
				for (int i=0; i<theMetaResourcePaths.length; i++) {
					if (!component.getMetadataResources().contains(theMetaResourcePaths[i]))
						component.getMetadataResources().add(theMetaResourcePaths[i]);
				}
			}
		} finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(null);
				moduleCore.dispose();
			}
		}
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
					.getComponent();
			if (component == null)
				component = moduleCore
						.createWorkbenchModule(getProject().getName());
				
		} finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(null);
				moduleCore.dispose();
			}
		}
	}	

	public IVirtualReference[] getReferences() { 
		StructureEdit core = null;
		List references = new ArrayList();
		try {
			core = StructureEdit.getStructureEditForRead(getProject());
			if (core!=null && core.getComponent()!=null) {
				WorkbenchComponent component = core.getComponent();
				if (component!=null) {
					List referencedComponents = component.getReferencedComponents();
					for (Iterator iter = referencedComponents.iterator(); iter.hasNext();) {
						ReferencedComponent referencedComponent = (ReferencedComponent) iter.next();
						if (referencedComponent==null) 
							continue;
						IVirtualReference vReference = StructureEdit.createVirtualReference(this, referencedComponent);
						if( vReference != null ){
							vReference.setArchiveName( referencedComponent.getArchiveName() );
						}
						if (vReference != null && vReference.getReferencedComponent() != null && vReference.getReferencedComponent().exists())
							references.add(vReference); 
					}
				}
			}
			return (IVirtualReference[]) references.toArray(new IVirtualReference[references.size()]);
		} finally {
			if(core != null)
				core.dispose();
		}		
	}

	public void addReferences(IVirtualReference[] references) {
		if (references==null || references.length==0)
			return;
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForWrite(getProject());
			if (core == null)
				return;
			WorkbenchComponent component = core.getComponent();
			ReferencedComponent referencedComponent = null;
			ComponentcoreFactory factory = ComponentcorePackage.eINSTANCE.getComponentcoreFactory();
			for (int i=0; i<references.length; i++) {
				if (references[i] == null)
					continue;
				referencedComponent = factory.createReferencedComponent();				
				referencedComponent.setDependencyType(DependencyType.get(references[i].getDependencyType()));
				referencedComponent.setRuntimePath(references[i].getRuntimePath());

				IVirtualComponent comp = references[i].getReferencedComponent();
				if(comp!=null && !comp.isBinary())
					referencedComponent.setHandle(ModuleURIUtil.fullyQualifyURI(comp.getProject()));
				else if (comp!=null)
					referencedComponent.setHandle(ModuleURIUtil.archiveComponentfullyQualifyURI(comp.getName()));
				if (component != null)
					component.getReferencedComponents().add(referencedComponent);
				referencedComponent.setArchiveName(references[i].getArchiveName());
			}
			//clean up any old obsolete references
			cleanUpReferences(component);	
		} finally {
			if(core != null) {
				core.saveIfNecessary(null);
				core.dispose();
			}
		}	
	}
	
	private void cleanUpReferences(WorkbenchComponent component) {
		List referencedComponents = component.getReferencedComponents();
		for (Iterator iter = referencedComponents.iterator(); iter.hasNext();) {
			ReferencedComponent referencedComponent = (ReferencedComponent) iter.next();
			if (referencedComponent==null) 
				continue;
			IVirtualReference vReference = StructureEdit.createVirtualReference(this, referencedComponent);
			if (vReference == null || vReference.getReferencedComponent() == null || !vReference.getReferencedComponent().exists())
				component.getReferencedComponents().remove(referencedComponent); 
		}
	}
	
	public void setReferences(IVirtualReference[] references) { 
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = core.getComponent();
			ReferencedComponent referencedComponent = null;
			  
			component.getReferencedComponents().clear();
			ComponentcoreFactory factory = ComponentcorePackage.eINSTANCE.getComponentcoreFactory();
			for (int i=0; i<references.length; i++) {
				referencedComponent = factory.createReferencedComponent();				
				referencedComponent.setDependencyType(DependencyType.get(references[i].getDependencyType()));
				referencedComponent.setRuntimePath(references[i].getRuntimePath());

				IVirtualComponent comp = references[i].getReferencedComponent();
				if( !comp.isBinary())
					referencedComponent.setHandle(ModuleURIUtil.fullyQualifyURI(references[i].getReferencedComponent().getProject()));
				else
					referencedComponent.setHandle(ModuleURIUtil.archiveComponentfullyQualifyURI(references[i].getReferencedComponent().getName()));
				
				referencedComponent.setArchiveName(references[i].getArchiveName());
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
			return getProject() !=null && 
					getProject().equals(otherComponent.getProject()) && 
					getName().equals(otherComponent.getName()) && 
					isBinary() == otherComponent.isBinary();
		}
		return false;
	}

	public IVirtualReference getReference(String aComponentName) {
		IVirtualReference[] refs = getReferences();
		for (int i = 0; i < refs.length; i++) {
			IVirtualReference reference = refs[i];
			if( reference.getReferencedComponent() != null ){
				if (reference.getReferencedComponent().getName().equals(aComponentName))
					return reference;
			}
		}
		return null;
	}
	
	public Object getAdapter(Class adapterType) {
		return Platform.getAdapterManager().getAdapter(this, adapterType);
	}

	public IVirtualFolder getRootFolder() {
		return rootFolder;
	}

	public IProject getProject() {
		return componentProject;
	}
	
	/**
	 * Return all components which have a reference to the passed in target component.
	 * 
	 * @param target
	 * @return array of components
	 */
	public IVirtualComponent[] getReferencingComponents() {
		IProject[] handles =  DependencyGraphManager.getInstance().getDependencyGraph().getReferencingComponents(getProject());
		IVirtualComponent[] result = new IVirtualComponent[handles.length];
		for (int i=0; i<handles.length; i++)
			result[i] = ComponentCore.createComponent(handles[i]);
		return result;
	}
	
	/**
	 * Remove the associated ReferencedComponent for the virtual reference from the workbench component 
	 * associated with this virtual component
	 * 
	 * @param aReference
	 */
	public void removeReference(IVirtualReference aReference) {
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForWrite(getProject());
			if (core == null || aReference == null)
				return;
			WorkbenchComponent component = core.getComponent();
			ReferencedComponent refComponent = getWorkbenchReferencedComponent(aReference, component);
			if (component != null && refComponent != null)
				component.getReferencedComponents().remove(refComponent);
		} finally {
			if(core != null) {
				core.saveIfNecessary(null);
				core.dispose();
			}
		}	
	}
	
	/**
	 * Return the associated structure edit ReferencedComponent object for the given IVirtualReference based on the handle
	 * and module URI.
	 * 
	 * @param aReference
	 * @param core
	 * @return ReferencedComponent
	 */
	protected ReferencedComponent getWorkbenchReferencedComponent(IVirtualReference aReference, WorkbenchComponent component) {
		if (aReference == null || aReference.getReferencedComponent() == null || component == null)
			return null;
		List referencedComponents = component.getReferencedComponents();
		for (int i=0; i<referencedComponents.size(); i++) {
			ReferencedComponent ref = (ReferencedComponent) referencedComponents.get(i);
			if (!aReference.getReferencedComponent().isBinary()) {
				if (ref.getHandle().equals(ModuleURIUtil.fullyQualifyURI(aReference.getReferencedComponent().getProject())))
					return ref;	
			} 
			else {
				if (ref.getHandle().equals(ModuleURIUtil.archiveComponentfullyQualifyURI(aReference.getReferencedComponent().getName())))
					return ref;
			}	
		}
		return null;
	}
}
