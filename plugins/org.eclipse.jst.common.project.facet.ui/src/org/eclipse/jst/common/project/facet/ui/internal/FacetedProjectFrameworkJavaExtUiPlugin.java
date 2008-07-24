/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.ui.internal;

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
    
    public static ImageDescriptor getImageDescriptor( final String path )
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin( PLUGIN_ID, path );
    }

}
