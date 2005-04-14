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

/**
 * Represents a component as defined by the .wtpmodules file. 
 * <p>
 * A component
 * is a container of virtual resources which has other features
 * that describe the component including:
 * <ul>
 * 	<li>{@link #getComponentTypeId()}
 * </p>
 */
public interface IVirtualComponent extends IVirtualContainer {
	
	String getName();
	
	String getComponentTypeId();
	void setComponentTypeId(String aComponentTypeId);
	
	Properties getMetaProperties();
	
	IPath[] getMetaResources();
	void setMetaResources(IPath[] theMetaResourcePaths);
	
	IVirtualResource[] getResources(String aResourceType);
	
	IVirtualReference[] getReferences();
	void setReferences(IVirtualReference[] references);
	

}
