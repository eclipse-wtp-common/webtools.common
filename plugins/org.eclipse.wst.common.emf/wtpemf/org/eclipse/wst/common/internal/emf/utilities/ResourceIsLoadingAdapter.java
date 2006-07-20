/*
 * Created on Oct 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.internal.emf.utilities;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author mdelder
 */
public class ResourceIsLoadingAdapter extends AdapterImpl implements Adapter {

    private static final Class RESOURCE_IS_LOADING_ADAPTER_CLASS = ResourceIsLoadingAdapter.class;

    public ResourceIsLoadingAdapter() {
    }

    public static ResourceIsLoadingAdapter findAdapter(Resource aResource) {
        ResourceIsLoadingAdapter adapter = null;
        //System.out.println("ResourceIsLoadingAdapter Synchronizing on " + aResource);
        
        /* Synchronize on the Resource (which will be the target of 
         * the ResourceIsLoadingAdapter in the list, if it exists).
         * 
         * removeIsLoadingSupport() will coordinate with this 
         * synchronization.
         */
        synchronized (aResource) {
            adapter = (ResourceIsLoadingAdapter) EcoreUtil.getAdapter(aResource.eAdapters(), ResourceIsLoadingAdapter.class);
        }
        return adapter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged(Notification notification) {

        if (notification.getNotifier() != null) {

            // listen for the remove of the loading adapter
            if (isSetLoadedResourceNotification(notification)) removeIsLoadingSupport();
        }
    }

    /**
     * Default implementation is a no-op.
     */
    public void waitForResourceToLoad() {

    }

    /**
     * @param notification
     * @return
     */
    protected boolean isSetLoadedResourceNotification(Notification notification) {
        return notification.getFeatureID(null) == Resource.RESOURCE__IS_LOADED && notification.getEventType() == Notification.SET;
    }

    protected void removeIsLoadingSupport() {

        /* Synchronize on the target of the Adapter. If 
         * the list of adapters is searched for a 
         * ResourceIsLoadingAdapter using the 
         * ResourceIsLoadingAdapter.findAdapter() API, then
         * the access to remove the Adapter using this method 
         * will be coordinated.  
         */
        if (getTarget() != null) {
            //System.out.println("ResourceIsLoadingAdapter Synchronizing on " + getTarget());
            synchronized (getTarget()) {
                getTarget().eAdapters().remove(this);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.emf.common.notify.Adapter#isAdapterForType(java.lang.Object)
     */
    public boolean isAdapterForType(Object type) {
        return type == RESOURCE_IS_LOADING_ADAPTER_CLASS;
    }

    /**
     * 
     */
    public void forceRelease() {

    }

}
