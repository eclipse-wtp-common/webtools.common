package org.eclipse.wst.common.frameworks.internal;

import org.eclipse.jem.util.UIContextDetermination;

/*
 * Licensed Material - Property of IBM (C) Copyright IBM Corp. 2001, 2002 - All
 * Rights Reserved. US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

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