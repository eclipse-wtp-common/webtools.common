/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jem.util.UIContextDetermination;

public class SimpleValidateEdit {

	/**
	 * @param files must contain only {@link IFile}s
	 * @return
	 */
	public static boolean validateEdit(List files){
		if(files == null || files.size() == 0){
			return true;
		}
		return validateEdit( (IFile [])files.toArray(new IFile[files.size()]));
	}
	
	public static boolean validateEdit(IFile[] files) {
		if(files == null || files.length == 0){
			return true;
		}
		ISimpleValidateEditContext validator = (ISimpleValidateEditContext) UIContextDetermination.createInstance(ISimpleValidateEditContext.CLASS_KEY);
		return validator.validateEdit(files).isOK();
	}

}
