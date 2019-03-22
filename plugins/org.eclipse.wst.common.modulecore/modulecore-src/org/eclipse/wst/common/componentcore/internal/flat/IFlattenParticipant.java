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
 * This class represents a participant in the process
 * of traversing the virtual component and deciding which
 * resources should be exposed by the model, and 
 * in what fashion.
 * 
 * Clients should not implement this class directly, 
 * but should rather extend AbstractExportParticipant. 
 */
public interface IFlattenParticipant {
	
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
			FlatComponentTaskModel dataModel, List<IFlatResource> resources);
	
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
			FlatComponentTaskModel dataModel);
	
	/**
	 * Return a full and complete list of members to be published.
	 * The original entries should be included, unless left out intentionally.
	 * 
	 * Only the first participant to claim it can optimize will be allowed to do so.
	 * This will be the final list returned by ExportUtil.
	 * No finalization or other actions on this list will take place
	 * 
	 * @param component
	 * @param dataModel
	 * @param original
	 * @return
	 */
	public void optimize(IVirtualComponent component, FlatComponentTaskModel dataModel, 
			List<IFlatResource> resources, List<IChildModuleReference> children);
	
	/**
	 * Returns true if this participant considers this file to be a child module
	 * The framework will consider the file a child module if at least one participant
	 * returns true to this method. 
	 * 
	 * The item in question is a flat file which has been found inside the project
	 * or inside a consumed reference of the project. The framework is asking
	 * all participants if this file is a child module, to be exposed as such later, 
	 * or if it is just a generic resource, which should be exposed as a regular member file. 
	 * 
	 * @param rootComponent
	 * @param dataModel
	 * @param file
	 * @return
	 */
	public boolean isChildModule(IVirtualComponent rootComponent,
			FlatComponentTaskModel dataModel, IFlatFile file);
	
	
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
			FlatComponentTaskModel dataModel, IFlatFile file);
	
	
	/**
	 * Returns true if this participant considers the reference a child module.
	 * The framework will consider it a child module if at least one participant returns true. 
	 * 
	 * The framework is asking whether the referenced component is a child module, 
	 * which should be added to the list of children modules and exposed as an  
	 * IChildModuleReference, or if it is just some generic type of entity to be 
	 * exposed as member resources inside the current flat component. 
	 * 
	 * The parameter "referenced" is guaranteed to be a "USED" reference,
	 * as "CONSUMED" references have already been consumed directly into the project
	 * as if they were part of it originally.  
	 * 
	 * A "USED" reference which is a child module will be exposed as an 
	 * IChildModuleReference and use the archiveName retrieved from the IVirtualReference. 
	 * 
	 * A "USED" reference which is *not* a child module will be represented
	 * as a folder member resource inside the parent, and the folder's name will
	 * also be retrieved from the archiveName attribute of the IVirtualReference.
	 * 
	 * @param rootComponent
	 * @param referenced 
	 * @param dataModel
	 * @return
	 */
	public boolean isChildModule(IVirtualComponent rootComponent, 
			IVirtualReference referenced, FlatComponentTaskModel dataModel);
	
	/**
	 * Should this reference be ignored, ie handled elsewhere
	 * @param rootComponent
	 * @param referenced
	 * @param dataModel
	 * @return
	 */
	public boolean shouldIgnoreReference(IVirtualComponent rootComponent,
			IVirtualReference referenced, FlatComponentTaskModel dataModel);
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
			FlatComponentTaskModel dataModel, List<IFlatResource> resources);
	
	
	/**
	 * Return a list of references that this participant believes should be treated as child modules.
	 * @param rootComponent
	 * @param dataModel
	 * @return
	 */
	public List<IVirtualReference> getChildModules(IVirtualComponent rootComponent, FlatComponentTaskModel dataModel);

}
