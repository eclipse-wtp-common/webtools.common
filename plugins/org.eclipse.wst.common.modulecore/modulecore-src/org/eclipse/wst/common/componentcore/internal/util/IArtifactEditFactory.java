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
package org.eclipse.wst.common.componentcore.internal.util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public interface IArtifactEditFactory {
	
	public ArtifactEdit createArtifactEditForRead(IVirtualComponent aComponent);
	
	public ArtifactEdit createArtifactEditForWrite(IVirtualComponent aComponent);

}
