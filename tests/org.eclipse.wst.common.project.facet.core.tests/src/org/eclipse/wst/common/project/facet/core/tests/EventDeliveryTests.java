/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.tests;

import static org.eclipse.wst.common.project.facet.core.tests.support.TestUtils.asSet;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

@SuppressWarnings( "unused" )
public final class EventDeliveryTests

    extends AbstractTests
    
{
    private static IProjectFacet f1;
    private static IProjectFacetVersion f1v10;
    private static IProjectFacetVersion f1v20;

    private static IProjectFacet f2;
    private static IProjectFacetVersion f2v11;
    private static IProjectFacetVersion f2v21;

    private static IProjectFacet f3;
    private static IProjectFacetVersion f3v12;
    private static IProjectFacetVersion f3v22;
    
    private static IProjectFacet f4;
    private static IProjectFacetVersion f4v13;
    private static IProjectFacetVersion f4v23;
    
    static
    {
        f1 = ProjectFacetsManager.getProjectFacet( "edt_f1" );
        f1v10 = f1.getVersion( "1.0" );
        f1v20 = f1.getVersion( "2.0" );
        
        f2 = ProjectFacetsManager.getProjectFacet( "edt_f2" );
        f2v11 = f2.getVersion( "1.1" );
        f2v21 = f2.getVersion( "2.1" );

        f3 = ProjectFacetsManager.getProjectFacet( "edt_f3" );
        f3v12 = f3.getVersion( "1.2" );
        f3v22 = f3.getVersion( "2.2" );

        f4 = ProjectFacetsManager.getProjectFacet( "edt_f4" );
        f4v13 = f4.getVersion( "1.3" );
        f4v23 = f4.getVersion( "2.3" );
    }
    
    private EventDeliveryTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Event Delivery Tests" );

        suite.addTest( new EventDeliveryTests( "testEventDelivery" ) );
        
        return suite;
    }
    
    public void testEventDelivery()
    
        throws CoreException
        
    {
        // Create a test project.
        
        final IFacetedProject fpj = createFacetedProject();
        
        // Setup listeners.
        
        final Listener fullListenerProjectApi = new Listener();
        fpj.addListener( fullListenerProjectApi );

        final Listener fullListenerGlobalApi = new Listener();
        FacetedProjectFramework.addListener( fullListenerGlobalApi );

        final Listener partialListenerProjectApi = new Listener();
        
        fpj.addListener( partialListenerProjectApi, IFacetedProjectEvent.Type.PRE_INSTALL, 
                         IFacetedProjectEvent.Type.FIXED_FACETS_CHANGED );
        
        final Listener partialListenerGlobalApi = new Listener();
        
        FacetedProjectFramework.addListener( partialListenerGlobalApi, 
                                             IFacetedProjectEvent.Type.PRE_INSTALL, 
                                             IFacetedProjectEvent.Type.FIXED_FACETS_CHANGED );
        
        if( FullExtensionBasedListener.instance != null )
        {
            FullExtensionBasedListener.instance.retrieveDeliveredEvents();
        }
        
        if( PartialExtensionBasedListener.instance != null )
        {
            PartialExtensionBasedListener.instance.retrieveDeliveredEvents();
        }
        
        addTearDownOperation
        (
            new Runnable()
            {
                public void run()
                {
                    FacetedProjectFramework.removeListener( fullListenerGlobalApi );
                    FacetedProjectFramework.removeListener( partialListenerGlobalApi );
                }
            }
        );
        
        // Run through a series of operations, checking to make sure that the listeners are seeing
        // the expected events.
        
        fpj.setFixedProjectFacets( asSet( f1, f2 ) );
        
        check( fullListenerProjectApi, Resources.fullStep1 );
        check( fullListenerGlobalApi, Resources.fullStep1 );
        check( FullExtensionBasedListener.instance, Resources.fullStep1 );
        check( partialListenerProjectApi, Resources.partialStep1 );
        check( partialListenerGlobalApi, Resources.partialStep1 );
        check( PartialExtensionBasedListener.instance, Resources.partialStep1 );
        
        fpj.modify( asSet( new Action( Action.Type.INSTALL, f1v10, null ),
                           new Action( Action.Type.INSTALL, f2v11, null ),
                           new Action( Action.Type.INSTALL, f3v12, null ) ), null );

        check( fullListenerProjectApi, Resources.fullStep2 );
        check( fullListenerGlobalApi, Resources.fullStep2 );
        check( FullExtensionBasedListener.instance, Resources.fullStep2 );
        check( partialListenerProjectApi, Resources.partialStep2 );
        check( partialListenerGlobalApi, Resources.partialStep2 );
        check( PartialExtensionBasedListener.instance, Resources.partialStep2 );
        
        fpj.installProjectFacet( f4v13, null, null );
        
        check( fullListenerProjectApi, Resources.fullStep3 );
        check( fullListenerGlobalApi, Resources.fullStep3 );
        check( FullExtensionBasedListener.instance, Resources.fullStep3 );
        check( partialListenerProjectApi, Resources.partialStep3 );
        check( partialListenerGlobalApi, Resources.partialStep3 );
        check( PartialExtensionBasedListener.instance, Resources.partialStep3 );
        
        fpj.uninstallProjectFacet( f3v12, null, null );
        
        check( fullListenerProjectApi, Resources.fullStep4 );
        check( fullListenerGlobalApi, Resources.fullStep4 );
        check( FullExtensionBasedListener.instance, Resources.fullStep4 );
        check( partialListenerProjectApi, Resources.partialStep4 );
        check( partialListenerGlobalApi, Resources.partialStep4 );
        check( PartialExtensionBasedListener.instance, Resources.partialStep4 );
        
        fpj.setFixedProjectFacets( asSet( f4 ) );
        
        check( fullListenerProjectApi, Resources.fullStep5 );
        check( fullListenerGlobalApi, Resources.fullStep5 );
        check( FullExtensionBasedListener.instance, Resources.fullStep5 );
        check( partialListenerProjectApi, Resources.partialStep5 );
        check( partialListenerGlobalApi, Resources.partialStep5 );
        check( PartialExtensionBasedListener.instance, Resources.partialStep5 );
        
        fpj.modify( asSet( new Action( Action.Type.VERSION_CHANGE, f1v20, null ),
                           new Action( Action.Type.VERSION_CHANGE, f4v23, null ) ), null );
        
        check( fullListenerProjectApi, Resources.fullStep6 );
        check( fullListenerGlobalApi, Resources.fullStep6 );
        check( FullExtensionBasedListener.instance, Resources.fullStep6 );
        check( partialListenerProjectApi, Resources.partialStep6 );
        check( partialListenerGlobalApi, Resources.partialStep6 );
        check( PartialExtensionBasedListener.instance, Resources.partialStep6 );
    }
    
    private static String toString( final List<IFacetedProjectEvent> events )
    {
        final StringBuilder buf = new StringBuilder();
        
        for( IFacetedProjectEvent event : events )
        {
            buf.append( event.toString() ).append( '\n' );
        }
        
        return buf.toString();
    }
    
    private static void check( final Listener listener,
                               final String expectedEvents )
    {
        final String actual = toString( listener.retrieveDeliveredEvents() ).trim();
        final String expected = expectedEvents.trim();
        
        assertEquals( expected, actual );
    }
    
    private static class Listener 
    
        implements IFacetedProjectListener
        
    {
        private final List<IFacetedProjectEvent> events = new ArrayList<IFacetedProjectEvent>();

        public final void handleEvent( final IFacetedProjectEvent event )
        {
            this.events.add( event );
        }
        
        public final List<IFacetedProjectEvent> retrieveDeliveredEvents()
        {
            final List<IFacetedProjectEvent> result 
                = new ArrayList<IFacetedProjectEvent>( this.events );
            
            this.events.clear();
            
            return result;
        }
    }
    
    public static final class FullExtensionBasedListener
    
        extends Listener
        
    {
        public static Listener instance = null;
        
        public FullExtensionBasedListener()
        {
            instance = this;
        }
    }
        
    public static final class PartialExtensionBasedListener
    
        extends Listener
        
    {
        public static Listener instance = null;
        
        public PartialExtensionBasedListener()
        {
            instance = this;
        }
    }

    public static final class Resources
    
        extends NLS
        
    {
        public static String fullStep1;
        public static String fullStep2;
        public static String fullStep3;
        public static String fullStep4;
        public static String fullStep5;
        public static String fullStep6;
        public static String partialStep1;
        public static String partialStep2;
        public static String partialStep3;
        public static String partialStep4;
        public static String partialStep5;
        public static String partialStep6;
        
        static
        {
            initializeMessages( EventDeliveryTests.class.getName(), 
                                Resources.class );
        }
    }
    
}
