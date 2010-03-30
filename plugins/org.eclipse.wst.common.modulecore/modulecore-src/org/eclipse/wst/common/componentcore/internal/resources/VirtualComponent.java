/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.internal.ComponentcoreFactory;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.internal.Property;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.builder.IDependencyGraph;
import org.eclipse.wst.common.componentcore.resolvers.IReferenceResolver;
import org.eclipse.wst.common.componentcore.resolvers.ReferenceResolverUtil;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.common.frameworks.internal.HashUtil;


public class VirtualComponent implements IVirtualComponent {
	IPath			runtimePath;
	IProject	componentProject;
	IVirtualFolder	rootFolder;
	String componentTypeId;
	private int flag = 0;
	
	// Cache variables
	String name = null;
	String deploymentName = null;
	Map <String, IVirtualReference> referenceMap = null;
	IVirtualReference[] referencesArray = null;
	Properties metaProperties = null;
	IPath[] cacheMetaResources = null;
	IVirtualReference[] allReferences = null;
	
	Resource projectResource;
	protected ResourceAdapter resourceAdapter = new ResourceAdapter();
	
	protected class ResourceAdapter extends AdapterImpl {
		public void notifyChanged(Notification notification) {
			if (notification.getFeatureID(null) == Resource.RESOURCE__IS_MODIFIED)
				resourceChanged((Resource) notification.getNotifier());
		}

		@Override
		public boolean equals(Object arg0) {
			
			return this.getClass().equals(arg0.getClass());
		}

		@Override
		public int hashCode() {
			
			return getClass().hashCode();
		}
	}
	
	protected void resourceChanged(Resource aResource) {
		clearCache();
	}
	
	private void addManagedResource() {
		if(projectResource == null)
			projectResource = createResource();
		if (projectResource != null && !projectResource.eAdapters().contains(resourceAdapter))
			projectResource.eAdapters().add(resourceAdapter);
	}
	
	protected void removeResource() {
		if(projectResource != null) {
			projectResource.eAdapters().remove(resourceAdapter);
			projectResource = null;
		}
	}
	
	protected void initializeResource() {
		projectResource = createResource();
		addManagedResource();
	}
	
	protected Resource createResource() {
		StructureEdit core = null;
		Resource res = null;
        try {
            core = StructureEdit.getStructureEditForRead(getProject());
            if (core == null)
            	return null;
            WorkbenchComponent component = core.getComponent();
            if(component != null)
            	res = component.eResource();
        } finally {
            if(core != null)
                core.dispose();
        }
        return res;
	}
	
	public void dispose() {
		removeResource();
	}
	
	protected VirtualComponent(){
	}
	
	public VirtualComponent(IProject aProject, IPath aRuntimePath) {
		if(aProject == null){
			throw new NullPointerException();
		}
		componentProject = aProject;
		runtimePath = aRuntimePath;
		rootFolder = ComponentCore.createFolder(componentProject, new Path("/")); //$NON-NLS-1$
		initializeResource();
	}
	
	public IVirtualComponent getComponent() {
		return this;
	}
	
	public String getName() {
		if(name == null){
			name = getProject().getName();
		}
		return name;
	}
	
	public String getDeployedName() {
		if(deploymentName != null) {
			return deploymentName;
		}
			
		StructureEdit core = null;
		IProject project = getProject();
		try {
			if (project != null && getName() != null) {
				core = StructureEdit.getStructureEditForRead(project);
				if(core != null && core.getComponent() != null){
					WorkbenchComponent component = core.getComponent();
					if (component.getName()!=null && component.getName().length()>0) {
						deploymentName = component.getName();
						return deploymentName;
					}
				}
			}
		} finally {
			if(core != null)
				core.dispose();
		}
		deploymentName = getProject().getName();
		return deploymentName;
	}
	
	public boolean exists() { 
		IProject project = getProject();
		return ModuleCoreNature.isFlexibleProject(project);
	}
	

