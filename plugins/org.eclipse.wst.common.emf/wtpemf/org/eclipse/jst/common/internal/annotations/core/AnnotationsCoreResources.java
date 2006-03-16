/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.common.internal.annotations.core;

import org.eclipse.osgi.util.NLS;

public final class AnnotationsCoreResources extends NLS {

	private static final String BUNDLE_NAME = "annotationcore";//$NON-NLS-1$

	private AnnotationsCoreResources() {
		// Do not instantiate
	}

	public static String TagSpec_3;
	public static String TagSpec_4;
	public static String TagAttribSpec_6;
	public static String AnnotationTagParser_0;
	public static String AnnotationTagParser_1;
	public static String AnnotationTagRegistry_0;
	public static String AnnotationTagRegistry_9;

	static {
		NLS.initializeMessages(BUNDLE_NAME, AnnotationsCoreResources.class);
	}
}