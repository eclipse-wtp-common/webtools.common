/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

public class SimpleValidateEditContextHeadless implements ISimpleValidateEditContext {

	public IStatus validateEdit(IFile[] files) {
		final List filesList = new ArrayList();
		for(int i=0;i<files.length; i++){
			if(files[i].exists() && files[i].isReadOnly()){
				filesList.add(files[i]);
			}
		}
		if(filesList.size() > 0){
			IFile [] filesToValidate = (IFile [])filesList.toArray(new IFile[filesList.size()]);
			return validateEditImpl(filesToValidate);
		}
		return IDataModelProvider.OK_STATUS;
	}

	protected IStatus validateEditImpl(IFile[] filesToValidate) {
		return ResourcesPlugin.getWorkspace().validateEdit(filesToValidate, null);
	}

}
