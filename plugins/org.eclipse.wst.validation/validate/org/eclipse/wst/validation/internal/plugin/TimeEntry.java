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
package org.eclipse.wst.validation.internal.plugin;

import com.ibm.wtp.common.logger.LogEntry;


/**
 * This class should be used when logging "elapsed time" information and nothing else. Instead of
 * creating a new instance of this class every time it is needed, for performance reasons, create an
 * instance and reuse it. The text in this class is never translated.
 */
public class TimeEntry extends LogEntry {
	private String _toolName = null;
	private String _details = null;
	private String _projectName = null;
	private String _sourceId = null;
	private int _executionMap = 0x0;

	public TimeEntry() {
		super();
	}

	/**
	 * The name of the tool (e.g., a validator, a builder) whose time is measured.
	 */
	public String getToolName() {
		return _toolName;
	}

	public void setToolName(String name) {
		_toolName = name;
	}


	/**
	 * If there are any details that need to be recorded about the tool, such as what input it runs
	 * on, this field stores the value. This field is optional.
	 */
	public String getDetails() {
		return _details;
	}

	public void setDetails(String d) {
		_details = d;
	}

	/**
	 * The name of the project on which the tool ran.
	 */
	public String getProjectName() {
		return _projectName;
	}

	public void setProjectName(String name) {
		_projectName = name;
	}

	/**
	 * The id of the code that launched the tool.
	 */
	public String getSourceID() {
		return _sourceId;
	}

	public void setSourceID(String id) {
		_sourceId = id;
	}

	/**
	 * If, in addition to elapsed time, the tool needs to track the execution path of a method, this
	 * field stores the hexadecimal number that tracks the path. See ValidationBuilder::build for an
	 * example that uses an execution path.
	 */
	public int getExcecutionMap() {
		return _executionMap;
	}

	public void setExecutionMap(int map) {
		_executionMap = map;
	}

	/**
	 * Clear all of the fields back to their initial setting so that this TimeEntry instance can be
	 * reused.
	 */
	public void reset() {
		_toolName = null;
		_details = null;
		_projectName = null;
		_sourceId = null;
		_executionMap = 0;
	}
}