/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.wst.common.modulecore.internal.builder;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.frameworks.operations.WTPOperation;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.internal.util.IModuleConstants;

public class DeployableModuleBuilder extends IncrementalProjectBuilder implements IModuleConstants {
    /**
     * Builder id of this incremental project builder.
     */
    public static final String BUILDER_ID = DEPLOYABLE_MODULE_BUILDER_ID;

    /**
     *  
     */
    public DeployableModuleBuilder() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
     *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        DeployableModuleProjectBuilderDataModel dataModel = null;
        ModuleCore moduleCore = null;
        try {
            moduleCore = ModuleCore.getModuleCoreForRead(getProject());
            dataModel = new DeployableModuleProjectBuilderDataModel();
            dataModel.setProperty(DeployableModuleProjectBuilderDataModel.MODULE_CORE, moduleCore);
            dataModel.setProperty(DeployableModuleProjectBuilderDataModel.PROJECT, getProject());
            dataModel.setProperty(DeployableModuleProjectBuilderDataModel.PROJECT_DETLA, getDelta(getProject()));
            // TODO: current implementation is for full build only...implement
            // in M4
            // dataModel.setProperty(DeployableModuleProjectBuilderDataModel.BUILD_KIND,
            // new Integer(kind));
            WTPOperation op = dataModel.getDefaultOperation();
            if (op != null)
                try {
                    op.run(monitor);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            return null;
        } finally {
            if (null != moduleCore) {
                moduleCore.dispose();
            }
        }
    }

    protected void clean(IProgressMonitor monitor) throws CoreException {
        IFolder[] oldOutput = ModuleCore.getOutputContainersForProject(getProject());
        if(oldOutput != null) {
            for(int i = 0; i < oldOutput.length; i++) {
                oldOutput[i].delete(true, monitor);
            }
        }
        super.clean(monitor);
    }

    /**
     * @param sourceResource
     * @param absoluteInputContainer
     * @param monitor
     * @throws CoreException
     */
    //TODO this is a bit sloppy; there must be existing API somewhere.
    public static void smartCopy(IResource sourceResource, IPath absoluteOutputContainer, NullProgressMonitor monitor) throws CoreException {
        Resource targetResource = ((Workspace) ResourcesPlugin.getWorkspace()).newResource(absoluteOutputContainer, sourceResource.getType());
        if (!targetResource.exists()) {
            sourceResource.copy(absoluteOutputContainer, true, monitor);
        } else if (sourceResource.getType() == Resource.FOLDER) {
            IFolder folder = (IFolder) sourceResource;
            IResource[] members = folder.members();
            for (int i = 0; i < members.length; i++) {
                smartCopy(members[i], absoluteOutputContainer.append(IPath.SEPARATOR + members[i].getName()), monitor);
            }
        } else {
            //TODO present a warning to the user about duplicate resources
        }
    }
}