
package org.eclipse.wst.common.modulecore;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.modulecore.internal.impl.WTPModulesResource;
import org.eclipse.wst.common.modulecore.internal.impl.WTPModulesResourceFactory;

public class ModuleStructuralModel extends EditModel implements IAdaptable {
	
	public static final String MODULE_CORE_ID = "moduleCoreId"; //$NON-NLS-1$ 
  

    public ModuleStructuralModel(String editModelID, EMFWorkbenchContext context, boolean readOnly) {
        super(editModelID, context, readOnly);
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emfworkbench.integration.EditModel#getPrimaryRootObject()
	 */
	public EObject getPrimaryRootObject() {
		if(getPrimaryResource().getContents().size() == 0)
			prepareProjectModulesIfNecessary();
		return super.getPrimaryRootObject();
	}
       
	public WTPModulesResource  makeWTPModulesResource() {
		return (WTPModulesResource) createResource(WTPModulesResourceFactory.WTP_MODULES_URI_OBJ);
	}

	public Resource prepareProjectModulesIfNecessary() {
		XMIResource res = makeWTPModulesResource();		
		addProjectModulesIfNecessary(res);
		return res;
	}
	
	public Object getAdapter(Class anAdapter) {
		return Platform.getAdapterManager().getAdapter(this, anAdapter); 
	}
	
	protected void addProjectModulesIfNecessary(XMIResource aResource) { 
		if (aResource != null && aResource.getContents().isEmpty()) {
			ProjectModules projectModules = ModuleCorePackage.eINSTANCE.getModuleCoreFactory().createProjectModules();
			aResource.getContents().add(projectModules); 
			aResource.setID(projectModules, MODULE_CORE_ID);
		}
	}
}
