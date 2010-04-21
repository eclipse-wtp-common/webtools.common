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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.ClasspathContainerWizard;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.common.internal.modulecore.ClasspathContainerVirtualComponent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.WizardFragment;
import org.eclipse.wst.common.componentcore.ui.propertypage.IReferenceWizardConstants;

public class ClasspathContainerWizardFragment extends WizardFragment {
	public boolean hasComposite() {
		return false;
	}
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		final IProject project = (IProject)getTaskModel().getObject(IReferenceWizardConstants.PROJECT);
		final IVirtualComponent rootComp = (IVirtualComponent)getTaskModel().getObject(IReferenceWizardConstants.ROOT_COMPONENT);
		Display.getDefault().syncExec(new Runnable() { 
			public void run() {
				IJavaProject jp = JavaCore.create(project);
				IClasspathEntry[] entries = new IClasspathEntry[]{};
				ClasspathContainerWizard wizard = new ClasspathContainerWizard((IClasspathEntry)null, jp, entries);
				int result = ClasspathContainerWizard.openWizard(new Shell(), wizard);
				if( result == Window.OK ) {
					ArrayList<IVirtualReference> references = new ArrayList<IVirtualReference>();
					IClasspathEntry[] results = wizard.getNewEntries();
					for( int i = 0; i < results.length; i++ ) {
						String defaultPath = (String)getTaskModel().getObject(IReferenceWizardConstants.DEFAULT_LIBRARY_LOCATION);
						defaultPath = defaultPath == null ? "/lib" : defaultPath;
						IVirtualComponent childComp = new ClasspathContainerVirtualComponent(project, rootComp, results[i].getPath().toString());
						VirtualReference ref = new VirtualReference(rootComp, childComp, new Path(defaultPath).makeAbsolute());
						ref.setDependencyType(IVirtualReference.DEPENDENCY_TYPE_CONSUMES);
						references.add(ref);
					}
					getTaskModel().putObject(IReferenceWizardConstants.FINAL_REFERENCE, 
							references.toArray(new IVirtualReference[references.size()]));
				}
			}
		});
	}
}
