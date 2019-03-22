/*******************************************************************************
 * Copyright (c) 2009 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Red Hat - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.flat;

import org.eclipse.core.runtime.CoreException;

public interface IFlatVirtualComponent {
	
	/**
	 * An options key listing the export participants.
	 * The value must be a List<IExportUtilParticipant>, 
	 *  or simply an IExportUtilParticipant
	 */
	public static String PARTICIPANT_LIST = "org.eclipse.wst.common.componentcore.export.participantList";
	
	/**
	 * The ExportModel being used; the value of this should be the IFlatVirtualComponent itself. 
	 */
	public static String EXPORT_MODEL = "org.eclipse.wst.common.componentcore.export.exportModel";

	/**
	 * Fetch the list of resources, which include raw files or folders only
	 * @return
	 * @throws CoreException
	 */
	public IFlatResource[] fetchResources() throws CoreException;
	
	/**
	 * Fetch a list of child module references.
	 * @return
	 * @throws CoreException
	 */
	public IChildModuleReference[] getChildModules() throws CoreException;
}
