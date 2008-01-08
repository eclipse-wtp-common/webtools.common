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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
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
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IActionDefinition;
import org.eclipse.wst.common.project.facet.core.ICategory;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.ui.internal.FacetUiPlugin;
import org.eclipse.wst.common.project.facet.ui.internal.FacetsSelectionPage;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class ModifyFacetedProjectWizard 

    extends Wizard 
    
{
    private IFacetedProjectWorkingCopy fpjwc;
    private final WizardContext context = new WizardContext(); 
    private boolean showFacetsSelectionPage;
    private FacetsSelectionPage facetsSelectionPage;
    private FacetPages[] facetPages = new FacetPages[ 0 ];
    private Composite pageContainer;
    private final List<IWizardPage> pagesToDispose = new ArrayList<IWizardPage>();
    private final List<Runnable> delayedActions;
    
    public ModifyFacetedProjectWizard()
    {
        this( (IFacetedProject) null );
    }
    
    public ModifyFacetedProjectWizard( final IFacetedProject fproj )
    {
        this( fproj == null
              ? FacetedProjectFramework.createNewProject() 
              : fproj.createWorkingCopy() );
    }

    public ModifyFacetedProjectWizard( final IFacetedProjectWorkingCopy fpjwc )
    {
        try
        {
            this.fpjwc = fpjwc;
            this.delayedActions = new ArrayList<Runnable>();;
            this.facetsSelectionPage = null;
            this.showFacetsSelectionPage = true;
            
            setNeedsProgressMonitor( true );
            setForcePreviousAndNextButtons( true );
            setWindowTitle( Resources.wizardTitle );
        }
        catch( RuntimeException e )
        {
            e.printStackTrace();
            throw e;
        }
    }
    
    public final IFacetedProjectWorkingCopy getFacetedProjectWorkingCopy()
    {
        return this.fpjwc;
    }
    
    public void setFacetedProjectWorkingCopy( final IFacetedProjectWorkingCopy fpjwc )
    {
        this.fpjwc = fpjwc;
    }
    
    public final IFacetedProject getFacetedProject()
    {
        IFacetedProject fproj = this.fpjwc.getFacetedProject();
        
        if( fproj == null )
        {
            final String projectName = this.fpjwc.getProjectName();
            
            if( projectName != null )
            {
                final IProject proj 
                    = ResourcesPlugin.getWorkspace().getRoot().getProject( projectName );
                
                try
                {
                    fproj = ProjectFacetsManager.create( proj );
                }
                catch( CoreException e )
                {
                    FacetUiPlugin.log( e );
                }
            }
        }
        
        return fproj;
    }
    
    public void addPages()
    {
        this.fpjwc.addListener
        (
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event ) 
                {
                    final Runnable runnable = new Runnable()
                    {
                        public void run()
                        {
                            handleSelectedFacetsChangedEvent();
                        }
                    };
                    
                    Display.getDefault().syncExec( runnable );
                }
            },
            IFacetedProjectEvent.Type.PROJECT_FACETS_CHANGED
        );
        
        if( this.showFacetsSelectionPage )
        {
            this.facetsSelectionPage = new FacetsSelectionPage( getBaseFacets(), this.fpjwc );
            addPage( this.facetsSelectionPage );
        }
    }
    
    public int getPageCount()
    {
        return getPages().length;
    }

    public IWizardPage[] getPages()
    {
        final List<IWizardPage> list = new ArrayList<IWizardPage>();
        
        if( this.facetsSelectionPage != null )
        {
            list.add( this.facetsSelectionPage );
        }
        
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
        if( this.facetsSelectionPage != null && ! this.facetsSelectionPage.isPageComplete() )
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
        this.fpjwc.commitChanges( monitor );
    }
    
    public void syncWithPresetsModel( final Combo combo )
    {
        syncWithPresetsModel( this.fpjwc, combo );
    }
	
	public static void syncWithPresetsModel( final IFacetedProjectWorkingCopy fpjwc,
	                                         final Combo combo )
	{
        final List<IPreset> sortedPresets = new ArrayList<IPreset>();
        
        // Contents : model -> view

        final IFacetedProjectListener availablePresetsChangedListener = new IFacetedProjectListener()
        {
            public void handleEvent( final IFacetedProjectEvent event )
            {
                final Runnable runnable = new Runnable()
                {
                    public void run()
                    {
                        synchronized( sortedPresets )
                        {
                            sortedPresets.clear();
                            sortedPresets.addAll( fpjwc.getAvailablePresets() );
                            
                            Collections.sort
                            (
                                sortedPresets,
                                new Comparator<IPreset>()
                                {
                                    public int compare( final IPreset p1, 
                                                        final IPreset p2 ) 
                                    {
                                        if( p1 == p2 )
                                        {
                                            return 0;
                                        }
                                        else
                                        {
                                            return p1.getLabel().compareTo( p2.getLabel() );
                                        }
                                    }
                                }
                            );
                            
                            final IPreset selectedPreset = fpjwc.getSelectedPreset();
                            
                            combo.removeAll();
                            combo.add( Resources.customPreset );
                            
                            if( selectedPreset == null )
                            {
                                combo.select( 0 );
                            }
                            
                            for( IPreset preset : sortedPresets )
                            {
                                combo.add( preset.getLabel() );
                                
                                if( selectedPreset != null && 
                                    preset.getId().equals( selectedPreset.getId() ) )
                                {
                                    combo.select( combo.getItemCount() - 1 );
                                }
                            }
                        }
                    }
                };
                
                Display.getDefault().syncExec( runnable );
            }
        };
        
        fpjwc.addListener( availablePresetsChangedListener, 
                           IFacetedProjectEvent.Type.AVAILABLE_PRESETS_CHANGED );
        
        final IFacetedProjectListener selectedPresetChangedListener = new IFacetedProjectListener()
        {
            public void handleEvent( final IFacetedProjectEvent event )
            {
                final Runnable runnable = new Runnable()
                {
                    public void run()
                    {
                        synchronized( sortedPresets )
                        {
                            final IPreset preset = fpjwc.getSelectedPreset();
                            final int index;
                            
                            if( preset == null )
                            {
                                index = -1;
                            }
                            else
                            {
                                index = sortedPresets.indexOf( preset );
                            }
                            
                            combo.select( index + 1 );
                        }
                    }
                };
                
                Display.getDefault().syncExec( runnable );
            }
        };
        
        fpjwc.addListener( selectedPresetChangedListener,
                           IFacetedProjectEvent.Type.SELECTED_PRESET_CHANGED );
        
        // Selection : view -> model
        
        combo.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    synchronized( sortedPresets )
                    {
                        final int selection = combo.getSelectionIndex();
                        final String presetId;
                        
                        if( selection == 0 )
                        {
                            presetId = null;
                        }
                        else
                        {
                            presetId = sortedPresets.get( selection - 1 ).getId();
                        }
                        
                        fpjwc.setSelectedPreset( presetId );
                    }
                }
            }
        );
        
        // Trigger initial UI population.
        
        availablePresetsChangedListener.handleEvent( null );
        
        // Make sure to remove working copy listeners when the widget is disposed.
        
        combo.addDisposeListener
        (
            new DisposeListener()
            {
                public void widgetDisposed( final DisposeEvent event )
                {
                    fpjwc.removeListener( availablePresetsChangedListener );
                    fpjwc.removeListener( selectedPresetChangedListener );
                }
            }
        );
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
    
    public boolean getShowFacetsSelectionPage()
    {
        return this.showFacetsSelectionPage;
    }
    
    public void setShowFacetsSelectionPage( final boolean showFacetsSelectionPage )
    {
        this.showFacetsSelectionPage = showFacetsSelectionPage;
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
        
        this.fpjwc.dispose();
    }
    
    private static final class FacetPages
    {
        public Action action;
        public List<IFacetWizardPage> pages;
    }
    
    private void handleSelectedFacetsChangedEvent()
    {
        // Don't do anything until there are no more validation errors.
        
        if( this.fpjwc.validate().getSeverity() == IStatus.ERROR)
        {
            return;
        }
        
        // Get the set of actions and sort them.
        
        final Set<IProjectFacetVersion> base = getBaseFacets();
        final Set<Action> actions = getFacetedProjectWorkingCopy().getProjectFacetActions();
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
        final IFacetedProject fproj = this.fpjwc.getFacetedProject();
        
        if( fproj == null )
        {
            return Collections.emptySet();
        }
        else
        {
            return fproj.getProjectFacets();
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
            return getFacetedProjectWorkingCopy().getProjectName();
        }

        public Set<IProjectFacetVersion> getSelectedProjectFacets()
        {
            return getFacetedProjectWorkingCopy().getProjectFacets();
        }

        public boolean isProjectFacetSelected( final IProjectFacetVersion fv )
        {
            return getSelectedProjectFacets().contains( fv );
        }

        public Set<Action> getActions()
        {
            return getFacetedProjectWorkingCopy().getProjectFacetActions();
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
        
        public Object getConfig( final IProjectFacetVersion fv, 
                                 final Action.Type type, 
                                 final String pjname )
        {
            final Action action = getAction( type, fv );
            return action.getConfig();
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String wizardTitle;
        public static String errDlgTitle;
        public static String customPreset;
        
        static
        {
            initializeMessages( ModifyFacetedProjectWizard.class.getName(), 
                                Resources.class );
        }
    }
    
}
