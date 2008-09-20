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

package org.eclipse.jst.common.project.facet.core.libprov.internal;

import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.createErrorStatus;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.common.project.facet.core.libprov.LibrariesProviderOperationConfig;
import org.eclipse.osgi.util.NLS;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class UnknownProviderUninstallOperationConfig

    extends LibrariesProviderOperationConfig
    
{
    public static final String UNKNOWN_PROVIDER_ID = "unknown-libraries-provider"; //$NON-NLS-1$
    
    private static final String CLASS_NAME = UnknownProviderUninstallOperationConfig.class.getName();
    
    public static final String PROP_CONTINUE_ANYWAY 
        = CLASS_NAME + ".PROP_CONTINUE_ANYWAY"; //$NON-NLS-1$

    private boolean continueAnyway;
    
    public UnknownProviderUninstallOperationConfig()
    {
        this.continueAnyway = false;
    }
    
    @Override
    public synchronized IStatus validate()
    {
        if( this.continueAnyway )
        {
            return Status.OK_STATUS;
        }
        else
        {
            return createErrorStatus( Resources.unknownProviderMsg );
        }
    }
    
    public synchronized boolean getContinueAnyway()
    {
        return this.continueAnyway;
    }
    
    public synchronized void setContinueAnyway( final boolean continueAnyway )
    {
        this.continueAnyway = continueAnyway;
        
        notifyListeners( PROP_CONTINUE_ANYWAY, Boolean.valueOf( ! continueAnyway ), 
                         Boolean.valueOf( continueAnyway ) );
    }

    public synchronized Object getProperty( final String property )
    {
        if( property.equals( PROP_CONTINUE_ANYWAY ) )
        {
            return Boolean.valueOf( this.continueAnyway );
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    public void setProperty( final String property,
                             final Object value )
    {
        if( property.equals( PROP_CONTINUE_ANYWAY ) )
        {
            setContinueAnyway( ( (Boolean) value ).booleanValue() );
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    private static final class Resources
        
        extends NLS

    {
        public static String unknownProviderMsg;
        
        static
        {
            initializeMessages( UnknownProviderUninstallOperationConfig.class.getName(), Resources.class );
        }
    }
    
}
