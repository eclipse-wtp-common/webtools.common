/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.edit;



import org.eclipse.emf.common.command.Command;
import org.eclipse.wst.common.internal.emfworkbench.integration.AbstractEditModelCommand;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelCommand;

/**
 * Insert the type's description here. Creation date: (05/22/01 8:58:24 AM)
 * 
 * @author: Administrator
 */
public class InvertedCommand extends AbstractEditModelCommand {
	public InvertedCommand(Command targetCommand) {
		super(targetCommand);
	}

	@Override
	public boolean canExecute() {
		return getTarget().canUndo();
	}

	@Override
	public boolean canUndo() {
		return getTarget().canExecute();
	}

	/**
	 * Does nothing
	 */
	public void execute() {
		//does nothing
	}

	/**
	 * getEditModelCommand method comment.
	 */
	@Override
	public EditModelCommand getEditModelCommand() {
		return ((AbstractEditModelCommand) getTarget()).getEditModelCommand();
	}

	protected int inversionDepth() {
		if (getEditModelCommand() == getTarget())
			return 1;
		return ((InvertedCommand) getTarget()).inversionDepth() + 1;
	}

	protected String labelPrefix() {
		return inversionDepth() % 2 == 1 ? "Undo " : "Redo ";//$NON-NLS-2$//$NON-NLS-1$
	}

	public void redo() {
		getTarget().undo();
	}

	@Override
	public void undo() {
		getTarget().redo();
	}
}
