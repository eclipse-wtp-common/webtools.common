/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;

/**
 * Provides a handle to a component within a project.
 * <p>
 * This class provides a way to access ArtifactEdit instances. The component
 * referenced by its handle may not exist. 
 * </p>
 * <p>
 * The following class is not intended to be extended.
 * </p>
 * @since 1.0
 */
public class ComponentHandle {

	private final IProject project;

	private final String name;

	private String toString;

	private int hashCode;

	/**
	 * @param aProject
	 *            The containing project
	 * @param aComponentName
	 *            The name of the expected component
	 */
	private ComponentHandle(IProject aProject, String aComponentName) {
		project = aProject;
		name = aComponentName;
	}

	/**
	 * The name of the component.
	 * @return The name of the component
	 */
	public String getName() {
		return name;
	}

	/**
	 * A handle to a valid, accessible project.
	 * @return The project that contains the component
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * Creates a component handle that indicates a component with the name
	 * <code>aComponentName</code> contained by the project
	 * <code>aProject</code>.
	 * 
	 * @param aProject
	 *            The project that will contain the component
	 * @param aComponentName
	 *            The name of the component
	 * @return A ComponentHandle that references the component indicated by the
	 *         project and component name.
	 */
	public static ComponentHandle create(IProject aProject,
			String aComponentName) {
		return new ComponentHandle(aProject, aComponentName);
	}

	/**
	 * Creates a component handle that indicates a component identified by
	 * <code>aComponentURI</code>. The project specified by
	 * <code>aContext</code> is used to resolve the component if the component
	 * URI is not fully qualified.
	 * 
	 * @param aContext
	 *            A starting place to begin resolving the component
	 * @param aComponentURI
	 *            A URI that is either fully qualified or relative to the
	 *            supplied project
	 * @return A ComponentHandle that references the component resolved from the
	 *         URI.
	 */
	public static ComponentHandle create(IProject aContext, URI aComponentURI) {
		IProject componentProject = null;
		String componentName = null;
		if (aComponentURI == null)
			return null;
		if (aComponentURI.segmentCount() == 1) {
			componentProject = aContext;
			componentName = aComponentURI.segment(0);
		} else {
			try {
				componentProject = StructureEdit
						.getContainingProject(aComponentURI);
				componentName = StructureEdit.getDeployedName(aComponentURI);
			} catch (UnresolveableURIException e) {
				return null;
			}
		}

		return new ComponentHandle(componentProject, componentName);
	}

	/**
	 * @return A string representation of the form [projectpath]:componentName
	 */
	public String toString() {
		if (toString == null)
			toString = "[" + project.getFullPath() + "]:" + name; //$NON-NLS-1$ //$NON-NLS-2$   
		return toString;
	}

	/**
	 * @return A hashCode derived from the string representation
	 */
	public int hashCode() {
		if (hashCode == 0)
			hashCode = toString().hashCode();
		return hashCode;
	}

	/**
	 * @param obj
	 *            Another object to compare for equality.
	 * @return true if obj instanceof ComponentHandle && this.project ==
	 *         obj.project && this.name == obj.name
	 */
	public boolean equals(Object obj) {
		if (obj instanceof ComponentHandle) {
			ComponentHandle other = (ComponentHandle) obj;
			return getProject().equals(other.getProject())
					&& ((getName() == null && other.getName() == null) || getName()
							.equals(other.getName()));
		}
		return false;
	}

}
