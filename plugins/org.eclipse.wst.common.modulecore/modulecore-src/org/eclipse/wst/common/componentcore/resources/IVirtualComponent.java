/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.componentcore.resources;

import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.internal.Property;

/**
 * Represents a component as defined by the .wtpmodules file.
 * <p>
 * A component is a container of virtual resources which has other features that describe the
 * component including:
 * <ul>
 * <li>{@link #getComponentTypeId()}
 * </p>
 * @plannedfor 1.0
 */
public interface IVirtualComponent extends IAdaptable {
	
	IPath ROOT = new Path("/");
	
	/**
	 * Type constant (bit mask value 1) which identifies component binary status.
	 */
	public static final int BINARY = 0x1;
	
	
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
	 * Returns reference to itself.
	 * <p>
	 *  
	 * @return the name of the component that contains the virtual resource
	 */
	public IVirtualComponent getComponent();
	

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
	 * Adds a single property
	 * @param property
	 * 		A value whiich is name, value pair, see ComponentcoreFactory.eINSTANCE.createProperty()
	 * 		
	 */
	void addMetaProperty(Property property);
	
	/**
	 * Adds the properties provided as a list
	 * @param properties
	 * 			A list of properties
	 */
	void setMetaProperties(List properties);

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
	
	/**
	 * The version associated with the component.
	 * 
	 * @return The version of the component.
	 */	
	String getVersion();	
	
	/**
	 * Returns true if this component is of binary type
	 * 
	 * @return The binary status.
	 */
	boolean isBinary();		
	
	
	/**
	 * Create the underlying model elements if they do not already exist. Resources
	 * may be created as a result of this method if the mapped path does not exist. 
	 * 
	 * @param updateFlags Any of IVirtualResource or IResource update flags. If a 
	 * 			resource must be created, the updateFlags will be supplied to the 
	 * 			resource creation operation.
	 * @param aMonitor
	 * @throws CoreException
	 */
	public void create(int updateFlags, IProgressMonitor aMonitor) throws CoreException; 
	
	/**
	 * Returns a handle to the root folder.
	 * <p> 
	 * This is a resource handle operation; neither the container
	 * nor the result need exist in the workspace.
	 * The validation check on the resource name/path is not done
	 * when the resource handle is constructed; rather, it is done
	 * automatically as the resource is created.
	 * </p>
	 *
	 * @param name the string name of the member folder
	 * @return the (handle of the) member folder
	 * @see #getFile(String)
	 */
	public IVirtualFolder getRootFolder();
	
	/**
	 * Returns the project which contains the component. 
	 * <p>
	 * The name of the project may not (and most likely will not) be referenced in the 
	 * runtime path of this virtual path, but will be referenced by the workspace-relative path. 
	 * </p>
	 * <p>
	 * This is a resource handle operation; neither the resource nor the resulting project need
	 * exist.
	 * </p>
	 * 
	 * @return the project handle
	 */
	public IProject getProject();
	
	/**
	 * Returns whether this component is backed by an accessible Component. 
	 */
	public boolean exists();
	
	/**
	 * Returns the components which reference this component.  This is only a one layer
	 * deep search algorithm.
	 * 
	 * @return array of IVirtualComponents
	 */
	public IVirtualComponent[] getReferencingComponents();

}
