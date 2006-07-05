package org.eclipse.wst.common.project.facet.core.tests;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import junit.framework.TestCase;

public abstract class AbstractTests

    extends TestCase
    
{
    protected static final IWorkspace ws = ResourcesPlugin.getWorkspace();
    protected final Set resourcesToCleanup = new HashSet();
    
    protected AbstractTests( final String name )
    {
        super( name );
    }
    
    protected void tearDown()
        
        throws CoreException
        
    {
        for( Iterator itr = this.resourcesToCleanup.iterator(); itr.hasNext(); )
        {
            final IResource r = (IResource) itr.next();
            r.delete( true, null );
        }
    }
    
    protected void addResourceToCleanup( final IResource resource )
    {
        this.resourcesToCleanup.add( resource );
    }

}
