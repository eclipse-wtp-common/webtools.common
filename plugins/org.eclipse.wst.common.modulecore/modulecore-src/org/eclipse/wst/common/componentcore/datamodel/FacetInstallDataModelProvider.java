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
package org.eclipse.wst.common.componentcore.datamodel;

import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;

public class FacetInstallDataModelProvider 
    extends FacetDataModelProvider
    implements IActionConfigFactory {

	public FacetInstallDataModelProvider() {
		super();
	}

	public Object getDefaultProperty(String propertyName) {
		if (FACET_TYPE.equals(propertyName)) {
			return FACET_TYPE_INSTALL;
		}
		return super.getDefaultProperty(propertyName);
	}

	public boolean propertySet(String propertyName, Object propertyValue) {
		if (FACET_TYPE.equals(propertyName)) {
			throw new RuntimeException();
		}
		return super.propertySet(propertyName, propertyValue);
	}

    public Object create()
    {
        return DataModelFactory.createDataModel( this );
    }

}