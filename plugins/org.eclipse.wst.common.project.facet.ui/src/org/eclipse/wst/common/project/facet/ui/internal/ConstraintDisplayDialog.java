package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.project.facet.core.IConstraint;
import org.eclipse.wst.common.project.facet.core.IGroup;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IVersionExpr;

public final class ConstraintDisplayDialog

    extends Dialog
    
{
    private static final Font BOLD_FONT;
    
    static
    {
        final FontData sys 
            = Display.getCurrent().getSystemFont().getFontData()[ 0 ];
        
        final FontData bold
            = new FontData( sys.getName(), sys.getHeight(), SWT.BOLD );
        
        BOLD_FONT = new Font( Display.getCurrent(), bold );
    }
    
    private final IConstraint constraint;
    private final Point location;
    private int width;
    private int height;
    
    protected ConstraintDisplayDialog( final Shell parentShell,
                                       final Point location,
                                       final IConstraint constraint )
    {
        super( parentShell );
        
        setShellStyle( SWT.APPLICATION_MODAL | getDefaultOrientation() );
        
        this.constraint = constraint;
        this.location = location;
    }

    protected Control createDialogArea( final Composite parent ) 
    {
        final Canvas canvas = new Canvas( parent, SWT.NONE );
        canvas.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        canvas.setBackground( new Color( null, 255, 255, 206 ) );
        
        final LightweightSystem lws = new LightweightSystem( canvas );
        final Figure outer = new Figure();
        outer.setLayoutManager( new ToolbarLayout() );
        lws.setContents( outer );
        final Figure contents = new Figure();
        contents.setBorder( new MarginBorder( 10 ) );
        outer.add( contents );
        
        createConstraintGraph( contents, null, this.constraint );
        contents.setLayoutManager( new GraphLayoutManager() );
        
        final Label escLabel = new Label( Resources.pressEscToClose );
        escLabel.setLabelAlignment( Label.RIGHT );
        escLabel.setBorder( new CompoundBorder( new DividerBorder( 2 ), new MarginBorder( 2 ) ) );
        
        outer.add( escLabel );

        final Dimension size = outer.getPreferredSize();
        this.width = ( size.width < 200 ? 200 : size.width );
        this.height = size.height;
        
        return canvas;
    }
    
    protected Control createButtonBar( final Composite parent )
    {
        return null;
    }
    
    protected Point getInitialLocation( final Point size )
    {
        return this.location;
    }
    
    protected Point getInitialSize()
    {
        return new Point( this.width, this.height );
    }
    
    private void createConstraintGraph( final IFigure container,
                                        final PolylineConnection parentCon,
                                        final IConstraint constraint )
    {
        if( constraint.getType() == IConstraint.Type.AND ||
            constraint.getType() == IConstraint.Type.OR )
        {
            if( constraint.getOperands().size() == 1 )
            {
                final IConstraint child
                    = (IConstraint) constraint.getOperand( 0 );
                
                createConstraintGraph( container, parentCon, child ); 
            }
            else
            {
                final AndOrConstraintFigure node 
                    = new AndOrConstraintFigure( constraint );
                
                container.add( node );
                
                if( parentCon != null )
                {
                    parentCon.setTargetAnchor( new EllipseAnchor( node ) );
                }
                
                for( Iterator itr = constraint.getOperands().iterator(); 
                     itr.hasNext(); )
                {
                    final IConstraint child = (IConstraint) itr.next();
                    final PolylineConnection childEdge = new PolylineConnection();
                    container.add( childEdge );
                    childEdge.setSourceAnchor( new EllipseAnchor( node ) );
                    createConstraintGraph( container, childEdge, child );
                }
            }
        }
        else if( constraint.getType() == IConstraint.Type.REQUIRES )
        {
            final RequiresConstraintFigure node 
                = new RequiresConstraintFigure( constraint );
            
            container.add( node );

            if( parentCon != null )
            {
                parentCon.setTargetAnchor( new ChopboxAnchor( node ) );
            }
        }
        else if( constraint.getType() == IConstraint.Type.CONFLICTS )
        {
            final ConflictsConstraintFigure node 
                = new ConflictsConstraintFigure( constraint );
        
            container.add( node );

            if( parentCon != null )
            {
                parentCon.setTargetAnchor( new ChopboxAnchor( node ) );
            }
        }
        else
        {
            throw new IllegalStateException();
        }
    }
    
    private static final class GraphLayoutManager
    
        extends AbstractLayout
        
    {
        private int laidout = 0;
        
        protected Dimension calculatePreferredSize( final IFigure container, 
                                                    final int wHint, 
                                                    final int hHint )
        {
            container.validate();
            List children = container.getChildren();
            Rectangle result = new Rectangle().setLocation(container.getClientArea().getLocation());
            for (int i = 0; i < children.size(); i++)
                result.union(((IFigure) children.get(i)).getBounds());
            result.resize(container.getInsets().getWidth() + 2, container.getInsets().getHeight() + 2);
            return result.getSize();        
        }

        public void layout( final IFigure container )
        {
            if( this.laidout > 5 )
            {
                return;
            }
            
            // Create the graph.
            
            final DirectedGraph graph = new DirectedGraph();
            final Map nodes = new HashMap();
            
            for( Iterator itr = container.getChildren().iterator(); 
                 itr.hasNext(); )
            {
                final IFigure child = (IFigure) itr.next();
                
                if( ! ( child instanceof PolylineConnection ) )
                {
                    final Node node = new Node( child );
                    final Dimension size = child.getPreferredSize();
                    node.height = size.height;
                    node.width = size.width;
                    graph.nodes.add( node );
                    nodes.put( child, node );
                }
            }
            
            for( Iterator itr = container.getChildren().iterator(); 
                 itr.hasNext(); )
            {
                final Object child = itr.next();
                
                if( child instanceof PolylineConnection )
                {
                    final PolylineConnection cn = (PolylineConnection) child;
                    
                    final IFigure source = cn.getSourceAnchor().getOwner();
                    final Node sourceNode = (Node) nodes.get( source );
                    
                    final IFigure target = cn.getTargetAnchor().getOwner();
                    final Node targetNode = (Node) nodes.get( target );
                    
                    final Edge edge = new Edge( cn, sourceNode, targetNode );
                    graph.edges.add( edge );
                }
            }
            
            // Call the graph layout algorith to determine node positions.
            
            ( new DirectedGraphLayout() ).visit( graph );
            
            // Layout nodes based on the results of the graph layout.

            for( Iterator itr = graph.nodes.iterator(); itr.hasNext(); )
            {
                final Node node = (Node) itr.next();
                final IFigure figure = (IFigure) node.data;
                
                final Rectangle bounds 
                    = new Rectangle( node.x + 10, node.y - 6, 
                                     figure.getPreferredSize().width,
                                     figure.getPreferredSize().height );
                
                figure.setBounds( bounds );
            }

            for( Iterator itr = graph.edges.iterator(); itr.hasNext(); )
            {
                final Edge edge = (Edge) itr.next();
                final PolylineConnection cn = (PolylineConnection) edge.data;
                
                if( edge.vNodes == null )
                {
                    cn.setRoutingConstraint( Collections.EMPTY_LIST );
                }
                else
                {
                    final List bends = new ArrayList();
                    
                    for( Iterator itr2 = edge.vNodes.iterator(); 
                         itr2.hasNext(); )
                    {
                        final Node vn = (Node) itr2.next();
                        
                        if( edge.isFeedback() )
                        {
                            bends.add( new AbsoluteBendpoint( vn.x, vn.y + vn.height ) );
                            bends.add( new AbsoluteBendpoint( vn.x, vn.y ) );
                        }
                        else
                        {
                            bends.add( new AbsoluteBendpoint( vn.x, vn.y ) );
                            bends.add( new AbsoluteBendpoint( vn.x, vn.y + vn.height ) );
                        }
                    }
                    
                    cn.setRoutingConstraint( bends );
                }
            }
            
            this.laidout++;
        }
    }
    
    private static final class DividerBorder
        
        extends AbstractBorder 
        
    {
        private final int lines;
        private final Insets insets;
        
        public DividerBorder( final int lines )
        {
            this.lines = lines;
            this.insets = new Insets( 1 + ( this.lines - 1 ) * 2 );
        }
        
        public Insets getInsets( final IFigure figure ) 
        {
            return this.insets;
        }
        
        public void paint( final IFigure figure, 
                           final Graphics graphics, 
                           final Insets insets ) 
        {
            final Rectangle paintRectangle 
                = getPaintRectangle( figure, insets );
            
            final int xLeft = paintRectangle.getTopLeft().x;
            final int xRight = paintRectangle.getTopRight().x;
            
            int y = paintRectangle.getTopLeft().y;
            
            for( int i = 0; i < this.lines; i++, y += 2 )
            {
                graphics.drawLine( xLeft, y, xRight, y );
            }
        }
    }
    
    private static final class DashedLineBorder
    
        extends LineBorder
        
    {
        public DashedLineBorder()
        {
            super( 1 );
        }
        
        public void paint(IFigure figure, Graphics graphics, Insets insets) 
        {
            graphics.setLineStyle( SWT.LINE_DASH );
            super.paint( figure, graphics, insets );
            graphics.setLineStyle( SWT.LINE_SOLID );
        }
    }
    
    private static final class AndOrConstraintFigure
    
        extends Figure
        
    {
        public AndOrConstraintFigure( final IConstraint constraint )
        {
            setLayoutManager( new StackLayout() );
            
            final String labelText;
            final Color background;
            
            if( constraint.getType() == IConstraint.Type.AND )
            {
                labelText = Resources.andOperator;
                background = new Color( null, 0, 175, 0 );
            }
            else if( constraint.getType() == IConstraint.Type.OR )
            {
                labelText = Resources.orOperator;
                background = new Color( null, 255, 128, 0 ); 
            }
            else
            {
                throw new IllegalStateException();
            }
            
            final Ellipse circle = new Ellipse();
            circle.setOpaque( true );
            circle.setBackgroundColor( background );
            add( circle );
            
            final Label label = new Label();
            label.setFont( BOLD_FONT );
            label.setText( labelText );
            add( label );
        }
        
        public Dimension getPreferredSize( final int wHint, 
                                           final int hHint )
        {
            return new Dimension( 35, 35 );
        }
    }
    
    private static final class RequiresConstraintFigure
    
        extends Figure
        
    {
        public RequiresConstraintFigure( final IConstraint constraint )
        {
            final IProjectFacet f = (IProjectFacet) constraint.getOperand( 0 );
            final IVersionExpr vexpr = (IVersionExpr) constraint.getOperand( 1 );
            final Boolean soft = (Boolean) constraint.getOperand( 2 );
            
            setLayoutManager( new ToolbarLayout() );
            setBackgroundColor( new Color( null, 255, 255, 255 ) );
            setOpaque( true );
            
            setBorder( soft.booleanValue() 
                       ? new DashedLineBorder() : new LineBorder( 1 )  );
            
            final Label headerLabel = new Label();
            headerLabel.setFont( BOLD_FONT );
            headerLabel.setText( Resources.requiresOperator );
            headerLabel.setBorder( new MarginBorder( 2 ) );
            add( headerLabel );
            
            final StringBuffer bodyLabelText = new StringBuffer();
            
            bodyLabelText.append( f.getLabel() );
            
            if( ! vexpr.toString().equals( IVersionExpr.WILDCARD_SYMBOL ) )
            {
                bodyLabelText.append( ' ' );
                bodyLabelText.append( vexpr.toDisplayString() );
            }
            
            final Label bodyLabel = new Label();
            bodyLabel.setText( bodyLabelText.toString() );
            bodyLabel.setBorder( new CompoundBorder( new DividerBorder( 1 ), new MarginBorder( 2 ) ) );
            add( bodyLabel );
        }
    }

    private static final class ConflictsConstraintFigure
    
        extends Figure
        
    {
        public ConflictsConstraintFigure( final IConstraint constraint )
        {
            setLayoutManager( new ToolbarLayout() );
            setBorder( new LineBorder( 1 ) );
            setOpaque( true );
            setBackgroundColor( new Color( null, 255, 255, 255 ) );
            
            final Label headerLabel = new Label();
            headerLabel.setFont( BOLD_FONT );
            headerLabel.setBorder( new MarginBorder( 2 ) );
            add( headerLabel );
            
            final StringBuffer bodyLabelText = new StringBuffer();
            final Object firstOperand = constraint.getOperand( 0 );
            
            if( firstOperand instanceof IGroup )
            {
                headerLabel.setText( Resources.conflictsWithGroupOperator );
                
                bodyLabelText.append( ( (IGroup) firstOperand ).getId() );
            }
            else
            {
                headerLabel.setText( Resources.conflictsWithFacetOperator );
                
                final IProjectFacet f = (IProjectFacet) firstOperand;
                
                bodyLabelText.append( f.getLabel() );
                
                if( constraint.getOperands().size() == 2 )
                {
                    final IVersionExpr vexpr 
                        = (IVersionExpr) constraint.getOperand( 1 );
                    
                    bodyLabelText.append( ' ' );
                    bodyLabelText.append( vexpr.toDisplayString() );
                }
            }

            final Label bodyLabel = new Label();
            bodyLabel.setText( bodyLabelText.toString() );
            bodyLabel.setBorder( new CompoundBorder( new DividerBorder( 1 ), new MarginBorder( 2 ) ) );
            add( bodyLabel );
        }
    }

    private static final class Resources
    
        extends NLS
        
    {
        public static String pressEscToClose;
        public static String andOperator;
        public static String orOperator;
        public static String requiresOperator;
        public static String conflictsWithGroupOperator;
        public static String conflictsWithFacetOperator;
        
        static
        {
            initializeMessages( ConstraintDisplayDialog.class.getName(), 
                                Resources.class );
        }
    }
    
}
