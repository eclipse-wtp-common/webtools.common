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
/*
 * Created on May 5, 2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.operation.extension.ui;

/**
 * @author mdelder
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Generation - Code and Comments
 */
public interface IOperationNode {

	public IOperationNode[] getChildren();

	public boolean isChecked();

	/**
	 * Same as calling setChecked(checked, true)
	 * 
	 * @param checked
	 */
	public void setChecked(boolean checked);

	public String getName();

	public String getDescription();

	public IOperationNode getParent();

	/**
	 * @return
	 */
	public boolean isAlwaysExecute();
}