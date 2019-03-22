/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Apr 19, 2004
 */
package org.eclipse.wst.common.frameworks.internal.ui;

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