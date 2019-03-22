/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: IEMFContextContributor.java,v $$
 *  $$Revision: 1.2 $$  $$Date: 2005/02/15 23:04:14 $$ 
 */

package org.eclipse.jem.util.emf.workbench;

/**
 * EMF Context Contributor interface. Implimenters are called to contribute to the context.
 * 
 * @see org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase#createEMFContext(IProject, IEMFContextContributor)
 * @since 1.0.0
 */
public interface IEMFContextContributor {

	/**
	 * This is your opportunity to add a primary EMFNature. Typically you would add to the WorkbenchContext held by <code>aNature</code> in order to
	 * change the container for the WorkbenchURIConverter or add adapter factories to the ResourceSet or anything else that is needed.
	 * 
	 * @param aNature
	 * 
	 * @since 1.0.0
	 */
	void primaryContributeToContext(EMFWorkbenchContextBase aNature);

	/**
	 * This is your opportunity to add a secondary EMFNature. Typically you would add to the WorkbenchContext held by <code>aNature</code> in order
	 * to change the container for the WorkbenchURIConverter or add adapter factories to the ResourceSet or anything else that is needed.
	 * 
	 * @param aNature
	 * 
	 * @since 1.0.0
	 */
	void secondaryContributeToContext(EMFWorkbenchContextBase aNature);

}