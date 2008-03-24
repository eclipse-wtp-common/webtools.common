/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.runtime.events.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.events.IRuntimeLifecycleEvent;
import org.eclipse.wst.common.project.facet.core.runtime.events.IValidationStatusChangedEvent;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ValidationStatusChangedEvent

    implements IValidationStatusChangedEvent
    
{
    private final IRuntime runtime;
    private final IStatus oldValidationStatus;
    private final IStatus newValidationStatus;
    
    public ValidationStatusChangedEvent( final IRuntime runtime,
                                         final IStatus oldValidationStatus,
                                         final IStatus newValidationStatus )
    {
        this.runtime = runtime;
        this.oldValidationStatus = oldValidationStatus;
        this.newValidationStatus = newValidationStatus;
    }

    public Type getType()
    {
        return IRuntimeLifecycleEvent.Type.VALIDATION_STATUS_CHANGED;
    }

    public IRuntime getRuntime()
    {
        return this.runtime;
    }

    public IStatus getOldValidationStatus()
    {
        return this.oldValidationStatus;
    }

    public IStatus getNewValidationStatus()
    {
        return this.newValidationStatus;
    }

}
