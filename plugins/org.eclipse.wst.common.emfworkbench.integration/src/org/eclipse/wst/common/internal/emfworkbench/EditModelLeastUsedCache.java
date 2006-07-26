package org.eclipse.wst.common.internal.emfworkbench;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;

public class EditModelLeastUsedCache {
	private final DynamicGrowthModel dynamicGrowthModel = new DynamicGrowthModel();

	/**
	 * Provide a singleton.
	 */
	static EditModelLeastUsedCache INSTANCE = new EditModelLeastUsedCache();	
	
	private int threshhold = dynamicGrowthModel.getOptimalSize();
	
	/* A LHS is required to ensure the order of the items is maintained. Other
	 * Set implementations (HashSet, TreeSet) do not preserve the order. This 
	 * is critical to the implementation. DO NOT CHANGE THIS. 
	 */
	private LinkedHashSet lru = new LinkedHashSet(threshhold);
	
	/**
	 * Remove the all elements from the lru that are contained
	 * in <code>aCollection</code>.  This method assumes the
	 * EditModels in the aCollection will be discarded and it
	 * will not attempt to decrememt its reference count.
	 * @param aCollection - A {@link Collection} of {@link EditModel}.
	 */
	void removeAllCached(Collection aCollection) {
		if (aCollection != null) { 
			lru.removeAll(aCollection);
		}
	}

	/**
	 * An {@link EditModel} is being accessed so we will want
	 * to update the lru and access the editModel which will hold
	 * a reference count.
	 * @param editModel - The {@link EditModel} that we want to place
	 * 	in the least used cache.
	 */
	void access(EditModel editModel) {
		if (lru.contains(editModel)) {
			moveToEnd(editModel);
		} else {
			editModel.access(this);
			lru.add(editModel);
		}
		IPath key = editModel.getProject().getFullPath().append(editModel.getEditModelID());
		if (dynamicGrowthModel.injectKey(key)){
			optimizeLRUSize() ;
			
		}
	}
	
	

	/*
	 * If we hit the capacity of the lru then remove the first one
	 * and release access.
	 */
	private void optimizeLRUSize() {
		if(threshhold > dynamicGrowthModel.getOptimalSize()) {
			// shrink
			if(lru.size() >= threshhold) {
				// remove elements and release them.
				int iterations = lru.size() - threshhold + 1;
				int i = 0;
				Iterator iterator = lru.iterator();
				if (iterator.hasNext() && (i < iterations)) {
					
					EditModel model = (EditModel) iterator.next();
					if (model != null) {
						lru.remove(model);
						model.releaseAccess(this);
					}
					i++;
				}
				
			}
			/* else ok */
		}
		threshhold = dynamicGrowthModel.getOptimalSize();
	}

	
	/**
	 * Move the editModel to the end of the list 
	 * @param editModel -- EditModel to be moved
	 */
	private void moveToEnd(EditModel editModel) {
		lru.remove(editModel);
		lru.add(editModel);
	}	
}
