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
package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;

public class ComponentStructuralBuilderDelayedDataModelCache {
	private static ComponentStructuralBuilderDelayedDataModelCache instance;
	private List list = new ArrayList();

	private ComponentStructuralBuilderDelayedDataModelCache() {
		super();
	}
	
	public List getCacheList() {
		return list;
	}
	
	public void addToCache(WTPOperationDataModel dataModel) {
		list.add(dataModel);
	}
	
	public void clearCache() {
	    list.clear();
	}
	                                       
	public static ComponentStructuralBuilderDelayedDataModelCache getInstance() {
		if (instance == null)
			instance = new ComponentStructuralBuilderDelayedDataModelCache();
		return instance;
	}
}
