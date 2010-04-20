package org.eclipse.wst.common.project.facet.core.internal;

import java.util.EnumSet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

public final class ProblemLog 
{
    public enum ProblemCase
    {            
        FACET_NOT_DEFINED( "facet-not-defined" ), //$NON-NLS-1$
        FACET_VERSION_NOT_DEFINED( "facet-version-not-defined" ), //$NON-NLS-1$
        GROUP_NOT_DEFINED( "group-not-defined" ), //$NON-NLS-1$
        RUNTIME_COMPONENT_TYPE_NOT_DEFINED( "runtime-component-type-not-defined" ), //$NON-NLS-1$
        RUNTIME_COMPONENT_VERSION_NOT_DEFINED( "runtime-component-version-not-defined" ); //$NON-NLS-1$
        
        private String str;
        
        private ProblemCase( final String str )
        {
            this.str = str;
        }
        
        public String toString()
        {
            return this.str;
        }
    }

    public static final class Policy
    {
        private final EnumSet<ProblemCase> problemCases;
        
        public Policy()
        {
            this.problemCases = EnumSet.allOf( ProblemCase.class );
            
            for( ProblemCase problemCase : ProblemCase.values() )
            {
                this.problemCases.add( problemCase );
            }
        }
        
        public static Policy createBasedOnIgnoreProblemsAttr( final IConfigurationElement element )
        {
            return createBasedOnIgnoreProblemsAttr( element, DEFAULT_POLICY );
        }
        
        public static Policy createBasedOnIgnoreProblemsAttr( final IConfigurationElement element,
                                                              final Policy defaultProblemLoggingPolicy )
        {
            final String enabledWhenAttrValue = element.getAttribute( "ignore-problems" ); //$NON-NLS-1$
            
            if( enabledWhenAttrValue == null )
            {
                return defaultProblemLoggingPolicy;
            }
            else
            {
                final Policy policy = new Policy();
            
                for( String segment : enabledWhenAttrValue.split( "," ) ) //$NON-NLS-1$
                {
                    segment = segment.trim();
                    
                    boolean knownCase = false;
                    
                    if( segment.equalsIgnoreCase( NOT_DEFINED_WILDCARD ) )
                    {
                        policy.problemCases.remove( ProblemCase.FACET_NOT_DEFINED );
                        policy.problemCases.remove( ProblemCase.FACET_VERSION_NOT_DEFINED );
                        policy.problemCases.remove( ProblemCase.GROUP_NOT_DEFINED );
                        policy.problemCases.remove( ProblemCase.RUNTIME_COMPONENT_TYPE_NOT_DEFINED );
                        policy.problemCases.remove( ProblemCase.RUNTIME_COMPONENT_VERSION_NOT_DEFINED );
                        
                        knownCase = true;
                    }
                    else
                    {
                        for( ProblemCase problemCase : ProblemCase.values() )
                        {
                            if( segment.equalsIgnoreCase( problemCase.toString() ) )
                            {
                                policy.problemCases.remove( problemCase );
                                knownCase = true;
                                break;
                            }
                        }
                    }
                    
                    if( ! knownCase )
                    {
                        final String msg
                            = NLS.bind( Resources.problemCaseNotRecognized, segment ) +
                              NLS.bind( Resources.usedInPlugin, element.getContributor().getName() );
                        
                        FacetCorePlugin.logWarning( msg, true );
                    }
                }
                
                return policy;
            }
        }
        
        public boolean isLoggingEnabled( final ProblemCase problemCase )
        {
            return this.problemCases.contains( problemCase );
        }
    }

    private static final String NOT_DEFINED_WILDCARD = "not-defined"; //$NON-NLS-1$
    
    public static final Policy DEFAULT_POLICY = new Policy();
    
    public static void reportMissingFacet( final String fid,
                                           final String plugin )
    {
        reportMissingFacet( fid, plugin, DEFAULT_POLICY );
    }

    public static void reportMissingFacet( final String fid,
                                           final String plugin,
                                           final Policy policy )
    {
        if( policy.isLoggingEnabled( ProblemCase.FACET_NOT_DEFINED ) )
        {
            final String msg
                = NLS.bind( Resources.facetNotDefined, fid ) +
                  NLS.bind( Resources.usedInPlugin, plugin );
            
            FacetCorePlugin.logError( msg, true );
        }
    }
    
    public static void reportMissingFacetVersion( final IProjectFacet facet,
                                                  final String version,
                                                  final String plugin )
    {
        reportMissingFacetVersion( facet, version, plugin, DEFAULT_POLICY );
    }

    public static void reportMissingFacetVersion( final IProjectFacet facet,
                                                  final String version,
                                                  final String plugin,
                                                  final Policy policy )
    {
        if( policy.isLoggingEnabled( ProblemCase.FACET_VERSION_NOT_DEFINED ) )
        {
            final String msg
                = NLS.bind( Resources.facetVersionNotDefined, facet.getId(), version ) +
                  NLS.bind( Resources.usedInPlugin, plugin );
            
            FacetCorePlugin.logError( msg, true );
        }
    }

    public static void reportMissingGroup( final String groupId,
                                           final String pluginId )
    {
        reportMissingGroup( groupId, pluginId, DEFAULT_POLICY );
    }

    public static void reportMissingGroup( final String groupId,
                                           final String pluginId,
                                           final Policy policy )
    {
        if( policy.isLoggingEnabled( ProblemCase.GROUP_NOT_DEFINED ) )
        {
            final String msg
                = NLS.bind( Resources.groupNotDefined, groupId ) +
                  NLS.bind( Resources.usedInPlugin, pluginId );
            
            FacetCorePlugin.logError( msg, true );
        }
    }
    
    public static void reportMissingRuntimeComponentType( final String rct,
                                                          final String plugin )
    {
        reportMissingRuntimeComponentType( rct, plugin, DEFAULT_POLICY );
    }

    public static void reportMissingRuntimeComponentType( final String rct,
                                                          final String plugin,
                                                          final Policy policy )
    {
        if( policy.isLoggingEnabled( ProblemCase.RUNTIME_COMPONENT_TYPE_NOT_DEFINED ) )
        {
            final String msg
                = NLS.bind( Resources.runtimeComponentTypeNotDefined, rct ) +
                  NLS.bind( Resources.usedInPlugin, plugin );
            
            FacetCorePlugin.logError( msg, true );
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String problemCaseNotRecognized;
        public static String facetNotDefined;
        public static String facetVersionNotDefined;
        public static String groupNotDefined;
        public static String runtimeComponentTypeNotDefined;
        public static String usedInPlugin;
        
        static
        {
            initializeMessages( ProblemLog.class.getName(), Resources.class );
        }
    }
    
}
