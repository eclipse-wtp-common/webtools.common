/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.enablement;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.core.util.UIContextDetermination;

public interface IEnablementManager {
	String INTERNAL_ENABLEMENT_DETERMINATION_ID = "org.eclipse.wst.common.frameworks.internal.EnablementDetermination"; //$NON-NLS-1$

	IEnablementManager INSTANCE = (IEnablementManager) UIContextDetermination.createInstance(INTERNAL_ENABLEMENT_DETERMINATION_ID);

	IEnablementIdentifier getIdentifier(String identifierId, IProject project);

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
	void notifyFunctionGroupChanged(String groupID, IProject project) throws CoreException;

	/**
	 * Utility method for clients to dispose of listeners
	 * 
	 * @param enablementIdentifiers
	 * @param listener
	 */
	void removeEnablementIdentifierListener(Collection enablementIdentifiers, IEnablementIdentifierListener listener);


}
