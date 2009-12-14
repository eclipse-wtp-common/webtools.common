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
 * This class represents a participant in the process
 * of traversing the virtual component and deciding which
 * resources should be exposed by the model, and 
 * in what fashion.
 * 
 * Clients should not implement this class directly, 
 * but should rather extend AbstractExportParticipant. 
 */
public interface IExportParticipant {
	
	/**
	 * Seed the list of resources with entries that must be present, 
	 * specifically files that may be missed by optimizations.
	 * 
	 * @param component
	 * @param dataModel
	 * @param original
	 * @return
	 */
	public void initialize(IVirtualComponent component, 
			ExportTaskModel dataModel, List<IExportableResource> resources);
	
	/**
	 * Can this participant return optimized members that
	 * preclude the need for running the full algorithm?
	 * 
	 * Caution is encouraged. If reference types you do not understand
	 * are present and you mistakenly claim you canOptimize, you may
	 * leave some references ignored and the archive incompletely assembled.
	 * 
	 * Example: if the component satisfies singleRoot requirements, this may return true
	 * 
	 * @param component
	 * @param dataModel
	 * @return
	 */
	public boolean canOptimize(IVirtualComponent component, 
			ExportTaskModel dataModel);
	
	/**
	 * Return a full and complete list of members to be published.
	 * The original entries should be included, unless left out intentionally.
	 * 
	 * Only the first participant to claim it can optimize will be allowed to do so.
	 * This will be the final list returned by ExportUtil.
	 * No finalization of the list will take place
	 * 
	 * @param component
	 * @param dataModel
	 * @param original
	 * @return
	 */
	public void optimize(IVirtualComponent component, 
			ExportTaskModel dataModel, List<IExportableResource> resources);
	
	/**
	 * Return true if this is a child module of the root component, false otherwise
	 * @param rootComponent
	 * @param dataModel
	 * @param file
	 * @return
	 */
	public boolean isChildModule(IVirtualComponent rootComponent,
			ExportTaskModel dataModel, IExportableFile file);
	
	
	/**
	 * Should the proposed file be included in the result set or not.
	 * If any one participant says no, the file will not be included.
	 * The default behaviour would be to return true unless you
	 * have a compelling reason to block the file from inclusion
	 * 
	 * Example: If the file is being actively filtered (in a .svn folder) return false
	 * Caution is encouraged here, as a consumed reference may actively 
	 * be providing the files you may be trying to filter. It's advised to only
	 * filter if you know what type currentComponent is and how it works.
	 * 
	 * @param rootComponent The root component that is being assembled
	 * @param currentComponent The component currently being processed, either rootComponent or a consumed reference
	 * @param dataModel
	 * @param file
	 * @return
	 */
	public boolean shouldAddExportableFile(IVirtualComponent rootComponent,
			IVirtualComponent currentComponent, 
			ExportTaskModel dataModel, IExportableFile file);
	
	
	/**
	 * Is this referenced component recognized as a child module?
	 * If any participant returns true, this reference will be 
	 * cached as a child module
	 * 
	 * @param rootComponent
	 * @param referenced 
	 * @param dataModel
	 * @return
	 */
	public boolean isChildModule(IVirtualComponent rootComponent, 
			IVirtualReference referenced, ExportTaskModel dataModel);
	
	/**
	 * Should this reference be ignored, ie handled elsewhere
	 * @param rootComponent
	 * @param referenced
	 * @param dataModel
	 * @return
	 */
	public boolean shouldIgnoreReference(IVirtualComponent rootComponent,
			IVirtualReference referenced, ExportTaskModel dataModel);
	/**
	 * Finalize the list of resources by adding missed resources or 
	 * removing files added by mistake. 
	 * 
	 * @param component
	 * @param dataModel
	 * @param original
	 * @param resources
	 * @return
	 */
	public void finalize(IVirtualComponent component, 
			ExportTaskModel dataModel, List<IExportableResource> resources);

}
