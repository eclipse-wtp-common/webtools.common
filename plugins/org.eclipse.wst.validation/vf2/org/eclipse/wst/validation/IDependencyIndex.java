/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

/**
 * This service is used to specify the resources that a particular resource depends on.
 * <p>
 * This is an optional service, validators do not need to use this Interface and can simply supply
 * their dependency information through the ValidationResult return result.
 * </p>
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * @noimplement
 * @see ValidationResult
 * @author karasiuk
 *
 */
public interface IDependencyIndex {
	
	/**
	 * Assert that one resource depends on another resource as part of it's validity.
	 * <p>
	 * For example, if an XML file is dependent on an XSD file to be valid, the
	 * resource that holds the XML file would be the dependent, and the resource
	 * that holds the XSD would be the dependsOn.
	 * </p>
	 * 
	 * @param id
	 * 		The validator id that is asserting that the dependency exists.
	 * @param dependent
	 * 		The resource that is dependent on the other resource. For example a XML file.
	 * @param dependsOn
	 * 		The resource that this being depended on. For example a XSD file.
	 */
	void add(String id, IResource dependent, IResource dependsOn);
	
	/**
	 * Remove all the dependency assertions for this project.
	 * 
	 * @param project
	 * 		The project that will have it's dependency assertions removed.
	 */
	void clear(IProject project);
	
	/**
	 * Answer all the resources that depend on this resource. For example, if
	 * this resource was a XSD, this could answer all the XML files that
	 * depended on it for their validity.
	 * 
	 * @param resource
	 * 		A resource that other resources may depend on.
	 * 
	 * @return the dependent resources.
	 */
	List<DependentResource> get(IResource resource);
	
	/**
	 * Answer all the resources that depend on this resource. For example, if
	 * this resource was a XSD, this could answer all the XML files that
	 * depended on it for their validity.
	 * 
	 * @param id
	 * 		The validator id that asserted that the dependency exists.
	 * @param resource
	 * 		A resource that other resources may depend on.
	 * 
	 * @return the dependent resources. This method can return null or an empty array, if
	 * 	there are no dependent resources.
	 */
	IResource[] get(String id, IResource resource);
	
	/**
	 * Answer true if other resources depend on this resource.
	 * 
	 * @param resource
	 * 		The resource being tested.
	 * 
	 * @return true if any of the validators asserted a dependency on this resource.
	 */
	boolean isDependedOn(IResource resource);
	
	/**
	 * Replace all the resources that the dependent depends on.
	 * 
	 * @param id
	 * 		The validator id that is asserting that the dependency exists.
	 * @param dependent
	 * 		The resource that is dependent on the other resource.
	 * @param dependsOn
	 * 		All the resources that are depended on. This can be null or a zero
	 * 		length array. Null or a zero length array removes all the dependencies.
	 */
	void set(String id, IResource dependent, IResource[] dependsOn);

}
