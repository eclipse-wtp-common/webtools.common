/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
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
