/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.integration;


/**
 * Insert the type's description here. Creation date: (4/11/2001 4:45:13 PM)
 * 
 * @author: Administrator
 */
public interface EditModelListener {
	/**
	 * An event ocurred on the J2EEEditModel.
	 */
	void editModelChanged(EditModelEvent anEvent);
}