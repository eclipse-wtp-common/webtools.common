package org.eclipse.wst.common.frameworks.internal.ui;

/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */

/**
 * This class perform the same function as the J2EETimedKeyListener but using the Modify SWT event
 * instead of the KeyUp. Creation date: (9/10/2001 11:46:51 AM)
 * 
 * @author: Administrator
 */
import java.awt.event.ActionListener;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Widget;

public class TimedModifyListener extends TimedKeyListener implements ModifyListener {
	/**
	 * J2EETimedModefyListener constructor comment.
	 * 
	 * @param delay
	 *            int
	 * @param listener
	 *            java.awt.event.ActionListener
	 */
	public TimedModifyListener(int delay, ActionListener listener) {
		super(delay, listener);
	}

	/**
	 * J2EETimedModefyListener constructor comment.
	 * 
	 * @param listener
	 *            java.awt.event.ActionListener
	 */
	public TimedModifyListener(ActionListener listener) {
		super(listener);
	}

	/*
	 * Re/Start the timer
	 */
	public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
		monitoringTarget = (Widget) e.getSource();
		restart();
	}
}