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
import org.eclipse.wst.common.project.facet.core.IDefaultVersionProvider;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * Defaults the java facet version to align with workspace java compiler
 * level settings.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JavaFacetDefaultVersionProvider

    implements IDefaultVersionProvider
    
{
    public IProjectFacetVersion getDefaultVersion()
    {
        final String compilerLevel = JavaFacetUtil.getCompilerLevel();
        
        if( JavaFacet.FACET.hasVersion( compilerLevel ) )
        {
            return JavaFacet.FACET.getVersion( compilerLevel );
        }
        else
        {
            try
            {
                return JavaFacet.FACET.getLatestVersion();
            }
            catch( CoreException e )
            {
                // Not expected for this facet.
                
                throw new RuntimeException( e );
            }
        }
    }
}
