/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal.util;



import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

/**
 * TAKEN FROM: TableViewerImpl 1.1.2.
 *  - added table field - implemented handleDownArrow and handleUpArrow -
 * added row-wrapping tab/shift-tab traversal
 * 
 * Internal table viewer implementation.
 */
/* package */
abstract class TableViewerImpl implements TraverseListener {


	boolean fIsActivating = false;
	private boolean fSingleClickCellSelect = false;
	private CellEditor fCellEditor;
	private CellEditor[] fCellEditors;
	private ICellModifier fCellModifier;
	private String[] fColumnProperties;
	private Item fTableItem;
	private int fColumnNumber;
	private ICellEditorListener fCellEditorListener;

	protected Table fTable = null;

	TableViewerImpl(Table table) {
		fTable = table;
		initCellEditorListener();
	}

	/**
	 * Activate a cell editor for the given column.
	 */
	private void activateCellEditor() {
		if (fCellEditors != null) {
			if (fCellEditors[fColumnNumber] != null && fCellModifier != null) {
				Object element = fTableItem.getData();
				String property = fColumnProperties[fColumnNumber];
				if (fCellModifier.canModify(element, property)) {
					fCellEditor = fCellEditors[fColumnNumber];
					// table.showSelection();
					fCellEditor.addListener(fCellEditorListener);
					Object value = fCellModifier.getValue(element, property);
					fCellEditor.setValue(value);
					// Tricky flow of control here:
					// activate() can trigger callback to cellEditorListener
					// which will clear cellEditor
					// so must get control first, but must still call
					// activate() even if there is no control.
					Control control = fCellEditor.getControl();
					fCellEditor.activate();
					if (control != null) {
						setLayoutData(fCellEditor.getLayoutData());
						setEditor(control, fTableItem, fColumnNumber);
						fCellEditor.setFocus();
					}
				}
			}
		}
	}

	/**
	 * Activate a cell editor for the given mouse position.
	 */
	private void activateCellEditor(MouseEvent event) {
		if (fTableItem == null || fTableItem.isDisposed()) {
			// item no longer exists
			return;
		}
		int columns = getColumnCount();
		int columnToEdit = -1;
		Rectangle bounds = null;
		for (int i = 0; i < columns; i++) {
			bounds = getBounds(fTableItem, i);
			if (bounds.contains(event.x, event.y)) {
				columnToEdit = i;
				break;
			}
		}
		if (columnToEdit == -1 || bounds == null)
			return;

		fColumnNumber = columnToEdit;
		activateCellEditor();
	}

	/**
	 * Activate the first cell editor.
	 */
	public void activateFirstCellEditor() {
		if (fCellEditors != null) {
			int columnNumber = -1;
			for (int i = 0; i < fCellEditors.length; i++) {
				if (fCellEditors[i] != null && getCellModifier().canModify(fTableItem.getData(), (String) getColumnProperties()[i])) {
					columnNumber = i;
					break;
				}
			}
			if (columnNumber > -1 && fCellModifier != null) {
				fColumnNumber = columnNumber;
				activateCellEditor();
			}
		}
	}

	/**
	 * Deactivates the currently active cell editor.
	 */
	public void applyEditorValue() {
		CellEditor c = this.fCellEditor;
		if (c != null) {
			// null out cell editor before calling save
			// in case save results in applyEditorValue being re-entered
			// see 1GAHI8Z: ITPUI:ALL - How to code event notification when
			// using cell editor ?
			this.fCellEditor = null;
			Item t = this.fTableItem;
			// don't null out table item -- same item is still selected
			if (t != null && !t.isDisposed()) {
				saveEditorValue(c, t);
			}
			setEditor(null, null, 0);
			c.removeListener(fCellEditorListener);
			c.deactivate();
		}
	}

	/**
	 * Cancels the active cell editor, without saving the value back to the
	 * domain model.
	 */
	public void cancelEditing() {
		if (fCellEditor != null) {
			setEditor(null, null, 0);
			fCellEditor.removeListener(fCellEditorListener);
			fCellEditor.deactivate();
			fCellEditor = null;
		}
	}

	/**
	 * Start editing the given element.
	 */
	public void editElement(Object element, int column) {
		if (fCellEditor != null)
			applyEditorValue();

		setSelection(new StructuredSelection(element), true);
		Item[] selection = getSelection();
		if (selection.length != 1)
			return;

		fTableItem = selection[0];

		// Make sure selection is visible
		showSelection();
		fColumnNumber = column;
		activateCellEditor();

	}

