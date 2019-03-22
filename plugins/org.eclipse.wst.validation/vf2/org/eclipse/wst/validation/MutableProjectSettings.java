/*******************************************************************************
 * Copyright (c) 2009, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;

import org.eclipse.core.resources.IProject;

/**
 * This class holds the overall project validation settings.
 * <p>
 * The following procedure is used to change a project's Validation settings.
 * <ol>
 * <li>The MutableProjectSettings are retrieved.</li>
 * <li>The MutableProjectSettings are changed.</li>
 * <li>The MutableProjectSettings are "applied".</li>
 * </ol>
 * </p>
 * <p>In order for a project's validation setting to be effective, both 
 * {@link #getOverride()} and {@link MutableWorkspaceSettings#getOverride()}
 * must be true.
 * </p>
 * <p>
 * These settings can be retrieved with {@link ValidationFramework#getProjectSettings(IProject)}.
 * </p>
 * @author karasiuk
 *
 */
public final class MutableProjectSettings {
	private final IProject	_project;

	private boolean _override;
	private boolean	_suspend;
	private final IMutableValidator[] _validators;
	
	public IMutableValidator[] getValidators() {
		return _validators;
	}

	public MutableProjectSettings(IProject project, IMutableValidator[] validators){
		_project = project;
		_validators = validators;
	}

	/**
	 * Can this project override the workspace level validation settings?
	 */
	public boolean getOverride() {
		return _override;
	}

	/**
	 * Change whether this project can override workspace level validation settings.
	 * @param override Set to true if the project is allowed to override workspace level validation settings.
	 */
	public void setOverride(boolean override) {
		_override = override;
	}

	/**
	 * Is validation suspended for this project?
	 */
	public boolean getSuspend() {
		return _suspend;
	}

	/**
	 * Change whether this project is suspending it's validation.
	 * @param suspend Set to true, to suspend validation for this project.
	 */
	public void setSuspend(boolean suspend) {
		_suspend = suspend;
	}

	/**
	 * Answer the project that these settings are for.
	 */
	public IProject getProject() {
		return _project;
	}

}
