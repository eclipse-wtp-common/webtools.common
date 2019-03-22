/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/


package org.eclipse.wst.common.componentcore.ui.internal.propertypage.verifier;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.componentcore.ui.Messages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.ModuleCoreUIPlugin;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.ResourceMappingFilterExtensionRegistry;
import org.eclipse.wst.common.componentcore.ui.propertypage.AddModuleDependenciesPropertiesPage.ComponentResourceProxy;

/**
 * This class does basic validation of the deployment assembly. It validates that 
 * references to other components like projects and archives exists. It also validates that
 * folder mappings exists in the project.  
 * 
 */

public class DefaultDeploymentAssemblyVerifier extends AbstractDeploymentAssemblyVerifier {

	@Override
	public IStatus verify(DeploymentAssemblyVerifierData data) {
		IStatus status = validateResourceMappings(data, null);
		return validateMissingReferences(data, status);
	}
	
	
	protected IStatus validateResourceMappings(DeploymentAssemblyVerifierData data, IStatus existingStatus){
		IStatus status = existingStatus!=null?existingStatus:Status.OK_STATUS;
		ArrayList<ComponentResourceProxy> mappings = data.getResourceMappings();
		if (mappings == null)
			return status;		
		int severity = Status.WARNING;
		String msg = null;
		IProject project = data.getComponent().getProject();
		for (ComponentResourceProxy mapping:mappings){
			if (ResourceMappingFilterExtensionRegistry.shouldFilter(mapping.source))
				continue;  // Do not validate filtered entries
			if (!project.exists(mapping.source)){
				msg = NLS.bind(Messages.ErrorEntryNotFound, mapping.source); 
				status = appendStatusMessage(status, msg, severity);
			}
		}
		return status;
		
	}
	
	protected IStatus validateMissingReferences(DeploymentAssemblyVerifierData data, IStatus existingStatus) {
		IStatus status = existingStatus!=null?existingStatus:Status.OK_STATUS;
		ArrayList<IVirtualReference> references = data.getCurrentReferences();
		if (references == null){
			return status;
		}
		int severity = Status.WARNING;
		String msg = null;
		for (IVirtualReference reference:references){
			if (!reference.getReferencedComponent().exists()){
				String name;
				if( reference.getReferencedComponent().isBinary() ) {
					IVirtualComponent vc = reference.getReferencedComponent();
					IPath p = vc.getAdapter(IPath.class);
					name= p == null ? vc.getName() : p.toString();
				}
				else {
					name = reference.getReferencedComponent().getProject().getName();
				}
				msg = NLS.bind(Messages.ErrorEntryNotFound, name); 
				status = appendStatusMessage(status, msg, severity);
			}
		}
		return status;		
	}
	
	private IStatus appendStatusMessage(IStatus existingStatus, String message, int severity) {
        MultiStatus multiStatus;
        IStatus newStatus = new Status(severity, ModuleCoreUIPlugin.PLUGIN_ID, message);
		int newSeverity = severity;
		if(existingStatus.getSeverity() > severity)
			newSeverity = existingStatus.getSeverity();
        if(existingStatus instanceof MultiStatus){
            multiStatus = (MultiStatus)existingStatus;
            multiStatus.merge(newStatus);
        } else {
        	if(!existingStatus.isMultiStatus() && existingStatus.isOK()) {
        		return newStatus;
        	}
            IStatus [] children = new IStatus [] {existingStatus, newStatus};
            multiStatus = new MultiStatus(ModuleCoreUIPlugin.PLUGIN_ID, newSeverity, children, null, null);
        }
        return multiStatus;
    }

}
