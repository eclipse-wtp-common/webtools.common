/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Feb 10, 2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.enablement;

import java.util.regex.Pattern;


/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FunctionGroupPatternBinding {

	private final static int HASH_FACTOR = 89;
	private final static int HASH_INITIAL = FunctionGroupPatternBinding.class.getName().hashCode();
	private String functionGroupId;
	private transient int hashCode;
	private transient boolean hashCodeComputed;
	private Pattern pattern;
	private transient String string;

	/**
	 *  
	 */
	public FunctionGroupPatternBinding(String functionGroupId, Pattern pattern) {
		if (pattern == null)
			throw new NullPointerException();

		this.functionGroupId = functionGroupId;
		this.pattern = pattern;
	}

	public int compareTo(Object object) {
		FunctionGroupPatternBinding castedObject = (FunctionGroupPatternBinding) object;
		int compareTo = Util.compare(functionGroupId, castedObject.functionGroupId);

		if (compareTo == 0)
			compareTo = Util.compare(pattern.pattern(), castedObject.pattern.pattern());

		return compareTo;
	}

	public boolean equals(Object object) {
		if (!(object instanceof FunctionGroupPatternBinding))
			return false;

		FunctionGroupPatternBinding castedObject = (FunctionGroupPatternBinding) object;
		boolean equals = true;
		equals &= Util.equals(functionGroupId, castedObject.functionGroupId);
		equals &= Util.equals(pattern, castedObject.pattern);
		return equals;
	}

	public String getActivityId() {
		return functionGroupId;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public int hashCode() {
		if (!hashCodeComputed) {
			hashCode = HASH_INITIAL;
			hashCode = hashCode * HASH_FACTOR + Util.hashCode(functionGroupId);
			hashCode = hashCode * HASH_FACTOR + Util.hashCode(pattern);
			hashCodeComputed = true;
		}

		return hashCode;
	}

	public String toString() {
		if (string == null) {
			final StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append('[').append(functionGroupId).append(",pattern=\"").append(pattern.pattern()).append("\"]"); //$NON-NLS-1$ //$NON-NLS-2$
			string = stringBuffer.toString();
		}

		return string;
	}
}