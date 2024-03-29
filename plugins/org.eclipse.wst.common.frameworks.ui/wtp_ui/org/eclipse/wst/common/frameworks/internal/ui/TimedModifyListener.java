/*******************************************************************************
 * Copyright (c) 2001, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.frameworks.internal.ui;

/**
 * This class perform the same function as the J2EETimedKeyListener but using the Modify SWT event
 * instead of the KeyUp. Creation date: (9/10/2001 11:46:51 AM)
 * 
 * @author: Administrator
 */
import java.awt.event.ActionListener;

import org.eclipse.swt.widgets.Widget;

public class TimedModifyListener extends TimedKeyListener {
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
	@Override
	public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
		monitoringTarget = (Widget) e.getSource();
		restart();
	}
}