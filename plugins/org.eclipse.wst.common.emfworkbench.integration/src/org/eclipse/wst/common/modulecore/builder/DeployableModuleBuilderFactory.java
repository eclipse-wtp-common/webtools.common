
package org.eclipse.wst.common.modulecore.builder;



public interface DeployableModuleBuilderFactory {
    /**
     *
     * This method is used to create a fully populated data model for the 
     * DeployableModuleProjectBuilderDataModel to then run on.  
     * 
     */
   public DeployableModuleBuilderDataModel createDeploymentModuleDataModel();
}
