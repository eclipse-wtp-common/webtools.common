/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: UIContextDetermination.java,v $$
 *  $$Revision: 1.6 $$  $$Date: 2009/07/30 22:11:23 $$ 
 */
package org.eclipse.jem.util;

/**
 * Static utility class for UIContext determination.
 * 
 * @deprecated Replaced by (@link org.eclipse.wst.common.core.util.UIContextDetermination)
 * @since 1.0.0
 */
public class UIContextDetermination extends org.eclipse.wst.common.core.util.UIContextDetermination {

	public static final String HEADLESS_CONTEXT_LITERAL = org.eclipse.wst.common.core.util.UIContextDetermination.HEADLESS_CONTEXT_LITERAL;

	public static final String UI_CONTEXT_LITERAL = org.eclipse.wst.common.core.util.UIContextDetermination.UI_CONTEXT_LITERAL;

	public static final int HEADLESS_CONTEXT = org.eclipse.wst.common.core.util.UIContextDetermination.HEADLESS_CONTEXT;

	public static final int UI_CONTEXT = org.eclipse.wst.common.core.util.UIContextDetermination.UI_CONTEXT;

	private UIContextDetermination(){}

	/**
	 * Returns an instance of a given class based on the UI or Headless context.
	 * 
	 * @param key
	 * @return new class instance for the given key.
	 * @throws IllegalArgumentException
	 *             If the key is invalid (e.g. no extension is found for the key)
	 * @deprecated Replaced by (@link org.eclipse.wst.common.core.util.UIContextDetermination.createInstance())
	 */
	public static Object createInstance(String key) {
		return org.eclipse.wst.common.core.util.UIContextDetermination.createInstance(key);
	}

	/**
	 * Returns the current context -- determines the value if necessary.
	 * 
	 * @return current context
	 * @see #HEADLESS_CONTEXT
	 * @see #UI_CONTEXT
	 * @deprecated Replaced by (@link org.eclipse.wst.common.core.util.UIContextDetermination.getCurrentContext())
	 */
	public static int getCurrentContext() {
		return org.eclipse.wst.common.core.util.UIContextDetermination.getCurrentContext();
	}
}