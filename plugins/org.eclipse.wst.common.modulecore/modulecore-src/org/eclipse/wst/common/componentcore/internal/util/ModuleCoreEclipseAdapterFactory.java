/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.wst.common.componentcore.internal.util;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.ModuleStructuralModel;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ModuleCoreEclipseAdapterFactory implements IAdapterFactory {
	
	private static final Class MODULE_CORE_CLASS = StructureEdit.class;
	private static final Class VIRTUAL_COMPONENT_CLASS = IVirtualComponent.class;
	private static final Class[] ADAPTER_LIST = new Class[] { MODULE_CORE_CLASS, VIRTUAL_COMPONENT_CLASS};


	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptable, Class anAdapterType) {
		if(anAdapterType == MODULE_CORE_CLASS)
				return new StructureEdit((ModuleStructuralModel)adaptable);
		if(anAdapterType == VIRTUAL_COMPONENT_CLASS)
			return getComponent((IResource)adaptable);
		return null;
	}

	private Object getComponent(IResource resource) {
		StructureEdit moduleCore = null;
		WorkbenchComponent module = null;
		if (!resource.exists()) return null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(resource.getProject());
			if (moduleCore.getWorkbenchModules().length == 1)
				module = moduleCore.getWorkbenchModules()[0];
			else {
				ComponentResource[] resources = moduleCore.findResourcesBySourcePath(resource.getFullPath());
				for (int i = 0; i < resources.length; i++) {
					module = resources[i].getComponent();
					if (module != null)
						break;
				}
			}
		} catch (UnresolveableURIException e) {
			// Ignore
		} finally {
			if (moduleCore != null)
				moduleCore.dispose();
		}
		return ComponentCore.createComponent(resource.getProject(), module.getName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() { 
		return ADAPTER_LIST;
	}

}
