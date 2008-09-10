package org.eclipse.wst.common.internal.emfworkbench;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;

/**
 * This class will be used to cache the recently used edit models.  Loading and unloading of edit models can be
 * costly, especially if operations and/or post operations have a need to reload the same edit model repeatedly.
 * This will allow those repeatedly used edit models to be cached, thereby improving performance.  By design, the
 * edit models load resources and these resources are stored in memory, so we don't want to cache every edit
 * model accessed, so this is a least used cache mechanism, where the max size is 10.  If an edit model is used
 * it is put to the back of the stack, and new edit models accessed are put to the back of the stack, so that the
 * top of the stack is the first edit model disposed when the cache is higher than the threshhold.
 *
 */
public class EditModelLeastUsedCache {
	
	/**
	 * Provide a singleton instance.
	 */
	private static EditModelLeastUsedCache INSTANCE = new EditModelLeastUsedCache();	
	
	/**
	 * The threshold, or most edit models we will keep open at a time is 10.  This is low enough to not
	 * overwhelm workbench memory and high enough to aid in operations which continually reload 3-4 
	 * edit models.
	 */
	private final static int threshhold = 10;
	
	/**
	 *  A LHS is required to ensure the order of the items is maintained. Other
	 * Set implementations (HashSet, TreeSet) do not preserve the order. This 
	 * is critical to the implementation. DO NOT CHANGE THIS. 
	 */
	private LinkedHashSet lru = new LinkedHashSet(threshhold);
	
	/**
	 * Accessor for the EditModelLeastUsedCache INSTANCE
	 * 
	 * @return the EditModelLeastUsedCache INSTANCE
	 */
	public static EditModelLeastUsedCache getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Remove the all elements from the lru that are contained
	 * in <code>aCollection</code>.  This method assumes the
	 * EditModels in the aCollection will be discarded and it
	 * will not attempt to decrememt its reference count.
	 * @param aCollection - A {@link Collection} of {@link EditModel}.
	 */
	public void removeAllCached(Collection aCollection) {
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
	public void access(EditModel editModel) {
		boolean shouldAccess = true;
		synchronized (lru) {
			if (lru.contains(editModel)) {
				moveToEnd(editModel);
				shouldAccess = false;
			}
		}
		if (shouldAccess) {
			editModel.access(this);
			synchronized (lru) {
				lru.add(editModel);
			}
		}
	}
	
	/**
	 * If we hit the capacity of the lru then remove the first one
	 * and release access.
	 */
	public void optimizeLRUSizeIfNecessary() {
		EditModel model = null;
		
		synchronized (lru) {
			if (lru.size() > threshhold) {
				// remove oldest element and release the edit model.
				Iterator iterator = lru.iterator();
				model = (EditModel) iterator.next();
				if (model != null) {
					lru.remove(model);	
				}
			}
		}
		if (model != null)
			model.releaseAccess(this);
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
