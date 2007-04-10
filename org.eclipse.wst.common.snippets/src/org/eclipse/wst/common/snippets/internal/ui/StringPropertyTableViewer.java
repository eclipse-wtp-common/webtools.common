/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wst.common.snippets.internal.SnippetsMessages;
import org.eclipse.wst.common.snippets.internal.util.AccessibleTableViewer;

public class StringPropertyTableViewer {

	protected class MultiCastingSelectionChangedListener implements ISelectionChangedListener {

		public void selectionChanged(SelectionChangedEvent event) {
			ISelection sel = event.getSelection();
			if (sel != null && !sel.isEmpty() && sel instanceof IStructuredSelection) {
				setSelection((String) ((IStructuredSelection) sel).getFirstElement());
			}
			else
				setSelection(null);
			fireSelectionChanged(event);
		}
	}

	protected class StringPropertyLabelProvider implements ITableLabelProvider {

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String text = null;
			if (element == null)
				text = ""; //$NON-NLS-1$
			else
				text = (String) getColumnData()[columnIndex].get(element);
			if (text == null)
				text = ""; //$NON-NLS-1$
			return text;
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
	}

	protected HashMap[] columnData = null;

	protected String[] fColumnNames = null;

	protected boolean fEditFirstColumn = true;
	protected String fSelection;

	protected List fSelectionChangedListeners;

	protected AccessibleTableViewer fTableViewer = null;
	protected List fValueChangedListeners;

	/**
	 * Constructor for StringPropertyTableViewer.
	 */
	public StringPropertyTableViewer() {
		super();
	}

	/**
	 * Constructor for VariableInsertionDialog.
	 * 
	 * @param parentShell
	 */
	public StringPropertyTableViewer(Composite parent, String[] columnNames) {
		super();
		setColumnNames(columnNames);
		createContents(parent);
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		getSelectionChangedListeners().add(listener);
	}

	public void addValueChangedListener(ValueChangedListener listener) {
		getValueChangedListeners().add(listener);
	}

	public void clear() {
		for (int i = 0; i < getColumnNames().length; i++)
			getColumnData()[i].clear();
		refresh();
	}

	public Control createContents(Composite parent) {
		// fTableViewer = new TableViewer(new Table(parent, SWT.FULL_SELECTION
		// | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.BORDER));
		// fTableViewer.getTable().addMouseListener(new EditMouseListener());
		fTableViewer = new AccessibleTableViewer(new Table(parent, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.BORDER));
		fTableViewer.setSingleClickCellSelect(true);

		fTableViewer.getTable().setHeaderVisible(true);
		fTableViewer.getTable().setLinesVisible(true);

		fTableViewer.addSelectionChangedListener(new MultiCastingSelectionChangedListener());

		fTableViewer.setLabelProvider(new StringPropertyLabelProvider());
		fTableViewer.setContentProvider(new ArrayContentProvider());

		TableLayout tlayout = new TableLayout();
		CellEditor[] cellEditors = new CellEditor[getColumnNames().length];
		columnData = new HashMap[getColumnNames().length];
		for (int i = 0; i < getColumnNames().length; i++) {
			getColumnData()[i] = new HashMap();
			tlayout.addColumnData(new ColumnWeightData(40, true));
			TableColumn tc = new TableColumn(fTableViewer.getTable(), SWT.NONE);
			tc.setText(getColumnNames()[i]);
			tc.setResizable(true);
			tc.setWidth(40);
			cellEditors[i] = new TextCellEditor(fTableViewer.getTable());
		}
		fTableViewer.getTable().setLayout(tlayout); // Create the Host column

		fTableViewer.setCellEditors(cellEditors);

		fTableViewer.setColumnProperties(getColumnNames());
		ICellModifier cellmodifier = new ICellModifier() {
			public boolean canModify(Object element, String property) {
				if (getEditFirstColumn())
					return true;
				else if (getColumnNames().length > 1) {
					for (int i = 1; i < getColumnNames().length; i++)
						if (property.equals(getColumnNames()[i]))
							return true;
				}
				return false;
			}

			public Object getValue(Object element, String property) {
				String value = null;
				for (int i = 0; i < getColumnNames().length; i++)
					if (property.equals(getColumnNames()[i]))
						value = (String) getColumnData()[i].get(element);
				if (value == null)
					value = ""; //$NON-NLS-1$
				return value;
			}

			public void modify(Object element, String property, Object value) {
				// Defect #213036, prevent zero-length names
				// Defect #223942, prevent whitespace-only names
				TableItem item = (TableItem) element;
				String key = (String) item.getData();
				if (property.equals(getColumnNames()[0]) && (value.toString().trim().length() < 1) || isNameExcluding(key, value.toString().trim())) {
					return;
				}
				// Defect #222898, problems removing variables
				if (getColumnData()[0].containsKey(key)) {
					for (int i = 0; i < getColumnNames().length; i++) {
						if (property.equals(getColumnNames()[i])) {
							item.setText(i, (String) value);
							String oldValue = (String) getColumnData()[i].get(key);
							getColumnData()[i].put(key, value);
							fireValueChanged(new String(key), property, oldValue, new String((String) value));
						}
					}
				}
			}
		};
		fTableViewer.setCellModifier(cellmodifier);
		fTableViewer.setInput(new ArrayList(getColumnData()[0].keySet()));
		return getControl();
	}

