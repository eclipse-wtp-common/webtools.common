/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation 
 *
 * Derived from org.eclipse.search.internal.ui.IFileSearchContentProvider
 *******************************************************************************/
package org.eclipse.wst.common.ui.internal.search.basecode;

public interface IFileSearchContentProvider {

	public abstract void elementsChanged(Object[] updatedElements);

	public abstract void clear();

}
