/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.GlobalConfiguration;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.eclipse.wst.validation.internal.ui.plugin.ValidationUIPlugin;

/**
 * This class and its inner classes are not intended to be subclassed outside of the validation
 * framework.
 */
public class ValidationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	private IValidationPage _pageImpl = null;

	/**
 * Initially, this interface was created as an abstract class, and getControl() was implemented.
 * (getProject() could also have been implemented in the abstract class.) However, at runtime, a
 * NullPointerException was thrown; the inner class had lost its pointer to its enclosing class.
 * After some experimentation, I discovered that if I changed the parent to an interface, the
 * enclosing class could be found. (Merely moving the AValidationPage into its own file was
 * insufficient.)
 */
public interface IValidationPage {
	public Composite createPage(Composite parent) throws InvocationTargetException;

	public boolean performOk() throws InvocationTargetException;

	public boolean performDefaults() throws InvocationTargetException;

	public Composite getControl();

	public void dispose();

	public void loseFocus(); // Page is losing focus (event notification)

	public void gainFocus(); // Page is gaining focus (event notification)
}

public class InvalidPage implements IValidationPage {
	private Composite page = null;

	private Composite composite = null;
	private GridLayout layout = null;
	private Label messageLabel = null;

	public InvalidPage(Composite parent) {
		page = createPage(parent);
	}

	/**
	 * This page is added to the Properties guide if some internal problem occurred; for
	 * example, the highlighted item in the workbench is not an IProject (according to this
	 * page's plugin.xml, this page is only valid when an IProject is selected).
	 */
	public Composite createPage(Composite parent) {
		// Don't create the default and apply buttons.
		noDefaultAndApplyButton();

		final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite = new Composite(sc1, SWT.NONE);
		sc1.setContent(composite);
		layout = new GridLayout();
		composite.setLayout(layout);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ContextIds.VALIDATION_PROPERTIES_PAGE);

		messageLabel = new Label(composite, SWT.NONE);
		messageLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INVALID_REGISTER));

		composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		return composite;
	}

	public boolean performDefaults() {
		return true;
	}

	/**
	 * Since this page occurs under invalid circumstances, there is nothing to save.
	 */
	public boolean performOk() {
		return true;
	}

	public Composite getControl() {
		return page;
	}

	public void dispose() {
		messageLabel.dispose();
		//			layout.dispose();
		composite.dispose();
	}

	public void loseFocus() {
		// This page does not depend on the contents of any other page in the wizard, so do
		// nothing.
	}

	public void gainFocus() {
		// This page does not depend on the contents of any other page in the wizard, so do
		// nothing.
	}
}

private class NoValidatorsPage implements IValidationPage {
	private Composite page = null;
	private Composite composite = null;
	private GridLayout layout = null;
	private GridData data = null;
	private Label messageLabel = null;

	public NoValidatorsPage(Composite parent) {
		page = createPage(parent);
	}

	/**
	 * This page is created if an IProject is selected, but that project has no validators
	 * configured (i.e., the page is valid, but an empty list.)
	 */
	public Composite createPage(Composite parent) {
		// Don't create the default and apply buttons.
		noDefaultAndApplyButton();

		// top level group
		final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite = new Composite(sc1, SWT.NONE);
		sc1.setContent(composite);
		layout = new GridLayout();
		composite.setLayout(layout);
		data = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
		composite.setLayoutData(data);

		messageLabel = new Label(composite, SWT.NONE);
		messageLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_NO_VALIDATORS_INSTALLED));

		composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		return composite;
	}


	/**
	 * Since there are no validators, there is nothing to save.
	 */
	public boolean performOk() {
		return true;
	}

	public boolean performDefaults() {
		return true;
	}

	public Composite getControl() {
		return page;
	}

	public void dispose() {
		messageLabel.dispose();
		//			layout.dispose();
		//			data.dispose();
		composite.dispose();
	}

	public void loseFocus() {
		// This page does not depend on the contents of any other page in the wizard, so do
		// nothing.
	}

	public void gainFocus() {
		// This page does not depend on the contents of any other page in the wizard, so do
		// nothing.
	}
}

