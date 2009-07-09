/*
 * Created on Mar 25, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.tests.bvt;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;

/**
 * @author jsholl
 */
public class AutomatedBVTEclipse extends AutomatedBVT {
	
	public AutomatedBVTEclipse(){
		super();
		IPluginDescriptor pluginDescriptor = Platform.getPluginRegistry().getPluginDescriptor("org.eclipse.wst.common.tests");
        URL url = pluginDescriptor.getInstallURL();
        try {
        	AutomatedBVT.baseDirectory = Platform.asLocalURL(url).getFile() + "TestData"+ java.io.File.separatorChar;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
