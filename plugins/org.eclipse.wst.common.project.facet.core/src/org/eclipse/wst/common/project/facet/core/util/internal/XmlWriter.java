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

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

/**
 * Facility for writing XML files.
 * 
 * @author <a href="konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class XmlWriter
{
    public static final String ENCODING = "UTF-8"; //$NON-NLS-1$
    
    private static final String NL 
        = System.getProperty( "line.separator" ); //$NON-NLS-1$
    
    private final Writer base;
    private final String singleIndent;
    private final LinkedList<Context> elementStack;
    
    private static final class Context
    {
        public String element;
        public boolean hasChildren;
        
        public Context( final String element )
        {
            this.element = element;
            this.hasChildren = false;
        }
    }
    
    public XmlWriter( final Writer base )
    {
        this( base, "  " ); //$NON-NLS-1$
    }
    
    public XmlWriter( final Writer base,
                      final String indent )
    {
        this.base = base;
        this.singleIndent = indent;
        this.elementStack = new LinkedList<Context>();
    }
    
    public void startElement( final String name )
    
        throws IOException
        
    {
        if( this.elementStack.size() > 0 )
        {
            final Context parentContext = this.elementStack.getLast();
            
            if( ! parentContext.hasChildren )
            {
                this.base.write( '>' );
                this.base.write( NL );
            }
            
            parentContext.hasChildren = true;
        }
        
        indent();
        
        this.base.write( '<' );
        this.base.write( name );
        
        this.elementStack.addLast( new Context( name ) );
    }
    
    public void endElement()
    
        throws IOException
        
    {
        if( this.elementStack.isEmpty() )
        {
            throw new RuntimeException();
        }
        
        final Context context = this.elementStack.removeLast();
        
        if( context.hasChildren )
        {
            indent();
            this.base.write( "</" ); //$NON-NLS-1$
            this.base.write( context.element );
            this.base.write( ">" ); //$NON-NLS-1$
            this.base.write( NL );
        }
        else
        {
            this.base.write( "/>" ); //$NON-NLS-1$
            this.base.write( NL );
        }
    }
    
    public void addAttribute( final String name,
                              final String value )
    
        throws IOException
        
    {
        if( this.elementStack.isEmpty() )
        {
            throw new RuntimeException();
        }
        
        if( this.elementStack.getLast().hasChildren )
        {
            throw new RuntimeException();
        }
        
        this.base.write( ' ' );
        this.base.write( name );
        this.base.write( "=\"" ); //$NON-NLS-1$
        this.base.write( value );
        this.base.write( '"' );
    }
    
    public void flush()
    
        throws IOException
        
    {
        this.base.flush();
    }
    
    private void indent()
    
        throws IOException
        
    {
        for( int i = 0, n = this.elementStack.size(); i < n; i++ )
        {
            this.base.write( this.singleIndent );
        }
    }
    
}
