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
package org.eclipse.jst.common.ui.internal.assembly.wizard;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.ui.wizards.BuildPathDialogAccess;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jst.common.ui.internal.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.IWizardHandle;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.WizardFragment;
import org.eclipse.wst.common.componentcore.ui.propertypage.IReferenceWizardConstants;

public class JarReferenceWizardFragment extends WizardFragment {
	protected LabelProvider labelProvider = null;
	protected ITreeContentProvider contentProvider = null;
	protected TreeViewer viewer;
	protected Button browse;
	protected IPath[] paths;
	protected IWizardHandle handle;
	protected IPath[] selected = new IPath[]{};
	boolean isComplete = false;

	public boolean isComplete() {
		return isComplete;
	}
	
	public boolean hasComposite() {
		return true;
	}

	public Composite createComposite(Composite parent, IWizardHandle handle) {
		this.handle = handle;
		handle.setTitle(Messages.ArchiveTitle);
		handle.setDescription(Messages.ArchiveDescription);
				
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new FormLayout());
		viewer = new TreeViewer(c, SWT.SINGLE | SWT.BORDER);
		viewer.setContentProvider(getContentProvider());
		viewer.setLabelProvider(getLabelProvider());
		viewer.setInput(ResourcesPlugin.getWorkspace());

		browse = new Button(c, SWT.NONE);
		browse.setText(Messages.Browse);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 5);
		fd.bottom = new FormAttachment(100, -5);
		browse.setLayoutData(fd);

		fd = new FormData();
		fd.left = new FormAttachment(0, 5);
		fd.top = new FormAttachment(0, 5);
		fd.right = new FormAttachment(100, -5);
		fd.bottom = new FormAttachment(browse, -5);
		viewer.getTree().setLayoutData(fd);

		browse.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				buttonPressed();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		return c;
	}

	protected void buttonPressed() {
		IProject project = (IProject)getTaskModel().getObject(IReferenceWizardConstants.PROJECT);
		selected = BuildPathDialogAccess.chooseJAREntries(
				browse.getShell(), 
				project.getFullPath(), new IPath[0]);
		viewer.refresh();
		if(selected != null && selected.length > 0) {
			isComplete = true;
		} else {
			isComplete = false;
		}
		handle.update();
	}

	public void performFinish(IProgressMonitor monitor) throws CoreException {
		IVirtualComponent rootComponent = (IVirtualComponent)getTaskModel().getObject(IReferenceWizardConstants.ROOT_COMPONENT);
		String runtimeLoc = (String)getTaskModel().getObject(IReferenceWizardConstants.DEFAULT_LIBRARY_LOCATION);
		if (selected != null && selected.length > 0) {
			ArrayList<IVirtualReference> refList = new ArrayList<IVirtualReference>();
			for (int i = 0; i < selected.length; i++) {
				// IPath fullPath = project.getFile(selected[i]).getFullPath();
				String type = VirtualArchiveComponent.LIBARCHIVETYPE
						+ IPath.SEPARATOR;
				IVirtualComponent archive = ComponentCore
						.createArchiveComponent(rootComponent.getProject(),
								type + selected[i].makeRelative().toString());
				VirtualReference ref = new VirtualReference(rootComponent, archive);
				ref.setArchiveName(selected[i].lastSegment());
				if (runtimeLoc != null) {
					ref.setRuntimePath(new Path(runtimeLoc).makeAbsolute());
				}
				refList.add(ref);
			}
			IVirtualReference[] finalRefs = refList.toArray(new IVirtualReference[refList.size()]);
			getTaskModel().putObject(IReferenceWizardConstants.FINAL_REFERENCE, finalRefs);
		}
	}

	protected LabelProvider getLabelProvider() {
		if (labelProvider == null) {
			labelProvider = new LabelProvider() {
				public Image getImage(Object element) {
					return null;
				}

				public String getText(Object element) {
					return element == null ? "" : element.toString();//$NON-NLS-1$
				}
			};
		}
		return labelProvider;
	}

	protected ITreeContentProvider getContentProvider() {
		if (contentProvider == null) {
			contentProvider = new ITreeContentProvider() {
				public Object[] getElements(Object inputElement) {
					return selected == null ? new Object[]{} : selected;
				}
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}
				public void dispose() {
				}
				public boolean hasChildren(Object element) {
					return false;
				}
				public Object getParent(Object element) {
					return null;
				}
				public Object[] getChildren(Object parentElement) {
					return null;
				}
			};
		}
		return contentProvider;
	}
}
