/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.util.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class DomUtil 
{
    public static Document doc( final Reader r )
    {
        final DocumentBuilder docbuilder = docbuilder();
        
        try
        {
            return docbuilder.parse( new InputSource( r ) );
        }
        catch( IOException e )
        {
            throw new XmlParseException( e );
        }
        catch( SAXParseException e )
        {
            throw new XmlParseException( e );
        }
        catch( SAXException e )
        {
            throw new XmlParseException( e );
        }
    }

    public static Document doc( final String str )
    {
        return doc( new StringReader( str ) );
    }
    
    public static Document doc( final File f )
    {
        final DocumentBuilder docbuilder = docbuilder();
        
        InputStream in = null;

        try
        {
            in = new BufferedInputStream( new FileInputStream( f ) );
            return docbuilder.parse( in );
        }
        catch( IOException e )
        {
            throw new XmlParseException( e );
        }
        catch( SAXParseException e )
        {
            throw new XmlParseException( e );
        }
        catch( SAXException e )
        {
            throw new XmlParseException( e );
        }
        finally
        {
            if( in != null )
            {
                try
                {
                    in.close();
                }
                catch( IOException e ) {}
            }
        }
    }
    
    public static Document doc()
    {
        return docbuilder().newDocument();
    }
    
    public static Element root( final Reader r )
    {
        return doc( r ).getDocumentElement();
    }
    
    public static Element root( final String str )
    {
        return doc( str ).getDocumentElement();
    }
    
    public static Element root( final File f )
    {
        return doc( f ).getDocumentElement();
    }

    public static Element element( final Element el,
                                   final String name )
    {
        final NodeList nodes = el.getChildNodes();
        
        for( int i = 0, n = nodes.getLength(); i < n; i++ )
        {
            final Node node = nodes.item( i );
            
            if( node.getNodeType() == Node.ELEMENT_NODE &&
                basename( node.getNodeName() ).equals( name ) )
            {
                return (Element) node;
            }
        }
        
        return null;
    }
    
    public static Iterable<Element> elements( final Element el )
    {
        return new ElementsIterator( el.getChildNodes() );
    }
    
    public static Iterable<Element> elements( final Element el,
                                              final String name )
    {
        return new ElementsIterator( el.getChildNodes(), name );
    }
    
    public static Iterable<Element> elements( final NodeList nodes )
    {
        return new ElementsIterator( nodes );
    }

    public static Iterable<Element> elements( final NodeList nodes,
                                              final String name )
    {
        return new ElementsIterator( nodes, name );
    }
    
    public static final class ElementsIterator
    
        implements Iterator<Element>, Iterable<Element>
        
    {
        private final NodeList nodes;
        private final int length;
        private final String name;
        private int position;
        private Element element;
        
        public ElementsIterator( final NodeList nodes )
        {
            this( nodes, null );
        }
        
        public ElementsIterator( final NodeList nodes,
                                 final String name )
        {
            this.nodes = nodes;
            this.length = nodes.getLength();
            this.position = -1;
            this.name = name;
            
            advance();
        }
        
        private void advance()
        {
            this.element = null;
            this.position++;
            
            for( ; this.position < this.length && this.element == null; 
                 this.position++ )
            {
                final Node node = this.nodes.item( this.position );
                
                if( node.getNodeType() == Node.ELEMENT_NODE &&
                    ( this.name == null || 
                      basename( node.getNodeName() ).equals( this.name ) ) )
                {
                    this.element = (Element) node;
                }
            }
        }

        public boolean hasNext() 
        {
            return ( this.element != null );
        }

        public Element next() 
        {
            final Element el = this.element;

            if( el == null ) 
            {
                throw new NoSuchElementException();
            }
            
            advance();
            
            return el;
        }

        public void remove() 
        {
            throw new UnsupportedOperationException();
        }

        public Iterator<Element> iterator() 
        {
            return this;
        }
    }
    
    public static String text( final Element el )
    {
        final NodeList nodes = el.getChildNodes();

        String str = null;
        StringBuilder buf = null;
        
        for( int i = 0, n = nodes.getLength(); i < n; i++ )
        {
            final Node node = nodes.item( i );
            
            if( node.getNodeType() == Node.TEXT_NODE )
            {
                final String val = node.getNodeValue();
                
                if( buf != null )
                {
                    buf.append( val );
                }
                else if( str != null )
                {
                    buf = new StringBuilder();
                    buf.append( str );
                    buf.append( val );
                    
                    str = null;
                }
                else
                {
                    str = val;
                }
            }
        }
        
        if( buf != null )
        {
            return buf.toString();
        }
        else
        {
            return str;
        }
    }
    
    public static String basename( final String name )
    {
        final int colon = name.indexOf( ':' );
        
        if( colon != -1 )
        {
            return name.substring( colon + 1 );
        }
        else
        {
            return name;
        }
    }
    
    public static void write( final Document doc,
                              final File file )
    {
        try
        {
            final DOMSource source = new DOMSource( doc );
            final StreamResult result = new StreamResult( file );
            
            final TransformerFactory factory = TransformerFactory.newInstance();
            final Transformer transformer = factory.newTransformer();
            
            transformer.transform( source, result );
        }
        catch( TransformerConfigurationException e )
        {
            throw new RuntimeException( e );
        }
        catch( TransformerException e )
        {
            throw new RuntimeException( e );
        }
    }
    
    private static DocumentBuilder docbuilder()
    {
        try
        {
            final DocumentBuilderFactory factory 
                = DocumentBuilderFactory.newInstance();
            
            factory.setValidating( false );
            factory.setNamespaceAware( true );
            factory.setIgnoringComments( false );
            
            final DocumentBuilder builder = factory.newDocumentBuilder();
            
            builder.setEntityResolver
            (
                new EntityResolver()
                {
                    public InputSource resolveEntity( final String publicID, 
                                                      final String systemID )
                    {
                        return new InputSource( new StringReader( "" ) ); //$NON-NLS-1$
                    }
                }
            );
            
            return builder;
        }
        catch( ParserConfigurationException e )
        {
            throw new RuntimeException( e );
        }
    }
    
}
