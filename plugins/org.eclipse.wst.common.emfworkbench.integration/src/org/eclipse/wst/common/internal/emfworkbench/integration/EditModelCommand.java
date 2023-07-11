/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.integration;

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

	@Override
	public boolean canUndo() {
		return getTarget().canUndo();
	}

	protected abstract void executeInModel(AbstractEditModelCommand cmd);

	@Override
	public EditModelCommand getEditModelCommand() {
		return this;
	}

	@Override
	public String getLabel() {
		return getTarget().getLabel();
	}

	public void invertAndPush() {
		executeInModel(this.inverted());
	}
}