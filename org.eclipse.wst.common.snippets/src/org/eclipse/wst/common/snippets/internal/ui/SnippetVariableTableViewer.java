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
package org.eclipse.wst.common.snippets.internal.ui;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wst.common.snippets.internal.ISnippetVariable;


/**
 * Not ready yet.
 */

public class SnippetVariableTableViewer {
	protected List fLibraryVariables;
	protected ISnippetVariable fSelection;

	protected List fSelectionChangedListeners;


	protected TableViewer fTableViewer = null;
	protected List fValueChangedListeners;
	protected Hashtable fValues = null;

	/**
	 * Constructor for VariableInsertionDialog.
	 * 
	 * @param parentShell
	 */
	public SnippetVariableTableViewer(Composite parent) {
		super();
		createContents(parent);
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		getSelectionChangedListeners().add(listener);
	}

	public void addValueChangedListener(ValueChangedListener listener) {
		getValueChangeListeners().add(listener);
	}

	public Control createContents(Composite parent) {
		// pity we can't just use a PropertySheetPage, but the column headers
		// aren't customizable
		fTableViewer = new TableViewer(new Table(parent, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER));

		// GridData data = new GridData(GridData.FILL_BOTH);
		// fTableViewer.getTable().setLayoutData(data);

		fTableViewer.getTable().setHeaderVisible(true);
		fTableViewer.getTable().setLinesVisible(true);

		fTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection sel = event.getSelection();
				if (sel != null && !sel.isEmpty() && sel instanceof IStructuredSelection)
					fSelection = (ISnippetVariable) ((IStructuredSelection) sel).getFirstElement();
				else
					fSelection = null;
				fireSelectionChanged(event);
			}
		});

		fTableViewer.setContentProvider(new IStructuredContentProvider() {

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				return ((List) inputElement).toArray();
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});

		fTableViewer.setLabelProvider(new ITableLabelProvider() {

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				if (element == null)
					return null;
				ISnippetVariable var = (ISnippetVariable) element;
				if (columnIndex == 0)
					return var.getId();
				else if (columnIndex == 1)
					return (String) fValues.get(var.getId());
				return ""; //$NON-NLS-1$
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
			}
		});

		TableLayout tlayout = new TableLayout();
		tlayout.addColumnData(new ColumnWeightData(35, true));
		tlayout.addColumnData(new ColumnWeightData(65, true));
		fTableViewer.getTable().setLayout(tlayout); // Create the Host column
		TableColumn tc1 = new TableColumn(fTableViewer.getTable(), SWT.NONE);
		tc1.setText("Variable ID"); //$NON-NLS-1$
		tc1.setResizable(true);
		tc1.setWidth(40);
		TableColumn tc2 = new TableColumn(fTableViewer.getTable(), SWT.NONE);
		tc2.setText("Value"); //$NON-NLS-1$
		tc2.setResizable(true);
		tc2.setWidth(40);

		fTableViewer.setCellEditors(new CellEditor[]{new TextCellEditor(fTableViewer.getTable()), new TextCellEditor(fTableViewer.getTable())});

		fTableViewer.setColumnProperties(new String[]{"id", "value"}); //$NON-NLS-1$ //$NON-NLS-2$
		ICellModifier cellmodifier = new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return property.equals("value"); //$NON-NLS-1$
			}

			public Object getValue(Object element, String property) {
				if (property.equals("id")) //$NON-NLS-1$
					return ((ISnippetVariable) element).getId();
				else if (property.equals("value")) //$NON-NLS-1$
					return fValues.get(((ISnippetVariable) element).getId());
				return ""; //$NON-NLS-1$
			}

			public void modify(Object element, String property, Object value) {
				TableItem item = (TableItem) element;
				ISnippetVariable var = (ISnippetVariable) item.getData();
				if (property.equals("value")) { //$NON-NLS-1$
					String oldValue = (String) fValues.get(var.getId());
					fValues.put(var.getId(), value);
					item.setText(1, (String) value);
					fireValueChanged(new String(var.getId()), property, oldValue, new String((String) value));
				}
			}
		};
		fTableViewer.setCellModifier(cellmodifier);
		fTableViewer.setInput(getLibraryVariables());
		refresh();
		return getControl();
	}

	protected void fireSelectionChanged(SelectionChangedEvent event) {
		ISelectionChangedListener[] listeners = new ISelectionChangedListener[getSelectionChangedListeners().size()];
		getSelectionChangedListeners().toArray(listeners);
		for (int i = 0; i < listeners.length; i++)
			listeners[i].selectionChanged(event);
	}

	protected void fireValueChanged(String key, String property, String oldValue, String newValue) {
		ValueChangedListener[] listeners = new ValueChangedListener[getValueChangeListeners().size()];
		getValueChangeListeners().toArray(listeners);
		for (int i = 0; i < listeners.length; i++)
			listeners[i].valueChanged(key, property, oldValue, newValue);
	}

	public Control getControl() {

		return getTable();
	}

	/**
	 * Gets the libraryVariables.
	 * 
	 * @return Returns a List
	 */
	public List getLibraryVariables() {
		if (fLibraryVariables == null)
			fLibraryVariables = new ArrayList();
		return fLibraryVariables;
	}

	public ISnippetVariable getSelectedVariable() {
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

	public String getValue(ISnippetVariable var) {
		return getValue(var.getId());
	}

	public String getValue(String id) {
		if (getValues().containsKey(id))
			return (String) getValues().get(id);
		return ""; //$NON-NLS-1$
	}

	protected List getValueChangeListeners() {
		if (fValueChangedListeners == null)
			fValueChangedListeners = new ArrayList();
		return fValueChangedListeners;
	}

	/**
	 * Gets the values.
	 * 
	 * @return Returns a Hashtable
	 */
	public Hashtable getValues() {
		if (fValues == null)
			fValues = new Hashtable();
		return fValues;
	}

	protected void refresh() {
		if (fLibraryVariables != null) {
			fValues = null;
			for (int i = 0; i < fLibraryVariables.size(); i++) {
				ISnippetVariable var = (ISnippetVariable) fLibraryVariables.get(i);
				getValues().put(var.getId(), var.getDefaultValue());
			}
		}
		if (fTableViewer != null) {
			fTableViewer.setInput(fLibraryVariables);
		}
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		getSelectionChangedListeners().remove(listener);
	}

	public void removeValueChangedListener(ValueChangedListener listener) {
		getValueChangeListeners().remove(listener);
	}

	/**
	 * Sets the libraryVariables.
	 * 
	 * @param libraryVariables
	 *            The libraryVariables to set
	 */
	public void setLibraryVariables(List libraryVariables) {
		fLibraryVariables = libraryVariables;
		refresh();
	}

}