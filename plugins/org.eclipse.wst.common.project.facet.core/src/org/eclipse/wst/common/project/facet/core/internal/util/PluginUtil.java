/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
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
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
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
    
    @SuppressWarnings( "unchecked" )
    public static <T> T instantiate( final String pluginId,
                                     final String clname,
                                     final Class<T> interfc )
    
        throws CoreException
        
    {
        final Bundle bundle = Platform.getBundle( pluginId );
        
        final Object obj;
        
        try
        {
            final Class cl = bundle.loadClass( clname );
            obj = cl.newInstance();
        }
        catch( Exception e )
        {
            final String msg = NLS.bind( Resources.failedToCreate, clname );
            throw new CoreException( FacetCorePlugin.createErrorStatus( msg, e ) );
        }
        
        if( ! interfc.isAssignableFrom( obj.getClass() ) )
        {
            final String msg
                = NLS.bind( Resources.doesNotImplement, clname, interfc.getClass().getName() );
            
            throw new CoreException( FacetCorePlugin.createErrorStatus( msg ) );
        }
        
        return (T) obj;
    }
    
    private static final class Resources

        extends NLS
    
    {
        public static String missingAttribute;
        public static String missingElement;
        public static String failedToCreate;
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
