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
 *  $$RCSfile: NotPresentPerformanceMonitor.java,v $$
 *  $$Revision: 1.2 $$  $$Date: 2005/01/12 16:57:32 $$ 
 */
/**
 * This is the instantiation to use if the performance monitor plugin is not installed. It basically does nothing.
 * 
 * <p>
 * This class is not intended to be instantiated by clients.
 * </p>
 * 
 * @since 1.0.0
 */
public class NotPresentPerformanceMonitor extends PerformanceMonitorUtil {

	/*
	 * Only instantiated from this package.
	 */
	NotPresentPerformanceMonitor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.PerformanceMonitorUtil#setVar(java.lang.String)
	 */
	public void setVar(String var) {
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jem.util.PerformanceMonitorUtil#doSnapshot(int, int)
	 */
	protected void doSnapshot(int step, int types) {
	}


	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jem.util.PerformanceMonitorUtil#doSnapshot(int)
	 */
	protected void doSnapshot(int step) {
	}
}