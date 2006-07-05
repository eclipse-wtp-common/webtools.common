package org.eclipse.wst.common.project.facet.core.tests.support;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

public final class Facet3aInstallDelegate

    implements IDelegate
    
{
    public void execute( final IProject project, 
                         final IProjectFacetVersion fv, 
                         final Object config, 
                         final IProgressMonitor monitor ) 
    
        throws CoreException
        
    {
        final IFile file = project.getFile( "facet3a.txt" );
        TestUtils.writeToFile( file, fv.getVersionString() );
    }
    
}
