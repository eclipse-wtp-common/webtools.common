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

import javax.swing.event.HyperlinkEvent;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
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
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.GlobalConfiguration;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.ui.plugin.ValidationUIPlugin;

/**
 * This class and its inner classes are not intended to be subclassed outside of the validation
 * framework.
 * 
 * This page implements the PropertyPage for validators; viewed when the user right-clicks on the
 * IProject, selects "Properties", and then "Validation."
 * 
 * There exist three possible page layouts: if there is an eclipse internal error, and the page is
 * brought up on a non-IProject type; if there are no validators configured on that type of
 * IProject, and a page which lists all validators configured on that type of IProject. These three
 * pages are implemented as inner classes, so that it's clear which method is needed for which
 * input. When all of the methods, and behaviour, were implemented in this one class, much more
 * error-checking had to be done, to ensure that the method wasn't being called incorrectly by one
 * of the pages.
 */
public class ValidationPropertiesPage extends PropertyPage {
	static final String NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$
	static final String TAB = "\t"; //$NON-NLS-1$
	static final String NEWLINE_AND_TAB = NEWLINE + TAB;
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
		public abstract Composite createPage(Composite parent) throws InvocationTargetException;

		public abstract boolean performOk() throws InvocationTargetException;

		public boolean performDefaults() throws InvocationTargetException;
		
		public Composite getControl();

		public abstract void dispose();

		public abstract boolean performCancel();
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

