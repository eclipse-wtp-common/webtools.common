/*
 * Created on Oct 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.tests;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;

/**
 * @author nirav
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
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