private class ValidatorListPage implements IValidationPage {
	private Composite page = null;

	private Composite composite = null;
	TableViewer validatorList = null;
	private Button enableAllButton = null;
	private Button disableAllButton = null;
	private Label emptyRowPlaceholder = null;
	Button disableAllValidation = null;
	Button overrideButton = null;
	private Label listLabel = null;
	private Table validatorsTable;

	GlobalConfiguration pagePreferences = null; // the values currently on the page, but not
	// necessarily stored yet. Package visibility
	// for the widget listeners (the compiler would
	// have to create a synthetic accessor method in
	// order to access this field)
	//private boolean _isAutoBuildEnabled; // initialized in the constructor
	private ValidatorMetaData[] _oldVmd = null; // Cache the enabled validators so that, if
  private Map _oldDelegates = null; // Cache the validator delegates.
	// there is no change to this list, the
	// expensive task list update can be avoided
	private boolean _allow = false; // Cache the value of the prefence "allow projects to

	// override" so that, when OK is clicked, we can determine
	// if "allow" has changed or not.

	/**
	 * This class is provided for the CheckboxTableViewer in the
	 * ValidationPropertiesPage$ValidatorListPage class.
	 */
	public class ValidationContentProvider implements IStructuredContentProvider {
		/**
		 * Disposes of this content provider. This is called by the viewer when it is disposed.
		 */
		public void dispose() {
			//dispose
		}

		/**
		 * Returns the elements to display in the viewer when its input is set to the given
		 * element. These elements can be presented as rows in a table, items in a list, etc.
		 * The result is not modified by the viewer.
		 * 
		 * @param inputElement
		 *            the input element
		 * @return the array of elements to display in the viewer
		 */
		public java.lang.Object[] getElements(Object inputElement) {
			if (inputElement instanceof ValidatorMetaData[]) {
				// The Collection is the Collection which is returned by ValidatorManager's
				// getConfiguredValidatorMetaData(IProject) call.
				// This Collection is set to be the input of the CheckboxTableViewer in
				// ValidationPropertiesPage$ValidatorListPage's createPage(Composite)
				// method.
				return (ValidatorMetaData[]) inputElement;
			}
			return new Object[0];
		}

		/**
		 * Notifies this content provider that the given viewer's input has been switched to a
		 * different element.
		 * <p>
		 * A typical use for this method is registering the content provider as a listener to
		 * changes on the new input (using model-specific means), and deregistering the viewer
		 * from the old input. In response to these change notifications, the content provider
		 * propagates the changes to the viewer.
		 * </p>
		 * 
		 * @param viewer
		 *            the viewer
		 * @param oldInput
		 *            the old input element, or <code>null</code> if the viewer did not
		 *            previously have an input
		 * @param newInput
		 *            the new input element, or <code>null</code> if the viewer does not have
		 *            an input
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			//do nothing
		}



	}

	/**
	 * This class is provided for ValidationPropertiesPage$ValidatorListPage's
	 * checkboxTableViewer element.
	 */
	public class ValidationLabelProvider extends LabelProvider implements ITableLabelProvider {
		/**
		 * Override the LabelProvider's text, by customizing the text for a ValidatorMetaData
		 * element.
		 */
		public String getText(Object element) {
			if (element == null) {
				return ""; //$NON-NLS-1$
			} else if (element instanceof ValidatorMetaData) {
				return ((ValidatorMetaData) element).getValidatorDisplayName();
			} else {
				return super.getText(element);
			}
		}

    private Image getImage(String imageName) {
      boolean isDisabled = !validatorsTable.isEnabled();
      if (isDisabled) {
          imageName = imageName + "_disabled";  //$NON-NLS-N$
      }
      Image image = ValidationUIPlugin.getPlugin().getImage(imageName);
      return image;
    }
    
		public Image getColumnImage(Object element, int columnIndex) {
			if(columnIndex == 1) {
				if(((ValidatorMetaData)element).isManualValidation())
					return  getImage("ok_tbl");
				return getImage("fail_tbl");
			} 
      else if(columnIndex == 2) {
				if(((ValidatorMetaData)element).isBuildValidation())
					return getImage("ok_tbl");;
				return getImage("fail_tbl");
			}
      else if (columnIndex == 3)
      {
        ValidatorMetaData vmd = (ValidatorMetaData)element;

        if (vmd.isDelegating())
        {
          return getImage("settings");          
        }
      }
			return null;
		
		}

