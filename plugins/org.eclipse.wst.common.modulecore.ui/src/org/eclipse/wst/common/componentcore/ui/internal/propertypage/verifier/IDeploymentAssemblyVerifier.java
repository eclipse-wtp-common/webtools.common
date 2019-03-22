/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.internal.propertypage.verifier;

import org.eclipse.core.runtime.IStatus;

/**
 * This interface is used to allow verifications from extending plugins registered by runtime targets and component type
 * This interface in NOT meant to be implemented, but instead extend the abstract class AbstractDeploymentAssemblyVerifier
 *
 */
public interface IDeploymentAssemblyVerifier {
	/**
	 * Validate the current state of the component resource and reference mappings
	 * 
	 * @param data - All of the relevant dialog properties/values is sent via a DeploymentAssemblyVerifierData object
	 * @return IStatus indicating OK, Warning , or Error.  Warning text will display in the wizard dialog, but not prevent completion. 
	 * Error status will prevent completion
	 */
	IStatus verify(DeploymentAssemblyVerifierData data);

}
