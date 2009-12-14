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
package org.eclipse.wst.common.componentcore.export;

import java.util.List;

import org.eclipse.wst.common.componentcore.export.ExportModel.ExportTaskModel;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

/**
 * A simple abstract class with default values so clients
 * can extend and override only the method they care about
 */
public class AbstractExportParticipant implements IExportParticipant {

	public void initialize(IVirtualComponent component,
			ExportTaskModel dataModel, List<IExportableResource> resources) {
	}

	public boolean canOptimize(IVirtualComponent component,
			ExportTaskModel dataModel) {
		return false;
	}

	public void optimize(IVirtualComponent component,
			ExportTaskModel dataModel, List<IExportableResource> resources) {
	}

	public void finalize(IVirtualComponent component,
			ExportTaskModel dataModel, List<IExportableResource> resources) {
	}

	public boolean isChildModule(IVirtualComponent rootComponent,
			ExportTaskModel dataModel, IExportableFile file) {
		return false;
	}

	public boolean isChildModule(IVirtualComponent rootComponent,
			IVirtualReference referenced, ExportTaskModel dataModel) {
		return false;
	}

	public boolean shouldAddExportableFile(IVirtualComponent rootComponent,
			IVirtualComponent currentComponent, ExportTaskModel dataModel,
			IExportableFile file) {
		return true;
	}
	
	public boolean shouldIgnoreReference(IVirtualComponent rootComponent,
			IVirtualReference referenced, ExportTaskModel dataModel) {
		return false;
	}
}
