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
package org.eclipse.wst.common.modulecore.util;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.wst.common.modulecore.ModuleStructuralModel;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ModuleCoreEclipseAdapterFactory implements IAdapterFactory {
	
	private static final Class MODULE_CORE_CLASS = ModuleCore.class;
	private static final Class[] ADAPTER_LIST = new Class[] { MODULE_CORE_CLASS };


	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object aModuleStructuralModel, Class anAdapterType) {
		if(aModuleStructuralModel instanceof ModuleStructuralModel) 
			if(anAdapterType == MODULE_CORE_CLASS)
				return new ModuleCore((ModuleStructuralModel)aModuleStructuralModel);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() { 
		return ADAPTER_LIST;
	}

}
