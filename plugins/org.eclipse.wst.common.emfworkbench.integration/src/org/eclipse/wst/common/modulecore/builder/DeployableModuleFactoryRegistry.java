package org.eclipse.wst.common.modulecore.builder;

import java.util.HashMap;

public class DeployableModuleFactoryRegistry {
    public static DeployableModuleFactoryRegistry INSTANCE = new DeployableModuleFactoryRegistry();

    private HashMap factories;
    /**
     * 
     */
    public DeployableModuleFactoryRegistry() {
        super();
    }

    public void registerDeployableFactory(String id, DeployableModuleFactory factoryClassName){
        if(factories == null)
            factories = new HashMap();
        factories.put(id, factoryClassName);
    }
    
    public DeployableModuleFactory createDeployableFactory(String id) {
        if(factories == null) return null;
        return (DeployableModuleFactory)factories.get(id);
    }
}
