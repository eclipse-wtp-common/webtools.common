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
 * Created on Jan 27, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;

/**
 * @author DABERG
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public abstract class WTPWizardSelectionPage extends WTPWizardPage {
	/**
	 * @param model
	 * @param pageName
	 */
	public WTPWizardSelectionPage(WTPOperationDataModel model, String pageName) {
		super(model, pageName);
	}

	/**
	 * @param model
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public WTPWizardSelectionPage(WTPOperationDataModel model, String pageName, String title, ImageDescriptor titleImage) {
		super(model, pageName, title, titleImage);
	}

	/**
	 * The selected node; <code>null</code> if none.
	 */
	private IWizardNode selectedNode = null;

	/**
	 * List of wizard nodes that have cropped up in the past (element type: <code>IWizardNode</code>).
	 */
	private List selectedWizardNodes = new ArrayList();

	/**
	 * Adds the given wizard node to the list of selected nodes if it is not already in the list.
	 * 
	 * @param node
	 *            the wizard node, or <code>null</code>
	 */
	private void addSelectedNode(IWizardNode node) {
		if (node == null)
			return;

		if (selectedWizardNodes.contains(node))
			return;

		selectedWizardNodes.add(node);
	}

	/**
	 * The <code>WizardSelectionPage</code> implementation of this <code>IWizardPage</code>
	 * method returns <code>true</code> if there is a selected node.
	 */
	public boolean canFlipToNextPage() {
		return isPageComplete() && selectedNode != null;
	}

	/**
	 * The <code>WizardSelectionPage</code> implementation of an <code>IDialogPage</code> method
	 * disposes of all nested wizards. Subclasses may extend.
	 */
	public void dispose() {
		super.dispose();
		// notify nested wizards
		for (int i = 0; i < selectedWizardNodes.size(); i++) {
			((IWizardNode) selectedWizardNodes.get(i)).dispose();
		}
	}

	/**
	 * The <code>WizardSelectionPage</code> implementation of this <code>IWizardPage</code>
	 * method returns the first page of the currently selected wizard if there is one.
	 */
	public IWizardPage getNextPage() {
		if (selectedNode == null)
			return null;

		boolean isCreated = selectedNode.isContentCreated();

		IWizard wizard = selectedNode.getWizard();

		if (wizard == null) {
			setSelectedNode(null);
			return null;
		}

		if (!isCreated)
			// Allow the wizard to create its pages
			wizard.addPages();

		return wizard.getStartingPage();
	}

	/**
	 * Returns the currently selected wizard node within this page.
	 * 
	 * @return the wizard node, or <code>null</code> if no node is selected
	 */
	public IWizardNode getSelectedNode() {
		return selectedNode;
	}

	/**
	 * Sets or clears the currently selected wizard node within this page.
	 * 
	 * @param node
	 *            the wizard node, or <code>null</code> to clear
	 */
	protected void setSelectedNode(IWizardNode node) {
		addSelectedNode(node);
		selectedNode = node;
		if (isCurrentPage())
			getContainer().updateButtons();
	}
}