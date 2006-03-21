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

package org.eclipse.wst.validation.internal.delegates;

import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * A delegating validator delegates the actual validation work to a delegate
 * validator. This is a marker interface used internally by the framework to
 * determine if a delegate is a delegating validator.
 * 
 * [Issue] Could/should this interface be replaced with an attribute on the
 * validators extension definition?
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still
 * under development and expected to change significantly before reaching
 * stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 * </p>
 */
public interface IDelegatingValidator extends IValidator
{
}
