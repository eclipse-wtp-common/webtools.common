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

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.modulecore.ArtifactEditModel;
import org.eclipse.wst.common.modulecore.ModuleCoreNature;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
import org.eclipse.wst.common.modulecore.impl.UnresolveableURIException;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ArtifactEdit {

	public static String TYPE_ID = "NO_TYPE";
	private ArtifactEditModel artifactEditModel;
	public static final Class ADAPTER_TYPE = ArtifactEdit.class;
	/**
	 * @param model
	 */
	public ArtifactEdit(ArtifactEditModel model) {
		
		artifactEditModel = model;
	}

	/*
	 * Javadoc copied from interface.
	 */
	public static ArtifactEditModel getModuleEditModelForRead(WorkbenchModule aModule, Object anAccessorKey) {
		try {
			IProject project = ModuleCore.getContainingProject(aModule.getHandle());
			ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
			return nature.getModuleEditModelForRead(aModule.getHandle(), anAccessorKey);
		} catch (UnresolveableURIException uue) {
		}
		return null;
	}

	public static ArtifactEditModel getModuleEditModelForWrite(WorkbenchModule aModule, Object anAccessorKey) {
		try {
			IProject project = ModuleCore.getContainingProject(aModule.getHandle());
			ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
			return nature.getModuleEditModelForWrite(aModule.getHandle(), anAccessorKey);
		} catch (UnresolveableURIException uue) {
		}
		return null;

	}
	public ArtifactEditModel getArtifactEditModel() {
		return artifactEditModel;
	}
}
