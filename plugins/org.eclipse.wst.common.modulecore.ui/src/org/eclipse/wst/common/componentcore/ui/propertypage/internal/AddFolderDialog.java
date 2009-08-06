/******************************************************************************
 * Copyright (c) 2009 Red Hat
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.propertypage.internal;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.ui.Messages;

public class AddFolderDialog extends TitleAreaDialog {
	private IProject project;
	private TreeViewer viewer;
	private IContainer selected = null;
	public AddFolderDialog(Shell parentShell, IProject project) {
		super(parentShell);
		this.project = project;
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.AddFolder);
		shell.setBounds(shell.getLocation().x, shell.getLocation().y, 400,300);
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite c = (Composite)super.createDialogArea(parent);
		this.viewer = new TreeViewer(c, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(getContentProvider());
		viewer.setLabelProvider(getLabelProvider());
		viewer.addFilter(getFilter());
		viewer.setInput(project);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.addSelectionChangedListener(getListener());
		return c;
	}
	
	protected ISelectionChangedListener getListener() {
		return new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
				Object first = sel.getFirstElement();
				if( first instanceof IContainer) 
					selected = (IContainer)first;
			}
		};
	}
	
	public IContainer getSelected() {
		return selected;
	}
	
	protected ViewerFilter getFilter() {
		return new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				return element instanceof IContainer;
			}
		};
	}
	
	protected ITreeContentProvider getContentProvider() {
		return new ITreeContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			
			public void dispose() {
			}
			
			public Object[] getElements(Object inputElement) {
				try {
					return project.members();
				} catch( CoreException ce ) {
					return new Object[]{};
				}
			}
			
			public boolean hasChildren(Object element) {
				if( element instanceof IContainer) {
					try {
						return ((IContainer)element).members().length > 0;
					} catch( CoreException ce ) {
					}
				}
				return false;
			}
			
			public Object getParent(Object element) {
				if( element instanceof IResource)
					return ((IResource)element).getParent();
				return null;
			}
			
			public Object[] getChildren(Object parentElement) {
				if( parentElement instanceof IContainer) {
					try {
						return ((IContainer)parentElement).members();
					} catch( CoreException ce ) {
					}
				}
				return new Object[]{};
			}
		};
	}

	protected LabelProvider getLabelProvider() {
		return new LabelProvider() {
			public Image getImage(Object element) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
			}
			public String getText(Object element) {
				if( element instanceof IResource)
					return ((IResource)element).getName();
				return element.toString();
			}
		};
	}
}
