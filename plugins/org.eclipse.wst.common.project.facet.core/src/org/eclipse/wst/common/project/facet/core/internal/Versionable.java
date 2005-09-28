/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.DefaultVersionComparator;
import org.eclipse.wst.common.project.facet.core.VersionFormatException;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public abstract class Versionable
{
    private static final Comparator DEFAULT_VERSION_COMPARATOR
        = new DefaultVersionComparator();

    protected final IndexedSet versions = new IndexedSet();
    private String versionComparatorClass;
    private Comparator versionComparator;
    
    public abstract String getPluginId();
    
    public Set getVersions()
    {
        return this.versions.getUnmodifiable();
    }
    
    public boolean hasVersion( final String version )
    {
        return this.versions.containsKey( version );
    }

    public List getSortedVersions( final boolean ascending )
    
        throws VersionFormatException
        
    {
        final ArrayList list = new ArrayList( this.versions );
        final IVersionAdapter adapter = getVersionAdapter();
              
        Collections.sort( list, getVersionComparator( ascending, adapter ) );
        
        return list;
    }
    
    public Comparator getVersionComparator()
    {
        return getVersionComparator( true, null );
    }
    
    protected Comparator getVersionComparator( final boolean ascending,
                                               final IVersionAdapter adapter )
    {
        Comparator comp;
        
        if( this.versionComparatorClass == null )
        {
            comp = DEFAULT_VERSION_COMPARATOR;
        }
        else
        {
            if( this.versionComparator == null )
            {
                final Bundle bundle = Platform.getBundle( getPluginId() );
                
                try
                {
                    final Class cl 
                        = bundle.loadClass( this.versionComparatorClass );
                    
                    this.versionComparator = (Comparator) cl.newInstance();
                }
                catch( Exception e )
                {
                    // TODO: handle this.
                    return null;
                }
            }
            
            comp = this.versionComparator;
        }
        
        if( adapter != null )
        {
            final Comparator base = comp;
            
            comp = new Comparator()
            {
                public int compare( final Object obj1, 
                                    final Object obj2 ) 
                {
                    final String ver1 = adapter.adapt( obj1 );
                    final String ver2 = adapter.adapt( obj2 );
                    return base.compare( ver1, ver2 );
                }
            };
        }
        
        if( ! ascending )
        {
            final Comparator base = comp;
            
            comp = new Comparator()
            {
                public int compare( final Object obj1,
                                    final Object obj2 )
                {
                    return base.compare( obj1, obj2 ) * -1;
                }
            };
        }
        
        return comp;
    }
    
    public void setVersionComparator( final String clname )
    {
        this.versionComparatorClass = clname;
    }
    
    protected abstract IVersionAdapter getVersionAdapter();
    
    protected static interface IVersionAdapter
    {
        String adapt( Object obj );
    }

}
