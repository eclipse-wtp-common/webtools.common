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



/*    public WorkbenchModule[] getWorkbenchModules() {
        Object[] values = getWorkbenchModulesMap().values().toArray();
        WorkbenchModule[] workbenchModules = new WorkbenchModule[values.length];
        for (int i = 0; i < values.length; i++) {
            workbenchModules[i] = (WorkbenchModule) values[i];
        }
        return workbenchModules;
    }*/

    private HashMap getModuleHandlesMap() {
        if (moduleHandlesMap == null)
            moduleHandlesMap = new HashMap();
        return moduleHandlesMap;
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
    


   /* private synchronized void update() {
        moduleHandlesMap.clear();
        workbenchModulesMap.clear();
        projectModules = null;
        try {
            if (getProjectModules() != null) {
                List workBenchModules = getProjectModules().getWorkbenchModules();
                for (int i = 0; i < workBenchModules.size(); i++) {
                    WorkbenchModule wbm = (WorkbenchModule) workBenchModules.get(i);
                   // IModuleHandle handle = wbm.getHandle();
                    if (handle == null || handle.getHandle() == null) continue;
                    moduleHandlesMap.put(handle.getHandle(), handle);
                    workbenchModulesMap.put(handle, wbm);
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
    }*/
}
