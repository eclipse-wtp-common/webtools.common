package org.eclipse.wst.common.frameworks.internal.ui;

/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */

import java.awt.event.ActionListener;

import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Widget;

/**
 * Insert the type's description here. Creation date: (8/30/2001 4:00:28 PM)
 * 
 * @author: Administrator
 */
public class TimedKeyListener extends Timer implements KeyListener, MouseListener, MouseTrackListener, ModifyListener {

	protected Widget monitoringTarget;

	private final static int TIME_LIMIT = 200;

	/**
	 * J2EETimedKeyListener constructor comment.
	 * 
	 * @param delay
	 *            int
	 * @param listener
	 *            java.awt.event.ActionListener
	 */
	public TimedKeyListener(int delay, ActionListener listener) {
		super(delay, listener);
		setRepeats(false);
	}

	/**
	 * Insert the method's description here. Creation date: (8/30/2001 4:43:33 PM)
	 * 
	 * @param a
	 *            java.awt.event.ActionListener
	 */
	public TimedKeyListener(ActionListener listener) {
		this(TIME_LIMIT, listener);
	}

	/**
	 * Sent when a key is pressed on the system keyboard.
	 * 
	 * @param e
	 *            an event containing information about the key press
	 */
	public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
	}

	/**
	 * Sent when a key is released on the system keyboard.
	 * 
	 * @param e
	 *            an event containing information about the key release
	 */
	public void keyReleased(org.eclipse.swt.events.KeyEvent e) {
		// Replaced with SWT.Modify -- see modifyText() in this class
		//        monitoringTarget = (Widget) e.getSource();
		//        restart();
	}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(MouseEvent)
	 */
	public void mouseDoubleClick(MouseEvent e) {
	}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(MouseEvent)
	 */
	public void mouseDown(MouseEvent e) {
	}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(MouseEvent)
	 */
	public void mouseUp(MouseEvent e) {
		monitoringTarget = (Widget) e.getSource();
		restart();
	}

	/**
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseEnter(MouseEvent)
	 */
	public void mouseEnter(MouseEvent e) {
	}

	/**
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseExit(MouseEvent)
	 */
	public void mouseExit(MouseEvent e) {
	}

	/**
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseHover(MouseEvent)
	 */
	public void mouseHover(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.ui.util.Timer#getSource()
	 */
	protected Object getSource() {
		return monitoringTarget;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e) {
		monitoringTarget = (Widget) e.getSource();
		restart();
	}
}