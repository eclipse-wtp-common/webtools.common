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

import java.text.MessageFormat;
import java.util.Arrays;

import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ValidationProblem 

    extends Status
    
{
    public static final class Type
    {
        public static final Type REQUIRES_EXACT 
            = new Type( Resources.requiresExact );

        public static final Type REQUIRES_ALLOW_NEWER 
            = new Type( Resources.requiresAllowNewer );
    
        public static final Type REQUIRES_EXPR 
            = new Type( Resources.requiresExpr );
        
        public static final Type CONFLICTS 
            = new Type( Resources.oneof );
        
        public static final Type COMPLEX 
            = new Type( Resources.complex );
        
        public static final Type INSTALL_NOT_SUPPORTED 
            = new Type( Resources.installNotSupported );
        
        public static final Type UNINSTALL_NOT_SUPPORTED 
            = new Type( Resources.uninstallNotSupported );
        
        public static final Type VERSION_CHANGE_NOT_SUPPORTED 
            = new Type( Resources.versionChangeNotSupported );

        public static final Type MULTIPLE_ACTIONS_NOT_SUPPORTED 
            = new Type( Resources.multipleActionsNotSupported );

        public static final Type FACET_ALREADY_INSTALLED 
            = new Type( Resources.facetAlreadyInstalled );

        public static final Type CANNOT_UNINSTALL 
            = new Type( Resources.cannotUninstall );

        public static final Type CANNOT_CHANGE_VERSION
            = new Type( Resources.cannotChangeVersion );
        
        public final String msg;
        
        private Type( final String msg ) 
        {
            this.msg = msg;
        }

        private String getMessageTemplate()
        {
            return this.msg;
        }
    }
    
    private final Type type;
    private final Object[] params;
    
    public ValidationProblem( final Type type,
                              final Object[] params )
    {
        super( ERROR, FacetCorePlugin.PLUGIN_ID, 0, format( type, params ), 
               null );
        
        this.type = type;
        this.params = params;
    }
    
    public ValidationProblem( final Type type )
    {
        this( type, new Object[ 0 ] );
    }

    public ValidationProblem( final Type type,
                              final Object param )
    {
        this( type, new Object[] { param } );
    }
    
    public ValidationProblem( final Type type,
                              final Object param1,
                              final Object param2 )
    {
        this( type, new Object[] { param1, param2 } );
    }

    public ValidationProblem( final Type type,
                              final Object param1,
                              final Object param2,
                              final Object param3 )
    {
        this( type, new Object[] { param1, param2, param3 } );
    }
    
    public Type getType()
    {
        return this.type;
    }
    
    public Object[] getParameters()
    {
        return this.params;
    }

    public boolean equals( final Object obj )
    {
        if( ! ( obj instanceof ValidationProblem ) )
        {
            return false;
        }
        else
        {
            final ValidationProblem prob = (ValidationProblem) obj;
            
            return this.type.equals( prob.type ) &&
                   Arrays.equals( this.params, prob.params );
        }
    }
    
    public int hashCode()
    {
    	int hash = 0;
        
    	for( int i = 0; i < this.params.length; i++ ) 
        {
			hash = hash ^ this.params[ i ].hashCode();
		}
        
        return this.type.hashCode() ^ hash;
    }
    
    public String toString()
    {
        return getMessage();
    }
    
    private static String format( final Type type,
                                  final Object[] params )
    {
        return MessageFormat.format( type.getMessageTemplate(), params );
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String requiresExact;
        public static String requiresAllowNewer;
        public static String requiresExpr;
        public static String oneof;
        public static String complex;
        public static String installNotSupported;
        public static String uninstallNotSupported;
        public static String versionChangeNotSupported;
        public static String multipleActionsNotSupported;
        public static String facetAlreadyInstalled;
        public static String cannotUninstall;
        public static String cannotChangeVersion;
        
        static
        {
            initializeMessages( ValidationProblem.class.getName(), 
                                Resources.class );
        }
    }

}