	abstract Rectangle getBounds(Item item, int columnNumber);

	public CellEditor[] getCellEditors() {
		return fCellEditors;
	}

	public ICellModifier getCellModifier() {
		return fCellModifier;
	}

	abstract int getColumnCount();

	public Object[] getColumnProperties() {
		return fColumnProperties;
	}

	abstract Item[] getSelection();

	/**
	 * Handles the double click event.
	 */
	public void handleMouseDoubleClick(MouseEvent event) {
		// The last mouse down was a double click. Cancel
		// the cell editor activation.
		// isActivating = false;
	}

	/**
	 * Handles the mouse down event. Activates the cell editor if it is not a
	 * double click.
	 * 
	 * This implementation must: i) activate the cell editor when clicking
	 * over the item's text or over the item's image. ii) activate it only if
	 * the item is already selected. iii) do NOT activate it on a double click
	 * (whether the item is selected or not).
	 */
	public void handleMouseDown(MouseEvent event) {
		if (event.button != 1)
			return;

		boolean wasActivated = isCellEditorActive();
		if (wasActivated)
			applyEditorValue();

		Item[] items = getSelection();
		// Do not edit if more than one row is selected.
		if (items.length != 1) {
			fTableItem = null;
			return;
		}

		if (fTableItem != items[0]) {
			// This mouse down was a selection. Keep the selection and return;
			fTableItem = items[0];
			if (!fSingleClickCellSelect) // only return if you don't want a
											// single click to active the cell
											// editor
				return;
		}

		// It may be a double click. If so, the activation was started by the
		// first click.
		if (fIsActivating || wasActivated)
			return;

		fIsActivating = true;
		// Post the activation. So it may be canceled if it was a double
		// click.
		postActivation(event);
	}

	private void handleRightArrow(TraverseEvent e) {
	}

	private void handleDownArrow(TraverseEvent e) {
		int row = fTable.indexOf((TableItem) fTableItem);
		if (fCellEditors != null && fCellModifier != null && row < fTable.getItemCount() - 1) {
			String property = null;

			TableItem next = fTable.getItem(row + 1);
			Object element = next.getData();
			if (fCellEditors[fColumnNumber] != null) {
				property = fColumnProperties[fColumnNumber];
				if (fCellModifier.canModify(element, property)) {
					applyEditorValue();

					fTableItem = next;
					setSelection(new StructuredSelection(next.getData()), true);
					fTable.select(row + 1);
					showSelection();
					e.doit = false;
				}
			}
			if (e.doit == false)
				startActivationThread();
		}
	}

	private void handleLeftArrow(TraverseEvent e) {
	}

	private void handleUpArrow(TraverseEvent e) {
		int row = fTable.indexOf((TableItem) fTableItem);
		if (fCellEditors != null && fCellModifier != null && row > 0) {
			String property = null;

			TableItem previous = fTable.getItem(row - 1);
			Object element = previous.getData();
			if (fCellEditors[fColumnNumber] != null) {
				property = fColumnProperties[fColumnNumber];
				if (fCellModifier.canModify(element, property)) {
					applyEditorValue();

					fTableItem = previous;
					setSelection(new StructuredSelection(previous.getData()), true);
					fTable.select(row - 1);
					showSelection();
					e.doit = false;
				}
			}
			if (e.doit == false)
				startActivationThread();
		}
	}

	private void handleTabNext(TraverseEvent e) {
		if (fCellEditors != null && fCellModifier != null && fCellEditors.length > (fColumnNumber + 1)) {
			int start = fColumnNumber + 1;
			// CellEditor ce = null;
			String property = null;

			Object element = fTableItem.getData();
			for (int i = start; i <= fCellEditors.length; i++) {
				if (fCellEditors[i] != null) {
					property = fColumnProperties[i];
					if (fCellModifier.canModify(element, property)) {
						fColumnNumber = i;
						e.doit = false;
						break;
					}
				}
			}
			if (e.doit == false)
				startActivationThread();
		}
		else if (fCellEditors != null && fCellModifier != null && fCellEditors.length == (fColumnNumber + 1)) {
			fColumnNumber = 0;
			handleDownArrow(e);
		}
	}

