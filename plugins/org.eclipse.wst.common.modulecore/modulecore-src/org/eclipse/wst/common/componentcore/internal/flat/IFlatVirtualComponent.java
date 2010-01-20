/*******************************************************************************
 * Copyright (c) 2009 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
	 * The ExportModel (this) being used
	 */
	public static String EXPORT_MODEL = "org.eclipse.wst.common.componentcore.export.exportModel";

	public IFlatResource[] fetchResources() throws CoreException;
	public IChildModuleReference[] getChildModules() throws CoreException;
}
