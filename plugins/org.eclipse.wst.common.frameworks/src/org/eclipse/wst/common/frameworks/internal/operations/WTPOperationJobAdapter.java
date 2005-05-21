/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
/*
 * Created on Jan 20, 2004
 * 
 * To change the template for this generated file go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.internal.WTPResourceHandler;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;


public class WTPOperationJobAdapter extends Job {

	private WTPOperation operation = null;

	public WTPOperationJobAdapter(WTPOperation operation) {
		super(operation.toString());
		this.operation = operation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		IStatus result = null;
		try {
			operation.run(monitor);
			result = operation.getStatus(); //new Status(IStatus.OK, WTPCommonPlugin.PLUGIN_ID,
											// IStatus.OK, WTPResourceHandler.getString("26"),
											// null); //$NON-NLS-1$
		} catch (Exception e) {
			operation.addStatus(new Status(IStatus.ERROR, WTPCommonPlugin.PLUGIN_ID, IStatus.ERROR, WTPResourceHandler.getString("27"), e)); //$NON-NLS-1$
			result = operation.getStatus();
			Logger.getLogger().logError(e);
		}
		return result;
	}

}