package org.eclipse.wst.common.project.facet.ui.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public enum FacetedProjectFrameworkImages
{
    BANNER_IMAGE( "facets-page-wizban.png" ); //$NON-NLS-1$
    
    private static final String BASE_PATH = "images/"; //$NON-NLS-1$
    
    private final ImageDescriptor imageDescriptor;
    private Image image;
    
    private FacetedProjectFrameworkImages( final String location )
    {
        this.imageDescriptor = FacetUiPlugin.getImageDescriptor( BASE_PATH + location );
        this.image = null;
    }
    
    public ImageDescriptor getImageDescriptor()
    {
        return this.imageDescriptor;
    }
    
    public synchronized Image getImage()
    {
        if( this.image == null || this.image.isDisposed() )
        {
            this.image = this.imageDescriptor.createImage();
        }
        
        return this.image;
    }
    
}
