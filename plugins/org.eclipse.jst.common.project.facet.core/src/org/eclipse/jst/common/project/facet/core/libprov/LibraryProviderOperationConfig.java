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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.common.project.facet.core.libprov.internal.PropertiesHost;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectBase;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * This class should be subclassed in order to implement parameterization of the library
 * provider operations (such as install and uninstall). If no additional parameters are
 * necessary, this class can also be used directly.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since WTP 3.1
 */

public class LibraryProviderOperationConfig

    extends PropertiesHost
    
{
    private IFacetedProjectBase fpj;
    private IProjectFacetVersion fv;
    private ILibraryProvider provider;
    
    /**
     * Initializes the operation config. This method is called soon after the provider
     * is instantiated. Extenders can override in order to add to the initialization, but
     * have make sure to forward the init call up the inheritance chain.
     * 
     * @param fpj the faceted project (or a working copy)
     * @param fv the project facet that is making the request for libraries
     * @param provider the library provider (useful if the same operation config class
     *   is re-used between multiple providers)
     */
    
    public void init( final IFacetedProjectBase fpj,
                      final IProjectFacetVersion fv,
                      final ILibraryProvider provider )
    {
        this.fpj = fpj;
        this.fv = fv;
        this.provider = provider;
    }
    
    /**
     * Returns the faceted project (or a working copy).
     * 
     * @return the faceted project (or a working copy)
     */
    
    public final IFacetedProjectBase getFacetedProject()
    {
        return this.fpj;
    }
    
    /**
     * Returns the project facet that made the request for libraries.
     * 
     * @return the project facet that made the request for libraries
     */
    
    public final IProjectFacet getProjectFacet()
    {
        return this.fv.getProjectFacet();
    }
    
    /**
     * Returns the project facet version that made the request for libraries.
     * 
     * @return the project facet version that made the request for libraries
     */
    
    public final IProjectFacetVersion getProjectFacetVersion()
    {
        return this.fv;
    }
    
    /**
     * Returns the library provider. This is particularly useful in cases where the same
     * operation config class is re-used across multiple library providers.
     * 
     * @return the library provider
     */
    
    public final ILibraryProvider getLibraryProvider()
    {
        return this.provider;
    }
    
    /**
     * Adds a listener that will be notified when the property changes. If no properties
     * are specified, the listener will be notified when any of the properties change. The 
     * extender is responsible for defining available properties (typically via constants 
     * in the config class) and for calling the notifyListeners method when a property changes.
     * 
     * @param listener the listener that should be registered
     * @param properties the list of properties to listen to or an empty list to list to
     *   all properties
     */

    @Override
    public final void addListener( final IPropertyChangeListener listener,
                                   final String... properties )
    {
        super.addListener( listener, properties );
    }
    
    /**
     * Removes the specified listener from the notification queue. Does nothing if the listener
     * is not registered.
     * 
     * @param listener the listener that should be removed
     */
    
    @Override
    public final void removeListener( final IPropertyChangeListener listener )
    {
        super.removeListener( listener );
    }
    
    /**
     * Notifies registered listeners that a property has changed. The extender is responsible 
     * for defining available properties (typically via constants in the config class) and for 
     * calling this method when a property changes.
     * 
     * @param property the property that has changed
     * @param oldValue the old value
     * @param newValue the new value
     */
    
    @Override
    protected final void notifyListeners( final String property,
                                          final Object oldValue,
                                          final Object newValue )
    {
        super.notifyListeners( property, oldValue, newValue );
    }
    
    /**
     * Validates the state of this operation config object and returns a status object
     * describing any problems. If no problems are detected, this should return OK
     * status (which is what the default implementation does).
     * 
     * @return the result of validating this operation config
     */
    
    public IStatus validate()
    {
        return Status.OK_STATUS;
    }
    
    /**
     * Resets this operation config to its initial state (prior to any user changes). The
     * default implementation does not do anything.
     */

    public void reset() {}

    /**
     * Allows the operation config implementation to dispose of any resources acquired during
     * the life of this object. This is a good place to remove any external listeners that
     * are registered. The default implementation does not do anything.
     */
    
    public void dispose() {}
    
}
