package org.eclipse.wst.common.modulecore.builder;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.modulecore.IModuleConstants;
import org.eclipse.wst.common.modulecore.ModuleStructuralModel;
import org.eclipse.wst.common.modulecore.util.ModuleCore;

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

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
	    ModuleStructuralModel structuralModel = null;
        try{
            structuralModel = ModuleCore.INSTANCE.getModuleStructuralModelForRead(getProject(), this);
		    DeployableModuleProjectBuilderDataModel dataModel = new DeployableModuleProjectBuilderDataModel();
		    dataModel.setProperty(DeployableModuleProjectBuilderDataModel.PROJECT, getProject());
		    dataModel.setProperty(DeployableModuleProjectBuilderDataModel.PROJECT_DETLA, getDelta(getProject()));
		    dataModel.setProperty(DeployableModuleProjectBuilderDataModel.MODULE_STRUCTURAL_MODEL, structuralModel);
		    //TODO: current implementation is for full build only...implement in M4
		    //dataModel.setProperty(DeployableModuleProjectBuilderDataModel.BUILD_KIND, new Integer(kind));
		    WTPOperation op = dataModel.getDefaultOperation();
		    if(op != null)
	            try {
	                op.run(monitor);
	            } catch (InvocationTargetException e) {
	                e.printStackTrace();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
        } finally {
            if(structuralModel != null)
                structuralModel.releaseAccess(this);
        }
		return null;
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		URI rootOutputURI = ModuleCore.INSTANCE.getProjectRootOutputContainer(getProject());
		//remove entire .deployables
		super.clean(monitor);
    }
}
