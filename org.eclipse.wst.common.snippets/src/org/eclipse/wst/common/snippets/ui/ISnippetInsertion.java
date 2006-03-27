/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.snippets.ui;

import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.snippets.core.ISnippetItem;

/**
 * An insertion class is responsible for inserting a snippet item's contents
 * into editors. Implementors are responsible for supporting the drag-and-drop
 * mechanism and inserting the snippet's text into a given editor part.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 1.0
 */
public interface ISnippetInsertion {

	/**
	 * The data is required from the drag source.
	 * 
	 * <p>
	 * The following fields in the DragSourceEvent should be examined/updated
	 * by the Insertion object:
	 * <ul>
	 * <li>dataType - (in) the type of data requested. This is a TransferData
	 * object and can be used with the Transfer subclasses.
	 * <li>data - (out) the application inserts the actual data here (must
	 * match the dataType)
	 * </ul>
	 * </p>
	 * 
	 * Subclasses should check which Transfer types are supported and set the
	 * data accordingly.
	 * 
	 * @param event -
	 *            the information associated with the drag set data event
	 * @param item -
	 *            the ISnippetItem instance from which to derive the data
	 */
	void dragSetData(DragSourceEvent event, ISnippetItem item);

	/**
	 * The Transfer types to support from a drag source. May be null. Required
	 * to do anything more complicated than a simple text drop with DND, a
	 * matching IDropAction should be registered to handle the insertion at
	 * drop-time.
	 * 
	 * @return the list of valid transfer types during Drag and Drop
	 */
	Transfer[] getTransfers();

	/**
	 * Insert the current ISnippetItem's insertion String to the given
	 * IEditorPart. Used by double-click behavior.
	 * 
	 * @param editorPart
	 *            the editor part into which to insert
	 */
	void insert(IEditorPart editorPart);

	/**
	 * The target editorpart in the workbench window. May be used as a hint
	 * for determining which Transfer types to allow and what transfer data to
	 * set during Drag and Drop operations.
	 * 
	 * @param targetPart the target editor part
	 */
	void setEditorPart(IEditorPart targetPart);

	/**
	 * Use this ISnippetItem
	 * 
	 * @param item
	 *            the item to insert
	 */
	void setItem(ISnippetItem item);
}