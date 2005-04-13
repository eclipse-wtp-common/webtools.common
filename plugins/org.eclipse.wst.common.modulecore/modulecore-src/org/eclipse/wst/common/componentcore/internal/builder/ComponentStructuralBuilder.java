/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.datamodel.properties.IProjectComponentsBuilderDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class ComponentStructuralBuilder extends IncrementalProjectBuilder implements IModuleConstants, IProjectComponentsBuilderDataModelProperties {
    /**
     * Builder id of this incremental project builder.
     */
    public static final String BUILDER_ID = COMPONENT_STRUCTURAL_BUILDER_ID;
    /**
     *  
     */
    public ComponentStructuralBuilder() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
     *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        StructureEdit moduleCore = null;
        
        // clean markers
		IResource wtpmoduleFile = getProject().findMember(".wtpmodules"); //$NON-NLS-1$
		wtpmoduleFile.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE); 

        IDataModel builderDataModel = DataModelFactory.createDataModel(new ProjectComponentsBuilderDataModelProvider());
        try {
            moduleCore = StructureEdit.getStructureEditForRead(getProject());
            builderDataModel.setProperty(COMPONENT_CORE, moduleCore);
            builderDataModel.setProperty(PROJECT, getProject());
            builderDataModel.setProperty(PROJECT_DETLA, getDelta(getProject()));
            //TODO: implement incremental builds
            // dataModel.setProperty(DeployableModuleProjectBuilderDataModel.BUILD_KIND;
            IUndoableOperation op = builderDataModel.getDefaultOperation();
            if (op != null)
                op.execute(monitor, null);
        } catch (ExecutionException e) {
            Logger.getLogger().log(e.getMessage());
        } finally {
            if (null != moduleCore) {
                moduleCore.dispose();
            }
        }
        return null;
    }

    protected void clean(IProgressMonitor monitor) throws CoreException {
        IFolder[] oldOutput = StructureEdit.getOutputContainersForProject(getProject());
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