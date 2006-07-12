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
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualComponent;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

public class ComponentImplRegistryReader extends RegistryReader {

	private static final String COMPONENT_IMPL_EXTENSION_POINT = "componentimpl"; //$NON-NLS-1$
	private static final String COMPONENT_IMPL = "componentimpl"; //$NON-NLS-1$
	private static final String TYPE = "typeID"; //$NON-NLS-1$
	private static final String CLASS = "class"; //$NON-NLS-1$

	private static final ComponentImplRegistryReader instance = new ComponentImplRegistryReader();

	private final Map/* <String, ComponentImplDescriptor> */descriptors = new Hashtable();

	private final Map/* <ComponentImplDescriptor, IComponentImplFactory> */instances = new Hashtable();

	/**
	 * @return Returns the instance.
	 */
	public static ComponentImplRegistryReader instance() {
		/* already initialized and registry read by the time the class initializes */
		return instance;
	}

	public ComponentImplRegistryReader() {
		super(ModulecorePlugin.PLUGIN_ID, COMPONENT_IMPL_EXTENSION_POINT);
		SafeRunner.run(new ISafeRunnable() {

			public void handleException(Throwable exception) {
				ModulecorePlugin.logError(0, exception.getMessage(), exception);
			}

			public void run() throws Exception {
				readRegistry();
			}

		});
	}

	/**
	 * @see org.eclipse.wst.common.frameworks.internal.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
	 */
	public boolean readElement(IConfigurationElement element) {
		if (COMPONENT_IMPL.equals(element.getName())) {

			/*
			 * Because the only instance of this type is created from a static singleton field, and
			 * the registry is initialized in the constructor of this type, other threads cannot
			 * compete with readElement() for access to <i>descriptors</i>
			 */
			String type = element.getAttribute(TYPE);
			if (type != null)
				descriptors.put(element.getAttribute(TYPE), new ComponentImplDescriptor(element));
			else
				ModulecorePlugin.logError(0, "No type attribute is specified for " + //$NON-NLS-1$
							ModulecorePlugin.PLUGIN_ID + "." + COMPONENT_IMPL_EXTENSION_POINT + //$NON-NLS-1$ 
							" extension in " + element.getDeclaringExtension().getNamespaceIdentifier(), null); //$NON-NLS-1$
			return true;
		}
		return false;
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
	
	// TODO Don't like this because it's going to cycle every project facet for each project
	protected IComponentImplFactory findFactoryForProject(IProject project){
		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			Iterator keys = descriptors.keySet().iterator();
			while (keys.hasNext()) {
				String typeID = (String) keys.next();
				try {
					IProjectFacet projectFacet = ProjectFacetsManager.getProjectFacet(typeID);
					if (projectFacet != null && facetedProject.hasProjectFacet(projectFacet)){
						IComponentImplFactory factory = getComponentImplFactory(typeID);
						if(null != factory){
							return factory;
						}
					}
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			// Just return null
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
			type = element.getAttribute(TYPE);
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
					factory[0] = (IComponentImplFactory) element.createExecutableExtension(CLASS);
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

}
