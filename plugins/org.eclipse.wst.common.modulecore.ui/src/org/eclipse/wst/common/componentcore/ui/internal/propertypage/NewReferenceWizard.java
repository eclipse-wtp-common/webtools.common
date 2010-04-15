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
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.Messages;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.DependencyPageExtensionManager.ReferenceExtension;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.TaskWizard;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.WizardFragment;
import org.eclipse.wst.common.componentcore.ui.propertypage.IReferenceWizardConstants;

public class NewReferenceWizard extends TaskWizard implements IReferenceWizardConstants {
	private static final Object REFERENCE_FAMILY = new Object();
	public NewReferenceWizard() {
		this(null);
	}
	public NewReferenceWizard(ReferenceExtension[] extensions) {
		super(Messages.NewReferenceWizard, new RootWizardFragment(extensions));
		setFinishJobFamily(REFERENCE_FAMILY);
		getRootFragment().setTaskModel(getTaskModel());
	}
	protected static class RootWizardFragment extends WizardFragment {
		private ReferenceExtension[] extensions = null;
		public RootWizardFragment() {}
		public RootWizardFragment(ReferenceExtension[] extensions) {
			this.extensions = extensions;
		}

		protected void createChildFragments(List<WizardFragment> list) {
			IVirtualReference origRef = (IVirtualReference)getTaskModel().getObject(ORIGINAL_REFERENCE);
			if( origRef == null )
				list.add(new NewReferenceRootWizardFragment(extensions));
			else {
				WizardFragment fragment = getFirstEditingFragment(origRef);
				if( fragment != null )
					list.add(fragment);
			}
			if( list.size() == 0 )
				setComplete(false);
		}
	}

	public static WizardFragment getFirstEditingFragment(IVirtualReference reference) {
		WizardFragment[] frags = DependencyPageExtensionManager.getManager().loadAllReferenceWizardFragments();
		for( int i = 0; i < frags.length; i++ ) {
			if( frags[i] instanceof IReferenceEditor ) {
				if( ((IReferenceEditor)frags[i]).canEdit(reference)) {
					// accept first one
					return frags[i];
				}
			}
		}
		return null;
	}
	
	public void init(IWorkbench newWorkbench, IStructuredSelection newSelection) {
		// do nothing
	}
}
