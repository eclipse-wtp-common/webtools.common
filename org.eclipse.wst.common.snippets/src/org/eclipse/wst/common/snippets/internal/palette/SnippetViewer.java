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
package org.eclipse.wst.common.snippets.internal.palette;

import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.customize.PaletteCustomizerDialog;

public class SnippetViewer extends PaletteViewer {
	private PaletteCustomizerDialog customizerDialog = null;

	public PaletteCustomizerDialog getCustomizerDialog() {
		if (customizerDialog == null) {
			customizerDialog = new SnippetCustomizerDialog(getControl().getShell(), getCustomizer(), getPaletteRoot());
		}
		return customizerDialog;
	}
}