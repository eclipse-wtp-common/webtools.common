package org.eclipse.wst.common.modulecore.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.events.ResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclipse.wst.common.modulecore.ModuleStructuralModel;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
import org.eclipse.wst.common.modulecore.util.ModuleCore;

public class DeployableModuleProjectBuilderDataModel extends WTPOperationDataModel {
	/**
	 * Required, type IProject
	 */
	public static final String PROJECT = "DeployableModuleProjectBuilderDataModel.PROJECT"; //$NON-NLS-1$
	/**
	 * Required, type Integer
	 * default to FULL
	 */
	public static final String BUILD_KIND = "DeployableModuleProjectBuilderDataModel.BUILD_KIND"; //$NON-NLS-1$
	/**
	 * Required, type IResourceDelta
	 * default to FULL
	 */
	public static final String PROJECT_DETLA = "DeployableModuleProjectBuilderDataModel.PROJECT_DETLA"; //$NON-NLS-1$
	/**
	 * Required, type ModuleBuilderDataModel
	 * default to FULL
	 */
	public static final String MODULE_BUILDER_DM_LIST = "DeployableModuleProjectBuilderDataModel.MODULE_BUILDER_DM"; //$NON-NLS-1$
	/**
	 * Required, type ModuleBuilderDataModel
	 * default to FULL
	 */
	public static final String MODULE_STRUCTURAL_MODEL = "DeployableModuleProjectBuilderDataModel.MODULE_STRUCTURAL_MODEL"; //$NON-NLS-1$
	
	
	
	protected void init() {
		super.init();
	}
	
	protected void initValidBaseProperties() {
		addValidBaseProperty(PROJECT);
		addValidBaseProperty(BUILD_KIND);
		addValidBaseProperty(PROJECT_DETLA);
		addValidBaseProperty(MODULE_BUILDER_DM_LIST);
		addValidBaseProperty(MODULE_STRUCTURAL_MODEL);
	}

    public DeployableModuleProjectBuilderDataModel() {
        super();
    }
	
	/* (non-Javadoc)
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getDefaultProperty(java.lang.String)
     */
    protected Object getDefaultProperty(String propertyName) {
        if(propertyName.equals(BUILD_KIND))
            return new Integer(IncrementalProjectBuilder.FULL_BUILD);
        else if(propertyName.equals(MODULE_BUILDER_DM_LIST))
            return populateModuleBuilderDataModelList();
        return super.getDefaultProperty(propertyName);
    }

    /**
     * @return
     */
    private Object populateModuleBuilderDataModelList() {
        //TODO: delta information should be taken into consideration
        List moduleDMList = new ArrayList();
        switch (((Integer)getProperty(BUILD_KIND)).intValue()) {
	        case IncrementalProjectBuilder.FULL_BUILD:
	            moduleDMList = populateFullModuleBuilderDataModelList();
	            break;
	        case IncrementalProjectBuilder.INCREMENTAL_BUILD:
	            moduleDMList = populateDeltaModuleBuilderDataModelList((ResourceDelta)getProperty(PROJECT_DETLA));
	        	break;
	        default:
	            moduleDMList = populateFullModuleBuilderDataModelList();
	            break;
        }
        
        
        return null;
    }

    private List populateFullModuleBuilderDataModelList() {
        WorkbenchModule[] wbModules =ModuleCore.INSTANCE.getWorkbenchModules((ModuleStructuralModel)getProperty(MODULE_STRUCTURAL_MODEL));
        List moduleBuilderDataModelList = new ArrayList();
        
        if(wbModules == null) return null;
        
        DeployableModuleBuilderFactory factory = null;
        DeployableModuleBuilderDataModel dataModel = null;
        
        for(int i = 0; i<wbModules.length; i++){
            factory = DeployableModuleBuilderFactoryRegistry.INSTANCE.createDeployableFactory(wbModules[i].getModuleType().getModuleTypeId());
            if(factory != null) {
                dataModel = factory.createDeploymentModuleDataModel();
                // TODO: set relevant DM info;
                moduleBuilderDataModelList.add(factory.createDeploymentModuleDataModel());
            }
        }
        return moduleBuilderDataModelList;        
    }
    
    private List populateDeltaModuleBuilderDataModelList(ResourceDelta delta) {
        //TODO: handle delta information correcty
        return populateFullModuleBuilderDataModelList();
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel#getDefaultOperation()
     */
    public WTPOperation getDefaultOperation() {
        // TODO Auto-generated method stub
        return null;
    }

}
