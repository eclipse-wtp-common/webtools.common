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
package org.eclipse.wst.common.internal.emfworkbench.edit;



import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.wst.common.internal.emfworkbench.integration.ComposedEditModel;

/**
 * Insert the type's description here. Creation date: (05/21/01 9:31:02 PM)
 * 
 * @author: Administrator
 */
public class WrappingCommandStack extends BasicCommandStack {
	private ComposedEditModel editModel;

	/**
	 * WrappingCommandStack constructor comment.
	 */
	public WrappingCommandStack(ComposedEditModel anEditModel) {
		super();
		editModel = anEditModel;
	}

	@Override
	public void execute(Command command) {
		ParentCommand parent = new ParentCommand(command, editModel);
		super.execute(parent);
	}
}