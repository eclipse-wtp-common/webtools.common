/*
 * Created on Oct 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.internal.emf;

import org.eclipse.wst.common.internal.emf.utilities.ResourceIsLoadingAdapter;
import org.eclipse.wst.common.internal.emf.utilities.ResourceIsLoadingAdapterFactory;


/**
 * @author mdelder
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ResourceSynchronizedIsLoadingAdapterFactory extends ResourceIsLoadingAdapterFactory {

    
    /* (non-Javadoc)
     * @see com.ibm.wtp.internal.emf.utilities.ResourceIsLoadingAdapterFactory#createResourceIsLoadingAdapter()
     */
    public ResourceIsLoadingAdapter createResourceIsLoadingAdapter() { 
        return new ResourceSynchronizedIsLoadingAdapter();
    }
}
