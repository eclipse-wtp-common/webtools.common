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
 * Created on Apr 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.internal.common.frameworks.ui;

import org.eclipse.swt.widgets.Display;

/**
 * @author mdelder
 */
public class DisplayUtility {

	public static void asyncExec(Runnable runnable) {

		Display d = Display.getCurrent();
		if (d == null)
			Display.getDefault().asyncExec(runnable);
		else
			runnable.run();

	}

}