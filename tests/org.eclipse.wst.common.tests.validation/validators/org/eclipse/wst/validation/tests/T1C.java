package org.eclipse.wst.validation.tests;

import org.eclipse.wst.common.tests.validation.Activator;

/**
 * A validator that is used to test the folder filters.
 * 
 * It looks at files with a file extension of t1c.
 * @author karasiuk
 *
 */
public class T1C extends TestValidator {
	
	public static String id(){
		return Activator.PLUGIN_ID +".T1C";
	}
	
	@Override
	public String getId() {
		return id();
	}
	
	@Override
	public String getName() {
		return "T1C";
	}
	
	
}
