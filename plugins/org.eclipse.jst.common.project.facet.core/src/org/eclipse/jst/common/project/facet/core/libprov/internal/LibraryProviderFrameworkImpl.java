/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.libprov.internal;

import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.PLUGIN_ID;
import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.createErrorStatus;
import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.log;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findOptionalElement;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findRequiredAttribute;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findRequiredElement;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getElementValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderActionType;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderFramework;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.InvalidExtensionException;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LibraryProviderFrameworkImpl
{
    private static final String UNKNOWN_LIBRARY_PROVIDER = "unknown-library-provider"; //$NON-NLS-1$
    
    private static final String EXTENSION_POINT_ID = "libraryProviders"; //$NON-NLS-1$
    
    private static final String EL_ACTION = "action"; //$NON-NLS-1$
    private static final String EL_CONFIG = "config"; //$NON-NLS-1$
    private static final String EL_ENABLEMENT = "enablement"; //$NON-NLS-1$
    private static final String EL_LABEL = "label"; //$NON-NLS-1$
    private static final String EL_OPERATION = "operation"; //$NON-NLS-1$
    private static final String EL_PARAM = "param"; //$NON-NLS-1$
    private static final String EL_PRIORITY = "priority"; //$NON-NLS-1$
    private static final String EL_PROVIDER = "provider"; //$NON-NLS-1$
    private static final String ATTR_ABSTRACT = "abstract"; //$NON-NLS-1$
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
    private static final String ATTR_EXTENDS = "extends"; //$NON-NLS-1$
    private static final String ATTR_HIDDEN = "hidden"; //$NON-NLS-1$
    private static final String ATTR_ID = "id"; //$NON-NLS-1$
    private static final String ATTR_NAME = "name"; //$NON-NLS-1$
    private static final String ATTR_TYPE = "type"; //$NON-NLS-1$
    private static final String ATTR_VALUE = "value"; //$NON-NLS-1$
    
    private static final String PREFS_LIBPROV = "libprov"; //$NON-NLS-1$
    private static final String PREFS_PROVIDER_ID = "provider-id"; //$NON-NLS-1$
    private static final String PREFS_LAST_PROVIDER_USED = PREFS_LIBPROV + "/last-provider-used"; //$NON-NLS-1$
    
    private static LibraryProviderFrameworkImpl instance = null;
    
    private final Set<ILibraryProvider> providers;
    private final Set<ILibraryProvider> providersReadOnly;
    private final Map<String,ILibraryProvider> providersLookupTable;
    
    public LibraryProviderFrameworkImpl()
    {
        this.providers = new HashSet<ILibraryProvider>();
        this.providersReadOnly = Collections.unmodifiableSet( this.providers );
        this.providersLookupTable = new HashMap<String,ILibraryProvider>();
        
        readExtensions();
    }
    
    public static synchronized LibraryProviderFrameworkImpl get()
    {
        if( instance == null )
        {
            instance = new LibraryProviderFrameworkImpl();
        }
        
        return instance;
    }
    
    public Set<ILibraryProvider> getProviders()
    {
        return this.providersReadOnly;
    }
    
    public boolean isProviderDefined( final String id )
    {
        return this.providersLookupTable.containsKey( id );
    }
    
    public ILibraryProvider getProvider( final String id )
    {
        if( ! isProviderDefined( id ) )
        {
            final String msg = Resources.bind( Resources.libraryProviderNotDefined, id );
            throw new IllegalArgumentException( msg );
        }
        
        return this.providersLookupTable.get( id );
    }
    
    public ILibraryProvider getCurrentProvider( final IProject project,
                                                final IProjectFacet facet )
    {
        final IFacetedProject fproj;
        
        try
        {
            fproj = ProjectFacetsManager.create( project );
        }
        catch( CoreException e )
        {
            throw new RuntimeException( e );
        }
        
        if( ! fproj.hasProjectFacet( facet ) )
        {
            return null;
        }
        
        String providerId = null;
        
        try
        {
            Preferences prefs = fproj.getPreferences( facet );
            
            if( prefs.nodeExists( PREFS_LIBPROV ) )
            {
                prefs = prefs.node( PREFS_LIBPROV );
                providerId = prefs.get( PREFS_PROVIDER_ID, null );
            }
        }
        catch( BackingStoreException e )
        {
            throw new RuntimeException( e );
        }
        
        ILibraryProvider provider = null;
        
        if( providerId != null )
        {
            if( LibraryProviderFramework.isProviderDefined( providerId ) )
            {
                provider = LibraryProviderFramework.getProvider( providerId );
            }
            else
            {
                provider = getProvider( UNKNOWN_LIBRARY_PROVIDER );
            }
        }
        else
        {
            provider = LegacyLibraryProviderDetectorsExtensionPoint.detect( project, facet );
            
            if( provider == null )
            {
                provider = getProvider( UNKNOWN_LIBRARY_PROVIDER );
            }
        }
        
        return provider;
    }
    
    public void setCurrentProvider( final IProject project,
                                    final IProjectFacet facet,
                                    final ILibraryProvider provider )
    {
        final IFacetedProject fproj;
        
        try
        {
            fproj = ProjectFacetsManager.create( project );
        }
        catch( CoreException e )
        {
            throw new RuntimeException( e );
        }
        
        try
        {
            Preferences prefs = fproj.getPreferences( facet ).node( PREFS_LIBPROV );
            
            if( provider == null )
            {
                prefs.removeNode();
            }
            else
            {
                prefs.put( PREFS_PROVIDER_ID, provider.getId() );
            }
            
            prefs.flush();
        }
        catch( BackingStoreException e )
        {
            throw new RuntimeException( e );
        }
    }
    
    public ILibraryProvider getLastProviderUsed( final IProjectFacetVersion fv )
    {
        try
        {
            final IProjectFacet facet = fv.getProjectFacet();
            
            final Preferences prefs 
                = FacetedProjectFramework.getPreferences( facet ).node( PREFS_LAST_PROVIDER_USED );
            
            final String providerId = prefs.get( fv.getVersionString(), null );
            
            if( providerId != null )
            {
                if( isProviderDefined( providerId ) )
                {
                    return getProvider( providerId );
                }
            }
        }
        catch( BackingStoreException e )
        {
            log( e );
        }
        
        return null;
    }
    
    public void setLastProviderUsed( final IProjectFacetVersion fv,
                                     final ILibraryProvider provider )
    {
        try
        {
            final IProjectFacet facet = fv.getProjectFacet();
            
            final Preferences prefs 
                = FacetedProjectFramework.getPreferences( facet ).node( PREFS_LAST_PROVIDER_USED );
            
            prefs.put( fv.getVersionString(), provider.getId() );
        
            prefs.flush();
        }
        catch( BackingStoreException e )
        {
            log( e );
        }
    }
    
    public static void reportInvalidActionType( final String type,
                                                final String pluginId )
    {
        final String msg = Resources.bind( Resources.invalidActionType, type, pluginId ); 
        log( createErrorStatus( msg ) );
    }
    
    public static void reportProviderNotDefined( final String providerId,
                                                 final String pluginId )
    {
        final String msg 
            = Resources.bind( Resources.libraryProviderNotDefinedWithPlugin, providerId,
                              pluginId );
        
        log( createErrorStatus( msg ) );
    }
    
    private void readExtensions()
    {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        
        final IExtensionPoint point 
            = registry.getExtensionPoint( PLUGIN_ID, EXTENSION_POINT_ID );
        
        if( point == null )
        {
            throw new IllegalStateException();
        }
        
        final List<IConfigurationElement> cfgels = new ArrayList<IConfigurationElement>();
        
        for( IExtension extension : point.getExtensions() )
        {
            for( IConfigurationElement element : extension.getConfigurationElements() )
            {
                cfgels.add( element );
            }
        }
        
        final Map<LibraryProvider,String> providerToBaseIdMap 
            = new HashMap<LibraryProvider,String>();

        for( IConfigurationElement element : cfgels )
        {
            if( ! element.getName().equals( EL_PROVIDER ) )
            {
                continue;
            }
            
            try
            {
                final LibraryProvider provider = new LibraryProvider();
                provider.setPluginId( element.getContributor().getName() );
                
                provider.setId( findRequiredAttribute( element, ATTR_ID ) );
                
                if( this.providersLookupTable.containsKey( provider.getId() ) )
                {
                    final String msg 
                        = Resources.bind( Resources.libraryProviderIdAlreadyUsed, 
                                          provider.getId() );
                    
                    log( createErrorStatus( msg ) );
                    
                    throw new InvalidExtensionException();
                }
                
                final String baseProviderId = element.getAttribute( ATTR_EXTENDS );
                
                if( baseProviderId != null )
                {
                    providerToBaseIdMap.put( provider, baseProviderId.trim() );
                }
                
                final String abstractAttr = element.getAttribute( ATTR_ABSTRACT );
                
                if( abstractAttr != null )
                {
                    provider.setIsAbstract( Boolean.valueOf( abstractAttr ) );
                }
                
                final String hiddenAttr = element.getAttribute( ATTR_HIDDEN );
                
                if( hiddenAttr != null )
                {
                    provider.setIsHidden( Boolean.valueOf( hiddenAttr ) );
                }

                for( IConfigurationElement child : element.getChildren() )
                {
                    final String childName = child.getName();
                    
                    if( childName.equals( EL_LABEL ) )
                    {
                        provider.setLabel( child.getValue().trim() );
                    }
                    else if( childName.equals( EL_ENABLEMENT ) )
                    {
                        final Expression expr;
                        
                        try
                        {
                            expr = ExpressionConverter.getDefault().perform( child );
                        }
                        catch( CoreException e )
                        {
                            log( e );
                            throw new InvalidExtensionException();
                        }
                        
                        provider.setEnablementCondition( expr );
                    }
                    else if( childName.equals( EL_PRIORITY ) )
                    {
                        final String priorityString = child.getValue().trim();
                        final int priority;
                        
                        try
                        {
                            priority = Integer.parseInt( priorityString );
                        }
                        catch( NumberFormatException e )
                        {
                            log( e );
                            throw new InvalidExtensionException();
                        }
                        
                        provider.setPriority( priority );
                    }
                    else if( childName.equals( EL_PARAM ) )
                    {
                        final String name = findRequiredAttribute( child, ATTR_NAME );
                        
                        String value = child.getAttribute( ATTR_VALUE );
                        
                        if( value == null )
                        {
                            value = getElementValue( child, null );
                        }
                        
                        provider.addParam( name, value );
                    }
                    else if( childName.equals( EL_ACTION ) )
                    {
                        final String type = findRequiredAttribute( child, ATTR_TYPE ).toUpperCase();
                        final LibraryProviderActionType t;
                        
                        try
                        {
                            t = LibraryProviderActionType.valueOf( type );
                        }
                        catch( IllegalArgumentException e )
                        {
                            reportInvalidActionType( type, provider.getPluginId() );
                            throw new InvalidExtensionException();
                        }
                        
                        final IConfigurationElement elConfig 
                            = findOptionalElement( child, EL_CONFIG );
                        
                        final String configClassName;
                        
                        if( elConfig == null )
                        {
                            configClassName = null;
                        }
                        else
                        {
                            configClassName = findRequiredAttribute( elConfig, ATTR_CLASS );
                        }
                        
                        final IConfigurationElement elOperation 
                            = findRequiredElement( child, EL_OPERATION );
                        
                        final String operationClassName 
                            = findRequiredAttribute( elOperation, ATTR_CLASS );
                        
                        provider.addActionDef( t, configClassName, operationClassName );
                    }
                }
                
                if( provider != null )
                {
                    this.providers.add( provider );
                    this.providersLookupTable.put( provider.getId(), provider );
                }
            }
            catch( InvalidExtensionException e )
            {
                // Ignore and continue. The problem has already been reported to the user
                // in the log.
            }
        }
        
        for( Map.Entry<LibraryProvider,String> entry : providerToBaseIdMap.entrySet() )
        {
            final LibraryProvider provider = entry.getKey();
            final String baseProviderId = entry.getValue();
            final ILibraryProvider baseProvider = this.providersLookupTable.get( baseProviderId );
            
            if( baseProvider == null )
            {
                reportProviderNotDefined( baseProviderId, provider.getPluginId() );
                this.providers.remove( provider );
                this.providersLookupTable.remove( provider.getId() );
            }
            else
            {
                provider.setBaseProvider( baseProvider );
            }
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String libraryProviderNotDefined;
        public static String libraryProviderNotDefinedWithPlugin;
        public static String libraryProviderIdAlreadyUsed;
        public static String invalidActionType;
        
        static
        {
            initializeMessages( LibraryProviderFrameworkImpl.class.getName(), 
                                Resources.class );
        }
    }
    
}
