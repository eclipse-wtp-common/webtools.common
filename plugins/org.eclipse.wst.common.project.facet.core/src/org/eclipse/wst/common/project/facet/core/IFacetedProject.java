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

package org.eclipse.wst.common.project.facet.core;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * This interface is used for manipulating the set of facets installed on a
 * project. Use {@see ProjectFacetsManager#create(IProject)} to get an instance 
 * of this interface.
 *  
 * <p><i>This class is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being 
 * made available at this early stage to solicit feedback from pioneering 
 * adopters on the understanding that any code that uses this API will almost 
 * certainly be broken (repeatedly) as the API evolves.</i></p>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IFacetedProject 
{
    /**
     * Represents a single action such as installing or uninstalling a project
     * facet.
     *  
     * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
     */
    
    public static final class Action
    {
        /**
         * The action type enumeration.
         *  
         * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
         */
        
        public static final class Type
        {
            public static final Type INSTALL = new Type( "INSTALL" );
            public static final Type UNINSTALL = new Type( "UNINSTALL" );
            
            public static final Type VERSION_CHANGE 
                = new Type( "VERSION_CHANGE" );
            
            private final String code;
            
            private Type( final String code )
            {
                this.code = code;
            }
            
            public String toString()
            {
                return this.code;
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
            return this.type.toString() + "[" + this.fv.toString() + "]";
        }
    }
    
    IProject getProject();
    
    /**
     * Returns the set of project facets currently installed on this project.
     * 
     * @return the set of project facets currently installed on this project 
     *   (element type: {@see IProjectFacetVersion})
     */
    
    Set getProjectFacets();
    
    /**
     * Determines whether any version of the specified project facet is 
     * installed on this project.
     * 
     * @param f the project facet descriptor
     * @return <code>true</code> if any version of the specified project facet 
     *   is installed on this project, <code>false</code> otherwise
     */
    
    boolean hasProjectFacet( IProjectFacet f );
    
    /**
     * Determines whether the specfied project facet version is installed on 
     * this project.
     * 
     * @param fv the project facet version descriptor
     * @return <code>true</code> if the specified project facet version is 
     *   installed on this project, <code>false</code> otherwise
     */
    
    boolean hasProjectFacet( IProjectFacetVersion fv );
    
    IProjectFacetVersion getInstalledVersion( IProjectFacet f );
    
    /**
     * Installs a project facet on this project. This method is equivalent to 
     * calling the {@see #modify(Set, IProgressMonitor)} method with a single 
     * install action.
     * 
     * @param fv the descriptor of the project facet version that should be 
     *   installed
     * @param config the associated configuration object, or <code>null</code>
     * @param monitor the progress monitor
     * @throws CoreException if anything goes wrong during install
     */
    
    void installProjectFacet( IProjectFacetVersion fv,
                              Object config,
                              IProgressMonitor monitor)
    
        throws CoreException;
    
    /**
     * Uninstalls a project facet from this project. This method is equivalent 
     * to calling the {@see #modify(Set, IProgressMonitor)} method with a single
     * uninstall action.
     * 
     * @param fv the descriptor of the project facet version that should be 
     *   uninstalled
     * @param config the associated configuration object, or <code>null</code>
     * @param monitor the progress monitor
     * @throws CoreException if anything goes wrong during uninstall
     */
    
    void uninstallProjectFacet( IProjectFacetVersion fv,
                                Object config,
                                IProgressMonitor monitor )
    
        throws CoreException;
    
    /**
     * Modifies the set of project facets installed on this project by 
     * performing a series of actions such as install and uninstall. 
     * 
     * @param actions the set of actions to apply to the project
     * @param monitor the progress monitor
     * @throws CoreException if anything goes wrong while applying actions
     */
    
    void modify( Set actions,
                 IProgressMonitor monitor )
    
        throws CoreException;
    
    /**
     * Returns the set of fixed project facets for this project. Fixed facets 
     * cannot be uninstalled, but the installed version can be changed.
     * 
     * @return the set of fixed project facets for this project (element type:
     *   {@see IProjectFacet})
     */
    
    Set getFixedProjectFacets();
    
    /**
     * Sets the set of fixed project facets for this project. Fixed facets 
     * cannot be uninstalled, but the installed version can be changed.
     * 
     * @param facets the set of project facets to mark as fixed (element type:
     *   {@see IProjectFacet})
     * @throws CoreException if failed while updating the set of fixed project
     *   facets
     */

    void setFixedProjectFacets( Set facets )
    
        throws CoreException;
    
    IRuntime getRuntime();
    
    void setRuntime( IRuntime runtime,
                     IProgressMonitor monitor )
    
        throws CoreException;
    
}
