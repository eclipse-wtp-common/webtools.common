package org.eclipse.wst.validation.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * Control the tracing that this plug-in performs. This is used for service.
 * @author karasiuk
 *
 */
public class Tracing {
	
	private static DateFormat 	_df = new SimpleDateFormat("HH:mm:ss.SSSS"); //$NON-NLS-1$
	private static boolean		_forceLogging;
	
	/**
	 * Are we in logging/debugging mode?
	 */
	public static boolean isLogging(){
		return _forceLogging || ValidationPlugin.getPlugin().isDebugging();
	}

	/**
	 * Write a line to the console for debugging, if in debugging mode.
	 * @param line
	 */
	public static void log(String line){
		if (isLogging())write(line);
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
		long thread = Thread.currentThread().getId();
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
	 * Force the logging to be turned on. Normally logging is turned on via -debug options. However
	 * the logging can be forced to be on by setting this to true. (Setting this to false doesn't force
	 * the logging to be turned off).
	 * 
	 * @param forceLogging
	 */
	public static void setForceLogging(boolean forceLogging) {
		_forceLogging = forceLogging;
	}

}
