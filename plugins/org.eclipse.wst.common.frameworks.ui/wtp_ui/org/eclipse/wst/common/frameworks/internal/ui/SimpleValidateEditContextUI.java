package org.eclipse.wst.common.frameworks.internal.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.frameworks.internal.ISimpleValidateEditContext;
import org.eclipse.wst.common.frameworks.internal.SimpleValidateEditContextHeadless;

public class SimpleValidateEditContextUI extends SimpleValidateEditContextHeadless implements ISimpleValidateEditContext {

	protected IStatus validateEditImpl(final IFile[] filesToValidate) {
		final IStatus [] status = new IStatus[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				status[0] = ResourcesPlugin.getWorkspace().validateEdit(filesToValidate, Display.getCurrent().getActiveShell());
			}
		});
		return status[0];
	}

}
