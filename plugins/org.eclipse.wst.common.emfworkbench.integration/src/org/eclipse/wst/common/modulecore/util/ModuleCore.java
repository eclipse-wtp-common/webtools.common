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
package org.eclipse.wst.common.modulecore.util;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.modulecore.ModuleCoreFactory;
import org.eclipse.wst.common.modulecore.ModuleCoreNature;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.ModuleStructuralModel;
import org.eclipse.wst.common.modulecore.ProjectModules;
import org.eclipse.wst.common.modulecore.WorkbenchModule;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ModuleCore {
	
	
	public static ModuleCore INSTANCE = new ModuleCore();
	static String MODULE_META_FILE_NAME = ".wtpmodules";

	private HashMap projectModules; // Module list keyed by name

	public void createModuleMetaData(IProject project) {
		IFile file = project.getFile(MODULE_META_FILE_NAME);
		Resource resource = WorkbenchResourceHelper.getExistingOrCreateResource(URI.createPlatformResourceURI(file.getFullPath().toString()));
		// URI metadataPath =
		// URI.createPlatformResourceURI(project.getFullPath().append(MODULE_META_FILE_NAME).toOSString());
		// Resource resource = getResourceFactory(metadataPath).createResource(metadataPath);

		createDefaultStructure(resource, project);
		try {
			resource.save(null);
			// return resource;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ModuleStructuralModel getModuleStructuralModelForRead(IProject aProject, Object anAccessorKey) {
		ModuleCoreNature aNature = ModuleCoreNature.getModuleCoreNature(aProject);
		return aNature.getModuleStructuralModelForRead(anAccessorKey);
	}
	public ModuleStructuralModel getModuleStructuralModelForWrite(IProject aProject, Object anAccessorKey) {
		ModuleCoreNature aNature = ModuleCoreNature.getModuleCoreNature(aProject);
		return aNature.getModuleStructuralModelForWrite(anAccessorKey);
	}
	public ProjectModules getProjectModules(ModuleStructuralModel aModuleStucturalModule) {
		return (ProjectModules)aModuleStucturalModule.getPrimaryRootObject();
	}
	public WorkbenchModule[] getWorkbenchModules(ModuleStructuralModel aModuleStucturalModule) {
		List wbModules = getProjectModules(aModuleStucturalModule).getWorkbenchModules();
		return (WorkbenchModule[])wbModules.toArray(new WorkbenchModule[wbModules.size()]);
	}

	public void loadModuleMetaData(IProject project) {
		String metadataPath = project.getFullPath().append(MODULE_META_FILE_NAME).toOSString();
		Resource resource = new ResourceSetImpl().createResource(URI.createURI(metadataPath));
		Collection modules = EcoreUtil.getObjectsByType(resource.getContents(), ModuleCorePackage.eINSTANCE.getWorkbenchModule());
		for (Iterator iter = modules.iterator(); iter.hasNext();) {
			WorkbenchModule module = (WorkbenchModule) iter.next();
			projectModules.put(module.getHandle(), module);
		}
	}

	/*
	 * Javadoc copied from interface.
	 */
	public Resource.Factory getResourceFactory(URI uri) {

		return Resource.Factory.Registry.INSTANCE.getFactory(uri);
	}
 
	public WorkbenchModule getModuleNamed(ModuleStructuralModel aStructuralModel, String moduleName) {
		return (WorkbenchModule) projectModules.get(moduleName);
	}

	/**
	 * @return
	 */
	private void createDefaultStructure(Resource resource, IProject project) {
		WorkbenchModule module = ModuleCoreFactory.eINSTANCE.createWorkbenchModule();
		// TODO Handle Handle
		// module.setName(project.getName());
		resource.getContents().add(module);
	}

}
