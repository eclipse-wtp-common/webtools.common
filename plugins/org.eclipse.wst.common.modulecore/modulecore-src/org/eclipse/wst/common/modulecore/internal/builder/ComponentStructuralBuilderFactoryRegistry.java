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
package org.eclipse.wst.common.modulecore.internal.builder;

import java.util.HashMap;

public class ComponentStructuralBuilderFactoryRegistry {
    public static ComponentStructuralBuilderFactoryRegistry INSTANCE = new ComponentStructuralBuilderFactoryRegistry();

    private HashMap factories;
    /**
     * 
     */
    public ComponentStructuralBuilderFactoryRegistry() {
        super();
    }

    public void registerDeployableFactory(String id, ComponentStructuralBuilderFactory factoryClassName){
        if(factories == null)
            factories = new HashMap();
        factories.put(id, factoryClassName);
    }
    
    public ComponentStructuralBuilderFactory createDeployableFactory(String id) {
        if(factories == null) return null;
        return (ComponentStructuralBuilderFactory)factories.get(id);
    }
}
