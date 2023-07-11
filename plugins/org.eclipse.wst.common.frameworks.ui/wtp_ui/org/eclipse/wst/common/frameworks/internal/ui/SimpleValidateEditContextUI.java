/*******************************************************************************
 * Copyright (c) 2005, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.frameworks.internal.SimpleValidateEditContextHeadless;

public class SimpleValidateEditContextUI extends SimpleValidateEditContextHeadless {

	@Override
	protected IStatus validateEditImpl(final IFile[] filesToValidate) {
		final IStatus [] status = new IStatus[1];
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				status[0] = ResourcesPlugin.getWorkspace().validateEdit(filesToValidate, Display.getCurrent().getActiveShell());
			}
		});
		return status[0];
	}

}
