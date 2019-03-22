/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.runtime;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * The interface implemented by extensions wishing to expose runtimes defined
 * through other means to the project facets framework.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IRuntimeBridge
{
    /**
     * Returns the set of names for runtimes that this bridge wants to export.
     * The system will try to accommodate these name choices, but may have to
     * disambiguate names due to collisions. However, even if the runtime name
     * is changed, the name that will be passed into the {@link #bridge(String)}
     * call will be the original name provided by this method call. 
     * 
     * @return the set of names for runtimes that this bridge wants to export
     * @throws CoreException if failed while bridging
     */
    
    Set<String> getExportedRuntimeNames()
        
        throws CoreException;
    
    /**
     * Returns a stub that represents the bridged runtime. The system will
     * wrap this stub and expose it to the clients through the {@link IRuntime}
     * interface.
     * 
     * @param name the name of the bridged runtime (as returned by the
     *   {@link #getExportedRuntimeNames()}) method
     * @return a stub that represents the bridged runtime
     * @throws CoreException if failed while bridging
     */
    
    IStub bridge( String name )
    
        throws CoreException;
    
    /**
     * Represents a single bridged runtime. The system will wrap this interface
     * and expose it to clients as {@link IRuntime}. All relevant calls will be
     * delegated to this interface.
     * 
     * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
     */

    public interface IStub
    {
        /**
         * Returns the runtime components that comprise this runtime. Note that 
         * the order is important since for some operations components are 
         * consulted in order and the first one capable of performing the
         * operation wins.
         *  
         * @return the runtime components that comprise this runtime
         */
        
        List<IRuntimeComponent> getRuntimeComponents();
        
        /**
         * Returns the properties associated with this runtime component. The
         * contents will vary depending on how the runtime was created and 
         * what component types/versions it's comprised of.
         * 
         * @return the properties associated with this runtime
         */
        
        Map<String,String> getProperties();
    }
    
    /**
     * Represents a single bridged runtime. The system will wrap this interface
     * and expose it to clients as {@link IRuntime}. All relevant calls will be
     * delegated to this interface.
     * 
     * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
     * @since 3.0
     */

    public abstract class Stub
    
        implements IStub
        
    {
        /**
         * Returns the runtime components that comprise this runtime. Note that 
         * the order is important since for some operations components are 
         * consulted in order and the first one capable of performing the
         * operation wins.
         *  
         * @return the runtime components that comprise this runtime
         * @since 3.0
         */
        
        public abstract List<IRuntimeComponent> getRuntimeComponents();
        
        /**
         * Returns the properties associated with this runtime component. The
         * contents will vary depending on how the runtime was created and 
         * what component types/versions it's comprised of.
         * 
         * @return the properties associated with this runtime
         * @since 3.0
         */
        
        public abstract Map<String,String> getProperties();
 
        /**
         * @since 3.0
         */
        
        public IStatus validate( final IProgressMonitor monitor )
        {
            return Status.OK_STATUS;
        }
    }
    
}
