/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.wst.common.componentcore.internal.impl.ResourceTreeNode;
import org.eclipse.wst.common.componentcore.internal.impl.ResourceTreeRoot;
import org.eclipse.wst.common.componentcore.internal.impl.WTPModulesResource;
import org.eclipse.wst.common.componentcore.internal.impl.WTPModulesResourceFactory;
import org.eclipse.wst.common.frameworks.internal.SaveFailedException;
import org.eclipse.wst.common.internal.emf.resource.ReferencedResource;
import org.eclipse.wst.common.internal.emf.resource.TranslatorResource;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.internal.emfworkbench.validateedit.ResourceStateValidator;
import org.eclipse.wst.common.internal.emfworkbench.validateedit.ResourceStateValidatorPresenter;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectNature;
/**
 * Manages the underlying Module Structural Metamodel.
* <a name="module-structural-model"/>
* <p>
* Each ModuleCoreNature from a given project can provide access to the
* {@see org.eclipse.wst.common.modulecore.ModuleStructuralModel}&nbsp; of the project.
* {@see org.eclipse.wst.common.modulecore.ModuleStructuralModel}&nbsp; is a subclass of
* {@see org.eclipse.wst.common.internal.emfworkbench.integration.EditModel}&nbsp;that manages
* resources associated with the Module Structural Metamodel. As an EditModel, the
* {@see org.eclipse.wst.common.modulecore.ModuleStructuralModel}&nbsp; references EMF resources,
* that contain EMF models -- in this case, the EMF model of <i>.component </i> file.
* </p>
* <p>
* Clients are encouraged to use the Edit Facade pattern (via
* {@see org.eclipse.wst.common.modulecore.ModuleCore}&nbsp; or one if its relevant subclasses)
* to work directly with the Module Structural Metamodel.
* </p> 
* <p>
* <a href="ModuleCoreNature.html#model-discussion">See the discussion</a> of how ModuleStructuralModel relates to the ArtifactEditModel and ModuleCoreNature.
* <a name="accessor-key"/>
* <p>
* All EditModels have a lifecycle that must be enforced to keep the resources loaded that are in
* use, and to unload resources that are not in use. To access an EditModel, clients are required to
* supply an object token referred to as an accessor key. The accessor key allows the framework to
* better track which clients are using the EditModel, and to ensure that only a client which has
* accessed the EditModel with an accessor key may invoke save*()s on that EditModel.
* </p>
*/ 
public class ModuleStructuralModel extends EditModel implements IAdaptable {
	
	public static final String MODULE_CORE_ID = "moduleCoreId"; //$NON-NLS-1$ 
	private static final String PROJECT_VERSION_1_5 = "1.5.0";
	private boolean useOldFormat = false;
	public ModuleStructuralModel(String editModelID, EMFWorkbenchContext context, boolean readOnly) {
        super(editModelID, context, readOnly);
    }
    /**
	 * Release each of the referenced resources.
	 */
	protected void release(ReferencedResource aResource) {
		if (isReadOnly() && aResource.getReadCount() != 0)
			aResource.releaseFromRead();
		else
			aResource.releaseFromWrite();

	}
	protected boolean removeResource(Resource aResource) {
		if (aResource != null) {
			synchronized (aResource) {
				aResource.eAdapters().remove(resourceAdapter);
				return getResources().remove(aResource);
			}
		}
		return false;
	}
    
