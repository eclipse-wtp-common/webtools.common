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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;

public class LocalDependencyDelayedOperationCache {
	private static LocalDependencyDelayedOperationCache instance;
	private List delayedOperationCacheList = new ArrayList();

	private LocalDependencyDelayedOperationCache() {
		super();
	}
	
	public List getOperationCacheList() {
		return delayedOperationCacheList;
	}
	
	public void addOperationToCacheList(WTPOperation operation) {
		delayedOperationCacheList.add(operation);
	}
	
	public void clearOperationCacheList() {
	    delayedOperationCacheList.clear();
	}
	                                       
	public static LocalDependencyDelayedOperationCache getInstance() {
		if (instance == null)
			instance = new LocalDependencyDelayedOperationCache();
		return instance;
	}
}
