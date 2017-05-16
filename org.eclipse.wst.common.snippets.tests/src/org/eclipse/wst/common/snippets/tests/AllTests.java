/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.common.snippets.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.common.snippets.core.tests.SnippetCoreTests;
import org.eclipse.wst.common.snippets.tests.providers.SnippetProviderExtensionTests;
import org.eclipse.wst.common.snippets.tests.providers.TextProviderTests;
import org.eclipse.wst.common.snippets.tests.scenarios.ObjectOrientedSnippetScenario;
import org.eclipse.wst.common.snippets.ui.tests.SnippetUITests;


public class AllTests extends TestSuite {

	public static Test suite() {
		return new AllTests();
	}

	public AllTests() {
		super();
		addTest(new TestSuite(SnippetCoreTests.class));
		addTest(new TestSuite(SnippetProviderExtensionTests.class));
		addTest(new TestSuite(TextProviderTests.class));
		addTest(new TestSuite(SnippetUITests.class));
		addTest(new TestSuite(ObjectOrientedSnippetScenario.class));
	}
}
