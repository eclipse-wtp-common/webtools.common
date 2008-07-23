/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IVersionExpr;
import org.eclipse.wst.common.project.facet.core.internal.Constraint;
import org.eclipse.wst.common.project.facet.core.internal.ProjectFacet;
import org.eclipse.wst.common.project.facet.core.internal.ProjectFacetRef;
import org.eclipse.wst.common.project.facet.ui.internal.constraints.ConstraintOperator;
import org.eclipse.wst.common.project.facet.ui.internal.constraints.ConstraintUtil;
import org.eclipse.wst.common.project.facet.ui.internal.constraints.GroupingConstraintOperator;
import org.eclipse.wst.common.project.facet.ui.internal.constraints.MultiFacetConstraintOperator;
import org.eclipse.wst.common.project.facet.ui.internal.util.ImageWithTextComposite;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class FacetDetailsPanel

    extends Composite

{
    private static final Comparator<ProjectFacetRef> FACET_REF_COMPARATOR 
        = new Comparator<ProjectFacetRef>()
    {
        public int compare( final ProjectFacetRef x,
                            final ProjectFacetRef y )
        {
            final String xLabel = x.getProjectFacet().getLabel();
            final String yLabel = y.getProjectFacet().getLabel();
            
            return xLabel.compareTo( yLabel );
        }
    };

    private final FacetsSelectionPanel facetsSelectionPanel;
    
    public FacetDetailsPanel( final Composite parent,
                              final FacetsSelectionPanel facetsSelectionPanel,
                              final IProjectFacetVersion facet )
    {
        super( parent, SWT.NONE );
        
        this.facetsSelectionPanel = facetsSelectionPanel;
        
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
        
        final ScrolledComposite details = new ScrolledComposite( this, SWT.V_SCROLL | SWT.H_SCROLL );
        details.setLayoutData( gdfill() );
        details.setMinWidth( 300 );
        details.setExpandHorizontal( true );
        details.setExpandVertical( true );
        
        final Composite nestedDetailsComposite = new Composite( details, SWT.NONE );
        nestedDetailsComposite.setLayout( glmargins( gl( 1 ), 0, 10, 0, 0 ) );
        details.setContent( nestedDetailsComposite );

        final Text descTextField = new Text( nestedDetailsComposite, SWT.WRAP | SWT.READ_ONLY );
        descTextField.setLayoutData( gdhfill() );
        descTextField.setText( facet.getProjectFacet().getDescription() );
        //descTextField.setBackground( getDisplay().getSystemColor( SWT.COLOR_WIDGET_BACKGROUND ) );
        
        final IConstraint prunedConstraint 
            = Constraint.pruneConstraint( facet, fpjwc.getFixedProjectFacets() );
        
        if( prunedConstraint != null )
        {
            final ConstraintOperator normalizedConstraint
                = ConstraintUtil.normalize( ConstraintUtil.convert( prunedConstraint ) );
            
            final List<ConstraintOperator> topLevelOperators;
            
            if( normalizedConstraint.getType() == ConstraintOperator.Type.AND )
            {
                topLevelOperators = ( (GroupingConstraintOperator) normalizedConstraint ).getChildren();
            }
            else
            {
                topLevelOperators = Collections.singletonList( normalizedConstraint );
            }

            renderConstraints( nestedDetailsComposite, topLevelOperators );
        }
        
        details.setMinHeight( nestedDetailsComposite.computeSize( 300, SWT.DEFAULT ).y );
    }
    
    private void renderConstraints( final Composite parent,
                                    final List<ConstraintOperator> constraints )
    {
        for( ConstraintOperator op : constraints )
        {
            final ConstraintOperator.Type type = op.getType();
            
            if( op instanceof MultiFacetConstraintOperator )
            {
                final MultiFacetConstraintOperator mfop = (MultiFacetConstraintOperator) op;
                final String labelText;
                
                if( type == ConstraintOperator.Type.REQUIRES_ALL )
                {
                    labelText = Resources.requiresAllFacetsLabel;
                }
                else if( type == ConstraintOperator.Type.REQUIRES_ONE )
                {
                    if( mfop.getProjectFacetRefs().size() == 1 )
                    {
                        labelText = Resources.requiresFacetLabel;
                    }
                    else
                    {
                        labelText = Resources.requiresOneOfFacetsLabel;
                    }
                }
                else
                {
                    labelText = Resources.conflictingFacetsLabel;
                }

                final Text label = new Text( parent, SWT.READ_ONLY );
                label.setLayoutData( gdvindent( gdhfill(), 5 ) );
                label.setText( labelText );
                //label.setBackground( getDisplay().getSystemColor( SWT.COLOR_WIDGET_BACKGROUND ) );
                
                final Composite facetsComposite = new Composite( parent, SWT.NONE );
                facetsComposite.setLayoutData( gdhindent( gdvindent( gdhfill(), 5 ), 5 ) );
                facetsComposite.setLayout( glspacing( glmargins( gl( 1 ), 0, 0 ), 0, 3 ) );
                
                final List<ProjectFacetRef> sortedProjectFacetRefs 
                    = new ArrayList<ProjectFacetRef>( mfop.getProjectFacetRefs() );
                
                Collections.sort( sortedProjectFacetRefs, FACET_REF_COMPARATOR );
                
                for( ProjectFacetRef requirement : sortedProjectFacetRefs )
                {
                    final IProjectFacet f = requirement.getProjectFacet();
                    final IVersionExpr vexpr = requirement.getVersionExpr();
                    
                    final StringBuilder text = new StringBuilder();
                    
                    text.append( f.getLabel() );
                    
                    if( ! vexpr.toString().equals( IVersionExpr.WILDCARD_SYMBOL ) &&
                        ! ( (ProjectFacet) f ).isVersionHidden() )
                    {
                        text.append( ' ' );
                        text.append( vexpr.toDisplayString() );
                    }
                    
                    final ImageWithTextComposite fLabel = new ImageWithTextComposite( facetsComposite );
                    fLabel.setLayoutData( gdhfill() );
                    fLabel.setImage( this.facetsSelectionPanel.getImage( f, false ) );
                    fLabel.setText( text.toString() );
                }
            }
            else
            {
                final Label requiredFacetsLabel = new Label( parent, SWT.NONE );
                requiredFacetsLabel.setLayoutData( gdhfill() );
                requiredFacetsLabel.setText( op.getType().name() );
                
                final Composite composite = new Composite( parent, SWT.NONE );
                composite.setLayoutData( gdhindent( gdhfill(), 5 ) );
                composite.setLayout( glspacing( glmargins( gl( 1 ), 0, 0 ), 0, 3 ) );
                
                renderConstraints( composite, ( (GroupingConstraintOperator) op ).getChildren() );
            }
        }
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String requiresFacetLabel;
        public static String requiresAllFacetsLabel;
        public static String requiresOneOfFacetsLabel;
        public static String conflictingFacetsLabel;
        
        static
        {
            initializeMessages( FacetDetailsPanel.class.getName(), 
                                Resources.class );
        }
    }
    
}
