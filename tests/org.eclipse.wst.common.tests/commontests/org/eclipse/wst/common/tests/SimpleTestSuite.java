/*
 * Created on Feb 2, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.tests;

import junit.framework.TestSuite;

/**
 * @author jsholl
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SimpleTestSuite extends TestSuite {

    public SimpleTestSuite(Class theClass) {
        super(theClass, getShortName(theClass));
    }

    public static String getShortName(Class c){
        String name = c.getName();
        if(name.lastIndexOf('.') > 0){
            name = name.substring(name.lastIndexOf('.')+1);
        }
        return name;
    }
    
}
