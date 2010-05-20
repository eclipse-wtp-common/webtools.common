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
package org.eclipse.wst.common.componentcore.ui.internal.propertypage.verifier;

import org.eclipse.core.runtime.IStatus;

/**
 * All extenders of AbstractDeploymentAssemblyVerifier must implement the verify methods to perform any verification on the deployment assembly wizard page
 *
 */
public abstract class AbstractDeploymentAssemblyVerifier implements IDeploymentAssemblyVerifier {

	public abstract IStatus verify(DeploymentAssemblyVerifierData data);

}
