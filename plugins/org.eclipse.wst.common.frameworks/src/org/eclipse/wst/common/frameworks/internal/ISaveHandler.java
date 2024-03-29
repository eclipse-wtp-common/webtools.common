/*******************************************************************************
 * Copyright (c) 2001, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.frameworks.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Defines the API for coping with attempts to overwrite read-only files or save failures
 */
public interface ISaveHandler {
	public void access();

	public void handleSaveFailed(SaveFailedException ex, IProgressMonitor monitor);

	public void release();

	public boolean shouldContinueAndMakeFileEditable(IFile aFile);
}