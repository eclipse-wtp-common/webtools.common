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

package org.eclipse.jst.common.project.facet.core.libprov.user.internal;

import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.PLUGIN_ID;
import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.createErrorStatus;
import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.log;
import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.logError;
import static org.eclipse.wst.common.project.facet.core.util.internal.DomUtil.doc;
import static org.eclipse.wst.common.project.facet.core.util.internal.DomUtil.elements;
import static org.eclipse.wst.common.project.facet.core.util.internal.DomUtil.text;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findExtensions;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.findOptionalElement;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.getTopLevelElements;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.common.project.facet.core.libprov.EnablementExpressionContext;
import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.user.UserLibraryProviderInstallOperationConfig;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectBase;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.util.internal.XmlParseException;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Contains the logic for processing the <code>downloadableLibraries</code> extension point. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class DownloadableLibrariesExtensionPoint
{
    public static final String EXTENSION_POINT_ID = "downloadableLibraries"; //$NON-NLS-1$
    
    private static final String ATTR_PATH = "path"; //$NON-NLS-1$
    private static final String ATTR_URL = "url"; //$NON-NLS-1$
    private static final String EL_IMPORT_DEFINITIONS = "import-definitions"; //$NON-NLS-1$
    private static final String EL_ENABLEMENT = "enablement"; //$NON-NLS-1$
    
    private static final String EL_LIBRARY = "library"; //$NON-NLS-1$
    private static final String EL_NAME = "name"; //$NON-NLS-1$
    private static final String EL_DOWNLOAD_PROVIDER = "download-provider"; //$NON-NLS-1$
    private static final String EL_DOWNLOAD_URL = "download-url"; //$NON-NLS-1$
    private static final String EL_LICENSE_URL = "license-url"; //$NON-NLS-1$
    private static final String EL_INCLUDE = "include"; //$NON-NLS-1$
    private static final String EL_EXCLUDE = "exclude"; //$NON-NLS-1$
    private static final String EL_ATTRIBUTES = "attributes"; //$NON-NLS-1$
    private static final String EL_COMPONENT = "component"; //$NON-NLS-1$
    private static final String EL_SOURCE = "source"; //$NON-NLS-1$
    private static final String EL_JAVADOC = "javadoc"; //$NON-NLS-1$

    private static final String EXPR_VAR_CONTEXT = "context"; //$NON-NLS-1$
    private static final String EXPR_VAR_REQUESTING_PROJECT_FACET = "requestingProjectFacet"; //$NON-NLS-1$
    private static final String EXPR_VAR_PROJECT_FACETS = "projectFacets"; //$NON-NLS-1$
    private static final String EXPR_VAR_TARGETED_RUNTIMES = "targetedRuntimes"; //$NON-NLS-1$
    private static final String EXPR_VAR_PROVIDER = "provider"; //$NON-NLS-1$

    public static List<DownloadableLibrary> list( final UserLibraryProviderInstallOperationConfig cfg,
                                                  final IProgressMonitor monitor )
    {
        final IProgressMonitor mon = ( monitor != null ? monitor : new NullProgressMonitor() );
        
        mon.beginTask( Resources.searchingForLibrariesTaskName, IProgressMonitor.UNKNOWN );
        
        try
        {
            final List<DownloadableLibrary> libraries = new ArrayList<DownloadableLibrary>();
            
            for( IConfigurationElement element 
                 : getTopLevelElements( findExtensions( PLUGIN_ID, EXTENSION_POINT_ID ) ) )
            {
                if( mon.isCanceled() )
                {
                    return null;
                }
                
                final String pluginId = element.getContributor().getName();
                
                if( element.getName().equals( EL_IMPORT_DEFINITIONS ) )
                {
                    final IConfigurationElement elEnablement = findOptionalElement( element, EL_ENABLEMENT );
                    
                    if( elEnablement != null )
                    {
                        try
                        {
                            final Expression expr 
                                = ExpressionConverter.getDefault().perform( elEnablement );
                            
                            final EvaluationContext context
                                = createEvaluationContext( cfg.getFacetedProject(), cfg.getProjectFacetVersion(), 
                                                           cfg.getLibraryProvider() );
                            
                            if( expr.evaluate( context ) != EvaluationResult.TRUE )
                            {
                                continue;
                            }
                        }
                        catch( CoreException e )
                        {
                            log( e );
                            break;
                        }
                    }
                    
                    URL url = null;
                    
                    final String pathAttr = element.getAttribute( ATTR_PATH );
                    final String urlAttr = element.getAttribute( ATTR_URL );
                    
                    if( pathAttr != null )
                    {
                        final Bundle plugin = Platform.getBundle( pluginId );
                        url = FileLocator.find( plugin, new Path( pathAttr ), null );
                        
                        if( url == null )
                        {
                            final String msg = NLS.bind( Resources.errorPluginResourceNotFound, pluginId, pathAttr );
                            logError( msg );
                            continue;
                        }
                    }
                    else if( urlAttr != null )
                    {
                        try
                        {
                            url = new URL( urlAttr );
                        }
                        catch( MalformedURLException e )
                        {
                            final String msg = NLS.bind( Resources.errorMalformedUrl, pluginId, urlAttr );
                            log( createErrorStatus( msg, e ) );
                            continue;
                        }
                    }
                    else
                    {
                        final String msg = NLS.bind( Resources.errorMissingPathOrUrl, pluginId );
                        logError( msg );
                        continue;
                    }
                    
                    readLibraryDefinitions( url, libraries );
                }
            }
            
            return libraries;
        }
        finally
        {
            mon.done();
        }
    }
    
    private static EvaluationContext createEvaluationContext( final IFacetedProjectBase fproj,
                                                              final IProjectFacetVersion fv,
                                                              final ILibraryProvider provider )
    {
        final EvaluationContext evalContext = new EvaluationContext( null, fv );
        final EnablementExpressionContext context = new EnablementExpressionContext( fproj, fv, provider );
        evalContext.addVariable( EXPR_VAR_CONTEXT, context );
        evalContext.addVariable( EXPR_VAR_REQUESTING_PROJECT_FACET, fv );
        evalContext.addVariable( EXPR_VAR_PROJECT_FACETS, fproj.getProjectFacets() );
        evalContext.addVariable( EXPR_VAR_TARGETED_RUNTIMES, fproj.getTargetedRuntimes() );
        evalContext.addVariable( EXPR_VAR_PROVIDER, provider );        
        evalContext.setAllowPluginActivation( true );
    
        return evalContext;
    }
    
    private static void readLibraryDefinitions( final URL url,
                                                final List<DownloadableLibrary> libraries )
    {
        Document document = null;
        InputStream in = null;
        
        try
        {
            in = url.openStream();
        }
        catch( IOException e )
        {
            final String msg = NLS.bind( Resources.errorReadingDefFile, url.toString() );
            log( createErrorStatus( msg, e ) );
            return;
        }
        
        try
        {
            document = doc( in );
        }
        catch( XmlParseException e )
        {
            final String msg = NLS.bind( Resources.errorParsingDefFile, url.toString() );
            log( createErrorStatus( msg, e ) );
            return;
        }
        finally
        {
            try
            {
                in.close();
            }
            catch( IOException e ) {}
        }
        
        if( document != null )
        {
            final Element root = document.getDocumentElement();
            
            if( root != null )
            {
                for( Element elLibrary : elements( root, EL_LIBRARY ) )
                {
                    try
                    {
                        final DownloadableLibrary library = new DownloadableLibrary();
                        
                        final String name = getRequiredElementText( url, elLibrary, EL_NAME );
                        library.setName( name );
                        
                        final String provider = getRequiredElementText( url, elLibrary, EL_DOWNLOAD_PROVIDER );
                        library.setDownloadProvider( provider );
                        
                        final String downloadUrl = getRequiredElementText( url, elLibrary, EL_DOWNLOAD_URL );
                        library.setUrl( downloadUrl );
                        
                        final String licenseUrl = text( elLibrary, EL_LICENSE_URL );
                        library.setLicenseUrl( licenseUrl );
                        
                        for( Element elInclude : elements( elLibrary, EL_INCLUDE ) )
                        {
                            library.addIncludePattern( text( elInclude ) );
                        }

                        for( Element elExclude : elements( elLibrary, EL_EXCLUDE ) )
                        {
                            library.addExcludePattern( text( elExclude ) );
                        }
                        
                        for( Element elAttachment : elements( elLibrary, EL_ATTRIBUTES ) )
                        {
                            final String jarPath = getRequiredElementText( url, elAttachment, EL_COMPONENT );
                            
                            final DownloadableLibraryComponentAttributes attachment 
                                = library.getComponentAttributes( new Path( jarPath ), true );
                            
                            final String sourceArchivePath = text( elAttachment, EL_SOURCE );
                            attachment.setSourceArchivePath( sourceArchivePath );
                            
                            final String javadocArchivePath = text( elAttachment, EL_JAVADOC );
                            attachment.setJavadocArchivePath( javadocArchivePath );
                        }
                        
                        libraries.add( library );
                    }
                    catch( InvalidLibraryDefinitionException e )
                    {
                        // Bad library definition. Problem already reported to the user in the log,
                        // so we just need to continue gracefully.
                        
                        continue;
                    }
                }
            }
        }
    }
    
    private static String getRequiredElementText( final URL url,
                                                  final Element parent,
                                                  final String childElementName )
    
        throws InvalidLibraryDefinitionException
        
    {
        final String val = text( parent, childElementName );
        
        if( val == null )
        {
            final String msg = NLS.bind( Resources.errorDefMissingElement, childElementName, url.toString() );
            logError( msg );
            throw new InvalidLibraryDefinitionException();
        }
        
        return val;
    }
    
    private static class InvalidLibraryDefinitionException
    
        extends Exception
        
    {
        private static final long serialVersionUID = 1L;
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String searchingForLibrariesTaskName;
        public static String errorDefMissingElement;
        public static String errorParsingDefFile;
        public static String errorReadingDefFile;
        public static String errorPluginResourceNotFound;
        public static String errorMalformedUrl;
        public static String errorMissingPathOrUrl;
    
        static
        {
            initializeMessages( DownloadableLibrariesExtensionPoint.class.getName(), 
                                Resources.class );
        }
    }
    
    
}
