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

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.TreeMap;

import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetPreferences

    implements Preferences
    
{
    private static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$
    
    private final ProjectFacetPreferencesGroup group;
    private final String facetId;
    private final IProjectFacet facet;
    private final IFacetedProject project;
    private final ProjectFacetPreferences parent;
    private final String name;
    private final String absolutePath;
    private final TreeMap<String,ProjectFacetPreferences> children;
    private final TreeMap<String,String> attributes;
    
    public ProjectFacetPreferences( final ProjectFacetPreferencesGroup group,
                                    final String facetId,
                                    final IFacetedProject project )
    {
        this( group, facetId, toFacet( facetId ), project, null, PATH_SEPARATOR );
    }
    
    public ProjectFacetPreferences( final ProjectFacetPreferencesGroup group,
                                    final String facetId,
                                    final IProjectFacet facet,
                                    final IFacetedProject project,
                                    final ProjectFacetPreferences parent,
                                    final String name )
    {
        this.group = group;
        this.facetId = facetId;
        this.facet = facet;
        this.project = project;
        this.parent = parent;
        this.name = name;
        
        this.absolutePath
            = this.parent == null ? name : this.parent.absolutePath() + PATH_SEPARATOR + name;
        
        this.children = new TreeMap<String,ProjectFacetPreferences>();
        this.attributes = new TreeMap<String,String>();
    }
    
    public String getProjectFacetId()
    {
        return this.facetId;
    }
    
    public IProjectFacet getProjectFacet()
    {
        return this.facet;
    }
    
    public IFacetedProject getFacetedProject()
    {
        return this.project;
    }
    
    public String name()
    {
        return this.name;
    }

    public String absolutePath()
    {
        return this.absolutePath;
    }

    public Preferences parent()
    {
        return this.parent();
    }

    public String[] childrenNames()
    {
        synchronized( this.group )
        {
            return this.children.keySet().toArray( new String[ this.children.size() ] );
        }
    }

    public Preferences node( final String path )
    {
        synchronized( this.group )
        {
            return node( split( path ), 0 );
        }
    }
    
    private Preferences node( final String[] path,
                              final int position )
    {
        final String segment = path[ position ];
        ProjectFacetPreferences child = this.children.get( segment );
        
        if( child == null )
        {
            child = new ProjectFacetPreferences( this.group, this.facetId, this.facet, this.project, this, segment );
            this.children.put( segment, child );
        }
        
        if( position == path.length - 1 )
        {
            return child;
        }
        else
        {
            return child.node( path, position + 1 );
        }
    }

    public boolean nodeExists( final String path )
    {
        synchronized( this.group )
        {
            return nodeExists( split( path ), 0 );
        }
    }

    private boolean nodeExists( final String[] path,
                                final int position )
    {
        final String segment = path[ position ];
        ProjectFacetPreferences child = this.children.get( segment );
        
        if( child == null )
        {
            return false;
        }
        
        if( position == path.length - 1 )
        {
            return true;
        }
        else
        {
            return child.nodeExists( path, position + 1 );
        }
    }

    public void removeNode()
    
        throws BackingStoreException
        
    {
        if( this.parent == null )
        {
            this.group.removePreferences( this.facet );
        }
        else
        {
            synchronized( this.group )
            {
                this.parent.children.remove( this.name );
            }
        }
    }

    public String[] keys()
    {
        synchronized( this.group )
        {
            return this.attributes.keySet().toArray( new String[ this.attributes.size() ] );
        }
    }

    public String get( final String key,
                       final String def )
    {
        synchronized( this.group )
        {
            String value = this.attributes.get( key );
            
            if( value == null )
            {
                value = def;
            }
            
            return value;
        }
    }

    public boolean getBoolean( final String key,
                               final boolean def )
    {
        synchronized( this.group )
        {
            String value = this.attributes.get( key );
            
            if( value == null )
            {
                return def;
            }
            
            return Boolean.parseBoolean( value );
        }
    }

    public byte[] getByteArray( final String key,
                                final byte[] def )
    {
        throw new UnsupportedOperationException();
    }

    public double getDouble( final String key,
                             final double def )
    {
        synchronized( this.group )
        {
            final String value = this.attributes.get( key );
            
            if( value == null )
            {
                return def;
            }
            
            try
            {
                return Double.parseDouble( value );
            }
            catch( NumberFormatException e )
            {
                return def;
            }
        }
    }

    public float getFloat( final String key,
                           final float def )
    {
        synchronized( this.group )
        {
            final String value = this.attributes.get( key );
            
            if( value == null )
            {
                return def;
            }
            
            try
            {
                return Float.parseFloat( value );
            }
            catch( NumberFormatException e )
            {
                return def;
            }
        }
    }

    public int getInt( final String key,
                       final int def )
    {
        synchronized( this.group )
        {
            final String value = this.attributes.get( key );
            
            if( value == null )
            {
                return def;
            }
            
            try
            {
                return Integer.parseInt( value );
            }
            catch( NumberFormatException e )
            {
                return def;
            }
        }
    }

    public long getLong( final String key,
                         final long def )
    {
        synchronized( this.group )
        {
            final String value = this.attributes.get( key );
            
            if( value == null )
            {
                return def;
            }
            
            try
            {
                return Long.parseLong( value );
            }
            catch( NumberFormatException e )
            {
                return def;
            }
        }
    }

    public void put( final String key,
                     final String value )
    {
        synchronized( this.group )
        {
            if( value == null )
            {
                this.attributes.remove( key );
            }
            else
            {
                this.attributes.put( key, value );
            }
        }
    }

    public void putBoolean( final String key,
                            final boolean value )
    {
        synchronized( this.group )
        {
            this.attributes.put( key, String.valueOf( value ) );
        }
    }

    public void putByteArray( final String key,
                              final byte[] value )
    {
        throw new UnsupportedOperationException();
    }

    public void putDouble( final String key,
                           final double value )
    {
        synchronized( this.group )
        {
            this.attributes.put( key, String.valueOf( value ) );
        }
    }

    public void putFloat( final String key,
                          final float value )
    {
        synchronized( this.group )
        {
            this.attributes.put( key, String.valueOf( value ) );
        }
    }

    public void putInt( final String key,
                        final int value )
    {
        synchronized( this.group )
        {
            this.attributes.put( key, String.valueOf( value ) );
        }
    }

    public void putLong( final String key,
                         final long value )
    {
        synchronized( this.group )
        {
            this.attributes.put( key, String.valueOf( value ) );
        }
    }

    public void remove( final String key )
    {
        synchronized( this.group )
        {
            this.attributes.remove( key );
        }
    }

    public void clear()
    {
        synchronized( this.group )
        {
            this.attributes.clear();
        }
    }

    public void flush()

        throws BackingStoreException
    
    {
        this.group.save();
    }

    public void sync()
    
        throws BackingStoreException
        
    {
        flush();
    }
    
    private static String[] split( final String path )
    {
        return path.split( PATH_SEPARATOR );
    }
    
    private static IProjectFacet toFacet( final String facetId )
    {
        if( ProjectFacetsManager.isProjectFacetDefined( facetId ) )
        {
            return ProjectFacetsManager.getProjectFacet( facetId );
        }
        else
        {
            return null;
        }
    }
    
}
