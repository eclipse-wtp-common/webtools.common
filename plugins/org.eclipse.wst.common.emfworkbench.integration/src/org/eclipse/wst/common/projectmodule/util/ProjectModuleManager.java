/*
 * Created on Jan 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.projectmodule.util;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.projectmodule.ProjectModuleFactory;
import org.eclipse.wst.common.projectmodule.ProjectModulePackage;
import org.eclipse.wst.common.projectmodule.WorkbenchModule;

import com.ibm.wtp.emf.workbench.ProjectUtilities;

/**
 * @author cbridgha
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProjectModuleManager {
	static String MODULE_META_FILE_NAME = ".modules";
	
	private static HashMap projectModules; // Module list keyed by name
	public static void createModuleMetaData(IProject project) {
		IFile file = project.getFile(MODULE_META_FILE_NAME);
		Resource resource = WorkbenchResourceHelper.getExistingOrCreateResource(URI.createPlatformResourceURI(file.getFullPath().toString()));
		//URI metadataPath = URI.createPlatformResourceURI(project.getFullPath().append(MODULE_META_FILE_NAME).toOSString());
		//Resource resource = getResourceFactory(metadataPath).createResource(metadataPath);
		
		createDefaultStructure(resource, project);
		try {
			resource.save(null);
			//return resource;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void loadModuleMetaData(IProject project) {
		String metadataPath = project.getFullPath().append(MODULE_META_FILE_NAME).toOSString();
		Resource resource = new ResourceSetImpl().createResource(URI.createURI(metadataPath));
		Collection modules = EcoreUtil.getObjectsByType(resource.getContents(),ProjectModulePackage.eINSTANCE.getWorkbenchModule());
		for (Iterator iter = modules.iterator(); iter.hasNext();) {
			WorkbenchModule module = (WorkbenchModule) iter.next();
			projectModules.put(module.getName(),module);
		}
	}
	 /*
	   * Javadoc copied from interface.
	   */
	  public static Resource.Factory getResourceFactory(URI uri){

	    return Resource.Factory.Registry.INSTANCE.getFactory(uri);
	  }
	public static WorkbenchModule getModuleNamed(String moduleName) {
		return (WorkbenchModule)projectModules.get(moduleName);
	}
	
	/**
	 * @return
	 */
	private static void createDefaultStructure(Resource resource, IProject project) {
		WorkbenchModule module = ProjectModuleFactory.eINSTANCE.createWorkbenchModule();
		module.setName(project.getName());
		resource.getContents().add(module);
	}
	

}
