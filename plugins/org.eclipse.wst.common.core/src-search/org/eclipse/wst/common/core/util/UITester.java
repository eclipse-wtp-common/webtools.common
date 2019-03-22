package org.eclipse.wst.common.core.util;
/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

/**
 * Interface for a UITester. The "classname" attribute on the "uiTester" extension point should implement this class.
 * 
 * @since 1.0.0
 */
public interface UITester {

	/**
	 * Answer if the current context is an UI context.
	 * 
	 * @return <code>true</code> if an UI context.
	 * 
	 * @since 1.0.0
	 */
	public boolean isCurrentContextUI();
}
