/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.wst.common.modulecore.util;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public interface IEditModelHandler {

	void save(IProgressMonitor aMonitor);
	void saveIfNecessary(IProgressMonitor aMonitor);
	void dispose();
}
