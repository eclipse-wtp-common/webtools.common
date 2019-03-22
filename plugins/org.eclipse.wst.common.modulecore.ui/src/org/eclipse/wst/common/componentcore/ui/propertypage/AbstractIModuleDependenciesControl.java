/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.propertypage;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public abstract class AbstractIModuleDependenciesControl implements IModuleDependenciesControl {

	/**
	 * Returns any error/warning messages that are associated with the current content of the page  
	 * @param parent Parent Composite.
	 * @return IStatus with all the error/warning messages associated with the page
	 */
	public IStatus validate() {
		return Status.OK_STATUS;
	}

}
