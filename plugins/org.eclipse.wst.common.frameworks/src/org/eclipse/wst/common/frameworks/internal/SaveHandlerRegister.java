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
package org.eclipse.wst.common.frameworks.internal;

import org.eclipse.wst.common.core.util.UIContextDetermination;

/**
 * A registry for the default ISaveHandler to use when saving edit models
 */
public class SaveHandlerRegister {
	private static ISaveHandler saveHandler;

	/**
	 * This is a utility class and should not be instantiated
	 */
	protected SaveHandlerRegister() {
		super();
	}

	public static ISaveHandler getSaveHandler() {
		if (saveHandler == null)
			saveHandler = (ISaveHandler) UIContextDetermination.createInstance("saveHandler"); //$NON-NLS-1$
		return saveHandler;
	}
}
