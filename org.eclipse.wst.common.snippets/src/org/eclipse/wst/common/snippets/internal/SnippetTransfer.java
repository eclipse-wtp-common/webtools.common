/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal;



import org.eclipse.swt.dnd.ByteArrayTransfer;

/**
 * Transfer type used for clipboard operations
 * 
 * @see org.eclipse.swt.dnd.ClipBoard
 */
public class SnippetTransfer extends ByteArrayTransfer {

	private static SnippetTransfer instance;

	private static final String LOCAL_NAME = "Snippet " + System.currentTimeMillis(); //$NON-NLS-1$
	private static final int LOCAL_TYPE = registerType(LOCAL_NAME);

	private static String[] names = null;
	private static int types[] = null;

	/**
	 * @return the registered Transfer instance
	 */
	public static SnippetTransfer getTransferInstance() {
		if (instance == null) {
			instance = new SnippetTransfer();
			init();
		}
		return instance;
	}

	private static void init() {
		types = new int[]{LOCAL_TYPE};
		names = new String[]{LOCAL_NAME};
	}

	private SnippetTransfer() {
		super();
	}

	protected int[] getTypeIds() {
		return types;
	}

	protected String[] getTypeNames() {
		return names;
	}

}