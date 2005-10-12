package org.eclipse.wst.common.project.facet.ui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public final class ConstraintDisplayDialog

    extends Dialog
    
{
    private final Point location;
    private int width;
    private int height;
    
    protected ConstraintDisplayDialog( final Shell parentShell,
                                       final Point location )
    {
        super( parentShell );
        
        setShellStyle( SWT.APPLICATION_MODAL | getDefaultOrientation() );
        
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
        
        final DirectedGraph graph = new DirectedGraph();
        
        final Figure box1 = new Figure();
        box1.setLayoutManager( new ToolbarLayout() );
        box1.setBorder( new LineBorder( ColorConstants.black, 1 ) );
        box1.setOpaque( false );
        box1.add( new Label( "box1\nlabel" ) );
        final Node box1node = new Node( box1 );
        graph.nodes.add( box1node );

        final Figure box2 = new Figure();
        box2.setLayoutManager( new ToolbarLayout() );
        box2.setBorder( new LineBorder( ColorConstants.black, 1 ) );
        box2.setOpaque( false );
        box2.add( new Label( "box2\nlabel" ) );
        final Node box2node = new Node( box2 );
        graph.nodes.add( box2node );
        
        final Figure box3 = new Figure();
        box3.setLayoutManager( new ToolbarLayout() );
        box3.setBorder( new LineBorder( ColorConstants.black, 1 ) );
        box3.setOpaque( false );
        box3.add( new Label( "box3\nlabel" ) );
        final Node box3node = new Node( box3 );
        graph.nodes.add( box3node );
        
        final Figure box4 = new Figure();
        box4.setLayoutManager( new ToolbarLayout() );
        box4.setBorder( new LineBorder( ColorConstants.black, 1 ) );
        box4.setOpaque( false );
        box4.add( new Label( "box4\nlabel" ) );
        final Node box4node = new Node( box4 );
        box4node.sortValue = -99.0;
        graph.nodes.add( box4node );

        final Figure box5 = new Figure();
        box5.setLayoutManager( new ToolbarLayout() );
        box5.setBorder( new LineBorder( ColorConstants.black, 1 ) );
        box5.setOpaque( false );
        box5.add( new Label( "box5\nlabel" ) );
        final Node box5node = new Node( box5 );
        graph.nodes.add( box5node );
        
        final Figure box6 = new Figure();
        box6.setLayoutManager( new ToolbarLayout() );
        box6.setBorder( new LineBorder( ColorConstants.black, 1 ) );
        box6.setOpaque( false );
        box6.add( new Label( "box6\nlabel" ) );
        final Node box6node = new Node( box6 );
        graph.nodes.add( box6node );

        final Figure box7 = new Figure();
        box7.setLayoutManager( new ToolbarLayout() );
        box7.setBorder( new LineBorder( ColorConstants.black, 1 ) );
        box7.setOpaque( false );
        box7.add( new Label( "box7\nlabel" ) );
        final Node box7node = new Node( box7 );
        graph.nodes.add( box7node );
        
        final Figure box8 = new Figure();
        box8.setLayoutManager( new ToolbarLayout() );
        box8.setBorder( new LineBorder( ColorConstants.black, 1 ) );
        box8.setOpaque( false );
        box8.add( new Label( "box8\nlabel" ) );
        final Node box8node = new Node( box8 );
        graph.nodes.add( box8node );
        
        final Figure box9 = new Figure();
        box9.setLayoutManager( new ToolbarLayout() );
        box9.setBorder( new LineBorder( ColorConstants.black, 1 ) );
        box9.setOpaque( false );
        box9.add( new Label( "box9\nlabel" ) );
        final Node box9node = new Node( box9 );
        graph.nodes.add( box9node );

        final Figure box10 = new Figure();
        box10.setLayoutManager( new ToolbarLayout() );
        box10.setBorder( new LineBorder( ColorConstants.black, 1 ) );
        box10.setOpaque( false );
        box10.add( new Label( "box10\nlabel" ) );
        final Node box10node = new Node( box10 );
        graph.nodes.add( box10node );
        
        final PolylineConnection cn1 = new PolylineConnection();
        cn1.setSourceAnchor( new ChopboxAnchor( box4 ) );
        cn1.setTargetAnchor( new ChopboxAnchor( box1 ) );
        final Edge cn1edge = new Edge( cn1, box4node, box1node );
        graph.edges.add( cn1edge );
        
        final PolylineConnection cn2 = new PolylineConnection();
        cn2.setSourceAnchor( new ChopboxAnchor( box4 ) );
        cn2.setTargetAnchor( new ChopboxAnchor( box2 ) );
        final Edge cn2edge = new Edge( cn2, box4node, box2node );
        graph.edges.add( cn2edge );
        
        final PolylineConnection cn3 = new PolylineConnection();
        cn3.setSourceAnchor( new ChopboxAnchor( box4 ) );
        cn3.setTargetAnchor( new ChopboxAnchor( box3 ) );
        final Edge cn3edge = new Edge( cn3, box4node, box3node );
        graph.edges.add( cn3edge );

        final PolylineConnection cn4 = new PolylineConnection();
        cn4.setSourceAnchor( new ChopboxAnchor( box3 ) );
        cn4.setTargetAnchor( new ChopboxAnchor( box5 ) );
        final Edge cn4edge = new Edge( cn4, box3node, box5node );
        graph.edges.add( cn4edge );

        final PolylineConnection cn5 = new PolylineConnection();
        cn5.setSourceAnchor( new ChopboxAnchor( box3 ) );
        cn5.setTargetAnchor( new ChopboxAnchor( box6 ) );
        final Edge cn5edge = new Edge( cn5, box3node, box6node );
        graph.edges.add( cn5edge );

        final PolylineConnection cn6 = new PolylineConnection();
        cn6.setSourceAnchor( new ChopboxAnchor( box3 ) );
        cn6.setTargetAnchor( new ChopboxAnchor( box7 ) );
        final Edge cn6edge = new Edge( cn6, box3node, box7node );
        graph.edges.add( cn6edge );

        final PolylineConnection cn7 = new PolylineConnection();
        cn7.setSourceAnchor( new ChopboxAnchor( box2 ) );
        cn7.setTargetAnchor( new ChopboxAnchor( box8 ) );
        final Edge cn7edge = new Edge( cn7, box2node, box8node );
        graph.edges.add( cn7edge );

        final PolylineConnection cn8 = new PolylineConnection();
        cn8.setSourceAnchor( new ChopboxAnchor( box2 ) );
        cn8.setTargetAnchor( new ChopboxAnchor( box9 ) );
        final Edge cn8edge = new Edge( cn2, box2node, box9node );
        graph.edges.add( cn8edge );

        final PolylineConnection cn9 = new PolylineConnection();
        cn9.setSourceAnchor( new ChopboxAnchor( box9 ) );
        cn9.setTargetAnchor( new ChopboxAnchor( box10 ) );
        final Edge cn9edge = new Edge( cn9, box9node, box10node );
        graph.edges.add( cn9edge );

        contents.setLayoutManager( new GraphLayoutManager( graph ) );
        
        contents.add( box1 );
        contents.add( box2 );
        contents.add( box3 );
        contents.add( box4 );
        contents.add( box5 );
        contents.add( box6 );
        contents.add( box7 );
        contents.add( box8 );
        contents.add( box9 );
        contents.add( box10 );
        contents.add( cn1 );
        contents.add( cn2 );
        contents.add( cn3 );
        contents.add( cn4 );
        contents.add( cn5 );
        contents.add( cn6 );
        contents.add( cn7 );
        contents.add( cn8 );
        contents.add( cn9 );
        
        final Label escLabel = new Label( "Press 'Esc' to close." );
        escLabel.setLabelAlignment( Label.RIGHT );
        escLabel.setBorder( new CompoundBorder( new DoubleLineBorder(), new MarginBorder( 2 ) ) );
        
        outer.add( escLabel );

        final Dimension size = outer.getPreferredSize();
        this.width = size.width;
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
    
    public class DoubleLineBorder 
        
        extends AbstractBorder 
        
    {
        private Insets insets = new Insets( 3, 0, 0, 0 );
        
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
            
            final int y = paintRectangle.getTopLeft().y;
            final int xLeft = paintRectangle.getTopLeft().x;
            final int xRight = paintRectangle.getTopRight().x;
            
            graphics.drawLine( xLeft, y, xRight, y );
            graphics.drawLine( xLeft, y + 2, xRight, y + 2 );
        }
    }
    
    private static final class GraphLayoutManager
    
        extends AbstractLayout
        
    {
        private final DirectedGraph graph;
        private int laidout = 0;
        
        public GraphLayoutManager( final DirectedGraph graph )
        {
            this.graph = graph;
        }
        
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

        public void layout( IFigure container )
        {
            if( this.laidout > 5 )
            {
                return;
            }
            
            ( new DirectedGraphLayout() ).visit( this.graph );

            for( Iterator itr = this.graph.nodes.iterator(); itr.hasNext(); )
            {
                final Node node = (Node) itr.next();
                final IFigure figure = (IFigure) node.data;
                
                final Rectangle bounds 
                    = new Rectangle( node.x + 10, node.y - 6, 
                                     figure.getPreferredSize().width,
                                     figure.getPreferredSize().height );
                
                figure.setBounds( bounds );
            }

            for( Iterator itr = this.graph.edges.iterator(); itr.hasNext(); )
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
                        
                        if( edge.isFeedback )
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
    
    
}
