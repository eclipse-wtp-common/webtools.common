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
package org.eclipse.wst.common.modulecore.internal.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.modulecore.WorkbenchModuleResource;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public interface IPathProvider {
	
	URI getPath(WorkbenchModuleResource aModuleResource);

}
