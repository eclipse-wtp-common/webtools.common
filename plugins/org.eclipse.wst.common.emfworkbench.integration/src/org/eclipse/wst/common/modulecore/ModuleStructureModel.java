/*
 * Created on Jan 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.modulecore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;

import com.ibm.wtp.common.logger.proxy.Logger;

public class ModuleStructureModel extends EditModel implements IResourceChangeListener {
    private final static ModuleCoreFactory MODULE_FACTORY = ModuleCoreFactory.eINSTANCE;

    ArrayList moduleStructureListeners = new ArrayList(3);

    private HashMap workbenchModulesMap;

    public ModuleStructureModel(String editModelID, EMFWorkbenchContext context, boolean readOnly) {
        super(editModelID, context, readOnly);
    }

    public void addIModuleStructureListener(IModuleStructureListener listener) {
        moduleStructureListeners.add(listener);
    }

    private void notifyListeners() {
        ModuleStructureEvent event = createEvent();
        for (Iterator iter = moduleStructureListeners.iterator(); iter.hasNext();) {
            IModuleStructureListener listener = (IModuleStructureListener) iter.next();
            listener.structureChanged(event);
        }
    }

    private void addResourceChangeListener() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    private ModuleStructureEvent createEvent() {
        ModuleStructureEvent event = new ModuleStructureEvent(getSource(), getModuleResources());
        return null;
    }

    private Object getSource() {
        return null;
    }

    private ModuleResource[] getModuleResources() {
        return null;
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

    private HashMap getWorkbenchModulesMap() {
        if (workbenchModulesMap == null)
            workbenchModulesMap = new HashMap();
        return workbenchModulesMap;
    }


    public void resourceChanged(IResourceChangeEvent event) {  
    }


    public void cacheNonResourceValidateState(List roNonResourceFiles) {
 
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
