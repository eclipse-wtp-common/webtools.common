/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;

public interface ISimpleValidateEditContext {

	public static final String CLASS_KEY = "ISimpleValidateEditContext"; //$NON-NLS-1$
	
	public IStatus validateEdit(IFile [] files);

}
