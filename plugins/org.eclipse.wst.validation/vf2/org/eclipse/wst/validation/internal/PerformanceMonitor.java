/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.IPerformanceMonitor;
import org.eclipse.wst.validation.PerformanceCounters;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

public class PerformanceMonitor implements IPerformanceMonitor {
	
	private CollectionLevel _level = CollectionLevel.None;
	private boolean			_summaryOnly;
	
	/**
	 * Create a performance monitor.
	 * 
	 * @param traceTimes
	 *            Should the monitor be turned on?
	 * @param file
	 *            Should the events be logged to a file. If this is null or the
	 *            empty string the events will be written to stderr. Otherwise
	 *            the events are appended to a file with this name.
	 * 
	 * @param logInSeconds
	 *            Set this to true if you want the times that are logged in the
	 *            trace file normalized to seconds. Otherwise the default units are used,
	 *            milliseconds for elapsed time and nanoseconds for cpu time.
	 */
	public static PerformanceMonitor create(boolean traceTimes, String file, boolean logInSeconds){
		PerformanceMonitor pm = null;
		if (file == null || file.length() == 0)pm = new PerformanceMonitor();
		else pm = new ToFile(file, logInSeconds);
		
		if (traceTimes)pm.setCollectionLevel(CollectionLevel.Default);
		
		return pm;
	}
	
	private PerformanceMonitor(){};
	
	public CollectionLevel getCollectionLevel() {
		return _level;
	}

	public void setCollectionLevel(CollectionLevel level) {
		_level = level;
	}

	public List<PerformanceCounters> getPerformanceCounters(boolean asSummary) {
		return new LinkedList<PerformanceCounters>();
	}


	public void resetPerformanceCounters() {
	}

	public boolean isCollecting() {
		return _level != CollectionLevel.None;
	}
	
	public boolean isSummaryOnly() {
		return _summaryOnly;
	}
	
	public void add(PerformanceCounters counters){
		Tracing.write(counters.toString());
	}
	
	public static class Counters{
		String	name;
		int 	numberInvocations;
		
		/** Elapsed time in milliseconds. */
		long	elapsedTime;
		
		/** CPU time in nanoseconds, or -1 if unknown. */
		long	cpuTime;		
	}
	
	public static class ToFile extends PerformanceMonitor {
		
		private String 		_fileName;
		private boolean		_logInSeconds;
		
		private PrintWriter _pw;
		private static final String Comma=","; //$NON-NLS-1$
		private static DateFormat 	_df = new SimpleDateFormat("HH:mm:ss.SSSS"); //$NON-NLS-1$
		
		private ToFile(String fileName, boolean logInSeconds){
			_fileName = fileName;
			_logInSeconds = logInSeconds;
		}
		
		@Override
		public synchronized void add(PerformanceCounters pc) {
			try {
				PrintWriter pw = getWriter();
				pw.print(_df.format(pc.getWhen()) + Comma + 
					pc.getValidatorId() + Comma + pc.getNumberInvocations() + Comma);
				if (_logInSeconds){
					double sec = ((double)pc.getElapsedTime()) / 1000.0;
					pw.print(sec);
					pw.print(Comma);
					sec = ((double)pc.getCpuTime()) / 1000000000.0;
					pw.print(sec);
				}
				else {
					pw.print(pc.getElapsedTime()+Comma+pc.getCpuTime());
				}
				pw.println(Comma + pc.getResourceName());
				pw.flush();
			}
			catch (IOException e){
				ValidationPlugin.getPlugin().handleException(e);
			}
		}
		
		private PrintWriter getWriter() throws IOException {
			if (_pw == null){
				_pw = new PrintWriter(new FileOutputStream(_fileName, true));
				DateFormat df = DateFormat.getDateTimeInstance();
				_pw.println("# " + NLS.bind(ValMessages.LogSession,  //$NON-NLS-1$
					df.format(new Date(System.currentTimeMillis()))));
				if (_logInSeconds)
					_pw.println("# when, id, invocation count, elapsed time (seconds), cpu time (seconds), resource"); //$NON-NLS-1$
				else 
					_pw.println("# when, id, invocation count, elapsed time (ms), cpu time (ns), resource"); //$NON-NLS-1$
			}
			return _pw;
			
		}
	}
	
	public static class Collecting extends PerformanceMonitor {
		private List<PerformanceCounters>	_counters = new LinkedList<PerformanceCounters>();
		
		public void add(PerformanceCounters counters){
			_counters.add(counters);
		}
		
		public List<PerformanceCounters> getPerformanceCounters(boolean asSummary) {
			if (asSummary){
				Map<String, Counters> map = new HashMap<String, Counters>(40);
				for (PerformanceCounters pc : _counters){
					Counters c = map.get(pc.getValidatorId());
					if (c == null){
						c = new Counters();
						c.name = pc.getValidatorName();
						map.put(pc.getValidatorId(), c);
					}
					c.numberInvocations += pc.getNumberInvocations();
					c.elapsedTime += pc.getElapsedTime();
					if (pc.getCpuTime() != -1)c.cpuTime += pc.getCpuTime();
					else c.cpuTime = -1;
				}
				List<PerformanceCounters> list = new LinkedList<PerformanceCounters>();
				for (Map.Entry<String, Counters> me : map.entrySet()){
					Counters c = me.getValue();
					list.add(new PerformanceCounters(me.getKey(), c.name, null, c.numberInvocations, c.elapsedTime, c.cpuTime));
				}
				return list;
			}
			return _counters;
		}
		
		public void resetPerformanceCounters() {
			_counters.clear();
		}

	}

}
