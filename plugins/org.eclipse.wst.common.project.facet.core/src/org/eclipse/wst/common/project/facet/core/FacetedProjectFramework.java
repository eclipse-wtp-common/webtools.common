package org.eclipse.wst.common.project.facet.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectNature;
import org.eclipse.wst.common.project.facet.core.internal.ProjectFacetsManagerImpl;

public final class FacetedProjectFramework
{
    public static final String PLUGIN_ID 
        = "org.eclipse.wst.common.project.facet.core"; //$NON-NLS-1$
    
    private static ProjectFacetsManagerImpl impl = null;
    
    private FacetedProjectFramework() { }
    
    /**
     * <p>Determines whether the specified project facet is installed in the
     * provided project. Returns <code>false</code> if the project is not 
     * accessible, the project is not faceted or the facet id is unrecognized.</p>
     * 
     * <p>This method is explicitly designed to avoid activation of the Faceted
     * Project Framework if the project is not faceted. For the code that
     * operates in the context where it can be assumed that the framework has
     * started already, better performance can be achieved by storing 
     * {@see IProjectFacet} and {@see IProjectFacetVersion} instances using the
     * singleton pattern and using the
     * {@see IFacetedProject.hasProjectFacet(IProjectFacet)} or
     * {@see IFacetedProject.hasProjectFacet(IProjectFacetVersion)} methods.</p>
     * 
     * <p>This method is equivalent to calling 
     * {@see hasProjectFacet(IProject,String,String)} with <code>null</code>
     * version expression parameter.</p>
     * 
     * @param project the project to check for the facet presence
     * @param fid the project facet id
     * @throws CoreException if failed while reading faceted project metadata
     */

    public static boolean hasProjectFacet( final IProject project,
                                           final String fid )
    
        throws CoreException
        
    {
        return hasProjectFacet( project, fid, null );
    }
    
    /**
     * <p>Determines whether the specified project facet is installed in the
     * provided project. Returns <code>false</code> if the project is not 
     * accessible, the project is not faceted or the facet id is unrecognized.</p>
     * 
     * <p>This method is explicitly designed to avoid activation of the Faceted
     * Project Framework if the project is not faceted. For the code that
     * operates in the context where it can be assumed that the framework has
     * started already, better performance can be achieved by storing 
     * {@see IProjectFacet} and {@see IProjectFacetVersion} instances using the
     * singleton pattern and using the
     * {@see IFacetedProject.hasProjectFacet(IProjectFacet)} or
     * {@see IFacetedProject.hasProjectFacet(IProjectFacetVersion)} methods.</p>
     * 
     * @param project the project to check for the facet presence
     * @param fid the project facet id
     * @param vexpr the version match expression, or <code>null</code> to
     *   match any version
     * @throws CoreException if failed while reading faceted project metadata;
     *   if the version expression is invalid
     */

    public static boolean hasProjectFacet( final IProject project,
                                           final String fid,
                                           final String vexpr )
    
        throws CoreException
        
    {
        if( project.isAccessible() &&
            project.isNatureEnabled( FacetedProjectNature.NATURE_ID ) )
        {
            initialize();
            
            final IFacetedProject fproj = ProjectFacetsManager.create( project );
            
            if( fproj != null )
            {
                if( ProjectFacetsManager.isProjectFacetDefined( fid ) )
                {
                    final IProjectFacet f = ProjectFacetsManager.getProjectFacet( fid );
                    
                    if( vexpr == null )
                    {
                        return fproj.hasProjectFacet( f );
                    }
                    else
                    {
                        final IProjectFacetVersion fv = fproj.getInstalledVersion( f );
                        
                        if( fv != null )
                        {
                            return f.getVersions( vexpr ).contains( fv );
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    private static synchronized void initialize()
    {
        if( impl == null )
        {
            impl = new ProjectFacetsManagerImpl();
        }
    }
    
    static ProjectFacetsManagerImpl getProjectFacetsManagerImpl()
    {
        initialize();
        return impl;
    }

}
