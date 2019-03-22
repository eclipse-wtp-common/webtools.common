/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;

/**
 * ValidatorHelper extensions must implement this interface.
 */
public interface IProjectValidationHelper {
	
	public IContainer[] getOutputContainers(IProject project);
	
	public IContainer[] getSourceContainers(IProject project);
	
	public void disposeInstance();

}
