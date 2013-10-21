/*******************************************************************************
 * Copyright (c) 2001, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.ui.internal.preferences;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.FullBuildJob;
import org.eclipse.wst.validation.internal.GlobalConfiguration;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValPrefManagerGlobal;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.ValidatorMutable;
import org.eclipse.wst.validation.internal.model.GlobalPreferences;
import org.eclipse.wst.validation.internal.model.GlobalPreferencesValues;
import org.eclipse.wst.validation.internal.ui.DelegatingValidatorPreferencesDialog;
import org.eclipse.wst.validation.internal.ui.plugin.ValidationUIPlugin;
import org.eclipse.wst.validation.ui.internal.HelpContextIds;
import org.eclipse.wst.validation.ui.internal.ImageNames;
import org.eclipse.wst.validation.ui.internal.ValUIMessages;
import org.eclipse.wst.validation.ui.internal.dialog.FilterDialog;

/**
 * From this page the user can configure individual validators.
 * <p>
 * This class and its inner classes are not intended to be subclassed outside of
 * the validation framework.
 * </p>
 */
public class ValidationPreferencePage extends PreferencePage implements	IWorkbenchPreferencePage {

	private IValidationPage _pageImpl = null;
	private Shell _shell;

	public interface IValidationPage {
		
		Composite createPage(Composite parent) throws InvocationTargetException;
		boolean performOk() throws InvocationTargetException;
		boolean performDefaults() throws InvocationTargetException;
		Composite getControl();
		void dispose();
		void loseFocus();
		void gainFocus();
	}

	public final class InvalidPage implements IValidationPage {
		private Composite page = null;

		private Composite composite = null;
		private GridLayout layout = null;
		private Label messageLabel = null;

		public InvalidPage(Composite parent) {
			page = createPage(parent);
		}

		/**
		 * This page is added to the Properties guide if some internal problem
		 * occurred; for example, the highlighted item in the workbench is not
		 * an IProject (according to this page's plugin.xml, this page is only
		 * valid when an IProject is selected).
		 */
		public Composite createPage(Composite parent) {
			// Don't create the default and apply buttons.
			noDefaultAndApplyButton();

			final ScrolledComposite sc1 = new ScrolledComposite(parent,	SWT.H_SCROLL | SWT.V_SCROLL);
			sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite = new Composite(sc1, SWT.NONE);
			sc1.setContent(composite);
			layout = new GridLayout();
			composite.setLayout(layout);

			messageLabel = new Label(composite, SWT.NONE);
			messageLabel.setText(ValUIMessages.VBF_EXC_INVALID_REGISTER);

			composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			return composite;
		}

		public boolean performDefaults() {
			return true;
		}

		/**
		 * Since this page occurs under invalid circumstances, there is nothing
		 * to save.
		 */
		public boolean performOk() {
			return true;
		}

		public Composite getControl() {
			return page;
		}

		public void dispose() {
			messageLabel.dispose();
			// layout.dispose();
			composite.dispose();
		}

		public void loseFocus() {
			// This page does not depend on the contents of any other page in
			// the wizard, so do nothing.
		}

		public void gainFocus() {
			// This page does not depend on the contents of any other page in
			// the wizard, so do nothing.
		}
	}

	private final class ValidatorListPage implements IValidationPage {
		private Composite _page;
		private TableViewer _validatorList;
		private Button _enableAllButton;
		private Button _disableAllButton;
		private Button _override;
		private Button _suspend;
		private Button _autoSave;
		private Button _confirmButton;
		private Label _listLabel;
		private Table _validatorsTable;
		private GlobalPreferencesValues 	_globalPreferences = ValManager.getDefault().getGlobalPreferences().asValues();
		private GlobalConfiguration _globalConfig;
		private ValidatorMutable[] _validators;
		
		private int _changeCount;

		/**
		 * This class is provided for the CheckboxTableViewer in the
		 * ValidationPropertiesPage$ValidatorListPage class.
		 */
		public class ValidationContentProvider implements IStructuredContentProvider {
			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof ValidatorMutable[]) {
					return (ValidatorMutable[]) inputElement;
				}
				return new Object[0];
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		}

