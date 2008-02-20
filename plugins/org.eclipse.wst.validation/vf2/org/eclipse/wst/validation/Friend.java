package org.eclipse.wst.validation;

import java.util.Map;

/**
 * This class is only to be called by the validation framework.
 * @author karasiuk
 *
 */
public class Friend {
	
	public static void setMessages(Validator validator, Map<String, MessageSeveritySetting> map) {
		validator.setMessages(map);
	}

}
