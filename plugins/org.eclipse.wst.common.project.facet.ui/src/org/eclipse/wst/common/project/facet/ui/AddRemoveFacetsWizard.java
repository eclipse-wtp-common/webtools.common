/******************************************************************************
 * Copyright (c) 2005 - 2006 BEA Systems, Inc.
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

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wst.common.project.facet.core.IActionDefinition;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action.Type;
import org.eclipse.wst.common.project.facet.ui.internal.AddRemoveFacetsDataModel;
import org.eclipse.wst.common.project.facet.ui.internal.ChangeTargetedRuntimesDataModel;
import org.eclipse.wst.common.project.facet.ui.internal.FacetUiPlugin;
import org.eclipse.wst.common.project.facet.ui.internal.FacetsSelectionPage;

/**
 * <p><i>This class is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being 
 * made available at this early stage to solicit feedback from pioneering 
 * adopters on the understanding that any code that uses this API will almost 
 * certainly be broken (repeatedly) as the API evolves.</i></p>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class AddRemoveFacetsWizard 

    extends Wizard 
    
{
    protected IFacetedProject fproj;
    
    private final WizardContext context = new WizardContext(); 
    protected final FacetsSelectionPage facetsSelectionPage;
    private FacetPages[] facetPages = new FacetPages[ 0 ];
    private Composite pageContainer;
    private final List pagesToDispose = new ArrayList();
    private final AddRemoveFacetsDataModel model;
    
    public AddRemoveFacetsWizard( final IFacetedProject fproj )
    {
        this.model = new AddRemoveFacetsDataModel();
        this.fproj = fproj;
        
        Set base = null;
        
        if( this.fproj != null )
        {
            base = this.fproj.getProjectFacets();
            
            final ChangeTargetedRuntimesDataModel rdm
                = this.model.getTargetedRuntimesDataModel();
            
            rdm.setTargetedRuntimes( this.fproj.getTargetedRuntimes() );
            rdm.setPrimaryRuntime( this.fproj.getPrimaryRuntime() );
        }
        
        this.facetsSelectionPage 
            = new FacetsSelectionPage( this.context, base, this.model );
        
        setNeedsProgressMonitor( true );
        setForcePreviousAndNextButtons( true );
        setWindowTitle( Resources.wizardTitle );
    }
    
    public final AddRemoveFacetsDataModel getModel()
    {
        return this.model;
    }
    
    public void addPages()
    {
        if( this.fproj != null )
        {
            this.facetsSelectionPage.setInitialSelection( this.fproj.getProjectFacets() );
            this.facetsSelectionPage.setFixedProjectFacets( this.fproj.getFixedProjectFacets());
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
        
        final IWorkspaceRunnable wr = new IWorkspaceRunnable()
        {
            public void run( final IProgressMonitor monitor ) 
            
                throws CoreException
                
            {
                performFinish( monitor );
            }
        };
        
        final IRunnableWithProgress op = new IRunnableWithProgress()
        {
            public void run( final IProgressMonitor monitor ) 
            
                throws InvocationTargetException, InterruptedException
                
            {
                try
                {
                    final IWorkspace ws = ResourcesPlugin.getWorkspace();
                    ws.run( wr, ws.getRoot(), IWorkspace.AVOID_UPDATE, monitor );
                }
                catch( CoreException e )
                {
                    throw new InvocationTargetException( e );
                }
            }
        };

        try 
        {
            getContainer().run( true, false, op );
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
                final IStatus st = ( (CoreException) te ).getStatus();
                
                ErrorDialog.openError( getShell(), Resources.errDlgTitle,
                                       st.getMessage(), st );
                
                FacetUiPlugin.log( st );
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
        monitor.beginTask( "", 3 ); //$NON-NLS-1$
        
        try
        {
            final ChangeTargetedRuntimesDataModel rdm
                = this.model.getTargetedRuntimesDataModel();
            
            this.fproj.setTargetedRuntimes( rdm.getTargetedRuntimes(), 
                                          submon( monitor, 1 ) );
            
            if( rdm.getPrimaryRuntime() != null )
            {
                this.fproj.setPrimaryRuntime( rdm.getPrimaryRuntime(), 
                                              submon( monitor, 1 ) );
            }
            
            this.fproj.modify( this.facetsSelectionPage.getActions(), 
                               submon( monitor, 1 ) );
        }
        finally
        {
            monitor.done();
        }
    }
    
    public String getProjectName()
    {
        return this.fproj.getProject().getName();
    }
    
	public Object getConfig(IProjectFacetVersion fv, Type type, String pjname) throws CoreException{
		return null;
	}
    
    public void syncWithPresetsModel( final Combo combo )
    {
        this.facetsSelectionPage.syncWithPresetsModel( combo );
    }
    
    public void dispose()
    {
        super.dispose();
        
        for( int i = 0; i < this.facetPages.length; i++ )
        {
            this.pagesToDispose.addAll( this.facetPages[ i ].pages );
        }
        
        for( Iterator itr = this.pagesToDispose.iterator(); itr.hasNext(); )
        {
            ( (IWizardPage) itr.next() ).dispose();
        }
        
        this.model.dispose();
    }
    
    private static IProgressMonitor submon( final IProgressMonitor parent,
                                            final int ticks )
    {
        return new SubProgressMonitor( parent, ticks );
    }
    
    private static final class FacetPages
    {
        public Action action;
        public List pages;
    }
    
    private void handleSelectedFacetsChangedEvent()
    {
        // Don't do anything until the facet selection page does not have any
        // errors.
        
        if( ! this.facetsSelectionPage.isPageComplete() )
        {
            return;
        }
        
        // Get the set of actions and sort them.
        
        final Set base;
        
        if( this.fproj == null )
        {
            base = Collections.EMPTY_SET;
        }
        else
        {
            base = this.fproj.getProjectFacets();
        }
        
        final Set actions = this.facetsSelectionPage.getActions();
        final ArrayList sortedActions = new ArrayList( actions );
        ProjectFacetsManager.sort( base, sortedActions );
        
        // Recalculate the sequence of wizard pages.
        
        final ArrayList newFacetPages = new ArrayList();
        final boolean[] markers = new boolean[ this.facetPages.length ];
        boolean changed = false;
        
        for( Iterator itr1 = sortedActions.iterator(); itr1.hasNext(); )
        {
            final Action action = (Action) itr1.next();
            final IProjectFacetVersion fv = action.getProjectFacetVersion();
            FacetPages fp = findFacetPages( action, markers );
            
            if( fp == null )
            {
                final IActionDefinition actiondef;
                
                try
                {
                    actiondef = fv.getActionDefinition( action.getType() );
                }
                catch( CoreException e )
                {
                    FacetUiPlugin.log( e );
                    continue;
                }
                
                final List pages
                    = ProjectFacetsUiManager.getWizardPages( actiondef.getId() );
                
                if( ! pages.isEmpty() )
                {
                    fp = new FacetPages();
                    fp.action = action;
                    fp.pages = pages;
                    
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
                    
                    changed = true;
                }
            }
            
            if( fp != null )
            {
                newFacetPages.add( fp );
            }
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
                    this.pagesToDispose.add( page );
                }
                
                changed = true;
            }
        }
        
        if( changed )
        {
            this.facetPages = new FacetPages[ newFacetPages.size() ];
            newFacetPages.toArray( this.facetPages );
            
            this.pageContainer.layout( true, true );
        }
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
        
		public Object getConfig(IProjectFacetVersion fv, Type type, String pjname) throws CoreException {
			Object config = AddRemoveFacetsWizard.this.getConfig(fv, type, pjname);
			if (null == config) {
				config = fv.createActionConfig(type, pjname);
			}
			return config;
		}
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String wizardTitle;
        public static String errDlgTitle;
        
        static
        {
            initializeMessages( AddRemoveFacetsWizard.class.getName(), 
                                Resources.class );
        }
    }
    
}
