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

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class ReferencedComponentBuilderDelayedDataModelCache {
	private static ReferencedComponentBuilderDelayedDataModelCache instance;
	private List list = new ArrayList();

	private ReferencedComponentBuilderDelayedDataModelCache() {
		super();
	}
	
	public List getCacheList() {
		return list;
	}
	
	public void addToCache(IDataModel dataModel) {
		list.add(dataModel);
	}
	
	public void clearCache() {
	    list.clear();
	}
	                                       
	public static ReferencedComponentBuilderDelayedDataModelCache getInstance() {
		if (instance == null)
			instance = new ReferencedComponentBuilderDelayedDataModelCache();
		return instance;
	}
}
