package org.eclipse.wst.common.modulecore.builder;

import java.util.HashMap;

public class DeployableModuleBuilderFactoryRegistry {
    public static DeployableModuleBuilderFactoryRegistry INSTANCE = new DeployableModuleBuilderFactoryRegistry();

    private HashMap factories;
    /**
     * 
     */
    public DeployableModuleBuilderFactoryRegistry() {
        super();
    }

    public void registerDeployableFactory(String id, DeployableModuleBuilderFactory factoryClassName){
        if(factories == null)
            factories = new HashMap();
        factories.put(id, factoryClassName);
    }
    
    public DeployableModuleBuilderFactory createDeployableFactory(String id) {
        if(factories == null) return null;
        return (DeployableModuleBuilderFactory)factories.get(id);
    }
}
