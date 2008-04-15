/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

public interface DebugConstants {
	
	/** matches - trace filter matches. */
	String TraceMatches = ValidationPlugin.PLUGIN_ID+"/matches"; //$NON-NLS-1$
	
	/** timings - trace the times of the validators. */
	String TraceTimes = ValidationPlugin.PLUGIN_ID+"/timings"; //$NON-NLS-1$
	
	/** v1 - trace the v1 validators. */
	String TraceV1 = ValidationPlugin.PLUGIN_ID+"/v1"; //$NON-NLS-1$
	
	/** timings/tracefile - file that stores the trace events */
	String TraceTimesFile = ValidationPlugin.PLUGIN_ID+"/timings/tracefile"; //$NON-NLS-1$
	
	/** 
	 * timings/useDoubles - By default times that are written to the trace file are
	 * in milliseconds for elapsed time and nanoseconds for cpu time. 
	 * If you find these times easier to read as seconds as expressed by a double, 
	 * the following flag can be turned on.
	 */
	String TraceTimesUseDoubles = ValidationPlugin.PLUGIN_ID+"/timings/useDoubles"; //$NON-NLS-1$
	
	/** 
	 * extraValDetail - If extra detail is needed for a particular validator it's id can be specified here. 
	 * For example, if you wanted more detail on the XML validator you would use org.eclipse.wst.xml.core.xml 
	 */
	String ExtraValDetail = ValidationPlugin.PLUGIN_ID+"/extraValDetail"; //$NON-NLS-1$

}
