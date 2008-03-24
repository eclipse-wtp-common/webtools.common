package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * This graph provides a backward mapping of project component dependencies. It
 * provides a project limited inverse of
 * {@link IVirtualComponent#getReferences()}.
 * 
 * For example:
 * <ul>
 * <li>if the IVirtualComponent for project A has a dependency on the
 * IVirtualComponent for project B, then calling
 * {@link #getReferencingComponents(IProject)} on project B will return project
 * A. </li>
 * <li>if the IVirtualComponent for project A has a dependency on on the
 * IVirtualComponent for a jar in project B, then calling
 * {@link #getReferencingComponents(IProject)} for project B will return project
 * A. This is true even if project B is not defined as an IVirtualComponent.
 * </li>
 * </ul>
 * 
 * Any call to {@link #getReferencingComponents(IProject)} is always expected to
 * be up to date. The only case where a client may need to force an update is if
 * that client is also defining dynamic IVirtualComponent dependencies, i.e. the
 * client is using the org.eclipse.wst.common.modulecore.componentimpl extension
 * point. Only in this case should a client be calling any of
 * {@link #preUpdate()}, {@link #postUpdate()}, or {@link #update(IProject)}
 * 
 */
public interface IDependencyGraph {

	/**
	 * The static instance of this graph
	 */
	public static IDependencyGraph INSTANCE = DependencyGraphImpl.getInstance();

	/**
	 * Returns the set of component projects referencing the specified target
	 * project.
	 * 
	 * @param targetProject
	 * @return
	 */
	public Set<IProject> getReferencingComponents(IProject targetProject);
	
	/**
	 * Returns a modification stamp.  This modification stamp will be different
	 * if the project dependencies ever change.
	 */
	public long getModStamp();
	
	/**
	 * WARNING: this should only be called by implementors of the
	 * org.eclipse.wst.common.modulecore.componentimpl extension point.
	 * 
	 * This method is part of the update API.
	 * 
	 * @see {@link #update(IProject)}
	 */
	public void preUpdate();

	/**
	 * WARNING: this should only be called by implementors of the
	 * org.eclipse.wst.common.modulecore.componentimpl extension point.
	 * 
	 * This method is part of the update API.
	 * 
	 * @see {@link #update(IProject)}
	 */
	public void postUpdate();

	/**
	 * WARNING: this should only be called by implementors of the
	 * org.eclipse.wst.common.modulecore.componentimpl extension point.
	 * 
	 * This method must be called when a resource change is detected which will
	 * affect how dependencies behave. For example, the core IVirtualComponent
	 * framework updates when changes are made to the
	 * .settings/org.eclipse.wst.common.component file changes, and also when
	 * IProjects are added or removed from the workspace. In the case for J2EE,
	 * this occurs when changes are made to the META-INF/MANIFEST.MF file. In
	 * general a call to update should only be made from a fast
	 * {@link IResourceDeltaVisitor}.
	 * 
	 * In order to improve efficiency and avoid unnecessary update processing,
	 * it is necessary to always proceed calls to update() with a call to
	 * preUpdate() and follow with a call to postUpdate() using a try finally
	 * block as follows: <code>
	 * try {
	 *     preUpdate();
	 *     // perform 0 or more update() calls here
	 * } finally {
	 *     IDependencyGraph.INSTANCE.postUpdate();
	 * }    
	 * </code>
	 * 
	 * 
	 */
	public void update(IProject sourceProject);

}
