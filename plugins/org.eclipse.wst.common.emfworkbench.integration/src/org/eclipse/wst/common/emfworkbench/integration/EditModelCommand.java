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
package org.eclipse.wst.common.emfworkbench.integration;

import org.eclipse.emf.common.command.Command;



/**
 * Insert the type's description here. Creation date: (05/22/01 8:57:00 AM)
 * 
 * @author: Administrator
 */
public abstract class EditModelCommand extends AbstractEditModelCommand {
	protected EditModelCommand() {
		super();
	}

	public EditModelCommand(Command targetCommand) {
		super(targetCommand);
	}

	public boolean canUndo() {
		return getTarget().canUndo();
	}

	protected abstract void executeInModel(AbstractEditModelCommand cmd);

	public EditModelCommand getEditModelCommand() {
		return this;
	}

	public String getLabel() {
		return getTarget().getLabel();
	}

	public void invertAndPush() {
		executeInModel(this.inverted());
	}
}