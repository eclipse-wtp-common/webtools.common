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
 * Created on Feb 10, 2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.enablement;



/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EnablementIdentifierEvent {

	private boolean functionGroupIdsChanged;
	private boolean enabledChanged;
	private IEnablementIdentifier identifier;

	public EnablementIdentifierEvent(IEnablementIdentifier identifier, boolean functionGroupIdsChanged, boolean enabledChanged) {
		if (identifier == null)
			throw new NullPointerException();

		this.identifier = identifier;
		this.functionGroupIdsChanged = functionGroupIdsChanged;
		this.enabledChanged = enabledChanged;
	}

	/**
	 * Returns the instance of the interface that changed.
	 * 
	 * @return the instance of the interface that changed. Guaranteed not to be <code>null</code>.
	 */
	public IEnablementIdentifier getIdentifier() {
		return identifier;
	}

	/**
	 * Returns whether or not the functionGroupIds property changed.
	 * 
	 * @return true, iff the functionGroupIds property changed.
	 */
	public boolean hasFunctionGroupIdsChanged() {
		return functionGroupIdsChanged;
	}

	/**
	 * Returns whether or not the enabled property changed.
	 * 
	 * @return true, iff the enabled property changed.
	 */
	public boolean hasEnabledChanged() {
		return enabledChanged;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EnablementIdentifierEvent [identifier=" + identifier.toString() + ", functionGroupIdsChanged=" + hasFunctionGroupIdsChanged() + ", enabledChanged=" + hasEnabledChanged() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
}
