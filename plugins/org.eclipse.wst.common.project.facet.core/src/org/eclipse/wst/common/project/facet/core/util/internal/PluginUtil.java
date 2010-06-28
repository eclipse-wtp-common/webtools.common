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

import static org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin.createErrorStatus;
import static org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;
import org.osgi.framework.Bundle;

/**
 * Utility methods that are helpful for implementing extension points.
 *
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PluginUtil
{
    public static final class InvalidExtensionException

        extends Exception
    
    {
        private static final long serialVersionUID = 1L;
    }
    
	private PluginUtil() {}
    
    public static Collection<IExtension> findExtensions( final String pluginId,
                                                         final String extensionPointId )
    {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        final IExtensionPoint point = registry.getExtensionPoint( pluginId, extensionPointId );
        
        if( point == null )
        {
            throw new RuntimeException();
        }
        
        final List<IExtension> extensions = new ArrayList<IExtension>();
        
        for( IExtension extension : point.getExtensions() )
        {
            extensions.add( extension );
        }
        
        return extensions;
    }
    
    public static Collection<IConfigurationElement> getTopLevelElements( final Collection<IExtension> extensions )
    {
        final List<IConfigurationElement> elements = new ArrayList<IConfigurationElement>();
        
        for( IExtension extension : extensions )
        {
            for( IConfigurationElement element : extension.getConfigurationElements() )
            {
                elements.add( element );
            }
        }
        
        return elements;
    }

    public static void reportMissingAttribute( final IConfigurationElement el,
                                               final String attribute )
    {
        final String pluginId = el.getContributor().getName();
        final String extPointId = el.getDeclaringExtension().getExtensionPointUniqueIdentifier();
        
        final String msg
            = Resources.bind( Resources.missingAttribute, pluginId, extPointId, el.getName(), 
                              attribute );

        FacetCorePlugin.log( msg );
    }

    public static void reportMissingElement( final IConfigurationElement el,
                                             final String element )
    {
        final String pluginId = el.getContributor().getName();
        final String extPointId = el.getDeclaringExtension().getExtensionPointUniqueIdentifier();
        
        final String msg
            = Resources.bind( Resources.missingElement, pluginId, extPointId, el.getName(), 
                              element );

        FacetCorePlugin.log( msg );
    }

    public static String findRequiredAttribute( final IConfigurationElement el,
                                                final String attribute )

        throws InvalidExtensionException

    {
        final String val = el.getAttribute( attribute );

        if( val == null )
        {
            reportMissingAttribute( el, attribute );
            throw new InvalidExtensionException();
        }

        return val;
    }

    public static IConfigurationElement findRequiredElement( final IConfigurationElement el,
                                                             final String childElement )

        throws InvalidExtensionException

    {
        final IConfigurationElement[] children = el.getChildren( childElement );

        if( children.length == 0 )
        {
            reportMissingElement( el, childElement );
            throw new InvalidExtensionException();
        }

        return children[ 0 ];
    }

    public static IConfigurationElement findOptionalElement( final IConfigurationElement el,
                                                             final String childElement )
    {
        final IConfigurationElement[] children = el.getChildren( childElement );

        if( children.length == 0 )
        {
            return null;
        }
        else
        {
            return children[ 0 ];
        }
    }
    
    public static String getElementValue( final IConfigurationElement el,
                                          final String defaultValue )
    {
        if( el != null )
        {
            String text = el.getValue();
            
            if( text != null )
            {
                text = text.trim();
                
                if( text.length() > 0 )
                {
                    return text;
                }
            }
        }
        
        return defaultValue;
    }
    
    public static <T> Class<T> loadClass( final String pluginId,
                                          final String clname )
    {
        return loadClass( pluginId, clname, null );
    }

    @SuppressWarnings( "unchecked" )
    
    public static <T> Class<T> loadClass( final String pluginId,
                                          final String clname,
                                          final Class<T> interfc )
    {
        final Bundle bundle = Platform.getBundle( pluginId );
        final Class<?> cl;

        try
        {
            cl = bundle.loadClass( clname );
        }
        catch( Exception e )
        {
            final String msg
                = Resources.bind( Resources.failedToLoadClass, clname, pluginId );

            log( createErrorStatus( msg, e ) );

            return null;
        }

        if( interfc != null && ! interfc.isAssignableFrom( cl ) )
        {
            final String msg
                = Resources.bind( Resources.doesNotImplement, clname, interfc.getName() );

            log( createErrorStatus( msg ) );

            return null;
        }

        return (Class<T>) cl;
    }

    public static <T> T instantiate( final String pluginId,
                                     final Class<T> cl )
    {
        try
        {
            return cl.newInstance();
        }
        catch( Exception e )
        {
            final String msg
                = NLS.bind( Resources.failedToInstantiate, cl.getName(), pluginId );

            log( createErrorStatus( msg, e ) );

            return null;
        }
    }

    public static <T> T instantiate( final String pluginId,
                                     final String clname )
    {
        return instantiate( pluginId, clname, (Class<T>) null );
    }

    public static <T> T instantiate( final String pluginId,
                                     final String clname,
                                     final Class<T> interfc )
    {
        final Class<T> cl = loadClass( pluginId, clname, interfc );

        if( cl == null )
        {
            return null;
        }

        return instantiate( pluginId, cl );
    }
    
    private static final class Resources

        extends NLS
    
    {
        public static String missingAttribute;
        public static String missingElement;
        public static String failedToLoadClass;
        public static String failedToInstantiate;
        public static String doesNotImplement;
    
        static
        {
            initializeMessages( PluginUtil.class.getName(), Resources.class );
        }
        
        public static String bind( final String message,
                                   final String arg1,
                                   final String arg2,
                                   final String arg3,
                                   final String arg4 )
        {
            return bind( message, new Object[] { arg1, arg2, arg3, arg4 } );
        }
    }
    
}
