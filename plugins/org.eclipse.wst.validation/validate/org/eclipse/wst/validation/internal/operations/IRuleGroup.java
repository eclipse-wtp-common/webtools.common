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
package org.eclipse.wst.validation.internal.operations;

/**
 * This type represents a group of rules which a validator checks. For now, the only grouping is by
 * speed: if a set of rules can be checked quickly, it will be included in the PASS_FAST group,
 * otherwise, it is grouped in the PASS_FULL group.
 * 
 * In future, this could be extended to include severities. To be able to group by severity helps
 * with the MessageLimitException. A validator should report errors first, and then warnings, and
 * then infos, in order to avoid the message limit being reached before the most severe problems are
 * reported.
 */
public interface IRuleGroup {
	// The following filters are used to identify a group of validation checks.

	// retrieves the type of pass, from the IValidationContext, which the validator should execute
	public static final String PASS_LEVEL = "PASS_LEVEL"; //$NON-NLS-1$

	// On the FAST_PASS, the validator should check the rules which do not
	// take much time to check, and exclude the rules which are valid only
	// before some action (e.g. exporting or deploying).
	// 
	// The severity of the messages is irrelevant.
	public static final int PASS_FAST = 0x1;
	public static final String PASS_FAST_NAME = "fast"; //$NON-NLS-1$ // In plugin.xml, identify this pass by this constant

	// On the FULL_PASS, the validator should check everything. This is the default.
	public static final int PASS_FULL = 0x2 | PASS_FAST;
	public static final String PASS_FULL_NAME = "full"; //$NON-NLS-1$ // In plugin.xml, identify this pass by this constant

}