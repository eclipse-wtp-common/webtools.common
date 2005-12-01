/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.internal.ArtifactEditModel;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.internal.impl.ArtifactEditModelFactory;
import org.eclipse.wst.common.componentcore.internal.impl.ModuleURIUtil;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.internal.emfworkbench.edit.EditModelRegistry;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * <p>
 * The following class is not intended to be used by clients.
 * </p>
 * <p>
 * Adapts {@see ArtifactEditModel} to an {@see ArtifactEdit) 
 * instance facade, if possible. The following class is 
 * registered with the Platform Adapter Manager in 
 * {@see ModulecorePlugin#start(BundleContext)}
 * </p>
 * @see ModulecorePlugin
 */
public class ArtifactEditAdapterFactory implements IAdapterFactory {

	private static final Class ARTIFACT_EDIT_MODEL_CLASS = ArtifactEditModel.class;
	private static final Class ARTIFACT_EDIT_CLASS = ArtifactEdit.class;
	
	/**
	 * <p>
	 * Returns an instance facade for the given anAdaptableObject, if possible.
	 * </p> 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object anAdaptableObject, Class anAdapterType) {
		if (anAdapterType == ArtifactEditModel.ADAPTER_TYPE) {
			if (anAdaptableObject instanceof ArtifactEdit) {
				ArtifactEdit edit = (ArtifactEdit)anAdaptableObject;
				IProject aProject = edit.getProject();
				URI componentURI = ModuleURIUtil.fullyQualifyURI(aProject);
				ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(edit.getComponent().getProject());
				Map params = new HashMap();
				params.put(ArtifactEditModelFactory.PARAM_MODULE_URI, componentURI);
				try {
					IFacetedProject facetedProject = ProjectFacetsManager.create(aProject);
					String[] editModelIDs = EditModelRegistry.getInstance().getRegisteredEditModelIDs();
					for (int i=0; i<editModelIDs.length; i++) {
						try {
							IProjectFacet facet = ProjectFacetsManager.getProjectFacet(editModelIDs[i]);
							if (facet != null && facetedProject.hasProjectFacet(facet)) {
								EditModel editModel = nature.getExistingEditModel(editModelIDs[i],params,edit.isReadOnly());
								if (editModel !=null)
									return editModel;
							}
						} catch (Exception e) {
							continue;
						}
					}
					
				} catch (Exception e){
					//Return null
				}
				return null;
			}
		}
		if (anAdapterType == ArtifactEdit.ADAPTER_TYPE) {
			if (anAdaptableObject instanceof ArtifactEditModel)
				return new ArtifactEdit((ArtifactEditModel) anAdaptableObject);
			if (anAdaptableObject instanceof IVirtualComponent) {
				ArtifactEditRegistryReader reader = ArtifactEditRegistryReader.instance();
	    		IArtifactEditFactory factory = reader.getArtifactEdit(((IVirtualComponent)anAdaptableObject).getProject());
	    		if (factory != null)
	    			return factory.createArtifactEditForRead((IVirtualComponent)anAdaptableObject);
			}
		}
		return null;
	}

	/**  
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() {
		return new Class[]{ARTIFACT_EDIT_MODEL_CLASS,ARTIFACT_EDIT_CLASS};
	}

}
