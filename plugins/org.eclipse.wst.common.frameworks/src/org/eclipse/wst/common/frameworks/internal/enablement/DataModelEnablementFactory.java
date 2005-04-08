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
package org.eclipse.wst.common.frameworks.internal.enablement;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.DataModelProviderDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

public class DataModelEnablementFactory {
    /**
     * Looks up the appropriate IDataModelProvider by the specified providerKind
     * String and a context of the containing Project. The method gets an array
     * of DataModelProviderDescriptor from the base DataModelFactory then
     * filters out Providers based on function group enablement. Finally the
     * Provider with the highest priority is returned. If the IDataModelProvider
     * is not found then a RuntimeException is logged and null is returned.
     * 
     * @param providerKind
     *            the String id of the provider kind
     * @param curProject
     *            the containing IProject
     * 
     * @return a new IDataModel
     */
    public static IDataModel createDataModel(String providerKind, IProject curProject) {
        DataModelProviderDescriptor[] providers = DataModelFactory.getProviderDescriptorsForProviderKind(providerKind);
        if (providers == null || providers.length == 0)
            return null;
        DataModelProviderDescriptor topProvider = getHighestPriorityEnabledProviderDesc(providers, curProject);
        if (topProvider == null)
            return null;
        IDataModelProvider provider = topProvider.createProviderInstance();
        if(provider == null)
            return null;
        return DataModelFactory.createDataModel(topProvider.createProviderInstance());
    }

    private static DataModelProviderDescriptor getHighestPriorityEnabledProviderDesc(DataModelProviderDescriptor[] providers, IProject curProject) {
        DataModelProviderDescriptor tempDesc;
        DataModelProviderDescriptor baseDesc = providers[0];
        IEnablementIdentifier enablementIdentifier;
        DataModelProviderDescriptor topPriorityDesc = null;

        for (int i = 1; i < providers.length; i++) {
            tempDesc = providers[i];
            enablementIdentifier = EnablementManager.INSTANCE.getIdentifier(tempDesc.getID(), curProject);
            if (enablementIdentifier.isEnabled()) {
                if (topPriorityDesc == null)
                    topPriorityDesc = tempDesc;
                else if (IdentifiableComparator.instance.compare(tempDesc, topPriorityDesc) == IdentifiableComparator.GREATER_THAN)
                    topPriorityDesc = tempDesc;
            }
        }
        if(topPriorityDesc == null)
            return baseDesc;
        return topPriorityDesc;
    }
}
