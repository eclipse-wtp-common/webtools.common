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
 * Created on May 5, 2004
 * 
 */
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.wst.common.frameworks.internal.operations.ComposedOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperation;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclipse.wst.common.frameworks.internal.ui.WTPCommonUIResourceHandler;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;


/**
 * @author jsholl
 *  
 */
public class WTPOptionalOperationDataModel extends WTPOperationDataModel {



	/**
	 * String, this is required along with a STRUCTURED_SELECTION unless MASTER_OPERATION_LIST is
	 * set
	 */
	public static final String EXTENDED_OPERATION_ID = "WTPOptionalOperationDataModel.EXTENDED_OPERATION_ID"; //$NON-NLS-1$

	/**
	 * IStructuredSelection, this is required along with a EXTENDED_OPERATION_ID unless
	 * MASTER_OPERATION_LIST is set
	 */
	public static final String STRUCTURED_SELECTION = "WTPOptionalOperationDataModel.STRUCTURED_SELECTION"; //$NON-NLS-1$

	/**
	 * List of MasterDescriptor, required unless both EXTENDED_OPERATION_ID and STRUCTURED_SELECTION
	 * are set
	 */
	public static final String MASTER_OPERATION_LIST = "WTPOptionalOperationDataModel.MASTER_OPERATION_LIST"; //$NON-NLS-1$

	/**
	 * A root IOperationNode whose operation is null and children are the contents of the
	 * MASTER_OPERATION_LIST
	 */
	public static final String OPERATION_TREE = "WTPOptionalOperationDataModel.OPERATION_TREE"; //$NON-NLS-1$

	/**
	 * Optional. Allow the WTPOperationDataModelUICreators access to the workbench site for
	 * additional initializations.
	 */
	public static final String IWORKBENCH_SITE = "IWORKBENCH_SITE"; //$NON-NLS-1$

	public static WTPOptionalOperationDataModel createDataModel(String extendedOperationId, IStructuredSelection selection) {
		WTPOptionalOperationDataModel dataModel = new WTPOptionalOperationDataModel();
		dataModel.setProperty(EXTENDED_OPERATION_ID, extendedOperationId);
		dataModel.setProperty(STRUCTURED_SELECTION, selection);
		return dataModel;
	}

	private class OperationNode implements IOperationNode {

		private WTPOperationDataModel dataModel = null;

		private SlaveDescriptor descriptor = null;

		private List children = null;

		private OperationNode parent = null;

		private boolean checked = false;

		public OperationNode(OperationNode parent, SlaveDescriptor descriptor) {
			this.parent = parent;
			this.descriptor = descriptor;
			verifyRoot();
		}

		private void verifyRoot() {
			if (null == parent) {
				return;
			}
			IOperationNode root = getOperationTree();
			OperationNode node = this;
			while (node.parent != null) {
				node = node.parent;
			}
			if (node != root) {
				throw new RuntimeException();
			}
		}

		public String getName() {
			return descriptor.getName();
		}

		public String getDescription() {
			return descriptor.getDescription();
		}

		public WTPOperationDataModel getDataModel() {
			IWorkbenchSite site = (IWorkbenchSite) getProperty(IWORKBENCH_SITE);
			if (dataModel == null) {
				IOperationNode root = getOperationTree();
				if (this.parent == root) {
					MasterDescriptor masterDescriptor = (MasterDescriptor) descriptor;
					dataModel = masterDescriptor.getCreator().createDataModel(masterDescriptor.getExtendedOperationId(), masterDescriptor.getOperationClass(), getStructuredSelection(), site);
				} else
					dataModel = parent.getDataModel();
			}
			return dataModel;
		}

		public WTPOperation getOperation() {
			WTPOperation operation = descriptor.createOperation();
			WTPOperationDataModel dataModel1 = getDataModel();
			if (dataModel1 != null) {
				if (operation == null)
					operation = dataModel.getDefaultOperation();
				else
					operation.setOperationDataModel(dataModel);
			}
			return operation;
		}

		public IOperationNode[] getChildren() {
			verifyRoot();
			return getChildren(true);
		}

		public IOperationNode[] getChildren(boolean expandChildren) {
			if (expandChildren && null == children && null != parent) {
				children = new ArrayList();
				SlaveDescriptor[] slaveDescriptors = UIOperationExtensionRegistry.INSTANCE.getSlaveDescriptors(descriptor.getOperationClass());
				for (int i = 0; null != slaveDescriptors && i < slaveDescriptors.length; i++) {
					OperationNode child = new OperationNode(this, slaveDescriptors[i]);
					child.checked = checked;
					addChild(child);
				}
			}
			if (null == children) {
				return null;
			}
			OperationNode[] childNodes = new OperationNode[children.size()];
			children.toArray(childNodes);
			return childNodes;
		}

		public void clearChildren() {
			if (null != children) {
				children.clear();
			}
		}

		public void addChild(OperationNode childNode) {
			if (null == children) {
				children = new ArrayList();
			}
			children.add(childNode);
		}

		public boolean isChecked() {
			verifyRoot();
			if (descriptor instanceof MasterDescriptor && ((MasterDescriptor) descriptor).isAlwaysExecute())
				return true;
			return checked;
		}

