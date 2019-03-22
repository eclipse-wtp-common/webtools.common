/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FacetedProjectNature

    implements IProjectNature

{
    public static final String NATURE_ID 
        = FacetCorePlugin.PLUGIN_ID + ".nature"; //$NON-NLS-1$
    
    private IProject project;
    
    public IProject getProject()
    {
        return this.project;
    }
    
    public void setProject( final IProject project )
    {
        this.project = project;
    }
    
    public void configure() 
    
        throws CoreException
        
    {
        final IProjectDescription desc = this.project.getDescription();
        
        final ICommand[] existing = desc.getBuildSpec();
        final ICommand[] cmds = new ICommand[ existing.length + 1 ];
        
        final ICommand newcmd = this.project.getDescription().newCommand();
        newcmd.setBuilderName( FacetedProjectValidationBuilder.BUILDER_ID );
        
        cmds[ 0 ] = newcmd;
        System.arraycopy( existing, 0, cmds, 1, existing.length );
        
        desc.setBuildSpec( cmds );
        this.project.setDescription( desc, null );
    }
    
    public void deconfigure() {}
    
}
