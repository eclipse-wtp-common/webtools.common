/*
 * Created on Jan 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.modulecore;

import org.eclipse.wst.common.modulecore.internal.impl.ModuleStructureEvent;


public interface IModuleStructureListener {
    
    public void structureChanged(ModuleStructureEvent event);

}
