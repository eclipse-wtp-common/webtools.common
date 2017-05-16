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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.DefaultVersionComparator;
import org.eclipse.wst.common.project.facet.core.IVersion;
import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class Versionable<T extends IVersion>
{
    private static final Comparator<String> DEFAULT_VERSION_COMPARATOR
        = new DefaultVersionComparator();

    protected final IndexedSet<String,T> versions;
    private String versionComparatorClass;
    private Comparator<String> versionComparator;
    
    public Versionable()
    {
        this.versions = new IndexedSet<String,T>();
    }
    
    public abstract String getPluginId();
    
    public Set<T> getVersions()
    {
        return this.versions.getItemSet();
    }
    
    public Set<T> getVersions( final String expr )
    
        throws CoreException
        
    {
        final VersionExpr<T> prepared = new VersionExpr<T>( this, expr, null );
        final Set<T> result = new HashSet<T>();
         
        for( T ver : this.versions.getItemSet() )
        {
            if( prepared.check( ver ) )
            {
                result.add( ver );
            }
        }
        
        return result;
    }
    
    public T getVersion( final String version )
    {
        final T ver = this.versions.getItemByKey( version );
        
        if( ver == null )
        {
            throw new IllegalArgumentException( createVersionNotFoundErrMsg( version ) );
        }
        
        return ver;
    }
    
    @SuppressWarnings( "unchecked" )
    
    public T getLatestVersion()
    {
        if( this.versions.getItemSet().size() > 0 )
        {
            return (T) Collections.max( this.versions.getItemSet() );
        }
        else
        {
            return null;
        }
    }
    
    public boolean hasVersion( final String version )
    {
        return this.versions.containsKey( version );
    }

    public List<T> getSortedVersions( final boolean ascending )
    {
        Comparator<T> comp;
        
        if( ascending )
        {
            comp = null;
        }
        else
        {
            comp = new Comparator<T>()
            {
                @SuppressWarnings( "unchecked" )
                public int compare( final T ver1,
                                    final T ver2 )
                {
                    return ver1.compareTo( ver2 ) * -1;
                }
            };
        }

        final List<T> list = new ArrayList<T>( this.versions.getItemSet() );
        Collections.sort( list, comp );
        
        return list;
    }
    
    @SuppressWarnings( "unchecked" )
    
    public Comparator<String> getVersionComparator()
    
        throws CoreException
        
    {
        Comparator<String> comp;
        
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
                    final Class<?> cl = bundle.loadClass( this.versionComparatorClass );
                    this.versionComparator = (Comparator<String>) cl.newInstance();
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