    /* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emfworkbench.integration.EditModel#getPrimaryRootObject()
	 */
	public EObject getPrimaryRootObject() {
		
		try {
			Resource res = prepareProjectModulesIfNecessary();
			if (res == null)
				return null;
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EObject modelRoot = null;
		modelRoot = super.getPrimaryRootObject();
		if (modelRoot != null) {
			// if the workspace tree is locked we cannot try to change the .component resource
			if (ResourcesPlugin.getWorkspace().isTreeLocked())
				return modelRoot;
			List components = ((ProjectComponents)modelRoot).getComponents();
			if (components.size()>0) {
				WorkbenchComponent wbComp = (WorkbenchComponent)components.get(0);
				// Check and see if we need to clean up spurrious redundant map entries
				if (!isVersion15(modelRoot)) {
					((ProjectComponents)modelRoot).setVersion(PROJECT_VERSION_1_5);
					cleanupWTPModules(wbComp);
				}
			}
		}
		return modelRoot;
	}
	private boolean isVersion15(EObject modelRoot){
		return ((ProjectComponents)modelRoot).getVersion().equals(PROJECT_VERSION_1_5);
	}
    
	/**
	 * This method is used to remove spurrious redundant entries from the .component file
	 * 
	 * @param wbComp
	 */
	public void cleanupWTPModules(WorkbenchComponent wbComp) {
		ModuleStructuralModel model = new ModuleStructuralModel(getEditModelID(),getEmfContext(),false);
		if (wbComp == null || model == null)
			return;
		try {
			model.access(this);
			ResourceTreeRoot root = ResourceTreeRoot.getSourceResourceTreeRoot(wbComp);
			List rootResources = getModuleResources(root);
			// Only if we need to do a clean, do we clear, add all required root resource mappings, and save
			if (!(wbComp.getResources().containsAll(rootResources) && wbComp.getResources().size()==rootResources.size())) {
				wbComp.getResources().clear();
				wbComp.getResources().addAll(rootResources);
				model.save(this);
			}
		} finally {
			if (model != null)
				model.dispose();
		}
	}
    
	/**
	 * This is a recursive method to find all the root level resources in the children's resource tree roots
	 * 
	 * @param node
	 * @return List of module resources
	 */
	public List getModuleResources(ResourceTreeNode node) {
		// If the resource node has module resources just return them
		if (node.getModuleResources().length>0)
			return Arrays.asList(node.getModuleResources());
		// Otherwise, the root resource maps are really at the next level or lower
		List rootResources = new ArrayList();
		Map children = node.getChildren();
		Iterator iter = children.values().iterator();
		while (iter.hasNext()) {
			ResourceTreeNode subNode = (ResourceTreeNode) iter.next();
			// recursively call method to obtain module resources
			rootResources.addAll(getModuleResources(subNode));
		}
		return rootResources;
	}
	public Resource prepareProjectModulesIfNecessary() throws CoreException {
		XMIResource res;
		res = (XMIResource) getPrimaryResource();
		if (res != null && resNeedsMigrating(res) && !useOldFormat)
			return null;
		if(res == null)
			res = makeWTPModulesResource();		
		try {
			addProjectModulesIfNecessary(res);
		} catch (IOException e) {		
			Platform.getLog(ModulecorePlugin.getDefault().getBundle()).log(new Status(IStatus.ERROR, ModulecorePlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
		} 
		return res;
	}
	
	public WTPModulesResource  makeWTPModulesResource() {
		return (WTPModulesResource) createResource(WTPModulesResourceFactory.WTP_MODULES_URI_OBJ);
	}
	protected void runSaveOperation(IWorkspaceRunnable runnable, IProgressMonitor monitor) throws SaveFailedException {
		try {
			ResourcesPlugin.getWorkspace().run(runnable, null,IWorkspace.AVOID_UPDATE,monitor);
		} catch (CoreException e) {
			throw new SaveFailedException(e);
		}
	}
	/**
	 * @see ResourceStateValidator#checkActivation(ResourceStateValidatorPresenter)
	 */
	public void checkActivation(ResourceStateValidatorPresenter presenter) throws CoreException {
		super.checkActivation(presenter);
	}
	/**
	 * Subclasses can override - by default this will return the first resource referenced by the
	 * known resource URIs for this EditModel
	 * 
	 * @return
	 */
	public Resource getPrimaryResource() {
		// Overriden to handle loading the .component resource in new and old forms
		// First will try to load from .settings/org.eclipse.wst.common.component
		// Second will try to load from the old location(s) .settings/.component or .component

		URI uri = (URI) URI.createURI(StructureEdit.MODULE_META_FILE_NAME);
		WTPModulesResource res = (WTPModulesResource)getResource(uri);
		if (res == null || !res.isLoaded()) {
			removeResource(res);
			uri = (URI) URI.createURI(".settings/.component");
			res = (WTPModulesResource)getResource(uri);
			if (res == null || !res.isLoaded()) {
				removeResource(res);
				uri = (URI) URI.createURI(".component");
				res = (WTPModulesResource)getResource(uri);
				if (res == null || !res.isLoaded()) {
					removeResource(res);
					res = null;
				}
			}
		}
		return res;
	}
	
	public Object getAdapter(Class anAdapter) {
		return Platform.getAdapterManager().getAdapter(this, anAdapter); 
	}
	
	protected void addProjectModulesIfNecessary(XMIResource aResource) throws IOException {
		
		if (aResource != null) { 
			if(aResource.getContents().isEmpty()) {
				ProjectComponents projectModules = ComponentcorePackage.eINSTANCE.getComponentcoreFactory().createProjectComponents();
				projectModules.setProjectName(project.getName());
				aResource.getContents().add(projectModules); 
				aResource.setID(projectModules, MODULE_CORE_ID);
			}
		}
	}
	private boolean resNeedsMigrating(XMIResource res) throws CoreException {
		boolean multiComps = false;
		if (project==null)
			return false;
		boolean needsMigrating = (!project.hasNature(FacetedProjectNature.NATURE_ID)) || res == null || ((res != null) && ((WTPModulesResource)res).getRootObject() == null); 
		if (!needsMigrating) {
			if (res instanceof TranslatorResource && ((TranslatorResource)res).getRootObject() instanceof ProjectComponents) {
				ProjectComponents components = (ProjectComponents) ((WTPModulesResource)res).getRootObject();
				if (components.getComponents() != null) {
					multiComps = components.getComponents().size() > 1;
					return multiComps;
				}
			}
		}
		return needsMigrating;
	}
	protected Resource getAndLoadLocalResource(URI aUri) {
		
			Resource resource = getLocalResource(aUri);
			if (null != resource) {
				if (!resource.isLoaded()) {
					try {
						resource.load(Collections.EMPTY_MAP); // reload it
					} catch (IOException e) {
						// Ignore
					}
				}
			}
			return resource;
	}
	
	public void setUseOldFormat(boolean useOldFormat) {
		this.useOldFormat = useOldFormat;
	}
}
