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
 * Created on Feb 3, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.eclispe.wst.common.frameworks.internal.enablement;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclispe.wst.common.frameworks.internal.plugin.WTPCommonMessages;
import org.eclispe.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

import com.ibm.wtp.common.logger.proxy.Logger;

/**
 * @author mdelder
 */
public class EnablementManager implements IEnablementManager {

	public static final IEnablementManager INSTANCE = new EnablementManager();

	private static Map identifiersByProject = new WeakHashMap();

	public EnablementManager() {
	}

	/**
	 * Returns a Map in which the keys are ids and the values are the identifiers
	 * 
	 * @param project
	 * @return
	 */
	protected Map getIdentifiersById(IProject project) {
		Map aMap = (Map) identifiersByProject.get(project);
		if (aMap == null) {
			aMap = new WeakHashMap();
			identifiersByProject.put(project, aMap);
		}
		return aMap;
	}

    public IEnablementIdentifier getIdentifier(String identifierId, IProject project) {
        if (identifierId == null) throw new NullPointerException();
        if (project != null && !project.isAccessible()) project = null;
        
        EnablementIdentifier identifier = null;
        synchronized(this) {
	        Map identifiersById = getIdentifiersById(project);	
	        
	        identifier = (EnablementIdentifier) identifiersById.get(identifierId);
	
	        if (identifier == null) {
	            identifier = createIdentifier(identifierId, project);
	            updateIdentifier(identifier);
	            identifiersById.put(identifierId, identifier);
	        }
        }

        return identifier;
    }

	protected EnablementIdentifier createIdentifier(String identifierId, IProject project) {
		return new EnablementIdentifier(identifierId, project);
	}

	private EnablementIdentifierEvent updateIdentifier(EnablementIdentifier identifier) {
		String id = identifier.getId();
		Set functionGroupIds = new HashSet();

		List groups = FunctionGroupRegistry.getInstance().getKnownGroups();
		for (Iterator iterator = groups.iterator(); iterator.hasNext();) {
			FunctionGroup group = (FunctionGroup) iterator.next();

			if (group == null)
				throw new IllegalStateException();
			if (group.isMatch(id))
				functionGroupIds.add(group.getGroupID());
		}

		boolean functionGroupsChanged = identifier.setFunctionGroupIds(functionGroupIds);
		boolean enabledChanged = identifier.resetEnabled();

		if (functionGroupsChanged || enabledChanged)
			return new EnablementIdentifierEvent(identifier, functionGroupsChanged, enabledChanged);
		return null;
	}

	/**
	 * Notify all identifier listeners that the state of a project has changed that affects the
	 * enablement of a group for a project. This method is fail safe, in that if one listener throws
	 * an exception while being notified, it will not stop the notification of other listeners.
	 * 
	 * @param evt
	 * @throws CoreException
	 *             if exceptions were caught notifying any of the listeners. Check the status of the
	 *             core exception for the nested exceptions.
	 */
	public final void notifyFunctionGroupChanged(String groupID, IProject project) throws CoreException {

		Map identifiers = getIdentifiersById(project);

		if (identifiers == null)
			return;

		EnablementIdentifier identifier = null;
		Iterator iter = identifiers.values().iterator();
		IStatus errorStatus = null;
		IStatus nextStatus = null;

		while (iter.hasNext()) {
			identifier = (EnablementIdentifier) iter.next();

			EnablementIdentifierEvent evt = updateIdentifier(identifier);
			try {
				if (evt != null)
					identifier.fireIdentifierChanged(evt);
			} catch (Exception ex) {
				//Defer the exception so others can handle it.
				nextStatus = WTPCommonPlugin.createErrorStatus(WTPCommonMessages.INTERNAL_ERROR, ex);
				Logger.getLogger().logError(ex);
				if (errorStatus == null)
					errorStatus = nextStatus;
				else if (errorStatus.isMultiStatus())
					((MultiStatus) errorStatus).add(nextStatus);
				else {
					IStatus[] children = {errorStatus, nextStatus};
					errorStatus = new MultiStatus(errorStatus.getPlugin(), errorStatus.getCode(), children, WTPCommonMessages.INTERNAL_ERROR, null);
				}
			}

		}
		if (errorStatus != null)
			throw new CoreException(errorStatus);

	}

	/**
	 * Utility method for clients to dispose of listeners
	 * 
	 * @param enablementIdentifiers
	 * @param listener
	 */
	public void removeEnablementIdentifierListener(Collection enablementIdentifiers, IEnablementIdentifierListener listener) {
		Iterator iter = enablementIdentifiers.iterator();
		while (iter.hasNext()) {
			IEnablementIdentifier identifier = (IEnablementIdentifier) iter.next();
			identifier.removeIdentifierListener(listener);
		}
	}

}