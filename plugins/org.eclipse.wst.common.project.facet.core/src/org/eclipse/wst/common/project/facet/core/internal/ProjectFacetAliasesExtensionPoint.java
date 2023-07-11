/******************************************************************************
 * Copyright (c) 2010, 2023 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import static org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin.PLUGIN_ID;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findExtensions;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findRequiredAttribute;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getTopLevelElements;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.InvalidExtensionException;

/**
 * Contains the logic for processing the <code>aliases</code> extension point. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProjectFacetAliasesExtensionPoint
{
    public static final String EXTENSION_POINT_ID = "aliases"; //$NON-NLS-1$
    
    private static final String EL_FACET_ALIAS = "facet-alias"; //$NON-NLS-1$
    private static final String EL_FACET_VERSION_ALIAS = "facet-version-alias"; //$NON-NLS-1$
    private static final String ATTR_ALIAS = "alias"; //$NON-NLS-1$
    private static final String ATTR_FACET = "facet"; //$NON-NLS-1$
    private static final String ATTR_VERSION = "version"; //$NON-NLS-1$
    
    /*
     * Note that the facetAliases and facetVersionAliases data structures hold strings rather
     * than more specific IProjectFacet and IProjectFacetVersion objects. This is done this
     * way as this class is called during initialization of FacetedProjectFramework and we need
     * to avoid circular calls (this class cannot call back to FacetedProjectFramework).
     */
    
    private static Map<String,Set<String>> facetAliases = null;
    private static Map<String,Map<String,Set<String>>> facetVersionAliases = null;
    
    public static Set<String> getAliases( final IProjectFacet f )
    {
        readExtensions();
        
        final Set<String> aliases = facetAliases.get( f.getId() );
        
        if( aliases != null )
        {
            return aliases; 
        }
        
        return Collections.emptySet();
    }
    
    public static Set<String> getAliases( final IProjectFacetVersion fv )
    {
        readExtensions();
        
        final Map<String,Set<String>> versionToAliasesMap = facetVersionAliases.get( fv.getProjectFacet().getId() );
        
        if( versionToAliasesMap != null )
        {
            final Set<String> aliases = versionToAliasesMap.get( fv.getVersionString() );
            
            if( aliases != null )
            {
                return aliases;
            }
        }
        
        return Collections.emptySet();
    }
    
    private static synchronized void readExtensions()
    {
        if( facetAliases != null )
        {
            return;
        }
        
        facetAliases = new HashMap<String,Set<String>>();
        facetVersionAliases = new HashMap<String,Map<String,Set<String>>>();
        
        for( IConfigurationElement element 
             : getTopLevelElements( findExtensions( PLUGIN_ID, EXTENSION_POINT_ID ) ) )
        {
            final String elname = element.getName();
            
            try
            {
                if( elname.equals( EL_FACET_ALIAS ) || elname.equals( EL_FACET_VERSION_ALIAS ) )
                {
                    final String fid = findRequiredAttribute( element, ATTR_FACET );
                    final String alias = findRequiredAttribute( element, ATTR_ALIAS );

                    if( elname.equals( EL_FACET_ALIAS ) )
                    {
                        Set<String> aliases = facetAliases.get( fid );
                        
                        if( aliases == null )
                        {
                            aliases = new HashSet<String>();
                            facetAliases.put( fid, aliases );
                        }
                        for( String aliasId: alias.split(",") ) { //$NON-NLS-1$
                            aliases.add( aliasId.trim() );
                        }
                    }
                    else
                    {
                        final String fvstr = findRequiredAttribute( element, ATTR_VERSION );
                        
                        Map<String,Set<String>> versionToAliasesMap = facetVersionAliases.get( fid );
                        
                        if( versionToAliasesMap == null )
                        {
                            versionToAliasesMap = new HashMap<String,Set<String>>();
                            facetVersionAliases.put( fid, versionToAliasesMap );
                        }
                        
                        Set<String> aliases = versionToAliasesMap.get( fvstr );
                        
                        if( aliases == null )
                        {
                            aliases = new HashSet<String>();
                            versionToAliasesMap.put( fvstr, aliases );
                        }
                        
                        for( String aliasVersion: alias.split(",") ) { //$NON-NLS-1$
                            aliases.add( aliasVersion.trim() );
                        }
                    }
                }
            }
            catch( InvalidExtensionException e )
            {
                // Continue. The problem has been reported to the user via the log.
            }
        }
    }
    
}
