/*
 * Created on Jan 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.modulecore;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;

import com.ibm.wtp.common.logger.proxy.Logger;

//In Progress......

public class ModuleCoreNature implements IProjectNature, IResourceChangeListener {
	private HashMap moduleHandles;

	private final static ModuleCoreFactory MODULE_FACTORY = ModuleCoreFactory.eINSTANCE;

	private HashMap workbenchModules;

	protected IModuleHandle createModuleHandle(URI uri) {
		if (uri == null)
			return null;
		IModuleHandle handle = null;
		WorkbenchModule module = null;
		try {
			handle = createHandle(uri);
			module = createModuleHandle(handle);
		} catch (IllegalArgumentException e) {
			Logger.getLogger().write(e);
		} finally {
			if (handle != null && module != null) {
				getModuleHandles().put(uri, handle);
				getWorkbenchModules().put(handle, module);
			}
		}
		return null;
	}

	private IModuleHandle createHandle(URI uri) throws IllegalArgumentException {
		IModuleHandle handle = null;
		handle = MODULE_FACTORY.createIModuleHandle();
		handle.setHandle(uri);
		return handle;
	}

	private WorkbenchModule createModuleHandle(IModuleHandle handle)throws IllegalArgumentException {
		WorkbenchModule module;
		module = MODULE_FACTORY.createWorkbenchModule();
		module.setHandle(handle);
		return module;
	}

	public void resourceChanged(IResourceChangeEvent event) {

	}

	private HashMap getModuleHandles() {
		if (moduleHandles == null)
			moduleHandles = new HashMap();
		return moduleHandles;
	}

	private HashMap getWorkbenchModules() {
		if (workbenchModules == null)
			workbenchModules = new HashMap();
		return workbenchModules;
	}


	public void configure() throws CoreException {
	
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException {
		
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject project) {
		// TODO Auto-generated method stub
		
	}
}
