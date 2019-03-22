/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.util.internal;

import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IVersion;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class UnknownVersion<T extends IVersion>

    implements IVersion
    
{
    private final Versionable<T> versionable;
    private final String versionString;
    
    public UnknownVersion( final Versionable<T> versionable,
                           final String versionString )
    {
        this.versionable = versionable;
        this.versionString = versionString;
    }
    
    public String getVersionString()
    {
        return this.versionString;
    }

    public int compareTo( final Object obj )
    {
        try
        {
            final Comparator<String> comp = this.versionable.getVersionComparator();
            return comp.compare( this.versionString, ( (IVersion) obj  ).getVersionString() );
        }
        catch( CoreException e )
        {
            throw new RuntimeException( e );
        }
    }

}
