/*
 * Created on Feb 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.modulecore.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;

/**
 * @author jialin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
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
