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
