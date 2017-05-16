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

package org.eclipse.jst.common.project.facet.ui.libprov.osgi;

import static org.eclipse.jst.common.project.facet.ui.internal.FacetedProjectFrameworkJavaExtUiPlugin.PLUGIN_ID;
import static org.eclipse.ui.plugin.AbstractUIPlugin.imageDescriptorFromPlugin;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jst.common.project.facet.core.libprov.osgi.BundleReference;
import org.eclipse.jst.common.project.facet.core.libprov.osgi.OsgiBundlesLibraryProviderInstallOperationConfig;
import org.eclipse.jst.common.project.facet.ui.libprov.LibraryProviderOperationPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.wst.common.project.facet.ui.internal.util.ProblemOverlayImageDescriptor;

/**
 * The install operation panel corresponding to the osgi-bundles-library-provider that exposes
 * specified OSGi libraries from the Eclipse install to user project's classpath. This class can 
 * be subclassed by those wishing to extend the base implementation supplied by the framework.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

public class OsgiBundlesLibraryProviderInstallPanel

    extends LibraryProviderOperationPanel
    
{
    /**
     * Creates the panel control.
     * 
     * @param parent the parent composite
     * @return the created control
     */
    
    @Override
    public Control createControl( final Composite parent )
    {
        final Composite rootComposite = new Composite( parent, SWT.NONE );
        rootComposite.setLayout( gl( 1, 0, 0 ) );
        
        final Table bundlesTable = new Table( rootComposite, SWT.BORDER );
        bundlesTable.setLayoutData( gdhhint( gdhfill(), 60 ) );
        bundlesTable.setBackground( Display.getCurrent().getSystemColor( SWT.COLOR_INFO_BACKGROUND ) );
        
        final TableViewer bundlesTableViewer = new TableViewer( bundlesTable );
        
        final IStructuredContentProvider contentProvider = new IStructuredContentProvider()
        {
            public Object[] getElements( final Object inputElement )
            {
                final OsgiBundlesLibraryProviderInstallOperationConfig cfg
                    = (OsgiBundlesLibraryProviderInstallOperationConfig) getOperationConfig();
                
                return cfg.getBundleReferences().toArray();
            }
    
            public void inputChanged( final Viewer viewer,
                                      final Object oldInput,
                                      final Object newInput )
            {
            }
            
            public void dispose()
            {
            }
        };
        
        bundlesTableViewer.setContentProvider( contentProvider );
        
        final LabelProvider labelProvider = new LabelProvider()
        {
            private final ImageDescriptor bundleImageDescriptor 
                = imageDescriptorFromPlugin( PLUGIN_ID, "images/bundle.png" ); //$NON-NLS-1$
            
            private final Image bundleImage = this.bundleImageDescriptor.createImage();
            
            private final Image bundleImageError
                = ( new ProblemOverlayImageDescriptor( this.bundleImageDescriptor, IStatus.ERROR ) ).createImage();
            
            public Image getImage( final Object element )
            {
                final boolean isResolvable = ( (BundleReference) element ).isResolvable();
                return isResolvable ? this.bundleImage : this.bundleImageError;
            }
    
            public String getText( final Object element )
            {
                final BundleReference bundleReference = (BundleReference) element;
                final StringBuilder buf = new StringBuilder();
                
                buf.append( ' ' );
                buf.append( bundleReference.getBundleId() );
                
                if( bundleReference.getVersionRange() != null )
                {
                    buf.append( ' ' );
                    buf.append( bundleReference.getVersionRange().toString() );
                }
                
                return buf.toString();
            }
    
            @Override
            public void dispose()
            {
                this.bundleImage.dispose();
                this.bundleImageError.dispose();
            }
        };
        
        bundlesTableViewer.setLabelProvider( labelProvider );

        bundlesTableViewer.setInput( new Object() );
        
        final Control footerControl = createFooter( rootComposite );
        
        if( footerControl != null )
        {
            footerControl.setLayoutData( gdhfill() );
        }
        
        return rootComposite;
    }
    
    /**
     * This method can be overridden to create a control beneath the bundles table. The default
     * implementation doesn't create a control and returns <code>null</code>.
     * 
     * @param parent the parent composite 
     * @return the created control
     */
    
    protected Control createFooter( final Composite parent )
    {
        return null;
    }

}