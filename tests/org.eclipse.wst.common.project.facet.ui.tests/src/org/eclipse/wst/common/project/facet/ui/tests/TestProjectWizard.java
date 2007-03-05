package org.eclipse.wst.common.project.facet.ui.tests;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.ui.FacetedProjectWizard;

public final class TestProjectWizard

    extends FacetedProjectWizard
    
{
    public TestProjectWizard()
    {
        setCategoryExpandedState( ProjectFacetsManager.getCategory( "ui.category" ), true );
    }
    
    @Override
    protected ImageDescriptor getDefaultPageImageDescriptor()
    {
        return null;
    }

    @Override
    protected String getPageDescription()
    {
        return "This wizard is used to test the Faceted Project Wizard.";
    }

    @Override
    protected IFacetedProjectTemplate getTemplate()
    {
        return ProjectFacetsManager.getTemplate( "ui.base.wizard.template" );
    }

}
