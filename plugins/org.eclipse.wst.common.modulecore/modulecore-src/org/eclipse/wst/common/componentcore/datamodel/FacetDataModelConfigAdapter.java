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
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IActionConfig;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetDataModelConfigAdapter

    implements IActionConfig
    
{
    private final IDataModel dm;
    
    public FacetDataModelConfigAdapter( final IDataModel dm )
    {
        this.dm = dm;
    }
    
    public void setVersion( final IProjectFacetVersion fv )
    {
        dm.setProperty( IFacetDataModelProperties.FACET_VERSION, fv );
    }

    public void setProjectName( final String pjname )
    {
        dm.setStringProperty( IFacetDataModelProperties.FACET_PROJECT_NAME, pjname );
    }

    public IStatus validate()
    {
        return Status.OK_STATUS;
    }

    public static final class Factory
    
        implements IAdapterFactory
        
    {
        private static final Class[] ADAPTER_TYPES
            = { IActionConfig.class };
        
        public Object getAdapter( final Object adaptable, 
                                  final Class adapterType )
        {
            if( adapterType == IActionConfig.class )
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
