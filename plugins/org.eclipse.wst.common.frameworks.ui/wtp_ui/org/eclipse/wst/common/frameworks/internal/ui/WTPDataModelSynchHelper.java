/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Nov 3, 2003
 * 
 * To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 * Comments
 */
package org.eclipse.wst.common.frameworks.internal.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModelEvent;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModelListener;
import org.eclipse.wst.common.frameworks.internal.operations.WTPPropertyDescriptor;


/**
 * @author jsholl
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WTPDataModelSynchHelper implements WTPOperationDataModelListener {
	protected WTPOperationDataModel dataModel;
	protected Map widgetToPropertyHash;
	protected Map propertyToWidgetHash;
	protected Map widgetToDepControls;
	/**
	 * @deprecated
	 */
	private Map deprecatedConvertPropertyNames; //TODO delete this
	/**
	 * @deprecated
	 */
	private HashSet deprecatedCombos; //TODO delete this

	protected String currentProperty;
	protected Widget currentWidget;
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

	/**
	 * @deprecated
	 * @author jsholl
	 */
	private class ComboListenerDeprecated implements SelectionListener, ModifyListener { //TODO delete this
		public void modifyText(ModifyEvent e) {
			if (ignoreModifyEvent)
				return;
			Combo combo = (Combo) e.getSource();
			if (currentWidget == combo)
				return;
			String propertyName = (String) widgetToPropertyHash.get(combo);
			setProperty(propertyName, combo.getText());
		}

		public void widgetSelected(SelectionEvent e) {
			Combo combo = (Combo) e.getSource();
			if (currentWidget == combo)
				return;
			String propertyName = (String) widgetToPropertyHash.get(combo);
			if (combo.getSelectionIndex() >= 0)
				setProperty(propertyName, combo.getItem(combo.getSelectionIndex()));
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}

	/**
	 * @deprecated
	 */
	private ComboListenerDeprecated comboListenerDeprecated; //TODO delete this

	private class ComboListener implements SelectionListener, ModifyListener {
		public void modifyText(ModifyEvent e) {
			if (ignoreModifyEvent)
				return;
			Combo combo = (Combo) e.getSource();
			if (currentWidget == combo)
				return;
			String propertyName = (String) widgetToPropertyHash.get(combo);
			WTPPropertyDescriptor[] descriptors = dataModel.getValidPropertyDescriptors(propertyName);
			String description = combo.getText();
			for (int i = 0; i < descriptors.length; i++) {
				if (description.equals(descriptors[i].getPropertyDescription())) {
					setProperty(propertyName, descriptors[i].getPropertyValue());
					return;
				}
			}
			// setProperty(propertyName, combo.getText());
		}

		public void widgetSelected(SelectionEvent e) {
			Combo combo = (Combo) e.getSource();
			if (currentWidget == combo)
				return;
			String propertyName = (String) widgetToPropertyHash.get(combo);
			if (combo.getSelectionIndex() >= 0) {
				WTPPropertyDescriptor[] descriptors = dataModel.getValidPropertyDescriptors(propertyName);
				String description = combo.getItem(combo.getSelectionIndex());
				for (int i = 0; i < descriptors.length; i++) {
					if (description.equals(descriptors[i].getPropertyDescription())) {
						setProperty(propertyName, descriptors[i].getPropertyValue());
						return;
					}
				}
				// setProperty(propertyName, combo.getItem(combo.getSelectionIndex()));
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

	public WTPDataModelSynchHelper(WTPOperationDataModel model) {
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

	public void propertyChanged(WTPOperationDataModelEvent event) {
		String propertyName = event.getPropertyName();
		int flag = event.getFlag();
		if (currentProperty != null) {
			String convertedProp = propertyName;
			if (flag == WTPOperationDataModelListener.PROPERTY_CHG)
				convertedProp = getConvertedProperty(propertyName);
			if (currentProperty.equals(convertedProp))
				return;
		}
		if (flag == WTPOperationDataModelListener.ENABLE_CHG)
			setEnablement(propertyName, ((Boolean) event.getNewValue()).booleanValue());
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
	 * @link WTPOperationDataModelListener for the flag values.
	 */
	public void synchUIWithModel(String propertyName, int flag) {
		if (null != propertyToWidgetHash && propertyToWidgetHash.containsKey(propertyName)) {
			try {
				dataModel.setIgnorePropertyChanges(true);
				currentWidget = (Widget) propertyToWidgetHash.get(propertyName);
				if (currentWidget != null) {
					ignoreModifyEvent = true;
					try {
						if (currentWidget instanceof Text)
							setWidgetValue(propertyName, flag, (Text) currentWidget);
						else if (currentWidget instanceof Combo){
							if(null != deprecatedCombos && deprecatedCombos.contains(currentWidget)){
								setWidgetValueDeprecated(propertyName, flag, (Combo) currentWidget);
							} else {
								setWidgetValue(propertyName, flag, (Combo) currentWidget);
							}
						}else if (currentWidget instanceof Button)
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
					setEnablement(propertyName);
				}
			} finally {
				currentWidget = null;
				dataModel.setIgnorePropertyChanges(false);
			}
		}
	}

	/**
	 * @param control
	 */
	private void setEnablement(String propertyName) {
		if (currentWidget == null)
			return;
		Boolean enabled = dataModel.isEnabled(propertyName);
		if (enabled != null)
			setEnablement((Control) currentWidget, enabled.booleanValue());
	}

	/**
	 * @param control
	 * @param enabled
	 */
	private void setEnablement(Control control, boolean enabled) {
		if (control.isEnabled() != enabled)
			control.setEnabled(enabled);
		setDependentControlEnablement(control, enabled);
	}

	private void setEnablement(String propertyName, boolean enabled) {
		if (propertyToWidgetHash != null) {
			Control control = (Control) propertyToWidgetHash.get(propertyName);
			if (control != null) {
				setEnablement(control, enabled);
			}
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

	/**
	 * @deprecated
	 * @param propertyName
	 * @param flag
	 * @param combo
	 */
	private void setWidgetValueDeprecated(String propertyName, int flag, Combo combo) {
		String prop = getConvertedProperty(propertyName);
		if (flag == WTPOperationDataModelListener.VALID_VALUES_CHG || combo.getItemCount() == 0) {
			// Display properties should only fire if the contents change.
			String[] items = dataModel.getValidStringPropertyValues(prop);
			combo.setItems(items);
		}
		String newText = dataModel.getStringProperty(prop);
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

	protected void setWidgetValue(String propertyName, int flag, Combo combo) {
		if (flag == WTPOperationDataModelListener.VALID_VALUES_CHG || combo.getItemCount() == 0) {
			// Display properties should only fire if the contents change.
			WTPPropertyDescriptor[] descriptors = dataModel.getValidPropertyDescriptors(propertyName);
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

	/**
	 * @param propertyName
	 * @return
	 * @deprecated
	 */
	private String getConvertedProperty(String propertyName) {
		if (deprecatedConvertPropertyNames != null) {
			String prop = (String) deprecatedConvertPropertyNames.get(propertyName);
			if (prop != null)
				return prop;
		}
		return propertyName;
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
			if (items.contains(treeItems[i].getData()))
				treeItems[i].setChecked(true);
			else
				treeItems[i].setGrayed(true);
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
					synchUIWithModel(propertyName, WTPOperationDataModelListener.PROPERTY_CHG);
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
		synchUIWithModel(propertyName, WTPOperationDataModelListener.PROPERTY_CHG);
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
	 * @deprecated use syncCombo(Combo, String, Control[])
	 * @param combo
	 * @param propertyName
	 * @param actualProperty
	 * @param dependentControls
	 */
	public void synchCombo(Combo combo, String propertyName, String actualProperty, Control[] dependentControls) {
		if (actualProperty != null) {
			if (propertyToWidgetHash == null)
				propertyToWidgetHash = new Hashtable();
			propertyToWidgetHash.put(actualProperty, combo);
			setConvertProperty(actualProperty, propertyName);
			if (null == deprecatedCombos) {
				deprecatedCombos = new HashSet();
			}
			deprecatedCombos.add(combo);
		}
		synchComposite(combo, propertyName, dependentControls);
		if (null == comboListenerDeprecated) {
			comboListenerDeprecated = new ComboListenerDeprecated();
		}
		combo.addSelectionListener(comboListenerDeprecated);
		combo.addModifyListener(comboListenerDeprecated);
	}

	public void synchCombo(Combo combo, String propertyName, Control[] dependentControls) {
		synchComposite(combo, propertyName, dependentControls);
		if (null == comboListener) {
			comboListener = new ComboListener();
		}
		combo.addSelectionListener(comboListener);
		combo.addModifyListener(comboListener);
	}


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

	/**
	 * This is necessary when a property is not actually persisted in the model but converted to
	 * another property. @param actualProperty @param convertProperty
	 * @deprecated
	 */
	private void setConvertProperty(String actualProperty, String convertProperty) {
		if (deprecatedConvertPropertyNames == null)
			deprecatedConvertPropertyNames = new HashMap();
		deprecatedConvertPropertyNames.put(actualProperty, convertProperty);
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

	public WTPOperationDataModel getDataModel() {
		return dataModel;
	}

	public void dispose() {
		dataModel.removeListener(this);
		checkboxSelectionListener = null;
		deprecatedConvertPropertyNames = null;
		deprecatedCombos = null;
		currentWidget = null;
		modifyTextListener = null;
		propertyToWidgetHash = null;
		timedModifyListener = null;
		widgetToDepControls = null;
		widgetToPropertyHash = null;
	}
}