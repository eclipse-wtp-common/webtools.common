/*******************************************************************************
 * Copyright (c) 2009 by SAP AG, Walldorf. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    SAP AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.tests.providers;

import junit.framework.TestCase;

import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.wst.common.snippets.core.ISnippetProvider;
import org.eclipse.wst.common.snippets.internal.util.SnippetProviderManager;
import org.eclipse.wst.common.snippets.tests.helpers.CommonApplicableInterface;
import org.eclipse.wst.common.snippets.tests.helpers.TextSnippetProvider2;
import org.eclipse.wst.common.snippets.ui.TextSnippetProvider;

public class SnippetProviderExtensionTests extends TestCase {

	public void testEnablement() throws Exception {
		ISnippetProvider applicableProvider = SnippetProviderManager.getApplicableProvider(new AbstractTextEditor(){});
		assertNotNull(applicableProvider);
		applicableProvider = SnippetProviderManager.getApplicableProvider(new CommonApplicableInterface(){});
		assertNotNull(applicableProvider);
		assertEquals("dummy", applicableProvider.getId());
		
	}
	
	public void testDefaultTextSnippetProviderRegistration() throws Exception {
		ISnippetProvider applicableProvider = SnippetProviderManager.getApplicableProvider(new AbstractTextEditor(){});
		assertNotNull(applicableProvider);
		assertEquals(applicableProvider.getClass(), TextSnippetProvider.class);
	}
	
	
	public void testPriority() throws Exception {
		ISnippetProvider applicableProvider = SnippetProviderManager.getApplicableProvider(new AbstractTextEditor(){});
		assertNotNull(applicableProvider);
		assertEquals(applicableProvider.getClass(), TextSnippetProvider.class);
		assertNotNull(SnippetProviderManager.findProvider(TextSnippetProvider2.class.getName()));
	}
	
	
	
}
