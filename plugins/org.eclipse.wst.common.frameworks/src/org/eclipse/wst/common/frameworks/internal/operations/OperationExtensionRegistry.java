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
import org.eclipse.wst.common.frameworks.operations.WTPOperation;
import org.eclispe.wst.common.frameworks.internal.enablement.EnablementManager;

import org.eclipse.jem.util.logger.proxy.Logger;

public class OperationExtensionRegistry {

	protected static boolean canExtHasRead = false;

	protected static boolean extPointHasRead = false;

	protected static OperationExtensionRegistry instance = null;

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

	public static ComposedExtendedOperationHolder getExtensions(String className) {
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

	public static ComposedExtendedOperationHolder getExtensions(WTPOperation op) {
		return getExtensions(op.getClass().getName());
	}

	protected String getExtendableOperationId(WTPOperation op) {
		return (String) extensibleOperations.get(op.getClass().getName());
	}

	private static ComposedExtendedOperationHolder calculateOperationHolder(Collection ext) throws CoreException {
		Object[] opExt = ext.toArray();
		ComposedExtendedOperationHolder extOperationHolder = new ComposedExtendedOperationHolder();
		WTPOperation preOp = null;
		WTPOperation postOp = null;
		OperationExtension currentExt = null;
		for (int i = 0; i < opExt.length; i++) {
			currentExt = (OperationExtension) opExt[i];
			/* Only allow extensions which enabled (null project indicates enabled by activity only) */
			if (EnablementManager.INSTANCE.getIdentifier(currentExt.getExtensionId(), null).isEnabled()) {
				preOp = currentExt.getPreOperation();
				if (preOp != null) {
					extOperationHolder.addPreOperation(preOp);
				}
				postOp = currentExt.getPostOperation();
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
	public static OperationExtensionRegistry getInstance() {
		if (instance == null)
			instance = new OperationExtensionRegistry();
		return instance;
	}

}