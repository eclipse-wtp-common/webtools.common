/*******************************************************************************
 * Copyright (c) 2009 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.flat;

/**
 * 
 * This interface is not intended to be implemented by clients
 *
 */
public interface IFlatFile extends IFlatResource {
	/**
	 * Returns a modification stamp. Whenever the modification
	 * stamp changes, there may have been a change to the file.
	 * 
	 * @return the modification stamp
	 */
	public long getModificationStamp();
}