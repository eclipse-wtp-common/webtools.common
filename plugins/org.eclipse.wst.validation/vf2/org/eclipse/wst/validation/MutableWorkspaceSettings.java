/*******************************************************************************
 * Copyright (c) 2009, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;

import org.eclipse.wst.validation.internal.model.GlobalPreferencesValues;


/**
 * This class holds the overall workspace validation settings.
 * <p>
 * The following procedure is used to change a project's Validation settings.
 * <ol>
 * <li>The MutableWorkspaceSettings are retrieved.</li>
 * <li>The MutableWorkspaceSettings are changed.</li>
 * <li>The MutableWorkspaceSettings are "applied".</li>
 * </ol>
 * </p>
 * <p>
 * These settings can be retrieved with {@link ValidationFramework#getWorkspaceSettings()}.
 * </p>
 * @author karasiuk
 *
 */
public final class MutableWorkspaceSettings {

	private final GlobalPreferencesValues _values;
	
	private final IMutableValidator[] _validators;
	
	public IMutableValidator[] getValidators() {
		return _validators;
	}

	public MutableWorkspaceSettings(IMutableValidator[] validators, GlobalPreferencesValues values){
		_validators = validators;
		_values = values;
	}
	
	public GlobalPreferencesValues getGlobalPreferencesValues(){
		return _values;
	}
	
	public boolean getAutoSave() {
		return _values.saveAutomatically;
	}

	public void setAutoSave(boolean autoSave) {
		_values.saveAutomatically = autoSave;
	}

	/**
	 * Can this project override the workspace level validation settings?
	 */
	public boolean getOverride() {
		return _values.override;
	}

	/**
	 * Change whether this project can override workspace level validation settings.
	 * @param override Set to true if the project is allowed to override workspace level validation settings.
	 */
	public void setOverride(boolean override) {
		_values.override = override;
	}

	/**
	 * Is validation suspended for this project?
	 */
	public boolean getSuspend() {
		return _values.disableAllValidation;
	}

	/**
	 * Change whether this project is suspending it's validation.
	 * @param suspend Set to true, to suspend validation for this project.
	 */
	public void setSuspend(boolean suspend) {
		_values.disableAllValidation = suspend;
	}

}
