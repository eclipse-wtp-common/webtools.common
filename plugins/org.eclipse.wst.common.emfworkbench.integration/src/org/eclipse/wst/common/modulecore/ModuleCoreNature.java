package org.eclipse.wst.common.modulecore;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.wst.common.internal.emfworkbench.CompatibilityWorkbenchURIConverterImpl;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelNature;

import com.ibm.wtp.emf.workbench.EMFWorkbenchContextBase;
import com.ibm.wtp.emf.workbench.ProjectResourceSet;
import com.ibm.wtp.emf.workbench.WorkbenchURIConverter;

//In Progress......

public class ModuleCoreNature extends EditModelNature implements IProjectNature, IModuleConstants, IResourceChangeListener {

	public void resourceChanged(IResourceChangeEvent anEvent) {
		// event.getDelta()
		// IResource changedResource = (IResource)event.getResource();
		// update()
	}
	
	public static ModuleCoreNature getModuleCoreNature(IProject aProject) {
		try {
			return (ModuleCoreNature) aProject.getNature(IModuleConstants.MODULE_NATURE_ID);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null; 
	}

	public ModuleStructuralModel getModuleStructuralModelForRead(Object anAccessorKey) {
		return (ModuleStructuralModel) getEditModelForRead(ModuleStructuralModelFactory.MODULE_STRUCTURAL_MODEL_ID, anAccessorKey);
	}

	public ModuleStructuralModel getModuleStructuralModelForWrite(Object anAccessorKey) {
		return (ModuleStructuralModel) getEditModelForWrite(ModuleStructuralModelFactory.MODULE_STRUCTURAL_MODEL_ID, anAccessorKey);
	}

	public ModuleEditModel getModuleEditModelForRead(URI aModuleURI, Object anAccessorKey) {
		Map params = new HashMap();
		params.put(ModuleEditModelFactory.PARAM_MODULE_URI, aModuleURI);
		return (ModuleEditModel) getEditModelForRead(ModuleEditModelFactory.MODULE_EDIT_MODEL_ID, anAccessorKey, params);
	}

	public ModuleEditModel getModuleEditModelForWrite(URI aModuleURI, Object anAccessorKey) {
		Map params = new HashMap();
		params.put(ModuleEditModelFactory.PARAM_MODULE_URI, aModuleURI);
		return (ModuleEditModel) getEditModelForWrite(ModuleEditModelFactory.MODULE_EDIT_MODEL_ID, anAccessorKey, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.emf.workbench.IEMFContextContributor#primaryContributeToContext(com.ibm.wtp.emf.workbench.EMFWorkbenchContextBase)
	 */
	public void primaryContributeToContext(EMFWorkbenchContextBase aNature) {
		if (emfContext == aNature)
			return;
		emfContext = aNature;
		getEmfContext().setDefaultToMOF5Compatibility(true);
		// Overriding superclass to use our own URI converter, which knows about binary projects
		ProjectResourceSet set = aNature.getResourceSet();
		set.setResourceFactoryRegistry(WTPResourceFactoryRegistry.INSTANCE);
		WorkbenchURIConverter conv = initializeWorbenchURIConverter(set);
		set.setURIConverter(conv);
//		initializeCacheEditModel();
//		addAdapterFactories(set);
//		set.getSynchronizer().addExtender(this); // added so we can be informed of closes to the
//		new J2EEResourceDependencyRegister(set); // This must be done after the URIConverter is

	}

	/**
	 * @param set
	 * @return
	 */
	private WorkbenchURIConverter initializeWorbenchURIConverter(ProjectResourceSet set) {
		WorkbenchURIConverter uriConverter = new CompatibilityWorkbenchURIConverterImpl();
		uriConverter.addInputContainer(getProject());
		return uriConverter;
	}

	public ResourceSet getResourceSet() {
		return getEmfContextBase().getResourceSet();
	}

	public String getNatureID() {
		return MODULE_NATURE_ID;
	}

	protected String getPluginID() {
		return MODULE_PLUG_IN_ID;
	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.emf.workbench.IEMFContextContributor#secondaryContributeToContext(com.ibm.wtp.emf.workbench.EMFWorkbenchContextBase)
	 */
	public void secondaryContributeToContext(EMFWorkbenchContextBase aNature) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * private synchronized void update() { moduleHandlesMap.clear(); workbenchModulesMap.clear();
	 * projectModules = null; try { if (getProjectModules() != null) { List workBenchModules =
	 * getProjectModules().getWorkbenchModules(); for (int i = 0; i < workBenchModules.size(); i++) {
	 * WorkbenchModule wbm = (WorkbenchModule) workBenchModules.get(i); // IModuleHandle handle =
	 * wbm.getHandle(); if (handle == null || handle.getHandle() == null) continue;
	 * moduleHandlesMap.put(handle.getHandle(), handle); workbenchModulesMap.put(handle, wbm); } } }
	 * catch (RuntimeException e) { Logger.getLogger().write(e); } }
	 * 
	 * private ProjectModules getProjectModules() { if (projectModules == null) { Resource resource =
	 * getWTPModuleResource(); if (resource != null) { EList wtpModuleResourceContents =
	 * resource.getContents(); if (wtpModuleResourceContents != null &&
	 * wtpModuleResourceContents.get(0) != null) projectModules = (ProjectModules)
	 * wtpModuleResourceContents.get(0); } }
	 * 
	 * return projectModules; }
	 */
}
