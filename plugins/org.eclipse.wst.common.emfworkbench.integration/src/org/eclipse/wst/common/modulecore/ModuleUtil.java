/*
 * Created on Feb 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.modulecore;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import com.ibm.wtp.common.logger.proxy.Logger;

// in progress...

public class ModuleUtil implements IModuleConstants {

    public static boolean isFlexableProject(Project project) {
        IProjectNature nature = null;
        try {
            nature = project.getNature(MODULE_NATURE_ID);
        } catch (CoreException e) {
            Logger.getLogger().write(e);
        }
        return nature != null;

    }

}
