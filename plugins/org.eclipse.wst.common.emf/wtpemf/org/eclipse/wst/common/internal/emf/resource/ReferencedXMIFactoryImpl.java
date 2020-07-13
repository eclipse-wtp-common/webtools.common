/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

public class ReferencedXMIFactoryImpl extends ResourceFactoryImpl {

	protected static List globalAdapterFactories;
	protected List localAdapterFactories;

	/**
	 * ReferencedXMIFactoryImpl constructor comment.
	 */
	public ReferencedXMIFactoryImpl() {
		super();
	}

	/**
	 * This is the method that subclasses can override to actually instantiate a new Resource
	 * 
	 * @param uri
	 * @return
	 */
	protected Resource doCreateResource(URI uri) {
		return new ReferencedXMIResourceImpl(uri);
	}

	/**
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl#createResource(URI)
	 */
	@Override
	public final Resource createResource(URI uri) {
		Resource res = doCreateResource(uri);
		adaptNew(res);
		return res;
	}

	protected void adaptNew(Resource res) {
		if (globalAdapterFactories != null) {
			for (int i = 0; i < globalAdapterFactories.size(); i++) {
				AdapterFactory factory = (AdapterFactory) globalAdapterFactories.get(i);
				factory.adaptAllNew(res);
			}
		}
		if (localAdapterFactories != null) {
			for (int i = 0; i < localAdapterFactories.size(); i++) {
				AdapterFactory factory = (AdapterFactory) localAdapterFactories.get(i);
				factory.adaptAllNew(res);
			}
		}
	}

	/**
	 * The local adapter factory is an adapter factory that you use to only adapt the resource
	 * specific to the ResourceFactory instance.
	 * 
	 * @param factory
	 */
	public void addLocalAdapterFactory(AdapterFactory factory) {
		if (localAdapterFactories == null)
			localAdapterFactories = new ArrayList(3);
		localAdapterFactories.add(factory);
	}

	public void removeLocalAdapterFactory(AdapterFactory factory) {
		if (localAdapterFactories != null)
			localAdapterFactories.remove(factory);
	}

	/**
	 * A global adapter factory will be used to adapt any resource created by any ResourceFactory
	 * instance.
	 * 
	 * @param factory
	 */
	public static void addGlobalAdapterFactory(AdapterFactory factory) {
		if (globalAdapterFactories == null)
			globalAdapterFactories = new ArrayList(3);
		globalAdapterFactories.add(factory);
	}

	public static void removeGlobalAdapterFactory(AdapterFactory factory) {
		if (globalAdapterFactories != null)
			globalAdapterFactories.remove(factory);
	}
}