		public String getColumnText(Object element, int columnIndex) {
			if(columnIndex == 0) {
				return ((ValidatorMetaData) element).getValidatorDisplayName();
			}
			/*if(columnIndex == 1) {
				if(((ValidatorMetaData)element).isManualValidation())
					return COMBO_VALUES[0];
				return COMBO_VALUES[1];	
			} else if(columnIndex == 2) {
				if(((ValidatorMetaData)element).isBuildValidation())
					return COMBO_VALUES[0];
				return COMBO_VALUES[1];
			}*/
			return null;
		}
	}

	/**
	 * This class is used to sort the CheckboxTableViewer elements.
	 */
	public class ValidationViewerSorter extends ViewerSorter {
		/**
		 * Returns a negative, zero, or positive number depending on whether the first element
		 * is less than, equal to, or greater than the second element.
		 * <p>
		 * The default implementation of this method is based on comparing the elements'
		 * categories as computed by the <code>category</code> framework method. Elements
		 * within the same category are further subjected to a case insensitive compare of their
		 * label strings, either as computed by the content viewer's label provider, or their
		 * <code>toString</code> values in other cases. Subclasses may override.
		 * </p>
		 * 
		 * @param viewer
		 *            the viewer
		 * @param e1
		 *            the first element
		 * @param e2
		 *            the second element
		 * @return a negative number if the first element is less than the second element; the
		 *         value <code>0</code> if the first element is equal to the second element;
		 *         and a positive number if the first element is greater than the second element
		 */
		public int compare(Viewer viewer, Object e1, Object e2) {
			// Can't instantiate ViewerSorter because it's abstract, so use this
			// inner class to represent it.
			return super.compare(viewer, e1, e2);
		}
	}

	public ValidatorListPage(Composite parent) throws InvocationTargetException {
		//_isAutoBuildEnabled = ValidatorManager.getManager().isGlobalAutoBuildEnabled();
		pagePreferences = new GlobalConfiguration(ConfigurationManager.getManager().getGlobalConfiguration()); // This
		// represents the values on the page that haven't been persistedyet.
		// Start with the last values that were persisted into the current page's starting values.
		
		_oldVmd = pagePreferences.getEnabledValidators(); // Cache the enabled validators so
		// that, if there is no change to this
		// list, the expensive task list
		// update can be avoided

    _oldDelegates =  new HashMap(pagePreferences.getDelegatingValidators());
		
    _allow = pagePreferences.canProjectsOverride();

		page = createPage(parent);
	}
	
	private void setupTableColumns(Table table, TableViewer viewer) {
		TableColumn validatorColumn = new TableColumn(table, SWT.NONE);
        validatorColumn.setText("Validator");
        validatorColumn.setResizable(false);
        validatorColumn.setWidth(240);
        TableColumn manualColumn = new TableColumn(table, SWT.NONE);
        manualColumn.setText("Manual");
        manualColumn.setResizable(false);
        manualColumn.setWidth(40);
        TableColumn buildColumn = new TableColumn(table, SWT.NONE);
        buildColumn.setText("Build");
        buildColumn.setResizable(false);
        buildColumn.setWidth(30);
        TableColumn settingsColumn = new TableColumn(table, SWT.NONE);
        settingsColumn.setText("Settings");
        settingsColumn.setResizable(false);
        settingsColumn.setWidth(40);
    }
	
	public Composite createPage(Composite parent) throws InvocationTargetException {
		// top level group
		final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite = new Composite(sc1, SWT.NONE);
		sc1.setContent(composite);
		composite.setLayout(new GridLayout()); // use the layout's default preferences
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ContextIds.VALIDATION_PREFERENCE_PAGE);

		Composite validatorGroup = new Composite(composite, SWT.NONE);

		
		GridLayout validatorGroupLayout = new GridLayout();
		validatorGroupLayout.numColumns = 2;
		validatorGroup.setLayout(validatorGroupLayout);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(validatorGroup, ContextIds.VALIDATION_PREFERENCE_PAGE);

