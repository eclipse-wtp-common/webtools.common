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
	public boolean canUndo() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#undo()
	 */
	public void undo() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#canRedo()
	 */
	public boolean canRedo() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#getUndoCommand()
	 */
	public Command getUndoCommand() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#getRedoCommand()
	 */
	public Command getRedoCommand() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#getMostRecentCommand()
	 */
	public Command getMostRecentCommand() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#redo()
	 */
	public void redo() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#flush()
	 */
	public void flush() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#addCommandStackListener(org.eclipse.emf.common.command.CommandStackListener)
	 */
	public void addCommandStackListener(CommandStackListener listener) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.command.CommandStack#removeCommandStackListener(org.eclipse.emf.common.command.CommandStackListener)
	 */
	public void removeCommandStackListener(CommandStackListener listener) {
	}
}