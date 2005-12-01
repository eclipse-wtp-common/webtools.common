/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.internal.enablement.EnablementManager;

public class ComposedExtendedOperationHolder {

	private ArrayList preOps = null;
	private ArrayList postOps = null;

	protected ComposedExtendedOperationHolder() {
		super();
	}

	public static ComposedExtendedOperationHolder createExtendedOperationHolder(String operationID) {
		Collection extensions = (Collection) OperationExtensionReader.getExtensionPoints().get(operationID);
		if (extensions == null) {
			return null;
		}

		ComposedExtendedOperationHolder extOperationHolder = new ComposedExtendedOperationHolder();
		Object preOp = null;
		Object postOp = null;
		OperationExtension currentExt = null;
		for (Iterator iterator = extensions.iterator(); iterator.hasNext();) {
			currentExt = (OperationExtension) iterator.next();
			if (EnablementManager.INSTANCE.getIdentifier(currentExt.getExtensionId(), null).isEnabled()) {
				try {
					preOp = currentExt.getPreOperation();
					if (preOp != null) {
						extOperationHolder.addPreOperation(preOp);
					}
				} catch (CoreException e) {
					Logger.getLogger().logError(e);
				}
				try {
					postOp = currentExt.getPostOperation();
					if (postOp != null) {
						extOperationHolder.addPostOperation(postOp);
					}
				} catch (CoreException e) {
					Logger.getLogger().logError(e);
				}
			}
		}
		return extOperationHolder;
	}

	protected void addPreOperation(Object preOperation) {
		if (preOps == null) {
			preOps = new ArrayList();
		}
		preOps.add(preOperation);
	}

	protected void addPostOperation(Object postOperation) {
		if (postOps == null) {
			postOps = new ArrayList();
		}
		postOps.add(postOperation);
	}

	public boolean hasPreOps() {
		return preOps != null;
	}

	public boolean hasPostOps() {
		return postOps != null;
	}

	public ArrayList getPostOps() {
		return postOps;
	}

	public ArrayList getPreOps() {
		return preOps;
	}

}
