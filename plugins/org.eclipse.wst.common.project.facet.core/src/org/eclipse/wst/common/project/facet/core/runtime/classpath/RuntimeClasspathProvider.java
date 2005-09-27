package org.eclipse.wst.common.project.facet.core.runtime.classpath;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;

public final class RuntimeClasspathProvider

    implements IClasspathProvider
    
{
    private final IRuntime r;
    
    public RuntimeClasspathProvider( final IRuntime r )
    {
        this.r = r;
    }

    public List getClasspathEntries( final IProjectFacetVersion fv )
    {
        for( Iterator itr = this.r.getRuntimeComponents().iterator(); 
             itr.hasNext(); )
        {
            final IRuntimeComponent rc = (IRuntimeComponent) itr.next();
            
            final IClasspathProvider cpprov 
                = (IClasspathProvider) rc.getAdapter( IClasspathProvider.class );
            
            if( cpprov != null )
            {
                final List cp = cpprov.getClasspathEntries( fv );
                
                if( cp != null )
                {
                    return cp;
                }
            }
        }
        
        return null;
    }
    
    public static final class Factory
    
        implements IAdapterFactory
        
    {
        private static final Class[] ADAPTER_TYPES
            = { IClasspathProvider.class };
        
        public Object getAdapter( final Object adaptable, 
                                  final Class adapterType )
        {
            if( adapterType == IClasspathProvider.class )
            {
                return new RuntimeClasspathProvider( (IRuntime) adaptable );
            }
            else
            {
                return null;
            }
        }

        public Class[] getAdapterList()
        {
            return ADAPTER_TYPES;
        }
    }

}
