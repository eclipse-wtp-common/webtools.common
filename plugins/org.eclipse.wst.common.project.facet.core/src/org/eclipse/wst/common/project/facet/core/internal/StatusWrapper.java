/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import org.eclipse.core.runtime.IStatus;

/**
 * Helper class that can be used to wrap an existing status object and override
 * the status code.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class StatusWrapper

    implements IStatus
    
{
    private final IStatus status;
    private final int code;
    
    public StatusWrapper( final IStatus status,
                          final int code )
    {
        this.status = status;
        this.code = code;
    }

    public IStatus[] getChildren()
    {
        return this.status.getChildren();
    }

    public int getCode()
    {
        return this.code;
    }

    public Throwable getException()
    {
        return this.status.getException();
    }

    public String getMessage()
    {
        return this.status.getMessage();
    }

    public String getPlugin()
    {
        return this.status.getPlugin();
    }

    public int getSeverity()
    {
        return this.status.getSeverity();
    }

    public boolean isMultiStatus()
    {
        return this.status.isMultiStatus();
    }

    public boolean isOK()
    {
        return this.status.isOK();
    }

    public boolean matches( final int severityMask )
    {
        return this.status.matches( severityMask );
    }
}
