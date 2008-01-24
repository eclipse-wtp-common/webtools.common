package org.eclipse.wst.validation.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * Some miscellaneous helper methods. 
 * @author karasiuk
 *
 */
public class Misc {
	
	private static DateFormat _df = new SimpleDateFormat("HH:mm:ss.SSSS"); //$NON-NLS-1$
	private static boolean		_forceLogging;
	
	public static void close(InputStream in){
		if (in == null)return;
		try {
			in.close();
		}
		catch (IOException e){
			// eat it
		}
	}

	public static void close(OutputStream out) {
		if (out == null)return;
		try {
			out.close();
		}
		catch (IOException e){
			// eat it
		}		
	}
	
	public static boolean debugOptionAsBoolean(String option){
		String opt = Platform.getDebugOption(option);
		if (opt == null)return false;
		opt = opt.toLowerCase();
		if ("true".equals(opt))return true; //$NON-NLS-1$
		if ("yes".equals(opt))return true; //$NON-NLS-1$
		return false;
	}
	
	
	/**
	 * Answer a units appropriate string for the time.
	 * @param time time in nano seconds
	 */
	public static String getTimeNano(long time){
		if (time <= 1000)return NLS.bind(ValMessages.TimeNano, time);
		if (time <= 1000000)return NLS.bind(ValMessages.TimeMicro, time/1000);
		return getTimeMS(time/1000000);
	}
	
	/**
	 * Answer the CPU time consumed by this thread in nano seconds.
	 * @return -1 if the time can not be determined.
	 */
	public static long getCPUTime(){
		long cpuTime = -1;
		ThreadMXBean tb = ManagementFactory.getThreadMXBean();
		if (tb.isCurrentThreadCpuTimeSupported()){
			cpuTime = tb.getCurrentThreadCpuTime();
		}
		return cpuTime;
	}
	
	/**
	 * Answer a units appropriate string for the time.
	 * @param time time in milliseconds
	 */
	public static String getTimeMS(long time) {
		if (time <= 1000)return ValMessages.TimeUnder;
		if (time <= 60000)return NLS.bind(ValMessages.TimeSec, time/1000);
		return NLS.bind(ValMessages.TimeMin, time/60000);
	}

	
	/**
	 * Write a line to the console for debugging, if in debugging mode.
	 * @param line
	 */
	public static void log(String line){
		if (isLogging())write(line);
	}
	
	/**
	 * Write a line to the log.
	 * @param line
	 */
	public static void write(String line){
		System.err.println(timestampIt(line));
	}
	
	public static String timestampIt(String line){
		Date date = new Date();
		long thread = Thread.currentThread().getId();
		return _df.format(date) + " " + thread + " " + line;  //$NON-NLS-1$//$NON-NLS-2$		
	}
	
	/**
	 * Are we in logging/debugging mode?
	 */
	public static boolean isLogging(){
		return _forceLogging || ValidationPlugin.getPlugin().isDebugging();
	}
	
	/**
	 * If we are in logging mode, log the item, and then reset the string buffer.
	 */
	public static void log(StringBuffer b){
		log(b.toString());
		b.setLength(0);
	}
	
	public static void niy(String msg){
		if (msg == null)msg = "Sorry, this function is not implemented yet"; //$NON-NLS-1$
		throw new RuntimeException(msg);
	}

	/**
	 * Force the logging to be turned on. Normally logging is turned on via -debug options. However
	 * the logging can be force to be on by setting this to true. (The logging can not be forced to be
	 * tuned off)
	 * 
	 * @param forceLogging
	 */
	public static void setForceLogging(boolean forceLogging) {
		_forceLogging = forceLogging;
	}
	
}
