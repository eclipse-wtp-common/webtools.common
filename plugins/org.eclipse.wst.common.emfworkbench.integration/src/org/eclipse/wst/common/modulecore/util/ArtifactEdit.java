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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.modulecore.ModuleCoreFactory;
import org.eclipse.wst.common.modulecore.ModuleCoreNature;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.ModuleEditModel;
import org.eclipse.wst.common.modulecore.ModuleStructuralModel;
import org.eclipse.wst.common.modulecore.ProjectModules;
import org.eclipse.wst.common.modulecore.WorkbenchModule;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ArtifactEdit {
	
	
	public static ArtifactEdit INSTANCE = new ArtifactEdit();

	/*
	 * Javadoc copied from interface.
	 */
	public ModuleEditModel getModuleEditModelForRead(WorkbenchModule wbModule, Object anAccessorKey) {
		ModuleCoreNature aNature = ModuleCoreNature.getModuleCoreNature(wbModule.getProject());
		return aNature.getModuleEditModelForRead(wbModule.getHandle(),anAccessorKey);
	}
	public ModuleEditModel getModuleEditModelForWrite(WorkbenchModule wbModule, Object anAccessorKey) {
		ModuleCoreNature aNature = ModuleCoreNature.getModuleCoreNature(wbModule.getProject());
		return aNature.getModuleEditModelForWrite(wbModule.getHandle(),anAccessorKey);
	}

}