	private void handleTabPrevious(TraverseEvent e) {
		if (fCellEditors != null && fCellModifier != null && fColumnNumber > 0) {
			int start = (fCellEditors.length >= fColumnNumber - 1) ? fColumnNumber - 1 : fCellEditors.length - 1;
			String property = null;

			Object element = fTableItem.getData();
			for (int i = start; i >= 0; i--) {
				if (fCellEditors[i] != null) {
					property = fColumnProperties[i];
					if (fCellModifier.canModify(element, property)) {
						fColumnNumber = i;
						e.doit = false;
						break;
					}
				}
			}
			if (e.doit == false)
				startActivationThread();
		}
		else if (fCellEditors != null && fCellModifier != null && fColumnNumber == 0) {
			fColumnNumber = getColumnCount() - 1;
			handleUpArrow(e);
		}
	}

	private void initCellEditorListener() {
		fCellEditorListener = new ICellEditorListener() {
			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
				// Ignore.
			}

			public void cancelEditor() {
				TableViewerImpl.this.cancelEditing();
			}

			public void applyEditorValue() {
				TableViewerImpl.this.applyEditorValue();
			}
		};
	}

	/**
	 * Returns <code>true</code> if there is an active cell editor;
	 * otherwise <code>false</code> is returned.
	 */
	public boolean isCellEditorActive() {
		return fCellEditor != null;
	}

	public void keyTraversed(TraverseEvent e) {
		if (e.detail == SWT.TRAVERSE_TAB_NEXT) {
			applyEditorValue();
			handleTabNext(e);
		}
		else if (e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
			applyEditorValue();
			handleTabPrevious(e);
		}
		else if (e.keyCode == SWT.ARROW_RIGHT) {
			handleRightArrow(e);
		}
		else if (e.keyCode == SWT.ARROW_LEFT) {
			handleLeftArrow(e);
		}
		else if (e.keyCode == SWT.ARROW_UP) {
			handleUpArrow(e);
		}
		else if (e.keyCode == SWT.ARROW_DOWN) {
			handleDownArrow(e);
		}
	}

	/**
	 * Handle the mouse down event. Activate the cell editor if it is not a
	 * doble click.
	 */
	private void postActivation(final MouseEvent event) {
		if (!fIsActivating)
			return;

		(new Thread() {
			public void run() {
				if (fIsActivating) {
					getDisplay().asyncExec(new Runnable() {
						public void run() {
							activateCellEditor(event);
							fIsActivating = false;
						}
					});
				}
			}
		}).start();
	}

	private Display getDisplay() {

		return PlatformUI.getWorkbench().getDisplay();
	}

	/**
	 * Saves the value of the currently active cell editor, by delegating to
	 * the cell modifier.
	 */
	private void saveEditorValue(CellEditor cellEditor, Item tableItem) {
		if (fCellModifier != null) {
			if (!cellEditor.isValueValid()) {
				// /Do what ???
			}
			String property = null;
			if (fColumnProperties != null && fColumnNumber < fColumnProperties.length)
				property = fColumnProperties[fColumnNumber];
			fCellModifier.modify(tableItem, property, cellEditor.getValue());
		}
	}

	public void setCellEditors(CellEditor[] editors) {
		this.fCellEditors = editors;
		Control control;
		for (int i = 0; i < editors.length; i++) {
			if (editors[i] != null) {
				control = editors[i].getControl();
				if (control != null)
					control.addTraverseListener(this);
			}
		}
	}

	public void setCellModifier(ICellModifier modifier) {
		this.fCellModifier = modifier;
	}

	public void setColumnProperties(String[] columnProperties) {
		this.fColumnProperties = columnProperties;
	}

	abstract void setEditor(Control w, Item item, int columnNumber);

	abstract void setLayoutData(CellEditor.LayoutData layoutData);

	abstract void setSelection(StructuredSelection selection, boolean b);

	abstract void showSelection();

	/**
	 * Gets the singleClickCellSelect.
	 * 
	 * @return Returns a boolean
	 */
	public boolean getSingleClickCellSelect() {
		return fSingleClickCellSelect;
	}

	/**
	 * Sets the singleClickCellSelect.
	 * 
	 * @param singleClickCellSelect
	 *            The singleClickCellSelect to set
	 */
	public void setSingleClickCellSelect(boolean singleClickCellSelect) {
		fSingleClickCellSelect = singleClickCellSelect;
	}

	public void setTableItem(Item item) {
		fTableItem = item;
	}

	private void startActivationThread() {
		(new Thread() {
			public void run() {
				getDisplay().asyncExec(new Runnable() {
					public void run() {
						activateCellEditor();
						fIsActivating = false;
					}
				});
			}
		}).start();
	}

}