		GridData overrideData = new GridData(GridData.FILL_HORIZONTAL);
		emptyRowPlaceholder = new Label(validatorGroup, SWT.NONE);
		emptyRowPlaceholder.setLayoutData(new GridData());
		emptyRowPlaceholder = new Label(validatorGroup, SWT.NONE);
		emptyRowPlaceholder.setLayoutData(new GridData());		
		
		overrideData.horizontalSpan = 2;
		overrideButton = new Button(validatorGroup, SWT.CHECK);
		overrideButton.setLayoutData(overrideData);
		overrideButton.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PREF_BUTTON_OVERRIDE));
		overrideButton.setEnabled(true);
		overrideButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				pagePreferences.setCanProjectsOverride(overrideButton.getSelection());
				try {
					updateWidgets();
				} catch (InvocationTargetException exc) {
					displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
				}
			}
		});
		PlatformUI.getWorkbench().getHelpSystem().setHelp(overrideButton, ContextIds.VALIDATION_PREFERENCE_PAGE_OVERRIDE);

		emptyRowPlaceholder = new Label(validatorGroup, SWT.NONE);
		emptyRowPlaceholder.setLayoutData(new GridData());
		
		GridData disableValidationData = new GridData(GridData.FILL_HORIZONTAL);
		disableValidationData.horizontalSpan = 2;
		disableAllValidation = new Button(validatorGroup, SWT.CHECK);
		disableAllValidation.setLayoutData(disableValidationData);
		disableAllValidation.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.DISABLE_VALIDATION));
		disableAllValidation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				pagePreferences.setDisableAllValidation(disableAllValidation.getSelection());
				disableAllValidation.setFocus();
				validatorsTable.setEnabled(!disableAllValidation.getSelection());
				enableAllButton.setEnabled(!disableAllValidation.getSelection());
				disableAllButton.setEnabled(!disableAllValidation.getSelection());
        validatorList.refresh();
			}
		});
		
		emptyRowPlaceholder = new Label(validatorGroup, SWT.NONE);
		emptyRowPlaceholder.setLayoutData(new GridData());

		listLabel = new Label(validatorGroup, SWT.NONE);
		GridData listLabelData = new GridData(GridData.FILL_HORIZONTAL);
		listLabelData.horizontalSpan = 2;
		listLabel.setLayoutData(listLabelData);
		listLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PREF_VALLIST_TITLE));

		validatorsTable = new Table(validatorGroup,SWT.BORDER | SWT.FULL_SELECTION);
		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(160, true));
    tableLayout.addColumnData(new ColumnWeightData(40, true));
    tableLayout.addColumnData(new ColumnWeightData(30, true));
    tableLayout.addColumnData(new ColumnWeightData(40, true));
    
		validatorsTable.setHeaderVisible(true);
		validatorsTable.setLinesVisible(true);
        validatorsTable.setLayout(tableLayout);
		
		validatorList = new TableViewer(validatorsTable);
        GridData validatorListData = new GridData(GridData.FILL_HORIZONTAL);
		validatorListData.horizontalSpan = 2;
		validatorsTable.setLayoutData(validatorListData);
		validatorList.getTable().setLayoutData(validatorListData);
		validatorList.setLabelProvider(new ValidationLabelProvider());
		validatorList.setContentProvider(new ValidationContentProvider());
		validatorList.setSorter(new ValidationViewerSorter());
    setupTableColumns(validatorsTable,validatorList);
		validatorList.setInput(pagePreferences.getValidators());
    validatorsTable.addMouseListener(new MouseAdapter() {

      public void mouseDown(MouseEvent e)
      {
        TableItem tableItem = validatorsTable.getItem(new Point(e.x, e.y));
        if (tableItem == null || tableItem.isDisposed())
        {
          // item no longer exists
          return;
        }
        int columnNumber;
        int columnsCount = validatorsTable.getColumnCount();
        if (columnsCount == 0)
        {
          // If no TableColumn, Table acts as if it has a single column
          // which takes the whole width.
          columnNumber = 0;
        }
        else
        {
          columnNumber = -1;
          for (int i = 0; i < columnsCount; i++)
          {
            Rectangle bounds = tableItem.getBounds(i);
            if (bounds.contains(e.x, e.y))
            {
              columnNumber = i;
              break;
            }
          }
          if (columnNumber == -1)
          {
            return;
          }
        }

        columnClicked(columnNumber);          
      }});
    
		
		enableAllButton = new Button(validatorGroup, SWT.PUSH);
		enableAllButton.setLayoutData(new GridData());
		enableAllButton.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PREF_BUTTON_ENABLEALL));
		enableAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					performEnableAll();
				} catch (InvocationTargetException exc) {
					displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
				}
			}
		});
		PlatformUI.getWorkbench().getHelpSystem().setHelp(enableAllButton, ContextIds.VALIDATION_PREFERENCE_PAGE);

		disableAllButton = new Button(validatorGroup, SWT.PUSH);
		disableAllButton.setLayoutData(new GridData());
		disableAllButton.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PREF_BUTTON_DISABLEALL));
		disableAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					performDisableAll();
				} catch (InvocationTargetException exc) {
					displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
				}
			}
		});
		PlatformUI.getWorkbench().getHelpSystem().setHelp(disableAllButton, ContextIds.VALIDATION_PREFERENCE_PAGE);

		Composite buttonGroup = new Composite(composite, SWT.NONE);
		GridLayout buttonGroupLayout = new GridLayout();
		buttonGroupLayout.numColumns = 2;
		buttonGroup.setLayout(buttonGroupLayout);
		buttonGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		// Have to set the tab order or only the first checkbox in a Composite can
		// be tab-ed to. (Seems to apply only to checkboxes. Have to use the arrow
		// key to navigate the checkboxes.)
		validatorGroup.setTabList(new Control[]{overrideButton, /*valWhenBuildButton, valWhenAutoBuildButton,*/ validatorList.getTable(), enableAllButton, disableAllButton});

		updateWidgets();

		composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		return composite;
	}
  
  protected void columnClicked(int columnToEdit)
  {
    IStructuredSelection selection = (IStructuredSelection) validatorList.getSelection();
    ValidatorMetaData vmd = (ValidatorMetaData) selection.getFirstElement();
    
    switch (columnToEdit)
    {
    case 1:
      vmd.setManualValidation(!vmd.isManualValidation());
      break;
    case 2:
      vmd.setBuildValidation(!vmd.isBuildValidation());
      break;
    case 3:
      {
        if (!vmd.isDelegating()) {
          break;
        }
          
        String delegateID = pagePreferences.getDelegateUniqueName(vmd);
  
        Shell shell = Display.getCurrent().getActiveShell();
        DelegatingValidatorPreferencesDialog dialog = new DelegatingValidatorPreferencesDialog(shell, vmd, delegateID);
  
        dialog.setBlockOnOpen(true);
        dialog.create();
  
        int result = dialog.open();
  
        if (result == Window.OK)
        {
          pagePreferences.setDelegateUniqueName(vmd, dialog.getDelegateID());
        }
      }
      break;
    default:
      break;
    }
    validatorList.refresh();      
  }	
  
  protected void updateWidgets() throws InvocationTargetException {
		// Need to update even the widgets that do not change based on another
		// widgets because of performDefaults(). If performDefaults() is selected,
		// then the pagePreferences values are reset, and these widgets
		// might also need to be updated.
		updateTable();
		updateAllWidgets();
		updateHelp();
	}
	
	protected void updateWidgetsForDefaults() throws InvocationTargetException {
		updateTableForDefaults();
		updateAllWidgets();
		updateHelp();
	}

	/**
	 * @throws InvocationTargetException
	 */
	private void updateAllWidgets() throws InvocationTargetException {
		overrideButton.setSelection(pagePreferences.canProjectsOverride());
		disableAllValidation.setSelection(pagePreferences.isDisableAllValidation());
		validatorsTable.setEnabled(!disableAllValidation.getSelection());
		enableAllButton.setEnabled(!disableAllValidation.getSelection());
		disableAllButton.setEnabled(!disableAllValidation.getSelection());
	}

	protected void updateTable() throws InvocationTargetException {
		TableItem[] items = validatorsTable.getItems();
		for (int i = 0; i < items.length; i++) {
				TableItem item = items[i];
				ValidatorMetaData vmd = (ValidatorMetaData) item.getData();

				// Should the validator be enabled? Read the user's preferences from last time,
				// if they exist, and set from that. If they don't exist, use the Validator class'
				// default value.
				if (pagePreferences.isManualEnabled(vmd))
					vmd.setManualValidation(true);
				else
					vmd.setManualValidation(false);
				if (pagePreferences.isBuildEnabled(vmd))
					vmd.setBuildValidation(true);
				else
					vmd.setBuildValidation(false);

			}
		validatorList.refresh();
	}
	
	protected void updateTableForDefaults() throws InvocationTargetException {
		TableItem[] items = validatorsTable.getItems();
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			ValidatorMetaData vmd = (ValidatorMetaData) item.getData();

			// Should the validator be enabled? Read the user's preferences from last time,
			// if they exist, and set from that. If they don't exist, use the Validator class'
			// default value.
			if(pagePreferences.isEnabled(vmd)) {
				vmd.setManualValidation(true);
				vmd.setBuildValidation(true);
			} else {
				vmd.setManualValidation(false);
				vmd.setBuildValidation(false);
			}
		}
		validatorList.refresh();
	}
	
	protected void enableManualAndBuildValues() {
		TableItem[] items = validatorsTable.getItems();
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			ValidatorMetaData vmd = (ValidatorMetaData) item.getData();
			vmd.setManualValidation(true);
			vmd.setBuildValidation(true);
		}
		validatorList.refresh();
	}
	
	protected void disableManualAndBuildValues() {
		TableItem[] items = validatorsTable.getItems();
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			ValidatorMetaData vmd = (ValidatorMetaData) item.getData();
			vmd.setManualValidation(false);
			vmd.setBuildValidation(false);
		}
		validatorList.refresh();
	}
	
	public boolean performOk() throws InvocationTargetException {
		storeValues();
		updateTaskList();
		return true;
	}

	public boolean performDefaults() throws InvocationTargetException {
		pagePreferences.resetToDefault();
		updateWidgetsForDefaults();
		getDefaultsButton().setFocus();
		return true;
	}

	public boolean performEnableAll() throws InvocationTargetException {
		setAllValidators(true);
		pagePreferences.setEnabledValidators(getEnabledValidators());
		enableManualAndBuildValues();
		enableAllButton.setFocus();
		return true;
	}

	/**
	 * 
	 */
	private void setAllValidators(boolean bool) {
		TableItem[] items = validatorsTable.getItems();
		for (int i = 0; i < items.length; i++) {
			ValidatorMetaData validatorMetaData = (ValidatorMetaData) items[i].getData();
			validatorMetaData.setManualValidation(bool);
			validatorMetaData.setBuildValidation(bool);
		}
	}
	
	public ValidatorMetaData[] getEnabledValidators() {
		List enabledValidators = new ArrayList();
		TableItem[] items = validatorsTable.getItems();
		for (int i = 0; i < items.length; i++) {
			ValidatorMetaData validatorMetaData = (ValidatorMetaData) items[i].getData();
			if(validatorMetaData.isManualValidation() || validatorMetaData.isBuildValidation())
				enabledValidators.add(validatorMetaData);
		}
		return (ValidatorMetaData[])enabledValidators.toArray(new ValidatorMetaData[enabledValidators.size()]);
	}
	
	public ValidatorMetaData[] getEnabledManualValidators() {
		List enabledValidators = new ArrayList();
		TableItem[] items = validatorsTable.getItems();
		for (int i = 0; i < items.length; i++) {
			ValidatorMetaData validatorMetaData = (ValidatorMetaData) items[i].getData();
			if(validatorMetaData.isManualValidation())
				enabledValidators.add(validatorMetaData);
		}
		return (ValidatorMetaData[])enabledValidators.toArray(new ValidatorMetaData[enabledValidators.size()]);
	
	}
	
	public ValidatorMetaData[] getEnabledBuildValidators() {
		List enabledValidators = new ArrayList();
		TableItem[] items = validatorsTable.getItems();
		for (int i = 0; i < items.length; i++) {
			ValidatorMetaData validatorMetaData = (ValidatorMetaData) items[i].getData();
			if(validatorMetaData.isBuildValidation())
				enabledValidators.add(validatorMetaData);
		}
		return (ValidatorMetaData[])enabledValidators.toArray(new ValidatorMetaData[enabledValidators.size()]);
	
	}

	public boolean performDisableAll() throws InvocationTargetException {
		setAllValidators(false);
		pagePreferences.setEnabledValidators(getEnabledValidators());
		disableManualAndBuildValues();
		disableAllButton.setFocus();
		return true;
	}

	protected void updateHelp() {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(disableAllValidation, ContextIds.VALIDATION_PREFERENCE_PAGE_DISABLE_ALL_ENABLED);
	}

	/*
	 * Store the current values of the controls into the preference store.
	 */
	private void storeValues() throws InvocationTargetException {
		pagePreferences.setCanProjectsOverride(overrideButton.getSelection());
		
		if (disableAllValidation.isEnabled()) {
			pagePreferences.setDisableAllValidation(disableAllValidation.getSelection());
		}
		//pagePreferences.setEnabledValidators(getEnabledValidators());
		
		pagePreferences.setEnabledManualValidators(getEnabledManualValidators());
		
		pagePreferences.setEnabledBuildValidators(getEnabledBuildValidators());

		pagePreferences.passivate();
		pagePreferences.store();
		
		// If the projects aren't allowed to override, clear their settings.
		if (!pagePreferences.canProjectsOverride()) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject[] projects = workspace.getRoot().getProjects();
			for (int i = 0; i < projects.length; i++) {
				IProject project = projects[i];
				try {
					if (project.isOpen()) {
						if (ConfigurationManager.getManager().isMigrated(project)) {
							ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
							prjp.setDoesProjectOverride(false);
							prjp.passivate();
							prjp.store();
						}
					}
				} catch (InvocationTargetException exc) {
					displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
				}
			}
		}

	}

	private void updateTaskList() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject[] projects = workspace.getRoot().getProjects();
		boolean allowChanged = (pagePreferences.canProjectsOverride() != _allow);
		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			try {
				if (project.isOpen()) {
					ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
					if (!prjp.doesProjectOverride() && (prjp.hasEnabledValidatorsChanged(_oldVmd, allowChanged) || true) && 
              (prjp.haveDelegatesChanged(_oldDelegates, allowChanged) || true)) {
						// If the project used to override the preferences, and the preferences
						// make that impossible now, then update the task list.
						//
						// If the preferences allow projects to override, and they don't, and if
						// the validators have changed, then update the task list.
						ValidatorManager.getManager().updateTaskList(project); // Do not remove
						// the exceeded message; only ValidationOperation should do that
						// because it's about to run validation. If the limit is increased, 
						//messages may still be missing, so don't remove the "messages
						// may be missing" message.
					}
				}
			} catch (InvocationTargetException exc) {
				displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
			}
		}
	}

	public Composite getControl() {
		return page;
	}

	public void dispose() {
		listLabel.dispose();
		overrideButton.dispose();
		disableAllValidation.dispose();
		emptyRowPlaceholder.dispose();
		disableAllButton.dispose();
		enableAllButton.dispose();
		validatorList.getTable().dispose();
		composite.dispose();
	}

	public void loseFocus() {
		// This page does not need to cache anything before it loses focus.
	}

	public void gainFocus() {/*
		// This page depends on the Workbench Preference page, so update the value of the
		// isAutoBuild (in case the workbench page's value has changed), and then update
		// this page's widgets.
		try {
			//_isAutoBuildEnabled = ValidatorManager.getManager().isGlobalAutoBuildEnabled();
			updateWidgets();
		} catch (InvocationTargetException exc) {
			displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
		}
	*/}
}

