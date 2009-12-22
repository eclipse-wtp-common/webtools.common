/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal.util;

import static org.eclipse.ui.plugin.AbstractUIPlugin.imageDescriptorFromPlugin;
import static org.eclipse.wst.common.project.facet.ui.internal.FacetUiPlugin.PLUGIN_ID;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

public final class ProblemOverlayImageDescriptor 

    extends CompositeImageDescriptor 
    
{
    private static final ImageData IMG_DATA_ERROR_OVERLAY
        = imageDescriptorFromPlugin( PLUGIN_ID, "images/overlays/error.gif" ).getImageData(); //$NON-NLS-1$

    private static final ImageData IMG_DATA_WARNING_OVERLAY
        = imageDescriptorFromPlugin( PLUGIN_ID, "images/overlays/warning.png" ).getImageData(); //$NON-NLS-1$

    private final ImageData base;
    private final ImageData overlay;
    private final Point size;
    
    public ProblemOverlayImageDescriptor( final ImageDescriptor base,
                                          final int severity ) 
    {
        this.base = base.getImageData();
        this.overlay = getOverlay( severity );
        this.size = new Point( this.base.width, this.base.height );
    }
    
    protected void drawCompositeImage( final int width, 
                                       final int height ) 
    {
        drawImage( this.base, 0, 0 );
        drawImage( this.overlay, width - this.overlay.width, height - this.overlay.height );
    }
    
    protected Point getSize()
    {
        return this.size;
    }
    
    private ImageData getOverlay( final int severity )
    {
        if( severity == IStatus.ERROR )
        {
            return IMG_DATA_ERROR_OVERLAY;
        }
        else if( severity == IStatus.WARNING )
        {
            return IMG_DATA_WARNING_OVERLAY;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }
}
