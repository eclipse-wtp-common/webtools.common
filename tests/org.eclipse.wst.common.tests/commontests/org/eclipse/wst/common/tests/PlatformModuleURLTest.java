/*
 * Created on Jan 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.tests;

import java.net.URL;
import java.net.URLConnection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.common.componentcore.internal.impl.PlatformURLModuleConnection;

public class PlatformModuleURLTest extends TestCase {

    public PlatformModuleURLTest(String name) {
        super(name);
    }
    
    
    public static Test suite() {
        return new TestSuite(PlatformModuleURLTest.class);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception { 
        super.setUp();
        PlatformURLModuleConnection.startup();
    }
        
    /**
     * 
     */
    public void testURLResolve() throws Exception {
        URL url = new URL("platform:/module:/MyModule/META-INF/ejb-jar.xml");
        URLConnection conx = url.openConnection();
        System.out.println(conx.getURL());

    }
}
