package org.eclipse.wst.common.modulecore;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;

import com.ibm.wtp.common.logger.proxy.Logger;

//In Progress......

public class ModuleCoreNature implements IProjectNature, IResourceChangeListener {
    private HashMap moduleHandlesMap;

    private HashMap workbenchModulesMap;

    private IProject moduleProject;

    private ProjectModules projectModules;

    private final static ModuleCoreFactory MODULE_FACTORY = ModuleCoreFactory.eINSTANCE;

    public WorkbenchModule createModuleHandle(URI uri) {
        if (uri == null) return null;
        WorkbenchModule module = null;
        try {           
            module = createModuleHandle(uri);
        } catch (RuntimeException e) {
            Logger.getLogger().write(e);
        } finally {
            if (module != null) {
                getWorkbenchModulesMap().put(uri, module);
            }
        }
        return module;
    }



  /*  private WorkbenchModule createModuleHandle(URI uri) throws RuntimeException {
        WorkbenchModule module;
        module = MODULE_FACTORY.createWorkbenchModule();
       // module.setHandle(handle);
        return module;
    }*/

    public void resourceChanged(IResourceChangeEvent event) {
        //event.getDelta()
       // IResource changedResource = (IResource)event.getResource();
        //update()
    }

    private Resource getWTPModuleResource() {
        URI wtpModuleURI = createWTPModuleURI();
        if (wtpModuleURI == null)
            return null;
        Resource wtpModuleResource = WorkbenchResourceHelper.getResource(wtpModuleURI);
        return wtpModuleResource;
    }

    private URI createWTPModuleURI() {
        IPath path = getWTPModulePath();
        if (path == null)
            return null;
        URI modulePathURI = URI.createPlatformResourceURI(path.toString());
        return modulePathURI;
    }

    private IPath getWTPModulePath() {
        IPath path = getProject().getFullPath();
        if (path == null)
            return null;
        path.append(IModuleConstants.WTPMODULE_FILE_NAME);
        return path;

    }

    public WorkbenchModule[] getWorkbenchModules() {
        Object[] values = getWorkbenchModulesMap().values().toArray();
        WorkbenchModule[] workbenchModules = new WorkbenchModule[values.length];
        for (int i = 0; i < values.length; i++) {
            workbenchModules[i] = (WorkbenchModule) values[i];
        }
        return workbenchModules;
    }

    private HashMap getModuleHandlesMap() {
        if (moduleHandlesMap == null)
            moduleHandlesMap = new HashMap();
        return moduleHandlesMap;
    }

    private HashMap getWorkbenchModulesMap() {
        if (workbenchModulesMap == null)
            workbenchModulesMap = new HashMap();
        return workbenchModulesMap;
    }

    public void configure() throws CoreException {

    }

    public void deconfigure() throws CoreException {

    }

    public IProject getProject() {
        return moduleProject;
    }

    public void setProject(IProject project) {
        moduleProject = project;
    }
    
    private void addResourceChangeListener(){
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);       
    }

    private synchronized void update() {
        moduleHandlesMap.clear();
        workbenchModulesMap.clear();
        projectModules = null;
        try {
            if (getProjectModules() != null) {
                List workBenchModules = getProjectModules().getWorkbenchModules();
                for (int i = 0; i < workBenchModules.size(); i++) {
                    WorkbenchModule wbm = (WorkbenchModule) workBenchModules.get(i);
                   // IModuleHandle handle = wbm.getHandle();
                  /*  if (handle == null || handle.getHandle() == null) continue;
                    moduleHandlesMap.put(handle.getHandle(), handle);
                    workbenchModulesMap.put(handle, wbm);*/
                }
            }
        } catch (RuntimeException e) {
            Logger.getLogger().write(e);
        }
    }

    private ProjectModules getProjectModules() {
        if (projectModules == null) {
            Resource resource = getWTPModuleResource();
            if (resource != null) {
                EList wtpModuleResourceContents = resource.getContents();
                if (wtpModuleResourceContents != null && wtpModuleResourceContents.get(0) != null)
                    projectModules = (ProjectModules) wtpModuleResourceContents.get(0);
            }
        }

        return projectModules;
    }
}
