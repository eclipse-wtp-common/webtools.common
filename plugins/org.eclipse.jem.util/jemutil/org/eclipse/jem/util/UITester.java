package org.eclipse.jem.util;
/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: UITester.java,v $$
 *  $$Revision: 1.1 $$  $$Date: 2005/01/07 20:19:23 $$ 
 */
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