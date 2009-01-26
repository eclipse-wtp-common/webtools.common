/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
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
