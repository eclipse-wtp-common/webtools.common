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
package org.eclipse.wst.common.componentcore.resources;

import java.util.Properties;

import org.eclipse.core.runtime.IPath;

public interface IVirtualComponent extends IVirtualContainer {
	
	String getName();
	
	String getComponentTypeId();
	void setComponentTypeId(String aComponentTypeId);
	
	Properties getMetaProperties();
	
	IPath[] getMetaResources();
	void setMetaResources(IPath[] theMetaResourcePaths);
	

}
