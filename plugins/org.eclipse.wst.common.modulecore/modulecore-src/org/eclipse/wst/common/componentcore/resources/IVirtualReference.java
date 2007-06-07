/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.resources;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.common.componentcore.internal.DependencyType;

/**
 * Represents a dependency between two components: EnclosingComponent->ReferencedComponent. 
 * <p>
 * The ReferencedComponent may exist in another project or as a binary form on the classpath 
 * of the project.
 * </p>
 * @plannedfor 1.0
 */
public interface IVirtualReference {
	
	/**
	 * Indicates that the dependency should be archived into a *.?ar format before being absorbed.
	 */
	int DEPENDENCY_TYPE_USES = DependencyType.USES;
	/**
	 * Indicates that the dependency will be absorbed as is without archiving. 
	 */
	int DEPENDENCY_TYPE_CONSUMES = DependencyType.CONSUMES;
	
	/**
	 * Creates this virtual reference in model, if it doesn't already exist.
	 * @param updateFlags Currently no update flags apply. 
	 * @param aMonitor A progress monitor to track the completion of the operation
	 */
	public void create(int updateFlags, IProgressMonitor aMonitor);
	
	/**
	 * Returns whether this reference actual exists in the model
	 * @return whether this reference actual exists in the model
	 */
	public boolean exists();
	
	/**
	 * The runtime path indicates where the contents of the referenced
	 * component will be absorbed within the context of the enclosing component.
	 * @param aRuntimePath A value component-relative path. 
	 */
	public void setRuntimePath(IPath aRuntimePath);
	/**
	 * The runtime path indicates where the contents of the referenced
	 * component will be absorbed within the context of the enclosing component.
	 * @return A value component-relative path. 
	 */
	public IPath getRuntimePath();
	
	/**
	 * The dependencyType indicates how the contents of the referenced component will be absorbed.
	 * @param aDependencyType One of DEPENDENCY_TYPE_USES or DEPENDENCY_TYPE_CONSUMES
	 * @see #DEPENDENCY_TYPE_CONSUMES
	 * @see #DEPENDENCY_TYPE_USES
	 */
	public void setDependencyType(int aDependencyType);
	

	/**
	 * @return One of DEPENDENCY_TYPE_USES or DEPENDENCY_TYPE_CONSUMES
	 * @see #DEPENDENCY_TYPE_CONSUMES
	 * @see #DEPENDENCY_TYPE_USES
	 */
	public int getDependencyType();
	
	/**
	 * The enclosing component contains this reference, and will absorb the contents of the referenced component
	 * @return The enclosing component
	 */
	public IVirtualComponent getEnclosingComponent();
	
	/**
	 * The referenced component is "targeted" by the reference, and will be absorbed by the enclosing component. 
	 * @return the referenced component.
	 */
	public IVirtualComponent getReferencedComponent();
	
	/**
	 * Set the referenced component that is "targeted" by the reference and will be absorbed by the enclosing component. 
	 * @param referencedComponent
	 */
	public void setReferencedComponent(IVirtualComponent referencedComponent, EObject dependentObject);
	
	
	/**
	 * Get the archive name of the referenced component
	 * @return
	 */
	public String getArchiveName();

	/**
	 * Set the archive name of the referenced component
	 * @param archiveName
	 */
	public void setArchiveName(String archiveName);
	
}
