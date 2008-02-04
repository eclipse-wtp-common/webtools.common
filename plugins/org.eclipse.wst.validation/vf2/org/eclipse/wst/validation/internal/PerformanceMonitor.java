package org.eclipse.wst.validation.internal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
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
	 *            should the monitor be turned on?
	 * @param file
	 *            should the events be logged to a file. If this is null or the
	 *            empty string the events will be written to stderr. Otherwise
	 *            the events are appended to a file with this name.
	 */
	public static PerformanceMonitor create(boolean traceTimes, String file){
		PerformanceMonitor pm = null;
		if (file == null || file.length() == 0)pm = new PerformanceMonitor();
		else pm = new ToFile(file);
		
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
		Misc.write(counters.toString());
	}
	
	public static class Counters{
		String	name;
		int 	numberInvocations;
		long	elapsedTime;
		long	cpuTime;		
	}
	
	public static class ToFile extends PerformanceMonitor {
		
		private String 		_fileName;
		private PrintWriter _pw;
		private static final String Comma=","; //$NON-NLS-1$
		
		private ToFile(String fileName){
			_fileName = fileName;
		}
		
		@Override
		public synchronized void add(PerformanceCounters pc) {
			try {
				PrintWriter pw = getWriter();
				pw.println(pc.getValidatorId() + Comma + pc.getNumberInvocations() +
						Comma+pc.getElapsedTime()+Comma+pc.getCpuTime());
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
