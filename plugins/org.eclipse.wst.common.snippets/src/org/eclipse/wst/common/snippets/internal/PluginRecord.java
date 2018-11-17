/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal;

public class PluginRecord {

	protected String fPluginName = null;
	protected String fPluginVersion = null;


	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		PluginRecord record = (PluginRecord) obj;
		return record.getPluginName().equals(getPluginName()) && record.getPluginVersion().equals(getPluginVersion());
	}

	/**
	 * Gets the pluginName.
	 * 
	 * @return Returns a String
	 */
	public String getPluginName() {
		return fPluginName;
	}

	/**
	 * Gets the pluginVersion.
	 * 
	 * @return Returns a String
	 */
	public String getPluginVersion() {
		return fPluginVersion;
	}

	/**
	 * Sets the pluginName.
	 * 
	 * @param pluginName
	 *            The pluginName to set
	 */
	public void setPluginName(String pluginName) {
		fPluginName = pluginName;
	}

	/**
	 * Sets the pluginVersion.
	 * 
	 * @param pluginVersion
	 *            The pluginVersion to set
	 */
	public void setPluginVersion(String pluginVersion) {
		fPluginVersion = pluginVersion;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return getPluginName() + ":" + getPluginVersion(); //$NON-NLS-1$
	}

}
