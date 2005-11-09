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
package org.eclipse.wst.common.snippets.internal.dnd;

import org.eclipse.swt.dnd.ByteArrayTransfer;

public class SnippetTextTransfer extends ByteArrayTransfer {

	private static SnippetTextTransfer instance;

	private static final String LOCAL_NAME = "Text with Variables" + System.currentTimeMillis(); //$NON-NLS-1$
	private static final int LOCAL_TYPE = registerType(LOCAL_NAME);

	private static String[] names = null;
	private static int types[] = null;

	public static SnippetTextTransfer getTransferInstance() {
		if (instance == null) {
			instance = new SnippetTextTransfer();
			init();
		}
		return instance;
	}

	private static void init() {
		types = new int[]{LOCAL_TYPE};
		names = new String[]{LOCAL_NAME};
	}

	private SnippetTextTransfer() {
		super();
	}

	protected int[] getTypeIds() {
		return types;
	}

	protected String[] getTypeNames() {
		return names;
	}

}