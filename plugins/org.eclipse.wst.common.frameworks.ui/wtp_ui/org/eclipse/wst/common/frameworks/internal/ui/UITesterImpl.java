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
/*
 * Created on Oct 27, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.jem.util.UITester;
import org.eclipse.ui.PlatformUI;


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
	 * @see org.eclipse.wst.common.frameworks.internal.UITester#isCurrentContextUI()
	 */
	public boolean isCurrentContextUI() {
		//TODO verify this is working properly now
		try {
			return PlatformUI.isWorkbenchRunning() || PlatformUI.getWorkbench().isClosing();
		} catch (Exception e) {
			//Ignore, workbench must not be running
			return false;
		}
	}

}
