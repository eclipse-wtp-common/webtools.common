/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.validation.Validator;

/**
 * Validation preferences for a particular project.
 * @author karasiuk
 *
 */
public class ProjectPreferences {
	/** false - Default setting for the "should all the validation be suspended" setting. */ 
	public static final boolean DefaultSuspend = false;
	
	/** false - Default setting for letting projects override the global settings. */
	public static final boolean DefaultOverride = false;
	
	private IProject	_project;

	private boolean 	_override = DefaultOverride;
	private boolean		_suspend = DefaultSuspend;
	
	private Validator[]	_validators = new Validator[0];
	
	public ProjectPreferences(IProject project){
		_project = project;
	}
	
	public boolean getOverride() {
		return _override;
	}
	public void setOverride(boolean override) {
		_override = override;
	}
	public boolean getSuspend() {
		return _suspend;
	}
	public void setSuspend(boolean suspend) {
		_suspend = suspend;
	}
	
	/**
	 * Answer the validators that have been registered for this project.
	 * @return an empty array if there are no validators.
	 */
	public Validator[] getValidators() {
		return _validators;
	}
	
	public void setValidators(Validator[] validators){
		_validators = validators;
	}
	
	public IProject getProject() {
		return _project;
	}

}
