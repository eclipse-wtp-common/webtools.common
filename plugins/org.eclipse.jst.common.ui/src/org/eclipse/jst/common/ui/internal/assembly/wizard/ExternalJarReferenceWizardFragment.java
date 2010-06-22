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
import org.eclipse.jst.common.ui.internal.IJstCommonUIConstants;
import org.eclipse.jst.common.ui.internal.IJstCommonUIContextIds;
import org.eclipse.jst.common.ui.internal.JstCommonUIPlugin;
import org.eclipse.jst.common.ui.internal.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.IWizardHandle;
import org.eclipse.wst.common.componentcore.ui.propertypage.IReferenceWizardConstants;

public class ExternalJarReferenceWizardFragment extends JarReferenceWizardFragment {	
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		Composite c = super.createComposite(parent, handle);
		handle.setTitle(Messages.ExternalArchiveTitle);
		handle.setDescription(Messages.ExternalArchiveDescription);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(c, IJstCommonUIContextIds.DEPLOYMENT_ASSEMBLY_NEW_EXTERNAL_ARCHIVE_REFERENCE_P1);
		return c;
	}

	protected void buttonPressed() {
		selected = chooseExternalArchiveEntries(browse.getShell());
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
			ArrayList<String> paths = new ArrayList<String>();
			for (int i = 0; i < selected.length; i++) {
				// IPath fullPath = project.getFile(selected[i]).getFullPath();
				String type = VirtualArchiveComponent.LIBARCHIVETYPE
						+ IPath.SEPARATOR;
				IVirtualComponent archive = ComponentCore
						.createArchiveComponent(rootComponent.getProject(),
								type + selected[i].toString());
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
	
	private static IPath[] chooseExternalArchiveEntries(Shell shell) {
		String lastUsedPath= JstCommonUIPlugin.getDefault().getDialogSettings().get(IJstCommonUIConstants.DIALOGSTORE_LASTEXTARCHIVE);
		if (lastUsedPath == null) {
			lastUsedPath= ""; //$NON-NLS-1$
		}
		
		FileDialog dialog= new FileDialog(shell, SWT.MULTI);
		dialog.setText(Messages.ArchiveDialogNewTitle);
		String [] extensions = new String[] {"*.jar;*.war;*.rar;*.zip", "*.*"};  //$NON-NLS-1$//$NON-NLS-2$
		dialog.setFilterExtensions(extensions);
		dialog.setFilterPath(lastUsedPath);

		String res= dialog.open();
		if (res == null) {
			return null;
		}
		String[] fileNames= dialog.getFileNames();
		int nChosen= fileNames.length;

		IPath filterPath= Path.fromOSString(dialog.getFilterPath());
		IPath[] elems= new IPath[nChosen];
		for (int i= 0; i < nChosen; i++) {
			elems[i]= filterPath.append(fileNames[i]).makeAbsolute();
		}
		
		JstCommonUIPlugin.getDefault().getDialogSettings().put(IJstCommonUIConstants.DIALOGSTORE_LASTEXTARCHIVE, dialog.getFilterPath());
		
		return elems;
	}
}