	public Properties getMetaProperties() {
		if(metaProperties != null) {
			return metaProperties;
		}
		
        StructureEdit core = null;
        Properties props = new Properties();
        metaProperties = props;
        try {
            core = StructureEdit.getStructureEditForRead(getProject());
            if (core == null)
            	return props;
            WorkbenchComponent component = core.getComponent(); 
            if (component == null) return props;
            List propList = component.getProperties();
            if(propList != null) {
                for (int i = 0; i < propList.size(); i++) {
                	Property property = (Property)propList.get(i);
                	String name = property.getName();
                	String value = property.getValue();
                	if(value == null){
                		value = ""; //$NON-NLS-1$
                		String message = "WARNING:  The component file in "+getProject().getName()+" has no value defined for the property: "+name;  //$NON-NLS-1$//$NON-NLS-2$
                		ModulecorePlugin.logError(IStatus.ERROR, message, null);
                	}
                    props.setProperty(name, value);
                }
            }
            metaProperties =  props; 
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
		        clearCache();
			} 
        } finally {
            if(core != null){
            	core.saveIfNecessary(null);
                core.dispose();
            }
        }
	}
	
	public void clearMetaProperties() {
		StructureEdit core = null;
        try {
            core = StructureEdit.getStructureEditForWrite(getProject());
            WorkbenchComponent component = core.getComponent(); 
            component.getProperties().clear();
            clearCache();
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
			clearCache();            
        } finally {
            if(core != null){
            	core.saveIfNecessary(null);
                core.dispose();
            }
        }
	}
	
	public IPath[] getMetaResources() {
		if(cacheMetaResources != null)
			return cacheMetaResources;
		
		StructureEdit moduleCore = null;
		List metaResources = new ArrayList();
		try {
			moduleCore = StructureEdit.getStructureEditForRead(getProject());
			if (moduleCore != null) {
				WorkbenchComponent component = moduleCore.getComponent();
				if (component != null)
					metaResources.addAll(component.getMetadataResources());
			}
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
			}
		}
		cacheMetaResources = (IPath[]) metaResources.toArray(new IPath[metaResources.size()]);
		return cacheMetaResources;
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
				clearCache();
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

	public IVirtualReference[] getReferences(Map<String, Object> options) {
		if(referencesArray != null) {
			return referencesArray;
		}
		
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
						if (vReference != null && vReference.getReferencedComponent() != null && vReference.getReferencedComponent().exists())
							references.add(vReference); 
					}
				}
			}
			referencesArray = (IVirtualReference[]) references.toArray(new IVirtualReference[references.size()]);
			return referencesArray;
		} finally {
			if(core != null)
				core.dispose();
		}		
	}
	
	public IVirtualReference[] getReferences() { 
		HashMap<String, Object> map = new HashMap<String, Object>();
		return getReferences(map);
	}

	public void addReferences(IVirtualReference[] references) {
		if (references==null || references.length==0)
			return;
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForWrite(getProject());
			if (core == null)
				return;
			clearCache();
			WorkbenchComponent component = core.getComponent();
			ReferencedComponent referencedComponent = null;
			ComponentcoreFactory factory = ComponentcorePackage.eINSTANCE.getComponentcoreFactory();
			IReferenceResolver resolver = null;
			for (int i=0; i<references.length; i++) {
				if (references[i] == null)
					continue;
				resolver = ReferenceResolverUtil.getDefault().getResolver(references[i]);
				referencedComponent = resolver.resolve(references[i]);
				if (component != null)
					component.getReferencedComponents().add(referencedComponent);
			}
		} finally {
			if(core != null) {
				core.saveIfNecessary(null);
				core.dispose();
			}
		}	
	}
	
	public void setReferences(IVirtualReference[] references) { 
		StructureEdit core = null;
		try {
			core = StructureEdit.getStructureEditForWrite(getProject());
			WorkbenchComponent component = core.getComponent();
			ReferencedComponent referencedComponent = null;
			 
			clearCache();
			component.getReferencedComponents().clear();
			ComponentcoreFactory factory = ComponentcorePackage.eINSTANCE.getComponentcoreFactory();
			IReferenceResolver resolver = null;
			for (int i=0; i<references.length; i++) {
				resolver = ReferenceResolverUtil.getDefault().getResolver(references[i]);
				referencedComponent = resolver.resolve(references[i]);
				if( referencedComponent != null)
					component.getReferencedComponents().add(referencedComponent);
			}
			 
		} finally {
			if(core != null) {
				core.saveIfNecessary(null);
				core.dispose();
			}
		}	
	}

	public int hashCode() {
		if(componentProject == null) return super.hashCode();  //If component factory instance, don't use project to hash
		int hash = HashUtil.SEED;
		hash = HashUtil.hash(hash, getProject().getName());
		hash = HashUtil.hash(hash, getName());
		hash = HashUtil.hash(hash, isBinary());
		return hash;
	}
	
	public boolean equals(Object anOther) {
		if(componentProject == null) return super.equals(anOther);  //If component factory instance, don't use project to compare
		if(anOther == this) return true;
		if(anOther instanceof IVirtualComponent) {
			IVirtualComponent otherComponent = (IVirtualComponent) anOther;
			return getProject().equals(otherComponent.getProject()) && 
				   getName().equals(otherComponent.getName()) && 
				   isBinary() == otherComponent.isBinary();
		}
		return false;
	}

	public IVirtualReference getReference(String aComponentName) {
		if(referenceMap == null){
			referenceMap = new Hashtable<String, IVirtualReference>();
		} else if(referenceMap.containsKey(aComponentName)) {
			return (IVirtualReference) referenceMap.get(aComponentName);
		}
		
		IVirtualReference[] refs = getReferences();
		for (int i = 0; i < refs.length; i++) {
			IVirtualReference reference = refs[i];
			if( reference.getReferencedComponent() != null ){
				if (reference.getReferencedComponent().getName().equals(aComponentName)) {
					referenceMap.put(aComponentName, reference);
					return reference;
				}
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
		Set<IProject> projects = IDependencyGraph.INSTANCE.getReferencingComponents(getProject());
		IVirtualComponent[] result = new IVirtualComponent[projects.size()];
		Iterator<IProject> i = projects.iterator();
		for (int j=0; j<projects.size(); j++)
			result[j] = ComponentCore.createComponent(i.next());
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
			if(aReference == null)
				return;
			core = StructureEdit.getStructureEditForWrite(getProject());
			if (core == null) {
				return;
			}
			clearCache();
			WorkbenchComponent component = core.getComponent();
			ReferencedComponent refComponent = getWorkbenchReferencedComponent(aReference, component);
			if (component != null && refComponent != null) {
				component.getReferencedComponents().remove(refComponent);
			}
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
		URI uri = ReferenceResolverUtil.getDefault().getResolver(aReference).resolve(aReference).getHandle(); 
		for (int i=0; i<referencedComponents.size(); i++) {
			ReferencedComponent ref = (ReferencedComponent) referencedComponents.get(i);
			if( ref.getHandle().equals(uri))
				return ref;
		}
		return null;
	}
	
	
	/**
	 * @return IVirtualReference[] - All the references of this component, including potentially deleted references
	 */
	public IVirtualReference[] getAllReferences() { 
		if(allReferences != null)
			return allReferences;
		
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
						if (vReference != null && vReference.getReferencedComponent() != null)
							references.add(vReference); 
					}
				}
			}
			allReferences = (IVirtualReference[]) references.toArray(new IVirtualReference[references.size()]);
			return allReferences;
		} finally {
			if(core != null)
				core.dispose();
		}		
	}
	
	protected void clearCache() {
		name = null;
		deploymentName = null;
		if(referenceMap != null) {
			referenceMap.clear();
		}
		referencesArray = null;
		metaProperties = null;
		cacheMetaResources = null;
		allReferences = null;
	}
		
	public String toString() {
		return componentProject.toString();
	}
}
