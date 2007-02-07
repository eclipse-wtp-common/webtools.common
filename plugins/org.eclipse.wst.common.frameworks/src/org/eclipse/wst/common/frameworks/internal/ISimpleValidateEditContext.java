package org.eclipse.wst.common.frameworks.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;

public interface ISimpleValidateEditContext {

	public static final String CLASS_KEY = "ISimpleValidateEditContext"; //$NON-NLS-1$
	
	public IStatus validateEdit(IFile [] files);

}
