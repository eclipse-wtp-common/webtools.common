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
/*
 * Created on May 5, 2004
 */
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

/**
 * @author mdelder
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