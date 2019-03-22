package org.eclipse.jem.util;
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
/*
 *  $$RCSfile: NotPresentPerformanceMonitor.java,v $$
 *  $$Revision: 1.3 $$  $$Date: 2005/02/15 23:04:14 $$ 
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