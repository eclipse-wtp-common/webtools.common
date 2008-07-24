/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;

/**
 * A basic wizard for creating faceted projects. This wizard is available directly
 * to the users as "Basic/Faceted Project" in the new project dialog, but can also
 * be subclassed.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class BasicFacetedProjectWizard 

    extends ModifyFacetedProjectWizard 
    implements INewWizard
    
{
    private IWizardPage firstPage;
    
    public BasicFacetedProjectWizard()
    {
        this.setWindowTitle( Resources.wizardTitle );
    }
    
    /**
     * Returns the wizard's first page.
     *  
     * @return the wizard's first page
     */
    
    public IWizardPage getFirstPage()
    {
    	return this.firstPage;
    }
    
    /**
     * Creates the first wizard page. Typically, this is where the user specifies the
     * project name and location. The default implementation users a basic first page
     * provided by the Eclipse Platform. Extenders can override this method in order 
     * to supply a custom page. Anyone overriding this page will probably also need to
     * override the {@link #getProjectName()} method.
     * 
     * @return the first page that will be used by this wizard
     */
    
    protected IWizardPage createFirstPage()
    {
        final IWizardPage firstPage = new WizardNewProjectCreationPage( "first.page" ); //$NON-NLS-1$
        firstPage.setTitle( Resources.wizardTitle );
        firstPage.setDescription( Resources.firstPageDescription );
        
        return firstPage;
    }
    
    /**
     * Used by the wizard to retrieve the project name that the user specifies on the
     * first page of the wizard. The default implementation works with any subclass of
     * the {@link WizardNewProjectCreationPage} class. Extenders who override the
     * {@link #createFirstPage()} method will probably also need to override this method.
     * 
     * @return the project name specified by the user
     */
    
    protected String getProjectName()
    {
    	return ( (WizardNewProjectCreationPage) getFirstPage() ).getProjectName();
    }
    
    public void addPages()
    {
        this.firstPage = createFirstPage();
        addPage( this.firstPage );
        super.addPages();
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
            fpjwc.setProjectName( getProjectName() );
        }
        
        return super.getNextPage( page );
    }

    public boolean canFinish()
    {
        return this.firstPage.isPageComplete() && super.canFinish();
    }
    
    public void init( final IWorkbench workbench, 
                      final IStructuredSelection selection )
    {
        
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String wizardTitle;
        public static String firstPageDescription;
        
        static
        {
            initializeMessages( BasicFacetedProjectWizard.class.getName(), 
                                Resources.class );
        }
    }
    
}
