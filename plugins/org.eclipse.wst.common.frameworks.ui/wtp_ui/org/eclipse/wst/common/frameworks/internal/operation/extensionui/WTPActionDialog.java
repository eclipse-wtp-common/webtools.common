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
 * Created on May 4, 2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.common.frameworks.internal.ui.WTPCommonUIResourceHandler;
import org.eclipse.wst.common.frameworks.operations.WTPOperationDataModelEvent;
import org.eclipse.wst.common.frameworks.operations.WTPOperationDataModelListener;


/**
 * @author mdelder
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Generation - Code and Comments
 */
public class WTPActionDialog extends Dialog implements WTPOperationDataModelListener {

	private ITreeViewerListener actionTreeListener;

	private ICheckStateListener checkStateListener;

	private ISelectionChangedListener updateDescriptionSelectionListener;

	private CheckboxTreeViewer checkboxTreeViewer;

	Text descriptionText;

	private final WTPOptionalOperationDataModel operationDataModel;

	/**
	 * This action's id, or <code>null</code> if none.
	 */
	private String id;

	/**
	 * @param arg0
	 */
	public WTPActionDialog(Shell arg0, WTPOptionalOperationDataModel operationDataModel) {
		super(arg0);
		this.operationDataModel = operationDataModel;
		this.operationDataModel.addListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		getShell().setText(WTPCommonUIResourceHandler.getString("WTPActionDialog_UI_0")); //$NON-NLS-1$
		Composite superComposite = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(superComposite, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0; //convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = 0; //convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = 0; //convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = 0; //convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// set infopop based on id if id is set
		if (getId() != null)
			WorkbenchHelp.setHelp(composite, getId());
		Label availableFiltersLabel = new Label(composite, SWT.BOLD);
		GridData availableFiltersLabelGridData = new GridData(GridData.FILL_HORIZONTAL);
		availableFiltersLabelGridData.heightHint = convertHeightInCharsToPixels(1);
		availableFiltersLabel.setLayoutData(availableFiltersLabelGridData);
		availableFiltersLabel.setText(WTPCommonUIResourceHandler.getString("WTPActionDialog_UI_0")); //$NON-NLS-1$

		checkboxTreeViewer = new CheckboxTreeViewer(composite, SWT.CHECK | SWT.BORDER);

		checkboxTreeViewer.setContentProvider(new WTPActionContentProvider());
		checkboxTreeViewer.setLabelProvider(new WTPActionLabelProvider());

		checkboxTreeViewer.setInput(this.operationDataModel);

		Tree tree = checkboxTreeViewer.getTree();
		GridLayout treeLayout = new GridLayout();
		treeLayout.marginHeight = 0; //convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		treeLayout.marginWidth = 0; //convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		treeLayout.verticalSpacing = 0; //convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		treeLayout.horizontalSpacing = 0; //convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		treeLayout.numColumns = 1;
		GridData treeGridData = new GridData(GridData.FILL_HORIZONTAL);
		treeGridData.widthHint = convertHorizontalDLUsToPixels(225);
		treeGridData.heightHint = convertVerticalDLUsToPixels(150);
		tree.setLayout(treeLayout);
		tree.setLayoutData(treeGridData);

		descriptionText = new Text(composite, SWT.BORDER | SWT.WRAP);
		GridData descriptionTextGridData = new GridData(GridData.FILL_HORIZONTAL);
		descriptionTextGridData.heightHint = convertHeightInCharsToPixels(3);
		descriptionText.setLayoutData(descriptionTextGridData);
		descriptionText.setBackground(superComposite.getBackground());

		initListeners();
		updateCheckedItems();

		return composite;
	}

	/**
	 *  
	 */
	protected void initListeners() {
		checkboxTreeViewer.addCheckStateListener(getCheckStateListener());
		checkboxTreeViewer.addSelectionChangedListener(getSelectionListener());
		checkboxTreeViewer.addTreeListener(getActionTreeListener());
	}

	/**
	 * @return
	 */
	protected ICheckStateListener getCheckStateListener() {
		if (checkStateListener == null)
			checkStateListener = new CheckStateListener();
		return checkStateListener;
	}



	public class CheckStateListener implements ICheckStateListener {

		public void checkStateChanged(CheckStateChangedEvent event) {
			boolean checked = event.getChecked();
			IOperationNode node = (IOperationNode) event.getElement();
			node.setChecked(checked);
		}
	}

	public class TreeViewerListener implements ITreeViewerListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeViewerListener#treeCollapsed(org.eclipse.jface.viewers.TreeExpansionEvent)
		 */
		public void treeCollapsed(TreeExpansionEvent event) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeViewerListener#treeExpanded(org.eclipse.jface.viewers.TreeExpansionEvent)
		 */
		public void treeExpanded(TreeExpansionEvent event) {
		}
	}

	public class SelectionChangedListener implements ISelectionChangedListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event) {

			IStructuredSelection structuredSelection = (IStructuredSelection) event.getSelection();
			Object element = structuredSelection.getFirstElement();
			String text = ""; //$NON-NLS-1$
			if (element instanceof IOperationNode)
				text = ((IOperationNode) element).getDescription();
			descriptionText.setText(text != null ? text : ""); //$NON-NLS-1$
		}

	}

	/**
	 * @return
	 */
	private ISelectionChangedListener getSelectionListener() {
		if (updateDescriptionSelectionListener == null)
			updateDescriptionSelectionListener = new SelectionChangedListener();
		return updateDescriptionSelectionListener;
	}

	/**
	 * @return
	 */
	private ITreeViewerListener getActionTreeListener() {
		if (actionTreeListener == null)
			actionTreeListener = new TreeViewerListener();
		return actionTreeListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperationDataModelListener#propertyChanged(org.eclipse.wst.common.frameworks.internal.operation.WTPOperationDataModelEvent)
	 */
	public void propertyChanged(WTPOperationDataModelEvent event) {
		switch (event.getFlag()) {
			case WTPOperationDataModelEvent.PROPERTY_CHG :
				if (WTPOptionalOperationDataModel.OPERATION_TREE.equals(event.getPropertyName()))
					updateCheckedItems();
				break;
		}
	}

	/**
	 *  
	 */
	private void updateCheckedItems() {
		IOperationNode root = (IOperationNode) this.operationDataModel.getProperty(WTPOptionalOperationDataModel.OPERATION_TREE);
		updateCheckedState(root);

	}

	/**
	 * @param root
	 */
	private void updateCheckedState(IOperationNode root) {
		if (root == null)
			return;

		IOperationNode[] children = root.getChildren();
		if (children == null)
			return;
		for (int i = 0; i < children.length; i++) {
			checkboxTreeViewer.setChecked(children[i], children[i].isChecked());
			boolean disabled = (children[i].getParent() != null) ? children[i].getParent().isChecked() : false;
			checkboxTreeViewer.setGrayed(children[i], disabled);
			updateCheckedState(children[i]);
		}
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
}