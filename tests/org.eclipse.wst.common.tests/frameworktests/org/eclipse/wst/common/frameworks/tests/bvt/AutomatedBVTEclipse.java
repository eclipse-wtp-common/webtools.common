/*
 * Created on Mar 25, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.tests.bvt;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

/**
 * @author jsholl
 */
public class AutomatedBVTEclipse extends AutomatedBVT {
	
	public AutomatedBVTEclipse(){
		super();
        URL url = Platform.getBundle("org.eclipse.wst.common.tests").getEntry("");
        try {
        	AutomatedBVT.baseDirectory = FileLocator.toFileURL(url).getFile() + "TestData"+ java.io.File.separatorChar;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
