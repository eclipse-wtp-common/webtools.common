/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Mar 29, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.componentcore.internal.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author cbridgha
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public class ArtifactEditRegistryReader extends RegistryReader {

	static final String ARTIFACT_EDIT_EXTENSION_POINT = "artifactedit"; //$NON-NLS-1$
	static final String ARTIFACTEDIT = "artifactedit"; //$NON-NLS-1$
	static final String TYPE = "typeID"; //$NON-NLS-1$
	static final String ARTIFACTEDITCLASS = "class"; //$NON-NLS-1$
	
	private static final ArtifactEditRegistryReader instance = new ArtifactEditRegistryReader();
	
	private final Map/*<String, ArtifactEditDescriptor>*/ descriptors = new HashMap();
	
	private final Map/*<ArtifactEditDescriptor, IArtifactEditFactory>*/ instances = new HashMap();
	
	/**
	 * @return Returns the instance.
	 */
	public static ArtifactEditRegistryReader instance() {
		/* already initialized and registry read by the time the class initializes */
		return instance;
	}
	
	public ArtifactEditRegistryReader() {
		super(ModulecorePlugin.PLUGIN_ID, ARTIFACT_EDIT_EXTENSION_POINT);
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
		if (ARTIFACTEDIT.equals(element.getName())) {
			
			/* Because the only instance of this type is created from a static singleton 
			 * field, and the registry is initialized in the constructor of this type, 
			 * other threads cannot compete with readElement() for access to <i>descriptors</i> 
			 */
			String type = element.getAttribute(TYPE);
			if(type != null)
				descriptors.put(element.getAttribute(TYPE), new ArtifactEditDescriptor(element));
			else 
				ModulecorePlugin.logError(0, "No type attribute is specified for " + //$NON-NLS-1$
										ModulecorePlugin.PLUGIN_ID + "." + ARTIFACT_EDIT_EXTENSION_POINT +  //$NON-NLS-1$ 
										" extension in "  + element.getDeclaringExtension().getNamespaceIdentifier(), null);  //$NON-NLS-1$
			return true;
		}
		return false;
	}
	
	public synchronized IArtifactEditFactory getArtifactEdit(String typeID) {
		
		ArtifactEditDescriptor descriptor = (ArtifactEditDescriptor) descriptors.get(typeID);
		IArtifactEditFactory factory = null;
		
		if(descriptor != null) {  
			
			factory = (IArtifactEditFactory) instances.get(descriptor);
			
			if(factory == null) {
				
				if((factory = descriptor.createFactory()) != null) {
					instances.put(descriptor, factory);
				} else {
					descriptors.remove(descriptor);
				} 
			} 
		}
		return factory;			
	} 
	
	// TODO Don't like this because it's going to cycle every project facet for each project
	public IArtifactEditFactory getArtifactEdit(IProject project) {
		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			Iterator keys = descriptors.keySet().iterator();
			while (keys.hasNext()) {
				String typeID = (String) keys.next();
				try {
					IProjectFacet projectFacet = ProjectFacetsManager.getProjectFacet(typeID);
					if (projectFacet != null && facetedProject.hasProjectFacet(projectFacet))
						return getArtifactEdit(typeID);
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			//Just return null
		}
		return null;
	} 
	
	public class ArtifactEditDescriptor {
		
		private final IConfigurationElement element;
		private final String type;

		public ArtifactEditDescriptor(IConfigurationElement configElement) {
			element = configElement;
			type = element.getAttribute(TYPE);
		}
		
		/**
		 * Create and return an {@link IArtifactEditFactory} for the given descriptor or 
		 * <b>null</b> if there are problems instantiating the extension.
		 * @return An {@link IArtifactEditFactory} for the given descriptor or 
		 * <b>null</b> if there are problems instantiating the extension.
		 */
		public IArtifactEditFactory createFactory() {
			
			final IArtifactEditFactory[] factory = new IArtifactEditFactory[1];
			
			SafeRunner.run(new ISafeRunnable() {

				public void handleException(Throwable exception) {
					ModulecorePlugin.logError(0, exception.getMessage(), exception); 
				}

				public void run() throws Exception {
					factory[0] = (IArtifactEditFactory) element.createExecutableExtension(ARTIFACTEDITCLASS); 
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
