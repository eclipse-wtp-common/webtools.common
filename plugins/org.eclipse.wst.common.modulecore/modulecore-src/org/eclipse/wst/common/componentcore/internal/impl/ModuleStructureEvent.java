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
