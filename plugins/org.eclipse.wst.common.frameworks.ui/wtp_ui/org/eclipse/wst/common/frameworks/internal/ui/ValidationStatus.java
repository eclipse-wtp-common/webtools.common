/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.frameworks.internal.ui;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.widgets.Control;

/**
 * This object encompas a set of Validation status Creation date: (9/10/2001 5:35:12 PM)
 * 
 * @author: Administrator
 */
public class ValidationStatus {
	Hashtable errMsgs = new Hashtable();
	Hashtable errStatus = new Hashtable();
	Hashtable tControls = new Hashtable();
	Map warnMsgs;
	Map warnStatus;

	/**
	 * ValidationStatus constructor comment.
	 */
	public ValidationStatus() {
		super();
	}

	/*
	 * Look for any error. If there is more than one, return errors according to their key. i.e.,
	 * lower key errors will be returned first.
	 */
	public String getLastErrMsg() {

		Enumeration e = errStatus.keys();
		String[] errs = new String[errStatus.size()];
		for (int i = 0; i < errs.length; i++)
			errs[i] = null;
		boolean foundOne = false;
		while (e.hasMoreElements()) {
			Integer key = (Integer) e.nextElement();
			if (!((Boolean) errStatus.get(key)).booleanValue()) {
				errs[key.intValue() % errs.length] = (String) errMsgs.get(key);
				foundOne = true;
			}
		}
		if (foundOne)
			for (int i = 0; i < errs.length; i++)
				if (errs[i] != null)
					return errs[i];
		return null;
	}

	private Map getWarningMsgs() {
		if (warnMsgs == null)
			warnMsgs = new HashMap();
		return warnMsgs;
	}

	private Map getWarningStatusMap() {
		if (warnStatus == null)
			warnStatus = new HashMap();
		return warnStatus;
	}

	/*
	 * Look for any warning. If there is more than one, return warnings according to their key.
	 * i.e., lower key errors will be returned first.
	 */
	public String getLastWarningMsg() {
		if (warnStatus == null)
			return null;
		Iterator e = warnStatus.keySet().iterator();
		String[] warns = new String[warnStatus.size()];
		for (int i = 0; i < warns.length; i++)
			warns[i] = null;
		boolean foundOne = false;
		while (e.hasNext()) {
			Integer key = (Integer) e.next();
			if (!((Boolean) warnStatus.get(key)).booleanValue()) {
				warns[key.intValue() % warns.length] = (String) warnMsgs.get(key);
				foundOne = true;
			}
		}
		if (foundOne)
			for (int i = 0; i < warns.length; i++)
				if (warns[i] != null)
					return warns[i];
		return null;
	}

	public String getLastErrMsgAndFocus() {

		Enumeration e = errStatus.keys();

		Integer theOne = null;
		while (e.hasMoreElements()) {
			Integer key = (Integer) e.nextElement();
			if (!((Boolean) errStatus.get(key)).booleanValue()) {
				if (theOne == null || key.intValue() < theOne.intValue()) {
					theOne = key;
				}
			}
		}
		if (theOne != null) {
			Control control = (Control) tControls.get(theOne);
			if (control != null) {
				control.setFocus();
			}
			return ((String) errMsgs.get(theOne));
		}
		return null;
	}

	public boolean hasError(Integer key) {
		Boolean stat = (Boolean) errStatus.get(key);
		if (stat != null)
			return stat.booleanValue();
		return true;
	}

	public void setControl(Integer key, Control control) {
		tControls.put(key, control);
	}

	public void setErrorStatus(Integer key, Boolean status, String msg) {
		errMsgs.put(key, msg);
		errStatus.put(key, status);
	}

	public void setErrorStatus(Integer key, String msg) {
		errMsgs.put(key, msg);
		errStatus.put(key, new Boolean(false));
	}

	public void setWarningStatus(Integer key, String msg) {
		getWarningMsgs().put(key, msg);
		getWarningStatusMap().put(key, new Boolean(false));
	}

	public void setOKStatus(Integer key) {
		errMsgs.put(key, ""); //$NON-NLS-1$
		errStatus.put(key, new Boolean(true));
		if (warnMsgs != null)
			warnMsgs.put(key, ""); //$NON-NLS-1$
		if (warnStatus != null)
			warnStatus.put(key, new Boolean(true));
	}

	public void setStatus(Integer key, boolean ok, String msg) {
		errMsgs.put(key, msg);
		errStatus.put(key, new Boolean(ok));
	}
}