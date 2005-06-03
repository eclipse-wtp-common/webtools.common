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
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.internal.ArtifactEditModel;

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
	
	/**
	 * <p>
	 * Returns an instance facade for the given anAdaptableObject, if possible.
	 * </p> 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object anAdaptableObject, Class anAdapterType) {
		if (anAdapterType == ArtifactEdit.ADAPTER_TYPE) {
			if (anAdaptableObject instanceof ArtifactEditModel)
				return new ArtifactEdit((ArtifactEditModel) anAdaptableObject);
		}
		return null;
	}

	/**  
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() {
		return new Class[]{ARTIFACT_EDIT_MODEL_CLASS};
	}

}