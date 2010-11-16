/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.internal.propertypage.verifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.Messages;
import org.eclipse.wst.common.componentcore.ui.ModuleCoreUIPlugin;
import org.eclipse.wst.common.componentcore.ui.propertypage.AddModuleDependenciesPropertiesPage.ComponentResourceProxy;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IRuntime;

public class DeploymentAssemblyVerifierHelper {
	
	
	/**
	 * This method will use the facet framework to find all registered verifiers based on installed facets and runtimes.
	 * All the verifiers will have an opportunity to verify and return status on the proposed component mappings
	 * @param component
	 * @param runtime
	 * @param resourceMappingsChanged 
	 * @param resourceMappings 
	 * @param currentReferences 
	 * @return IStatus
	 */
	public static IStatus verify(IVirtualComponent component,IRuntime runtime, ArrayList<IVirtualReference> currentReferences, ArrayList<ComponentResourceProxy> resourceMappings, boolean resourceMappingsChanged) {
		IProject project = component.getProject();
		
		List verifiers = collectAllVerifiers(project,runtime);
		if (verifiers.isEmpty()) return Status.OK_STATUS;
		MultiStatus masterStatus = new MultiStatus(ModuleCoreUIPlugin.PLUGIN_ID, IStatus.OK, Messages.DeploymentAssemblyVerifierHelper_0,null); 
		
		for (int i = 0; i < verifiers.size(); i++) {
			if (!(verifiers.get(i) instanceof IConfigurationElement))
				continue;
			IDeploymentAssemblyVerifier verifier = null;
			try {
				verifier = (IDeploymentAssemblyVerifier) ((IConfigurationElement) verifiers.get(i)).createExecutableExtension(VerifierRegistryReader.VERIFIER_CLASS);
				DeploymentAssemblyVerifierData data = new DeploymentAssemblyVerifierData(component, runtime,currentReferences,resourceMappings,resourceMappingsChanged);
				IStatus verifyStatus = verifier.verify(data);
				if(verifyStatus != null && verifyStatus.isMultiStatus()) {
					masterStatus.addAll(verifyStatus);
				} else {
					masterStatus.add(verifyStatus);
				}
			} catch (Exception e) {
				ModuleCoreUIPlugin.log(e);
				continue;
			}
			
		}
		return masterStatus;
	}

	/**
	 * @param project
	 * @param runtime
	 * @return all verifiers by iterating through projects installed facets, and querying the registry
	 */
	public static List collectAllVerifiers(IProject project, IRuntime runtime) {
		IFacetedProject fProj = null;
		try {
			fProj = ProjectFacetsManager.create(project);
		} catch (CoreException e) {
			ModuleCoreUIPlugin.log(e);
			return Collections.EMPTY_LIST;
		}
		Set<IProjectFacetVersion> facets = fProj.getProjectFacets();
		List verifiers = new ArrayList();
		for (Iterator iterator = facets.iterator(); iterator.hasNext();) {
			IProjectFacetVersion facet = (IProjectFacetVersion)iterator.next();
			verifiers.addAll(VerifierRegistry.instance().getVerifierExtensions(facet.getProjectFacet().getId(), runtime));	
		}
		return verifiers;
	}
	
}
