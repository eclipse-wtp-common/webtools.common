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

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.ModuleStructuralModel;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ModuleCoreEclipseAdapterFactory implements IAdapterFactory {
	
	private static final Class MODULE_CORE_CLASS = StructureEdit.class;
	private static final Class[] ADAPTER_LIST = new Class[] { MODULE_CORE_CLASS };


	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object aModuleStructuralModel, Class anAdapterType) {
		if(aModuleStructuralModel instanceof ModuleStructuralModel) 
			if(anAdapterType == MODULE_CORE_CLASS)
				return new StructureEdit((ModuleStructuralModel)aModuleStructuralModel);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() { 
		return ADAPTER_LIST;
	}

}
