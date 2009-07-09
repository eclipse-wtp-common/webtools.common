package org.eclipse.wst.common.tests;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;

/**
 * @author nirav
 */
public class PostBuildListener implements IResourceChangeListener {
    private boolean buildComplete = false;
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        if (event.getType() == IResourceChangeEvent.POST_BUILD){
          buildComplete = true;  
        }
    }

    public boolean isBuildComplete() {
        return buildComplete;
    }
    
    public void testComplete() {
        buildComplete = false;
    }
}
