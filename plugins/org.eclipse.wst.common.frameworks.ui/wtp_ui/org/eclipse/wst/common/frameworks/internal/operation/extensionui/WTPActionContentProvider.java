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
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author mdelder
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Generation - Code and Comments
 */
public class WTPActionContentProvider implements ITreeContentProvider {

	/**
	 *  
	 */
	public WTPActionContentProvider() {
		super();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		IOperationNode[] children = null;
		if (parentElement instanceof IOperationNode)
			children = ((IOperationNode) parentElement).getChildren();
		return (children != null) ? WTPOptionalOperationDataModel.filterRequiredChildren(children) : new Object[0];
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {

		if (element instanceof IOperationNode)
			return ((IOperationNode) element).getParent();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {

		return getChildren(element).length != 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		IOperationNode[] elements = null;
		if (inputElement != null && inputElement instanceof WTPOptionalOperationDataModel) {
			IOperationNode root = ((WTPOptionalOperationDataModel) inputElement).getOperationTree();
			if (root != null)
				elements = root.getChildren();
		}
		return (elements != null) ? WTPOptionalOperationDataModel.filterRequiredChildren(elements) : new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

}