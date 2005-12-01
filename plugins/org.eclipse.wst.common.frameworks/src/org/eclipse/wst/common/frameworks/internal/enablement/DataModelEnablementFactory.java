/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class DataModelEnablementFactory {
	/**
	 * Looks up the appropriate IDataModelProvider by the specified providerKind String and a
	 * context of the containing Project. The method gets an array of DataModelProviderDescriptor
	 * from the base DataModelFactory then filters out Providers based on function group enablement.
	 * Finally the Provider with the highest priority is returned. If the IDataModelProvider is not
	 * found then a RuntimeException is logged and null is returned.
	 * 
	 * @param providerKind
	 *            the String id of the provider kind
	 * @param curProject
	 *            the containing IProject
	 * 
	 * @return a new IDataModel
	 */
	public static IDataModel createDataModel(String providerKind, IProject curProject) {
		String[] providerIDs = DataModelFactory.getDataModelProviderIDsForKind(providerKind);
		if (providerIDs == null || providerIDs.length == 0)
			return null;
		String topProvider = getHighestPriorityEnabledProviderDesc(providerIDs, curProject);
		if (topProvider == null)
			return null;
		return DataModelFactory.createDataModel(topProvider);
	}

	private static String getHighestPriorityEnabledProviderDesc(String[] providerIDs, IProject curProject) {
		String defaultID = providerIDs[0];
		IEnablementIdentifier enablementIdentifier;
		Identifiable tempIdentifiable;
		Identifiable topPriorityIdentifiable = null;

		for (int i = 1; i < providerIDs.length; i++) {
			tempIdentifiable = new AbstractIdentifiable(providerIDs[i]);
			enablementIdentifier = EnablementManager.INSTANCE.getIdentifier(tempIdentifiable.getID(), curProject);
			if (enablementIdentifier.isEnabled()) {
				if (topPriorityIdentifiable == null)
					topPriorityIdentifiable = tempIdentifiable;
				else if (IdentifiableComparator.instance.compare(tempIdentifiable, topPriorityIdentifiable) == IdentifiableComparator.GREATER_THAN)
					topPriorityIdentifiable = tempIdentifiable;
			}
		}
		if (topPriorityIdentifiable == null)
			return defaultID;
		return topPriorityIdentifiable.getID();
	}
}
