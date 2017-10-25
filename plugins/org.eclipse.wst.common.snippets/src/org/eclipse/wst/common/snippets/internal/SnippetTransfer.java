/*******************************************************************************
 * Copyright (c) 2004, 2017 IBM Corporation and others.
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
import org.eclipse.swt.dnd.TransferData;

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
	
	/**
	 * This records the time at which the transfer data was recorded.
	 */
	protected long startTime;
	
	/**
	 * This records the data being transferred.
	 */
	protected Object object;
	
	/**
	 * This records the object and current time and encodes only the current
	 * time into the transfer data.
	 */
	public void javaToNative(Object object, TransferData transferData) {
		startTime = System.currentTimeMillis();
		this.object = object;
		if (transferData != null) {
			super.javaToNative(String.valueOf(startTime).getBytes(), transferData);
		}
	}

	/**
	 * This decodes the time of the transfer and returns the recorded the
	 * object if the recorded time and the decoded time match.
	 */
	public Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		if (bytes != null && bytes.length > 0) {
			try {
				Long value = Long.valueOf(new String(bytes));
				long clipboardTime = value.longValue();
				return this.startTime == clipboardTime ? object : null;
			}
			catch (NumberFormatException e) {
				// nothing to be done, we can get here via a clipboard operation
			}
		}
		return null;
	}

}
