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

package org.eclipse.wst.common.project.facet.core.internal;

import static org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin.PLUGIN_ID;
import static org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin.createErrorStatus;
import static org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin.log;
import static org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin.logError;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findExtensions;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findOptionalElement;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findRequiredAttribute;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findRequiredElement;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getElementValue;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getTopLevelElements;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.instantiate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IDynamicPreset;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IPresetFactory;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.PresetDefinition;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.util.internal.IndexedSet;
import org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.InvalidExtensionException;

/**
 * Contains the logic for processing the <code>presets</code> extension point.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PresetsExtensionPoint
{
    public static final String EXTENSION_POINT_ID = "presets"; //$NON-NLS-1$
    private static final String OLD_EXTENSION_POINT_ID = "facets"; //$NON-NLS-1$
    
    private static final String EL_DESCRIPTION = "description"; //$NON-NLS-1$
    private static final String EL_DYNAMIC_PRESET = "dynamic-preset"; //$NON-NLS-1$
    private static final String EL_FACET = "facet"; //$NON-NLS-1$
    private static final String EL_FACTORY = "factory"; //$NON-NLS-1$
    private static final String EL_LABEL = "label"; //$NON-NLS-1$
    private static final String EL_PRESET = "preset"; //$NON-NLS-1$
    private static final String EL_STATIC_PRESET = "static-preset"; //$NON-NLS-1$
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
    private static final String ATTR_EXTENDS = "extends"; //$NON-NLS-1$
    private static final String ATTR_ID = "id"; //$NON-NLS-1$
    private static final String ATTR_VERSION = "version"; //$NON-NLS-1$
    
    private static final String DEFAULT_DESCRIPTION = ""; //$NON-NLS-1$
    
    private static IndexedSet<String,IPreset> presets = null;
    
    public static Set<IPreset> getPresets()
    {
        readExtensions();
        return presets.getUnmodifiable();
    }
    
    public static IPreset getPreset( final String id )
    {
        readExtensions();
        return presets.get( id );
    }
    
    private static synchronized void readExtensions()
    {
        if( presets != null )
        {
            return;
        }
        
        presets = new IndexedSet<String,IPreset>();        
        
        for( IConfigurationElement element 
             : getTopLevelElements( findExtensions( PLUGIN_ID, EXTENSION_POINT_ID ) ) )
        {
            final String elName = element.getName();
            
            if( elName.equals( EL_STATIC_PRESET ) )
            {
                try
                {
                    readStaticPreset( element );
                }
                catch( InvalidExtensionException e )
                {
                    // Continue. The problem has been reported to the user via the log.
                }
            }
            else if( elName.equals( EL_DYNAMIC_PRESET ) )
            {
                try
                {
                    readDynamicPreset( element );
                }
                catch( InvalidExtensionException e )
                {
                    // Continue. The problem has been reported to the user via the log.
                }
            }
        }

        for( IConfigurationElement element 
             : getTopLevelElements( findExtensions( PLUGIN_ID, OLD_EXTENSION_POINT_ID ) ) )
        {
            if( element.getName().equals( EL_PRESET ) )
            {
                try
                {
                    readStaticPreset( element );
                }
                catch( InvalidExtensionException e )
                {
                    // Continue. The problem has been reported to the user via the log.
                }
            }
        }
        
        // Resolve the base presets. Make sure that no presets are extending presets that do not
        // exist, check for circular references, etc.
        
        Set<IPreset> copy = new HashSet<IPreset>( presets ); 
        
        for( IPreset preset : copy )
        {
            if( presets.contains( preset ) )
            {
                resolveBasePreset( preset, new HashSet<IPreset>() );
            }
        }
        
        // Find static presets that are extending dynamic presets and make them into dynamic
        // presets (StaticExtendingDynamicPreset class).
        
        boolean doAnotherPass = true;
        
        while( doAnotherPass )
        {
            doAnotherPass = false;
            
            for( IPreset preset : presets )
            {
                if( preset.getType() == IPreset.Type.STATIC )
                {
                    final StaticPreset stPreset = (StaticPreset) preset;
                    final String basePresetId = stPreset.getBasePresetId();
                    
                    if( basePresetId != null )
                    {
                        final IPreset basePreset = getPreset( basePresetId );
                        
                        if( basePreset.getType() == IPreset.Type.DYNAMIC )
                        {
                            final StaticExtendingDynamicPreset stPresetNew
                                = new StaticExtendingDynamicPreset( stPreset.getId(), 
                                                                    stPreset.getPluginId(),
                                                                    stPreset.getLabel(),
                                                                    stPreset.getDescription(),
                                                                    stPreset.getBasePresetId(),
                                                                    stPreset.getProjectFacets() );
                            
                            presets.add( stPresetNew.getId(), stPresetNew );

                            doAnotherPass = true;
                            break;
                        }
                    }
                }
            }
        }
    }
    
    private static void readStaticPreset( final IConfigurationElement el )
    
        throws InvalidExtensionException
        
    {
        final String pluginId = el.getContributor().getName();
        final String id = findRequiredAttribute( el, ATTR_ID );
        
        final IConfigurationElement elLabel = findOptionalElement( el, EL_LABEL );
        final String label = getElementValue( elLabel, id );
        
        final IConfigurationElement elDesc = findOptionalElement( el, EL_DESCRIPTION );
        final String description = getElementValue( elDesc, DEFAULT_DESCRIPTION );
        
        String basePreset = el.getAttribute( ATTR_EXTENDS );
        
        if( basePreset != null )
        {
            basePreset = basePreset.trim();
            
            if( basePreset.length() == 0 )
            {
                basePreset = null;
            }
        }
        
        final Set<IProjectFacetVersion> facets = new HashSet<IProjectFacetVersion>();

        for( IConfigurationElement child : el.getChildren() )
        {
            final String childName = child.getName();
            
            if( childName.equals( EL_FACET ) )
            {
                final String fid = findRequiredAttribute( child, ATTR_ID );
                final String fver = findRequiredAttribute( child, ATTR_VERSION );
                
                if( ! ProjectFacetsManager.isProjectFacetDefined( fid ) )
                {
                    final String msg
                        = Resources.bind( Resources.presetUsesUnknownFacet, id, pluginId, fid );
                    
                    logError( msg );
                    
                    return;
                }
                
                final IProjectFacet f = ProjectFacetsManager.getProjectFacet( fid );
                
                if( ! f.hasVersion( fver ) )
                {
                    final String msg
                        = Resources.bind( Resources.presetUsesUnknownFacetVersion, id, pluginId,
                                          fid, fver );
                    
                    logError( msg );
                    
                    return;
                }
                
                final IProjectFacetVersion fv = f.getVersion( fver );
                
                facets.add( fv );
            }
        }
        
        final StaticPreset preset 
            = new StaticPreset( id, pluginId, label, description, basePreset, facets );
        
        presets.add( id, preset );
    }

    private static void readDynamicPreset( final IConfigurationElement el )
    
        throws InvalidExtensionException
        
    {
        final String pluginId = el.getContributor().getName();
        final String id = findRequiredAttribute( el, ATTR_ID );
        final IConfigurationElement elFactory = findRequiredElement( el, EL_FACTORY );
        final String factoryClassName = findRequiredAttribute( elFactory, ATTR_CLASS );
        
        final DynamicPreset preset = new DynamicPreset( id, pluginId, factoryClassName );
        presets.add( id, preset );
    }
    
    private static boolean resolveBasePreset( final IPreset preset,
                                              final Set<IPreset> visitedPresets )
    {
        if( preset.getType() == IPreset.Type.STATIC )
        {
            final StaticPreset stPreset = (StaticPreset) preset;
            final String basePresetId = stPreset.getBasePresetId();
            
            if( basePresetId != null )
            {
                final IPreset basePreset = getPreset( basePresetId );
                boolean problem = false;
                
                visitedPresets.add( preset );
                
                if( basePreset == null )
                {
                    final String msg
                        = Resources.bind( Resources.basePresetNotFound, stPreset.getId(), 
                                          stPreset.getPluginId(), basePresetId );
                    
                    logError( msg );
                    
                    problem = true;
                }
                else if( visitedPresets.contains( basePreset ) )
                {
                    final StringBuilder cycle = new StringBuilder();
                    final int cycleSize = visitedPresets.size();
                    int count = 0;
                    
                    for( IPreset vp : visitedPresets )
                    {
                        count++;
                        
                        if( count > 1 )
                        {
                            if( count == cycleSize )
                            {
                                cycle.append( " and " ); //$NON-NLS-1$
                            }
                            else
                            {
                                cycle.append( ", " ); //$NON-NLS-1$
                            }
                        }
                        
                        cycle.append( '"' );
                        cycle.append( vp.getId() );
                        cycle.append( '"' );
                    }
                    
                    final String msg = Resources.bind( Resources.cycleDetected, cycle.toString() );
                    
                    logError( msg );
                    
                    problem = true;
                }
                else if( ! resolveBasePreset( basePreset, visitedPresets ) )
                {
                    problem = true;
                }

                if( problem )
                {
                    PresetsExtensionPoint.presets.delete( preset.getId() );
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private static class StaticPreset
    
        implements IPreset
        
    {
        private final String id;
        private final String pluginId;
        private final String label;
        private final String description;
        private final String basePresetId;
        private final Set<IProjectFacet> facets;
        private final Set<IProjectFacetVersion> facetVersions;
        private final Set<IProjectFacetVersion> facetVersionsReadOnly;
        
        public StaticPreset( final String id,
                             final String pluginId,
                             final String label,
                             final String description,
                             final String basePreset,
                             final Set<IProjectFacetVersion> facets )
        {
            this.id = id;
            this.pluginId = pluginId;
            this.label = label;
            this.description = description;
            this.basePresetId = basePreset;
            this.facetVersions = facets;
            this.facetVersionsReadOnly = Collections.unmodifiableSet( this.facetVersions );
            
            this.facets = new HashSet<IProjectFacet>( this.facetVersions.size() );
            
            for( IProjectFacetVersion fv : this.facetVersions )
            {
                this.facets.add( fv.getProjectFacet() );
            }
        }
        
        public final String getId()
        {
            return this.id;
        }
        
        public final String getPluginId()
        {
            return this.pluginId;
        }

        public Type getType()
        {
            return Type.STATIC;
        }
        
        public final String getLabel()
        {
            return this.label;
        }

        public final String getDescription()
        {
            return this.description;
        }
        
        public final String getBasePresetId()
        {
            return this.basePresetId;
        }

        public Set<IProjectFacetVersion> getProjectFacets()
        {
            if( this.basePresetId == null )
            {
                return this.facetVersionsReadOnly;
            }
            else
            {
                final IPreset basePreset = getPreset( this.basePresetId );
                return createCombinedFacetSet( basePreset );
            }
        }

        public final boolean isUserDefined()
        {
            return false;
        }
        
        public final String toString()
        {
            return this.id;
        }
        
        public boolean equals( final Object obj )
        {
            if( ! ( obj instanceof StaticPreset ) )
            {
                return false;
            }
            
            final StaticPreset p = (StaticPreset) obj;
            
            return this.id.equals( p.id ) &&
                   this.pluginId.equals( p.pluginId ) &&
                   this.label.equals( p.label ) &&
                   this.description.equals( p.description ) &&
                   this.facetVersions.equals( p.facetVersions );
        }
        
        protected final Set<IProjectFacetVersion> createCombinedFacetSet( final IPreset basePreset )
        {
            final Set<IProjectFacetVersion> result 
                = new HashSet<IProjectFacetVersion>( this.facetVersions );
            
            for( IProjectFacetVersion fv : basePreset.getProjectFacets() )
            {
                if( ! this.facets.contains( fv.getProjectFacet() ) )
                {
                    result.add( fv );
                }
            }
            
            return Collections.unmodifiableSet( result );
        }
    }
    
    private static final class StaticExtendingDynamicPreset
    
        extends StaticPreset
        implements IDynamicPreset
        
    {
        public StaticExtendingDynamicPreset( final String id,
                                             final String pluginId,
                                             final String label,
                                             final String description,
                                             final String basePresetId,
                                             final Set<IProjectFacetVersion> facets )
        {
            super( id, pluginId, label, description, basePresetId, facets );
        }
        
        @Override
        public Type getType()
        {
            return Type.DYNAMIC;
        }
        
        @Override
        public Set<IProjectFacetVersion> getProjectFacets()
        {
            return Collections.emptySet();
        }

        public IPreset resolve( final Map<String,Object> context )
        {
            final IDynamicPreset basePreset = (IDynamicPreset) getPreset( getBasePresetId() );
            final IPreset resBasePreset = basePreset.resolve( context );
            
            if( resBasePreset == null )
            {
                return null;
            }
            else
            {
                final Set<IProjectFacetVersion> facets = createCombinedFacetSet( resBasePreset );
                
                return new StaticPreset( getId(), getPluginId(), getLabel(), getDescription(), 
                                         getBasePresetId(), facets );
            }
        }
    }
    
    private static final class DynamicPreset
    
        implements IDynamicPreset
        
    {
        private final String id;
        private final String pluginId;
        private final String factoryClassName;
        
        public DynamicPreset( final String id,
                              final String pluginId,
                              final String factoryClassName )
        {
            this.id = id;
            this.pluginId = pluginId;
            this.factoryClassName = factoryClassName;
        }

        public String getId()
        {
            return this.id;
        }
        
        public Type getType()
        {
            return Type.DYNAMIC;
        }

        public String getLabel()
        {
            return this.id;
        }

        public String getDescription()
        {
            return ""; //$NON-NLS-1$
        }

        public Set<IProjectFacetVersion> getProjectFacets()
        {
            return Collections.emptySet();
        }

        public boolean isUserDefined()
        {
            return false;
        }
        
        public IPreset resolve( final Map<String,Object> context )
        {
            IPresetFactory factory = null;
            
            try
            {
                factory = instantiate( this.pluginId, this.factoryClassName, IPresetFactory.class );
            }
            catch( CoreException e )
            {
                log( e.getStatus() );
            }
            
            if( factory != null )
            {
                PresetDefinition def = null;
                
                try
                {
                    def = factory.createPreset( this.id, context );
                }
                catch( Exception e )
                {
                    final String msg 
                        = Resources.bind( Resources.failedWhileInvokingPresetFactory, 
                                          this.pluginId );
                    
                    log( createErrorStatus( msg, e ) );
                }
                
                if( def != null )
                {
                    final StaticPreset staticPreset
                        = new StaticPreset( this.id, this.pluginId, def.getLabel(), 
                                            def.getDescription(), null, def.getProjectFacets() );
                    
                    return staticPreset;
                }
            }
            
            return null;
        }
        
        public String toString()
        {
            return this.id;
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String presetUsesUnknownFacet;
        public static String presetUsesUnknownFacetVersion;
        public static String basePresetNotFound;
        public static String cycleDetected;
        public static String failedWhileInvokingPresetFactory;
        
        static
        {
            initializeMessages( PresetsExtensionPoint.class.getName(), Resources.class );
        }
        
        public static String bind( final String template,
                                   final Object arg1,
                                   final Object arg2,
                                   final Object arg3 )
        {
            return NLS.bind( template, new Object[] { arg1, arg2, arg3 } );
        }

        public static String bind( final String template,
                                   final Object arg1,
                                   final Object arg2,
                                   final Object arg3,
                                   final Object arg4 )
        {
            return NLS.bind( template, new Object[] { arg1, arg2, arg3, arg4 } );
        }
    }
    
}
