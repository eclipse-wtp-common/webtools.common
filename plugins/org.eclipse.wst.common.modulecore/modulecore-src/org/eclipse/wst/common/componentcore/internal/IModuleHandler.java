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

/**
 * Interface intended to reflect behavior of customized components
 *
 */
public interface IModuleHandler {
	
	String getArchiveName(IVirtualComponent comp);
	
	List<IVirtualComponent> getFilteredListForAdd(IVirtualComponent sourceComponent, IVirtualComponent[] availableComponents);
	

}
