/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.componentcore.datamodel;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.ActionConfig;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetDataModelConfigAdapter

    extends ActionConfig
    
{
    private final IDataModel dm;
    
    public FacetDataModelConfigAdapter( final IDataModel dm )
    {
        this.dm = dm;
    }
    
    public void setProjectFacetVersion( final IProjectFacetVersion fv )
    {
        dm.setProperty( IFacetDataModelProperties.FACET_VERSION, fv );
    }
    
    public void setFacetedProjectWorkingCopy( final IFacetedProjectWorkingCopy fpjwc )
    {
        dm.setProperty( IFacetDataModelProperties.FACETED_PROJECT_WORKING_COPY, fpjwc );
        
        final IFacetedProjectListener nameChangeListener = new IFacetedProjectListener()
        {
            public void handleEvent( final IFacetedProjectEvent event )
            {
                dm.setStringProperty( IFacetDataModelProperties.FACET_PROJECT_NAME, fpjwc.getProjectName() );
            }
        };
        
        fpjwc.addListener( nameChangeListener, IFacetedProjectEvent.Type.PROJECT_NAME_CHANGED );
        nameChangeListener.handleEvent( null );
    }

    public IStatus validate()
    {
        return dm.validate();
    }

    public static final class Factory
    
        implements IAdapterFactory
        
    {
        private static final Class[] ADAPTER_TYPES = { ActionConfig.class };
        
        public Object getAdapter( final Object adaptable, 
                                  final Class adapterType )
        {
            if( adapterType == ActionConfig.class )
            {
                return new FacetDataModelConfigAdapter( (IDataModel) adaptable );
            }
            else
            {
                return null;
            }
        }

        public Class[] getAdapterList()
        {
            return ADAPTER_TYPES;
        }
    }

}
