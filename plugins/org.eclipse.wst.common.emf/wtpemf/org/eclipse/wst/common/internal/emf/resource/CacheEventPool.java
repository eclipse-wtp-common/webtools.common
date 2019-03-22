/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;


import java.util.ArrayList;
import java.util.Collection;

import org.xml.sax.Attributes;

/**
 * Provides an instance pool of reusable CacheEventNodes. The pool will default to 10 live
 * instances. When its available instances reach five times its initial capacity, it will shrink
 * itself down to the initial capacity.
 * 
 * @author mdelder
 */
public class CacheEventPool {

	public static final int DEFAULT_CAPACITY = 10;
	public static final int DEFAULT_CAPACITY_INCREMENT = 25;

	private int poolCapacity = DEFAULT_CAPACITY;
	private CacheEventStack availablePool = null;
	private Collection inusePool = null;

	public CacheEventPool() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * Create a CacheEventPOol with the given initial capacity
	 * 
	 * @param initialCapacity
	 *            The number of available instances to create.
	 */
	public CacheEventPool(int initialCapacity) {
		poolCapacity = (initialCapacity > 0) ? initialCapacity : DEFAULT_CAPACITY;
		availablePool = new CacheEventStack();
		inusePool = new ArrayList(poolCapacity);
	}

	/**
	 * Create a CacheEventNode (CENO) initialized to use the given resource as its EMF Owner.
	 * 
	 * THIS METHOD SHOULD ONLY BE USED TO CREATE ROOT NODES.
	 * 
	 * @param resource
	 *            the resource that will be populated
	 * @return a CacheEventNode to serve as the root.
	 */
	public CacheEventNode createCacheEventNode(TranslatorResource resource) {
		CacheEventNode adapter = fetchFreeNode();
		adapter.init(resource);
		return adapter;
	}

	/**
	 * Create child CacheEventNodes (CENOs) that will branch from the given parent.
	 * 
	 * @param parent
	 *            the containing CENO
	 * @param nodeName
	 *            The value of the XML element node name
	 * @param attributes
	 *            The attributes that were part of the given XML element
	 * @return A CENO that has been properly initialized.
	 */
	public CacheEventNode createCacheEventNode(CacheEventNode parent, String nodeName, Attributes attributes) {
		CacheEventNode adapter = fetchFreeNode();
		adapter.init(parent, nodeName, attributes);
		return adapter;
	}

	/**
	 * Release the CacheEventNode CENO back to the pool of availabe instances. This method should
	 * not be invoked directly. CENOs which are acquired from a given pool will automatically
	 * release themselves when necessary.
	 * 
	 * @param adapter
	 */
	public void releaseNode(CacheEventNode adapter) {
		freeNode(adapter);
	}

	/**
	 * freezePool() should be invoked to free any unused resources. After freezePool has been
	 * invoked, warmPool() will need to be invoked before the pool can be used again.
	 *  
	 */
	public void freezePool() {
		availablePool.clear();
		availablePool = null;
	}

	/**
	 * warmPool() must be invoked to notify the pool it is about to be used. This should occur only
	 * once per document rendering. Until the pool is in use, it contains no available
	 * CacheEventNodes (CENOs) in order to limit the size of the in-memory footprint of the
	 * EMF2SAXWriter.
	 *  
	 */
	public void warmPool() {
		ensureMinimumCapacity();
	}

	private CacheEventNode fetchFreeNode() {
		CacheEventNode result = null;

		if (availablePool == null || availablePool.isEmpty())
			warmPool();

		result = availablePool.pop();
		inusePool.add(result);

		return result;
	}

	private void freeNode(CacheEventNode adapter) {
		if (inusePool.remove(adapter))
			availablePool.push(adapter);
		//else
		//	throw new IllegalStateException("Adapter not contained in pool!");
		if (availablePool.size() > (5 * poolCapacity)) {
			availablePool.clear();
			ensureMinimumCapacity();
		}
	}

	private void ensureMinimumCapacity() {
		if (availablePool == null) {
			availablePool = new CacheEventStack();
		}
		if (availablePool.size() < poolCapacity) {
			final int minimumCapacity = poolCapacity - availablePool.size();
			for (int i = 0; i < minimumCapacity; i++)
				availablePool.push(new CacheEventNode(this));
		}
	}

}