/*
 * Created on Apr 3, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.etools.common.tests.xml;

import java.io.File;
import java.io.FileReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.xml.sax.InputSource;

/**
 * @author jsholl
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DomComparitorTest extends TestCase {

    String baseDir = System.getProperty("user.dir") + java.io.File.separatorChar;
    String testData = baseDir + "testData" + java.io.File.separatorChar;

    public DomComparitorTest(String name) {
        super(name);
    }

    public void testEqualDoms_01() {
        checkEqual("baseData_01.xml", "baseData_01.xml");
    }

    public void testEqualDoms_01_01() {
        checkEqual("baseData_01.xml", "equalTo_01_case_01.xml");
    }

	public void testEqualDoms_01_02() {
		checkEqual("baseData_01.xml", "equalTo_01_case_02.xml");
	}

    public void testUnequalDom_01_01() {
        checkUnequal("baseData_01.xml", "unequalTo_01_case_01.xml");
    }

    public void testUnequalDom_01_02() {
        checkUnequal("baseData_01.xml", "unequalTo_01_case_02.xml");
    }

    public void testUnequalDom_01_03() {
        checkUnequal("baseData_01.xml", "unequalTo_01_case_03.xml");
    }

    public void testUnequalDom_01_04() {
        checkUnequal("baseData_01.xml", "unequalTo_01_case_04.xml");
    }

    //TODO figure out how to compare encodings
    //	public void testUnequalDom_01_05() {
    //		checkUnequal("baseData_01.xml", "unequalTo_01_case_05.xml");
    //	}

    private void checkEqual(String fileName1, String fileName2) {
        try {
            InputSource source1 = new InputSource(new FileReader(new File(testData + fileName1)));
            InputSource source2 = new InputSource(new FileReader(new File(testData + fileName2)));
//            String results = DomComparitor.compareDoms(source1, source2);
//            if (results != null) {
//                Assert.fail("Equal doms compared as unequal " + fileName1 + " " + fileName2 + "\ncompare results = " + results);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private void checkUnequal(String fileName1, String fileName2) {
        try {
            InputSource source1 = new InputSource(new FileReader(new File(testData + fileName1)));
            InputSource source2 = new InputSource(new FileReader(new File(testData + fileName2)));
//            String results = DomComparitor.compareDoms(source1, source2);
//            if (results == null) {
//                Assert.fail("Unequal doms compared as equal " + fileName1 + " " + fileName2);
//            }
        } catch (Exception e) {
        	e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}
