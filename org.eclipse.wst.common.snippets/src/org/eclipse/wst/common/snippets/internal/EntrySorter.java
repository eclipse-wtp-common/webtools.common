/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal;



import com.ibm.icu.text.Collator;
import java.util.Arrays;
import java.util.List;

import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.common.snippets.internal.util.Sorter;

public class EntrySorter extends Sorter {

	public static ISnippetCategory[] sortCategories(Object[] categories) {
		Object sortedEntries[] = new EntrySorter().sort(categories);
		ISnippetCategory[] results = new ISnippetCategory[sortedEntries.length];
		for (int i = 0; i < results.length; i++)
			results[i] = (ISnippetCategory) sortedEntries[i];
		return results;
	}

	public static List sortEntries(List entries) {
		return Arrays.asList(new EntrySorter().sort(entries.toArray()));
	}

	public static ISnippetItem[] sortItems(Object[] items) {
		Object sortedEntries[] = new EntrySorter().sort(items);
		ISnippetItem[] results = new ISnippetItem[sortedEntries.length];
		for (int i = 0; i < results.length; i++)
			results[i] = (ISnippetItem) sortedEntries[i];
		return results;
	}


	/**
	 * (non-Javadoc)
	 * 
	 * @see Sorter#compare(Object, Object)
	 */
	Collator collator = Collator.getInstance();

	public boolean compare(Object elementOne, Object elementTwo) {
		/**
		 * Returns true if elementTwo is 'greater than' elementOne This is the
		 * 'ordering' method of the sort operation. Each subclass overides
		 * this method with the particular implementation of the 'greater
		 * than' concept for the objects being sorted.
		 */
		ISnippetsEntry entry1 = (ISnippetsEntry) elementOne;
		ISnippetsEntry entry2 = (ISnippetsEntry) elementTwo;
		return (collator.compare(entry1.getLabel(), entry2.getLabel())) < 0;
	}
}