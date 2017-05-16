package org.eclipse.wst.validation.tests;

import org.eclipse.wst.common.tests.validation.Activator;

/**
 * This validator is used to test pattern filters.
 * @author karasiuk
 *
 */
public class T5A extends TestValidator {
	
	public String getName() {
		return "T5A";
	}
	
	public static String id(){
		return Activator.PLUGIN_ID +".T5A";
	}
			
}
