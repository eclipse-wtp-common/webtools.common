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
/*
 * Created on Nov 26, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.internal.emfworkbench.integration;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;


/**
 * @author DABERG
 * 
 * This class does not actually execute any commands. It merely gathers the commands to be executed
 * and compounds them so that they can be executed against the actualCommandStack.
 */
public class CompoundingCommandStack implements CommandStack {
	private CommandStack actualCommandStack;
	private Command compoundCommand;

	/**
	 *  
	 */
	public CompoundingCommandStack(CommandStack actualCommandStack) {
		this.actualCommandStack = actualCommandStack;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#execute(org.eclipse.emf.common.command.Command)
	 */
	@Override
	public void execute(Command command) {
		if (compoundCommand == null)
			compoundCommand = command;
		else
			compoundCommand = compoundCommand.chain(command);
	}

	public void performExecution() {
		if (compoundCommand != null) {
			try {
				actualCommandStack.execute(compoundCommand);
			} finally {
				compoundCommand = null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#canUndo()
	 */
	@Override
	public boolean canUndo() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#undo()
	 */
	@Override
	public void undo() {
		//default
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#canRedo()
	 */
	@Override
	public boolean canRedo() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#getUndoCommand()
	 */
	@Override
	public Command getUndoCommand() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#getRedoCommand()
	 */
	@Override
	public Command getRedoCommand() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#getMostRecentCommand()
	 */
	@Override
	public Command getMostRecentCommand() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#redo()
	 */
	@Override
	public void redo() {
		//redo
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#flush()
	 */
	@Override
	public void flush() {
		//flush
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#addCommandStackListener(org.eclipse.emf.common.command.CommandStackListener)
	 */
	@Override
	public void addCommandStackListener(CommandStackListener listener) {
		//default
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#removeCommandStackListener(org.eclipse.emf.common.command.CommandStackListener)
	 */
	@Override
	public void removeCommandStackListener(CommandStackListener listener) {
		//default
	}
}
