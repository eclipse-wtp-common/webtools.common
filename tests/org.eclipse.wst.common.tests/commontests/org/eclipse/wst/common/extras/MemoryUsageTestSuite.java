package org.eclipse.wst.common.extras;


import junit.framework.TestResult;
import junit.framework.TestSuite;
/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */
public class MemoryUsageTestSuite extends TestSuite {
	//protected String GLOBAL_OUTPUT_FILENAME = "d:/eclipse/common_archive/memoryUsage/CommonArchive_MOF5.out";
	protected String GLOBAL_OUTPUT_FILENAME = "./EMF_Data.txt";
	protected boolean TRACK_MEMORY = true;
	protected String outputFileName;
	/**
	 * Constructor for MemoryUsageTestSuite.
	 */
	public MemoryUsageTestSuite() {
		super();
	}
	/**
	 * Constructor for MemoryUsageTestSuite.
	 * @param theClass
	 */
	public MemoryUsageTestSuite(Class theClass) {
		super(theClass);
	}
	/**
	 * Constructor for MemoryUsageTestSuite.
	 * @param name
	 */
	public MemoryUsageTestSuite(String name) {
		super(name);
	}
		
	public void run(TestResult result) {
//		TimerStep step = null;
//		if (TRACK_MEMORY) {
//			step = TimerStep.instance();
//			step.setLogFile(getOutputName());
//			if (!step.isOn())
//				step.setIsOn(true);
//			step.write("", "Before " + getMemoryName() + " Test Run");
//			step.totalMemory(0);
//			step.usedMemory(0);
//		}
//		super.run(result);
//		if (TRACK_MEMORY) {
//			step.write("", "After " + getMemoryName() + " Test Run");
//			step.totalMemory(0);
//			step.usedMemory(0);
//		}
		
		super.run(result);
	}

	/**
	 * Method getOutputName.
	 * @return String
	 */
	private String getOutputName() {
		if (outputFileName != null && outputFileName.length() > 0)
			return outputFileName;
		return GLOBAL_OUTPUT_FILENAME;
	}


	/**
	 * Method getMemoryName.
	 * @return String
	 */
	private String getMemoryName() {
		if (getName() != null && getName().length() > 0)
			return getName();
		if (getClass() != null)
			return getClass().getName();
		return "Unknown Test";
	}

	/**
	 * Sets the outputFileName.
	 * @param outputFileName The outputFileName to set
	 */
	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	/**
	 * Returns the gLOBAL_OUTPUT_FILENAME.
	 * @return String
	 */
	public String getGLOBAL_OUTPUT_FILENAME() {
		return GLOBAL_OUTPUT_FILENAME;
	}

	/**
	 * Sets the gLOBAL_OUTPUT_FILENAME.
	 * @param gLOBAL_OUTPUT_FILENAME The fileName to set
	 */
	public void setGLOBAL_OUTPUT_FILENAME(String fileName) {
		GLOBAL_OUTPUT_FILENAME = fileName;
	}

	/**
	 * Sets the tRACK_MEMORY.
	 * @param tRACK_MEMORY The aBoolean to set
	 */
	public void setTRACK_MEMORY(boolean aBoolean) {
		TRACK_MEMORY = aBoolean;
	}

}
