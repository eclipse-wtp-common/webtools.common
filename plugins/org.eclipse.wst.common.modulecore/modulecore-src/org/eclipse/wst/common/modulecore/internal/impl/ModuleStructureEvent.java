/*
 * Created on Jan 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.modulecore.internal.impl;

import java.util.EventObject;

import org.eclipse.wst.common.modulecore.WorkbenchModuleResource;

//in progress...

public class ModuleStructureEvent extends EventObject {
    private WorkbenchModuleResource[] resources;

    public ModuleStructureEvent(Object source) {
        super(source);
    }
    
    public ModuleStructureEvent(Object source, WorkbenchModuleResource[] theModuleResources) {
        super(source);
        resources = theModuleResources;
        
    }

    public WorkbenchModuleResource[] getMoudleResources() {
        return resources;
    }
  
}
