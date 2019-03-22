/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
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

	// TODO make all these vars private and change errMsgs & errStatus to maps so the same methods
	// can be used as with warnings & info
	Hashtable errMsgs = new Hashtable();
	Hashtable errStatus = new Hashtable();
	Hashtable tControls = new Hashtable();
	Map warnMsgs;
	Map warnStatus;
	Map infoMsgs;
	Map infoStatus;

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
		//TODO once ivars are changed to maps, pass this to getLastMsg()
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

	private Map getInfoMsgs() {
		if (infoMsgs == null)
			infoMsgs = new HashMap();
		return infoMsgs;
	}

	private Map getInfoStatusMap() {
		if (infoStatus == null)
			infoStatus = new HashMap();
		return infoStatus;
	}

	public String getLastWarningMsg() {
		return getLastMsg(warnStatus, warnMsgs);
	}

	public String getLastInfoMsg() {
		return getLastMsg(infoStatus, infoMsgs);
	}

	/*
	 * Look for any status. If there is more than one, return status according to their key. i.e.,
	 * lower key status will be returned first.
	 */
	private String getLastMsg(Map statusMap, Map msgMap) {
		if (statusMap == null)
			return null;
		Iterator e = statusMap.keySet().iterator();
		String[] infos = new String[statusMap.size()];
		for (int i = 0; i < infos.length; i++)
			infos[i] = null;
		boolean foundOne = false;
		while (e.hasNext()) {
			Integer key = (Integer) e.next();
			if (!((Boolean) statusMap.get(key)).booleanValue()) {
				infos[key.intValue() % infos.length] = (String) msgMap.get(key);
				foundOne = true;
			}
		}
		if (foundOne)
			for (int i = 0; i < infos.length; i++)
				if (infos[i] != null)
					return infos[i];
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

	public void setInfoStatus(Integer key, String msg) {
		getInfoMsgs().put(key, msg);
		getInfoStatusMap().put(key, new Boolean(false));
	}

	public void setOKStatus(Integer key) {
		errMsgs.put(key, ""); //$NON-NLS-1$
		errStatus.put(key, new Boolean(true));
		if (warnMsgs != null)
			warnMsgs.put(key, ""); //$NON-NLS-1$
		if (warnStatus != null)
			warnStatus.put(key, new Boolean(true));
		if (infoMsgs != null)
			infoMsgs.put(key, ""); //$NON-NLS-1$
		if (infoStatus != null)
			infoStatus.put(key, new Boolean(true));
	}

	public void setStatus(Integer key, boolean ok, String msg) {
		errMsgs.put(key, msg);
		errStatus.put(key, new Boolean(ok));
	}
}
