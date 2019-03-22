/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Jan 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.componentcore.internal.impl;

import java.util.EventObject;

import org.eclipse.wst.common.componentcore.internal.ComponentResource;

//in progress...

public class ModuleStructureEvent extends EventObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ComponentResource[] resources;

    public ModuleStructureEvent(Object source) {
        super(source);
    }
    
    public ModuleStructureEvent(Object source, ComponentResource[] theModuleResources) {
        super(source);
        resources = theModuleResources;
        
    }

    public ComponentResource[] getMoudleResources() {
        return resources;
    }
  
}
