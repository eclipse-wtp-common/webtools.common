/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
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
public final class ProjectPreferences {
	/*
	 * Before this object can be considered immutable, the mutable validators need to be addressed.
	 */
	/** false - Default setting for the "should all the validation be suspended" setting. */ 
	public static final boolean DefaultSuspend = false;
	
	/** false - Default setting for letting projects override the global settings. */
	public static final boolean DefaultOverride = false;
	
	private final IProject	_project;

	private final boolean 	_override;
	private final boolean	_suspend;
	
	private final Validator[]	_validators;
	
	public ProjectPreferences(IProject project){
		_project = project;
		_override = DefaultOverride;
		_suspend  = DefaultSuspend;
		_validators = new Validator[0];
	}
	
	public ProjectPreferences(IProject project, boolean override, boolean suspend, Validator[] validators){
		_project = project;
		_override = override;
		_suspend = suspend;
		_validators = validators;
	}
	
	public boolean getOverride() {
		return _override;
	}
	public boolean getSuspend() {
		return _suspend;
	}
	
	/**
	 * Answer the validators that have been registered for this project.
	 * @return an empty array if there are no validators.
	 */
	public Validator[] getValidators() {
		return _validators;
	}
	
	public IProject getProject() {
		return _project;
	}

}
