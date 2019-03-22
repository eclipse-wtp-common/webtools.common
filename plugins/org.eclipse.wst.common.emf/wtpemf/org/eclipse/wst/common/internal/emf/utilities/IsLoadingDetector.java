/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
/*
 * Created on Apr 1, 2003
 * 
 * To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code
 * and Comments
 */
package org.eclipse.wst.common.internal.emf.utilities;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;


public class IsLoadingDetector implements Adapter {
	public static final IsLoadingDetector INSTANCE = new IsLoadingDetector();

	private IsLoadingDetector() {
	}

	public void notifyChanged(Notification notification) {
	}

	public Notifier getTarget() {
		return null;
	}

	public void setTarget(Notifier newTarget) {
	}

	public boolean isAdapterForType(Object type) {
		return type == this;
	}
}