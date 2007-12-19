/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Mar 14, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.wst.common.internal.emf.utilities;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;

/**
 * @author DABERG
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DefaultOverridableResourceFactoryRegistry extends ResourceFactoryRegistryImpl {
	protected static Resource.Factory GLOBAL_FACTORY = Resource.Factory.Registry.INSTANCE.getFactory(URI.createURI(DEFAULT_EXTENSION));

	/**
	 *  
	 */
	public DefaultOverridableResourceFactoryRegistry() {
		super();
	}

	public Resource.Factory getFactory(URI uri) {
		return getFactory(uri, null);
	}

	public Resource.Factory getFactory(URI uri, String contentType)
	{
		Object resourceFactory = null;
		if (resourceFactory == null) {
			String protocol = uri.scheme();
			resourceFactory = protocolToFactoryMap.get(protocol);
			if (resourceFactory == null) {
				String extension = uri.fileExtension();
				resourceFactory = extensionToFactoryMap.get(extension);
				if (resourceFactory == null) {
					resourceFactory = delegatedGetFactory(uri);
					if (resourceFactory == GLOBAL_FACTORY) {
						resourceFactory = extensionToFactoryMap.get(Resource.Factory.Registry.DEFAULT_EXTENSION);
						if (resourceFactory == null)
							resourceFactory = GLOBAL_FACTORY;
					}

				}
			}
		}

		return resourceFactory instanceof Resource.Factory.Descriptor ? ((Resource.Factory.Descriptor) resourceFactory).createFactory() : (Resource.Factory) resourceFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl#delegatedGetFactory(org.eclipse.emf.common.util.URI)
	 */
	protected Factory delegatedGetFactory(URI uri) {
		return Resource.Factory.Registry.INSTANCE.getFactory(uri);
	}
}
