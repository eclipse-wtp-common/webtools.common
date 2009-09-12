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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.BuildPathDialogAccess;
import org.eclipse.jst.common.ui.internal.Messages;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.IWizardHandle;
import org.eclipse.wst.common.componentcore.ui.propertypage.IReferenceWizardConstants;

public class VariableReferenceWizardFragment extends JarReferenceWizardFragment {
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		Composite c = super.createComposite(parent, handle);
		handle.setTitle(Messages.VariableReferenceTitle);
		handle.setDescription(Messages.VariableReferenceDescription);
		return c;
	}

	protected void buttonPressed() {
		selected = BuildPathDialogAccess.chooseVariableEntries(
				browse.getShell(), new Path[0]);
		viewer.refresh();
	}

	public void performFinish(IProgressMonitor monitor) throws CoreException {
		IVirtualComponent rootComponent = (IVirtualComponent)getTaskModel().getObject(IReferenceWizardConstants.ROOT_COMPONENT);
		if (selected != null && selected.length > 0) {
			ArrayList<IVirtualComponent> compList = new ArrayList<IVirtualComponent>();
			ArrayList<String> paths = new ArrayList<String>();
			for (int i = 0; i < selected.length; i++) {
				IPath resolvedPath = JavaCore.getResolvedVariablePath(selected[i]);
				java.io.File file = new java.io.File(resolvedPath.toOSString());
				if (file.isFile() && file.exists()) {
					String type = VirtualArchiveComponent.VARARCHIVETYPE
							+ IPath.SEPARATOR;
					IVirtualComponent archive = ComponentCore
							.createArchiveComponent(rootComponent.getProject(),
									type + selected[i].toString());
					compList.add(archive);
					paths.add(resolvedPath.lastSegment());
				}
			}
			IVirtualComponent[] components = compList.toArray(new IVirtualComponent[compList.size()]);
			String[] paths2 = paths.toArray(new String[paths.size()]);
			getTaskModel().putObject(IReferenceWizardConstants.COMPONENT, components);
			getTaskModel().putObject(IReferenceWizardConstants.COMPONENT_PATH, paths2);
		}
	}
}
