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
 * Created on Apr 27, 2004
 */
package org.eclipse.wst.common.emf.utilities;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;



/**
 * @author John Mourra
 */
public class CommandContext implements ICommandContext {

	private IProgressMonitor monitor;
	private Map properties;
	private ResourceSet resourceSet;

	/**
	 * @param monitor
	 */
	public CommandContext(IProgressMonitor monitor) {
		this(monitor, null, null);
	}

	/**
	 * @param monitor
	 * @param configurationProperties
	 * @param resourceSet
	 */
	public CommandContext(IProgressMonitor monitor, Map configurationProperties, ResourceSet resourceSet) {
		this.monitor = monitor;
		this.properties = configurationProperties;
		this.resourceSet = resourceSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.ws.rd.command.framework.ICommandContext#getProgressMonitor()
	 */
	public IProgressMonitor getProgressMonitor() {
		if (monitor == null)
			monitor = new NullProgressMonitor();
		return monitor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.ws.rd.command.framework.ICommandContext#getConfigurationProperties()
	 */
	public Map getConfigurationProperties() {
		if (properties == null)
			properties = new HashMap();
		return properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.ws.rd.command.framework.ICommandContext#getResourceSet()
	 */
	public ResourceSet getResourceSet() {
		if (resourceSet == null)
			resourceSet = new ResourceSetImpl();
		return resourceSet;
	}
}