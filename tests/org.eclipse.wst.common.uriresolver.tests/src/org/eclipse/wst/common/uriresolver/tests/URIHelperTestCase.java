/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.uriresolver.tests;

import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.wst.common.uriresolver.URIHelper;

public class URIHelperTestCase extends TestCase {
	// http://bugs.eclipse.org/343163
	public void testGetInputStream() {
		final long time = System.currentTimeMillis();
		final InputStream stream = URIHelper.getInputStream("http://hans-moleman.w3.org/TR/xhtml1/DTD/xhtml1S-transitional.dtd", 500);
		assertTrue("Opening the inputstream did not bail", (System.currentTimeMillis() - time) < 2000);
		assertNull("No inputstream should have been returned", stream);
	}

}
