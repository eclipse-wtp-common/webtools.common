/*******************************************************************************
 * Copyright (c) 2006, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.validation.internal.provisional.core;

import org.eclipse.core.resources.IProject;

/**
 * IProjectValidationContext extends IValidationContext to provide access to a
 * reference to the project on which a validator is being invoked. 
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
