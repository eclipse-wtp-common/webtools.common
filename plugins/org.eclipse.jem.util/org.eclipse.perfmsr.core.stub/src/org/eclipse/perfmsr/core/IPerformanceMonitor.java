/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.perfmsr.core;

/**
 * Take snapshots of the various performance counters.
 * ************* This is just a stub of the real interface to get org.eclipse.jem.util 
 *               to compile. It is not usable by itself. ****************************
 */
public interface IPerformanceMonitor
{
	/**
	 * These constants control how much information is gathered for a particular snapshot.
	 * They are or'ed together and passed into the snapshot method.  
	 */
	interface Types
	{
		/** 
		 * 1 - Write out the performance counters from the operating system. These
		 * include working set, peak working set, elapsed time, user time, and 
		 * kernel time.
		 */
		int OperatingSystemCounters		= 1;
		
		/**
		 * 2 - Write out the global performance info. This includes things like the total
		 * committed memory for the entire system.
		 * 
		 * This function depends on the GetPerformanceInfo() function being available in
		 * the Windows psapi.dll. This is available in XP but is usually not available
		 * in Win/2000. If it is not available then this function throws an UnsupportedOperationException.
		 */
		int GlobalSystemCounters		= 2;
		
		/**
		 * 4 - Write out the size of the Java Heap.
		 */
		int JavaHeapSize				= 4;

		/**
		 * 8 - Write out how much of the Java heap is being used. This calls the 
		 * garbage collector so it may skew timing results.
		 */
		int JavaHeapUsed				= 8;
		
		/**
		 * 16 - The plugin startup and size information.
		 */
		int PluginInfo					= 16;
		
		/** 0xffff - Everything. */
		int All							= 0xffff;	
	}
	
	/**
	 * Add some more "extra" variations. 
	 * 
	 * The variations that are in effect are made up of these extra variations plus any variations
	 * that may have been set with the setVar() method.
	 * 
	 * @param varList a comma separated list of variations
	 */
	public void addVarAppend(String varList);
	
	/**
	 * Set the driver name. 
	 * 
	 * @param driver a label that identifies the driver that is being tested. If it is a WSAD
	 * driver, this will usually be set by the extension.
	 */
	public void setDriver(String driver);
	
	/**
	 * Set the location of the performance measurement file, that is the place where
	 * the measurements are stored.
	 * 
	 * @param logFile the file name of where the log should be written. Usually this is fully qualified
	 * path name. For example "x:\logs\timer.xml".
	 */
	public void setLogFile(String logFile);
	
	/**
	 * Set the performance monitor to be on or off. If it is off then most of the other 
	 * operations will no-op.
	 * 
	 * The default is for the performance monitor to be off unless it's special environment variable is set. 
	 * When called from the UI, the UI should call this to make sure that it is on.
	 */
	public void setIsOn(boolean isOn);
	
	/**
	 * Sets the test case number for this measurement run.
	 *  
	 * @param testd test case identifier
	 */
	public void setTestd(int testd);
	
	/**
	 * Sets the test case name. If will cause a new test case to be created if it does not already exist.
	 * 
	 * @param testName a simple (short) test case name. In the case of JUnit test cases, it would usually 
	 * be the JUnit name.
	 */
	public void setTestName(String testName);
	
	/**
	 * Set the variations that are in effect.
	 * 
	 * @param var a comma delimited string of variation numbers
	 */
	public void setVar(String var);
	
	/**
	 * Set the upload host
	 * 
	 * @param host DNS name of the upload host
	 */
	public void setUploadHost(String host);
	
	/**
	 * Set the upload port
	 * 
	 * @param port upload port
	 */
	public void setUploadPort(int port);
	
	/**
	 * Set the upload userid
	 * 
	 * @param userid
	 */
	public void setUploadUserid(String userid);
	
	/**
	 * Take a snapshot of some default performance measurements.
	 * 
	 * @param step this identifies the step that the snapshot is for
	 */
	void snapshot(int step);
	
	/**
	 * Take a snapshot of the selected performance measurements.
	 * 
	 * @param step this identifies the step that the snapshot is for
	 * 
	 * @param types This controls which measurements are selected. It is an or'd together
	 * list of the IPerformanceMonitor.Types constants.
	 * 
	 * @see IPerformanceMonitor.Types 
	 */
	void snapshot(int step, int types);
	
	/**
	 * Write the comment to the performance measurement file.
	 * 
	 * @param comment
	 */
	void writeComment(String comment);
	
	/**
	 * Upload the results to the server. This causes the file to be
	 * closed, and the monitor to be placed into the finished state.
	 * 
	 * This method can only be called if the uploadhost, uploadport and uploaduserid
	 * have been configured before hand.
	 * 
	 * @param description an optional description (it can be null)
	 * 
	 * @return some status information
	 */
	public Upload.Status upload(String description);
//	
//	/**
//	 * Upload the results to the server. This causes the file to be
//	 * closed, and the monitor to be placed into the finished state.
//	 * 
//	 * @param host the host name that the file is being sent to
//	 * @param port the port on the host
//	 * @param userid the userid that is doing the upload
//	 * @param description an optional description (it can be null)
//	 * 
//	 * @return some status information
//	 */
//	Upload.Status upload(String host, int port, String userid, String description);


}
