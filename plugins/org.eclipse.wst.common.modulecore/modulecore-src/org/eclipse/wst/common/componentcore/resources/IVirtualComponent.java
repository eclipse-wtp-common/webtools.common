/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.componentcore.resources;

import java.util.Properties;

import org.eclipse.core.runtime.IPath;

/**
 * Represents a component as defined by the .wtpmodules file.
 * <p>
 * A component is a container of virtual resources which has other features that describe the
 * component including:
 * <ul>
 * <li>{@link #getComponentTypeId()}
 * </p>
 * @since 1.0
 */
public interface IVirtualComponent extends IVirtualContainer {

	/**
	 * The name of the component must be unique within its enclosing project.
	 * 
	 * @return The name of the component.
	 */
	String getName();

	/**
	 * The componentTypeId is used to understand how this component should be edited and deployed.
	 * Examples include "jst.web" or "jst.utility". The componentTypeId can be set to any value when
	 * created so long as that value makes sense to the clients. Standard componentTypeIds may be
	 * available for common component types.
	 * 
	 * @return The componentTypeId, a string based identifier that indicates the component
	 */
	String getComponentTypeId();
	
	/**
	 * The componentHandle is a handy way to pass the identity of this component instance
	 * 
	 * @return The componentHandle, of ComponentHandle type
	 */
	ComponentHandle getComponentHandle();

	/**
	 * 
	 * The componentTypeId is used to understand how this component should be edited and deployed.
	 * Examples include "jst.web" or "jst.utility". The componentTypeId can be set to any value when
	 * created so long as that value makes sense to the clients. Standard componentTypeIds may be
	 * available for common component types.
	 * 
	 * @param aComponentTypeId
	 *            A value which is either standard for a common component type or client-defined for
	 *            a custom component type
	 */
	void setComponentTypeId(String aComponentTypeId);

	/**
	 * MetaProperties are String-based name-value pairs that include information about this
	 * component that may be relevant to clients or the way that clients edit or deploy components.
	 * 
	 * @return A by-reference instance of the properties for this component.
	 */
	Properties getMetaProperties();

	/**
	 * MetaResources provide a loose mechanism for components that would like to list off the
	 * metadata-resources available in the component which can aid or expedite searching for this
	 * resources.
	 * <p>
	 * Clients are not required to get or set the MetaResources for a component.
	 * </p>
	 * 
	 * @return A by-value copy of the MetaResources array
	 * @see #setMetaResources(IPath[])
	 */
	IPath[] getMetaResources();

	/**
	 * 
	 * MetaResources provide a loose mechanism for components that would like to list off the
	 * metadata-resources available in the component which can aid or expedite searching for this
	 * resources.
	 * <p>
	 * Clients are not required to get or set the MetaResources for a component. The existing
	 * MetaResources will be overwritten after the call to this method.
	 * </p>
	 * 
	 * @param theMetaResourcePaths
	 *            An array of paths that will become the new MetaResource array.
	 */
	void setMetaResources(IPath[] theMetaResourcePaths);

	/**
	 * Returns all virtual resources of a given type. 
	 * <p>
	 * Virtual Resources can have a
	 * {@link org.eclipse.wst.common.componentcore.internal.ComponentResource#getResourceType() type}&nbsp;
	 * associated with them. This method will find those resources
	 * which are tagged with a given resource type The returned array
	 * could be a mix of {@link IVirtualFile}s or {@link IVirtualFolder}s 
	 * </p>
	 * <p><b>null</b> may be supplied to this method, which will return all resources without a resourceType.</p> 
	 * 
	 * @param aResourceType A client-defined resource type that was used to create 0 or more resources within this component
	 * @return An array of those resources which matched the resourceType
	 */
	IVirtualResource[] getResources(String aResourceType);

	/**
	 * Virtual components may reference other virtual components to build logical dependency trees. 
	 * <p>
	 * Each virtual reference will indicate how the content of the reference will be absorbed 
	 * by this component. Each virtual reference will always specify an enclosing component that will
	 * be this component.   
	 * </p>
	 * @return A by-value copy of the virtual reference array
	 */
	IVirtualReference[] getReferences();
	/**
	 * Virtual components may reference other virtual components to build logical dependency trees. 
	 * <p>
	 * Each virtual reference will indicate how the content of the reference will be absorbed 
	 * by this component. Each virtual reference will always specify an enclosing component that will
	 * be this component.   
	 * </p>
	 * @return A by-value copy of the virtual reference with given name, or null if none exist matching this name
	 */
	IVirtualReference getReference(String aComponentName);
	
	/**
	 * Virtual components may reference other virtual components to build logical dependency trees. 
	 * <p>
	 * Each virtual reference will indicate how the content of the reference will be absorbed 
	 * by this component. Each virtual reference will always specify an enclosing component that will
	 * be this component. Any references specified in the array which do not specify an enclosing
	 * component that matches this component will be modified to specify this virtual component. 
	 * </p>
	 * <p>
	 * Existing virtual references will be overwritten when this method is called.
	 * </p>
	 * @param theReferences A by-value copy of the virtual reference array
	 */
	void setReferences(IVirtualReference[] theReferences);


}
