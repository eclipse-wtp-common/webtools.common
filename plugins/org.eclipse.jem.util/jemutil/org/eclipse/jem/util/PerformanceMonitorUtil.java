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
 *  $RCSfile: PerformanceMonitorUtil.java,v $
 *  $Revision: 1.1 $  $Date: 2005/01/07 20:19:23 $ 
 */
package org.eclipse.jem.util;
import org.eclipse.perfmsr.core.IPerformanceMonitor;

/**
 * This is a simplified wrapper to the IPerformanceMonitor that hides it so that the actual plugin can be optional and not required.
 * 
 * <p>
 * This class is not meant to be subclassed by clients.
 * </p>
 * 
 * @since 1.0.0
 */
public abstract class PerformanceMonitorUtil {

	public interface Types {

		/**
		 * 1 - Write out the performance counters from the operating system. These include working set, peak working set, elapsed time, user time, and
		 * kernel time.
		 */
		int OperatingSystemCounters = IPerformanceMonitor.Types.OperatingSystemCounters;

		/**
		 * 2 - Write out the global performance info. This includes things like the total committed memory for the entire system.
		 * 
		 * This function depends on the GetPerformanceInfo() function being available in the Windows psapi.dll. This is available in XP but is usually
		 * not available in Win/2000. If it is not available then this function throws an UnsupportedOperationException.
		 */
		int GlobalSystemCounters = IPerformanceMonitor.Types.GlobalSystemCounters;

		/**
		 * 4 - Write out the size of the Java Heap.
		 */
		int JavaHeapSize = IPerformanceMonitor.Types.JavaHeapSize;

		/**
		 * 8 - Write out how much of the Java heap is being used. This calls the garbage collector so it may skew timing results.
		 */
		int JavaHeapUsed = IPerformanceMonitor.Types.JavaHeapUsed;

		/**
		 * 16 - The plugin startup and size information.
		 */
		int PluginInfo = IPerformanceMonitor.Types.PluginInfo;

		/** 0xffff - Everything. */
		int All = IPerformanceMonitor.Types.All;
	}

	private static PerformanceMonitorUtil sharedMonitor;

	public static PerformanceMonitorUtil getMonitor() {
		if (sharedMonitor == null) {
			try {
				Class.forName("org.eclipse.perfmsr.core.PerfMsrCorePlugin"); // This just tests if the performance plugin is available. Throws
																			 // exception otherwise.
				Class presentClass = Class.forName("org.eclipse.jem.util.PresentPerformanceMonitor"); // Get the class we use wrapper it.
				sharedMonitor = (PerformanceMonitorUtil) presentClass.newInstance();
				if (!sharedMonitor.isValid())
					sharedMonitor = null;
			} catch (RuntimeException e) {
				// If any runtime exception, just use the not present one.
			} catch (ClassNotFoundException e) {
				// If class not found, then plugin not available, so just use the not present one.
			} catch (InstantiationException e) {
				// Problem instantiating, so just use the not present one.
			} catch (IllegalAccessException e) {
				// Some illegal access, so just use the not present one.
			}
			if (sharedMonitor == null) {
				// Couldn't get the performance one for some reason. Use not present one instead.
				sharedMonitor = new NotPresentPerformanceMonitor();
			}
		}
		return sharedMonitor;
	}
	
	protected boolean isValid() {
		return true;
	}

	/**
	 * Set the variations that are in effect.
	 * 
	 * @param var
	 *            a comma delimited string of variation numbers
	 */
	public abstract void setVar(String var);

	/**
	 * Take a snapshot of some default performance measurements.
	 * 
	 * @param step
	 *            this identifies the step that the snapshot is for
	 */
	public abstract void snapshot(int step);

	/**
	 * Take a snapshot of the selected performance measurements.
	 * 
	 * @param step
	 *            this identifies the step that the snapshot is for
	 * 
	 * @param types
	 *            This controls which measurements are selected. It is an or'd together list of the IPerformanceMonitor.Types constants.
	 * 
	 * @see IPerformanceMonitor.Types
	 */
	public abstract void snapshot(int step, int types);

}