		/**
		 * This class is provided for
		 * ValidationPropertiesPage$ValidatorListPage's checkboxTableViewer element.
		 */
		public class ValidationLabelProvider extends LabelProvider implements ITableLabelProvider {
			public String getText(Object element) {
				if (element == null)return ""; //$NON-NLS-1$
				else if (element instanceof ValidatorMutable)
					return ((ValidatorMutable) element).getName();
				else
					return super.getText(element);
			}

			private Image getImage(String imageName) {
				boolean isDisabled = !_validatorsTable.isEnabled();
				if (isDisabled)imageName = imageName + ImageNames.disabled;

				return ValidationUIPlugin.getPlugin().getImage(imageName);
			}

			public Image getColumnImage(Object element, int columnIndex) {
				ValidatorMutable v = (ValidatorMutable) element;
				if (columnIndex == 1) {
					return getImage(v.isManualValidation() ? ImageNames.okTable : ImageNames.failTable);
				} else if (columnIndex == 2) {
					return getImage(v.isBuildValidation() ? ImageNames.okTable : ImageNames.failTable);
				} else if (columnIndex == 3) {
					if (hasSettings(v))return getImage(ImageNames.settings);
					return  null;

				}
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == 0)return ((ValidatorMutable) element).getName();
				return null;
			}
		}

		/**
		 * This class is used to sort the CheckboxTableViewer elements.
		 */
		public class ValidationViewerSorter extends ViewerSorter {
			public int compare(Viewer viewer, Object e1, Object e2) {
				// Can't instantiate ViewerSorter because it's abstract, so use this
				// inner class to represent it.
				return super.compare(viewer, e1, e2);
			}
		}

		public ValidatorListPage(Composite parent) throws InvocationTargetException {
			_page = createPage(parent);
		}

		private void setupTableColumns(Table table) {
			TableColumn validatorColumn = new TableColumn(table, SWT.NONE);
			validatorColumn.setText(ValUIMessages.VALIDATOR);
			validatorColumn.setWidth(245);
			TableColumn manualColumn = new TableColumn(table, SWT.CENTER);
			manualColumn.setText(ValUIMessages.MANUAL);
			manualColumn.pack();
			TableColumn buildColumn = new TableColumn(table, SWT.CENTER);
			buildColumn.setText(ValUIMessages.BUILD);
			buildColumn.pack();
			TableColumn settingsColumn = new TableColumn(table, SWT.CENTER);
			settingsColumn.setText(ValUIMessages.SETTINGS);
			settingsColumn.pack();
		}
		
		private ValidatorMutable[] getDefaultValidators(){
			Validator[] vals = ValManager.getDefault().getValidators();
			ValidatorMutable[] vms = new ValidatorMutable[vals.length];
			for (int i=0; i<vals.length; i++)vms[i] = new ValidatorMutable(vals[i]);
			return vms;
		}

		public Composite createPage(Composite parent) throws InvocationTargetException {
			_globalConfig = new GlobalConfiguration(ConfigurationManager.getManager().getGlobalConfiguration());
			_validators = getDefaultValidators();
			
			Composite validatorGroup = new Composite(parent, SWT.NONE);

			GridLayout validatorGroupLayout = new GridLayout();
			validatorGroupLayout.numColumns = 2;
			validatorGroup.setLayout(validatorGroupLayout);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(validatorGroup);
			
			new Label(validatorGroup, SWT.NONE).setLayoutData(new GridData());

			addOverride(validatorGroup);
			addSuspend(validatorGroup);
			addAutoSave(validatorGroup);
			addConfirm(validatorGroup);

			_listLabel = new Label(validatorGroup, SWT.NONE);
			GridData listLabelData = new GridData(GridData.FILL_HORIZONTAL);
			listLabelData.horizontalSpan = 2;
			_listLabel.setLayoutData(listLabelData);
			_listLabel.setText(ValUIMessages.PREF_VALLIST_TITLE);
			
			_validatorsTable = new Table(validatorGroup, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
			Point preferredSize = _validatorsTable.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			GridDataFactory.fillDefaults().grab(true, true).hint(preferredSize)
				.span(2,1).applyTo(_validatorsTable);
						

			_validatorsTable.setHeaderVisible(true);
			_validatorsTable.setLinesVisible(true);

			_validatorList = new TableViewer(_validatorsTable);			
//			_validatorsTable.setLayoutData(validatorListData);
//			_validatorList.getTable().setLayoutData(validatorListData);
			_validatorList.setLabelProvider(new ValidationLabelProvider());
			_validatorList.setContentProvider(new ValidationContentProvider());
			_validatorList.setSorter(new ValidationViewerSorter());
			setupTableColumns(_validatorsTable);

			_validatorList.setInput(_validators);
			_validatorsTable.addMouseListener(new MouseAdapter() {

				public void mouseDown(MouseEvent e) {
					if (e.button != 1)return;

					TableItem tableItem = _validatorsTable.getItem(new Point(e.x, e.y));
					if (tableItem == null || tableItem.isDisposed())return;
					int columnNumber;
					int columnsCount = _validatorsTable.getColumnCount();
					if (columnsCount == 0) {
						// If no TableColumn, Table acts as if it has a single
						// column which takes the whole width.
						columnNumber = 0;
					} else {
						columnNumber = -1;
						for (int i = 0; i < columnsCount; i++) {
							Rectangle bounds = tableItem.getBounds(i);
							if (bounds.contains(e.x, e.y)) {
								columnNumber = i;
								break;
							}
						}
						if (columnNumber == -1)return;
					}

					columnClicked(columnNumber);
				}
			});

			_validatorsTable.setMenu(createContextMenu());
			_validatorsTable.addFocusListener(new FocusAdapter() {

				public void focusGained(FocusEvent e) {
					super.focusGained(e);
					if (_validatorsTable.getSelectionCount() == 0) {
						_validatorsTable.select(0);
					}
				}
			});

			addEnableDisable(validatorGroup);

			updateWidgets();

			applyDialogFont(validatorGroup);
			validatorGroup.setSize(validatorGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			return validatorGroup;
		}

		private void addEnableDisable(Composite validatorGroup) {
			_enableAllButton = new Button(validatorGroup, SWT.PUSH);
			_enableAllButton.setLayoutData(new GridData());
			_enableAllButton.setText(ValUIMessages.PREF_BUTTON_ENABLEALL);
			_enableAllButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						performEnableAll();
					} catch (InvocationTargetException exc) {
						displayAndLogError(ValUIMessages.VBF_EXC_INTERNAL_TITLE,
							ValUIMessages.VBF_EXC_INTERNAL_PAGE, exc);
					}
				}
			});

			_disableAllButton = new Button(validatorGroup, SWT.PUSH);
			_disableAllButton.setLayoutData(new GridData());
			_disableAllButton.setText(ValUIMessages.PREF_BUTTON_DISABLEALL);
			_disableAllButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						performDisableAll();
					} catch (InvocationTargetException exc) {
						displayAndLogError(ValUIMessages.VBF_EXC_INTERNAL_TITLE,
							ValUIMessages.VBF_EXC_INTERNAL_PAGE, exc);
					}
				}
			});
		}
		
		private void addConfirm(Composite validatorGroup) {
			GridData gd;
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			_confirmButton = new Button(validatorGroup, SWT.CHECK);
			_confirmButton.setLayoutData(gd);
			_confirmButton.setText(ValUIMessages.PrefPageConfirmDialog);
			_confirmButton.setSelection(_globalPreferences.confirmDialog);
			_confirmButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					// do not increment the _changeCount as this by itself should not trigger a build prompt
					_globalPreferences.confirmDialog = _confirmButton.getSelection();
					_confirmButton.setFocus();
				}
			});
		}

		private void addAutoSave(Composite validatorGroup) {
			GridData gd;
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			_autoSave = new Button(validatorGroup, SWT.CHECK);
			_autoSave.setLayoutData(gd);
			_autoSave.setText(ValUIMessages.PrefPage_always_save);
			_autoSave.setSelection(_globalPreferences.saveAutomatically);
			_autoSave.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					// do not increment the _changeCount as this by itself should not trigger a build prompt
					_globalPreferences.saveAutomatically = _autoSave.getSelection();
					_autoSave.setFocus();
				}
			});
		}

		private void addSuspend(Composite validatorGroup) {
			GridData gd;
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			
			_suspend = new Button(validatorGroup, SWT.CHECK);
			_suspend.setLayoutData(gd);
			_suspend.setText(ValUIMessages.DISABLE_VALIDATION);
			_suspend.setSelection(_globalPreferences.disableAllValidation);
			_suspend.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					_suspend.setFocus();
					_validatorsTable.setEnabled(!_suspend.getSelection());
					_enableAllButton.setEnabled(!_suspend.getSelection());
					_disableAllButton.setEnabled(!_suspend.getSelection());
					_validatorList.refresh();
				}
			});
		}

		private void addOverride(Composite validatorGroup) {
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			
			_override = new Button(validatorGroup, SWT.CHECK);
			_override.setLayoutData(gd);
			_override.setText(ValUIMessages.PREF_BUTTON_OVERRIDE);
			_override.setEnabled(true);
			_override.setSelection(_globalPreferences.override);
			_override.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					_globalPreferences.override = _override.getSelection();
					_override.setFocus();
					
				}
			});
		}

		protected Menu createContextMenu() {
			final Menu menu = new Menu(_validatorsTable.getShell(), SWT.POP_UP);
			final MenuItem manualItem = new MenuItem(menu, SWT.CHECK);
			manualItem.setText(ValUIMessages.PREF_MNU_MANUAL);
			final MenuItem buildItem = new MenuItem(menu, SWT.CHECK);
			buildItem.setText(ValUIMessages.PREF_MNU_BUILD);
			final MenuItem settingsItem = new MenuItem(menu, SWT.PUSH);
			settingsItem.setText(ValUIMessages.PREF_MNU_SETTINGS);

			class MenuItemListener extends SelectionAdapter {
				public void widgetSelected(SelectionEvent e) {
					MenuItem menuItem = (MenuItem) e.getSource();
					int index = menu.indexOf(menuItem) + 1;
					columnClicked(index);
				}
			}
			MenuItemListener listener = new MenuItemListener();
			manualItem.addSelectionListener(listener);
			buildItem.addSelectionListener(listener);
			settingsItem.addSelectionListener(listener);

			menu.addMenuListener(new MenuAdapter() {
				public void menuShown(MenuEvent e) {
					IStructuredSelection selection = (IStructuredSelection) _validatorList.getSelection();
					ValidatorMutable vw = (ValidatorMutable) selection.getFirstElement();
					manualItem.setSelection(vw.isManualValidation());
					buildItem.setSelection(vw.isBuildValidation());
					settingsItem.setEnabled(hasSettings(vw));
				}
			});

			return menu;
		}

		protected void columnClicked(int columnToEdit) {
			IStructuredSelection selection = (IStructuredSelection) _validatorList.getSelection();
			ValidatorMutable val = (ValidatorMutable) selection.getFirstElement();

			// In Mac OS, val can be null (see bugs 397349 and 412826)
			if (val == null) return;
			
			switch (columnToEdit) {
			case 1:
				_changeCount++;
				val.setManualValidation(!val.isManualValidation());
				break;
			case 2:
				_changeCount++;
				val.setBuildValidation(!val.isBuildValidation());
				break;
			case 3:
				if (val.isV2Validator()){
					ValidatorMutable newVal = new ValidatorMutable(val);
					FilterDialog fd = new FilterDialog(_shell, newVal, null);
					if (Window.OK == fd.open()){
						_changeCount++;
						newVal = fd.getValidator();
						int i = findit(val);
						if (i != -1)_validators[i] = newVal;
					}
				}
				else {
					handleOldDelegate(val);
				}

				break;
			default:
				break;
			}
			_validatorList.refresh();
		}

		private int findit(ValidatorMutable val) {
			for (int i=0; i<_validators.length; i++)if (_validators[i] == val)return i;
			return -1;
		}

		private void handleOldDelegate(ValidatorMutable val) {
			if (!val.isV1Validator())return;
			
			ValidatorMetaData vmd = val.getVmd();
		    if (!vmd.isDelegating())return;
		    
		    String delegateID = _globalConfig.getDelegateUniqueName(vmd);
		    Shell shell = Display.getCurrent().getActiveShell();
		    DelegatingValidatorPreferencesDialog dialog = 
		    	new DelegatingValidatorPreferencesDialog(shell, vmd, delegateID);
		
		    dialog.setBlockOnOpen(true);
		    dialog.create();
		
		    int result = dialog.open();
	        if (result == Window.OK){
	        	_changeCount++;
	        	_globalConfig.setDelegateUniqueName(vmd, dialog.getDelegateID());
	        }
		}
		
		/**
		 * Does this validator have extra settings that can be configured?
		 * @param v
		 * @return true if it does
		 */
		boolean hasSettings(ValidatorMutable v){
			if (v.isV2Validator())return true;
			if (v.getDelegatingId() != null)return true;
			return false;
		}

		protected void updateWidgets() throws InvocationTargetException {
			// Need to update even the widgets that do not change based on another
			// widgets because of performDefaults(). If performDefaults() is
			// selected, then the pagePreferences values are reset, and these widgets
			// might also need to be updated.
			updateAllWidgets();
			updateHelp();
		}

		private void updateAllWidgets() throws InvocationTargetException {
			_suspend.setSelection(_globalPreferences.disableAllValidation);
			_autoSave.setSelection(_globalPreferences.saveAutomatically);
			_confirmButton.setSelection(_globalPreferences.confirmDialog);
			_override.setSelection(_globalPreferences.override);
			_validatorsTable.setEnabled(!_suspend.getSelection());
			_enableAllButton.setEnabled(!_suspend.getSelection());
			_disableAllButton.setEnabled(!_suspend.getSelection());
			_validatorList.setInput(_validators);
			_validatorList.refresh();
		}

		public boolean performOk() throws InvocationTargetException {
			_globalPreferences.disableAllValidation = _suspend.getSelection();
			_globalPreferences.saveAutomatically = _autoSave.getSelection();
			ValManager vm = ValManager.getDefault();
			int changes = vm.replace(_globalPreferences);
			if ((changes & GlobalPreferences.BuildChangeMask) != 0)_changeCount++;
			
			ValPrefManagerGlobal vpm = ValPrefManagerGlobal.getDefault();
			vpm.savePreferences(vm.getGlobalPreferences(), _validators, null);
			saveV1Preferences();
			
			if (_changeCount > 0 && 
				MessageDialog.openQuestion(_shell, ValUIMessages.RebuildTitle, ValUIMessages.RebuildMsg)){
				
				FullBuildJob fbj = new FullBuildJob();
				fbj.schedule();
			}
			return true;
		}
		
		/**
		 * Save the V1 preferences, so that the old validators continue to work.
		 */
		private void saveV1Preferences(){
			try {
				GlobalConfiguration gc = ConfigurationManager.getManager().getGlobalConfiguration();
				gc.setDisableAllValidation(_globalPreferences.disableAllValidation);
				gc.setEnabledManualValidators(getEnabledManualValidators());				
				gc.setEnabledBuildValidators(getEnabledBuildValidators());
	
				gc.passivate();
				gc.store();
			}
			catch (InvocationTargetException e){
				ValidationUIPlugin.getPlugin().handleException(e);
			}			
		}

		/**
		 * Answer all the V1 validators that are manually enabled.
		 * @return
		 */
		private ValidatorMetaData[] getEnabledManualValidators() {
			List<ValidatorMetaData> list = new LinkedList<ValidatorMetaData>();
			for (ValidatorMutable v : _validators){
				if (v.isManualValidation() && v.isV1Validator())list.add(v.getVmd());
			}
			ValidatorMetaData[] result = new ValidatorMetaData[list.size()];
			list.toArray(result);
			return result;
		}

		/**
		 * Answer all the V1 validators that are enabled for build.
		 * @return
		 */
		private ValidatorMetaData[] getEnabledBuildValidators() {
			List<ValidatorMetaData> list = new LinkedList<ValidatorMetaData>();
			for (ValidatorMutable v : _validators){
				if (v.isBuildValidation() && v.isV1Validator())list.add(v.getVmd());
			}
			ValidatorMetaData[] result = new ValidatorMetaData[list.size()];
			list.toArray(result);
			return result;
		}

		public boolean performDefaults() throws InvocationTargetException {
			_changeCount++;
			Validator[] vals = ValManager.getDefaultValidators();
			_validators = new ValidatorMutable[vals.length];
			for (int i=0; i<vals.length; i++)_validators[i] = new ValidatorMutable(vals[i]);
			updateWidgets();
			getDefaultsButton().setFocus();
			return true;
		}

		public boolean performEnableAll() throws InvocationTargetException {
			setAllValidators(true);
			_enableAllButton.setFocus();
			_validatorList.refresh();
			return true;
		}

		public boolean performDisableAll() throws InvocationTargetException {
			setAllValidators(false);
			_disableAllButton.setFocus();
			_validatorList.refresh();
			return true;
		}
		
		private void setAllValidators(boolean bool){
			_changeCount++;
			for (ValidatorMutable v : _validators){
				v.setBuildValidation(bool);
				v.setManualValidation(bool);
			}
		}

		protected void updateHelp() {
//			PlatformUI.getWorkbench().getHelpSystem().setHelp(_suspend, ContextIds.VALIDATION_PREFERENCE_PAGE_DISABLE_ALL_ENABLED);
		}

		/*
		 * Store the current values of the controls into the preference store.
		 */

		public Composite getControl() {
			return _page;
		}

		public void dispose() {
			_autoSave.dispose();
			_suspend.dispose();
			_disableAllButton.dispose();
			_enableAllButton.dispose();
			_listLabel.dispose();
			_suspend.dispose();
			_validatorList.getTable().dispose();
		}

		public void loseFocus() {
		}

		public void gainFocus() {
		}
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HelpContextIds.PreferencePage);
		try {
			_shell = parent.getShell();
			_pageImpl = new ValidatorListPage(parent);
		} catch (Exception exc) {
			_pageImpl = new InvalidPage(parent);
			displayAndLogError(ValUIMessages.VBF_EXC_INTERNAL_TITLE, ValUIMessages.VBF_EXC_INTERNAL_PAGE, exc);
		}

		return _pageImpl.getControl();
	}

	public void init(IWorkbench workbench) {
	}

	/**
	 * Performs special processing when this page's Defaults button has been
	 * pressed.
	 * <p>
	 * This is a framework hook method for subclasses to do special things when
	 * the Defaults button has been pressed. Subclasses may override, but should
	 * call <code>super.performDefaults</code>.
	 * </p>
	 */
	protected void performDefaults() {
		super.performDefaults();

		try {
			_pageImpl.performDefaults();
		} catch (Exception exc) {
			displayAndLogError(ValUIMessages.VBF_EXC_INTERNAL_TITLE, ValUIMessages.VBF_EXC_INTERNAL_PAGE, exc);
		}
	}

	/**
	 * When the user presses the "OK" or "Apply" button on the Properties
	 * Guide/Properties Page, respectively, some processing is performed by this
	 * PropertyPage. If the page is found, and completes successfully, true is
	 * returned. Otherwise, false is returned, and the guide doesn't finish.
	 */
	public boolean performOk() {
		try {
			return _pageImpl.performOk();
		} 
		catch (Exception exc) {
			displayAndLogError(ValUIMessages.VBF_EXC_INTERNAL_TITLE, ValUIMessages.VBF_EXC_INTERNAL_PAGE, exc);
			return false;
		}
	}

	/**
	 * Since the pages are inner classes of a child PreferencePage, not a
	 * PreferencePage itself, DialogPage's automatic disposal of its children's
	 * widgets cannot be used. Instead, dispose of each inner class' widgets
	 * explicitly.
	 */
	public void dispose() {
		super.dispose();
		try {
			if (_pageImpl != null) {
				_pageImpl.dispose();
				_pageImpl = null;
			}

			// TODO figure out what this thing did
			// ExtensionManger.instance().getDelegate().disposePreferencePage();
		} catch (Exception exc) {
			displayAndLogError(ValUIMessages.VBF_EXC_INTERNAL_TITLE, ValUIMessages.VBF_EXC_INTERNAL_PAGE, exc);
		}
	}

	private void logError(Throwable exc) {
		ValidationUIPlugin.getPlugin().handleException(exc);
	}

	/*
	 * package visibility because if this method is private, then the compiler
	 * needs to create a synthetic accessor method for the internal classes, and
	 * that can have performance implications.
	 */
	void displayAndLogError(String title, String message, Throwable exc) {
		logError(exc);
		displayMessage(title, message, org.eclipse.swt.SWT.ICON_ERROR);
	}

	private void displayMessage(String title, String message, int iIconType) {
		MessageBox messageBox = new MessageBox(getShell(),
				org.eclipse.swt.SWT.OK | iIconType	| org.eclipse.swt.SWT.APPLICATION_MODAL);
		messageBox.setMessage(message);
		messageBox.setText(title);
		messageBox.open();
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (_pageImpl == null)return;
		if (visible)_pageImpl.gainFocus();
		else _pageImpl.loseFocus();
	}

	protected Button getDefaultsButton() {
		return super.getDefaultsButton();
	}
}
