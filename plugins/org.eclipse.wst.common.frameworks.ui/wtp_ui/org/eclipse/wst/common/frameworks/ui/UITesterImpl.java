/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Oct 27, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.frameworks.ui;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;

import com.ibm.wtp.common.UITester;


/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
	 * @see org.eclipse.wst.common.framework.UITester#isCurrentContextUI()
	 */
	public boolean isCurrentContextUI() {
		try {
			return PlatformUI.isWorkbenchRunning() || ((Workbench) PlatformUI.getWorkbench()).isClosing();
		} catch (RuntimeException e) {
			return false;
		}
	}

}