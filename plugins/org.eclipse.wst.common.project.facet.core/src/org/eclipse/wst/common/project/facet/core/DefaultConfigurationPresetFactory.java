/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.internal.DefaultFacetsExtensionPoint;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * Preset factory for the <code>default.configuration</code> preset. The contents of this preset
 * are calculated as follows:
 * 
 * <ol>
 *   <li>If a runtime is selected, this preset will contain default facets as specified by
 *     {@see IRuntime.getDefaultFacets(Set)}.</li>
 *   <li>If no runtime is selected, this preset will contain default versions for all of the 
 *     fixed facets as specified by {@see IProjectFacet.getDefaultVersion()}.
 * </ol>
 * 
 * @since 3.0
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class DefaultConfigurationPresetFactory

    implements IPresetFactory
    
{
    public static final String PRESET_ID = "default.configuration"; //$NON-NLS-1$
    
    public PresetDefinition createPreset( final String presetId,
                                          final Map<String,Object> context ) 
    
        throws CoreException
        
    {
        final IFacetedProjectBase fproj 
            = (IFacetedProjectBase) context.get( IDynamicPreset.CONTEXT_KEY_FACETED_PROJECT );
        
        final IRuntime runtime = fproj.getPrimaryRuntime(); 
        
        final String label;
        final String description;
        
        if( runtime != null )
        {
            label = Resources.bind( Resources.presetLabel, runtime.getLocalizedName() );
            description = Resources.bind( Resources.presetDescription, runtime.getLocalizedName() );
        }
        else
        {
            label = Resources.presetLabelNoRuntime;
            description = Resources.presetDescriptionNoRuntime;
        }

        final Set<IProjectFacetVersion> facets 
            = DefaultFacetsExtensionPoint.getDefaultFacets( fproj );
        
        return new PresetDefinition( label, description, facets );
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String presetLabel;
        public static String presetDescription;
        public static String presetLabelNoRuntime;
        public static String presetDescriptionNoRuntime;
        
        static
        {
            initializeMessages( DefaultConfigurationPresetFactory.class.getName(), 
                                Resources.class );
        }
    }
    
}
