/*******************************************************************************
 * Copyright (c) 2003, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.frameworks.internal.ui.TimedModifyListener;

public class DataModelSynchHelper implements IDataModelListener {
	protected static final boolean isLinux = System.getProperty("os.name").equals("Linux"); //$NON-NLS-1$ //$NON-NLS-2$

	protected IDataModel dataModel;
	protected Map widgetToPropertyHash;
	protected Map propertyToWidgetHash;
	protected Map widgetToDepControls;

	protected String currentProperty;
	protected Widget currentWidget;
	protected Widget currentWidgetFromEvent; // TODO M4 see if this should be set with for
	// listeners
	protected boolean ignoreModifyEvent = false;

	private class ModifyTextListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			if (ignoreModifyEvent)
				return;
			Text text = (Text) e.getSource();
			if (currentWidget == text)
				return;
			String propertyName = (String) widgetToPropertyHash.get(text);
			setProperty(propertyName, text.getText());
		}
	}

	private ModifyTextListener modifyTextListener;
	private TimedModifyListener timedModifyListener;

	private class ComboListener implements SelectionListener, ModifyListener {

		public void modifyText(ModifyEvent e) {
			if (ignoreModifyEvent)
				return;
			Combo combo = (Combo) e.getSource();
			if (currentWidget == combo)
				return;

			try {
				currentWidgetFromEvent = combo;
				String propertyName = (String) widgetToPropertyHash.get(combo);
				DataModelPropertyDescriptor[] descriptors = dataModel.getValidPropertyDescriptors(propertyName);
				String description = combo.getText();
				// On a combo selection linux fires 2 events;
				// the first clears the value which needs to be ignored when the type is not String
				// the second sets the new value
				if (isLinux && description.length() == 0 && descriptors.length != 0 && !(descriptors[0].getPropertyValue() instanceof String)) {
					return;
				}
				for (int i = 0; i < descriptors.length; i++) {
					if (description.equals(descriptors[i].getPropertyDescription())) {
						setProperty(propertyName, descriptors[i].getPropertyValue());
						return;
					}
				}
				setProperty(propertyName, combo.getText());
			} finally {
				currentWidgetFromEvent = null;
			}
		}

		public void widgetSelected(SelectionEvent e) {
			Combo combo = (Combo) e.getSource();
			if (currentWidget == combo)
				return;
			String propertyName = (String) widgetToPropertyHash.get(combo);
			if (combo.getSelectionIndex() >= 0) {
				DataModelPropertyDescriptor[] descriptors = dataModel.getValidPropertyDescriptors(propertyName);
				String description = combo.getItem(combo.getSelectionIndex());
				for (int i = 0; i < descriptors.length; i++) {
					if (description.equals(descriptors[i].getPropertyDescription())) {
						setProperty(propertyName, descriptors[i].getPropertyValue());
						return;
					}
				}
				setProperty(propertyName, combo.getItem(combo.getSelectionIndex()));
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}

	private ComboListener comboListener;

	private class CheckboxSelectionListener implements SelectionListener {
		public void widgetSelected(SelectionEvent e) {
			Button button = (Button) e.getSource();
			if (currentWidget == button)
				return;
			String propertyName = (String) widgetToPropertyHash.get(button);
			setProperty(propertyName, new Boolean(button.getSelection()));
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}

	private CheckboxSelectionListener checkboxSelectionListener;

	public DataModelSynchHelper(IDataModel model) {
		this.dataModel = model;
		dataModel.addListener(this);
	}

	private CheckBoxViewerListener checkBoxViewerStateListener;

	private class CheckBoxViewerListener implements ICheckStateListener {
		public void checkStateChanged(CheckStateChangedEvent event) {
			StructuredViewer viewer = (StructuredViewer) event.getSource();
			CheckboxTableViewer checkBoxTableViewer;
			CheckboxTreeViewer checkBoxTreeViewer;
			Object[] items = null;
			if (viewer instanceof CheckboxTableViewer) {
				checkBoxTableViewer = (CheckboxTableViewer) viewer;
				items = checkBoxTableViewer.getCheckedElements();
			} else if (viewer instanceof CheckboxTreeViewer) {
				checkBoxTreeViewer = (CheckboxTreeViewer) viewer;
				items = checkBoxTreeViewer.getCheckedElements();
			}
			String propertyName = (String) widgetToPropertyHash.get(viewer.getControl());
			setPropertyItems(propertyName, items);
		}
	}

	public void propertyChanged(DataModelEvent event) {
		String propertyName = event.getPropertyName();
		int flag = event.getFlag();
		if (flag == DataModelEvent.ENABLE_CHG)
			setEnablement(propertyName, event.isPropertyEnabled());
		else
			synchUIWithModel(propertyName, flag);
	}

	protected void setProperty(String propertyName, Object value) {
		currentProperty = propertyName;
		try {
			dataModel.setProperty(propertyName, value);
		} finally {
			currentProperty = null;
		}
	}

	protected void setPropertyItems(String propertyName, Object[] items) {
		currentProperty = propertyName;
		try {
			dataModel.setProperty(propertyName, items);
		} finally {
			currentProperty = null;
		}
	}

	/**
	 * Sets the UI to have the property value defined in the model
	 * 
	 * @param propertyName
	 * @link IDataModelListener for the flag values.
	 */
	public void synchUIWithModel(final String propertyName, final int flag) {
		if (null != propertyToWidgetHash && propertyToWidgetHash.containsKey(propertyName)) {
			if(Thread.currentThread() == Display.getDefault().getThread()){
				doSynchUIWithModel(propertyName, flag);
			} else {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						doSynchUIWithModel(propertyName, flag);
					}
				});
			}
		}
	}
	/**
	 * This method must run on the UI thread
	 * @param propertyName
	 * @param flag
	 */
	private void doSynchUIWithModel(final String propertyName, final int flag) {
		try {
			currentWidget = (Widget) propertyToWidgetHash.get(propertyName);
			if (currentWidget != null && currentWidget != currentWidgetFromEvent) {
				//We must hold a copy in a temp variable because setting the widget value
				//may trigger an event that will cause this method to be called again.
				Widget widget = currentWidget;
				try {
					ignoreModifyEvent = true;
					if (currentWidget instanceof Text)
						setWidgetValue(propertyName, flag, (Text) currentWidget);
					else if (currentWidget instanceof Combo) {
						setWidgetValue(propertyName, flag, (Combo) currentWidget);
					} else if (currentWidget instanceof Button)
						setWidgetValue(propertyName, flag, (Button) currentWidget);
					else if (currentWidget instanceof Label)
						setWidgetValue(propertyName, flag, (Label) currentWidget);
					else if (currentWidget instanceof List)
						setWidgetValue(propertyName, flag, (List) currentWidget);
					else if (currentWidget instanceof Table)
						setWidgetValue(propertyName, flag, (Table) currentWidget);
					else if (currentWidget instanceof Tree)
						setWidgetValue(propertyName, flag, (Tree) currentWidget);
				} finally {
					ignoreModifyEvent = false;
				}
				//Pass the copy of the currentWidget
				setEnablement((Control) widget, dataModel.isPropertyEnabled(propertyName));
			}
		} finally {
			currentWidget = null;
		}
	}
	

	/**
	 * @param control
	 * @param enabled
	 */
	protected void setEnablement(Control control, boolean enabled) {
		if (control.isEnabled() != enabled)
			control.setEnabled(enabled);
		setDependentControlEnablement(control, enabled);
	}

	private void setEnablement(final String propertyName, final boolean enabled) {
		if (propertyToWidgetHash != null) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					Control control = (Control) propertyToWidgetHash.get(propertyName);
					if (control != null) {
						setEnablement(control, enabled);
					}
				}
			});
		}
	}

	/**
	 * @param control
	 * @param enabled
	 */
	private void setDependentControlEnablement(Control control, boolean enabled) {
		if (widgetToDepControls != null) {
			Control[] dependents = (Control[]) widgetToDepControls.get(control);
			if (dependents != null) {
				Control dep = null;
				for (int i = 0; i < dependents.length; i++) {
					dep = dependents[i];
					if (dep.isEnabled() != enabled)
						dep.setEnabled(enabled);
				}
			}
		}
	}

	protected void setWidgetValue(String propertyName, int flag, Button button) {
		if ((button.getStyle() & SWT.CHECK) == SWT.CHECK || (button.getStyle() & SWT.RADIO) == SWT.RADIO) {
			boolean checked = dataModel.getBooleanProperty(propertyName);
			if (button.getSelection() != checked) {
				button.setSelection(checked);
			}
		}
	}

	protected void setWidgetValue(String propertyName, int flag, Combo combo) {
		if (flag == DataModelEvent.VALID_VALUES_CHG || combo.getItemCount() == 0) {
			// Display properties should only fire if the contents change.
			DataModelPropertyDescriptor[] descriptors = dataModel.getValidPropertyDescriptors(propertyName);
			String[] items = new String[descriptors.length];
			for (int i = 0; i < descriptors.length; i++) {
				items[i] = descriptors[i].getPropertyDescription();
			}
			combo.setItems(items);
		}
		String newText = dataModel.getPropertyDescriptor(propertyName).getPropertyDescription();
		int selIndex = combo.getSelectionIndex();
		if (selIndex < 0 || !newText.equals(combo.getItem(selIndex))) {
			String[] items = combo.getItems();
			for (int i = 0; i < items.length; i++) {
				if (items[i].equals(newText)) {
					combo.select(i);
					return;
				}
			}
		}
		combo.setText(newText);
	}

	protected void setWidgetValue(String propertyName, int flag, Text text) {
		String newText = dataModel.getStringProperty(propertyName);
		if (!newText.equals(text.getText())) {
			text.setText(newText);
		}
	}

	protected void setWidgetValue(String propertyName, int flag, Table table) {
		Object[] elements = (Object[]) dataModel.getProperty(propertyName);
		if (elements == null || elements.length == 0) {
			setTableItemsChecked(table.getItems(), false);
			return;
		}
		java.util.List elementList = Arrays.asList(elements);
		TableItem[] tableItems = table.getItems();
		for (int i = 0; i < tableItems.length; i++) {
			TableItem item = tableItems[i];
			if (elementList.contains(item.getData()))
				item.setChecked(true);
			else
				item.setChecked(false);
		}
	}

	protected void setTableItemsChecked(TableItem[] tableItems, boolean b) {
		for (int i = 0; i < tableItems.length; i++)
			tableItems[i].setChecked(b);
	}

	protected void setWidgetValue(String propertyName, int flag, Tree tree) {
		Object[] elements = (Object[]) dataModel.getProperty(propertyName);
		if (elements == null)
			return;
		java.util.List elementList = Arrays.asList(elements);
		TreeItem[] treeItems = tree.getItems();
		if (elementList.size() == 0) {
			setTreeItemsGrey(treeItems, false);
			return;
		}
		if (treeItems.length > 0)
			setTreeItemChecked(treeItems, elementList);
	}

	public void setTreeItemsGrey(TreeItem[] treeItems, boolean b) {
		for (int i = 0; i < treeItems.length; i++) {
			TreeItem item = treeItems[i];
			item.setGrayed(b);
			setTreeItemsGrey(treeItems[i].getItems(), b);
		}
	}

	protected void setTreeItemChecked(TreeItem[] treeItems, java.util.List items) {
		for (int i = 0; i < treeItems.length; i++) {
			if (items.contains(treeItems[i].getData())){
				treeItems[i].setChecked(true);
				treeItems[i].setGrayed(false);
			}else{
				treeItems[i].setGrayed(true);
			}
			TreeItem[] childernItems = treeItems[i].getItems();
			if (childernItems.length > 0) {
				treeItems[i].setExpanded(true);
				setTreeItemChecked(childernItems, items);
			}
		}
	}

	protected void setWidgetValue(String propertyName, int flag, Label label) {
		String newText = dataModel.getStringProperty(propertyName);
		if (!newText.equals(label.getText())) {
			label.setText(newText);
		}
	}

	protected void setWidgetValue(String propertyName, int flag, List list) {
		Object newContents = dataModel.getProperty(propertyName);
		if (newContents == null) {
			list.setItems(new String[0]);
			return;
		}
		if (newContents instanceof java.util.List) {
			java.util.List modelContents = (java.util.List) newContents;
			String[] items = new String[modelContents.size()];
			for (int i = 0; i < modelContents.size(); i++) {
				items[i] = modelContents.get(i).toString();
			}
			list.setItems(items);
		} else if (newContents instanceof String[]) {
			list.setItems((String[]) newContents);
		}
	}

	public void synchAllUIWithModel() {
		if (null != propertyToWidgetHash) {
			Collection keys = propertyToWidgetHash.keySet();
			if (!keys.isEmpty()) {
				Iterator propertyNames = keys.iterator();
				String propertyName = null;
				while (propertyNames.hasNext()) {
					propertyName = (String) propertyNames.next();
					synchUIWithModel(propertyName, DataModelEvent.VALUE_CHG);
				}
			}
		}
	}

	protected void synchComposite(Widget widget, String propertyName, Control[] depControls) {
		if (null == widgetToPropertyHash)
			widgetToPropertyHash = new HashMap();
		if (propertyToWidgetHash == null)
			propertyToWidgetHash = new HashMap();
		widgetToPropertyHash.put(widget, propertyName);
		propertyToWidgetHash.put(propertyName, widget);
		if (depControls != null) {
			if (widgetToDepControls == null)
				widgetToDepControls = new HashMap();
			widgetToDepControls.put(widget, depControls);
		}
		synchUIWithModel(propertyName, DataModelEvent.VALUE_CHG);
	}

	public void synchText(Text text, String propertyName, Control[] dependentControls) {
		synchText(text, propertyName, false, dependentControls);
	}

	public void synchText(Text text, String propertyName, boolean isTimeModified, Control[] dependentControls) {
		synchComposite(text, propertyName, dependentControls);
		if (isTimeModified)
			text.addModifyListener(getTimedListener());
		else
			text.addModifyListener(getModifyTextListener());
	}

	public void synchLabel(Label label, String propertyName, Control[] dependentControls) {
		synchComposite(label, propertyName, dependentControls);
	}

	/**
	 * Use this to synch the contents of the <code>list</code> to the List elements returned from
	 * the <code>propertyName</code>.
	 */
	public void synchList(List list, String propertyName, Control[] dependentControls) {
		synchComposite(list, propertyName, dependentControls);
	}

	/**
	 * Use this to synch the value of the specified Combo with the specified propertyName. The
	 * possible values displayed to the user are determined by return of
	 * IDataModel.getValidPropertyDescriptors(String).
	 * 
	 * @param combo
	 * @param propertyName
	 * @param dependentControls
	 */
	public void synchCombo(Combo combo, String propertyName, Control[] dependentControls) {
		synchComposite(combo, propertyName, dependentControls);
		if (null == comboListener) {
			comboListener = new ComboListener();
		}
		combo.addSelectionListener(comboListener);
		combo.addModifyListener(comboListener);
	}

	/**
	 * Use this to sync the state of the specified checkbox with the value of the specified
	 * propertyName. The specified propertyName must contain a java.lang.Boolean typed Object.
	 * 
	 * @param checkbox
	 * @param propertyName
	 * @param dependentControls
	 */
	public void synchCheckbox(Button checkbox, String propertyName, Control[] dependentControls) {
		synchComposite(checkbox, propertyName, dependentControls);
		if (null == checkboxSelectionListener) {
			checkboxSelectionListener = new CheckboxSelectionListener();
		}
		checkbox.addSelectionListener(checkboxSelectionListener);
	}

	public void synchCheckBoxTableViewer(CheckboxTableViewer tableViewer, String propertyName, Control[] dependentControls) {
		synchComposite(tableViewer.getControl(), propertyName, dependentControls);
		if (null == checkBoxViewerStateListener) {
			checkBoxViewerStateListener = new CheckBoxViewerListener();
		}
		tableViewer.addCheckStateListener(checkBoxViewerStateListener);
	}

	public void synchCheckBoxTreeViewer(CheckboxTreeViewer treeViewer, String propertyName, Control[] dependentControls) {
		treeViewer.expandAll();
		synchComposite(treeViewer.getControl(), propertyName, dependentControls);
		if (null == checkBoxViewerStateListener) {
			checkBoxViewerStateListener = new CheckBoxViewerListener();
		}
		treeViewer.addCheckStateListener(checkBoxViewerStateListener);
	}

	public void synchRadio(Button radio, String propertyName, Control[] dependentControls) {
		// Uses checkbox syncher
		synchCheckbox(radio, propertyName, dependentControls);
	}

	private TimedModifyListener getTimedListener() {
		if (timedModifyListener == null)
			timedModifyListener = new TimedModifyListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Widget w = (Widget) e.getSource();
					if (currentWidget == w || w.isDisposed())
						return;
					String propertyName = (String) widgetToPropertyHash.get(w);
					setTextProperty(propertyName, (Text) w);
				}
			}) {
				@Override
				public void modifyText(ModifyEvent e) {
					if (ignoreModifyEvent)
						return;
					super.modifyText(e);
				}
			};
		return timedModifyListener;
	}

	protected void setTextProperty(String propertyName, Text text) {
		setProperty(propertyName, text.getText());
	}

	private ModifyTextListener getModifyTextListener() {
		if (null == modifyTextListener)
			modifyTextListener = new ModifyTextListener();
		return modifyTextListener;
	}

	public IDataModel getDataModel() {
		return dataModel;
	}

	public void dispose() {
		dataModel.removeListener(this);
		checkboxSelectionListener = null;
		currentWidget = null;
		modifyTextListener = null;
		propertyToWidgetHash = null;
		timedModifyListener = null;
		widgetToDepControls = null;
		widgetToPropertyHash = null;
	}
}
