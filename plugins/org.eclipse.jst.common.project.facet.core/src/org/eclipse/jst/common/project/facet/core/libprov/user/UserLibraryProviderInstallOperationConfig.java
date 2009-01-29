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

package org.eclipse.jst.common.project.facet.core.libprov.user;

import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.createErrorStatus;
import static org.eclipse.jst.common.project.facet.core.internal.FacetedProjectFrameworkJavaPlugin.log;
import static org.eclipse.wst.common.project.facet.core.util.internal.PluginUtil.instantiate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.UserLibrary;
import org.eclipse.jdt.internal.core.UserLibraryManager;
import org.eclipse.jst.common.project.facet.core.internal.ClasspathUtil;
import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderFramework;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderInstallOperationConfig;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectBase;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * The install operation config corresponding to the user-library-provider that uses JDT user library facility
 * for managing libraries. This class can be subclassed by those wishing to extend the base implementation
 * supplied by the framework.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @since 1.4
 */

@SuppressWarnings( "restriction" )

public class UserLibraryProviderInstallOperationConfig

    extends LibraryProviderInstallOperationConfig
    
{
    private static final String CLASS_NAME 
        = UserLibraryProviderInstallOperationConfig.class.getName();
    
    /**
     * The preferences path (in the workspace-level facet preferences) where information about
     * user libraries that were last used for a particular facet are stored. Beneath this path,
     * content is organized by facet version.
     */
    
    public static final String PREFS_LAST_USED_LIBRARIES
        = "user.library.provider/last.used.libraries"; //$NON-NLS-1$

    /**
     * The property corresponding to the list of selected user libraries.
     */
    
    public static final String PROP_LIBRARY_NAMES 
        = CLASS_NAME + ".LIBRARY_NAMES"; //$NON-NLS-1$
    
    private static final String PARAM_VALIDATOR = "validator"; //$NON-NLS-1$
    private static final String PARAM_VALIDATOR_PARAM = "validator.param"; //$NON-NLS-1$

    private List<String> libraryNames;
    private List<String> libraryNamesReadOnly;
    private UserLibraryValidator validator;
    
    /**
     * Constructs the user library provider install operation config.
     */
    
    public UserLibraryProviderInstallOperationConfig()
    {
        this.libraryNames = Collections.emptyList();
        this.libraryNamesReadOnly = Collections.emptyList();
        this.validator = null;
    }

    /**
     * Initializes the operation config. This method is called soon after the provider
     * is instantiated. Extenders can override in order to add to the initialization, but
     * have make sure to forward the init call up the inheritance chain.
     * 
     * @param fpj the faceted project (or a working copy)
     * @param fv the project facet that is making the request for libraries
     * @param provider the library provider (useful if the same operation config class
     *   is re-used between multiple providers)
     */
    
    @Override
    public synchronized void init( final IFacetedProjectBase fproj,
                                   final IProjectFacetVersion fv,
                                   final ILibraryProvider provider )
    {
        super.init( fproj, fv, provider );
        
        final Map<String,String> params = provider.getParams();
        final String validatorClassName = params.get( PARAM_VALIDATOR );
        
        if( validatorClassName != null )
        {
            this.validator = instantiate( provider.getPluginId(), validatorClassName, UserLibraryValidator.class );
            
            if( this.validator != null )
            {
                final List<String> keyClassNames = new ArrayList<String>();
                
                int i = 0; 
                String paramName = PARAM_VALIDATOR_PARAM + "." + String.valueOf( i ); //$NON-NLS-1$
                String paramValue = params.get( paramName );
                
                while( paramValue != null )
                {
                    keyClassNames.add( paramValue );
    
                    i++; 
                    paramName = PARAM_VALIDATOR_PARAM + "." + String.valueOf( i ); //$NON-NLS-1$
                    paramValue = params.get( paramName );
                }
                
                try
                {
                    this.validator.init( keyClassNames );
                }
                catch( Exception e )
                {
                    log( e );
                    this.validator = null;
                }
            }
        }
        
        reset();
    }
    
    /**
     * Returns the list of user libraries that are currently selected. To listen for changes
     * to this list use PROP_LIBRARY_NAMES.
     * 
     * @return the list of user libraries that are currently selected
     */

    public synchronized final List<String> getLibraryNames()
    {
        return this.libraryNamesReadOnly;
    }
    
    /**
     * Sets the list of user libraries. 
     * 
     * @param libraryNames the list of user library names
     */
    
    public synchronized final void setLibraryNames( final List<String> libraryNames )
    {
        final List<String> oldLibraryNames = this.libraryNamesReadOnly;
        this.libraryNames = new ArrayList<String>( libraryNames );
        this.libraryNamesReadOnly = Collections.unmodifiableList( this.libraryNames );
        notifyListeners( PROP_LIBRARY_NAMES, oldLibraryNames, this.libraryNamesReadOnly );
    }
    
    /**
     * Adds a library to the list of selected user libraries.
     * 
     * @param libraryName the name of the library
     */
    
    public synchronized final void addLibraryName( final String libraryName )
    {
        if( ! this.libraryNamesReadOnly.contains( libraryName ) )
        {
            final List<String> oldLibraryNames = this.libraryNamesReadOnly;
            this.libraryNames = new ArrayList<String>( oldLibraryNames );
            this.libraryNamesReadOnly = Collections.unmodifiableList( this.libraryNames );
            this.libraryNames.add( libraryName );
            notifyListeners( PROP_LIBRARY_NAMES, oldLibraryNames, this.libraryNamesReadOnly );
        }
    }
    
    /**
     * Resolve the referenced user libraries into a collection of classpath entries that reference
     * physical libraries. 
     * 
     * @return the classpath entries contributed by the referenced user libraries
     */
    
    public synchronized Collection<IClasspathEntry> resolve()
    {
        final List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
        final UserLibraryManager userLibraryManager = JavaModelManager.getUserLibraryManager();
        
        for( String libraryName : this.libraryNames )
        {
            final UserLibrary userLibrary = userLibraryManager.getUserLibrary( libraryName );
            
            if( userLibrary != null )
            {
                for( IClasspathEntry cpe : userLibrary.getEntries() )
                {
                    entries.add( cpe );
                }
            }
        }
        
        return Collections.unmodifiableCollection( entries );
    }
    
    /**
     * Validates the state of this operation config object and returns a status object
     * describing any problems. If no problems are detected, this should return OK
     * status.
     * 
     * @return the result of validating this operation config
     */
    
    @Override
    public synchronized IStatus validate()
    {
        IStatus st = Status.OK_STATUS;
        
        if( this.libraryNames.size() == 0 )
        {
            st = createErrorStatus( Resources.libraryNeedsToBeSelected );
        }
        else
        {
            if( this.validator != null )
            {
                try
                {
                    st = this.validator.validate( this );
                }
                catch( Exception e )
                {
                    log( e );
                    this.validator = null;
                }
            }
        }
        
        return st;
    }
    
    /**
     * Resets this operation config to its initial state (prior to any user changes).
     */

    @Override
    public synchronized void reset()
    {
        final IProject project = getFacetedProject().getProject();
        final IProjectFacet f = getProjectFacet();
        final IProjectFacetVersion fv = getProjectFacetVersion();
        
        List<String> newLibraryNames = null;

        if( project != null )
        {
            final ILibraryProvider currentProvider 
                = LibraryProviderFramework.getCurrentProvider( project, f );
            
            if( currentProvider == getLibraryProvider() )
            {
                final List<IClasspathEntry> entries;
                
                try
                {
                    entries = ClasspathUtil.getClasspathEntries( project, f );
                }
                catch( CoreException e )
                {
                    throw new RuntimeException( e );
                }
                
                final List<String> libraryNamesList = new ArrayList<String>();

                for( IClasspathEntry cpe : entries )
                {
                    if( cpe.getEntryKind() == IClasspathEntry.CPE_CONTAINER )
                    {
                        final IPath path = cpe.getPath();
                        
                        if( path.segmentCount() >= 2 && path.segment( 0 ).equals( JavaCore.USER_LIBRARY_CONTAINER_ID ) )
                        {
                            libraryNamesList.add( path.segment( 1 ) );
                        }
                    }
                }
                
                newLibraryNames = libraryNamesList;
            }
        }
        
        if( newLibraryNames == null )
        {
            newLibraryNames = new ArrayList<String>();
            
            try
            {
                Preferences prefs = FacetedProjectFramework.getPreferences( f );
                
                if( prefs.nodeExists( PREFS_LAST_USED_LIBRARIES ) )
                {
                    prefs = prefs.node( PREFS_LAST_USED_LIBRARIES );
                    
                    if( prefs.nodeExists( fv.getVersionString() ) )
                    {
                        prefs = prefs.node( fv.getVersionString() );
                        
                        for( String libraryName : prefs.childrenNames() )
                        {
                            newLibraryNames.add( libraryName );
                        }
                    }
                }
            }
            catch( BackingStoreException e )
            {
                log( e );
            }
        }
        
        setLibraryNames( newLibraryNames );
    }

    private static final class Resources
    
        extends NLS
        
    {
        public static String libraryNeedsToBeSelected;

        static
        {
            initializeMessages( UserLibraryProviderInstallOperationConfig.class.getName(), 
                                Resources.class );
        }
    }
}
