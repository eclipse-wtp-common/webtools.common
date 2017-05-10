package org.eclipse.wst.validation.tests.testcase;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ValidationTestSuite extends TestSuite {
	
    public ValidationTestSuite() {
        super();
        TestSuite suite = (TestSuite)ValidationTestSuite.suite();
        for (int i = 0; i < suite.testCount(); i++) {
            addTest(suite.testAt(i));
        }
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.eclipse.wst.validation.tests.testcase");
		suite.addTest(TestSuite1.suite());
		suite.addTest(TestSuite2.suite());
		suite.addTest(TestSuite3.suite());
		//suite.addTest(TestSuite4.suite());
		suite.addTest(TestSuite5.suite());
		suite.addTest(TestSuite6.suite());
		suite.addTest(TestSuite7.suite());
		suite.addTest(TestSuite8.suite());
        suite.addTest(TestSuite9.suite());
        suite.addTest(TestSuite10.suite());
        return suite;
    }

}
