/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;


/**
 * The combined results of validating multiple resources.
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * @author karasiuk
 *
 */
public class ValidationResults {
	
	private ValidatorMessage[] _messages;
	private int _error;
	private int	_warn;
	private int _info;
	
	public ValidationResults(ValidationResult result){
		if (result == null){
			_messages = new ValidatorMessage[0];			
		}
		else {
			ValidatorMessage[] messages = result.getMessages();
			_messages = new ValidatorMessage[messages.length];
			System.arraycopy(messages, 0, _messages, 0, messages.length);
			_error = result.getSeverityError();
			_warn = result.getSeverityWarning();
			_info = result.getSeverityInfo();
		}
	}
	
	/**
	 * Answer any validation messages that were added by the validation operation.
	 * @return an array is returned even if there are no messages.
	 */
	public ValidatorMessage[] getMessages(){
		return _messages;
	}

	/**
	 * Answer the number of error messages that were generated as part of this validation operation.
	 */
	public int getSeverityError() {
		return _error;
	}

	/**
	 * Answer the number of informational messages that were generated as part of this validation operation.
	 */
	public int getSeverityInfo() {
		return _info;
	}
	
	/**
	 * Answer the number of warning messages that were generated as part of this validation operation.
	 */
	public int getSeverityWarning() {
		return _warn;
	}

}
