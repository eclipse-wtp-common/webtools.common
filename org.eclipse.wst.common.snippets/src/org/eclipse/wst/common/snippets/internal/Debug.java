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

/**
 * <p>
 * SSE Snippets Plug-in Debug flags
 * </p>
 * <p>
 * This class contains constants only; it is not intended to be extended.
 * </p>
 */
public class Debug {
	/**
	 * Controls debugging output for loading/storing of the Snippets model
	 */
	public static final boolean debugDefinitionPersistence = false;

	/**
	 * Controls debugging output for drag and drop processing
	 */
	public static final boolean debugDragAndDrop = false;

	/**
	 * Controls debugging output for selection within the view
	 */
	public static final boolean debugPaletteSelection = false;
	/**
	 * Controls debugging output for replacement of the model's category list
	 */
	public static final boolean debugViewerContent = false;
}
