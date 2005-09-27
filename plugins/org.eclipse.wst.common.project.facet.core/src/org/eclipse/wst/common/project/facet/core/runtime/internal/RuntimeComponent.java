package org.eclipse.wst.common.project.facet.core.runtime.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;

public final class RuntimeComponent

    implements IRuntimeComponent
    
{
    private RuntimeComponentVersion rcv;
    
    private final Map properties = new HashMap();
    
    private final Map propertiesReadOnly 
        = Collections.unmodifiableMap( this.properties );
    
    /**
     * This class should not be instantiated outside this package.
     */

    RuntimeComponent() {}

    public IRuntimeComponentType getRuntimeComponentType()
    {
        return this.rcv.getRuntimeComponentType();
    }

    public IRuntimeComponentVersion getRuntimeComponentVersion()
    {
        return this.rcv;
    }
    
    void setRuntimeComponentVersion( final IRuntimeComponentVersion rcv )
    {
        this.rcv = (RuntimeComponentVersion) rcv;
    }

    public Map getProperties()
    {
        return this.propertiesReadOnly;
    }

    public String getProperty( final String key )
    {
        return (String) this.properties.get( key );
    }
    
    void setProperty( final String key,
                      final String value )
    {
        this.properties.put( key, value );
    }

    public Object getAdapter( final Class type )
    {
        final IAdapterManager manager = Platform.getAdapterManager();
        Object res = manager.loadAdapter( this, type.getName() );
        
        if( res == null )
        {
            final IAdapterFactory factory = rcv.getAdapterFactory( type );
            
            if( factory != null )
            {
                res = factory.getAdapter( this, type );
            }
        }
        
        return res;
    }

}