	/**
	 * Closes and applies any editors in progress
	 */
	public void finishEditing() {
		fTableViewer.finishEditing();
	}

	protected void fireSelectionChanged(SelectionChangedEvent event) {
		ISelectionChangedListener[] listeners = new ISelectionChangedListener[getSelectionChangedListeners().size()];
		getSelectionChangedListeners().toArray(listeners);
		for (int i = 0; i < listeners.length; i++)
			listeners[i].selectionChanged(event);
	}

	protected void fireValueChanged(String key, String property, String oldValue, String newValue) {
		ValueChangedListener[] listeners = new ValueChangedListener[getValueChangedListeners().size()];
		getValueChangedListeners().toArray(listeners);
		for (int i = 0; i < listeners.length; i++)
			listeners[i].valueChanged(key, property, oldValue, newValue);
	}

	/**
	 * Gets the columnData.
	 * 
	 * @return Returns a HashMap[]
	 */
	public HashMap[] getColumnData() {
		return columnData;
	}

	// /**
	// * Sets the editNames.
	// * @param editNames The editNames to set
	// */
	// protected void setEditNames(boolean editNames) {
	// fEditNames = editNames;
	// }

	/**
	 * Gets the columnNames.
	 * 
	 * @return Returns a String
	 */
	public String[] getColumnNames() {
		if (fColumnNames == null)
			return new String[]{SnippetsMessages.Variable_Name_3, SnippetsMessages.Value_4}; //$NON-NLS-1$ //$NON-NLS-2$
		return fColumnNames;
	}

	public Control getControl() {

		return getTable();
	}

	/**
	 * Gets the editFirstColumn.
	 * 
	 * @return Returns a boolean
	 */
	public boolean getEditFirstColumn() {
		return fEditFirstColumn;
	}

	public String getSelection() {
		return fSelection;
	}

	protected List getSelectionChangedListeners() {
		if (fSelectionChangedListeners == null)
			fSelectionChangedListeners = new ArrayList();
		return fSelectionChangedListeners;
	}

	/**
	 * Gets the tableViewer.
	 * 
	 * @return Returns a TableViewer
	 */
	public Table getTable() {
		if (fTableViewer == null)
			return null;
		return fTableViewer.getTable();
	}

	/**
	 * Gets the valueChangedListeners.
	 * 
	 * @return Returns a List
	 */
	public List getValueChangedListeners() {
		if (fValueChangedListeners == null)
			fValueChangedListeners = new ArrayList();
		return fValueChangedListeners;
	}

	/**
	 * determines of a name is present anywhere but in the given element's row
	 */
	protected boolean isNameExcluding(Object element, Object value) {
		return isValueExcluding(element, 0, value);
	}

	protected boolean isValueExcluding(Object element, int column, Object value) {
		Iterator keys = getColumnData()[0].keySet().iterator();
		boolean duplicated = false;
		Object key = null;
		while (keys.hasNext()) {
			key = keys.next();
			if (!key.equals(element) && getColumnData()[column].get(key).toString().trim().equals(value))
				duplicated = true;
		}
		return duplicated;
	}

	public void refresh() {
		if (fTableViewer != null) {
			fTableViewer.setInput(new ArrayList(getColumnData()[0].keySet()));
			// fTableViewer.refresh();
		}
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		getSelectionChangedListeners().remove(listener);
	}

	public void removeValueChangedListener(ValueChangedListener listener) {
		getValueChangedListeners().remove(listener);
	}

	/**
	 * Sets the columnData.
	 * 
	 * @param columnData
	 *            The columnData to set
	 */
	public void setColumnData(HashMap[] newColumnData) {
		this.columnData = newColumnData;
	}

	/**
	 * Sets the columnNames.
	 * 
	 * @param columnNames
	 *            The columnNames to set
	 */
	public void setColumnNames(String[] columnNames) {
		fColumnNames = columnNames;
	}

	/**
	 * Sets the editFirstColumn.
	 * 
	 * @param editFirstColumn
	 *            The editFirstColumn to set
	 */
	public void setEditFirstColumn(boolean editFirstColumn) {
		fEditFirstColumn = editFirstColumn;
	}

	/**
	 * Sets the selection.
	 * 
	 * @param selection
	 *            The selection to set
	 */
	public void setSelection(String selection) {
		fSelection = selection;
	}

}
