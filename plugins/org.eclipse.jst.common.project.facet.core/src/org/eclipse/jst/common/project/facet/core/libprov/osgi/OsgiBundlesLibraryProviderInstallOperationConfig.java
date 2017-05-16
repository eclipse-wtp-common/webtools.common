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

package org.eclipse.jst.common.project.facet.core.libprov.osgi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin;
import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderOperationConfig;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectBase;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @since 1.4
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class OsgiBundlesLibraryProviderInstallOperationConfig

	extends LibraryProviderOperationConfig
    
{
    private static final String PARAM_CONTAINER_LABEL = "container.label"; //$NON-NLS-1$
    private static final String PARAM_BUNDLE = "bundle."; //$NON-NLS-1$
    
    private String containerLabelOverride;
    private List<BundleReference> bundleReferences;
    private List<BundleReference> bundleReferencesReadOnly;

    public OsgiBundlesLibraryProviderInstallOperationConfig()
    {
        this.containerLabelOverride = null;
        this.bundleReferences = new ArrayList<BundleReference>();
        this.bundleReferencesReadOnly = Collections.unmodifiableList( this.bundleReferences );
    }
    
    @Override
    public synchronized void init( final IFacetedProjectBase fproj,
                                   final IProjectFacetVersion fv,
                                   final ILibraryProvider provider )
    {
        super.init( fproj, fv, provider );
        
        this.containerLabelOverride = provider.getParams().get( PARAM_CONTAINER_LABEL );
        this.bundleReferences.addAll( getBundleReferences( provider ) );
    }
    
    public List<BundleReference> getBundleReferences()
    {
        return this.bundleReferencesReadOnly;
    }
    
    public static List<BundleReference> getBundleReferences( final ILibraryProvider provider )
    {
        final List<BundleReference> bundleReferences = new ArrayList<BundleReference>();
        final Map<String,String> params = provider.getParams();
        String unparsedBundleReference;
        
        for( int i = 0; ( unparsedBundleReference = params.get( PARAM_BUNDLE + String.valueOf( i ) ) ) != null; i++ )
        {
            bundleReferences.add( OsgiBundlesContainer.parseBundleReference( unparsedBundleReference ) );
        }
        
        return bundleReferences;
    }
    
    public IClasspathAttribute[] getClasspathAttributes()
    {
        return null;
    }
    
    public String getContainerLabel()
    {
        return this.containerLabelOverride;
    }

    @Override
    public IStatus validate()
    {
        IStatus status = super.validate();
        
        for( BundleReference bundleReference : this.bundleReferences )
        {
            if( ! bundleReference.isResolvable() )
            {
                final String msg;
                final String bundleId = bundleReference.getBundleId();
                final VersionRange versionRange = bundleReference.getVersionRange();
                
                if( versionRange == null )
                {
                    msg = NLS.bind( Resources.bunldeCannotBeResolvedNoVersion, bundleId ); 
                }
                else
                {
                    msg = NLS.bind( Resources.bundleCannotBeResolved, bundleId, versionRange.toString() );
                }
                
                status = new Status( IStatus.ERROR, FacetedProjectFrameworkJavaPlugin.PLUGIN_ID, msg );
                
                break;
            }
        }
        
        return status;
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String bundleCannotBeResolved;
        public static String bunldeCannotBeResolvedNoVersion;
        
        static
        {
            initializeMessages( OsgiBundlesLibraryProviderInstallOperationConfig.class.getName(), 
                                Resources.class );
        }
    }

}
