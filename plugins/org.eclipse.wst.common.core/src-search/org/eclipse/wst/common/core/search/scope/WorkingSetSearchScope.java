/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.common.core.search.scope;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;

/**
 * This class is required to wrap the content of an IWorkingSet.
 * We can't use IWorkingSet directly because it's part of an eclipse.ui
 * component. Therefore, we need to create this object as a surrogate.
 * <br>
 * Usage: We get the array IAdaptable[] of the IWorkingSet object and use this
 * array as argument for all methods of this class including the constructor.
 */
public class WorkingSetSearchScope extends SearchScopeImpl{
	
	/**
	 * Creates an empty scope when no resources is given.
	 */
	public WorkingSetSearchScope(){
		super();
	}
  
	/**
	 * Intended to be used with an IWorkingSet
	 * <br>
	 * For example:
	 * <pre>
	 * IWorkingSet oneWorkingSet;
	 * ...
	 * IAdaptable[] elements = oneWorkingSet.getElements();
	 * WorkingSetSearchScope scope = new WorkingSetSearchScope(elements);
	 * </pre>
	 */
	public WorkingSetSearchScope(IAdaptable[] elements)
	{
		super();
		addElementsOfWorkingSet(elements);
	}
	
	/**
	 * Intended to be used with an IWorkingSet
	 * <br>
	 * For example:
	 * <pre>
	 * WorkingSetSearchScope scope = ..; 
	 * ...
	 * IWorkingSet anotherWorkingSet; 
	 * IAdaptable[] elements = anotherWorkingSet.getElements();
	 * scope.addAWorkingSetToScope(elements);
	 * </pre>
	 */
	public void addAWorkingSetToScope(IAdaptable[] elements){
		addElementsOfWorkingSet(elements);
	}
	
	private void addElementsOfWorkingSet(IAdaptable[] elements){
		for (int j = 0; j < elements.length; j++){
			IContainer container = (IContainer) elements[j].getAdapter(IContainer.class);
			if ( container != null ){
				traverseContainer(container);
			}
			else{
				IFile aFile = (IFile) elements[j].getAdapter(IFile.class);
				if ( aFile != null)
					acceptFile(aFile);
			}
		}
	}
}