/*
 * @see PreferencePage#createContents(Composite)
 */
protected Control createContents(Composite parent) {
	try {
		GlobalConfiguration gp = ConfigurationManager.getManager().getGlobalConfiguration();
		if (gp.numberOfValidators() == 0) {
			_pageImpl = new NoValidatorsPage(parent);
		} else {
			try {
				_pageImpl = new ValidatorListPage(parent);
			} catch (InvocationTargetException exc) {
				_pageImpl = new InvalidPage(parent);
				displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
			} catch (Throwable exc) {
				_pageImpl = new InvalidPage(parent);
				displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
			}
		}
	} catch (InvocationTargetException exc) {
		_pageImpl = new InvalidPage(parent);
		displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
	} catch (Throwable exc) {
		_pageImpl = new InvalidPage(parent);
		displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
	}

	return _pageImpl.getControl();
}

/*
 * @see IWorkbenchPreferencePage#init(IWorkbench)
 */
public void init(IWorkbench workbench) {
	//init
}

protected void noDefaultAndApplyButton() {
	super.noDefaultAndApplyButton();
}

/**
 * Performs special processing when this page's Defaults button has been pressed.
 * <p>
 * This is a framework hook method for sublcasses to do special things when the Defaults button
 * has been pressed. Subclasses may override, but should call <code>super.performDefaults</code>.
 * </p>
 */
