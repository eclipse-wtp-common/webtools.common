/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;

import java.util.List;

import org.eclipse.core.resources.IProject;

public interface ICommand2 extends ICommand {

	/**
	 * Accessor for a {@link List} of {@link IProject} that were modified by the execution of this command.
	 * 
	 * @return A {@link List} of {@link IProject} that were modified by the execution of this command.
	 */
	public List getAffectedProjects();
}
