/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.core;


/**
 * Encapsulates common message filtering parameters. Message Filters are used when retrieving
 * messages via an IMessageAccess. The filter encapsulates a simple set of typical filter criteria
 * which is used to select a subset of messages to retrieve. If multiple attributes of the message
 * filter are specified, they should be logically AND'd together by the MessageAccess
 * implementation.
 */
public class MessageFilter {
	public static final int ANY_SEVERITY = -1;

	/**
	 * Messages matching this severity(s) will be retrieved. One of SeverityEnum XXX_severity
	 * constants, or ANY_SEVERITY to specify that messages of any severity will be returned. This
	 * field can be combined with other filter attributes to narrow the selection set further.
	 */
	protected int severity = ANY_SEVERITY;

	/**
	 * The validator which reported the messages that should be returned. All messages which were
	 * reported using validator matching this validator will be returned. This field can be combined
	 * with other filter attributes to narrow the selection set further.
	 */
	protected IValidator validator;

	/**
	 * The target object of the messages that should be returned, or null to specify to retrieve
	 * messages of any target. All messages having a target object matching this object will be
	 * returned. This field can be combined with other filter attributes to narrow the selection set
	 * further.
	 */
	protected Object targetObject;

	/**
	 * @deprecated
	 */
	public MessageFilter() {
		super();
	}

	public MessageFilter(int aSeverity, IValidator validator, Object aTargetObject) {
		super();
		severity = aSeverity;
		this.validator = validator;
		targetObject = aTargetObject;
	}

	/**
	 * Get the severity filter constraint.
	 * 
	 * @return int
	 */
	public int getSeverity() {
		return severity;
	}

	/**
	 * Get the target object filter constraint.
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.Object getTargetObject() {
		return targetObject;
	}

	/**
	 * Get the validator to filter on.
	 * 
	 * @return IValidator
	 */
	public IValidator getValidator() {
		return validator;
	}

	/**
	 * Set the severity filter constraint.
	 * 
	 * @param newSeverity
	 *            int
	 */
	public void setSeverity(int newSeverity) {
		severity = newSeverity;
	}

	/**
	 * Set the target object filter constraint.
	 * 
	 * @param newTargetObject
	 *            java.lang.Object
	 */
	public void setTargetObject(java.lang.Object newTargetObject) {
		targetObject = newTargetObject;
	}

	/**
	 * Set the validator to return messages for.
	 * 
	 * @param validator
	 *            IValidator
	 */
	public void setValidator(IValidator validator) {
		this.validator = validator;
	}
}