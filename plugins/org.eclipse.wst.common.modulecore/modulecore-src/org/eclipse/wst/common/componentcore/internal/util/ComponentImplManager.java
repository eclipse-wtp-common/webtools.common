/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.internal.resources.ResourceTimestampMappings;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualComponent;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

public class ComponentImplManager  {

	private static final String NO_FACETS = "NONE";//$NON-NLS-1$
	
	private static final String COMPONENT_IMPL_EXTENSION_POINT = "componentimpl"; //$NON-NLS-1$
	private static final String TAG_COMPONENT_IMPL = "componentimpl"; //$NON-NLS-1$
	private static final String ATT_TYPE = "typeID"; //$NON-NLS-1$
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$

	private static final ComponentImplManager instance = new ComponentImplManager();
//	private static final Object LOAD_FAILED = new Object();

	private final Map/* <String, ComponentImplDescriptor> */ descriptors = new Hashtable();

	private final Map/* <ComponentImplDescriptor, IComponentImplFactory> */ instances = new Hashtable();
	private final ResourceTimestampMappings factoryMap = new ResourceTimestampMappings();

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
			}

		});
	}


	private IComponentImplFactory getComponentImplFactory(String typeID) {

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
	 
	private IComponentImplFactory findFactoryForProject(IProject project){
		try {
			IComponentImplFactory factory = null;		
			
			if( !factoryMap.hasChanged(project) ) {				

				if( factoryMap.hasCacheError(project))
					return null;
				
				if( factoryMap.hasCacheData(project)) 
					return (IComponentImplFactory) factoryMap.getData(project);
			} 
			
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			if (facetedProject == null){
				factory = getComponentImplFactory(NO_FACETS);
				factoryMap.mark(project, factory);
				return factory;
			}
			Iterator keys = descriptors.keySet().iterator();
			while (keys.hasNext()) {
				String typeID = (String) keys.next();
				if(typeID.equals(NO_FACETS)){
					continue;
				}
				try {
					IProjectFacet projectFacet = ProjectFacetsManager.getProjectFacet(typeID);
					if (projectFacet != null && facetedProject.hasProjectFacet(projectFacet)){
						factory = getComponentImplFactory(typeID);
						if(null != factory){
							factoryMap.mark(project, factory);
							return factory;
						}
					}
				} catch (Exception e) {
					continue;
				}
			}
			
		} catch (Exception e) {
			ModulecorePlugin.logError(0, "Returning null factory for project: " + project, e); //$NON-NLS-1$
			factoryMap.markError(project);
		}
		return null;
	}
	 
	
	
	public IVirtualFolder createFolder(IProject aProject, IPath aRuntimePath){
		try {
			IComponentImplFactory factory = findFactoryForProject(aProject);
			if(null != factory){
				return factory.createFolder(aProject, aRuntimePath);
			}
		} catch (Exception e) {
			// Just return a default folder
		}
		return new VirtualFolder(aProject, aRuntimePath);
	}

	public IVirtualComponent createComponent(IProject project) {
		try {
			IComponentImplFactory factory = findFactoryForProject(project);
			if(null != factory){
				return factory.createComponent(project);
			}
		} catch (Exception e) {
			// Just return a default component
		}
		if (!ModuleCoreNature.isFlexibleProject(project)){
			return null;
		}
		return new VirtualComponent(project, new Path("/")); //$NON-NLS-1$
	}

	public IVirtualComponent createComponent(IProject project, boolean checkSettings) {
		if  (checkSettings)
			return createComponent(project);
		try {
			IComponentImplFactory factory = findFactoryForProject(project);
			if(null != factory){
				return factory.createComponent(project);
			}
		} catch (Exception e) {
			// Just return a default component
		}
		if (ModuleCoreNature.getModuleCoreNature(project) == null){
			return null;
		}
		return new VirtualComponent(project, new Path("/")); //$NON-NLS-1$
	}

	public IVirtualComponent createArchiveComponent(IProject aProject, String aComponentName) {
		try {
			IComponentImplFactory factory = findFactoryForProject(aProject);
			if(null != factory){
				return factory.createArchiveComponent(aProject, aComponentName, new Path("/")); //$NON-NLS-1$
			}
		} catch (Exception e) {
			// Just return a default archive component
		}
		return new VirtualArchiveComponent(aProject, aComponentName, new Path("/")); //$NON-NLS-1$
	}
	
	private class ComponentImplDescriptor {

		private final IConfigurationElement element;
		private final String type;

		public ComponentImplDescriptor(IConfigurationElement configElement) {
			element = configElement;
			type = element.getAttribute(ATT_TYPE);
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

		/**
		 * 
		 * @return The type id of this ArtifactEdit definition
		 */
		public String getType() {
			return type;
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
				String type = element.getAttribute(ATT_TYPE);
				if (type != null)
					descriptors.put(element.getAttribute(ATT_TYPE), new ComponentImplDescriptor(element));
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