		public boolean performCancel() {
			return true;
		}
	}

	public class NoValidatorsPage implements IValidationPage {
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
			PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ContextIds.VALIDATION_PROPERTIES_PAGE);

			messageLabel = new Label(composite, SWT.NONE);
			String[] msgParm = {getProject().getName()};
			messageLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_LBL_NOVALIDATORS_DESC, msgParm));
			composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			return composite;
		}

		public boolean performDefaults() {
			return true;
		}

		/**
		 * Since there are no validators, there is nothing to save.
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
			//			data.dispose();
			composite.dispose();
		}

		public boolean performCancel() {
			return true;
		}
	}

	public class ValidatorListPage implements IValidationPage {
		Composite page = null;
		GridLayout layout = null;
		GridData data = null;
		Label messageLabel = null;
		TableViewer validatorList = null;
		Button overrideGlobalButton = null;
		boolean existingOverrideGlobalVal = false;
		Button disableAllValidation = null;
		boolean existingDisableAllValidation = false;
		private Button enableAllButton = null;
		private Button disableAllButton = null;
		Label emptyRowPlaceholder = null;
		private Table validatorsTable;
		private Label globalPrefLink = null;
		ProjectConfiguration pagePreferences = null;
		private boolean canOverride = false;
		private Button addValidationBuilder = null;

		private ValidatorMetaData[] oldVmd = null; // Cache the enabled validators so that, if there
    private Map oldDelegates = null; // Cache the validator delegates.

		// is no change to this list, the expensive task
		// list update can be avoided

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
					// The ValidatorMetaData[] is the array which is returned by ValidatorManager's
					// getConfiguredValidatorMetaData(IProject) call.
					// This array is set to be the input of the CheckboxTableViewer in
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
			
			public String getColumnText(Object element, int columnIndex) {
				if(columnIndex == 0) {
					return ((ValidatorMetaData) element).getValidatorDisplayName();
				}
				/*if(columnIndex == 1) {
					if(((ValidatorMetaData)element).isManualValidation())
						return ENABLED;
					return DISABLED;	
				} else if(columnIndex == 2) {
					if(((ValidatorMetaData)element).isBuildValidation())
						return ENABLED;
					return DISABLED;
				}*/
				return null;
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
				} else if(columnIndex == 2) {
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
			ConfigurationManager prefMgr = ConfigurationManager.getManager();
			ValidatorManager vMgr = ValidatorManager.getManager();

			pagePreferences = prefMgr.getProjectConfiguration(getProject()); // This
			// represents the values on the page that haven't been persisted yet.
			// Start with the last values that were persisted into the current
			// page's starting values.

			// store the default values for the widgets
			canOverride = prefMgr.getGlobalConfiguration().canProjectsOverride();
			//isAutoBuildEnabled = vMgr.isGlobalAutoBuildEnabled();
			//isBuilderConfigured = ValidatorManager.doesProjectSupportBuildValidation(getProject());
			oldVmd = pagePreferences.getEnabledValidators(); // Cache the enabled validators so
      // that, if there is no change to this
      // list, the expensive task list update
      // can be avoided

      oldDelegates =  new HashMap(pagePreferences.getDelegatingValidators());

			createPage(parent);
			
			existingOverrideGlobalVal = overrideGlobalButton.getSelection();
			existingDisableAllValidation = disableAllValidation.getSelection();
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

		/**
		 * This page is created if the current project has at least one validator configured on it.
		 */
		public Composite createPage(Composite parent) throws InvocationTargetException {
			// top level group
			final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
			sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
			page = new Composite(sc1, SWT.NONE);
			sc1.setContent(page);
			page.setLayout(new GridLayout()); // use the layout's default preferences
			
			Composite validatorGroup = new Composite(page, SWT.NONE);
			GridLayout validatorGroupLayout = new GridLayout();
			validatorGroupLayout.numColumns = 2;
			validatorGroup.setLayout(validatorGroupLayout);
			
			Hyperlink link = new Hyperlink(validatorGroup,SWT.None);
			GridData layout = new GridData(GridData.HORIZONTAL_ALIGN_END);
			layout.horizontalSpan = 2;
			link.setLayoutData(layout);
			link.setUnderlined(true);
			Color color = new Color(validatorGroup.getDisplay(),new RGB(0,0,255) );
			link.setForeground(color);
			link.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.CONFIG_WS_SETTINGS));
			link.addHyperlinkListener(new IHyperlinkListener() {
				public static final String DATA_NO_LINK = "PropertyAndPreferencePage.nolink"; //$NON-NLS-1$

				public void hyperlinkUpdate(HyperlinkEvent e) {
				}

				public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				}

				public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				}

				public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
					String id = getPreferencePageID();
					PreferencesUtil.createPreferenceDialogOn(getShell(), id, new String[]{id}, DATA_NO_LINK).open();
					try {
						updateWidgets();
					} catch (InvocationTargetException ie) {

					}
				}

				private String getPreferencePageID() {
					return "ValidationPreferencePage";
				}
			});
			
			GridData overrideData = new GridData(GridData.GRAB_HORIZONTAL);
			overrideData.horizontalSpan = 2;
			overrideGlobalButton = new Button(validatorGroup, SWT.CHECK);
			overrideGlobalButton.setLayoutData(overrideData);
			overrideGlobalButton.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PROP_BUTTON_OVERRIDE, new String[]{getProject().getName()}));
			overrideGlobalButton.setFocus(); // must focus on something for F1 to have a topic to lanuch
			overrideGlobalButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					pagePreferences.setDoesProjectOverride(overrideGlobalButton.getSelection());
					try {
						updateWidgets();
					} catch (InvocationTargetException exc) {
						displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
					}
				}
			});
			
			emptyRowPlaceholder = new Label(validatorGroup, SWT.NONE);
			emptyRowPlaceholder.setLayoutData(new GridData());
			
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			disableAllValidation = new Button(validatorGroup, SWT.CHECK);
			disableAllValidation.setLayoutData(data);
			disableAllValidation.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.DISABLE_VALIDATION));
			disableAllValidation.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					pagePreferences.setDisableAllValidation(disableAllValidation.getSelection());
					disableAllValidation.setFocus();
					validatorsTable.setEnabled(!disableAllValidation.getSelection());
					enableAllButton.setEnabled(!disableAllValidation.getSelection());
					disableAllButton.setEnabled(!disableAllValidation.getSelection());
	          try {
	            updateWidgets();
	          } catch (InvocationTargetException exc) {
	            displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
	          }
				}
			});
			
			emptyRowPlaceholder = new Label(validatorGroup, SWT.NONE);
			emptyRowPlaceholder.setLayoutData(new GridData());



			GridData listLabelData = new GridData(GridData.FILL_HORIZONTAL);
			listLabelData.horizontalSpan = 2;
			messageLabel = new Label(validatorGroup, SWT.NONE);
			messageLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_LBL_DESC, new String[]{getProject().getName()}));
			messageLabel.setLayoutData(listLabelData);

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
      validatorsTable.addMouseListener(new MouseAdapter() {

        public void mouseDown(MouseEvent e)
        {
          if (e.button != 1) {
            return;
          }

          // Handles mouse clicks in the table.
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
      
      validatorsTable.setMenu(createContextMenu());
			
			validatorList.setInput(pagePreferences.getValidators());
			
			enableAllButton = new Button(validatorGroup, SWT.PUSH);
			GridData selectData = new GridData();
			enableAllButton.setLayoutData(selectData);
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
			PlatformUI.getWorkbench().getHelpSystem().setHelp(enableAllButton, ContextIds.VALIDATION_PROPERTIES_PAGE);

			GridData deselectData = new GridData();
			disableAllButton = new Button(validatorGroup, SWT.PUSH);
			disableAllButton.setLayoutData(deselectData);
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
			PlatformUI.getWorkbench().getHelpSystem().setHelp(disableAllButton, ContextIds.VALIDATION_PROPERTIES_PAGE);

			emptyRowPlaceholder = new Label(validatorGroup, SWT.NONE);
			emptyRowPlaceholder.setLayoutData(new GridData());
			
			GridData disableValidationData = new GridData(GridData.FILL_HORIZONTAL);
			disableValidationData.horizontalSpan = 2;
			addValidationBuilder = new Button(validatorGroup, SWT.CHECK);
			addValidationBuilder.setLayoutData(disableValidationData);
			addValidationBuilder.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.ADD_VALIDATION_BUILDER));

			// Have to set the tab order or only the first checkbox in a Composite can
			// be tab-ed to. (Seems to apply only to checkboxes. Have to use the arrow
			// key to navigate the checkboxes.)
			validatorGroup.setTabList(new Control[]{overrideGlobalButton,disableAllValidation, validatorList.getTable(), enableAllButton, disableAllButton});

			updateWidgets();

			page.setSize(page.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			return page;
		}

		protected void updateTable() throws InvocationTargetException {
			TableItem[] items = validatorsTable.getItems();
			for (int i = 0; i < items.length; i++) {
				TableItem item = items[i];
				ValidatorMetaData vmd = (ValidatorMetaData) item.getData();

				// Should the validator be enabled? Read the user's preferences from last time,
				// if they exist, and set from that. If they don't exist, use the Validator class'
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

		public boolean performDefaults() throws InvocationTargetException {
			pagePreferences.resetToDefault();
			updateWidgets();
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

		public boolean performDisableAll() throws InvocationTargetException {
			setAllValidators(false);
			pagePreferences.setEnabledValidators(getEnabledValidators());
			disableManualAndBuildValues();
			disableAllButton.setFocus();
			return true;
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
		
		public ValidatorMetaData[] getManualEnabledValidators() {
			List enabledValidators = new ArrayList();
			TableItem[] items = validatorsTable.getItems();
			for (int i = 0; i < items.length; i++) {
				ValidatorMetaData validatorMetaData = (ValidatorMetaData) items[i].getData();
				if(validatorMetaData.isManualValidation())
					enabledValidators.add(validatorMetaData);
			}
			return (ValidatorMetaData[])enabledValidators.toArray(new ValidatorMetaData[enabledValidators.size()]);
		}
		
		public ValidatorMetaData[] getBuildEnabledValidators() {
			List enabledValidators = new ArrayList();
			TableItem[] items = validatorsTable.getItems();
			for (int i = 0; i < items.length; i++) {
				ValidatorMetaData validatorMetaData = (ValidatorMetaData) items[i].getData();
				if(validatorMetaData.isBuildValidation())
					enabledValidators.add(validatorMetaData);
			}
			return (ValidatorMetaData[])enabledValidators.toArray(new ValidatorMetaData[enabledValidators.size()]);
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

    protected Menu createContextMenu()
    {
      final Menu menu = new Menu(validatorsTable.getShell(), SWT.POP_UP);
      final MenuItem manualItem = new MenuItem (menu, SWT.CHECK);
      manualItem.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PREF_MNU_MANUAL));
      final MenuItem buildItem = new MenuItem (menu, SWT.CHECK);
      buildItem.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PREF_MNU_BUILD));
      final MenuItem settingsItem = new MenuItem (menu, SWT.PUSH);
      settingsItem.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PREF_MNU_SETTINGS));
      
      class MenuItemListener extends SelectionAdapter
      {
        public void widgetSelected(SelectionEvent e)
        {
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
        public void menuShown(MenuEvent e)
        {
          IStructuredSelection selection = (IStructuredSelection) validatorList.getSelection();
          ValidatorMetaData vmd = (ValidatorMetaData) selection.getFirstElement();
          manualItem.setSelection(vmd.isManualValidation());
          buildItem.setSelection(vmd.isBuildValidation());
          settingsItem.setEnabled(vmd.isDelegating());
        }});
      
      return menu;
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
      default:
        break;
      }
      validatorList.refresh();      
    }
    
		void updateWidgets() throws InvocationTargetException {
			// Since the setting of the "override" button enables/disables the other widgets on the
			// page, update the enabled state of the other widgets from the "override" button.
			updateTable();
			updateAllWidgets();
			
		}
		
		protected void updateWidgetsForDefaults() throws InvocationTargetException {
			updateTableForDefaults();
			updateAllWidgets();
		}
		
		private void updateAllWidgets() throws InvocationTargetException {
			// Since the setting of the "override" button enables/disables the other widgets on the
			// page, update the enabled state of the other widgets from the "override" button.
			ConfigurationManager prefMgr = ConfigurationManager.getManager();
			canOverride = prefMgr.getGlobalConfiguration().canProjectsOverride();
			boolean overridePreferences = canOverride && pagePreferences.doesProjectOverride();
			overrideGlobalButton.setEnabled(canOverride);
			overrideGlobalButton.setSelection(overridePreferences);
			disableAllValidation.setEnabled(overridePreferences);
			disableAllValidation.setSelection(pagePreferences.isDisableAllValidation());
			if (overridePreferences)
				enableDependentControls(!pagePreferences.isDisableAllValidation());
			else
				enableDependentControls(overridePreferences);
			setAddValidationBuilderButtonEnablement();
			updateHelp();
		}

		private void setAddValidationBuilderButtonEnablement() {
			try {
				boolean builderExists = false;
				IProjectDescription description = getProject().getDescription();
				ICommand[] commands = description.getBuildSpec();
				for (int i = 0; i < commands.length; i++) {
					if (commands[i].getBuilderName().equals(ValidationPlugin.VALIDATION_BUILDER_ID) | isValidationBuilderEnabled(commands[i]))
						builderExists = true;
				}
				if (builderExists) {
					addValidationBuilder.setVisible(false);
				} else {
					addValidationBuilder.setVisible(true);
					addValidationBuilder.setSelection(false);
					addValidationBuilder.setEnabled(true);
				}

			} catch (CoreException ce) {
				Logger.getLogger().log(ce);
			}
		}
		
		private  boolean isValidationBuilderEnabled(ICommand command) {
			Map args = command.getArguments();
			if(args.isEmpty())
				return false;
			String handle = (String)args.get("LaunchConfigHandle");
			if(handle != null && handle.length() > 0 && handle.indexOf(ValidationPlugin.VALIDATION_BUILDER_ID) != -1)
				return true;
			return false;
		}

		private void updateTableForDefaults() throws InvocationTargetException {
			TableItem[] items = validatorsTable.getItems();
			for (int i = 0; i < items.length; i++) {
				TableItem item = items[i];
				ValidatorMetaData vmd = (ValidatorMetaData) item.getData();

				// Should the validator be enabled? Read the user's preferences from last time,
				// if they exist, and set from that. If they don't exist, use the Validator class'
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

		/**
		 * @param overridePreferences
		 */
		private void enableDependentControls(boolean overridePreferences) {
			validatorsTable.setEnabled(overridePreferences);
      validatorList.refresh();
			enableAllButton.setEnabled(overridePreferences); // since help messsage isn't
			disableAllButton.setEnabled(overridePreferences);
      
		}

		protected void updateHelp() throws InvocationTargetException {
			// Whenever a widget is disabled, it cannot get focus.
			// Since it can't have focus, its context-sensitive F1 help can't come up.
			// From experimentation, I know that the composite parent of the widget
			// can't have focus either. So, fudge the focus by making the table the widget
			// surrogate so that the F1 help can be shown, with its instructions on how to
			// enable the disabled widget. The table never has F1 help associated with it other
			// than the page F1, so this fudge doesn't remove any context-sensitive help
			// from the table widget.

			/*if (autoButton.getEnabled()) {
				// set the table's help back to what it was
				PlatformUI.getWorkbench().getHelpSystem().setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE);
				PlatformUI.getWorkbench().getHelpSystem().setHelp(autoButton, ContextIds.VALIDATION_PROPERTIES_PAGE_AUTO_ENABLED);
			} else {
				// The order of the following if statement is important!
				// If the user cannot enable automatic validation on the project, then the user
				// should not be told, for example, to turn auto-build on. Let the user know that
				// no matter what they do they cannot run auto-validate on the project. IF the
				// project
				// supports auto-validate, THEN check for the items which the user can change.
				validatorList.getTable().setFocus();
				if (pagePreferences.numberOfIncrementalValidators() == 0) {
					PlatformUI.getWorkbench().getHelpSystem().setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_AUTO_NOINCVALCONFIG);
				} else if (!ValidatorManager.getManager().isGlobalAutoBuildEnabled()) {
					PlatformUI.getWorkbench().getHelpSystem().setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_AUTO_AUTOBUILD);
				} else {
					// Incremental validators configured but not selected
					PlatformUI.getWorkbench().getHelpSystem().setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_AUTO_NOINCVALSELECTED);
				}
			}

			// if autoButton AND build button are disabled, show the build button's "to enable" text
			if (valWhenBuildButton.getEnabled()) {
				// Do NOT set the table's help back to what it was.
				// Only if auto-validate is enabled should the page go back.
				PlatformUI.getWorkbench().getHelpSystem().setHelp(valWhenBuildButton, ContextIds.VALIDATION_PROPERTIES_PAGE_REBUILD_ENABLED);
			} else {
				//				page.getParent().setFocus();
				validatorList.getTable().setFocus();
				PlatformUI.getWorkbench().getHelpSystem().setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_BUILD_NOVALSELECTED);
			}
*/
			// if the override button is disabled, show its "to enable" text.
			if (overrideGlobalButton.getEnabled()) {
				// Do NOT set the table's help back to what it was.
				// Only if auto-validate is enabled should the page go back.
				boolean doesProjectSupportBuildValidation = ValidatorManager.doesProjectSupportBuildValidation(getProject());
				GlobalConfiguration gp = ConfigurationManager.getManager().getGlobalConfiguration();
				//boolean isPrefAuto = gp.isAutoValidate();
				//boolean isPrefManual = gp.isBuildValidate();
				if (doesProjectSupportBuildValidation) {
					// Project supports build validation, so it doesn't matter what the preferences
					// are
					PlatformUI.getWorkbench().getHelpSystem().setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED);
				} /*else if (!doesProjectSupportBuildValidation && (isPrefAuto && isPrefManual)) {
					// Project doesn't support build validation, and the user prefers both auto and
					// manual build validation
					PlatformUI.getWorkbench().getHelpSystem().setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED_CANNOT_HONOUR_BOTH);
				} else if (!doesProjectSupportBuildValidation && isPrefAuto) {
					// Project doesn't support build validation, and the user prefers auto build
					// validation
					PlatformUI.getWorkbench().getHelpSystem().setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED_CANNOT_HONOUR_AUTO);
				} else if (!doesProjectSupportBuildValidation && isPrefManual) {
					// Project doesn't support build validation, and the user prefers manual build
					// validation
					PlatformUI.getWorkbench().getHelpSystem().setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED_CANNOT_HONOUR_MANUAL);
				} else if (!doesProjectSupportBuildValidation && !isPrefAuto && !isPrefManual) {
					// Project doesn't support build validation, but that doesn't matter because the
					// user prefers no build validation.
					PlatformUI.getWorkbench().getHelpSystem().setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED);
				}*/
			} else {
				validatorList.getTable().setFocus();
				// Preference page doesn't allow projects to override
				PlatformUI.getWorkbench().getHelpSystem().setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_OVERRIDE);
			}
		}

		/*
		 * Store the current values of the controls into the preference store.
		 */
		private void storeValues() throws InvocationTargetException {
			pagePreferences.setDoesProjectOverride(overrideGlobalButton.getSelection());

			if (pagePreferences.doesProjectOverride()) {
				pagePreferences.setEnabledManualValidators(getManualEnabledValidators());
				pagePreferences.setEnabledBuildValidators(getBuildEnabledValidators());
			} else {
				pagePreferences.resetToDefault(); // If the project can't or doesn't override,
				// update its values to match the global
				// preference values.
			}
			pagePreferences.store();
		}

		/**
		 * Reads the list of validators, enables the validators which are selected, disables the
		 * validators which are not selected, and if the auto-validate checkbox is chosen, performs
		 * a full validation.
		 */
		public boolean performOk() throws InvocationTargetException {
			// addBuilder MUST be called before storeValues
			// addBuilder adds a builder to the project, and that changes the project description.
			// Changing a project's description triggers the validation framework's "natureChange"
			// migration, and a nature change requires that the list of validators be recalculated.
			// If the builder is added after the values are stored, the stored values are
			// overwritten.
			addBuilder();

			// If this method is being called because an APPLY was hit instead of an OK,
			// recalculate the "can build be enabled" status because the builder may have
			// been added in the addBuilder() call above.
			// Also recalculate the values that depend on the isBuilderConfigured value.
			//isBuilderConfigured = ValidatorManager.doesProjectSupportBuildValidation(getProject());

			// Persist the values.
			storeValues();

			if (pagePreferences.hasEnabledValidatorsChanged(oldVmd, false) ||
          pagePreferences.haveDelegatesChanged(oldDelegates, false)) { 
				// false means that the preference "allow" value hasn't changed
				ValidatorManager.getManager().updateTaskList(getProject()); 
			}

			return true;
		}

		/**
		 * If the current project doesn't have the validation builder configured on it, add the
		 * builder. Otherwise return without doing anything.
		 */
		private void addBuilder() {
			if (addValidationBuilder.isEnabled() && addValidationBuilder.getSelection())
				ValidatorManager.addProjectBuildValidationSupport(getProject());
		}

		public Composite getControl() {
			return page;
		}

		public void dispose() {
			enableAllButton.dispose();
			disableAllButton.dispose();
			validatorList.getTable().dispose();
			messageLabel.dispose();
			//			layout.dispose();
			//			data.dispose();
			emptyRowPlaceholder.dispose();
			overrideGlobalButton.dispose();
			page.dispose();
		}

		public boolean performCancel() {
			pagePreferences.setDoesProjectOverride(existingOverrideGlobalVal);
			pagePreferences.setDisableAllValidation(existingDisableAllValidation);
			return true;
		}
	}

	/**
	 * ValidationPreferencePage constructor comment.
	 */
	public ValidationPropertiesPage() {
		// Some of the initialization is done in the "initialize" method, which is
		// called by the "getPageType" method, because the current project must
		// be known in order to initialize those fields.
	}

	/**
	 * Given a parent (the Properties guide), create the Validators page to be added to it.
	 */
	protected Control createContents(Composite parent) {
		IProject project = getProject();

		if ((project == null) || !project.isOpen()) {
			_pageImpl = new InvalidPage(parent);
		} else {
			try {
				if (ConfigurationManager.getManager().getProjectConfiguration(project).numberOfValidators() == 0) {
					_pageImpl = new NoValidatorsPage(parent);
				} else {
					_pageImpl = new ValidatorListPage(parent);
				}
			} catch (InvocationTargetException exc) {
				_pageImpl = new InvalidPage(parent);
				displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
			} catch (Throwable exc) {
				_pageImpl = new InvalidPage(parent);
				displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
			}
		}

		return _pageImpl.getControl();
	}

	/**
	 * Since the pages are inner classes of a child PreferencePage, not a PreferencePage itself,
	 * DialogPage's automatic disposal of its children's widgets cannot be used. Instead, dispose of
	 * each inner class' widgets explicitly.
	 */
	public void dispose() {
		super.dispose();
		try {
			_pageImpl.dispose();
		} catch (Throwable exc) {
			logError(exc);
		}
	}

	/**
	 * Returns the highlighted item in the workbench.
	 */
	public IProject getProject() {
		Object element = getElement();

		if (element == null) {
			return null;
		}

		if (element instanceof IProject) {
			return (IProject) element;
		}

		return null;
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
	
	public boolean performCancel() {
		_pageImpl.performCancel();
		return true;
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

	void logError(Throwable exc) {
		Logger logger = WTPUIPlugin.getLogger();
		if (logger.isLoggingLevel(Level.SEVERE)) {
			LogEntry entry = ValidationUIPlugin.getLogEntry();
			entry.setSourceIdentifier("ValidationPropertiesPage.displayAndLogError"); //$NON-NLS-1$
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
	 * @see org.eclipse.jface.preference.PreferencePage#getDefaultsButton()
	 */
	protected Button getDefaultsButton() {
		return super.getDefaultsButton();
	}
}