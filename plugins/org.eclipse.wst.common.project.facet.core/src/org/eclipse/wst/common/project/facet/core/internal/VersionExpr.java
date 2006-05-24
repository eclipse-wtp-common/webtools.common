/******************************************************************************
 * Copyright (c) 2005, 2006 BEA Systems, Inc.
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
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IVersionExpr;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class VersionExpr

    implements IVersionExpr
    
{
    private static final int SM1_START = 0;
    private static final int SM1_PARSING_START_VERSION = 1;
    private static final int SM1_PARSING_END_VERSION = 2;
    private static final int SM1_FINISHED_RANGE_INCLUSIVE = 3;
    private static final int SM1_FINISHED_RANGE_EXCLUSIVE = 4;
    
    private static final int SM2_VERSION_START = 0;
    private static final int SM2_VERSION_CONTINUING = 1;
    private static final int SM2_ESCAPE = 2;
    
    private final Versionable versionable;
    private final Comparator comparator;
    private final List subexprs;
    private final String usedInPlugin;
    
    public VersionExpr( final Object versionable,
                        final String expr,
                        final String usedInPlugin )
    
        throws CoreException
        
    {
        this( (Versionable) versionable, expr, usedInPlugin );
    }
    
    public VersionExpr( final Versionable versionable,
                        final String expr,
                        final String usedInPlugin )
    
        throws CoreException
        
    {
        this.versionable = versionable;
        this.comparator = versionable.getVersionComparator();
        this.subexprs = new ArrayList();
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
    
    private String parseVersion( final String str )
    
        throws CoreException
        
    {
        if( ! this.versionable.hasVersion( str ) )
        {
            final String msg 
                = this.versionable.createVersionNotFoundErrMsg( str );
            
            final IStatus st = FacetCorePlugin.createErrorStatus( msg );
            
            throw new CoreException( st );
        }
        else
        {
            return this.versionable.getVersionInternal( str ).getVersionString();
        }
    }
    
    public boolean evaluate( final IVersion ver )
    
        throws CoreException
        
    {
        return evaluate( ver.getVersionString() );
    }
    
    public boolean evaluate( final String ver )
    {
        for( Iterator itr = this.subexprs.iterator(); itr.hasNext(); )
        {
            if( ( (Range) itr.next() ).evaluate( ver ) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isSingleVersionMatch()
    {
        if( this.subexprs.size() == 1 )
        {
            final Range range = (Range) this.subexprs.get( 0 );
            
            if( range.startVersion == range.endVersion &&
                range.includesStartVersion == range.includesEndVersion == true )
            {
                return true;
            }
        }
        
        return false;
    }

    public boolean isSimpleAllowNewer()
    {
        if( this.subexprs.size() == 1 )
        {
            final Range range = (Range) this.subexprs.get( 0 );
            
            if( range.startVersion != null && range.endVersion == null &&
                range.includesStartVersion  )
            {
                return true;
            }
        }
        
        return false;
    }
    
    public String getFirstVersion()
    {
        if( isSingleVersionMatch() || isSimpleAllowNewer() )
        {
            final Range range = (Range) this.subexprs.get( 0 );
            return range.startVersion;
        }
        else
        {
            throw new IllegalStateException();
        }
    }
    
    public String toString()
    {
        final StringBuffer buf = new StringBuffer();
        
        for( Iterator itr = this.subexprs.iterator(); itr.hasNext(); )
        {
            if( buf.length() > 0 ) buf.append( ',' );
            buf.append( itr.next().toString() );
        }
        
        return buf.toString();
    }
    
    public String toDisplayString()
    {
        if( this.subexprs.size() == 1 )
        {
            final Range r = (Range) this.subexprs.get( 0 );
            
            if( r.isSingleVersion() )
            {
                return r.startVersion;
            }
            else if( r.endVersion == null && r.includesStartVersion )
            {
                return NLS.bind( Resources.versionOrNewer, r.startVersion );
            }
        }
        else
        {
            boolean onlySingleVersions = true;
            
            for( Iterator itr = this.subexprs.iterator(); itr.hasNext(); )
            {
                if( ! ( (Range) itr.next() ).isSingleVersion() )
                {
                    onlySingleVersions = false;
                    break;
                }
            }
            
            if( onlySingleVersions )
            {
                final StringBuffer buf = new StringBuffer();
                
                for( Iterator itr = this.subexprs.iterator(); itr.hasNext(); )
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
                    
                    buf.append( r.startVersion );
                }
                
                return buf.toString();
            }
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
    
    private final class Range
    {
        public String startVersion = null;
        public boolean includesStartVersion = false;
        public String endVersion = null;
        public boolean includesEndVersion = false;
        
        public boolean isSingleVersion()
        {
            return this.startVersion.equals( this.endVersion ) &&
                   this.includesStartVersion == this.includesEndVersion == true;
        }
        
        public boolean evaluate( final String version )
        {
            final Comparator comp = VersionExpr.this.comparator;
        
            if( this.startVersion != null )
            {
                final int res = comp.compare( version, this.startVersion ); 
                
                if( ! ( res > 0 || ( res == 0 && this.includesStartVersion ) ) )
                {
                    return false;
                }
            }
            
            if( this.endVersion != null )
            {
                final int res = comp.compare( version, this.endVersion );
                
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
                return this.startVersion;
            }
            else
            {
                final StringBuffer buf = new StringBuffer();
                
                if( this.startVersion != null )
                {
                    buf.append( this.includesStartVersion ? '[' : '(' );
                    buf.append( this.startVersion );
                }
                
                if( this.endVersion != null )
                {
                    if( buf.length() != 0 )
                    {
                        buf.append( '-' );
                    }
                    
                    buf.append( this.endVersion );
                    buf.append( this.includesEndVersion ? ']' : ')' );
                }
                
                return buf.toString();
            }
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
