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

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IDynamicPreset;
import org.eclipse.wst.common.project.facet.core.IPresetFactory;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.PresetDefinition;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * Preset factory for the <code>minimal.configuration</code> preset. This preset only contains
 * fixed facets. The version of the facets are calculated as follows: 
 * 
 * <ol>
 *   <li>If a runtime is selected, the versions are looked up using 
 *     {@see IRuntime.getDefaultFacets(Set)}.</li>
 *   <li>If no runtime is selected, this versions are the default versions as specified 
 *     by {@see IProjectFacet.getDefaultVersion()}.
 * </ol>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class MinimalConfigurationPresetFactory

    implements IPresetFactory
    
{
    @SuppressWarnings( "unchecked" )
    public PresetDefinition createPreset( final String presetId,
                                          final Map<String,Object> context ) 
    
        throws CoreException
        
    {
        final Set<IProjectFacet> fixed
            = (Set<IProjectFacet>) context.get( IDynamicPreset.CONTEXT_KEY_FIXED_FACETS );
        
        if( fixed == null )
        {
            return null;
        }
    
        final IRuntime runtime 
            = (IRuntime) context.get( IDynamicPreset.CONTEXT_KEY_PRIMARY_RUNTIME );
        
        final Set<IProjectFacetVersion> facets = new HashSet<IProjectFacetVersion>();
        
        if( runtime != null )
        {
            final Set<IProjectFacetVersion> defaultFacets = runtime.getDefaultFacets( fixed );
            
            for( IProjectFacet f : fixed )
            {
                facets.add( findProjectFacetVersion( defaultFacets, f ) );
            }
        }
        else
        {
            for( IProjectFacet f : fixed )
            {
                facets.add( f.getDefaultVersion() );
            }
        }
        
        return new PresetDefinition( Resources.presetLabel, Resources.presetDescription, facets );
    }
    
    private static IProjectFacetVersion findProjectFacetVersion( final Set<IProjectFacetVersion> facets,
                                                                 final IProjectFacet facet )
    {
        for( IProjectFacetVersion fv : facets )
        {
            if( fv.getProjectFacet() == facet )
            {
                return fv;
            }
        }
        
        throw new IllegalStateException();
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String presetLabel;
        public static String presetDescription;
        
        static
        {
            initializeMessages( MinimalConfigurationPresetFactory.class.getName(), 
                                Resources.class );
        }
    }
    
}
