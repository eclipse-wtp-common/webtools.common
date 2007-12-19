package org.eclipse.wst.validation.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * Some miscellaneous helper methods. 
 * @author karasiuk
 *
 */
public class Misc {
	
	private static DateFormat _df = new SimpleDateFormat("HH:mm:ss.SSSS"); //$NON-NLS-1$
	
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
		if (!ValidationPlugin.getPlugin().isDebugging())return false;
		String opt = Platform.getDebugOption(option);
		if (opt == null)return false;
		opt = opt.toLowerCase();
		if ("true".equals(opt))return true; //$NON-NLS-1$
		if ("yes".equals(opt))return true; //$NON-NLS-1$
		return false;
	}
	
	/**
	 * Write a line to the console for debugging.
	 * @param line
	 */
	public static void log(String line){
		if (isLogging())System.err.println(timestampIt(line));
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
		return ValidationPlugin.getPlugin().isDebugging();
	}
	
	public static void log(StringBuffer b){
		log(b.toString());
		b.setLength(0);
	}
	
	public static void niy(String msg){
		if (msg == null)msg = "Sorry, this function is not implemented yet"; //$NON-NLS-1$
		throw new RuntimeException(msg);
	}
	
}
