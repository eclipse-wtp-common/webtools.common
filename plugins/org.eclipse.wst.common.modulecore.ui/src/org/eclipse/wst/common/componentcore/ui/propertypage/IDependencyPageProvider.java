/******************************************************************************
 * Copyright (c) 2009 Red Hat
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.propertypage;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;

/**
 * Suggested use case of this class is as follows:
 * 
 * 		provider = DependencyPageExtensionManager.getManager().getProvider(facetedProject);
 *		if( provider != null ) {
 *			controls = provider.createPages(facetedProject, this);
 *			Composite root = provider.createRootControl(controls, parent);
 *		}
 */
public interface IDependencyPageProvider {
	/**
	 * Returns true if this page provider knows how to 
	 * handle the given faceted project, false otherwise
	 * @param project a project
	 * @return true if this provider can handle it, false otherwise
	 */
	public boolean canHandle(IFacetedProject project);
	
	/**
	 * Returns a list of page control objects which should be
	 * presented to the user for this project type. 
	 * 
	 * @param project
	 * @param parent
	 * @return
	 */
	public IModuleDependenciesControl[] createPages(IFacetedProject project, ModuleAssemblyRootPage parent);
	
	/**
	 * Turn this array of pages / controls into one root control
	 * that can be returned to the Properties Page container. 

	 * @param pages
	 * @param parent
	 * @return
	 */
	public Composite createRootControl(IFacetedProject project, IModuleDependenciesControl[] pages, Composite parent);

	/**
	 * 
	 * @return String - page title string or null if default title is desired
	 */
	public String getPageTitle(IProject project);
	
}
