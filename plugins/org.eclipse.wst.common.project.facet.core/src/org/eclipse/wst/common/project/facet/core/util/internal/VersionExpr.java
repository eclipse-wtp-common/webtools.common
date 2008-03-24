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

package org.eclipse.wst.common.project.facet.core.util.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IVersion;
import org.eclipse.wst.common.project.facet.core.IVersionExpr;
import org.eclipse.wst.common.project.facet.core.internal.FacetCorePlugin;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class VersionExpr<T extends IVersion>

    implements IVersionExpr
    
{
    private static final int SM1_START = 0;
    private static final int SM1_PARSING_START_VERSION = 1;
    private static final int SM1_PARSING_END_VERSION = 2;
    private static final int SM1_FINISHED_RANGE_INCLUSIVE = 3;
    private static final int SM1_FINISHED_RANGE_EXCLUSIVE = 4;
    private static final int SM1_PARSING_WILDCARD = 5;
    
    private static final int SM2_VERSION_START = 0;
    private static final int SM2_VERSION_CONTINUING = 1;
    private static final int SM2_ESCAPE = 2;
    
    private final Versionable<T> versionable;
    private final List<ISubExpr> subexprs;
    private final String usedInPlugin;
    
    @SuppressWarnings( "unchecked" )
    public VersionExpr( final Object versionable,
                        final String expr,
                        final String usedInPlugin )
    
        throws CoreException
        
    {
        this( (Versionable<T>) versionable, expr, usedInPlugin );
    }
    
    public VersionExpr( final Versionable<T> versionable,
                        final String expr,
                        final String usedInPlugin )
    
        throws CoreException
        
    {
        this.versionable = versionable;
        this.subexprs = new ArrayList<ISubExpr>();
        this.usedInPlugin = usedInPlugin;
        
        int state = SM1_START;
        Range range = null;
        boolean usingDeprecatedSyntax = false;
        
        for( MutableInteger position = new MutableInteger(); 
             position.value < expr.length(); position.value++ )
        {
            final char ch = expr.charAt( position.value );
            
            switch( state )
            {
                case SM1_START:
                {
                    if( ch == '[' )
                    {
                        range = new Range();
                        range.includesStartVersion = true;
                        state = SM1_PARSING_START_VERSION;
                    }
                    else if( ch == '(' )
                    {
                        range = new Range();
                        range.includesStartVersion = false;
                        state = SM1_PARSING_START_VERSION;
                    }
                    else if( ch == '*' )
                    {
                        this.subexprs.add( new Wildcard() );
                        state = SM1_PARSING_WILDCARD;
                    }
                    else if( ch == ' ' || ch == ',' )
                    {
                        // ignore
                    }
                    else
                    {
                        final StringBuffer buf = new StringBuffer();
                        final int exitState = parseVersion( expr, position, buf );
                        
                        if( exitState == SM1_START )
                        {
                            final String vstr = buf.toString();
                            final Range r = new Range();
                            
                            if( vstr.startsWith( ">=" ) ) //$NON-NLS-1$
                            {
                                r.startVersion = parseVersion( vstr.substring( 2 ) );
                                r.includesStartVersion = true;
                                usingDeprecatedSyntax = true;
                            }
                            else if( vstr.startsWith( ">" ) ) //$NON-NLS-1$
                            {
                                r.startVersion = parseVersion( vstr.substring( 1 ) );
                                r.includesStartVersion = false;
                                usingDeprecatedSyntax = true;
                            }
                            else if( vstr.startsWith( "<=" ) ) //$NON-NLS-1$
                            {
                                r.endVersion = parseVersion( vstr.substring( 2 ) );
                                r.includesEndVersion = true;
                                usingDeprecatedSyntax = true;
                            }
                            else if( vstr.startsWith( "<" ) ) //$NON-NLS-1$
                            {
                                r.endVersion = parseVersion( vstr.substring( 1 ) );
                                r.includesEndVersion = false;
                                usingDeprecatedSyntax = true;
                            }
                            else
                            {
                                r.startVersion = parseVersion( vstr );
                                r.includesStartVersion = true;
                                r.endVersion = r.startVersion;
                                r.includesEndVersion = true;
                            }
                            
                            this.subexprs.add( r );
                        }
                        else if( exitState == SM1_FINISHED_RANGE_INCLUSIVE ||
                                 exitState == SM1_FINISHED_RANGE_EXCLUSIVE )
                        {
                            range = new Range();
                            range.endVersion = parseVersion( buf.toString() );
                            state = exitState;
                            position.value--;
                        }
                        else
                        {
                            throw createInvalidVersionExprException( expr );
                        }
                    }
                    
                    break;
                }
                case SM1_PARSING_START_VERSION:
                {
                    final StringBuffer buf = new StringBuffer();
                    final int exitState = parseVersion( expr, position, buf );

                    if( exitState == SM1_START )
                    {
                        range.startVersion = parseVersion( buf.toString() );
                        this.subexprs.add( range );
                        range = null;
                        state = exitState;
                    }
                    else if( exitState == SM1_PARSING_END_VERSION )
                    {
                        range.startVersion = parseVersion( buf.toString() );
                        state = exitState;
                    }
                    else
                    {
                        throw createInvalidVersionExprException( expr );
                    }
                    
                    break;
                }
                case SM1_PARSING_END_VERSION:
                {
                    final StringBuffer buf = new StringBuffer();
                    final int exitState = parseVersion( expr, position, buf );
                    
                    if( exitState == SM1_FINISHED_RANGE_INCLUSIVE ||
                        exitState == SM1_FINISHED_RANGE_EXCLUSIVE )
                    {
                        range.endVersion = parseVersion( buf.toString() );
                        state = exitState;
                        position.value--;
                    }
                    else
                    {
                        throw createInvalidVersionExprException( expr );
                    }
                    
                    break;
                }
                case SM1_FINISHED_RANGE_INCLUSIVE:
                case SM1_FINISHED_RANGE_EXCLUSIVE:
                {
                    range.includesEndVersion 
                        = ( state == SM1_FINISHED_RANGE_INCLUSIVE );
                    
                    this.subexprs.add( range );
                    range = null;
                    
                    state = SM1_START;
                    
                    break;
                }
                case SM1_PARSING_WILDCARD:
                {
                    if( ch == ' ' )
                    {
                        // ignore
                    }
                    else if( ch == ',' )
                    {
                        state = SM1_START;
                    }
                    else
                    {
                        throw createInvalidVersionExprException( expr );
                    }
                }
                default:
                {
                    throw new IllegalStateException();
                }
            }
        }
        
        // Report the use of deprecated syntax.
        
        if( usingDeprecatedSyntax )
        {
            final String msg;
            
            if( this.usedInPlugin == null )
            {
                msg = Resources.deprecatedSyntaxNoPlugin;
            }
            else
            {
                msg = NLS.bind( Resources.deprecatedSyntax, this.usedInPlugin );
            }
            
            FacetCorePlugin.logWarning( msg, true );
        }
    }
    
    private int parseVersion( final String str,
                              final MutableInteger position,
                              final StringBuffer version )
    
        throws CoreException
        
    {
        int localState = SM2_VERSION_START;
        int exitState = -1;
        
        for( ; exitState == -1 && position.value < str.length(); 
             position.value++ )
        {
            final char ch = str.charAt( position.value );
            
            switch( localState )
            {
                case SM2_VERSION_START:
                {
                    if( ch == '[' || ch == '(' || ch == ']' || ch == ')' ||
                        ch == '-' || ch == ',' )
                    {
                        throw createInvalidVersionExprException( str );
                    }
                    else if( ch == '\\' )
                    {
                        localState = SM2_ESCAPE;
                    }
                    else if( ch == ' ' )
                    {
                        // ignore
                    }
                    else
                    {
                        version.append( ch );
                        localState = SM2_VERSION_CONTINUING;
                    }
                    
                    break;
                }
                case SM2_VERSION_CONTINUING:
                {
                    if( ch == '[' || ch == '(' )
                    {
                        throw createInvalidVersionExprException( str );
                    }
                    else if( ch == '\\' )
                    {
                        localState = SM2_ESCAPE;
                    }
                    else if( ch == ',' )
                    {
                        exitState = SM1_START;
                    }
                    else if( ch == '-' )
                    {
                        exitState = SM1_PARSING_END_VERSION;
                    }
                    else if( ch == ']' )
                    {
                        exitState = SM1_FINISHED_RANGE_INCLUSIVE;
                    }
                    else if( ch == ')' )
                    {
                        exitState = SM1_FINISHED_RANGE_EXCLUSIVE;
                    }
                    else if( ch == ' ' )
                    {
                        // ignore
                    }
                    else
                    {
                        version.append( ch );
                    }
                    
                    break;
                }
                case SM2_ESCAPE:
                {
                    version.append( ch );
                    break;
                }
                default:
                {
                    throw new IllegalStateException();
                }
            }
        }
        
        position.value--;
        
        if( exitState != -1 )
        {
            return exitState;
        }
        else
        {
            if( localState == SM2_VERSION_CONTINUING )
            {
                // Expected end of input.
                
                return SM1_START;
            }
            else
            {
                // Unexpected end of input.
                
                throw createInvalidVersionExprException( str );
            }
        }
    }
    
    private IVersion parseVersion( final String str )
    {
        if( this.versionable.hasVersion( str ) )
        {
            return this.versionable.getVersion( str );
        }
        else
        {
            return new UnknownVersion<T>( this.versionable, str );
        }
    }
    
    public boolean check( final IVersion ver )
    {
        for( ISubExpr subexpr : this.subexprs )
        {
            if( subexpr.evaluate( ver ) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean evaluate( final String ver )
    {
        return check( parseVersion( ver ) );
    }
    
    public boolean isSingleVersionMatch()
    {
        if( this.subexprs.size() == 1 )
        {
            final ISubExpr subExpr = this.subexprs.get( 0 );
            
            if( subExpr instanceof Range )
            {
                final Range range = (Range) subExpr;
                
                if( range.startVersion.equals( range.endVersion ) &&
                    range.includesStartVersion == range.includesEndVersion == true )
                {
                    return true;
                }
            }
        }
        
        return false;
    }

    public boolean isSimpleAllowNewer()
    {
        if( this.subexprs.size() == 1 )
        {
            final ISubExpr subExpr = this.subexprs.get( 0 );
            
            if( subExpr instanceof Range )
            {
                final Range range = (Range) subExpr;
            
                if( range.startVersion != null && range.endVersion == null &&
                    range.includesStartVersion  )
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean isWildcard()
    {
        return this.subexprs.size() == 1 && 
               this.subexprs.get( 0 ) instanceof Wildcard;
    }
    
    public String getFirstVersion()
    {
        if( isSingleVersionMatch() || isSimpleAllowNewer() )
        {
            final Range range = (Range) this.subexprs.get( 0 );
            return range.startVersion.getVersionString();
        }
        else
        {
            throw new IllegalStateException();
        }
    }
    
    public String toString()
    {
        final StringBuffer buf = new StringBuffer();
        
        for( ISubExpr subexpr : this.subexprs )
        {
            if( buf.length() > 0 ) buf.append( ',' );
            buf.append( subexpr.toString() );
        }
        
        return buf.toString();
    }
    
    public String toDisplayString()
    {
        if( this.subexprs.size() == 1 )
        {
            final ISubExpr subexpr = this.subexprs.get( 0 );
            
            if( subexpr instanceof Range )
            {
                final Range r = (Range) subexpr;
                
                if( r.isSingleVersion() )
                {
                    return r.startVersion.getVersionString();
                }
                else if( r.endVersion == null && r.includesStartVersion )
                {
                    return NLS.bind( Resources.versionOrNewer, 
                                     r.startVersion.getVersionString() );
                }
            }
        }

        boolean onlySingleVersions = true;
        
        for( ISubExpr subexpr : this.subexprs )
        {
            if( ! ( subexpr instanceof Range ) || 
                ! ( (Range) subexpr ).isSingleVersion() )
            {
                onlySingleVersions = false;
                break;
            }
        }
        
        if( onlySingleVersions )
        {
            final StringBuffer buf = new StringBuffer();

            for( Iterator<ISubExpr> itr = this.subexprs.iterator(); itr.hasNext(); )
            {
                final Range r = (Range) itr.next();
                
                if( buf.length() > 0 )
                {
                    if( itr.hasNext() )
                    {
                        buf.append( ", " ); //$NON-NLS-1$
                    }
                    else
                    {
                        buf.append( " or " ); //$NON-NLS-1$
                    }
                }
                
                buf.append( r.startVersion.getVersionString() );
            }
            
            return buf.toString();
        }
        
        return toString();
    }
    
    private static CoreException createInvalidVersionExprException( final String str )
    {
        final String msg
            = NLS.bind( Resources.invalidVersionExpr, str );
    
        final IStatus st = FacetCorePlugin.createErrorStatus( msg );
    
        return new CoreException( st );
    }
    
    private static interface ISubExpr
    {
        boolean evaluate( IVersion version );
    }
    
    private static final class Range
    
        implements ISubExpr
        
    {
        public IVersion startVersion = null;
        public boolean includesStartVersion = false;
        public IVersion endVersion = null;
        public boolean includesEndVersion = false;
        
        public boolean isSingleVersion()
        {
            return this.startVersion.equals( this.endVersion ) &&
                   this.includesStartVersion == this.includesEndVersion == true;
        }
        
        @SuppressWarnings( "unchecked" )
        public boolean evaluate( final IVersion version )
        {
            if( this.startVersion != null )
            {
                final int res = version.compareTo( this.startVersion );
                
                if( ! ( res > 0 || ( res == 0 && this.includesStartVersion ) ) )
                {
                    return false;
                }
            }
            
            if( this.endVersion != null )
            {
                final int res = version.compareTo( this.endVersion );
                
                if( ! ( res < 0 || ( res == 0 && this.includesEndVersion ) ) )
                {
                    return false;
                }
            }
            
            return true;
        }
        
        public String toString()
        {
            if( this.startVersion.equals( this.endVersion ) &&
                this.includesStartVersion == this.includesEndVersion == true )
            {
                return this.startVersion.getVersionString();
            }
            else
            {
                final StringBuffer buf = new StringBuffer();
                
                if( this.startVersion != null )
                {
                    buf.append( this.includesStartVersion ? '[' : '(' );
                    buf.append( this.startVersion.getVersionString() );
                }
                
                if( this.endVersion != null )
                {
                    if( buf.length() != 0 )
                    {
                        buf.append( '-' );
                    }
                    
                    buf.append( this.endVersion.getVersionString() );
                    buf.append( this.includesEndVersion ? ']' : ')' );
                }
                
                return buf.toString();
            }
        }
    }
    
    private static final class Wildcard
    
        implements ISubExpr
        
    {
        public boolean evaluate( final IVersion version )
        {
            return true;
        }
        
        public String toString()
        {
            return IVersionExpr.WILDCARD_SYMBOL;
        }
    }
    
    private static final class MutableInteger
    {
        public int value = 0;
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String invalidVersionExpr;
        public static String deprecatedSyntax;
        public static String deprecatedSyntaxNoPlugin;
        public static String versionOrNewer;
        
        static
        {
            initializeMessages( VersionExpr.class.getName(), 
                                Resources.class );
        }
    }

}
