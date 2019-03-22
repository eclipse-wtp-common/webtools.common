/*******************************************************************************
 * Copyright (c) 2007, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;


/**
 * The combined results of validating multiple resources.
 * @author karasiuk
 *
 */
public final class ValidationResults {
	
	private final ValidatorMessage[] _messages;
	private final int 	_error;
	private final int	_warn;
	private final int	_info;
	
	public ValidationResults(ValidationResult result){
		if (result == null){
			_messages = new ValidatorMessage[0];
			_error = 0;
			_warn = 0;
			_info = 0;
		}
		else {
			_messages = result.getMessagesAsCopy();
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
