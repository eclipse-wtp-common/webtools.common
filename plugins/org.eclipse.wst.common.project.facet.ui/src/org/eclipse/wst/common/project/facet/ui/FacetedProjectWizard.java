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

package org.eclipse.wst.common.project.facet.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class FacetedProjectWizard 

    extends ModifyFacetedProjectWizard 
    implements INewWizard
    
{
    private final IFacetedProjectTemplate template;
    private WizardNewProjectCreationPage firstPage;
    
    public FacetedProjectWizard()
    {
        this.template = getTemplate();
        
        final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
        fpjwc.setSelectedPreset( this.template.getInitialPreset().getId() );
        fpjwc.setFixedProjectFacets( this.template.getFixedProjectFacets() );
        
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
    
    @Override
    public IWizardPage getNextPage( final IWizardPage page )
    {
        if( page == this.firstPage )
        {
            final IFacetedProjectWorkingCopy fpjwc = getFacetedProjectWorkingCopy();
            fpjwc.setProjectName( this.firstPage.getProjectName() );
            fpjwc.setProjectLocation( this.firstPage.getLocationPath() );
        }
        
        return super.getNextPage( page );
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