		public void setChecked(boolean checked) {
			if (descriptor instanceof MasterDescriptor && ((MasterDescriptor) descriptor).isAlwaysExecute())
				return;
			internalSetChecked(checked);
			Object root = getProperty(OPERATION_TREE);
			notifyListeners(OPERATION_TREE);
		}

		public IOperationNode getParent() {
			return parent;
		}

		protected void internalSetChecked(boolean checked1) {
			this.checked = checked1;
			if (checked) {
				OperationNode[] children1 = (OperationNode[]) getChildren(false);
				for (int i = 0; null != children && i < children1.length; i++)
					children1[i].internalSetChecked(checked);
			} else {
				if (this.parent != null)
					this.parent.internalSetChecked(checked);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.common.frameworks.internal.operation.extension.ui.IOperationNode#isAlwaysExecute()
		 */
		public boolean isAlwaysExecute() {
			if (descriptor instanceof MasterDescriptor && ((MasterDescriptor) descriptor).isAlwaysExecute())
				return true;
			return false;
		}
	}

	protected void init() {
		super.init();
		setProperty(OPERATION_TREE, new OperationNode(null, null));
	}

	protected void initValidBaseProperties() {
		super.initValidBaseProperties();
		addValidBaseProperty(MASTER_OPERATION_LIST);
		addValidBaseProperty(OPERATION_TREE);
		addValidBaseProperty(STRUCTURED_SELECTION);
		addValidBaseProperty(EXTENDED_OPERATION_ID);
		addValidBaseProperty(IWORKBENCH_SITE);
	}

	IStructuredSelection getStructuredSelection() {
		return (IStructuredSelection) getProperty(STRUCTURED_SELECTION);
	}

	public IOperationNode getOperationTree() {
		return (IOperationNode) getProperty(OPERATION_TREE);
	}

	public static IOperationNode[] getOptionalChildren(IOperationNode node) {
		IOperationNode[] children = node.getChildren();
		children = filterRequiredChildren(children);
		return children;
	}

	/**
	 * @param children
	 * @return
	 */
	public static IOperationNode[] filterRequiredChildren(IOperationNode[] children) {
		List filteredChildren = new ArrayList(Arrays.asList(children));
		for (int i = 0; i < children.length; i++)
			if (children[i].isAlwaysExecute())
				filteredChildren.remove(children[i]);
		filteredChildren.toArray((children = new IOperationNode[filteredChildren.size()]));
		return children;
	}

	protected boolean doSetProperty(String propertyName, Object propertyValue) {
		boolean returnVal = super.doSetProperty(propertyName, propertyValue);
		if (propertyName.equals(STRUCTURED_SELECTION) || propertyName.equals(EXTENDED_OPERATION_ID)) {
			String extendedOperationID = getStringProperty(EXTENDED_OPERATION_ID);
			IStructuredSelection selection = (IStructuredSelection) getProperty(STRUCTURED_SELECTION);
			setProperty(MASTER_OPERATION_LIST, UIOperationExtensionRegistry.INSTANCE.getExtendedUIOperations(extendedOperationID, selection));
		}
		if (propertyName.equals(MASTER_OPERATION_LIST)) {
			OperationNode rootNode = (OperationNode) getOperationTree();
			rootNode.clearChildren();
			MasterDescriptor[] descriptors = (MasterDescriptor[]) propertyValue;
			if (null != descriptors) {
				for (int i = 0; i < descriptors.length; i++) {
					OperationNode child = new OperationNode(rootNode, descriptors[i]);
					child.setChecked(true);
					rootNode.addChild(child);
				}
			}
			notifyListeners(OPERATION_TREE);
		}
		return returnVal;
	}

	public WTPOperation getDefaultOperation() {
		ComposedOperation operation = new ComposedOperation();
		OperationNode root = (OperationNode) getOperationTree();
		addOperationIfNecessary(operation, root);
		return operation;
	}

	private void addOperationIfNecessary(ComposedOperation operation, OperationNode node) {
		if (node.isAlwaysExecute() || node.isChecked()) {
			WTPOperation op = node.getOperation();
			if (op != null)
				operation.addRunnable(op);
		} else {
			IOperationNode[] children = node.getChildren(false);
			for (int i = 0; null != children && i < children.length; i++)
				addOperationIfNecessary(operation, (OperationNode) children[i]);
		}
	}

	private boolean hasSelectedNodes(OperationNode node) {
		if (node.isChecked()) {
			return true;
		}
		boolean foundSelection = false;
		OperationNode[] children = (OperationNode[]) node.getChildren(false);
		for (int i = 0; !foundSelection && null != children && i < children.length; i++) {
			foundSelection = hasSelectedNodes(children[i]);
		}
		return foundSelection;
	}

	protected IStatus doValidateProperty(String propertyName) {
		if (propertyName.equals(OPERATION_TREE)) {
			OperationNode root = (OperationNode) getOperationTree();
			if (root.isChecked()) {
				return WTPUIPlugin.createErrorStatus(WTPCommonUIResourceHandler.getString("WTPOptionalOperationDataModel_UI_0")); //$NON-NLS-1$
			} else if (!hasSelectedNodes(root)) {
				return WTPUIPlugin.createErrorStatus(WTPCommonUIResourceHandler.getString("WTPOptionalOperationDataModel_UI_1"));} //$NON-NLS-1$
		}
		return super.doValidateProperty(propertyName);
	}
}