package org.eclipse.wst.validation.internal.model;

import org.eclipse.wst.validation.Validator;

/**
 * Implement some common validator methods, that don't need to be part of the API.
 * 
 * @author karasiuk
 *
 */
public class ValidatorHelper {
	
	/**
	 * Answer true if this validator already has an exclude filter.
	 * 
	 * @param v
	 * @return
	 */
	public static boolean hasExcludeGroup(Validator.V2 v){
		FilterGroup[] groups = v.getGroups();
		for (int i=0; i<groups.length; i++){
			if (groups[i].isExclude())return true;
		}
		return false;
		
	}
}
