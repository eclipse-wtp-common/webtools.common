
package org.eclipse.wst.common.modulecore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;

import com.ibm.wtp.common.logger.proxy.Logger;

public class ModuleStructuralModel extends EditModel implements IResourceChangeListener {
	
	public static final String MODULE_CORE_ID = "moduleCoreId"; //$NON-NLS-1$
    private final static ModuleCoreFactory MODULE_FACTORY = ModuleCoreFactory.eINSTANCE;

    ArrayList moduleStructureListeners = new ArrayList(3);

    private HashMap workbenchModulesMap;

    public ModuleStructuralModel(String editModelID, EMFWorkbenchContext context, boolean readOnly) {
        super(editModelID, context, readOnly);
    }

    private void addResourceChangeListener() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    public WorkbenchModule createWorkbenchModule(URI uri) {
        if (uri == null)
            return null;
        WorkbenchModule module = null;
        try {
            module = MODULE_FACTORY.createWorkbenchModule();
        } catch (RuntimeException e) {
            Logger.getLogger().write(e);
        } finally {
            if (module != null) {
                getWorkbenchModulesMap().put(uri, module);
            }
        }
        return module;
    }

    public WorkbenchApplication createWorkbenchApplication(URI handleURI, String deployedName) {
        if (handleURI == null || deployedName == null)
            return null;
        WorkbenchApplication workBenchApplication = null;
        workBenchApplication = createWorkbenchApplication(handleURI);
        workBenchApplication.setDeployedName(deployedName);
        return workBenchApplication;
    }

    public WorkbenchApplication createWorkbenchApplication(URI handleURI) {
        if (handleURI == null)
            return null;
        WorkbenchApplication workBenchApplication = null;
        try {
            workBenchApplication = MODULE_FACTORY.createWorkbenchApplication();
        } catch (RuntimeException e) {
            Logger.getLogger().write(e);
        } finally {
            if (workBenchApplication != null) {
                getWorkbenchModulesMap().put(handleURI, workBenchApplication);
            }
        }
        return workBenchApplication;
    }

    private HashMap getWorkbenchModulesMap() {
        if (workbenchModulesMap == null)
            workbenchModulesMap = new HashMap();
        return workbenchModulesMap;
    }

    public void resourceChanged(IResourceChangeEvent event) {
    }

    public void cacheNonResourceValidateState(List roNonResourceFiles) {

    }
    
	public WTPModulesResource  makeWTPModulesResource() {
		return (WTPModulesResource) createResource(WTPModulesResourceFactory.WTP_MODULES_URI_OBJ);
	}

	public Resource prepareProjectModulesIfNecessary() {
		XMIResource res = makeWTPModulesResource();		
		addProjectModulesIfNecessary(res);
		return res;
	}
	
	protected void addProjectModulesIfNecessary(XMIResource aResource) { 
		if (aResource != null && aResource.getContents().isEmpty()) {
			ProjectModules projectModules = ModuleCorePackage.eINSTANCE.getModuleCoreFactory().createProjectModules();
			aResource.getContents().add(projectModules); 
			aResource.setID(projectModules, MODULE_CORE_ID);
		}
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

}
