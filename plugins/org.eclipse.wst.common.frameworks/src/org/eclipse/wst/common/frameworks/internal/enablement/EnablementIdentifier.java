/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;


/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EnablementIdentifier implements IEnablementIdentifier {

	private final static int HASH_FACTOR = 89;

	private final static int HASH_INITIAL = EnablementIdentifier.class.getName().hashCode();

	private final static Set strongReferences = new HashSet();

	private Set functionGroupIds;

	private transient String[] functionGroupIdsAsArray;

	private boolean enabled;

	private transient int hashCode;

	private transient boolean hashCodeComputed;

	private String id;

	private List identifierListeners;

	private transient String string;

	private IProject project;

	protected EnablementIdentifier(String id) {
		this(id, null);
	}

	protected EnablementIdentifier(String id, IProject project) {
		if (id == null)
			throw new NullPointerException();
		this.id = id;
		this.project = project;
	}

	public void addIdentifierListener(IEnablementIdentifierListener identifierListener) {
		if (identifierListener == null)
			throw new NullPointerException();

		if (identifierListeners == null)
			identifierListeners = new ArrayList();

		if (!identifierListeners.contains(identifierListener))
			identifierListeners.add(identifierListener);

		strongReferences.add(this);
	}

	public int compareTo(Object object) {
		EnablementIdentifier castedObject = (EnablementIdentifier) object;
		return Util.compare(id, castedObject.id);

	}

	public boolean equals(Object object) {
		if (!(object instanceof EnablementIdentifier))
			return false;

		EnablementIdentifier castedObject = (EnablementIdentifier) object;
		boolean equals = Util.equals(id, castedObject.id);
		return equals;
	}

	protected void fireIdentifierChanged(EnablementIdentifierEvent functionIdentifierEvent) {
		if (functionIdentifierEvent == null)
			throw new NullPointerException();

		if (identifierListeners != null) {
			synchronized (identifierListeners) {
				for (int i = 0; i < identifierListeners.size(); i++)
					((IEnablementIdentifierListener) identifierListeners.get(i)).identifierChanged(functionIdentifierEvent);
			}
		}
	}

	public Set getFunctionGroupIds() {
		return functionGroupIds;
	}

	public String getId() {
		return id;
	}

	public int hashCode() {
		if (!hashCodeComputed) {
			hashCode = HASH_INITIAL;
			hashCode = hashCode * HASH_FACTOR + Util.hashCode(id);
			hashCodeComputed = true;
		}

		return hashCode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void removeIdentifierListener(IEnablementIdentifierListener identifierListener) {
		if (identifierListener == null)
			throw new NullPointerException();

		if (identifierListeners != null)
			identifierListeners.remove(identifierListener);

		if (identifierListeners.isEmpty())
			strongReferences.remove(this);
	}

	protected boolean setFunctionGroupIds(Set functionGroupIds) {
		functionGroupIds = Util.safeCopy(functionGroupIds, String.class);

		if (!Util.equals(functionGroupIds, this.functionGroupIds)) {
			this.functionGroupIds = functionGroupIds;
			this.functionGroupIdsAsArray = (String[]) this.functionGroupIds.toArray(new String[this.functionGroupIds.size()]);

			hashCodeComputed = false;
			hashCode = 0;
			string = null;
			return true;
		}

		return false;
	}

	protected boolean setEnabled(boolean enabled) {
		if (enabled != this.enabled) {
			this.enabled = enabled;
			hashCodeComputed = false;
			hashCode = 0;
			string = null;
			return true;
		}

		return false;
	}

	/**
	 * Recompute the enabled state and return whether the state changed
	 */
	protected boolean resetEnabled() {
		return setEnabled(getNewEnabled());
	}

	protected boolean getNewEnabled() {
		if (project == null)
			return true;
		if (functionGroupIdsAsArray.length == 0)
			return true;
		for (int i = 0; i < functionGroupIdsAsArray.length; i++) {
			FunctionGroup group = FunctionGroupRegistry.getInstance().getGroupByID(functionGroupIdsAsArray[i]);
			if (group != null && group.isEnabled(project))
				return true;
		}
		return false;
	}

	public String toString() {
		if (string == null) {
			final StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append('[');
			stringBuffer.append(functionGroupIds);
			stringBuffer.append(',');
			stringBuffer.append(enabled);
			stringBuffer.append(',');
			stringBuffer.append(id);
			stringBuffer.append(']');
			string = stringBuffer.toString();
		}

		return string;
	}

	/**
	 * The associated Project may be null
	 * 
	 * @return Returns the project.
	 */
	public IProject getProject() {
		return project;
	}

	public String getPrimaryFunctionGroupId() {
		int selectedPriority = Integer.MAX_VALUE;
		int priority = 0;
		String selectedFunctionGroupId = null;
		String functionGroupId = null;
		for (Iterator iterator = getFunctionGroupIds().iterator(); iterator.hasNext();) {
			functionGroupId = iterator.next().toString();
			priority = FunctionGroupRegistry.getInstance().getGroupPriority(functionGroupId);
			if (priority < selectedPriority)
				selectedFunctionGroupId = functionGroupId;
			if (priority == 0)
				return selectedFunctionGroupId;
		}
		return selectedFunctionGroupId;
	}

}
