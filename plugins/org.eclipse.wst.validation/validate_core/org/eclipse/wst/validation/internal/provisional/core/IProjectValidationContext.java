/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.validation.internal.provisional.core;

import org.eclipse.core.resources.IProject;

/**
 * IProjectValidationContext extends IValidationContext to provide access to a
 * reference to the project on which a validator is being invoked.
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still
 * under development and expected to change significantly before reaching
 * stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 * </p>
 */
public interface IProjectValidationContext extends IValidationContext
{
  /**
   * Provides the project on which the validator is being invoked.
   * 
   * @return an IProject reference to the project on which the validator is
   *         being invoked.
   */
  IProject getProject();
}
