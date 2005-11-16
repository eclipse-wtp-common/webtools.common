/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class VersionMatchExpr
{
    private final Versionable versionable;
    private final List subexprs = new ArrayList();
    
    public VersionMatchExpr( final Object versionable,
                             final String expr )
    
        throws CoreException
        
    {
        this( (Versionable) versionable, expr );
    }
    
    public VersionMatchExpr( final Versionable versionable,
                             final String expr )
    
        throws CoreException
        
    {
        this.versionable = versionable;
        
        final String[] subexprs = expr.split( "," );
        
        for( int i = 0; i < subexprs.length; i++ )
        {
            final String subexpr = subexprs[ i ].trim();
            final AbstractVersionOperator op;
            
            if( subexpr.startsWith( "<=" ) )
            {
                op = new LessThanOrEq( parseVersion( subexpr, 2 ) );
            }
            else if( subexpr.startsWith( "<" ) )
            {
                op = new LessThan( parseVersion( subexpr, 1 ) );
            }
            else if( subexpr.startsWith( ">=" ) )
            {
                op = new GreaterThanOrEq( parseVersion( subexpr, 2 ) );
            }
            else if( subexpr.startsWith( ">" ) )
            {
                op = new GreaterThan( parseVersion( subexpr, 1 ) );
            }
            else
            {
                op = new Equals( parseVersion( subexpr, 0 ) );
            }
            
            this.subexprs.add( op );
        }
    }
    
    private IVersion parseVersion( final String str,
                                   final int offset )
    
        throws CoreException
        
    {
        final String verstr = str.substring( offset ).trim();
        
        if( ! this.versionable.hasVersion( verstr ) )
        {
            final String msg 
                = this.versionable.createVersionNotFoundErrMsg( verstr );
            
            final IStatus st = FacetCorePlugin.createErrorStatus( msg );
            
            throw new CoreException( st );
        }
        else
        {
            return this.versionable.getVersionInternal( verstr );
        }
    }
    
    public boolean evaluate( final IVersion ver )
    
        throws CoreException
        
    {
        for( Iterator itr = this.subexprs.iterator(); itr.hasNext(); )
        {
            if( ( (AbstractVersionOperator) itr.next() ).evaluate( ver ) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isSingleVersionMatch()
    {
        return this.subexprs.size() == 1 && 
               this.subexprs.get( 0 ) instanceof Equals;
    }

    public boolean isSimpleAllowNewer()
    {
        return this.subexprs.size() == 1 && 
               this.subexprs.get( 0 ) instanceof GreaterThanOrEq;
    }
    
    public String getFirstVersion()
    {
        return ( (AbstractVersionOperator) this.subexprs.get( 0 ) ).param.getVersionString();
    }
    
    public String toString()
    {
        final StringBuffer buf = new StringBuffer();
        
        for( Iterator itr = subexprs.iterator(); itr.hasNext(); )
        {
            if( buf.length() > 0 ) buf.append( ',' );
            buf.append( itr.next().toString() );
        }
        
        return buf.toString();
    }
    
    private static abstract class AbstractVersionOperator
    {
        protected final IVersion param;
        
        public AbstractVersionOperator( final IVersion param )
        {
            this.param = param;
        }
        
        public boolean evaluate( final IVersion fv )
        
            throws CoreException
            
        {
            final Comparator comp 
                = this.param.getVersionable().getVersionComparator();
            
            final int result
                = comp.compare( fv.getVersionString(), 
                                param.getVersionString() );
            
            return evaluate( result );
        }
        
        protected abstract boolean evaluate( int result );
    }
    
    private static final class Equals
    
        extends AbstractVersionOperator
        
    {
        public Equals( final IVersion param )
        {
            super( param );
        }
        
        protected boolean evaluate( final int result )
        {
            return ( result == 0 );
        }
        
        public String toString()
        {
            return this.param.getVersionString();
        }
    }

    private static final class LessThan
    
        extends AbstractVersionOperator
        
    {
        public LessThan( final IVersion param )
        {
            super( param );
        }
        
        protected boolean evaluate( final int result )
        {
            return ( result < 0 );
        }
        
        public String toString()
        {
            return "<" + this.param.getVersionString();
        }
    }

    private static final class LessThanOrEq
    
        extends AbstractVersionOperator
        
    {
        public LessThanOrEq( final IVersion param )
        {
            super( param );
        }
        
        protected boolean evaluate( final int result )
        {
            return ( result <= 0 );
        }
        
        public String toString()
        {
            return "<=" + this.param.getVersionString();
        }
    }

    private static final class GreaterThan
    
        extends AbstractVersionOperator
        
    {
        public GreaterThan( final IVersion param )
        {
            super( param );
        }
        
        protected boolean evaluate( final int result )
        {
            return ( result > 0 );
        }
        
        public String toString()
        {
            return ">" + this.param.getVersionString();
        }
    }

    private static final class GreaterThanOrEq
    
        extends AbstractVersionOperator
        
    {
        public GreaterThanOrEq( final IVersion param )
        {
            super( param );
        }
        
        protected boolean evaluate( final int result )
        {
            return ( result >= 0 );
        }
        
        public String toString()
        {
            return ">=" + this.param.getVersionString();
        }
    }

}
