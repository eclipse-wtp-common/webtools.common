package org.eclipse.wst.common.tests.collector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author jsholl
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SuiteHelper {

    private Hashtable allTests = new Hashtable();

    public SuiteHelper(TestSuite suite) {
        addTest(suite);
    }

    private void addTest(Test test) {
        if (test instanceof TestSuite) {
            Enumeration tests = ((TestSuite) test).tests();
            while (tests.hasMoreElements()) {
                Test t = (Test) tests.nextElement();
                allTests.put(t.toString(), t);
            }
            return;
        }
        allTests.put(test.toString(), test);
    }

    public String[] getAllTests() {
        ArrayList testList = new ArrayList();
        Enumeration enumeration = allTests.keys();
        while (enumeration.hasMoreElements()) {
            testList.add(enumeration.nextElement());
        }
        Collections.sort(testList, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((String) o1).compareTo(((String) o2));
            }
        });

        String[] strArray = new String[testList.size()];
        for (int i = 0; i < strArray.length; i++) {
            strArray[i] = (String) testList.get(i);
        }

        return strArray;
    }

    public TestSuite buildSuite(String[] completeTests, String[] partialTests) {
        TestSuite suite = new TestSuite();
        for (int i = 0; i < completeTests.length; i++) {
            suite.addTest((Test) allTests.get(completeTests[i]));
        }
        for (int i = 0; i < partialTests.length; i++) {
            suite.addTest(getTest(partialTests[i]));
        }
        return suite;
    }

    public String[] getTestMethods(String testName) {
        ArrayList methodList = new ArrayList();
        Test test = (Test) allTests.get(testName);
        if (test instanceof TestSuite) {
            Enumeration testsEnum = ((TestSuite) test).tests();
            while (testsEnum.hasMoreElements()) {
                Test t = (Test) testsEnum.nextElement();
                methodList.add(t.toString());
            }
        }

        Collections.sort(methodList, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((String) o1).compareTo(((String) o2));
            }
        });

        String[] strArray = new String[methodList.size()];
        for (int i = 0; i < strArray.length; i++) {
            strArray[i] = (String) methodList.get(i);
        }

        return strArray;
    }

    private Test getSubTest(TestSuite suite, String testName) {
        if (null != suite) {
            Enumeration tests = suite.tests();
            while (tests.hasMoreElements()) {
                Test t = (Test) tests.nextElement();
                if (t.toString().equals(testName)) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Returns a TestSuite to run
     */
    private Test getTest(String testName) {
        int firstIndex = testName.indexOf(".");
        String suiteName = testName.substring(0, firstIndex);
        String subTestName = testName.substring(firstIndex + 1);

        //check the obvious suite first
        TestSuite suite = (TestSuite) allTests.get(suiteName);
        Test test = getSubTest(suite, subTestName);
        if (test != null) {
            return test;
        }
        //otherwise check all suites
        Enumeration keys = allTests.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if (testName.startsWith(key)) {
                suite = (TestSuite) allTests.get(key);
                subTestName = testName.substring(key.length() + 1);
                test = getSubTest(suite, subTestName);
                if (test != null) {
                    return test;
                }
            }
        }

        return null;

    }

}
