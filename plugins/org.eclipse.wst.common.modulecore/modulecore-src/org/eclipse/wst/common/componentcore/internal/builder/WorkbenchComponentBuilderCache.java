/*******************************************************************************
 * Copyright (c) 2003, 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.HashMap;


public class WorkbenchComponentBuilderCache {
    private HashMap extensionMap = new HashMap();
    /**
     * 
     */
    public WorkbenchComponentBuilderCache() {
        super();
    }
    
    public  void addComponentStructrualBuilderForID(WorkbenchComponentBuilderDataModelProvider builderInstance, String componentID){
        extensionMap.put(componentID, builderInstance);
    }

    public WorkbenchComponentBuilderDataModelProvider getAvailableComponentStructuralBuilderForID(String componentID) {
        if(extensionMap.containsKey(componentID))
            return (WorkbenchComponentBuilderDataModelProvider)extensionMap.get(componentID);
        return null;
    }
}
