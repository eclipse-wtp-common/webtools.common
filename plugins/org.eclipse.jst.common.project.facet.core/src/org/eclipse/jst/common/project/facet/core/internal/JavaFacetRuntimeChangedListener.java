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

package org.eclipse.jst.common.project.facet.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JavaFacetRuntimeChangedListener 

    implements IFacetedProjectListener
    
{
    public void handleEvent( final IFacetedProjectEvent event )
    {
        final IFacetedProject fproj = event.getProject();
        
        if( fproj.hasProjectFacet( JavaFacet.FACET ) )
        {
            final IProjectFacetVersion fv = fproj.getInstalledVersion( JavaFacet.FACET );
            
            try
            {
                JavaFacetUtil.resetClasspath( fproj.getProject(), fv, fv );
            }
            catch( CoreException e )
            {
                FacetedProjectFrameworkJavaPlugin.log( e );
            }
        }
    }

}
