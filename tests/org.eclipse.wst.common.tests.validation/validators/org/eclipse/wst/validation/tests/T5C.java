package org.eclipse.wst.validation.tests;

import org.eclipse.wst.common.tests.validation.Activator;

/**
 * This validator is used to test pattern filters. This validator would normally validate *.test1 files, but an exclusion rule
 * has been added (via the exclude extension point) to not validate /second/third/ files. 
 * @author karasiuk
 *
 */
public class T5C extends TestValidator {
	
	public String getName() {
		return "T5C";
	}
	
	public static String id(){
		return Activator.PLUGIN_ID +".T5C";
	}
			
}
