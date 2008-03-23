/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

/**
 * A class that wants to be notified when the validator preferences change.
 * @author karasiuk
 *
 */
public interface IValChangedListener {
	
	/**
	 * The validators for the project have changed.
	 *  
	 * @param project the project can be null, which means that the global validation preferences have
	 * changed.
	 */
	public void validatorsForProjectChanged(org.eclipse.core.resources.IProject project);
}
