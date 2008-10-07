package org.eclipse.wst.validation.tests;

import org.eclipse.wst.common.tests.validation.Activator;

/**
 * This validator is used to test pattern filters.
 * @author karasiuk
 *
 */
public class T5B extends TestValidator {
	
	public String getName() {
		return "T5B";
	}
	
	public static String id(){
		return Activator.PLUGIN_ID +".T5B";
	}
			
}
