/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.common.snippets.core.tests;

import junit.framework.TestCase;

import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.core.ISnippetVariable;
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.model.SnippetManager;
import org.eclipse.wst.common.snippets.internal.palette.SnippetVariable;

public class SnippetCoreTests extends TestCase {

	private SnippetDefinitions fCurrentDefinitions;

	public SnippetCoreTests() {
		super();
	}

	public SnippetCoreTests(String name) {
		super(name);
	}

	private ISnippetVariable getVariable(ISnippetItem item, String id) {
		for (int i = 0; i < item.getVariables().length; i++) {
			if (((SnippetVariable) item.getVariables()[i]).getId().equals(id)) {
				return item.getVariables()[i];
			}
		}
		return null;
	}

	protected void setUp() throws Exception {
		super.setUp();
		fCurrentDefinitions = SnippetManager.getInstance().getDefinitions();
	}

	public void testCategoryLoadedFromPlugin() {
		ISnippetCategory testCategory = fCurrentDefinitions.getCategory("org.eclipse.wst.common.snippets.tests.category0"); //$NON-NLS-1$
		assertNotNull("test category not found", testCategory); //$NON-NLS-1$
		assertEquals("test category source type incorrect", ISnippetsEntry.SNIPPET_SOURCE_PLUGINS, testCategory.getSourceType()); //$NON-NLS-1$
		assertNotNull("test category source description missing", testCategory.getSourceDescriptor()); //$NON-NLS-1$
		assertEquals("test category description incorrect", null, testCategory.getDescription()); //$NON-NLS-1$
		assertEquals("test category label incorrect", "org.eclipse.wst.common.snippets.tests-category", testCategory.getLabel()); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull("test category has no content-type visibility filters", testCategory.getFilters()); //$NON-NLS-1$
		assertTrue("test category has no visibility filters", testCategory.getFilters().length > 0); //$NON-NLS-1$
		assertEquals("test category has wrong content-type visibility filter", "*", testCategory.getFilters()[0]); //$NON-NLS-1$
	}

	public void testItem0LoadedFromPlugin() {
		ISnippetCategory testCategory = fCurrentDefinitions.getCategory("org.eclipse.wst.common.snippets.tests.category0"); //$NON-NLS-1$
		assertNotNull("test category not found", testCategory); //$NON-NLS-1$
		ISnippetItem item = fCurrentDefinitions.getItem("org.eclipse.wst.common.snippets.tests.item0"); //$NON-NLS-1$
		assertNotNull("test item 0 not found", item); //$NON-NLS-1$
		assertEquals("parent category mismatched", testCategory, item.getCategory()); //$NON-NLS-1$
		assertEquals("item content incorrect", "sample content 0", item.getContentString()); //$NON-NLS-1$
		assertEquals("item description incorrect", "", item.getDescription()); //$NON-NLS-1$
		assertEquals("item label incorrect", "test item 0", item.getLabel()); //$NON-NLS-1$
		assertEquals("item source type incorrect", ISnippetsEntry.SNIPPET_SOURCE_PLUGINS, item.getSourceType()); //$NON-NLS-1$
		assertNotNull("item source description missing", item.getSourceDescriptor()); //$NON-NLS-1$
		assertEquals("item has extra variables", 0, item.getVariables().length); //$NON-NLS-1$
		assertTrue("item has visibility filters", item.getFilters().length == 0); //$NON-NLS-1$
	}

	public void testItem1LoadedFromPlugin() {
		ISnippetCategory testCategory = fCurrentDefinitions.getCategory("org.eclipse.wst.common.snippets.tests.category0"); //$NON-NLS-1$
		assertNotNull("test category not found", testCategory); //$NON-NLS-1$
		ISnippetItem item = fCurrentDefinitions.getItem("org.eclipse.wst.common.snippets.tests.item1"); //$NON-NLS-1$
		assertNotNull("test item 1 not found", item); //$NON-NLS-1$
		assertEquals("parent category mismatched", testCategory, item.getCategory()); //$NON-NLS-1$
		assertEquals("item content incorrect", "test content ${variableA} and more ${variableB}", item.getContentString()); //$NON-NLS-1$
		assertEquals("item description incorrect", "", item.getDescription()); //$NON-NLS-1$
		assertEquals("item label incorrect", "test item 1", item.getLabel()); //$NON-NLS-1$
		assertEquals("item source type incorrect", ISnippetsEntry.SNIPPET_SOURCE_PLUGINS, item.getSourceType()); //$NON-NLS-1$
		assertNotNull("item source description missing", item.getSourceDescriptor()); //$NON-NLS-1$
		assertTrue("item has visibility filters", item.getFilters().length == 0); //$NON-NLS-1$
		assertEquals("item has the wrong number of variables", 2, item.getVariables().length); //$NON-NLS-1$

		ISnippetVariable variable = getVariable(item, "variableA");
		assertNotNull("variable A missing", variable);
		assertEquals("variable has the wrong default value", "", variable.getDefaultValue()); //$NON-NLS-1$
		assertEquals("variable has the wrong description", "variable A", variable.getDescription()); //$NON-NLS-1$
		assertEquals("variable has the wrong name", "variableA", variable.getName()); //$NON-NLS-1$

		variable = getVariable(item, "variableB");
		assertNotNull("variable B missing", variable);
		assertEquals("variable has the wrong default value", "bfoo", variable.getDefaultValue()); //$NON-NLS-1$
		assertEquals("variable has the wrong description", "variable B", variable.getDescription()); //$NON-NLS-1$
		assertEquals("variable has the wrong name", "variableB", variable.getName()); //$NON-NLS-1$
	}
}
