
package org.eclipse.wst.common.modulecore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import com.ibm.wtp.common.logger.proxy.Logger;

// in progress...

public class ModuleUtil implements IModuleConstants {

    public static boolean isFlexibleProject(IProject project) {
        IProjectNature nature = null;
        try {
            //should check 
            if (project.isAccessible())
             nature = project.getNature(MODULE_NATURE_ID);
        } catch (CoreException e) {
            Logger.getLogger().write(e);
        }
        return nature != null;

    }

}
