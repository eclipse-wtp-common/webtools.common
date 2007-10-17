/**
 * 
 */
package org.eclipse.wst.common.tests;

import java.util.HashMap;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;

/**
 * @author itewk
 *
 */
public class JUnitLogListener implements ILogListener {

	public static final JUnitLogListener INSTANCE = new JUnitLogListener();
	private HashMap<IStatus, String> loggedStatuses = new HashMap<IStatus, String>();
	
	private JUnitLogListener() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.ILogListener#logging(org.eclipse.core.runtime.IStatus, java.lang.String)
	 */
	public void logging(IStatus status, String plugin) {
		int severity = status.getSeverity();
		if(severity == IStatus.CANCEL || severity == IStatus.ERROR || severity == IStatus.WARNING) {
			loggedStatuses.put(status, plugin);
		}
	}
	
	/**
	 * 
	 * @return the current list of logged statuses, clears list after return.
	 */
	public HashMap<IStatus,String> getLoggedStatuses() {
		HashMap<IStatus,String> returnStatuses = new HashMap<IStatus,String>(loggedStatuses);
		loggedStatuses.clear();
		return returnStatuses;
	}
}
