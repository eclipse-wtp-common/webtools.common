/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;

/**
 * @author John Mourra
 */
public interface ICommand {

	/*
	 * This will contain the multi-selection of objects to deploy. This selection could be used to
	 * filter elements within a Module. Any other setup code should be done here...
	 */
	void init(Object[] selection);

	/**
	 * @param resource
	 * @param delta
	 * @param context
	 * @return
	 * @throws CoreException
	 */
	public boolean execute(IResource resource, IResourceDelta delta, ICommandContext context) throws CoreException;

}
