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
package org.eclipse.wst.common.frameworks.datamodel;

import org.eclipse.wst.common.frameworks.internal.enablement.Identifiable;

public class DataModelProviderDescriptor implements Identifiable{

    private String providerId;
    
    public DataModelProviderDescriptor(String providerId) {
        super();
        this.providerId = providerId;
    }

    public IDataModelProvider createProviderInstance(){   
        return DataModelFactory.loadProvider(providerId);
    }

    public String getID() {
        return providerId;
    }
    
    public int getLoadOrder() {
        return 0;
    }
}
