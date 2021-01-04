/*******************************************************************************
 * Copyright (c) 2005, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.internal;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

public final class FacetCorePlugin

    extends Plugin

{
    public static final String PLUGIN_ID = "org.eclipse.jst.common.project.facet.core"; //$NON-NLS-1$
    public static final String OUTPUT_FOLDER = "outputFolder"; //$NON-NLS-1$
    public static final String PROD_PROP_SOURCE_FOLDER_LEGACY = "defaultSource"; //$NON-NLS-1$
    private static final String PROD_PROP_SOURCE_FOLDER = "defaultJavaSourceFolder"; //$NON-NLS-1$
    public static final String DEFAULT_SOURCE_FOLDER = "src/main/java"; //$NON-NLS-1$
    public static final String DEFUALT_OUTPUT_FOLDER ="build/classes"; //$NON-NLS-1$

    private static FacetCorePlugin inst;

    /**
     * Get the plugin singleton.
     */

    public static FacetCorePlugin getDefault()
    {
        return inst;
    }
    
    public FacetCorePlugin() {
    	super();
		if (inst == null)
			inst = this;
	}
    public String getPluginID() 
    {
        return PLUGIN_ID;
    }
    
    public static void log( final Exception e )
    {
        final ILog log = FacetCorePlugin.getDefault().getLog();
        final String msg = "Encountered an unexpected exception."; //$NON-NLS-1$
        
        log.log( new Status( IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, e ) );
    }
    
    public static IStatus createErrorStatus( final String msg )
    {
        return createErrorStatus( msg, null );
    }

    public static IStatus createErrorStatus( final String msg,
                                             final Exception e )
    {
        return new Status( IStatus.ERROR, FacetCorePlugin.PLUGIN_ID, 0, msg, e );
    }
    
    
    
    public static String getJavaSrcFolder(){
    	String srcFolder = FacetCorePlugin.getDefault().getPluginPreferences().getString(PROD_PROP_SOURCE_FOLDER_LEGACY);
    	if( srcFolder == null || srcFolder.equals("") ){ //$NON-NLS-1$
    		if( Platform.getProduct() != null ){
    			srcFolder = Platform.getProduct().getProperty( PROD_PROP_SOURCE_FOLDER );
    		    if( srcFolder == null || srcFolder.equals("")){ //$NON-NLS-1$
    		    	srcFolder = Platform.getProduct().getProperty( PROD_PROP_SOURCE_FOLDER_LEGACY );
    		    }      			
    		}
	    	if( srcFolder == null || srcFolder.equals("") ){ //$NON-NLS-1$
	    		srcFolder = DEFAULT_SOURCE_FOLDER;
	    	}

    	}
	    return srcFolder;
    }
}
