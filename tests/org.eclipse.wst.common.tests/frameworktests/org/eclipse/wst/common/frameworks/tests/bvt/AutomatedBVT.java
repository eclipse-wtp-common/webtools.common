/*
 * Created on Apr 1, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.frameworks.tests.bvt;

import java.net.URL;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.frameworks.artifactedit.tests.ArtifactEditAPITests;
import org.eclipse.wst.common.frameworks.componentcore.tests.AllTests;
import org.eclipse.wst.common.frameworks.datamodel.tests.DataModelAPITests;


/**
 * @author jsholl
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AutomatedBVT extends TestSuite {

    public static String baseDirectory = System.getProperty("user.dir") + java.io.File.separatorChar + "TestData" + java.io.File.separatorChar;
    
    static {
        try {
            IPluginDescriptor pluginDescriptor = Platform.getPluginRegistry().getPluginDescriptor("org.eclipse.wst.common.tests");
            URL url = pluginDescriptor.getInstallURL(); 
        	AutomatedBVT.baseDirectory = Platform.asLocalURL(url).getFile() + "TestData"+ java.io.File.separatorChar;
		} catch (Exception e) { 
			System.err.println("Using working directory since a workspace URL could not be located.");
		} 
    }

    public static int unimplementedMethods;

    public static void main(String[] args) {
        unimplementedMethods = 0;
        TestRunner.run(suite());
        if (unimplementedMethods > 0) {
            System.out.println("\nCalls to warnUnimpl: " + unimplementedMethods);
        }
    }

    public AutomatedBVT() {
        super();
        TestSuite suite = (TestSuite) AutomatedBVT.suite();
        for (int i = 0; i < suite.testCount(); i++) {
            addTest(suite.testAt(i));
        }
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.eclipse.wst.common.test.bvt");
        suite.addTest(AllTests.suite());
		suite.addTest(DataModelAPITests.suite());
		suite.addTest(ArtifactEditAPITests.suite());
        return suite;
    }
}
