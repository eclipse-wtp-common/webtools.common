/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.frameworks.internal.ISimpleValidateEditContext;
import org.eclipse.wst.common.frameworks.internal.SimpleValidateEditContextHeadless;

public class SimpleValidateEditContextUI extends SimpleValidateEditContextHeadless implements ISimpleValidateEditContext {

	protected IStatus validateEditImpl(final IFile[] filesToValidate) {
		final IStatus [] status = new IStatus[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				status[0] = ResourcesPlugin.getWorkspace().validateEdit(filesToValidate, Display.getCurrent().getActiveShell());
			}
		});
		return status[0];
	}

}
