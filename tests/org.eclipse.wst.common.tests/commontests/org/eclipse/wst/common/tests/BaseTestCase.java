/*
 * Created on Nov 24, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.tests;
import junit.framework.TestCase;

public class BaseTestCase extends TestCase {

	/**
	 * 
	 */ 
	public BaseTestCase() {
		super(); 
	}

	/**
	 * @param name
	 */
	public BaseTestCase(String name) {
		super(name); 
	}
	
		
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUpTest() throws Exception { 
		setUp();
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	public final void tearDownTest() throws Exception { 
		tearDown();
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#runBare()
	 */
	public final void runCoreTest() throws Throwable { 
		runTest();
	}

}
