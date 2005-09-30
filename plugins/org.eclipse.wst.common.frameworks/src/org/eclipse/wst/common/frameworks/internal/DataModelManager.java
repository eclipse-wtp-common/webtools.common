/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal;

import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class DataModelManager {
	private IDataModel rootDataModel;
	private HashMap nestedModelEntries; // Element = java.lang.Integer

	public DataModelManager(IDataModel rootDataModel) {
		this.rootDataModel = rootDataModel;
		nestedModelEntries = new HashMap();

		Iterator names = this.rootDataModel.getNestedModelNames().iterator();

		while (names.hasNext()) {
			String nestedModelName = (String) names.next();
			nestedModelEntries.put(nestedModelName, new Integer(1));
		}
	}

	public IDataModel getDataModel() {
		return rootDataModel;
	}

	public void addNestedDataModel(String dataModelID) {
		Integer referenceCount = (Integer) nestedModelEntries.get(dataModelID);

		if (referenceCount == null) {
			// This nested data model is not currently in the root data model.
			// Therefore, we need to create one and add it as a nested data model.
			IDataModel dataModel = DataModelFactory.createDataModel(dataModelID);

			if (dataModel != null) {
				rootDataModel.addNestedModel(dataModelID, dataModel);
				nestedModelEntries.put(dataModelID, new Integer(1));
			}
		} else {
			// We already have this data model nested in the root data model
			// so just increment the reference count.
			nestedModelEntries.put(dataModelID, new Integer(referenceCount.intValue() + 1));
		}
	}

	public void removeNestedDataModel(String dataModelID) {
		Integer referenceCount = (Integer) nestedModelEntries.get(dataModelID);

		if (referenceCount != null) {
			int newRefCount = referenceCount.intValue() - 1;

			if (newRefCount == 0) {
				// Remove the nested data model.
				nestedModelEntries.remove(dataModelID);
				rootDataModel.removeNestedModel(dataModelID);
			} else {
				// Set the table entry to the decremented ref count value.
				nestedModelEntries.put(dataModelID, new Integer(newRefCount));
			}
		}
	}
}
