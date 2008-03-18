package org.eclipse.wst.validation.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

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
	
	public static String listMarkers(IResource resource){
		StringBuffer b = new StringBuffer(2000);
		try {
			IMarker[] markers = resource.findMarkers(null, true, IResource.DEPTH_ZERO);
			for (IMarker m : markers){
				Object o = m.getAttribute(IMarker.MESSAGE);
				if (o != null){
					b.append(o);
				}
				o = m.getAttribute(IMarker.SEVERITY);
				if (o != null){
					b.append(", Severity=");
					b.append(o);
				}
				b.append("; ");
			}
		}
		catch (CoreException e){
			
		}
		return b.toString();
	}
	
	/**
	 * Write a line to the console for debugging.
	 * @param line
	 */
	public static void log(String line){
		System.err.println(timestampIt(line));  
	}
	
	public static String timestampIt(String line){
		Date date = new Date();
		long thread = Thread.currentThread().getId();
		return _df.format(date) + " " + thread + " " + line;//$NON-NLS-1$//$NON-NLS-2$
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
