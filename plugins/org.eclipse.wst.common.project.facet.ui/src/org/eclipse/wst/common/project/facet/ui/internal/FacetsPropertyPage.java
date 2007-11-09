/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Karl Lum - initial implementation
 *    Konstantin Komissarchik - ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.ui.ModifyFacetedProjectWizard;

/**
 * @author <a href="mailto:klum@bea.com">Karl Lum</a>
 */

public class FacetsPropertyPage extends PropertyPage 
{
    private IFacetedProject project;
    private IFacetedProjectListener projectListener;
    private TableViewer viewer;
    
    protected Control createContents(Composite parent) 
    {
        noDefaultAndApplyButton();
        final IAdaptable element = getElement();

        if (element instanceof IProject)
        {
            final IProject project = (IProject)element;
            try {
                this.project = ProjectFacetsManager.create(project);
            }
            catch (CoreException ce)
            {
                return null;
            }
            
            this.projectListener = new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    handleProjectChangedEvent();
                }
            };
            
            this.project.addListener( this.projectListener,
                                      IFacetedProjectEvent.Type.PROJECT_MODIFIED );
            
            Composite composite = new Composite(parent, SWT.NONE);
            
            final GridLayout layout = new GridLayout();
            layout.marginHeight = 0;
            layout.marginWidth = 0;
            
            composite.setLayout( layout );
            composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            createTableGroup(composite);
            
            composite.addDisposeListener
            (
                new DisposeListener()
                {
                    public void widgetDisposed( final DisposeEvent e )
                    {
                        handleDisposeEvent();
                    }
                }
            );
            Dialog.applyDialogFont(parent);
            return composite;
        }
        return null;
    }

    /**
     * Create the table viewer and add/remove facet button group.
     * @param parent
     */
    private void createTableGroup(Composite parent)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.viewer = createTableViewer(composite);
        this.viewer.setInput(this.project);
        
        // Create the button that will open the Modify Faceted Project wizard.
        
        final Button button = new Button( parent, SWT.PUSH );
        button.setText( Resources.modifyProjectButtonLabel );
        
        final GridData gd = new GridData( GridData.HORIZONTAL_ALIGN_END );
        gd.widthHint = button.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x + 15;
        button.setLayoutData( gd );
        
        button.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent event)
                {
                    final IWizard wizard = new ModifyFacetedProjectWizard( FacetsPropertyPage.this.project );
                    final WizardDialog dialog = new WizardDialog( getShell(), wizard );
                        
                    dialog.open();
                }
            }
        );
    }
    
    private TableViewer createTableViewer(Composite parent)
    {
        final Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        
        table.setLayout(new GridLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        final TableColumn labelCol = new TableColumn(table, SWT.NONE);
        labelCol.setText(Resources.facetLabel);
        labelCol.setResizable(true);
        labelCol.setWidth(200);
        
        final TableColumn versionCol = new TableColumn(table, SWT.NONE);
        versionCol.setText(Resources.versionLabel);
        versionCol.setResizable(true);
        versionCol.setWidth(100);
        
        TableViewer viewer = new TableViewer(table);
        
        viewer.setContentProvider(new FacetsContentProvider());
        viewer.setLabelProvider(new FacetsLabelProvider());
        
        return viewer;
    }
    
    private void handleProjectChangedEvent()
    {
        final Display display = this.viewer.getTable().getDisplay();
        
        display.asyncExec
        ( 
            new Runnable()
            {
                public void run()
                {
                    FacetsPropertyPage.this.viewer.refresh();
                }
            }
        );
    }
    
    private void handleDisposeEvent()
    {
        this.project.removeListener( this.projectListener );
    }

    private static class FacetsLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        static final int DESCRIPTION_FIELD = 0;
        static final int VERSION_FIELD = 1;

        public Image getColumnImage(Object element, int columnIndex) { return null; }
        public String getColumnText(Object element, int columnIndex) 
        {
            if (element instanceof IProjectFacetVersion)
            {
                final IProjectFacetVersion facet = (IProjectFacetVersion)element;
                switch (columnIndex)
                {
                case DESCRIPTION_FIELD:
                    return facet.getProjectFacet().getLabel();
                case VERSION_FIELD:
                    return facet.getVersionString();
                }
            }
            return null;
        }
    }
    
    private static class FacetsContentProvider
    
        implements IStructuredContentProvider
        
    {
        public Object[] getElements( final Object inputElement )
        {
            if( inputElement instanceof IFacetedProject )
            {
                final IFacetedProject project = (IFacetedProject) inputElement;
                
                final List<IProjectFacetVersion> facets
                    = new ArrayList<IProjectFacetVersion>( project.getProjectFacets() );

                Collections.sort
                (
                    facets, 
                    new Comparator<IProjectFacetVersion>()
                    {
                        public int compare( final IProjectFacetVersion fv1, 
                                            final IProjectFacetVersion fv2 ) 
                        {
                            if( fv1 == fv2 )
                            {
                                return 0;
                            }
                            else
                            {
                                final String label1 = fv1.getProjectFacet().getLabel();
                                final String label2 = fv2.getProjectFacet().getLabel();
                                
                                return label1.compareTo( label2 );
                            }
                        }
                    }
                );

                return facets.toArray( new IProjectFacetVersion[ facets.size() ] );
            }
            
            return new String[ 0 ];
        }

        public void dispose(){}
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput){}
    }
    
    private static final class Resources extends NLS
    {
        public static String modifyProjectButtonLabel;
        public static String facetLabel;
        public static String versionLabel;
        
        static
        {
            initializeMessages( FacetsPropertyPage.class.getName(), 
                                Resources.class );
        }
    }
}
