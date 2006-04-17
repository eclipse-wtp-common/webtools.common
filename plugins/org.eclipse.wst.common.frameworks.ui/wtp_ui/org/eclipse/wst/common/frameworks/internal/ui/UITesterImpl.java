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
		// Because this is contributed by the UI plugin return true for now regardless of the state of the PlatformUI
		return true;
//		try {
//			return PlatformUI.isWorkbenchRunning() || PlatformUI.getWorkbench().isClosing();
//		} catch (RuntimeException e) {
//			e.printStackTrace();
//			return false;
//		}
	}

}
