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

/**
 * A validator that has fields that can be updated.
 * <p>
 * The following procedure is used to change a Validator's settings.
 * <ol>
 * <li>An IMutableValidator is retrieved.</li>
 * <li>The IMutableValidator is changed.</li>
 * <li>The IMutableValidator is "activated".</li>
 * </ol>
 * </p>
 * <p>The methods {@link ValidationFramework#getProjectSettings(org.eclipse.core.resources.IProject)} and 
 * {@link ValidationFramework#getWorkspaceSettings()} can be used to retrieve IMutableValidator's.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.

 * @author karasiuk
 *
 */
public interface IMutableValidator {
	
	/**
	 * Answer the validator's id.
	 * @return
	 */
	String getId();
	
	/**
	 * Answer the validator's name.
	 * @return
	 */
	String getName();
	
	/**
	 * Answer the validator's class name.
	 * @return
	 */
	String getValidatorClassname();
	
	/**
	 * Answer if the validator is enabled for build based validation.
	 */
	boolean isBuildValidation();
	
	/**
	 * Answer if the validator is enabled for manual based validation.
	 */
	boolean isManualValidation();
	
	/**
	 * Set whether the validator should be enabled for build based validation.
	 */
	void setBuildValidation(boolean build);
	
	/**
	 * Set whether the validator should be enabled for manual based validation.
	 */	
	void setManualValidation(boolean manual);

}
