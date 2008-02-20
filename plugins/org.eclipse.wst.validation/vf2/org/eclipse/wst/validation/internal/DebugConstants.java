package org.eclipse.wst.validation.internal;

import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

public interface DebugConstants {
	
	/** matches - trace filter matches. */
	String TraceMatches = ValidationPlugin.PLUGIN_ID+"/matches"; //$NON-NLS-1$
	
	/** timings - trace the times of the validators. */
	String TraceTimes = ValidationPlugin.PLUGIN_ID+"/timings"; //$NON-NLS-1$
	
	/** timings/tracefile - file that stores the trace events */
	String TraceTimesFile = ValidationPlugin.PLUGIN_ID+"/timings/tracefile"; //$NON-NLS-1$

}
