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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelEvent;

public class GlobalComponentChangeNotifier extends AdapterImpl implements GlobalComponentChangeListener {

	private static GlobalComponentChangeNotifier instance;

	private List listeners;
	private List removedListeners = new ArrayList();
	private boolean isNotifing = false;
	private static final int EMF_EVENT = 0;
	private static final int EDIT_EVENT = 1;
	
	public static GlobalComponentChangeNotifier getInstance() {
		if (instance == null) {
			instance = new GlobalComponentChangeNotifier();
		}
		return instance;
	}
	
	public void notifyChanged(Notification notification) {
		notifyListeners(EMF_EVENT, notification);
	}

	
	public void editModelChanged(EditModelEvent anEvent) {
		notifyListeners(EDIT_EVENT, anEvent);
	}

	public void addListener(GlobalComponentChangeListener aListener) {
		if (aListener != null && !getListeners().contains(aListener))
			getListeners().add(aListener);
	}

	public synchronized boolean removeListener(GlobalComponentChangeListener aListener) {
		if (aListener != null) {
			if (isNotifing)
				return removedListeners.add(aListener);
			return getListeners().remove(aListener);
		}
		return false;
	}

	private java.util.List getListeners() {
		if (listeners == null)
			listeners = new ArrayList();
		return listeners;
	}

	private void notifyListeners(int eventKind, Object anEvent) {
		if (listeners == null)
			return;
		boolean oldIsNotifying = isNotifing;
		synchronized (this) {
			isNotifing = true;
		}
		try {
			List list = getListeners();
			for (int i = 0; i < list.size(); i++) {
				GlobalComponentChangeListener listener = (GlobalComponentChangeListener) list.get(i);
				if (!removedListeners.contains(listener)){
					switch(eventKind){
						case EMF_EVENT:
							listener.notifyChanged((Notification)anEvent);
							break;
						case EDIT_EVENT:
							listener.editModelChanged((EditModelEvent)anEvent);
							break;
					}
				}
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
