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

package org.eclipse.wst.common.project.facet.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * This interface is used for manipulating the set of facets installed on a
 * project. Use {@link ProjectFacetsManager#create(IProject)} to get an instance 
 * of this interface.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IFacetedProject 

    extends IFacetedProjectBase
    
{
    /**
     * Represents a single action such as installing or uninstalling a project
     * facet.
     */
    
    static final class Action
    {
        /**
         * The action type enumeration.
         */
        
        public static final class Type
        {
            private static final Map<String,Type> items = new HashMap<String,Type>();
            public static final Type INSTALL = new Type( "INSTALL" ); //$NON-NLS-1$
            public static final Type UNINSTALL = new Type( "UNINSTALL" ); //$NON-NLS-1$
            public static final Type VERSION_CHANGE = new Type( "VERSION_CHANGE" ); //$NON-NLS-1$
            
            static
            {
                // Backwards compatibility.
                
                items.put( "install", INSTALL ); //$NON-NLS-1$
                items.put( "uninstall", UNINSTALL ); //$NON-NLS-1$
                items.put( "version-change", VERSION_CHANGE ); //$NON-NLS-1$
            }
            
            private final String name;
            
            private Type( final String name )
            {
                this.name = name;
                items.put( name, this );
            }
            
            public static Type valueOf( final String name )
            {
                return items.get( name );
            }
            
            public String name()
            {
                return this.name;
            }
            
            public String toString()
            {
                return this.name;
            }
        }
        
        private final Type type;
        private final IProjectFacetVersion fv;
        private final Object config;
        
        /**
         * Creates a new action.
         * 
         * @param type action type
         * @param fv the the project facet version
         * @param config the configuration object, or <code>null</code>
         */
        
        public Action( final Type type,
                       final IProjectFacetVersion fv,
                       final Object config )
        {
            if( type == null || fv == null )
            {
                throw new IllegalArgumentException();
            }
            
            this.type = type;
            this.fv = fv;
            this.config = config;
        }
        
        /**
         * Returns the action type
         * 
         * @return the action type
         */
        
        public Type getType()
        {
            return this.type;
        }
        
        /**
         * Returns the descriptor for the project facet version that this 
         * action will be manipulating.
         * 
         * @return the descriptor for the project facet version that this action 
         *   will be manipulating
         */
        
        public IProjectFacetVersion getProjectFacetVersion()
        {
            return this.fv;
        }
        
        /**
         * Returns the configuration object associated with this action, if any.
         * 
         * @return the configuration object associated with this action, or
         *   <code>null</code>
         */
        
        public Object getConfig()
        {
            return this.config;
        }
        
        public boolean equals( final Object obj )
        {
            if( ! ( obj instanceof Action ) )
            {
                return false;
            }
            else
            {
                final Action action = (Action) obj;
                
                return this.type == action.type && 
                       this.fv.equals( action.fv );
            }
        }
        
        public int hashCode()
        {
            return this.type.hashCode() ^ this.fv.hashCode();
        }
        
        public String toString()
        {
            final StringBuffer buf = new StringBuffer();
            
            buf.append( this.type.toString() );
            buf.append( '[' );
            buf.append( this.fv.getProjectFacet().getId() );
            buf.append( ' ' );
            buf.append( this.fv.getVersionString() );
            buf.append( ']' );
            
            return buf.toString();
        }
    }
    
    IProjectFacetVersion getInstalledVersion( IProjectFacet f );
    
    /**
     * <p>Installs a project facet on this project. This method is equivalent to 
     * calling the {@link #modify(Set, IProgressMonitor)} method with a single 
     * install action.</p>
     * 
     * <p>This method should not be called from the UI thread as it is long-
     * running and may trigger resource change events. Although this framework
     * is safe, there is no guarantee that other bundles are UI-safe and the
     * risk of UI deadlock is high.</p>
     * 
     * @param fv the descriptor of the project facet version that should be 
     *   installed
     * @param config the associated configuration object, or <code>null</code>
     * @param monitor a progress monitor, or null if progress reporting and 
     *   cancellation are not desired
     * @throws CoreException if anything goes wrong during install
     */
    
    void installProjectFacet( IProjectFacetVersion fv,
                              Object config,
                              IProgressMonitor monitor)
    
        throws CoreException;
    
    /**
     * <p>Uninstalls a project facet from this project. This method is 
     * equivalent to calling the {@link #modify(Set, IProgressMonitor)} method 
     * with a single uninstall action.</p>
     * 
     * <p>This method should not be called from the UI thread as it is long-
     * running and may trigger resource change events. Although this framework
     * is safe, there is no guarantee that other bundles are UI-safe and the
     * risk of UI deadlock is high.</p>
     * 
     * @param fv the descriptor of the project facet version that should be 
     *   uninstalled
     * @param config the associated configuration object, or <code>null</code>
     * @param monitor a progress monitor, or null if progress reporting and 
     *   cancellation are not desired
     * @throws CoreException if anything goes wrong during uninstall
     */
    
    void uninstallProjectFacet( IProjectFacetVersion fv,
                                Object config,
                                IProgressMonitor monitor )
    
        throws CoreException;
    
    /**
     * <p>Modifies the set of project facets installed on this project by 
     * performing a series of actions such as install and uninstall.</p>
     * 
     * <p>This method should not be called from the UI thread as it is long-
     * running and may trigger resource change events. Although this framework
     * is safe, there is no guarantee that other bundles are UI-safe and the
     * risk of UI deadlock is high.</p>
     * 
     * @param actions the set of actions to apply to the project
     * @param monitor a progress monitor, or null if progress reporting and 
     *   cancellation are not desired
     * @throws CoreException if anything goes wrong while applying actions
     */
    
    void modify( Set<Action> actions,
                 IProgressMonitor monitor )
    
        throws CoreException;
    
    /**
     * Sets the set of fixed project facets for this project. Fixed facets 
     * cannot be uninstalled, but the installed version can be changed.
     * 
     * @param facets the set of project facets to mark as fixed
     * @throws CoreException if failed while updating the set of fixed project
     *   facets
     */

    void setFixedProjectFacets( Set<IProjectFacet> facets )
    
        throws CoreException;
    
    /**
     * 
     * @deprecated use getTargetRuntimes() and getPrimaryRuntime() instead
     */
    
    IRuntime getRuntime();
    
    /**
     * 
     * <p>This method should not be called from the UI thread as it is long-
     * running and may trigger resource change events. Although this framework
     * is safe, there is no guarantee that other bundles are UI-safe and the
     * risk of UI deadlock is high.</p>
     * 
     * @param monitor a progress monitor, or null if progress reporting and 
     *   cancellation are not desired
     * @deprecated use setTargetRuntimes() and setPrimaryRuntime() instead
     */
    
    void setRuntime( IRuntime runtime,
                     IProgressMonitor monitor )
    
        throws CoreException;
    
    /**
     * <p>Sets the runtimes that this project will target. When a project 
     * targets a runtime, the set of facets that can be installed is limited to 
     * those supported by that runtime. When a project targets multiple 
     * runtimes, the set of applicable facets is limited to those supported by 
     * all targeted runtimes.<p>
     * 
     * <p>If the existing primary runtime is <code>null</code> or is not part of 
     * the new set of targeted runtimes, the primary runtime will be reset to 
     * one picked at random from the new set. If the new set is empty, the 
     * primary runtime will be set to <code>null</code>.</p>
     * 
     * <p>This method should not be called from the UI thread as it is long-
     * running and may trigger resource change events. Although this framework
     * is safe, there is no guarantee that other bundles are UI-safe and the
     * risk of UI deadlock is high.</p>
     * 
     * @param runtimes the new set of runtimes to target
     * @param monitor a progress monitor, or <code>null</code> if progress
     *   reporting and cancelation are not desired
     * @throws CoreException if the project contains one or more facets that
     *   are not supported by all of the new runtimes; if failed for any other 
     *   reason
     */
    
    void setTargetedRuntimes( Set<IRuntime> runtimes,
                              IProgressMonitor monitor )
    
        throws CoreException;
    
    /**
     * <p>Adds a new runtime to the set of runtimes targeted by this project.
     * If the set of targeted runtimes has been empty prior to this call, this
     * runtime will become the primary runtime</p>
     *  
     * <p>This method should not be called from the UI thread as it is long-
     * running and may trigger resource change events. Although this framework
     * is safe, there is no guarantee that other bundles are UI-safe and the
     * risk of UI deadlock is high.</p>
     * 
     * @param runtime the runtime
     * @param monitor a progress monitor, or <code>null</code> if progress
     *   reporting and cancelation are not desired
     * @throws CoreException if the project contains one or more facets that
     *   are not supported by this runtime; if failed for any other reason
     */
    
    void addTargetedRuntime( IRuntime runtime,
                             IProgressMonitor monitor )
    
        throws CoreException;
    
    /**
     * <p>Removes a runtime from the set of runtimes targeted by this project.
     * If this runtime has been the primary runtime prior to this call, a new
     * primary will be automatically assigned (unless the list of target
     * runtimes becomes empty, in which case the primary runtime will be set
     * to <code>null</code>).</p>
     * 
     * <p>This method should not be called from the UI thread as it is long-
     * running and may trigger resource change events. Although this framework
     * is safe, there is no guarantee that other bundles are UI-safe and the
     * risk of UI deadlock is high.</p>
     * 
     * @param runtime the runtime to remove
     * @param monitor a progress monitor, or <code>null</code> if progress
     *   reporting and cancelation are not desired
     * @throws CoreException if failed for any reason
     */
    
    void removeTargetedRuntime( IRuntime runtime,
                                IProgressMonitor monitor )
    
        throws CoreException;

    /**
     * <p>Sets the primary target runtime for this project. The new primary has
     * to be one of the runtimes currently targeted by the project.
     * 
     * <p>This method should not be called from the UI thread as it is long-
     * running and may trigger resource change events. Although this framework
     * is safe, there is no guarantee that other bundles are UI-safe and the
     * risk of UI deadlock is high.</p>
     * 
     * @param runtime the new primary runtime
     * @param monitor a progress monitor, or <code>null</code> if progress
     *   reporting and cancelation are not desired
     * @throws CoreException if the primary runtime is not one of the runtimes
     *   currently targeted by the project; if failed for any other reason
     */

    void setPrimaryRuntime( IRuntime runtime,
                            IProgressMonitor monitor )
    
        throws CoreException;
    
    IFacetedProjectWorkingCopy createWorkingCopy();
    
    IMarker createErrorMarker( String message )
    
        throws CoreException;
    
    IMarker createErrorMarker( String type,
                               String message )
    
        throws CoreException;
    
    IMarker createWarningMarker( String message )
    
        throws CoreException;
    
    IMarker createWarningMarker( String type,
                                 String message )
    
        throws CoreException;
    
    /**
     * @deprecated
     */
    
    void addListener( org.eclipse.wst.common.project.facet.core.IFacetedProjectListener listener );
    
    /**
     * @deprecated
     */
    
    void removeListener( org.eclipse.wst.common.project.facet.core.IFacetedProjectListener listener );
    
}
