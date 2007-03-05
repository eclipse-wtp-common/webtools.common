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

import static org.eclipse.wst.common.project.facet.core.internal.util.ProgressMonitorUtil.beginTask;
import static org.eclipse.wst.common.project.facet.core.internal.util.ProgressMonitorUtil.done;
import static org.eclipse.wst.common.project.facet.core.internal.util.ProgressMonitorUtil.subTask;
import static org.eclipse.wst.common.project.facet.core.internal.util.ProgressMonitorUtil.submon;
import static org.eclipse.wst.common.project.facet.core.internal.util.ProgressMonitorUtil.worked;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action.Type;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.ui.internal.ChangeTargetedRuntimesDataModel;
import org.eclipse.wst.common.project.facet.ui.internal.FacetUiPlugin;
import org.eclipse.wst.common.project.facet.ui.internal.FacetsSelectionPage;
import org.eclipse.wst.common.project.facet.ui.internal.ModifyFacetedProjectDataModel;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class ModifyFacetedProjectWizard 

    extends Wizard 
    
{
    protected IFacetedProject fproj;
    
    private final WizardContext context = new WizardContext(); 
    protected final FacetsSelectionPage facetsSelectionPage;
    private FacetPages[] facetPages = new FacetPages[ 0 ];
    private Composite pageContainer;
    private final List<IWizardPage> pagesToDispose = new ArrayList<IWizardPage>();
    private final ModifyFacetedProjectDataModel model;
    private final List<Runnable> delayedActions;
    
    public ModifyFacetedProjectWizard( final IFacetedProject fproj )
    {
        this.model = new ModifyFacetedProjectDataModel();
        this.delayedActions = new ArrayList<Runnable>();;
        this.fproj = fproj;
        
        Set<IProjectFacetVersion> base = null;
        
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
    
    public final ModifyFacetedProjectDataModel getModel()
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
        final List<IWizardPage> list = new ArrayList<IWizardPage>();
        
        list.add( this.facetsSelectionPage );
        
        for( int i = 0; i < this.facetPages.length; i++ )
        {
            list.addAll( this.facetPages[ i ].pages );
        }
        
        return list.toArray( new IWizardPage[ list.size() ] );
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
        executeDelayedActions();
    }
    
    public boolean performFinish() 
    {
        for( int i = 0; i < this.facetPages.length; i++ )
        {
            for( IFacetWizardPage fp : this.facetPages[ i ].pages )
            {
                fp.transferStateToConfig();
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
        beginTask( monitor, "", 20 ); //$NON-NLS-1$
        
        try
        {
            // Figure out whether we can set runtimes before applying facet actions. This is better
            // for performance reasons, but may not work if the project contains facets that are
            // not supported by the new runtime. You can get into this situation if the user tries
            // to simultaneously uninstall a facet and select a different runtime. The fallback
            // solution for this situation is to set the targeted runtimes to an empty list first,
            // then apply the facet actions, and then set the targeted runtimes to the new list.
            // This is more drastic than necessary in all situations, but it is not clear that
            // implementing additional optimizations is necessary either.
            
            final ChangeTargetedRuntimesDataModel rdm = this.model.getTargetedRuntimesDataModel();
            boolean canSetRuntimesFirst = true;
            
            for( IProjectFacetVersion fv : this.fproj.getProjectFacets() )
            {
                for( IRuntime r : rdm.getTargetedRuntimes() )
                {
                    if( ! r.supports( fv ) )
                    {
                        canSetRuntimesFirst = false;
                        break;
                    }
                }
                
                if( ! canSetRuntimesFirst )
                {
                    break;
                }
            }
            
            subTask( monitor, Resources.taskConfiguringRuntimes );
            
            if( canSetRuntimesFirst )
            {
                this.fproj.setTargetedRuntimes( rdm.getTargetedRuntimes(), submon( monitor, 2 ) );
                
                if( rdm.getPrimaryRuntime() != null )
                {
                    this.fproj.setPrimaryRuntime( rdm.getPrimaryRuntime(), submon( monitor, 2 ) );
                }
                else
                {
                    worked( monitor, 2 );
                }
            }
            else
            {
                final Set<IRuntime> emptySet = Collections.emptySet();
                this.fproj.setTargetedRuntimes( emptySet, submon( monitor, 2 ) );
            }
            
            this.fproj.modify( this.facetsSelectionPage.getActions(), submon( monitor, 16 ) );
            
            if( ! canSetRuntimesFirst )
            {
                subTask( monitor, Resources.taskConfiguringRuntimes );
                
                this.fproj.setTargetedRuntimes( rdm.getTargetedRuntimes(), submon( monitor, 1 ) );
                
                if( rdm.getPrimaryRuntime() != null )
                {
                    this.fproj.setPrimaryRuntime( rdm.getPrimaryRuntime(), submon( monitor, 1 ) );
                }
                else
                {
                    worked( monitor, 1 );
                }
            }
        }
        finally
        {
            done( monitor );
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
    
    public void setCategoryExpandedState( final ICategory category,
                                          final boolean expanded )
    {
        if( this.facetsSelectionPage != null && this.facetsSelectionPage.panel != null )
        {
            this.facetsSelectionPage.panel.setCategoryExpandedState( category, true );
        }
        else
        {
            final Runnable action = new Runnable()
            {
                public void run()
                {
                    setCategoryExpandedState( category, expanded );
                }
            };
            
            this.delayedActions.add( action );
        }
    }
    
    public void dispose()
    {
        super.dispose();
        
        for( int i = 0; i < this.facetPages.length; i++ )
        {
            this.pagesToDispose.addAll( this.facetPages[ i ].pages );
        }
        
        for( IWizardPage page : this.pagesToDispose )
        {
            page.dispose();
        }
        
        this.model.dispose();
    }
    
    private static final class FacetPages
    {
        public Action action;
        public List<IFacetWizardPage> pages;
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
        
        final Set<IProjectFacetVersion> base = getBaseFacets();
        final Set<Action> actions = this.facetsSelectionPage.getActions();
        final List<Action> sortedActions = new ArrayList<Action>( actions );
        ProjectFacetsManager.sort( base, sortedActions );
        
        // Recalculate the sequence of wizard pages.
        
        final List<FacetPages> newFacetPages = new ArrayList<FacetPages>();
        final boolean[] markers = new boolean[ this.facetPages.length ];
        boolean changed = false;
        
        for( Action action : sortedActions )
        {
            final IProjectFacetVersion fv = action.getProjectFacetVersion();
            FacetPages fp = findFacetPages( action, markers );
            
            if( fp == null )
            {
                final IActionDefinition actiondef;
                
                try
                {
                    actiondef = fv.getActionDefinition( base, action.getType() );
                }
                catch( CoreException e )
                {
                    FacetUiPlugin.log( e );
                    continue;
                }
                
                final List<IFacetWizardPage> pages
                    = ProjectFacetsUiManager.getWizardPages( actiondef.getId() );
                
                if( ! pages.isEmpty() )
                {
                    fp = new FacetPages();
                    fp.action = action;
                    fp.pages = pages;
                    
                    for( IFacetWizardPage page : fp.pages )
                    {
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
                for( IFacetWizardPage page : this.facetPages[ i ].pages )
                {
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
    
    private Set<IProjectFacetVersion> getBaseFacets()
    {
        if( this.fproj == null )
        {
            return Collections.emptySet();
        }
        else
        {
            return this.fproj.getProjectFacets();
        }
    }
    
    private void executeDelayedActions()
    {
        for( Runnable r : this.delayedActions )
        {
            try
            {
                r.run();
            }
            catch( Exception e )
            {
                FacetUiPlugin.log( e );
            }
        }
    }

    /**
     * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
     */

    private final class WizardContext

        implements IWizardContext
        
    {
        public String getProjectName()
        {
            return ModifyFacetedProjectWizard.this.getProjectName();
        }

        public Set<IProjectFacetVersion> getSelectedProjectFacets()
        {
            return ModifyFacetedProjectWizard.this.facetsSelectionPage.getSelectedProjectFacets();
        }

        public boolean isProjectFacetSelected( final IProjectFacetVersion fv )
        {
            return getSelectedProjectFacets().contains( fv );
        }

        public Set<Action> getActions()
        {
            final FacetsSelectionPage page
                = ModifyFacetedProjectWizard.this.facetsSelectionPage;
            
            return page.getActions();
        }

        public Action getAction( final Action.Type type,
                                 final IProjectFacetVersion f )
        {
            for( Action action : getActions() )
            {
                if( action.getType() == type && action.getProjectFacetVersion() == f )
                {
                    return action;
                }
            }
            
            return null;
        }
        
		public Object getConfig(IProjectFacetVersion fv, Type type, String pjname) throws CoreException {
			Object config = ModifyFacetedProjectWizard.this.getConfig(fv, type, pjname);
			if (null == config) {
                final Set<IProjectFacetVersion> base = getBaseFacets();
                final IActionDefinition def = fv.getActionDefinition( base, type );
                config = def.createConfigObject( fv, pjname );
			}
			return config;
		}
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String wizardTitle;
        public static String errDlgTitle;
        public static String taskConfiguringRuntimes;
        
        static
        {
            initializeMessages( ModifyFacetedProjectWizard.class.getName(), 
                                Resources.class );
        }
    }
    
}
