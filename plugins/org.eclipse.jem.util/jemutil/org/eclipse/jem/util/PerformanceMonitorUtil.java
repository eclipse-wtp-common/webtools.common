/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $RCSfile: PerformanceMonitorUtil.java,v $
 *  $Revision: 1.5 $  $Date: 2005/05/18 21:58:34 $ 
 */
package org.eclipse.jem.util;
import java.util.EventObject;

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
	/**
	 * Event for PerformanceListener notification.
	 * 
	 * @since 1.1.0
	 */
	public static class PerformanceEvent extends EventObject {
		
		PerformanceEvent(Object source, int step) {
			super(source);
			snapshowWithTypes = false;
			this.step = step;
			this.types = 0;	// Not set.
		}
		
		PerformanceEvent(Object source, int step, int types) {
			super(source);
			snapshowWithTypes = true;
			this.step = step;
			this.types = types;
		}

		
		/**
		 * Snapshot with types if <code>true</code>.
		 * @since 1.1.0
		 */
		public final boolean snapshowWithTypes;

		/**
		 * Step of snapshot
		 * @since 1.1.0
		 */
		public final int step;
		
		/**
		 * types of snapshot.
		 * @since 1.1.0
		 */
		public final int types;
	}
	
	/**
	 * Performance Listener interface
	 * 
	 * @since 1.1.0
	 */
	public interface PerformanceListener {
		/**
		 * Snapshot was called.
		 * @param event
		 * 
		 * @since 1.1.0
		 */
		public void snapshot(PerformanceEvent event);
	}
	
	private PerformanceListener[] listeners;

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
				Class.forName("org.eclipse.perfmsr.core.PerfMsrCorePlugin"); // This just tests if the performance plugin is available. Throws //$NON-NLS-1$
																			 // exception otherwise.
				Class presentClass = Class.forName("org.eclipse.jem.util.PresentPerformanceMonitor"); // Get the class we use wrapper it. //$NON-NLS-1$
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
	public final void snapshot(int step) {
		doSnapshot(step);
		if (listeners != null)
			notifySnapshot(new PerformanceEvent(this, step));
	}
	
	private void notifySnapshot(PerformanceEvent event) {
		PerformanceListener[] list = listeners;
		for (int i = 0; i < list.length; i++) {
			list[i].snapshot(event);
		}
	}
	
	/**
	 * Do the actual snapshot
	 * @param step
	 * 
	 * @see #snapshot(int)
	 * @since 1.1.0
	 */
	protected abstract void doSnapshot(int step);

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
	public void snapshot(int step, int types) {
		doSnapshot(step, types);
		if (listeners != null)
			notifySnapshot(new PerformanceEvent(this, step, types));		
	}
	
	/**
	 * Do the actual snapshot
	 * @param step
	 * 
	 * @see #snapshot(int, int)
	 * @since 1.1.0
	 */
	protected abstract void doSnapshot(int step, int types);	
	
	/**
	 * Add listener to list.
	 * @param listener
	 * 
	 * @since 1.1.0
	 */
	public void addPerformanceListener(PerformanceListener listener) {
		if (findListener(listener) != -1)
			return;
		PerformanceListener[] newList = new PerformanceListener[listeners != null ? listeners.length+1 : 1];
		if (listeners != null)
			System.arraycopy(listeners, 0, newList, 0, listeners.length);
		newList[newList.length-1] = listener;
		listeners = newList;
	}
	
	private int findListener(PerformanceListener listener) {
		if (listeners != null) {
			for (int i = 0; i < listeners.length; i++) {
				if (listeners[i] == listener)
					return i;
			}
		}
		return -1;
	}
	
	/**
	 * Remove the listener from the list.
	 * @param listener
	 * 
	 * @since 1.1.0
	 */
	public void removePerformanceListener(PerformanceListener listener) {
		int index = findListener(listener);
		if (index != -1) {
			if (listeners.length == 1) {
				listeners = null;
				return;
			}
			PerformanceListener[] newList = new PerformanceListener[listeners.length-1];
			System.arraycopy(listeners, 0, newList, 0, index);
			System.arraycopy(listeners, index+1, newList, index, newList.length-index);
			listeners = newList;
		}
	}
	/**
	 * Upload the results to the server. This causes the file to be
	 * closed, and the monitor to be placed into the finished state.
	 * 
	 * This method can only be called if the uploadhost, uploadport and uploaduserid
	 * have been configured before hand.
	 * 
	 * @param description an optional description (it can be null)
	 * 
	 */
	public boolean upload(String description){return false;}
}