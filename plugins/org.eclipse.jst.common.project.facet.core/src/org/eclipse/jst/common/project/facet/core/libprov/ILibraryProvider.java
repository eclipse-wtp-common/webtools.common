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

package org.eclipse.jst.common.project.facet.core.libprov;

import java.util.Map;

import org.eclipse.wst.common.project.facet.core.IFacetedProjectBase;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * Represents a single library provider as declared to the framework.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public interface ILibraryProvider

    extends Comparable<ILibraryProvider>
    
{
    /**
     * Returns the id of the library provider. Each library provider must have a unique id.
     * 
     * @return the id of the library provider
     */
    
    String getId();
    
    /**
     * Returns the id of the plugin that declares this library provider.
     * 
     * @return the id of the plugin that declares this library provider
     */
    
    String getPluginId();
    
    /**
     * Returns the library provider label. This is the text that will be presented to the user
     * when referencing this library provider. The user never sees the library provider id. The
     * label can be translated.
     * 
     * @return the library provider label
     */
    
    String getLabel();
    
    /**
     * Returns the library provider that this provider extends or <code>null</code> if this
     * library provider doesn't extend anything.
     * 
     * @return the library provider that this provider extends or <code>null</code>
     */
    
    ILibraryProvider getBaseProvider();
    
    /**
     * Returns the root library provider in the extension chain. If this library provider does
     * not extend another, this method call will return this provider.
     * 
     * @return the root library provider in the extension chain
     */
    
    ILibraryProvider getRootProvider();
    
    /**
     * Indicates whether this library provider is only meant to be used as a base for other
     * providers and not to be used directly.
     * 
     * @return <code>true</code> if this library provider is only meant to be used as a base
     *   for other providers
     */
    
    boolean isAbstract();
    
    /**
     * Indicates whether this library provider is meant to be hidden from the user. This
     * typically means that it cannot be installed.
     * 
     * @return <code>true</code> if this library provider is meant to be hidden from the user
     */
    
    boolean isHidden();
    
    /**
     * Returns the priority number of this library provider. Each library provider can be assigned
     * a priority number that is used when sorting applicable providers in order to present
     * providers to the user. Higher priority numbers are sorted first. The provider with the
     * highest priority in the set of applicable providers is one that is selected by default.
     * 
     * @return the priority number of this library provider
     */
    
    int getPriority();
    
    /**
     * Checks whether this provider is enabled for the given context.
     * 
     * @param fpjwc the faceted project (or working copy)
     * @param fv the project facet that is making the request for libraries
     * @return <code>true</code> if this provider is enabled for the given context
     */
    
    boolean isEnabledFor( IFacetedProjectBase fproj, 
                          IProjectFacetVersion fv );
    
    /**
     * Determines whether this provider supports the specified action type.
     * 
     * @param type the action type to check
     * @return <code>true</code> if this provider supports the specified action type
     */
    
    boolean isActionSupported( LibraryProviderActionType type );
    
    /**
     * Returns the parameters associated with this library provider at declaration time. The
     * list of parameters and their meaning is specific to the implementation of this library
     * provider.
     * 
     * @return the parameters associated with this library provider at declaration time.
     */
    
    Map<String,String> getParams();
}
