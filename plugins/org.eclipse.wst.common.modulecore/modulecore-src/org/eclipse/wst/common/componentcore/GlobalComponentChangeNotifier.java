/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelEvent;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelListener;

public class GlobalComponentChangeNotifier implements EditModelListener {

	private static GlobalComponentChangeNotifier instance;

	private List listeners;
	private List removedListeners = new ArrayList();
	private boolean isNotifing = false;


	public static GlobalComponentChangeNotifier getInstance() {
		if (instance == null) {
			instance = new GlobalComponentChangeNotifier();
		}
		return instance;
	}

	public void editModelChanged(EditModelEvent anEvent) {
		notifyListeners(anEvent);
	}

	public void addListener(EditModelListener aListener) {
		if (aListener != null && !getListeners().contains(aListener))
			getListeners().add(aListener);
	}

	public synchronized boolean removeListener(EditModelListener aListener) {
		if (aListener != null) {
			if (isNotifing)
				return removedListeners.add(aListener);
			return getListeners().remove(aListener);
		}
		return false;
	}

	protected java.util.List getListeners() {
		if (listeners == null)
			listeners = new ArrayList();
		return listeners;
	}

	protected void notifyListeners(EditModelEvent anEvent) {
		if (listeners == null)
			return;
		boolean oldIsNotifying = isNotifing;
		synchronized (this) {
			isNotifing = true;
		}
		try {
			List list = getListeners();
			for (int i = 0; i < list.size(); i++) {
				EditModelListener listener = (EditModelListener) list.get(i);
				if (!removedListeners.contains(listener))
					listener.editModelChanged(anEvent);
			}
		} finally {
			synchronized (this) {
				isNotifing = oldIsNotifying;
				if (!isNotifing && removedListeners != null && !removedListeners.isEmpty()) {
					listeners.removeAll(removedListeners);
					removedListeners.clear();
				}
			}
		}
	}

}
