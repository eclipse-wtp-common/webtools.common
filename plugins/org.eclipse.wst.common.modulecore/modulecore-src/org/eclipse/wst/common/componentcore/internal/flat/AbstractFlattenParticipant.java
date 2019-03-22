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

import java.util.List;

import org.eclipse.wst.common.componentcore.internal.flat.FlatVirtualComponent.FlatComponentTaskModel;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

/**
 * A simple abstract class with default values so clients
 * can extend and override only the method they care about
 */
public class AbstractFlattenParticipant implements IFlattenParticipant {

	public void initialize(IVirtualComponent component,
			FlatComponentTaskModel dataModel, List<IFlatResource> resources) {
	}

	public boolean canOptimize(IVirtualComponent component,
			FlatComponentTaskModel dataModel) {
		return false;
	}

	public void optimize(IVirtualComponent component, FlatComponentTaskModel dataModel, 
			List<IFlatResource> resources, List<IChildModuleReference> childModules) {
	}

	public void finalize(IVirtualComponent component,
			FlatComponentTaskModel dataModel, List<IFlatResource> resources) {
	}

	public boolean isChildModule(IVirtualComponent rootComponent,
			FlatComponentTaskModel dataModel, IFlatFile file) {
		return false;
	}

	public boolean isChildModule(IVirtualComponent rootComponent,
			IVirtualReference referenced, FlatComponentTaskModel dataModel) {
		return false;
	}

	public boolean shouldAddExportableFile(IVirtualComponent rootComponent,
			IVirtualComponent currentComponent, FlatComponentTaskModel dataModel,
			IFlatFile file) {
		return true;
	}
	
	public boolean shouldIgnoreReference(IVirtualComponent rootComponent,
			IVirtualReference referenced, FlatComponentTaskModel dataModel) {
		return false;
	}

	public List<IVirtualReference> getChildModules(IVirtualComponent rootComponent, FlatComponentTaskModel dataModel) {
		return null;
	}

}
