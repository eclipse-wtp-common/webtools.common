/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * Control the tracing that this plug-in performs. This is used for service.
 * @author karasiuk
 *
 */
public final class Tracing {
	
	private final static DateFormat _df = new SimpleDateFormat("HH:mm:ss.SSSS"); //$NON-NLS-1$
	private final static boolean	_isLogging = ValidationPlugin.getPlugin().isDebugging();
	private final static boolean	_traceMatches = Misc.debugOptionAsBoolean(DebugConstants.TraceMatches);
	private final static boolean	_traceV1 = Misc.debugOptionAsBoolean(DebugConstants.TraceV1);
	private final static String		_extraValDetail = Platform.getDebugOption(DebugConstants.ExtraValDetail);
	private final static int 		_tracingLevel;
	
	private final static String		_filter = Platform.getDebugOption(DebugConstants.FilterAllExcept);
	
	static {
		String traceLevel = Platform.getDebugOption(DebugConstants.TraceLevel);
		int level = 0;
		if (traceLevel != null){
			try {
				level = Integer.parseInt(traceLevel);
			}
			catch (Exception e){
			}
		}
		_tracingLevel = level;
	}
	
	/**
	 * Answer true if the filters allow this validator to be enabled. Normally this method will answer true.
	 * It is only when filters are activated via the debugging options, that this method might return false.
	 * This is used to aid in debugging by making it look like only one validator has been registered.
	 * 
	 * @param validatorId the validator id.
	 * @return true if the validator should be registered via an extension point.
	 */
	public static boolean isEnabled(String validatorId){
		if (_filter == null || _filter.length() == 0)return true;
		return (_filter.equals(validatorId));		
	}
	
	/**
	 * Are we in logging/debugging mode?
	 */
	public static boolean isLogging(){
		return _isLogging;
	}
	
	/**
	 * Answer true if we are in logging mode, and if the current logging level is greater than or
	 * equal to level.
	 * @param level The logging level that we are testing. The higher the level the more verbose
	 * the tracing.
	 */
	public static boolean isLogging(int level){
		if (_isLogging){
			return _tracingLevel >= level;
		}
		return false;
	}
	
	public static boolean isTraceMatches(){
		return _traceMatches;
	}
	
	public static boolean isTraceV1(){
		return _traceV1;
	}
	
	public static boolean matchesExtraDetail(String validatorId){
		if (_extraValDetail == null)return false;
		return _extraValDetail.equals(validatorId);
	}

	/**
	 * Write a line to the console for debugging, if in debugging mode.
	 * @param line
	 */
	public static void log(String line){
		if (isLogging())write(line);
	}
	
	public static void log(Object... parts){
		if (isLogging()){
			StringBuffer b = new StringBuffer(200);
			for (Object p : parts)b.append(p);
			write(b.toString());
		}
	}

	/**
	 * Write a line to the log. Include a time stamp with the line.
	 * @param line
	 */
	public static void write(String line){
		System.err.println(timestampIt(line));
	}

	public static String timestampIt(String line){
		Date date = new Date();
		String thread = Thread.currentThread().getName();
		return _df.format(date) + " " + thread + " " + line;  //$NON-NLS-1$//$NON-NLS-2$		
	}

	/**
	 * If we are in logging mode, log the item, and then reset the string buffer.
	 */
	public static void log(StringBuffer b){
		log(b.toString());
		b.setLength(0);
	}

	/**
	 * This method doesn't do anything, and will be removed.
	 * 
	 * @deprecated
	 */
	public static void setForceLogging(boolean forceLogging) {
	}
	
	/**
	 * Log up to maxNumber deltas to the log.
	 * @param delta The deltas to log.
	 * @param maxNumber The maximum number of deltas to log.
	 */
	public static void logResourceDeltas(IResourceDelta delta, int maxNumber){
		if (!isLogging())return;
		if (delta == null)Tracing.log("  ResourceDelta: null"); //$NON-NLS-1$
		else {
			DeltaLogger logger = new DeltaLogger(maxNumber);
			try {
				delta.accept(logger);
				if (logger.getCount() == 0)Tracing.log("  ResourceDelta: no deltas"); //$NON-NLS-1$
			}
			catch (CoreException e){
				// eat it
			}
		}
	}
	
	/**
	 * A debugging class that prints out some resource delta's.
	 * @author karasiuk
	 *
	 */
	private final static class DeltaLogger implements IResourceDeltaVisitor {
		
		private final int 	_max;
		private int 		_count;
		public int getCount() {
			return _count;
		}

		private StringBuffer _b = new StringBuffer(200);
		
		public DeltaLogger(int max){
			_max = max;
		}

		public boolean visit(IResourceDelta delta) throws CoreException {
			if (_count++ > _max)return false;
			int kind = delta.getKind();
			String type = "unknown"; //$NON-NLS-1$
			switch (kind){
			case IResourceDelta.ADDED:
				type = "Added"; //$NON-NLS-1$
				break;
			case IResourceDelta.CHANGED:
				type = "Changed"; //$NON-NLS-1$
				break;
			case IResourceDelta.REMOVED:
				type = "Removed"; //$NON-NLS-1$
				break;				
			}
			_b.append("  ResourceDelta "); //$NON-NLS-1$
			_b.append(type);
			_b.append(' ');
			_b.append(delta.getResource());
			Tracing.log(_b);
			return true;
		}
		
	}

}
