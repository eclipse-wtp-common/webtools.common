/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.core.search.internal;

import org.eclipse.osgi.util.NLS;

// issue (cs) is this a UI issue?  where does the framework need to expose stings?
public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "org.eclipse.wst.common.core.search.internal.messages";//$NON-NLS-1$

	public static String engine_searching;

	public static String engine_searching_locatingDocuments;

	public static String engine_searching_matching;

	private Messages()
	{
		// Do not instantiate
	}

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
