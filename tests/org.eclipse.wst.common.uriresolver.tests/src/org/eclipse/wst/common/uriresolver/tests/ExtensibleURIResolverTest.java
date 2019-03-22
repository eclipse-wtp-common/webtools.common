/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.common.uriresolver.tests;

import junit.framework.TestCase;

import org.eclipse.wst.common.uriresolver.internal.ExtensibleURIResolver;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.common.uriresolver.tests.util.AuxiliaryCounter;
import org.eclipse.wst.common.uriresolver.tests.util.ResolverExtensionForText;

public class ExtensibleURIResolverTest extends TestCase {


	public void testExtensibleURIResolver() {

		URIResolver resolver = URIResolverPlugin.createResolver();
		if (resolver instanceof ExtensibleURIResolver) {
			ExtensibleURIResolver extensibleURIResolver = (ExtensibleURIResolver) resolver;

			// Reset counter
			AuxiliaryCounter.getInstance().resetCounter();

			// Resolve using public id
			String publicResolve = extensibleURIResolver.resolve(null, ResolverExtensionForText.PUBLIC_ID_URL, null);
			assertEquals(ResolverExtensionForText.PUBLIC_ID_LOCATION, publicResolve);

			// Resolve using system id
			String systemResolve = extensibleURIResolver.resolve(null, null, ResolverExtensionForText.SYSTEM_ID_URL);
			assertEquals(ResolverExtensionForText.SYSTEM_ID_LOCATION, systemResolve);

			// Verify the number of times the resolvers have been called
			int count = AuxiliaryCounter.getInstance().getCount();
			assertEquals(2, count);
		}
	}


}
