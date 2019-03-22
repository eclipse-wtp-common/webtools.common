/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.integration;



import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.command.Command;
import org.eclipse.wst.common.internal.emfworkbench.edit.InvertedCommand;


public abstract class AbstractEditModelCommand extends AbstractCommand {
	private Command target;
	private AbstractEditModelCommand owner;

	protected AbstractEditModelCommand() {
		super();
	}

	public AbstractEditModelCommand(Command targetCommand) {
		super();
		target = targetCommand;
	}

	public abstract EditModelCommand getEditModelCommand();

	protected AbstractEditModelCommand getOutermostCommand() {
		return owner == null ? this : owner.getOutermostCommand();
	}

	protected Command getOwner() {
		return owner;
	}

	public Command getTarget() {
		return target;
	}

	/**
	 * Creates a new inverted command on the outermost command
	 */
	public InvertedCommand inverted() {
		AbstractEditModelCommand outer = getOutermostCommand();
		InvertedCommand cmd = new InvertedCommand(outer);
		outer.setOwner(cmd);
		return cmd;
	}

	protected void setOwner(AbstractEditModelCommand ownerCommand) {
		owner = ownerCommand;
	}
}