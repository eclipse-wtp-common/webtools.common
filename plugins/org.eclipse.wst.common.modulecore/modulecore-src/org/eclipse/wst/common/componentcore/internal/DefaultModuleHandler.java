/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal;

import java.util.List;

import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

public class DefaultModuleHandler implements IModuleHandler {

	public String getArchiveName(IVirtualComponent comp) {
		return comp.getName() + ".jar";
	}

	public List<IVirtualComponent> getFilteredListForAdd(IVirtualComponent sourceComponent, IVirtualComponent[] availableComponents) {
		// TODO Auto-generated method stub
		return null;
	}

}
