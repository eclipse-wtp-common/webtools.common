/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import static org.eclipse.jface.resource.JFaceResources.getFontRegistry;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gd;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhindent;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdvindent;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glmargins;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glspacing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IVersionExpr;
import org.eclipse.wst.common.project.facet.core.internal.Constraint;
import org.eclipse.wst.common.project.facet.core.internal.ProjectFacet;
import org.eclipse.wst.common.project.facet.ui.internal.util.EnhancedHyperlink;
import org.eclipse.wst.common.project.facet.ui.internal.util.ImageWithTextComposite;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetDetailsPanel

    extends Composite

{
    public FacetDetailsPanel( final Composite parent,
                              final FacetsSelectionPanel facetsSelectionPanel,
                              final IProjectFacetVersion facet )
    {
        super( parent, SWT.NONE );
        
        setLayout( glmargins( gl( 1 ), 0, 0 ) );
        
        final IFacetedProjectWorkingCopy fpjwc 
            = facetsSelectionPanel.getFacetedProjectWorkingCopy();
        
        final ImageWithTextComposite header = new ImageWithTextComposite( this );
        header.setLayoutData( gdhfill() );
        header.setImage( facetsSelectionPanel.getImage( facet.getProjectFacet(), false ) );
        header.setFont( getFontRegistry().get( DetailsPanel.HEADER_FONT ) );
        header.setText( facet.toString() );
        
        final Label separator = new Label( this, SWT.SEPARATOR | SWT.HORIZONTAL );
        separator.setLayoutData( gdhfill() );

        final Label descLabel = new Label( this, SWT.WRAP );
        descLabel.setLayoutData( gdhfill() );
        descLabel.setText( facet.getProjectFacet().getDescription() );
        
        final IConstraint prunedConstraint 
            = Constraint.pruneConstraint( facet, fpjwc.getFixedProjectFacets() );
        
        if( prunedConstraint != null )
        {
            final List<IConstraint> requirements = getRequiresConstraints( prunedConstraint );
            
            if( requirements != null )
            {
                final Label requiredFacetsLabel = new Label( this, SWT.NONE );
                requiredFacetsLabel.setLayoutData( gdvindent( gdhfill(), 5 ) );
                requiredFacetsLabel.setText( Resources.requiredFacetsLabel );
                
                final Composite requiredFacetsComposite = new Composite( this, SWT.NONE );
                requiredFacetsComposite.setLayoutData( gdhindent( gdvindent( gdfill(), 5 ), 5 ) );
                requiredFacetsComposite.setLayout( glspacing( glmargins( gl( 1 ), 0, 0 ), 0, 3 ) );
                
                for( IConstraint requirement : requirements )
                {
                    final IProjectFacet f = (IProjectFacet) requirement.getOperand( 0 );
                    
                    final StringBuilder text = new StringBuilder();
                    final IVersionExpr vexpr = (IVersionExpr) requirement.getOperand( 1 );
                    
                    text.append( f.getLabel() );
                    
                    if( ! vexpr.toString().equals( IVersionExpr.WILDCARD_SYMBOL ) &&
                        ! ( (ProjectFacet) f ).isVersionHidden() )
                    {
                        text.append( ' ' );
                        text.append( vexpr.toDisplayString() );
                    }
                    
                    final ImageWithTextComposite fLabel = new ImageWithTextComposite( requiredFacetsComposite );
                    fLabel.setLayoutData( gdhfill() );
                    fLabel.setImage( facetsSelectionPanel.getImage( f, false ) );
                    fLabel.setText( text.toString() );
                }
            }
            else
            {
                final Hyperlink showConstraintsLink = new EnhancedHyperlink( this, SWT.NONE );
                showConstraintsLink.setText( Resources.showConstraintsLink );
                showConstraintsLink.setLayoutData( gdvindent( gd(), 4 ) );
        
                showConstraintsLink.addHyperlinkListener
                (
                    new HyperlinkAdapter()
                    {
                        public void linkActivated( HyperlinkEvent e )
                        {
                            handleShowConstraintsLinkActivated( prunedConstraint );
                        }
                    }
                );
            }
        }
    }
    
    private static List<IConstraint> getRequiresConstraints( final IConstraint constraint )
    {
        final List<IConstraint> requirements
            = getRequiresConstraints( constraint, new ArrayList<IConstraint>() );
        
        if( requirements != null )
        {
            Collections.sort
            ( 
                requirements,
                new Comparator<IConstraint>()
                {
                    public int compare( final IConstraint x,
                                        final IConstraint y )
                    {
                        final String xLabel = ( (IProjectFacet) x.getOperand( 0 ) ).getLabel();
                        final String yLabel = ( (IProjectFacet) y.getOperand( 0 ) ).getLabel();
                        
                        return xLabel.compareTo( yLabel );
                    }
                }
            );
        }
        
        return requirements;
    }
    
    private static List<IConstraint> getRequiresConstraints( final IConstraint constraint,
                                                             final List<IConstraint> requirements )
    {
        if( constraint.getType() == IConstraint.Type.AND )
        {
            for( Object operand : constraint.getOperands() )
            {
                if( getRequiresConstraints( (IConstraint) operand, requirements ) == null )
                {
                    return null;
                }
            }
            
            return requirements;
        }
        else if( constraint.getType() == IConstraint.Type.REQUIRES )
        {
            requirements.add( constraint );
            return requirements;
        }
        else
        {
            return null;
        }
    }
    
    private void handleShowConstraintsLinkActivated( final IConstraint constraint )
    {
        final ConstraintDisplayDialog dialog 
            = new ConstraintDisplayDialog( getShell(), null, constraint );
    
        dialog.open();
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String showConstraintsLink;
        public static String requiredFacetsLabel;
        
        static
        {
            initializeMessages( FacetDetailsPanel.class.getName(), 
                                Resources.class );
        }
    }
    
}
