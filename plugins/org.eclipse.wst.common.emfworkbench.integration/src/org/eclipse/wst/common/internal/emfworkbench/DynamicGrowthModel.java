/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;


public class DynamicGrowthModel {
	
	private static final int MINIMUM_OPTIMAL_SIZE = 10;

	private static final int MEMORY_THRESHHOLD = 100;
	
	private static final int NOTICEABLE_CHANGE = 5;
	
	
	/* Stores a FIFO list of the Key types (IPath)*/
	private final List queue = new LinkedList();
	
	/* We use an int[] as the value so we don't have to keep creating Integer objects */
	private final Map/* <IPath, int[]> */ uniquesMap = new HashMap();
	
	
	/**
	 * Inject the key into the DynamicGrowthModel. May or may not affect the 
	 * dynamic size.
	 *
	 * @param key The key to inject into the model
	 * @return True if the optimal size changed greather than {@value NOTICEABLE_CHANGE} as a result of the injection.
	 */
	public synchronized boolean injectKey(IPath key) {
		
		int originalSize = getOptimalSize();
		
		int[] count = null;
		if( (count = (int[]) uniquesMap.get(key)) != null ) {
			/* increment the count */
			++count[0];
		} else {
			/* insert the first count */
			uniquesMap.put(key, count = new int[] { 1 } );
		}
		
		if( queue.size() == MEMORY_THRESHHOLD ) {
			
			/* take the oldest value off the queue */
			IPath oldestKey = (IPath) queue.remove(0);
			
			/* determine if another instance of the oldest key is still in the queue */
			count = (int[]) uniquesMap.get(oldestKey);
			Assert.isNotNull(count);
			
			/* Reduce the count */
			count[0] -= 1;
		
			/* Count should never be negative */
			Assert.isTrue(count[0] >= 0);
			
		
			/* This unique key is no longer in the queue*/
			if(count[0] == 0) {
				uniquesMap.remove(oldestKey);
			}
			
			
			
		}
		/* Add the newKey to end of the list*/
		queue.add(key);
		
		return Math.abs( originalSize - getOptimalSize() ) > NOTICEABLE_CHANGE;

	}
	
	/**
	 * The optimal size is an integer from [{@value #MINIMUM_OPTIMAL_SIZE}, {@value #MEMORY_THRESHHOLD}]. 
	 * 
	 * @return the optimal size for the LRU Cache.  
	 */
	public int getOptimalSize() {		
		return uniquesMap.size() > MINIMUM_OPTIMAL_SIZE ? uniquesMap.size() : MINIMUM_OPTIMAL_SIZE;
	}

}	
