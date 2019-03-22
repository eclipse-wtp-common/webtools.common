/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.core.util.UITester;

public class UITesterImpl implements UITester {

	/**
	 *  
	 */
	public UITesterImpl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.UITester#isCurrentContextUI()
	 */
	public boolean isCurrentContextUI() {
		try {
			return PlatformUI.isWorkbenchRunning() || PlatformUI.getWorkbench().isClosing();
		} catch (Exception e) {
			//Ignore, workbench must not be running
			return false;
		}
	}

}
