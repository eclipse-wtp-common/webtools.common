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
package org.eclipse.wst.common.componentcore.ui.internal.propertypage;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.wst.common.componentcore.ui.Messages;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.TaskWizard;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.WizardFragment;
import org.eclipse.wst.common.componentcore.ui.propertypage.IReferenceWizardConstants;

public class NewReferenceWizard extends TaskWizard implements IReferenceWizardConstants {
	private static final Object REFERENCE_FAMILY = new Object();
	public NewReferenceWizard() {
		super(Messages.NewReferenceWizard, new WizardFragment() {
			protected void createChildFragments(List<WizardFragment> list) {
				list.add(new NewReferenceRootWizardFragment());
			}
		});
		setFinishJobFamily(REFERENCE_FAMILY);
	}

	public void init(IWorkbench newWorkbench, IStructuredSelection newSelection) {
		// do nothing
	}
}
