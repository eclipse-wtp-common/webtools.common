/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $RCSfile: PresentPerformanceMonitor.java,v $
 *  $Revision: 1.3 $  $Date: 2005/02/02 20:51:09 $ 
 */
package org.eclipse.jem.util;
import org.eclipse.perfmsr.core.IPerformanceMonitor;
import org.eclipse.perfmsr.core.PerfMsrCorePlugin;

/**
 * This is the version used when the performance plugin is available.
 * 
 * <p>
 * This class is not meant to be instantiated by clients.
 * </p>
 * 
 * @since 1.0.0
 */
public class PresentPerformanceMonitor extends PerformanceMonitorUtil {

	/* (non-Javadoc)
	 * @see org.eclipse.jem.util.PerformanceMonitorUtil#upload(java.lang.String)
	 */
	public boolean upload(String description) {
		return monitor.upload(description).success;
	}
	
	private IPerformanceMonitor monitor;

	/*
	 * So that only instantiated by this package.
	 */
	PresentPerformanceMonitor() {
		monitor = PerfMsrCorePlugin.getPerformanceMonitor(true);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jem.util.PerformanceMonitorUtil#isValid()
	 */
	protected boolean isValid() {
		return monitor != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jem.util.PerformanceMonitorUtil#setVar(java.lang.String)
	 */
	public void setVar(String var) {
		monitor.setVar(var);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jem.util.PerformanceMonitorUtil#doSnapshot(int)
	 */
	protected void doSnapshot(int step) {
		monitor.snapshot(step);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jem.util.PerformanceMonitorUtil#doSnapshot(int, int)
	 */
	protected void doSnapshot(int step, int types) {
		monitor.snapshot(step, types);
	}

}