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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.DefaultVersionComparator;
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
    
    public Set getVersions( final String expr )
    
        throws CoreException
        
    {
        final VersionExpr prepared = new VersionExpr( this, expr, null );
        final Set result = new HashSet();
            
        for( Iterator itr = this.versions.iterator(); itr.hasNext(); )
        {
            final IVersion ver = (IVersion) itr.next();
            
            if( prepared.check( ver ) )
            {
                result.add( ver );
            }
        }
        
        return result;
    }
    
    public IVersion getVersionInternal( final String version )
    {
        return (IVersion) this.versions.get( version );
    }
    
    public boolean hasVersion( final String version )
    {
        return this.versions.containsKey( version );
    }

    public List getSortedVersions( final boolean ascending )
    {
        Comparator comp;
        
        if( ascending )
        {
            comp = null;
        }
        else
        {
            comp = new Comparator()
            {
                public int compare( final Object obj1,
                                    final Object obj2 )
                {
                    return ( (Comparable) obj1 ).compareTo( obj2 ) * -1;
                }
            };
        }

        final ArrayList list = new ArrayList( this.versions );
        Collections.sort( list, comp );
        
        return list;
    }
    
    public Comparator getVersionComparator()
    
        throws CoreException
        
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
                    final String msg
                        = NLS.bind( Resources.failedToCreate, 
                                    this.versionComparatorClass );
                    
                    final IStatus st 
                        = FacetCorePlugin.createErrorStatus( msg, e );
                    
                    throw new CoreException( st );
                }
            }
            
            comp = this.versionComparator;
        }
        
        return comp;
    }
    
    public void setVersionComparator( final String clname )
    {
        this.versionComparatorClass = clname;
    }
    
    public abstract String createVersionNotFoundErrMsg( String verstr );
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String failedToCreate;
        
        static
        {
            initializeMessages( Versionable.class.getName(), 
                                Resources.class );
        }
    }

}
