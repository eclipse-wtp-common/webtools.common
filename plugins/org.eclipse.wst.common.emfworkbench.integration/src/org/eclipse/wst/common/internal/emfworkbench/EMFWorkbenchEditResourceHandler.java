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
 * Created on May 25, 2004
 */
package org.eclipse.wst.common.internal.emfworkbench;

import org.eclipse.osgi.util.NLS;

/**
 * @author vijayb
 */
public class EMFWorkbenchEditResourceHandler extends NLS {
	private static final String BUNDLE_NAME = "emfworkbenchedit";//$NON-NLS-1$

	private EMFWorkbenchEditResourceHandler() {
		// Do not instantiate
	}

	public static String ClientAccessRegistryException_UI_1;
	public static String ClientAccessRegistryException_UI_0;
	public static String Snapshot_ERROR_0;
	public static String EditModelRegistry_ERROR_2;
	public static String EditModelRegistry_ERROR_1;
	public static String EditModelRegistry_ERROR_0;
	public static String AdapterFactoryDescriptor_ERROR_1;
	public static String AdapterFactoryDescriptor_ERROR_0;
	public static String DynamicAdapterFactory_ERROR_0;
	public static String ClientAccessRegistry_ERROR_1;
	public static String ClientAccessRegistry_ERROR_0;

	static {
		NLS.initializeMessages(BUNDLE_NAME, EMFWorkbenchEditResourceHandler.class);
	}

	public static String getString(String key, Object[] args) {
		return NLS.bind(key, args);
	}
}
