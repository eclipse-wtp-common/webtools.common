/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on May 4, 2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.common.frameworks.internal.operations.OperationExtensionRegistry;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;

import org.eclipse.jem.util.RegistryReader;

/**
 * @author mdelder
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Generation - Code and Comments
 */
public class UIOperationExtensionRegistry extends RegistryReader {

	public static final UIOperationExtensionRegistry INSTANCE = new UIOperationExtensionRegistry();

	public static final String EXTENSION_POINT = "wtpuiAction"; //$NON-NLS-1$

	private Map slaveDescriptorMap;

	private Map masterDescriptorMap;

	private HashMap overriddingDescriptorMap;

	static {
		INSTANCE.readRegistry();
	}

	protected UIOperationExtensionRegistry() {
		super(WTPUIPlugin.PLUGIN_ID, EXTENSION_POINT);
	}

	public MasterDescriptor[] getExtendedUIOperations(String extendedOperationId, IStructuredSelection selection) {
		if (selection == null || selection.isEmpty())
			return new MasterDescriptor[0];
		List enabledExtendedOperations = new ArrayList();
		List descs = getMasterDescriptorsById(extendedOperationId);
		MasterDescriptor descriptor = null;
		for (Iterator itr = descs.iterator(); itr.hasNext();) {
			descriptor = (MasterDescriptor) itr.next();
			if (descriptor.isEnabledFor(selection) && !isOverridden(descriptor, selection))
				enabledExtendedOperations.add(descriptor);
		}
		if (enabledExtendedOperations.size() == 0)
			return new MasterDescriptor[0];
		MasterDescriptor[] enabledExtendedOperationsArray = new MasterDescriptor[enabledExtendedOperations.size()];
		enabledExtendedOperations.toArray(enabledExtendedOperationsArray);
		return enabledExtendedOperationsArray;
	}

	/**
	 * @param descriptor
	 * @return
	 */
	protected boolean isOverridden(MasterDescriptor descriptor, IStructuredSelection selection) {
		MasterDescriptor overriddingDescriptor = getOverriddingDescriptor(descriptor);
		if (overriddingDescriptor == null)
			return false;
		return overriddingDescriptor.isEnabledFor(selection);
	}

	/**
	 * @param descriptor
	 * @return
	 */
	protected MasterDescriptor getOverriddingDescriptor(MasterDescriptor descriptor) {
		return (MasterDescriptor) getOverriddingDescriptorMap().get(descriptor.getId());
	}

	/**
	 * @return
	 */
	protected Map getOverriddingDescriptorMap() {
		if (overriddingDescriptorMap == null)
			overriddingDescriptorMap = new HashMap();
		return overriddingDescriptorMap;
	}

	public SlaveDescriptor[] getSlaveDescriptors(String parentOperationClass) {
		String[] slaves = OperationExtensionRegistry.getRegisteredOperations(parentOperationClass);
		List slaveDescriptors = new ArrayList();
		SlaveDescriptor slaveDescriptor = null;
		for (int i = 0; null != slaves && i < slaves.length; i++) {
			slaveDescriptor = getSlaveDescriptor(slaves[i]);
			if (slaveDescriptor != null)
				slaveDescriptors.add(slaveDescriptor);
		}
		SlaveDescriptor[] slaveDescriptorsArray = new SlaveDescriptor[slaveDescriptors.size()];
		slaveDescriptors.toArray(slaveDescriptorsArray);
		return slaveDescriptorsArray;
	}

	/**
	 * @param string
	 * @return
	 */
	public SlaveDescriptor getSlaveDescriptor(String operationClass) {
		return (SlaveDescriptor) getSlaveDescriptorMap().get(operationClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
	 */
	public boolean readElement(IConfigurationElement element) {
		if (MasterDescriptor.MASTER_OPERATION.equals(element.getName())) {
			MasterDescriptor md = new MasterDescriptor(element);
			addDescriptor(md);
			if (md.getOverrideId() != null && md.getOverrideId().length() > 0)
				getOverriddingDescriptorMap().put(md.getOverrideId(), md);
		} else if (SlaveDescriptor.SLAVE_OPERATION.equals(element.getName())) {
			addDescriptor(new SlaveDescriptor(element));
		} else {
			return false;
		}
		return true;
	}

	/**
	 * @param descriptor
	 */
	protected void addDescriptor(MasterDescriptor descriptor) {
		getMasterDescriptorsById(descriptor.getExtendedOperationId()).add(descriptor);
	}

	/**
	 * @param descriptor
	 */
	protected void addDescriptor(SlaveDescriptor descriptor) {
		getSlaveDescriptorMap().put(descriptor.getOperationClass(), descriptor);
	}

	/**
	 * @param string
	 * @return
	 */
	protected List getMasterDescriptorsById(String extendedOperationId) {
		List descs = (List) getMasterDescriptorMap().get(extendedOperationId);
		if (descs == null) {
			getMasterDescriptorMap().put(extendedOperationId, (descs = new ArrayList()));
		}
		return descs;
	}

	/**
	 * @return
	 */
	protected Map getMasterDescriptorMap() {
		if (masterDescriptorMap == null)
			masterDescriptorMap = new HashMap();
		return masterDescriptorMap;
	}

	/**
	 * @return Returns the slaveDescriptorMap.
	 */
	protected Map getSlaveDescriptorMap() {
		if (slaveDescriptorMap == null)
			slaveDescriptorMap = new HashMap();
		return slaveDescriptorMap;
	}
}