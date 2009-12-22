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

package org.eclipse.jst.common.project.facet.ui.internal;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectFrameworkJavaExtUiPlugin 
{
    public static final String PLUGIN_ID = "org.eclipse.jst.common.project.facet.ui"; //$NON-NLS-1$
    
    public static final String IMG_PATH_JAVA_WIZBAN = "images/java-wizban.png"; //$NON-NLS-1$
    public static final String IMG_PATH_SOURCE_FOLDER = "images/source-folder.gif"; //$NON-NLS-1$
    public static final String IMG_PATH_WIZBAN_DOWNLOAD_LIBRARY = "images/wizban/download-library.png"; //$NON-NLS-1$
    public static final String IMG_PATH_BUTTON_DOWNLOAD = "images/buttons/download.png"; //$NON-NLS-1$
    public static final String IMG_PATH_BUTTON_MANAGE_LIBRARIES = "images/buttons/manage-libraries.gif"; //$NON-NLS-1$
    public static final String IMG_PATH_OBJECTS_LIBRARY = "images/objects/library.gif"; //$NON-NLS-1$
    
    private static final ILog platformLog
        = Platform.getLog( Platform.getBundle( PLUGIN_ID ) );

    public static ImageDescriptor getImageDescriptor( final String path )
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin( PLUGIN_ID, path );
    }

    public static void log( final Exception e )
    {
        final String message = e.getMessage() + ""; //$NON-NLS-1$
        log( createErrorStatus( message, e ) );
    }

    public static void log( final IStatus status )
    {
        platformLog.log( status );
    }
    
    public static IStatus createErrorStatus( final String message )
    {
        return createErrorStatus( message, null );
    }

    public static IStatus createErrorStatus( final String message,
                                             final Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, -1, message, e )  ;      
    }
    
}
