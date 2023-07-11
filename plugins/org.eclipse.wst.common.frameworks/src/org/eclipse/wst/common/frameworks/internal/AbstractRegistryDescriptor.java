/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Feb 12, 2004
 *
 */
package org.eclipse.wst.common.frameworks.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.frameworks.internal.enablement.FunctionGroupRegistry;
import org.eclipse.wst.common.frameworks.internal.enablement.IEnablementIdentifier;
import org.eclipse.wst.common.frameworks.internal.enablement.IEnablementManager;
import org.eclipse.wst.common.frameworks.internal.enablement.Identifiable;


/**
 * @author schacher
 * 
 * Common superclass for enablement-aware (e.g., using function groups or activiities) extension
 * points.
 */
public abstract class AbstractRegistryDescriptor extends ConfigurationElementWrapper implements Identifiable {

	protected Integer priority;

	public AbstractRegistryDescriptor(IConfigurationElement anElement) {
		super(anElement);
	}

	public IEnablementIdentifier getEnablementIdentifier(IProject project) {
		return IEnablementManager.INSTANCE.getIdentifier(getID(), project);
	}

	@Override
	public abstract String getID();

	public int getPriority() {
		if (priority == null)
			priority = new Integer(FunctionGroupRegistry.getInstance().getFunctionPriority(getID()));
		return priority.intValue();
	}

	public boolean isEnabled(IProject p) {
		IEnablementIdentifier id = getEnablementIdentifier(p);
		return id.isEnabled();
	}
}
