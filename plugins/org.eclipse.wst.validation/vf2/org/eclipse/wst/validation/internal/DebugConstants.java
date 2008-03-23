/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
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

}
