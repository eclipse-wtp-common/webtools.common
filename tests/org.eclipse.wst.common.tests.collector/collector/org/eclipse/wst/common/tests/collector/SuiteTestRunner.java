package org.eclipse.wst.common.tests.collector;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * @author jsholl
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SuiteTestRunner extends TestRunner {

    private TestSuite suite;
    
    /**
     * PluginTestRunner constructor comment.
     */
    public SuiteTestRunner(TestSuite suiteToRun) {
        super();
        suite = suiteToRun;
    }

    /**
     * Only return the specified suite
     */
    public Test getTest(String suiteClassName) {
        return suite;
    }

    /**
     * called by the gui
     */
    public void launch() {
        doRun(suite);
    }
   
}
