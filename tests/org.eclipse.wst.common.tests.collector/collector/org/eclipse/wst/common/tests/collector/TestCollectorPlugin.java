/*
 * Created on Mar 6, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.tests.collector;

import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Plugin;

/**
 * @author jsholl
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestCollectorPlugin extends Plugin {


	public static TestCollectorPlugin instance = null;
	public IExtensionPoint suitesExtensionPoint = null;


	public TestCollectorPlugin getInstance(){
		return instance;
	}

    /**
     * @param descriptor
     */
    public TestCollectorPlugin(IPluginDescriptor descriptor) {
        super(descriptor);
		instance = this;
		suitesExtensionPoint = descriptor.getExtensionPoint("suites");
    }

}
