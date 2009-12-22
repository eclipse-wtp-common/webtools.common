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

package org.eclipse.wst.common.project.facet.core.util.internal;

import org.eclipse.core.runtime.IStatus;

/**
 * Helper class that can be used to wrap an existing status object and override
 * the status code.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StatusWrapper

    implements IStatus
    
{
    private final IStatus base;
    private Integer code = null;
    private String message = null;
    
    public StatusWrapper( final IStatus status )
    {
        this.base = status;
    }
    
    public IStatus[] getChildren()
    {
        return this.base.getChildren();
    }

    public int getCode()
    {
        if( this.code != null )
        {
            return this.code;
        }
        else
        {
            return this.base.getCode();
        }
    }
    
    public void setCode( final int code )
    {
        this.code = code;
    }

    public Throwable getException()
    {
        return this.base.getException();
    }

    public String getMessage()
    {
        if( this.message != null )
        {
            return this.message;
        }
        else
        {
            return this.base.getMessage();
        }
    }
    
    public void setMessage( final String message )
    {
        this.message = message;
    }

    public String getPlugin()
    {
        return this.base.getPlugin();
    }

    public int getSeverity()
    {
        return this.base.getSeverity();
    }

    public boolean isMultiStatus()
    {
        return this.base.isMultiStatus();
    }

    public boolean isOK()
    {
        return this.base.isOK();
    }

    public boolean matches( final int severityMask )
    {
        return this.base.matches( severityMask );
    }
}
