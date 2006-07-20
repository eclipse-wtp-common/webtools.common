/*
 * Created on Oct 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.internal.emf.utilities;


/**
 * @author mdelder
 * 
 */
public class ResourceIsLoadingAdapterFactory {

    public static ResourceIsLoadingAdapterFactory INSTANCE = new ResourceIsLoadingAdapterFactory();
         
    public ResourceIsLoadingAdapter createResourceIsLoadingAdapter() {
        return new ResourceIsLoadingAdapter();
    }
    
}
