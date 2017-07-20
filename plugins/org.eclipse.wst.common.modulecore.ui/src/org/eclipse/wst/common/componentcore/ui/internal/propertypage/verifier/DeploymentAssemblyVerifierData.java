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

import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.propertypage.AddModuleDependenciesPropertiesPage.ComponentResourceProxy;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

public class DeploymentAssemblyVerifierData {
	
	public DeploymentAssemblyVerifierData(IVirtualComponent component, IRuntime runtime, ArrayList<IVirtualReference> currentReferences,
			ArrayList<ComponentResourceProxy> resourceMappings, boolean resourceMappingsChanged) {
		super();
		this.component = component;
		this.runtime = runtime;
		this.currentReferences = currentReferences;
		this.resourceMappings = resourceMappings;
		this.resourceMappingsChanged = resourceMappingsChanged;
	}
	private IVirtualComponent component;
	private IRuntime runtime;
	private ArrayList<IVirtualReference> currentReferences;
	private ArrayList<ComponentResourceProxy> resourceMappings;
	private boolean resourceMappingsChanged;
	public IVirtualComponent getComponent() {
		return component;
	}
	public void setComponent(IVirtualComponent component) {
		this.component = component;
	}
	
	/**
	 * Return the facet runtime (NOT SERVER RUNTIME) associated with this data
	 * @return
	 */
	public IRuntime getRuntime() {
		return runtime;
	}
	/**
	 * Set the facet runtime (NOT SERVER RUNTIME) to be associated with this data
	 * @param runtime set the runtime 
	 * @return
	 */
	public void setRuntime(IRuntime runtime) {
		this.runtime = runtime;
	}
	public ArrayList<IVirtualReference> getCurrentReferences() {
		return currentReferences;
	}
	public void setCurrentReferences(ArrayList<IVirtualReference> currentReferences) {
		this.currentReferences = currentReferences;
	}
	public ArrayList<ComponentResourceProxy> getResourceMappings() {
		return resourceMappings;
	}
	public void setResourceMappings(ArrayList<ComponentResourceProxy> resourceMappings) {
		this.resourceMappings = resourceMappings;
	}
	public boolean isResourceMappingsChanged() {
		return resourceMappingsChanged;
	}
	public void setResourceMappingsChanged(boolean resourceMappingsChanged) {
		this.resourceMappingsChanged = resourceMappingsChanged;
	}

}
