/*
 * Created on Jan 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.modulecore;

import java.util.EventObject;

//in progress...

public class ModuleStructureEvent extends EventObject {
    private ModuleResource[] resources;

    public ModuleStructureEvent(Object source) {
        super(source);
    }
    
    public ModuleStructureEvent(Object source, ModuleResource[] moduleResources) {
        super(source);
        resources = moduleResources;
        
    }

    public ModuleResource[] getMoudleResources() {
        return resources;
    }
  
}