protected void performDefaults() {
	super.performDefaults();

	try {
		_pageImpl.performDefaults();
	} catch (InvocationTargetException exc) {
		displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
	} catch (Throwable exc) {
		displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
	}
}

/**
 * When the user presses the "OK" or "Apply" button on the Properties Guide/Properties Page,
 * respectively, some processing is performed by this PropertyPage. If the page is found, and
 * completes successfully, true is returned. Otherwise, false is returned, and the guide doesn't
 * finish.
 */
public boolean performOk() {
	try {
		return _pageImpl.performOk();
	} catch (InvocationTargetException exc) {
		displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
		return false;
	} catch (Throwable exc) {
		displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
		return false;
	}
}

/**
 * Since the pages are inner classes of a child PreferencePage, not a PreferencePage itself,
 * DialogPage's automatic disposal of its children's widgets cannot be used. Instead, dispose of
 * each inner class' widgets explicitly.
 */
public void dispose() {
	super.dispose();
	try {
		if (_pageImpl != null) {
			_pageImpl.dispose();
		}
	} catch (Throwable exc) {
		displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
	}
}

private void logError(Throwable exc) {
	Logger logger = WTPUIPlugin.getLogger();
	if (logger.isLoggingLevel(Level.SEVERE)) {
		LogEntry entry = ValidationUIPlugin.getLogEntry();
		entry.setSourceIdentifier("ValidationPreferencePage.displayAndLogError"); //$NON-NLS-1$
		entry.setMessageTypeIdentifier(ResourceConstants.VBF_EXC_INTERNAL_PAGE);
		entry.setTargetException(exc);
		logger.write(Level.SEVERE, entry);

		if (exc instanceof InvocationTargetException) {
			if (((InvocationTargetException) exc).getTargetException() != null) {
				entry.setTargetException(((InvocationTargetException) exc).getTargetException());
				logger.write(Level.SEVERE, entry);
			}
		}
	}
}

/**
 * package visibility because if this method is private, then the compiler needs to create a
 * synthetic accessor method for the internal classes, and that can have performance
 * implications.
 */
void displayAndLogError(String title, String message, Throwable exc) {
	logError(exc);
	displayMessage(title, message, org.eclipse.swt.SWT.ICON_ERROR);
}

private void displayMessage(String title, String message, int iIconType) {
	MessageBox messageBox = new MessageBox(getShell(), org.eclipse.swt.SWT.OK | iIconType | org.eclipse.swt.SWT.APPLICATION_MODAL);
	messageBox.setMessage(message);
	messageBox.setText(title);
	messageBox.open();
}

/**
 * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
 */
public void setVisible(boolean visible) {
	super.setVisible(visible);
	if (_pageImpl == null)
		return;
	if (visible) {
		_pageImpl.gainFocus();
	} else {
		_pageImpl.loseFocus();
	}
}

/**
 * @see org.eclipse.jface.preference.PreferencePage#getDefaultsButton()
 */
protected Button getDefaultsButton() {
	return super.getDefaultsButton();
}
}