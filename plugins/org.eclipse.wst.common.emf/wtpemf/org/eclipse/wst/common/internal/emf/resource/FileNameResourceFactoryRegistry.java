/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.wst.common.emf.utilities.DefaultOverridableResourceFactoryRegistry;


public abstract class FileNameResourceFactoryRegistry extends DefaultOverridableResourceFactoryRegistry {
	//We are using two lists instead of a Map because we need to iterate
	//the list of simpleFileNames quite a lot.
	protected List simpleFileNames;
	protected List simpleFileNameFactories;

	public FileNameResourceFactoryRegistry() {
		super();
	}

	/**
	 * Return a Resource.Factory that is registered with the last segment of the URI's file name.
	 * 
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl#getFactory(URI)
	 */
	protected Object getFileNameFactory(URI uri) {
		if (simpleFileNames != null) {
			String fileName = uri.lastSegment();
			if (fileName != null) {
				String key;
				for (int i = 0; i < simpleFileNames.size(); i++) {
					key = (String) simpleFileNames.get(i);
					if (fileName.equals(key))
						return simpleFileNameFactories.get(i);
				}
			}
		}
		return null;
	}

	public Resource.Factory getFactory(URI uri) {
		Object resourceFactory = getFileNameFactory(uri);
		if (resourceFactory == null)
			resourceFactory = super.getFactory(uri);
		return (Resource.Factory) resourceFactory;
	}

	/**
	 * Register a file name representing the last segment of a URI with the corresponding
	 * Resource.Factory.
	 */
	public void registerLastFileSegment(String aSimpleFileName, Resource.Factory aFactory) {
		URI uri = URI.createURI(aSimpleFileName);
		String lastSegment = uri.lastSegment();
		int index = getFileNameIndexForAdd(lastSegment);
		setFileName(lastSegment, index);
		setFileNameFactory(aFactory, index);
	}

	private int getFileNameIndexForAdd(String aSimpleFileName) {
		if (simpleFileNames != null) {
			int i = simpleFileNames.indexOf(aSimpleFileName);
			if (i > -1)
				return i;
			return simpleFileNames.size();
		}
		return 0;
	}

	private void setFileNameFactory(Factory aFactory, int index) {
		if (simpleFileNameFactories == null)
			simpleFileNameFactories = new ArrayList();
		simpleFileNameFactories.add(index, aFactory);
	}

	private void setFileName(String aSimpleFileName, int index) {
		if (simpleFileNames == null)
			simpleFileNames = new ArrayList();
		simpleFileNames.add(index, aSimpleFileName);
	}
}