/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.operations;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.internal.ValidationMigrator;


/**
 * Transition builder to phase out the old validation builder and phase in the new. This class will
 * be around as long as we support 4.X workspaces.
 */
public class TransitionValidationBuilder extends IncrementalProjectBuilder {
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		// Phase out this builder gracefully by removing its build command
		// from the IProject's list of commands, and replacing it with the
		// new validation builder's build command. Once this builder's build
		// command is removed from this project, this builder will not be
		// called again.
		ValidationMigrator.singleton().migrate(monitor, getProject());


		return null; // no other projects should be built after this one
	}
}