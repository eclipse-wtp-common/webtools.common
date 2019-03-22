/*******************************************************************************
 * Copyright (c) 2003, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.jem.util.emf.workbench.ISynchronizerExtender;
import org.eclipse.jem.util.emf.workbench.ProjectResourceSet;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.internal.resources.ResourceTimestampMappings;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualComponent;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.internal.emfworkbench.edit.EMFWorkbenchEditContextFactory;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;



public class ComponentImplManager implements ISynchronizerExtender{

	private static final String NO_FACETS = "NONE";//$NON-NLS-1$

	private static final String COMPONENT_IMPL_EXTENSION_POINT = "componentimpl"; //$NON-NLS-1$
	private static final String TAG_COMPONENT_IMPL = "componentimpl"; //$NON-NLS-1$
	private static final String ATT_TYPE = "typeID"; //$NON-NLS-1$
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$
	private static final String EXTENDED_ELEMENT="extendedTypeID"; //$NON-NLS-1$

	private static final ComponentImplManager instance = new ComponentImplManager();
//	private static final Object LOAD_FAILED = new Object();

	private final Map/* <String, ComponentImplDescriptor> */ descriptors = new Hashtable();

	private final Map/* <ComponentImplDescriptor, IComponentImplFactory> */ instances = new Hashtable();
	private ArrayList<HashSet> sortedDescriptorsKeyList = new ArrayList<HashSet>();

	/**
	 * @return Returns the instance.
	 */
	public static ComponentImplManager instance() {
		/* already initialized and registry read by the time the class initializes */
		return instance;
	}

	public ComponentImplManager() {
		SafeRunner.run(new ISafeRunnable() {

			public void handleException(Throwable exception) {
				ModulecorePlugin.logError(0, exception.getMessage(), exception);
			}

			public void run() throws Exception {
				new ComponentImplRegistryReader().readRegistry();
				//sort descriptors keys by size, bigger size is at lower index
				if( descriptors != null ){

					sortedDescriptorsKeyList.addAll(descriptors.keySet());
					
					final Comparator<HashSet> comp = new Comparator<HashSet>() {
						public int compare(final HashSet descriptor1, final HashSet descriptor2) {
							if( descriptor1.size() == descriptor2.size() )
								return 0;
							return descriptor1.size() < descriptor2.size() ? 1 : -1;
						}
					};
					Collections.sort(sortedDescriptorsKeyList, comp);
				}
			}
		});
	}
	
	private IComponentImplFactory getComponentImplFactory(HashSet typeID) {

		ComponentImplDescriptor descriptor = (ComponentImplDescriptor) descriptors.get(typeID);
		IComponentImplFactory factory = null;

		if (descriptor != null) {

			factory = (IComponentImplFactory) instances.get(descriptor);

			if (factory == null) {

				if ((factory = descriptor.createFactory()) != null) {
					instances.put(descriptor, factory);
				} else {
					descriptors.remove(descriptor);
				}
			}
		}
		return factory;
	}
	
	

	private IComponentImplFactory findFactoryForProject(IProject project, Map descriptors){
		try {
			IComponentImplFactory factory = ComponentCacheManager.instance().getComponentImplFactory(project);

			if(factory != null)
				return factory;

			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			if (facetedProject == null){
				HashSet set = new HashSet();
				set.add(NO_FACETS);
				factory = getComponentImplFactory(set);
				ComponentCacheManager.instance().setComponentImplFactory(project, factory);
				return factory;
			}
			return findFactorySupportingFacetsForProject(facetedProject);

		} catch (Exception e) {
			ModulecorePlugin.logError(0, "Returning null factory for project: " + project, e); //$NON-NLS-1$
			ComponentCacheManager.instance().markErrorComponentImplFactory(project);
		}
		return null;
	}
	
		
	private IComponentImplFactory findFactorySupportingFacetsForProject(IFacetedProject facetedProject){
		Set<IProjectFacetVersion> projectFacets = facetedProject.getProjectFacets();

		IComponentImplFactory factory = null;	

		if( sortedDescriptorsKeyList != null ){
			for( HashSet key : sortedDescriptorsKeyList ){
				
				boolean invalidDescriptor = false;
				Iterator iterator = key.iterator();

				while (iterator.hasNext()) {

					String typeID = (String) iterator.next();
					IProjectFacet projectFacet = null;
					try{
						projectFacet = ProjectFacetsManager.getProjectFacet(typeID);
					}catch(Exception e){
						invalidDescriptor = true;
						break;
					}
					//if a project does not have a facet supported by this type, return 
					if (projectFacet != null && !facetedProject.hasProjectFacet(projectFacet)){
						invalidDescriptor = true;
						break;
					}
				}
				if( !invalidDescriptor ){
					factory = getComponentImplFactory(key);
					if(null != factory){
						ComponentCacheManager.instance().setComponentImplFactory(facetedProject.getProject(), factory);
						return factory;
					}
				}
			}
		}
		return null;
	}

	
	
	public IVirtualFolder createFolder(IProject aProject, IPath aRuntimePath){
		try {
			IComponentImplFactory factory = findFactoryForProject(aProject, descriptors);
			if(null != factory){
				return factory.createFolder(aProject, aRuntimePath);
			}
		} catch (Exception e) {
			// Just return a default folder
		}
		ComponentCacheManager.instance().setComponentImplFactory(aProject, null);
		return new VirtualFolder(aProject, aRuntimePath);
	}

	public IVirtualComponent createComponent(IProject project) {
		return createComponent(project, true);
	}

	public IVirtualComponent createComponent(IProject project, boolean checkSettings) {
		// acquire the lock that StructureEdit will need already, to prevent others from locking that before calling createComponent() - see bug 508685 and bug 511793
		ILock lock = EMFWorkbenchEditContextFactory.getProjectLockObject(project);
		try{
			if(null != lock){
				lock.acquire();
			}
			return createComponentSynchronously(project, checkSettings);
		} finally{
			if(null != lock){
				lock.release();
			}
		}
	}

	private synchronized IVirtualComponent createComponentSynchronously(IProject project, boolean checkSettings) {
		try {
			IVirtualComponent component = ComponentCacheManager.instance().getComponent(project);
			if(component != null) {
				return component;
			}		

			IComponentImplFactory factory = findFactoryForProject(project, descriptors);
			if(null != factory){
				component = factory.createComponent(project);
				if(component != null) {
					ComponentCacheManager.instance().setComponent(project, component);
					registerListener(project);
				}
				return component;
			}
		} catch (Exception e) {
			// Just return a default component
		}
		if(checkSettings) {
			if (!ModuleCoreNature.isFlexibleProject(project)){
				return null;
			}
		}
		else {
			if (ModuleCoreNature.getModuleCoreNature(project) == null){
				return null;
			}
		}
		IVirtualComponent component = new VirtualComponent(project, new Path("/")); //$NON-NLS-1$
		if(component != null) {
			ComponentCacheManager.instance().setComponentImplFactory(project, null);
			ComponentCacheManager.instance().setComponent(project, component);
			registerListener(project);
		}

		return component;
	}

	public IVirtualComponent createArchiveComponent(IProject aProject, String aComponentName) {
		return createArchiveComponent(aProject, aComponentName, new Path("/"));
	}
	
	public synchronized IVirtualComponent createArchiveComponent(IProject aProject, String aComponentName, IPath path) {
		path = path == null ? new Path("/") : path; //$NON-NLS-1$
		try {
			IVirtualComponent component = ComponentCacheManager.instance().getArchiveComponent(aProject, aComponentName);
			if(component != null)
				return component;

			if(!ComponentCacheManager.instance().isValidComponentImplFactory(aProject)) {
				registerListener(aProject);
			}

			IComponentImplFactory factory = findFactoryForProject(aProject, descriptors);
			if(null != factory){
				IVirtualComponent archiveComponent = factory.createArchiveComponent(aProject, aComponentName, path);
				ComponentCacheManager.instance().setArchiveComponent(aProject, aComponentName, archiveComponent);
				return archiveComponent;
			}
		} catch (Exception e) {
			// Just return a default archive component
		}
		ComponentCacheManager.instance().setComponentImplFactory(aProject, null);
		IVirtualComponent archiveComponent = new VirtualArchiveComponent(aProject, aComponentName, path); //$NON-NLS-1$
		ComponentCacheManager.instance().setArchiveComponent(aProject, aComponentName, archiveComponent);
		return archiveComponent;
	}
	
	private void registerListener(IProject aProject) {
		ProjectResourceSet resSet = getResourceSet(aProject);
		if (resSet == null || resSet.getSynchronizer() == null)
			return;
		resSet.getSynchronizer().addExtender(this);
	}

	protected ProjectResourceSet getResourceSet(IProject proj) {
		return (ProjectResourceSet)WorkbenchResourceHelperBase.getResourceSet(proj);
	}

	public void projectChanged(IResourceDelta delta) {
		// TODO Auto-generated method stub

	}

	public synchronized void projectClosed() {
		ComponentCacheManager.instance().clearCache();
	}

	private static class ComponentCacheManager  {
		private static final ComponentCacheManager instance = new ComponentCacheManager();

		private final ResourceTimestampMappings factoryMap = new ResourceTimestampMappings();	
		private final Map <IProject , IVirtualComponent> componentsMap = new Hashtable<IProject , IVirtualComponent>();
		private final Map <IProject , Map<String, IVirtualComponent>> componentsArchivesMap = new Hashtable<IProject , Map<String, IVirtualComponent>>();

		private Object cacheLock = new Object();

		public ComponentCacheManager() {}

		public static ComponentCacheManager instance() {
			return instance;
		}

		public IComponentImplFactory getComponentImplFactory(IProject project) {
			synchronized (cacheLock) {
				if(isValidComponentImplFactory(project)) {
					Object data = factoryMap.getData(project);
					if(data instanceof IComponentImplFactory)
						return (IComponentImplFactory) data;
				}
				return null;
			}
		}

		public boolean isValidComponentImplFactory(IProject project) {
			synchronized (cacheLock) {
				if(!factoryMap.hasChanged(project) && !factoryMap.hasCacheError(project) && factoryMap.hasCacheData(project))
					return true;
				return false;
			}
		}

		public void setComponentImplFactory(IProject project, IComponentImplFactory factory){
			synchronized (cacheLock) {
				if(factory != null) {
					factoryMap.mark(project, factory);
				}
				else {
					factoryMap.mark(project, project);
				}
			}
		}

		public void markErrorComponentImplFactory(IProject project){
			synchronized (cacheLock) {
				factoryMap.markError(project);
			}
		}

		public IVirtualComponent getComponent(IProject project) {
			synchronized (cacheLock) {
				if(componentsMap.containsKey(project)) {
					if(isValidComponentImplFactory(project)) {
						return componentsMap.get(project);
					} else {
						IVirtualComponent component = componentsMap.remove(project);
						if(component instanceof VirtualComponent)
							((VirtualComponent)component).dispose();
					}
				}
				return null;
			}
		}

		public void setComponent(IProject project, IVirtualComponent component) {
			synchronized (cacheLock) {
				if(component != null)
					componentsMap.put(project, component);
			}
		}

		public IVirtualComponent getArchiveComponent(IProject project, String componentName) {
			synchronized (cacheLock) {
				Map archives = getComponentArchives(project);			
				if(isValidComponentImplFactory(project)) {
					if(archives.containsKey(componentName)) {
						return (IVirtualComponent) archives.get(componentName);
					}
				}
				else {
					archives = new Hashtable<String, IVirtualComponent>();
					componentsArchivesMap.put(project, archives);
				}
				return null;
			}
		}

		public Map getComponentArchives(IProject project) {
			synchronized (cacheLock) {
				Map archives = componentsArchivesMap.get(project);
				if(archives == null) {
					archives = new Hashtable<String, IVirtualComponent>();
					componentsArchivesMap.put(project, archives);
				}
				return archives;
			}
		}

		public void setArchiveComponent(IProject project, String componentName, IVirtualComponent archiveComponent) {
			synchronized (cacheLock) {
				if(archiveComponent != null) {
					Map archives = ComponentCacheManager.instance().getComponentArchives(project);
					archives.put(componentName, archiveComponent);
				}
			}
		}


		public void clearCache() {
			Object[] components = null;
			Object[] componentsArchives = null;
			synchronized (cacheLock) {
				components = (Object[]) componentsMap.values().toArray();
				componentsMap.clear();
				
				componentsArchives = (Object[]) componentsArchivesMap.values().toArray();
				componentsArchivesMap.clear();
			}
			
			for(int i = 0; i < components.length; i++) {
				if(components[i] instanceof VirtualComponent)
					((VirtualComponent)components[i]).dispose();
			}
			
			for(int i = 0; i < componentsArchives.length; i++) {
				if(componentsArchives[i] instanceof VirtualComponent)
					((VirtualComponent)componentsArchives[i]).dispose();
			}
		}
	}

	private class ComponentImplDescriptor {

		private final IConfigurationElement element;
		
		public ComponentImplDescriptor(IConfigurationElement configElement) {
			element = configElement;
		}

		/**
		 * Create and return an {@link IArtifactEditFactory} for the given descriptor or <b>null</b>
		 * if there are problems instantiating the extension.
		 * 
		 * @return An {@link IArtifactEditFactory} for the given descriptor or <b>null</b> if there
		 *         are problems instantiating the extension.
		 */
		public IComponentImplFactory createFactory() {

			final IComponentImplFactory[] factory = new IComponentImplFactory[1];

			SafeRunner.run(new ISafeRunnable() {

				public void handleException(Throwable exception) {
					ModulecorePlugin.logError(0, exception.getMessage(), exception);
				}

				public void run() throws Exception {
					factory[0] = (IComponentImplFactory) element.createExecutableExtension(ATT_CLASS);
				}

			});

			return factory[0];
		}
	}
	
	private class ComponentImplRegistryReader extends RegistryReader {

		public ComponentImplRegistryReader() {
			super(ModulecorePlugin.PLUGIN_ID, COMPONENT_IMPL_EXTENSION_POINT);
		} 

		/**
		 * @see org.eclipse.wst.common.frameworks.internal.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
		 */
		public boolean readElement(IConfigurationElement element) {
			if (TAG_COMPONENT_IMPL.equals(element.getName())) {

				/*
				 * Because the only instance of this type is created from a static singleton field, and
				 * the registry is initialized in the constructor of this type, other threads cannot
				 * compete with readElement() for access to <i>descriptors</i>
				 */
				
				HashSet<String> setOfIds = new HashSet<String>();
				setOfIds.add( element.getAttribute(ATT_TYPE) );
				
				IConfigurationElement[] child = element.getChildren(EXTENDED_ELEMENT);
				if( child.length > 0 ){
					for( int i=0; i< child.length; i++ ){
						setOfIds.add(child[i].getValue());
					}
				}
				String type = element.getAttribute(ATT_TYPE);
				if (type != null)
					descriptors.put(setOfIds, new ComponentImplDescriptor(element));
					//descriptors.put(element.getAttribute(ATT_TYPE), new ComponentImplDescriptor(element));
				else
					ModulecorePlugin.logError(0, "No type attribute is specified for " + //$NON-NLS-1$
								ModulecorePlugin.PLUGIN_ID + "." + COMPONENT_IMPL_EXTENSION_POINT + //$NON-NLS-1$ 
								" extension in " + element.getDeclaringExtension().getNamespaceIdentifier(), null); //$NON-NLS-1$
				return true;
			}
			return false;
		}
	}

}
