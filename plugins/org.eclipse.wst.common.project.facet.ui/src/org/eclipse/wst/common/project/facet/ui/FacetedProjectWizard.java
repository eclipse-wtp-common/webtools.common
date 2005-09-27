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

package org.eclipse.wst.common.project.facet.ui;

import java.util.Set;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.ui.internal.ConflictingFacetsFilter;
import org.eclipse.wst.common.project.facet.ui.internal.FacetsSelectionPanel;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public abstract class FacetedProjectWizard 

    extends AddRemoveFacetsWizard 
    implements INewWizard
    
{
    private static final String FACETED_PROJECT_NATURE
        = "org.eclipse.wst.common.project.facet.core.nature";
    
    private WizardNewProjectCreationPage firstPage;
    private IPath customPath;
    
    public FacetedProjectWizard()
    {
        super( null );
        
        this.setWindowTitle( getWindowTitleText() );
        this.setDefaultPageImageDescriptor( getDefaultPageImageDescriptor() );
    }
    
    public void init( final IWorkbench workbench, 
                      final IStructuredSelection selection )
    {
        
    }
    
    public void addPages()
    {
        this.firstPage = new WizardNewProjectCreationPage( "first.page" );
        this.firstPage.setTitle( getPageTitle() );
        this.firstPage.setDescription( getPageDescription() );
    
        addPage( this.firstPage );
        
        super.addPages();
        
        this.facetsSelectionPage.setInitialPreset( getInitialPreset() );
        
        final Set fixed = getFixedProjectFacets();
        
        this.facetsSelectionPage.setFixedProjectFacets( fixed );
        
        final ConflictingFacetsFilter filter 
            = new ConflictingFacetsFilter( fixed );
        
        this.facetsSelectionPage.setFilters( new FacetsSelectionPanel.IFilter[] { filter } );
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
        this.project = this.firstPage.getProjectHandle();

        this.customPath
            = this.firstPage.useDefaults() 
              ? null : this.firstPage.getLocationPath();
        
        return super.performFinish();
    }
    
    protected void performFinish( final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        final IWorkspace ws = ResourcesPlugin.getWorkspace();
        
        final IProjectDescription desc
            = ws.newProjectDescription( this.project.getName() );

        desc.setLocation( this.customPath );
        desc.setNatureIds( new String[] { FACETED_PROJECT_NATURE } );
                
        this.project.create( desc, new SubProgressMonitor( monitor, 1 ) );
                    
        this.project.open( IResource.BACKGROUND_REFRESH,
                           new SubProgressMonitor( monitor, 1 ) );
        
        super.performFinish( monitor );
        
        final IFacetedProject fproj
            = ProjectFacetsManager.get().create( this.project );
        
        fproj.setFixedProjectFacets( getFixedProjectFacets() );
    }
    
    public synchronized String getProjectName()
    {
        if( this.project == null )
        {
            return this.firstPage.getProjectName();
        }
        else
        {
            return this.project.getName();
        }
    }
    
    protected abstract String getWindowTitleText();
    protected abstract String getPageTitle();
    protected abstract String getPageDescription();
    protected abstract ImageDescriptor getDefaultPageImageDescriptor();
    protected abstract IPreset getInitialPreset();
    protected abstract Set getFixedProjectFacets();

}
