/*
 * Created on Feb 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.modulecore.builder;

import java.util.List;

/**
 * @author jialin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LocalDependencyDelayedOperationCache {
	private static LocalDependencyDelayedOperationCache instance;
	private List delayedOperationCacheList;

	public LocalDependencyDelayedOperationCache() {
		super();
	}
	
	public List getOperationCacheList() {
		return delayedOperationCacheList;
	}
	public void setOperationCacheList(List delayedOperationCacheList) {
		this.delayedOperationCacheList = delayedOperationCacheList;
	}
	public LocalDependencyDelayedOperationCache getInstance() {
		if (instance == null)
			instance = new LocalDependencyDelayedOperationCache();
		return instance;
	}
}
