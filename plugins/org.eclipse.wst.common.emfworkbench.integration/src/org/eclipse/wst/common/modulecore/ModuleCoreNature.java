package org.eclipse.wst.common.modulecore;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;

import com.ibm.wtp.common.logger.proxy.Logger;

//In Progress......

public class ModuleCoreNature implements IProjectNature {
    private HashMap moduleHandlesMap;

    private HashMap workbenchModulesMap;

    private IProject moduleProject;

    private final static ModuleCoreFactory MODULE_FACTORY = ModuleCoreFactory.eINSTANCE;

    public IModuleHandle createModuleHandle(URI uri) {
        if (uri == null)
            return null;
        IModuleHandle handle = null;
        WorkbenchModule module = null;
        try {
            handle = createHandle(uri);
            module = createModuleHandle(handle);
        } catch (RuntimeException e) {
            Logger.getLogger().write(e);
        } finally {
            if (handle != null && module != null) {
                getModuleHandlesMap().put(uri, handle);
                getWorkbenchModulesMap().put(handle, module);
            }
        }
        return handle;
    }

    private IModuleHandle createHandle(URI uri) throws RuntimeException {
        IModuleHandle handle = null;
        handle = MODULE_FACTORY.createIModuleHandle();
        handle.setHandle(uri);
        return handle;
    }

    private WorkbenchModule createModuleHandle(IModuleHandle handle) throws RuntimeException {
        WorkbenchModule module;
        module = MODULE_FACTORY.createWorkbenchModule();
        module.setHandle(handle);
        return module;
    }

    public void resourceChanged(IResourceChangeEvent event) {

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
        if (path == null)return null;
        URI modulePathURI = URI.createPlatformResourceURI(path.toString());
        return modulePathURI;
    }

    private IPath getWTPModulePath() {
        IPath path = getProject().getFullPath();
        if (path == null) return null;
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
}
