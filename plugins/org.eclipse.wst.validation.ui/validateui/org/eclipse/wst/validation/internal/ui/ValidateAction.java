/***************************************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.validation.internal.ui;


import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.wst.validation.internal.ui.plugin.ValidationUIPlugin;


public class ValidateAction extends SelectionListenerAction {
	protected ValidationMenuAction delegate;

	public ValidateAction() {
		super(ValidationUIPlugin.getResourceString(ResourceConstants.VBF_UI_POPUP_RUNVALIDATION));
		delegate = new ValidationMenuAction();
	}

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	public void run() {
		delegate.run(this);
	}

	/**
	 * Updates this action in response to the given selection.
	 * <p>
	 * The <code>SelectionListenerAction</code> implementation of this method returns
	 * <code>true</code>. Subclasses may extend to react to selection changes; however, if the
	 * super method returns <code>false</code>, the overriding method must also return
	 * <code>false</code>.
	 * </p>
	 * 
	 * @param selection
	 *            the new selection
	 * @return <code>true</code> if the action should be enabled for this selection, and
	 *         <code>false</code> otherwise
	 */
	protected boolean updateSelection(IStructuredSelection selection) {
		if (!super.updateSelection(selection))
			return false;

		delegate.selectionChanged(this, selection);

		return isEnabled(); // "Enabled" is set by the delegate.
	}

	/**
	 * If a subclass of ValidateAction enables this menu action on a type that is not an IResource,
	 * this method returns the IResource[] that represent that object. If null is returned, the
	 * object will be ignored and the user will not be notified that the object was not validated.
	 * This method will be called when updateSelection and run are called. (The result of this
	 * method is used to determine which resources will be validated)
	 */
	protected IResource[] getResource(Object selected) {
		return null;
	}
}