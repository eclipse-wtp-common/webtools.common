/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
/*
 * Created on Feb 26, 2004
 * 
 * To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code
 * Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emfworkbench.edit;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.internal.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.wst.common.emfworkbench.EMFWorkbenchEditResourceHandler;
import org.eclipse.wst.common.framework.AbstractRegistryDescriptor;

import com.ibm.wtp.emf.workbench.plugin.EMFWorkbenchPlugin;


public class AdapterFactoryDescriptor extends AbstractRegistryDescriptor implements Comparable {

	private String packageURI = null;
	private String id = null;
	private Set viewIDs = null;
	private final int loadOrder;
	private static int loadOrderCounter = 0;

	public AdapterFactoryDescriptor(IConfigurationElement element) {
		super(element);

		packageURI = element.getAttribute(AdapterFactoryRegistry.PACKAGE_URI);
		id = element.getAttribute(AdapterFactoryRegistry.ID);
		Assert.isNotNull(packageURI, EMFWorkbenchEditResourceHandler.getString("AdapterFactoryDescriptor_ERROR_0")); //$NON-NLS-1$
		Assert.isNotNull(id, EMFWorkbenchEditResourceHandler.getString("AdapterFactoryDescriptor_ERROR_1", new Object[]{element.getDeclaringExtension().getDeclaringPluginDescriptor().getUniqueIdentifier()})); //$NON-NLS-1$

		readViewIDs();
		this.loadOrder = loadOrderCounter++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("AdapterFactoryDescriptor"); //$NON-NLS-1$
		sb.append('[');
		sb.append(packageURI);
		sb.append(",p"); //$NON-NLS-1$
		sb.append(getPriority());
		if (viewIDs != null && !viewIDs.isEmpty()) {
			sb.append(':');
			boolean first = true;
			Iterator iter = viewIDs.iterator();
			while (iter.hasNext()) {
				if (!first)
					sb.append(',');
				first = false;
				sb.append(iter.next());
			}
		}
		sb.append(']');
		return sb.toString();
	}

	private void readViewIDs() {
		viewIDs = new HashSet(3);
		IConfigurationElement[] children = element.getChildren(AdapterFactoryRegistry.VIEW);
		if (children == null || children.length == 0)
			return;

		String viewID = null;
		for (int i = 0; i < children.length; i++) {
			viewID = children[i].getAttribute(AdapterFactoryRegistry.ID);
			if (viewID != null)
				viewIDs.add(viewID);
		}
	}

	public AdapterFactory createInstance() {
		if (element == null)
			return null;

		AdapterFactory factory = null;
		try {
			factory = (AdapterFactory) element.createExecutableExtension(AdapterFactoryRegistry.CLASS_NAME);
		} catch (CoreException e) {
			EMFWorkbenchPlugin.getLogger().logError(e);
			factory = null;
		}
		return factory;
	}


	public String getPackageURI() {
		return packageURI;
	}

	public Set getViewIDs() {
		return viewIDs;
	}

	public boolean appliesTo(String viewID) {
		return viewIDs.isEmpty() || viewIDs.contains(viewID);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (this == o)
			return 0;
		if (!(o instanceof AdapterFactoryDescriptor))
			return 1;
		AdapterFactoryDescriptor desc = (AdapterFactoryDescriptor) o;

		int pCompare = getPriority() - desc.getPriority();
		if (pCompare != 0)
			//We have reverse the sorting of the priority for the adapter factories
			return -pCompare;

		//The group is the same - in this case the one for a specific view has precedence over
		//a generic one
		else if (viewIDs != null && !viewIDs.isEmpty())
			return -1;
		else
			return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.framework.AbstractRegistryDescriptor#getID()
	 */
	public String getID() {
		return id;
	}


	/**
	 * @return Returns the loadOrder.
	 */
	public int getLoadOrder() {
		return loadOrder;
	}
}