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

package org.eclipse.jst.common.project.facet.core.libprov.osgi;

import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * @since 1.4
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class BundleReference
{
    private String id;
    private VersionRange versionRange;
    
    public BundleReference( final String id,
                            final VersionRange versionRange )
    {
        this.id = id;
        this.versionRange = versionRange;
    }
    
    public String getBundleId()
    {
        return this.id;
    }
    
    public VersionRange getVersionRange()
    {
        return this.versionRange;
    }
    
    public boolean isResolvable()
    {
        return ( getBundles() != null );
    }
    
    public Bundle getBundle()
    {
        final Bundle[] bundles = getBundles();
        
        if( bundles == null )
        {
            return null;
        }
        
        final SortedMap<Version,Bundle> bundlesByVersion = new TreeMap<Version,Bundle>(); 
        
        for( Bundle bundle : bundles )
        {
            final Version version = getBundleVersion( bundle );
            
            if( version != null )
            {
                bundlesByVersion.put( version, bundle );
            }
        }
        
        if( bundlesByVersion.isEmpty() )
        {
            return null;
        }
        else
        {
            return bundlesByVersion.get( bundlesByVersion.lastKey() );
        }
    }
    
    private Bundle[] getBundles()
    {
        return Platform.getBundles( this.id, this.versionRange == null ? null : this.versionRange.toString() );
    }
    
    private static Version getBundleVersion( final Bundle bundle )
    {
        final String versionString = (String) bundle.getHeaders().get( "Bundle-Version" ); //$NON-NLS-1$
        return versionString == null ? null : new Version( versionString );
    }
    
}
