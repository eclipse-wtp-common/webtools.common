/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

/**
 * @author mdelder
 *  
 */
public class AdaptabilityUtility {


	public static Object getAdapter(Object element, Class adapter) {
		if (element == null)
			return null;
		else if (element instanceof IAdaptable)
			return ((IAdaptable) element).getAdapter(adapter);
		else
			return Platform.getAdapterManager().getAdapter(element, adapter);
	}

}