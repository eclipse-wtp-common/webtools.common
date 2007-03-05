/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public abstract class FacetedProjectWizard 

    extends ModifyFacetedProjectWizard 
    implements INewWizard
    
{
    private final IFacetedProjectTemplate template;
    private WizardNewProjectCreationPage firstPage;
    private String projectName;
    private IPath customPath;
    
    public FacetedProjectWizard()
    {
        super( null );
        
        this.template = getTemplate();
        
        this.setWindowTitle( Resources.newPrefix + this.template.getLabel() );
        
        final ImageDescriptor defImageDescriptor = getDefaultPageImageDescriptor();
        
        if( defImageDescriptor != null )
        {
            this.setDefaultPageImageDescriptor( defImageDescriptor );
        }
    }
    
    public void init( final IWorkbench workbench, 
                      final IStructuredSelection selection )
    {
        
    }
    
    public void addPages()
    {
        this.firstPage = new WizardNewProjectCreationPage( "first.page" ); //$NON-NLS-1$
        this.firstPage.setTitle( this.template.getLabel() );
        this.firstPage.setDescription( getPageDescription() );
    
        addPage( this.firstPage );
        
        super.addPages();
        
        this.facetsSelectionPage.setInitialPreset( this.template.getInitialPreset() );
        
        final Set<IProjectFacet> fixed = this.template.getFixedProjectFacets();
        this.facetsSelectionPage.setFixedProjectFacets( fixed );
    }
    
    public boolean canFinish()
    {
        return this.firstPage.isPageComplete() && super.canFinish();
    }
    
    public IWizardPage[] getPages()
    {
        final IWizardPage[] base = super.getPages();
        final IWizardPage[] pages = new IWizardPage[ base.length + 1 ];
        
        pages[ 0 ] = this.firstPage;
        System.arraycopy( base, 0, pages, 1, base.length );
        
        return pages;
    }
    
    public synchronized boolean performFinish() 
    {
        this.projectName = this.firstPage.getProjectName();

        this.customPath
            = this.firstPage.useDefaults() 
              ? null : this.firstPage.getLocationPath();
        
        return super.performFinish();
    }
    
    protected void performFinish( final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        this.fproj 
            = ProjectFacetsManager.create( this.projectName,
                                           this.customPath, monitor );
        
        super.performFinish( monitor );
        
        final Set<IProjectFacet> fixed = this.template.getFixedProjectFacets();
        this.fproj.setFixedProjectFacets( fixed );
    }
    
    public synchronized String getProjectName()
    {
        if( this.fproj == null )
        {
            return this.firstPage.getProjectName();
        }
        else
        {
            return this.fproj.getProject().getName();
        }
    }
    
    protected abstract IFacetedProjectTemplate getTemplate();
    protected abstract String getPageDescription();
    protected abstract ImageDescriptor getDefaultPageImageDescriptor();

    private static final class Resources
    
        extends NLS
        
    {
        public static String newPrefix;
        
        static
        {
            initializeMessages( FacetedProjectWizard.class.getName(), 
                                Resources.class );
        }
    }
    
}
