/*
 * Created on Nov 3, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.tests;

import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Plugin;

/**
 * @author jsholl
 */
public class CommonTestsPlugin extends Plugin {
	public static String PLUGIN_ID = "org.eclipse.wst.common.tests";
	public static CommonTestsPlugin instance = null;
	public IExtensionPoint dataModelVerifierExt = null;
	
	/**
	 * @param descriptor
	 */
	public CommonTestsPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		instance = this;
		dataModelVerifierExt = descriptor.getExtensionPoint("DataModelVerifier");
	}

}
