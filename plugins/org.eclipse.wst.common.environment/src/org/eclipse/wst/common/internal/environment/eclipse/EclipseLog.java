/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 * yyyymmdd bug      Email and other contact information
 * -------- -------- -----------------------------------------------------------
 * 20060221   100364 pmoogk@ca.ibm.com - Peter Moogk
 *******************************************************************************/
package org.eclipse.wst.common.internal.environment.eclipse;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.environment.ILog;
import org.eclipse.wst.common.internal.environment.plugin.EnvironmentPlugin;

public class EclipseLog implements org.eclipse.wst.common.environment.ILog 
{
	private org.eclipse.core.runtime.ILog logger;
	
	public EclipseLog() 
	{
		Plugin plugin = EnvironmentPlugin.getInstance();
		
    logger = plugin.getLog();
	}
	
	/**
	 * @see org.eclipse.wst.common.environment.ILog#isEnabled()
	 */
	public boolean isEnabled() 
  {
		return Platform.inDebugMode();
	}
	
	/**
	 * @see org.eclipse.wst.common.Environment.ILog#isEnabled(java.lang.String)
	 */
	public boolean isEnabled(String option) {
		return "true".equals(Platform.getDebugOption("org.eclipse.wst.common.environment/trace/" //$NON-NLS-1$ //$NON-NLS-2$
				+ option));
	}
	
	/**
	 * @see org.eclipse.wst.common.environment.ILog#log(int, int, java.lang.Object, java.lang.String, java.lang.Object)
	 */
	public void log(int severity, int messageNum, Object caller,
			String method, Object object) 
	{
		
		if (isEnabled()) {
			switch (severity) {
				case ILog.ERROR :
				{
					if (isEnabled("error")) //$NON-NLS-1$
					{
						String message = getMessageNumString(messageNum) + "E " //$NON-NLS-1$
							+ caller + "::" + method + ": object=" //$NON-NLS-1$ //$NON-NLS-2$
							+ object;
						log( severity, message, null );
					}
					break;
				}
				
				case ILog.WARNING :
				{
					if (isEnabled("warning")) //$NON-NLS-1$
					{
						String message = getMessageNumString(messageNum)
								+ "W " + caller + "::" + method //$NON-NLS-1$ //$NON-NLS-2$
								+ ": object=" + object; //$NON-NLS-1$
						log( severity, message, null );
					}
					break;
				}
				
				case ILog.INFO :
				{
					if (isEnabled("info")) //$NON-NLS-1$
					{
						String message = getMessageNumString(messageNum) + "I " //$NON-NLS-1$
								+ caller + "::" + method + ": object=" //$NON-NLS-1$ //$NON-NLS-2$
								+ object;
						log( severity, message, null );
					}
					break;
				}
			}
		}
		
	}
	
	/**
	 * @see org.eclipse.wst.common.environment.ILog#log(int, int, java.lang.Object, java.lang.String, org.eclipse.core.runtime.IStatus)
	 */
	public void log(int severity, int messageNum, Object caller,
			String method, IStatus status) {
		log(severity, messageNum, caller, method, (Object)status);
	}
	
	/**
	 * @see org.eclipse.wst.common.environment.ILog#log(int, int, java.lang.Object, java.lang.String, java.lang.Throwable)
	 */
	public void log(int severity, int messageNum, Object caller,
			String method, Throwable throwable) {
		log( severity, messageNum, caller, method, (Object)null );
	}
	
	/**
	 * @see org.eclipse.wst.common.environment.ILog#log(int, java.lang.String, int, java.lang.Object, java.lang.String, java.lang.Object)
	 */
	public void log(int severity, String option, int messageNum,
			Object caller, String method, Object object) 
	{
		if (isEnabled(option))
		{
			String message = getMessageNumString(messageNum) + "I " + caller //$NON-NLS-1$
					+ "::" + method + ": object=" + object; //$NON-NLS-1$ //$NON-NLS-2$
			log(severity, message, null );
		}	
	}
	
	/**
	 * @see org.eclipse.wst.common.environment.ILog#log(int, java.lang.String, int, java.lang.Object, java.lang.String, java.lang.Throwable)
	 */
	public void log(int severity, String option, int messageNum,
			Object caller, String method, Throwable throwable) 
	{
		if (isEnabled(option)) 
		{
			String message = getMessageNumString(messageNum) + "I " + caller //$NON-NLS-1$
					+ "::" + method; //$NON-NLS-1$
			log( severity, message, throwable );
		}
	}
	
	/**
	 * @see org.eclipse.wst.common.environment.ILog#log(int, java.lang.String, int, java.lang.Object, java.lang.String, org.eclipse.core.runtime.IStatus)
	 */
	public void log(int severity, String option, int messageNum,
			Object caller, String method, IStatus status) 
	{
		logger.log( status );
	}
	
	private String getMessageNumString(final int messageNum) {
		String messageNumString = "IWAB"; //$NON-NLS-1$
		int innerMessageNum = messageNum;
		if (innerMessageNum > 9999 || innerMessageNum < 0)
			innerMessageNum = 9999; //default message number
		messageNumString += (new Integer(innerMessageNum)).toString();
		return messageNumString;
	}
	
	private void log( int severity, String message, Throwable exc )
	{
		Status status = new Status( severity, "id", 0, message, exc ); //$NON-NLS-1$
		logger.log( status );
	}
}
