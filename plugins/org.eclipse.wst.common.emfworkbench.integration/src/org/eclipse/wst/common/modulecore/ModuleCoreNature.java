package org.eclipse.wst.common.modulecore;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;

import com.ibm.wtp.common.logger.proxy.Logger;
import com.ibm.wtp.emf.workbench.EMFWorkbenchContextBase;
import com.ibm.wtp.emf.workbench.WorkbenchResourceHelperBase;
import com.ibm.wtp.emf.workbench.nature.EMFNature;
import com.ibm.wtp.internal.emf.workbench.EMFWorkbenchContextFactory;

//In Progress......

public class ModuleCoreNature extends EMFNature implements IProjectNature, IModuleConstants, IResourceChangeListener {

    private HashMap editModelsForRead;

    private HashMap editModelsForWrite;

    private IProject project;

    private static EMFWorkbenchContextFactory EMF_WORKBENCH_CONTEXT_FACTORY = EMFWorkbenchContextFactory.INSTANCE;

    public void resourceChanged(IResourceChangeEvent event) {
        //event.getDelta()
        // IResource changedResource = (IResource)event.getResource();
        //update()
    }

    private ModuleStructureModel getEditModel(URI moduleURI, boolean readOnly) {
        if (moduleURI == null || getProject() == null)
            return null;
        String editModelID = moduleURI.toFileString();
        EMFWorkbenchContext context = (EMFWorkbenchContext) EMF_WORKBENCH_CONTEXT_FACTORY.getEMFContext(getProject());
        ModuleStructureModel structureModule = new ModuleStructureModel(editModelID, context, readOnly);
        if (readOnly)
            getEditModelsForRead().put(moduleURI, structureModule);
        else
            getEditModelsForWrite().put(moduleURI, structureModule);
        return structureModule;
    }

    private HashMap getEditModelsForRead() {
        if (editModelsForRead == null)
            editModelsForRead = new HashMap();
        return editModelsForRead;
    }

    private HashMap getEditModelsForWrite() {
        if (editModelsForWrite == null)
            editModelsForWrite = new HashMap();
        return editModelsForWrite;
    }

    public IProject getProject() {
        return project;
    }

    public void setProject(IProject moduleProject) {
        project = moduleProject;
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

    public void primaryContributeToContext(EMFWorkbenchContextBase aNature) {
        // TODO Auto-generated method stub

    }

    public void secondaryContributeToContext(EMFWorkbenchContextBase aNature) {
        // TODO Auto-generated method stub

    }

    /*
     * private synchronized void update() { moduleHandlesMap.clear();
     * workbenchModulesMap.clear(); projectModules = null; try { if
     * (getProjectModules() != null) { List workBenchModules =
     * getProjectModules().getWorkbenchModules(); for (int i = 0; i <
     * workBenchModules.size(); i++) { WorkbenchModule wbm = (WorkbenchModule)
     * workBenchModules.get(i); // IModuleHandle handle = wbm.getHandle(); if
     * (handle == null || handle.getHandle() == null) continue;
     * moduleHandlesMap.put(handle.getHandle(), handle);
     * workbenchModulesMap.put(handle, wbm); } } } catch (RuntimeException e) {
     * Logger.getLogger().write(e); } }
     * 
     * private ProjectModules getProjectModules() { if (projectModules == null) {
     * Resource resource = getWTPModuleResource(); if (resource != null) { EList
     * wtpModuleResourceContents = resource.getContents(); if
     * (wtpModuleResourceContents != null && wtpModuleResourceContents.get(0) !=
     * null) projectModules = (ProjectModules) wtpModuleResourceContents.get(0); } }
     * 
     * return projectModules; }
     */
}
