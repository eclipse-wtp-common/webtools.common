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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.ui.internal.ConflictingFacetsFilter;
import org.eclipse.wst.common.project.facet.ui.internal.FacetsSelectionPage;
import org.eclipse.wst.common.project.facet.ui.internal.FacetsSelectionPanel;
import org.eclipse.wst.common.project.facet.ui.internal.FacetUiPlugin;
import org.eclipse.wst.common.project.facet.ui.internal.RuntimeBridge;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class AddRemoveFacetsWizard 

    extends Wizard 
    
{
    protected IProject project;
    
    private final WizardContext context = new WizardContext(); 
    protected FacetsSelectionPage facetsSelectionPage;
    private FacetPages[] facetPages = new FacetPages[ 0 ];
    private Composite pageContainer;
    
    public AddRemoveFacetsWizard( final IProject project )
    {
        // Temporary bridge.
        
        RuntimeBridge.port();

        this.project = project;
        
        setNeedsProgressMonitor( true );
        setForcePreviousAndNextButtons( true );
        setWindowTitle( "Add/Remove Project Facets" );
    }
    
    public void addPages()
    {
        this.facetsSelectionPage = new FacetsSelectionPage();
        
        if( this.project != null )
        {
            final IFacetedProject fproj
                = ProjectFacetsManager.get().create( this.project );
            
            this.facetsSelectionPage.setInitialSelection( fproj.getProjectFacets() );
            this.facetsSelectionPage.setFixedProjectFacets( fproj.getFixedProjectFacets());
            this.facetsSelectionPage.setFilters( new FacetsSelectionPanel.IFilter[] { new ConflictingFacetsFilter( fproj.getFixedProjectFacets() ) } );
            this.facetsSelectionPage.setRuntime( fproj.getRuntime() );
        }
        
        this.facetsSelectionPage.addSelectedFacetsChangedListener
        (
            new Listener()
            {
                public void handleEvent( final Event event ) 
                {
                    handleSelectedFacetsChangedEvent();
                }
            }
        );
        
        addPage( this.facetsSelectionPage );
    }
    
    public int getPageCount()
    {
        return getPages().length;
    }

    public IWizardPage[] getPages()
    {
        final ArrayList list = new ArrayList();
        
        list.add( this.facetsSelectionPage );
        
        for( int i = 0; i < this.facetPages.length; i++ )
        {
            list.addAll( this.facetPages[ i ].pages );
        }
        
        return (IWizardPage[]) list.toArray( new IWizardPage[ 0 ] );
    }
    
    public IWizardPage getPage( final String pageName )
    {
        final IWizardPage[] pages = getPages();
        
        for( int i = 0; i < pages.length; i++ )
        {
            final IWizardPage page = pages[ i ];
            
            if( page.getName().equals( pageName ) )
            {
                return page;
            }
        }
        
        return null;
    }
    
    public IWizardPage getStartingPage()
    {
        return getPages()[ 0 ];
    }
    
    public IWizardPage getNextPage( final IWizardPage page )
    {
        final IWizardPage[] pages = getPages();
        
        int pos = -1;
        
        for( int i = 0; i < pages.length; i++ )
        {
            if( pages[ i ] == page )
            {
                pos = i;
            }
        }
        
        if( pos == pages.length - 1 )
        {
            return null;
        }
        else
        {
            return pages[ pos + 1 ];
        }
    }

    public IWizardPage getPreviousPage( final IWizardPage page )
    {
        final IWizardPage[] pages = getPages();
        
        int pos = -1;
        
        for( int i = 0; i < pages.length; i++ )
        {
            if( pages[ i ] == page )
            {
                pos = i;
            }
        }
        
        if( pos == 0 )
        {
            return null;
        }
        else
        {
            return pages[ pos - 1 ];
        }
    }
    
    public boolean canFinish()
    {
        if( ! this.facetsSelectionPage.isPageComplete() )
        {
            return false;
        }
        
        final IWizardPage[] pages = getPages();
        
        for( int i = 0; i < pages.length; i++ )
        {
            if( ! pages[ i ].isPageComplete() )
            {
                return false;
            }
        }
        
        return true;
    }
    
    public void createPageControls( final Composite container )
    {
        super.createPageControls( container );
        this.pageContainer = container;
        handleSelectedFacetsChangedEvent();
    }
    
    public boolean performFinish() 
    {
        for( int i = 0; i < this.facetPages.length; i++ )
        {
            final FacetPages fp = this.facetPages[ i ];
            
            for( Iterator itr = fp.pages.iterator(); itr.hasNext(); )
            {
                ( (IFacetWizardPage) itr.next() ).transferStateToConfig();
            }
        }
        
        final Set actions = this.facetsSelectionPage.getActions();
        
        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() 
        {
            protected void execute( final IProgressMonitor monitor )
            
                throws CoreException 
                
            {
                monitor.beginTask( "", actions.size() );
                
                try
                {
                    performFinish( monitor );
                }
                finally
                {
                    monitor.done();
                }
            }
        };

        try 
        {
            getContainer().run( true, true, op );
        }
        catch( InterruptedException e ) 
        {
            return false;
        } 
        catch( InvocationTargetException e ) 
        {
            final Throwable te = e.getTargetException();
            
            if( te instanceof CoreException )
            {
                final CoreException ce = (CoreException) te;
                
                ErrorDialog.openError( getShell(), Resources.errDlgTitle,
                                       ce.getMessage(), ce.getStatus() );
                
                FacetUiPlugin.log( ce );
            }
            else
            {
                throw new RuntimeException( e.getTargetException() );
            }
        }
        
        return true;
    }

    protected void performFinish( final IProgressMonitor monitor )
    
        throws CoreException
        
    {
        monitor.beginTask( "", 2 );
        
        try
        {
            final IFacetedProject fopj 
                = ProjectFacetsManager.get().create( AddRemoveFacetsWizard.this.project );
            
            fopj.setRuntime( this.facetsSelectionPage.getSelectedRuntime(),
                             new SubProgressMonitor( monitor, 1 ) );
            
            fopj.modify( this.facetsSelectionPage.getActions(), 
                         new SubProgressMonitor( monitor, 1 ) );
        }
        finally
        {
            monitor.done();
        }
    }
    
    public String getProjectName()
    {
        return this.project.getName();
    }
    
    private static final class FacetPages
    {
        public Action action;
        public List pages;
    }
    
    private void handleSelectedFacetsChangedEvent()
    {
        // Get the set of actions and sort them.
        
        final Set base;
        
        if( this.project == null )
        {
            base = Collections.EMPTY_SET;
        }
        else
        {
            final IFacetedProject fproj
                = ProjectFacetsManager.get().create( this.project );
            
            base = fproj.getProjectFacets();
        }
        
        final Set actions = this.facetsSelectionPage.getActions();
        final ArrayList sortedActions = new ArrayList( actions );
        ProjectFacetsManager.get().sort( base, sortedActions );
        
        // Recalculate the sequence of wizard pages.
        
        final ArrayList newFacetPages = new ArrayList();
        final boolean[] markers = new boolean[ this.facetPages.length ];
        
        for( Iterator itr1 = sortedActions.iterator(); itr1.hasNext(); )
        {
            final Action action = (Action) itr1.next();
            final IProjectFacetVersion f = action.getProjectFacetVersion();
            FacetPages fp = findFacetPages( action, markers );
            
            if( fp == null )
            {
                fp = new FacetPages();
                fp.action = action;
                fp.pages = ProjectFacetsUiManager.get().getWizardPages( action.getType(), f );
                
                for( Iterator itr2 = fp.pages.iterator(); itr2.hasNext(); )
                {
                    final IFacetWizardPage page 
                        = (IFacetWizardPage) itr2.next();
                    
                    page.setWizard( this );
                    page.setWizardContext( this.context );
                    page.setConfig( action.getConfig() );
                    
                    if( page.getControl() == null ) 
                    {
                        page.createControl( this.pageContainer );
                        page.getControl().setVisible( false );
                    }
                }
            }
            
            newFacetPages.add( fp );
        }
        
        for( int i = 0; i < this.facetPages.length; i++ )
        {
            if( ! markers[ i ] )
            {
                for( Iterator itr = this.facetPages[ i ].pages.iterator();
                     itr.hasNext(); )
                {
                    final IFacetWizardPage page 
                        = (IFacetWizardPage) itr.next();
                    
                    page.setWizard( null );
                    page.dispose();
                }
            }
        }
        
        this.facetPages = new FacetPages[ newFacetPages.size() ];
        newFacetPages.toArray( this.facetPages );
        
        this.pageContainer.layout( true, true );
    }
    
    private FacetPages findFacetPages( final Action action,
                                           final boolean[] markers )
    {
        for( int i = 0; i < this.facetPages.length; i++ )
        {
            final FacetPages fp = this.facetPages[ i ];
            
            if( fp.action == action )
            {
                markers[ i ] = true;
                return fp;
            }
        }
        
        return null;
    }

    /**
     * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
     */

    private final class WizardContext

        implements IWizardContext
        
    {
        public String getProjectName()
        {
            return AddRemoveFacetsWizard.this.getProjectName();
        }

        public Set getSelectedProjectFacets()
        {
            return AddRemoveFacetsWizard.this.facetsSelectionPage.getSelectedProjectFacets();
        }

        public boolean isProjectFacetSelected( final IProjectFacetVersion fv )
        {
            return getSelectedProjectFacets().contains( fv );
        }

        public Set getActions()
        {
            final FacetsSelectionPage page
                = AddRemoveFacetsWizard.this.facetsSelectionPage;
            
            return page.getActions();
        }

        public Action getAction( final Action.Type type,
                                 final IProjectFacetVersion f )
        {
            for( Iterator itr = getActions().iterator(); itr.hasNext(); )
            {
                final Action action = (Action) itr.next();
                
                if( action.getType() == type && action.getProjectFacetVersion() == f )
                {
                    return action;
                }
            }
            
            return null;
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String errDlgTitle;
        
        static
        {
            initializeMessages( AddRemoveFacetsWizard.class.getName(), 
                                Resources.class );
        }
    }
    
}
