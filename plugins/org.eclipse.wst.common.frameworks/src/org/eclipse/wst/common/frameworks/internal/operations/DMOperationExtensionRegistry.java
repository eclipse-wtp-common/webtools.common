/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
/*
 * Created on Oct 1, 2003
 * 
 * To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code
 * Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.enablement.EnablementManager;

public class DMOperationExtensionRegistry {

	protected static boolean canExtHasRead = false;

	protected static boolean extPointHasRead = false;

	protected static DMOperationExtensionRegistry instance = null;

	protected static HashMap extensibleOperations = null;

	protected static HashMap opExtensions = null;

	protected static OperationExtensibilityReader opExtensibilityReader = null;

	protected static OperationExtensionReader opExtensionReader = null;

	public static String[] getRegisteredOperations(String className) {
		extensibleOperations = getExtensibility();
		if (extensibleOperations != null) {
			String id = (String) extensibleOperations.get(className);
			if (id == null)
				return null;
			if (opExtensions == null)
				opExtensions = getExtensionPoints();
			if (opExtensions != null) {
				Collection ext = (Collection) opExtensions.get(id);
				if (ext != null)
					return getClassNames(ext.toArray());
			}
		}
		return null;
	}

	private static String[] getClassNames(Object[] opExt) {
		ArrayList classNames = new ArrayList();
		OperationExtension currentExt = null;
		for (int i = 0; i < opExt.length; i++) {
			currentExt = (OperationExtension) opExt[i];
			/* Only allow extensions which enabled (null project indicates enabled by activity only) */
			if (EnablementManager.INSTANCE.getIdentifier(currentExt.getExtensionId(), null).isEnabled()) {
				String className = currentExt.getPreOperationClass();
				if (null != className) {
					classNames.add(className);
				}
				className = currentExt.getPostOperationClass();
				if (null != className) {
					classNames.add(className);
				}
			}
		}
		String[] array = new String[classNames.size()];
		classNames.toArray(array);
		return array;
	}

	public static DMComposedExtendedOperationHolder getExtensions(String className) {
		extensibleOperations = getExtensibility();
		if (extensibleOperations != null) {
			String id = (String) extensibleOperations.get(className);
			if (id == null)
				return null;
			if (opExtensions == null)
				opExtensions = getExtensionPoints();
			if (opExtensions != null) {
				Collection ext = (Collection) opExtensions.get(id);
				if (ext != null) {
					try {
						return calculateOperationHolder(ext);
					} catch (CoreException ex) {
						Logger.getLogger().logError(ex);
					}
				}
			}
		}
		return null;
	}

	public static DMComposedExtendedOperationHolder getExtensions(IDataModelOperation op) {
		return getExtensions(op.getClass().getName());
	}

	protected String getExtendableOperationId(IDataModelOperation op) {
		return (String) extensibleOperations.get(op.getClass().getName());
	}

	private static DMComposedExtendedOperationHolder calculateOperationHolder(Collection ext) throws CoreException {
		Object[] opExt = ext.toArray();
		DMComposedExtendedOperationHolder extOperationHolder = new DMComposedExtendedOperationHolder();
		IDataModelOperation preOp = null;
		IDataModelOperation postOp = null;
		OperationExtension currentExt = null;
		for (int i = 0; i < opExt.length; i++) {
			currentExt = (OperationExtension) opExt[i];
			/* Only allow extensions which enabled (null project indicates enabled by activity only) */
			if (EnablementManager.INSTANCE.getIdentifier(currentExt.getExtensionId(), null).isEnabled()) {
				preOp = currentExt.getDMPreOperation();
				if (preOp != null) {
					extOperationHolder.addPreOperation(preOp);
				}
				postOp = currentExt.getDMPostOperation();
				if (postOp != null) {
					extOperationHolder.addPostOperation(postOp);
				}
			}
		}
		return extOperationHolder;
	}

	private static HashMap getExtensibility() {
		if (!canExtHasRead) {
			opExtensibilityReader = new OperationExtensibilityReader();
			opExtensibilityReader.readRegistry();
			canExtHasRead = true;
		}
		if (opExtensibilityReader == null)
			return null;
		return OperationExtensibilityReader.getExtendableOperations();
	}

	private static HashMap getExtensionPoints() {
		if (!extPointHasRead) {
			opExtensionReader = new OperationExtensionReader();
			opExtensionReader.readRegistry();
			extPointHasRead = true;
		}
		if (opExtensionReader == null)
			return null;
		return OperationExtensionReader.getExtensionPoints();
	}

	/**
	 * Gets the instance.
	 * 
	 * @return Returns a EjbPageExtensionRegistry
	 */
	public static DMOperationExtensionRegistry getInstance() {
		if (instance == null)
			instance = new DMOperationExtensionRegistry();
		return instance;
	}

}