package org.eclipse.wst.validation.tests;

import org.eclipse.wst.common.tests.validation.Activator;

/**
 * This validator is used to test facet versions. It should be called on projects that have a version 5 java facet.
 * @author karasiuk
 *
 */
public class T4A extends TestValidator {
	
	public String getName() {
		return "T4A";
	}
	
	public static String id(){
		return Activator.PLUGIN_ID +".T4A";
	}
			
}
